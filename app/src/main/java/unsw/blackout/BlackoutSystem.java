package unsw.blackout;

import java.util.*;
import java.util.stream.Collectors;
import unsw.response.models.EntityInfoResponse;
import unsw.blackout.files.*;
import unsw.blackout.entities.*;
import static unsw.blackout.algorithms.ConnectivityHelper.inRangeAndVisible;
import static unsw.blackout.algorithms.ConnectivityHelper.hasRelayPath;

public class BlackoutSystem {
    private List<FileTransfer> transfers;
    private Map<String, Entity> entities;

    public BlackoutSystem() {
        this.entities = new HashMap<>();
        this.transfers = new ArrayList<>();
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public void removeEntity(String entityId) {
        entities.remove(entityId);
    }

    public Entity getEntity(String entityId) {
        return entities.get(entityId);
    }

    public List<Entity> listEntities() {
        return new ArrayList<>(entities.values());
    }

    public List<String> listEntityIds() {
        return new ArrayList<>(entities.keySet());
    }

    public boolean canCommunicate(Entity from, Entity to) {
        if (from.equals(to)) {
            return false;
        } else if (from.supports(to) && inRangeAndVisible(from, to)) {
            return true;
        } else {
            return hasRelayPath(from, to, listRelaySatellites(from, to));
        }
    }

    public void moveSatellites() {
        for (Satellite satellite : listSatellites()) {
            satellite.orbit();
        }
    }

    public EntityInfoResponse getEntityInfo(String id) {
        Entity entity = getEntity(id);
        return entity.getInfo();
    }

    public void transferFiles() {
        updateRemovedTransientFiles();
        for (FileTransfer transfer : transfers) {
            Entity from = transfer.getFrom();
            Entity to = transfer.getTo();
            if (!canCommunicate(from, to)) {
                transfer.setOutOfRange();
            } else if (canCommunicate(from, to) && transfer.isPaused()) {
                transfer.resume();
            }
        }
        transfers.stream().filter(FileTransfer::isReady).forEach(FileTransfer::start);
        transfers.removeIf(t -> t.isComplete() || t.isCancelled());
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Entity device = getEntity(deviceId);
        FileStorage files = device.getFiles();
        files.addFile(filename, content, true);
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        performTransferChecks(fileName, fromId, toId);

        Entity from = getEntity(fromId);
        Entity to = getEntity(toId);
        FileStorage fromFiles = from.getFiles();
        FileStorage toFiles = to.getFiles();

        FileTransfer transfer = new FileTransfer(fileName, fromFiles, toFiles, from, to);
        transfers.add(transfer);
        from.sendFileTo(fileName, toFiles);
    }

    private void performTransferChecks(String fileName, String fromId, String toId) throws FileTransferException {
        Entity from = getEntity(fromId);
        Entity to = getEntity(toId);
        FileStorage fromFiles = from.getFiles();
        FileStorage toFiles = to.getFiles();

        if (!from.canSendFile(fileName)) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        if (!to.canReceiveFile(fileName)) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
        }

        if (!to.hasReceiveBandwidth() || !from.hasSendBandwidth()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(fromId);
        }

        if (toFiles.maxFilesReached()) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");
        } else if (toFiles.maxStorageReached(fromFiles.getFileSize(fileName))) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");
        }
    }

    private List<Satellite> listSatellites() {
        return listEntities().stream().filter(e -> e instanceof Satellite).map(e -> (Satellite) e)
                .collect(Collectors.toList());
    }

    private List<Entity> listRelaySatellites(Entity from, Entity to) {
        return listEntities().stream().filter(e -> e instanceof RelaySatellite && !e.equals(from) && !e.equals(to))
                .collect(Collectors.toList());
    }

    private void updateRemovedTransientFiles() {
        transfers.stream().filter(FileTransfer::isDeleted).forEach(FileTransfer::updateRemovedTransientFiles);
        transfers.removeIf(FileTransfer::isDeleted);
    }
}
