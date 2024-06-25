package unsw.blackout;

import java.util.*;
import java.util.stream.Collectors;

import unsw.blackout.files.*;
import unsw.blackout.entities.*;

public class BlackoutSystem extends JupiterSystem {
    private List<FileTransfer> transfers = new ArrayList<>();

    @Override
    public boolean canCommunicate(Entity from, Entity to) {
        if (from.equals(to)) {
            return false;
        } else if (from.supports(to) && inRangeAndVisible(from, to)) {
            return true;
        } else if (from instanceof RelaySatellite || to instanceof RelaySatellite) {
            return false;
        } else {
            return hasRelayPath(from, to);
        }
    }

    public void moveSatellites() {
        for (Satellite satellite : listSatellites()) {
            satellite.orbit();
        }
    }

    public void transferFiles() {
        for (FileTransfer transfer : transfers) {
            Entity from = transfer.getFrom();
            Entity to = transfer.getTo();
            if (!canCommunicate(from, to) && !transfer.isTeleportingTransfer()) {
                transfer.cancel();
            }
        }
        transfers.forEach(t -> t.updateTransfer());
        transfers.removeIf(t -> t.isComplete() || t.isCancelled());
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Entity device = getEntity(deviceId);
        FileStorage files = device.getFiles();
        files.addFile(filename, content, true);
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        transferExceptionChecks(fileName, fromId, toId);

        Entity from = getEntity(fromId);
        Entity to = getEntity(toId);
        FileStorage fromFiles = from.getFiles();
        FileStorage toFiles = to.getFiles();

        FileTransfer transfer = new FileTransfer(fileName, fromFiles, toFiles, from, to);
        System.out.println("Sending file: " + fileName + " from " + fromId + " to " + toId);
        transfers.add(transfer);
        transfer.sendFile();
    }

    private void transferExceptionChecks(String fileName, String fromId, String toId) throws FileTransferException {
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

    private boolean hasRelayPath(Entity from, Entity to) {
        List<Entity> relays = listRelaySatellites();
        Queue<Entity> queue = new LinkedList<>();
        Set<Entity> visited = new HashSet<>();

        visited.add(from);
        queue.add(from);

        while (!queue.isEmpty()) {
            Entity current = queue.poll();
            if (current.equals(to)) {
                return true;
            }

            for (Entity relay : relays) {
                if (!visited.contains(relay) && inRangeAndVisible(current, relay)) {
                    visited.add(relay);
                    queue.add(relay);
                }
            }

            if (inRangeAndVisible(current, to)) {
                return true;
            }
        }
        return false;
    }

    private List<Satellite> listSatellites() {
        return listEntities().stream().filter(e -> e instanceof Satellite).map(e -> (Satellite) e)
                .collect(Collectors.toList());
    }

    private List<Entity> listRelaySatellites() {
        return listEntities().stream().filter(e -> e instanceof RelaySatellite).collect(Collectors.toList());
    }

}
