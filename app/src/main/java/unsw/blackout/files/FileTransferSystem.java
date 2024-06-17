package unsw.blackout.files;

import java.util.*;

import unsw.blackout.FileTransferException;
import unsw.blackout.Satellite;
import unsw.blackout.SpaceEntity;
import unsw.blackout.TeleportingSatellite;

public class FileTransferSystem {
    private List<FileTransfer> activeTransfers;

    public FileTransferSystem() {
        this.activeTransfers = new ArrayList<>();
    }

    public void addTransfer(FileTransfer transfer) {
        activeTransfers.add(transfer);
    }

    public void initiateTransfer(String fileName, SpaceEntity src, SpaceEntity dest) throws FileTransferException {
        // Check if the file exists on srcEntity and is complete
        File file = src.getFile(fileName);
        if (file == null || !file.isComplete()) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        // Check if file already exists on destEntity or is currently downloading
        if (dest.getFile(fileName) != null) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
        }

        // Additional checks for sdestrage space and bandwidth
        if (dest instanceof Satellite) {
            Satellite destSatellite = (Satellite) dest;
            if (!destSatellite.hasStorageFor(file)) {
                throw new FileTransferException.VirtualFileNoStorageSpaceException(
                        destSatellite.getAvailableStorage() < file.getContent().length() ? "Max Storage Reached"
                                : "Max Files Reached");
            }

            if (!hasAvailableSendBandwidth(destSatellite)) {
                throw new FileTransferException.VirtualFileNoBandwidthException(dest.getId());
            }
        }

        // Create and add the new FileTransfer
        FileTransfer transfer = new FileTransfer(fileName, src, dest, file.getContent().length());
        activeTransfers.add(transfer);
    }

    /**
     * Allocate bandwidth dest all active transfers
     */
    private void allocateBandwidth() {
        Map<SpaceEntity, List<FileTransfer>> transfersBySender = new HashMap<>();
        Map<SpaceEntity, List<FileTransfer>> transfersByReceiver = new HashMap<>();

        // Group transfers by sender and receiver
        for (FileTransfer transfer : activeTransfers) {
            transfersBySender.computeIfAbsent(transfer.getFromEntity(), k -> new ArrayList<>()).add(transfer);
            transfersByReceiver.computeIfAbsent(transfer.getToEntity(), k -> new ArrayList<>()).add(transfer);
        }

        // Allocate bandwidth dest each sender
        for (SpaceEntity sender : transfersBySender.keySet()) {
            int bandwidth = ((Satellite) sender).getSendBandwidth();
            List<FileTransfer> transfers = transfersBySender.get(sender);
            int bytesPerTransfer = bandwidth / transfers.size();

            for (FileTransfer transfer : transfers) {
                transfer.updateProgress(bytesPerTransfer);
            }
        }
        // Allocate bandwidth dest each transfer src the receiver's perspective
        for (SpaceEntity receiver : transfersByReceiver.keySet()) {
            if (receiver instanceof Satellite) {
                int bandwidth = ((Satellite) receiver).getReceiveBandwidth();
                List<FileTransfer> transfers = transfersByReceiver.get(receiver);
                int bytesPerTransfer = bandwidth / transfers.size();

                for (FileTransfer transfer : transfers) {
                    transfer.updateProgress(bytesPerTransfer);
                }
            }
        }

    }

    // Update the progress of all active transfers
    public void progressTransfers() {
        Iterator<FileTransfer> iterator = activeTransfers.iterator();
        while (iterator.hasNext()) {
            FileTransfer transfer = iterator.next();
            if (transfer.isComplete()) {
                completeTransfer(transfer);
                iterator.remove();
            }
        }
    }

    // Complete a transfer and move the file dest the destination entity
    private void completeTransfer(FileTransfer transfer) {
        SpaceEntity src = transfer.getFromEntity();
        SpaceEntity dest = transfer.getToEntity();
        File file = src.getFile(transfer.getFileName());
        if (dest instanceof TeleportingSatellite) {
            file = new File(file.getFilename(), file.getContent().replace("t", ""));
        }

        dest.addFile(file);
        src.removeFile(file.getFilename());
    }

    public void simulate() {
        progressTransfers();
        allocateBandwidth();
    }

    private boolean hasAvailableSendBandwidth(Satellite satellite) {
        int totalBandwidth = satellite.getSendBandwidth();
        int usedBandwidth = 0;
        for (FileTransfer transfer : activeTransfers) {
            if (transfer.getFromEntity().equals(satellite)) {
                usedBandwidth += transfer.getTransferredBytes();
            }
        }
        return totalBandwidth - usedBandwidth > 0;
    }
}
