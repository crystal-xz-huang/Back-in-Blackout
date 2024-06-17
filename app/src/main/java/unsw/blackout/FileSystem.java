package unsw.blackout;

import java.util.*;

public class FileSystem {
    private List<FileTransfer> activeTransfers; // List of active file transfers at this current time

    public FileSystem() {
        activeTransfers = new ArrayList<>();
    }

    /**
     * Update the progress of all active transfers for 1 minute
     */
    public void updateTransfers() {
        for (FileTransfer transfer : activeTransfers) {
            int transferRate = calculateTransferRate(transfer.getSender(), transfer.getReceiver());
            transfer.setTransferRate(transferRate);
            transfer.updateProgress();
            // update the file downloadedBytes accordingly in each entity
            SpaceEntity sender = transfer.getSender();
            SpaceEntity receiver = transfer.getReceiver();
            File fileReceived = receiver.getFile(transfer.getFileName());
            if (fileReceived == null) {
                receiver.addFile(transfer.getFileName(), transfer.getContent(), transfer.getTransferredBytes());
            } else {
                fileReceived.setDownloadedBytes(transfer.getTransferredBytes());
            }
            if (transfer.isComplete()) {
                sender.removeFile(transfer.getFileName());
            }
        }
        activeTransfers.removeIf(transfer -> transfer.isComplete());
    }

    public void sendFile(SpaceEntity sender, SpaceEntity receiver, String fileName) throws FileTransferException {
        File file = sender.getFile(fileName);
        if (file == null || !file.isTransferComplete()) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }
        if (receiver.getFile(fileName) != null || !fileNotDownloading(receiver, fileName)) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
        }
        if (!hasAvailableSendBandwidth(sender) || !hasAvailableReceiveBandwidth(receiver)) {
            throw new FileTransferException.VirtualFileNoBandwidthException(sender.getId());
        }
        if (!hasAvailableStorage(receiver, file)) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");
        } else if (!maxFilesNotReached(receiver, 1)) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");
        }
        FileTransfer transfer = new FileTransfer(file, sender, receiver);
        activeTransfers.add(transfer);
        file.setTransferComplete(false);
        receiver.addFile(fileName, file.getContent(), 0);
    }

    private int calculateTransferRate(SpaceEntity sender, SpaceEntity receiver) {
        int sendingSpeed = getDistributedSendBandwidth(sender);
        int receivingSpeed = getDistributedReceiveBandwidth(receiver);
        int transferRate = Math.min(sendingSpeed, receivingSpeed);
        return transferRate;
    }

    private int getDistributedSendBandwidth(SpaceEntity sender) {
        int totalBandwidth = sender.getSendBandwidth();
        int totalTransfers = listUploadingTransfers(sender).size();
        return totalBandwidth / totalTransfers;
    }

    private int getDistributedReceiveBandwidth(SpaceEntity receiver) {
        int totalBandwidth = receiver.getReceiveBandwidth();
        int totalTransfers = listDownloadingTransfers(receiver).size();
        return totalBandwidth / totalTransfers;
    }

    private boolean fileNotDownloading(SpaceEntity receiver, String fileName) {
        for (FileTransfer transfer : listDownloadingTransfers(receiver)) {
            if (transfer.getFileName().equals(fileName)) {
                return false;
            }
        }
        return true;
    }

    private boolean maxFilesNotReached(SpaceEntity receiver, int numFiles) {
        int maxFiles = receiver.getMaxFiles();
        int numCurrentFiles = listTransfers(receiver).size();
        return numCurrentFiles + numFiles <= maxFiles;
    }

    private boolean hasAvailableStorage(SpaceEntity receiver, File file) {
        int maxStorage = receiver.getMaxStorage();
        int usedStorage = 0;
        for (FileTransfer transfer : listTransfers(receiver)) {
            usedStorage += transfer.getFinalSize();
        }
        return maxStorage - usedStorage >= file.getSize();
    }

    private boolean hasAvailableReceiveBandwidth(SpaceEntity receiver) {
        int totalBandwidth = receiver.getReceiveBandwidth();
        int numTransfers = listDownloadingTransfers(receiver).size();
        int bytesPerTransfer = totalBandwidth / (numTransfers + 1);
        return bytesPerTransfer > 0;
    }

    private boolean hasAvailableSendBandwidth(SpaceEntity sender) {
        int availableBandwidth = sender.getSendBandwidth();
        int numTransfers = listUploadingTransfers(sender).size();
        int bytesPerTransfer = availableBandwidth / (numTransfers + 1);
        return bytesPerTransfer > 0;
    }

    private List<FileTransfer> listUploadingTransfers(SpaceEntity sender) {
        List<FileTransfer> transfers = new ArrayList<>();
        for (FileTransfer transfer : activeTransfers) {
            if (transfer.getSender().equals(sender)) {
                transfers.add(transfer);
            }
        }
        return transfers;
    }

    private List<FileTransfer> listDownloadingTransfers(SpaceEntity receiver) {
        List<FileTransfer> transfers = new ArrayList<>();
        for (FileTransfer transfer : activeTransfers) {
            if (transfer.getReceiver().equals(receiver)) {
                transfers.add(transfer);
            }
        }
        return transfers;
    }

    private List<FileTransfer> listTransfers(SpaceEntity entity) {
        List<FileTransfer> transfers = new ArrayList<>();
        for (FileTransfer transfer : activeTransfers) {
            if (transfer.getSender().equals(entity) || transfer.getReceiver().equals(entity)) {
                transfers.add(transfer);
            }
        }
        return transfers;
    }

    // public void addTransfer(FileTransfer transfer) {
    //     activeTransfers.add(transfer);
    // }

    // public void initiateTransfer(String fileName, SpaceEntity src, SpaceEntity dest) throws FileTransferException {
    //     // Check if the file exists on srcEntity and is complete
    //     File file = src.getFile(fileName);
    //     if (file == null || !file.isComplete()) {
    //         throw new FileTransferException.VirtualFileNotFoundException(fileName);
    //     }

    //     // Check if file already exists on destEntity or is currently downloading
    //     if (dest.getFile(fileName) != null) {
    //         throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
    //     }

    //     // Additional checks for sdestrage space and bandwidth
    //     if (dest instanceof Satellite) {
    //         Satellite destSatellite = (Satellite) dest;
    //         if (!destSatellite.hasStorageFor(file)) {
    //             throw new FileTransferException.VirtualFileNoStorageSpaceException(
    //                     destSatellite.getAvailableStorage() < file.getContent().length() ? "Max Storage Reached"
    //                             : "Max Files Reached");
    //         }

    //         if (!hasAvailableSendBandwidth(destSatellite)) {
    //             throw new FileTransferException.VirtualFileNoBandwidthException(dest.getId());
    //         }
    //     }

    //     // Create and add the new FileTransfer
    //     FileTransfer transfer = new FileTransfer(fileName, src, dest, file.getContent().length());
    //     activeTransfers.add(transfer);
    // }

    // /**
    //  * Allocate bandwidth dest all active transfers
    //  */
    // private void allocateBandwidth() {
    //     Map<SpaceEntity, List<FileTransfer>> transfersBySender = new HashMap<>();
    //     Map<SpaceEntity, List<FileTransfer>> transfersByReceiver = new HashMap<>();

    //     // Group transfers by sender and receiver
    //     for (FileTransfer transfer : activeTransfers) {
    //         transfersBySender.computeIfAbsent(transfer.getFromEntity(), k -> new ArrayList<>()).add(transfer);
    //         transfersByReceiver.computeIfAbsent(transfer.getToEntity(), k -> new ArrayList<>()).add(transfer);
    //     }

    //     // Allocate bandwidth dest each sender
    //     for (SpaceEntity sender : transfersBySender.keySet()) {
    //         int bandwidth = ((Satellite) sender).getSendBandwidth();
    //         List<FileTransfer> transfers = transfersBySender.get(sender);
    //         int bytesPerTransfer = bandwidth / transfers.size();

    //         for (FileTransfer transfer : transfers) {
    //             transfer.updateProgress(bytesPerTransfer);
    //         }
    //     }
    //     // Allocate bandwidth dest each transfer src the receiver's perspective
    //     for (SpaceEntity receiver : transfersByReceiver.keySet()) {
    //         if (receiver instanceof Satellite) {
    //             int bandwidth = ((Satellite) receiver).getReceiveBandwidth();
    //             List<FileTransfer> transfers = transfersByReceiver.get(receiver);
    //             int bytesPerTransfer = bandwidth / transfers.size();

    //             for (FileTransfer transfer : transfers) {
    //                 transfer.updateProgress(bytesPerTransfer);
    //             }
    //         }
    //     }

    // }

    // // Update the progress of all active transfers
    // public void progressTransfers() {
    //     Iterator<FileTransfer> iterator = activeTransfers.iterator();
    //     while (iterator.hasNext()) {
    //         FileTransfer transfer = iterator.next();
    //         if (transfer.isComplete()) {
    //             completeTransfer(transfer);
    //             iterator.remove();
    //         }
    //     }
    // }

    // // Complete a transfer and move the file dest the destination entity
    // private void completeTransfer(FileTransfer transfer) {
    //     SpaceEntity src = transfer.getFromEntity();
    //     SpaceEntity dest = transfer.getToEntity();
    //     File file = src.getFile(transfer.getFileName());
    //     if (dest instanceof TeleportingSatellite) {
    //         file = new File(file.getFilename(), file.getContent().replace("t", ""));
    //     }

    //     dest.addFile(file);
    //     src.removeFile(file.getFilename());
    // }

    // public void simulate() {
    //     progressTransfers();
    //     allocateBandwidth();
    // }

    // private boolean hasAvailableSendBandwidth(Satellite satellite) {
    //     int totalBandwidth = satellite.getSendBandwidth();
    //     int usedBandwidth = 0;
    //     for (FileTransfer transfer : activeTransfers) {
    //         if (transfer.getFromEntity().equals(satellite)) {
    //             usedBandwidth += transfer.getTransferredBytes();
    //         }
    //     }
    //     return totalBandwidth - usedBandwidth > 0;
    // }
}
