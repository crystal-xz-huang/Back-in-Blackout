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
        int transferRate = Math.min(from.getSendBandwidth(), to.getReceiveBandwidth());
        int progressIncrement = Math.min(transferRate, size - progress);
        progress += progressIncrement;
        if (progress == size) {
            complete();
        }
        toFile.updateData(progress);
    }

    private void updateTeleportingTransfer() {
        if (from instanceof Device) {
            fromFile.removeTBytes();
            fromFile.setComplete();
            cancel();
        } else {
            toFile.removeRemainingTBytes(progress);
            toFile.setComplete();
            progress = fromFile.getSize();
            complete();
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
