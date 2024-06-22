package unsw.blackout;

import java.util.*;
import java.util.stream.Collectors;

public class BlackoutSystem extends SpaceSystem {
    private Map<String, FileTransfer> transfers = new HashMap<>();

    @Override
    public boolean canCommunicate(Entity src, Entity dest) {
        if (src.equals(dest) || src instanceof RelaySatellite || dest instanceof RelaySatellite) {
            return false;
        } else if (src.supports(dest) && inRangeAndVisible(src, dest)) {
            return true;
        } else {
            return hasRelayPath(src, dest);
        }
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        transferExceptionChecks(fileName, fromId, toId);

        Entity from = getEntity(fromId);
        Entity to = getEntity(toId);

        if (canCommunicate(from, to)) {
            FileTransfer transfer = new FileTransfer(fileName, from.getFileContent(fileName), from, to);
            transfers.put(fileName, transfer);
        }
    }

    public void transferExceptionChecks(String fileName, String fromId, String toId) throws FileTransferException {
        Entity from = getEntity(fromId);
        Entity to = getEntity(toId);

        // file does not exist or is incomplete
        if (!from.hasFile(fileName) || !from.isFileComplete(fileName)) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        // File already exists on toId or is currently downloading to the target device
        if (to.hasFile(fileName)) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
        }

        // Check if there is enough bandwidth to send the file
        if (!from.hasSendBandwidth() || !from.hasReceiveBandwidth()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(fromId);
        }

        if (!to.hasSendBandwidth() || !to.hasReceiveBandwidth()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(toId);
        }

        if (to.maxFilesReached()) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");
        } else if (to.maxStorageReached(from.getFileSize(fileName))) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");
        }
    }

    public void transferFiles() {
        // Check if the entities can communicates
        for (FileTransfer transfer : transfers.values()) {
            Entity from = transfer.getFrom();
            Entity to = transfer.getTo();
            String fileName = transfer.getFileName();

            if (!canCommunicate(from, to)) {
                transfer.cancel();
                transfers.remove(fileName);
            }
        }

        // For each transfer, transfer the file
        for (FileTransfer transfer : transfers.values()) {
            transfer.updateProgress();
            if (transfer.isComplete()) {
                transfer.complete();
                transfers.remove(transfer.getFileName());
            } else if (transfer.isCancelled()) {
                transfer.cancel();
                transfers.remove(transfer.getFileName());
            }
        }
    }

    /************************************************************************* */

    public void moveSatellites() {
        for (Satellite satellite : listSatellites()) {
            satellite.orbit();
        }
    }

    private boolean hasRelayPath(Entity src, Entity dest) {
        List<Entity> relays = listRelaySatellites();

        Queue<Entity> queue = new LinkedList<>();
        Set<Entity> visited = new HashSet<>();

        visited.add(src);
        queue.add(src);

        while (!queue.isEmpty()) {
            Entity current = queue.poll();
            if (current.equals(dest)) {
                return true;
            }

            for (Entity relay : relays) {
                if (!visited.contains(relay) && inRangeAndVisible(current, dest)) {
                    visited.add(relay);
                    queue.add(relay);
                }
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
