package unsw.blackout;

public class FileTransfer {
    private File fromFile;
    private File toFile;
    private Entity from;
    private Entity to;
    private int progress;
    private int size;
    private boolean isComplete = false;
    private boolean isCancelled = false;

    public FileTransfer(File fromFile, File toFile, Entity from, Entity to) {
        this.fromFile = fromFile;
        this.toFile = toFile;
        this.from = from;
        this.to = to;
        this.progress = 0;
        this.size = fromFile.getSize();
    }

    public void updateProgress() {
        int transferRate = Math.min(from.getSendBandwidth(), to.getReceiveBandwidth());
        if (progress + transferRate >= size) {
            complete();
        }

        if (isTeleportingTransfer()) {
            if (from instanceof Device) {
                // Remove all "t" letter bytes from the file CONTENT on the SENDER (Device)
                fromFile.removeTBytes();
                fromFile.setComplete();
                cancel();
            } else {
                // Remove all remaining "t" letter bytes to be downloaded from the file CONTENT on the RECEIVER
                toFile.removeRemainingTBytes(progress);
                toFile.setComplete();
                progress = fromFile.getSize();
                complete();
            }
        } else {
            progress += transferRate;
            toFile.updateData(progress);
        }
    }

    public void complete() {
        from.decrementOutgoingTransfers();
        to.decrementIncomingTransfers();
        toFile.setComplete();
        isComplete = true;
    }

    public void cancel() {
        from.decrementOutgoingTransfers();
        to.decrementIncomingTransfers();
        to.removeFile(toFile.getFileName());
        isCancelled = true;
    }

    private boolean isTeleportingTransfer() {
        return from instanceof TeleportingSatellite || to instanceof TeleportingSatellite;
    }

    public int getProgress() {
        return progress;
    }

    public String getFileName() {
        return fromFile.getFileName();
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
