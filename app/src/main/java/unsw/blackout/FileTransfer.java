package unsw.blackout;

public class FileTransfer {
    private String fileName;
    private FileStorage fromFiles;
    private FileStorage toFiles;
    private Entity from;
    private Entity to;
    private int progress;
    private int size;
    private boolean isComplete = false;
    private boolean isCancelled = false;

    public FileTransfer(String fileName, FileStorage fromFiles, FileStorage toFiles, Entity from, Entity to) {
        this.fileName = fileName;
        this.fromFiles = fromFiles;
        this.toFiles = toFiles;
        this.from = from;
        this.to = to;
        this.progress = 0;
        this.size = fromFiles.getFileSize(fileName);
    }

    public void sendFile() {
        File file = fromFiles.getFile(fileName);
        fromFiles.incrementOutgoingFiles();
        toFiles.addFile(fileName, file.getContent(), false);
        toFiles.incrementIncomingFiles();
    }

    public void updateTransfer() {
        if (isComplete || isCancelled) {
            return;
        }
        if (isTeleportingTransfer()) {
            updateTeleportingTransfer();
        } else {
            updateNormalTransfer();
        }
    }

    private void updateNormalTransfer() {
        int transferRate = Math.min(from.getSendingSpeed(), to.getReceivingSpeed());
        int progressIncrement = Math.min(transferRate, size - progress);
        progress += progressIncrement;
        if (progress == size) {
            complete();
        }
        toFiles.updateFileData(fileName, progress);
    }

    private void updateTeleportingTransfer() {
        if (from instanceof Device) {
            fromFiles.removeTBytes(fileName);
            fromFiles.setComplete(fileName);
            cancel();
        } else {
            toFiles.removeRemainingTBytes(fileName, progress);
            toFiles.setComplete(fileName);
            progress = size;
            complete();
        }
    }

    public void complete() {
        fromFiles.decrementOutgoingFiles();
        toFiles.decrementIncomingFiles();
        toFiles.setComplete(fileName);
        isComplete = true;
    }

    public void cancel() {
        fromFiles.decrementOutgoingFiles();
        toFiles.decrementIncomingFiles();
        toFiles.removeFile(fileName);
        isCancelled = true;
    }

    public boolean isTeleportingTransfer() {
        if (from instanceof TeleportingSatellite && to instanceof TeleportingSatellite) {
            return ((TeleportingSatellite) from).hasTeleported() || ((TeleportingSatellite) to).hasTeleported();
        } else if (from instanceof TeleportingSatellite) {
            return ((TeleportingSatellite) from).hasTeleported();
        } else if (to instanceof TeleportingSatellite) {
            return ((TeleportingSatellite) to).hasTeleported();
        } else {
            return false;
        }
    }

    public Entity getFrom() {
        return from;
    }

    public Entity getTo() {
        return to;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
