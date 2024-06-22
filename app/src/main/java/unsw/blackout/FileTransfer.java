package unsw.blackout;

public class FileTransfer {
    private String fileName;
    private String content;
    private int size;
    private Entity from;
    private Entity to;
    private int progress = 0;
    private boolean isComplete = false;
    private boolean isCancelled = false;

    private File fromFile;
    private File toFile;

    public FileTransfer(String fileName, String content, Entity from, Entity to) {
        this.fileName = fileName;
        this.content = content;
        this.size = content.length();
        this.from = from;
        this.to = to;
        this.setTransfer();
    }

    private void setTransfer() {
        from.setFileComplete(fileName, false);
        to.addFile(fileName, content, false);
        from.incrementOutgoingTransfers();
        to.incrementIncomingTransfers();
        fromFile = from.getFile(fileName);
        toFile = to.getFile(fileName);
    }

    public String getFileName() {
        return fileName;
    }

    public Entity getFrom() {
        return from;
    }

    public Entity getTo() {
        return to;
    }

    public void updateProgress() {
        int transferRate = Math.min(from.getSendingSpeed(), to.getReceivingSpeed());
        progress += transferRate;
        if (progress >= size) {
            complete();
        }

        if (isTeleportingTransfer()) {
            if (to instanceof Device) {
                toFile.removeTBytes();
                cancel();
            } else {
                String remainingData = fromFile.getContent().substring(progress);
                remainingData = remainingData.replaceAll("t", "");
                toFile.setData(toFile.getData() + remainingData);
                progress = fromFile.getSize();
                complete();
            }
        } else {
            toFile.setData(toFile.getData() + fromFile.getContent().substring(progress - transferRate, progress));
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void complete() {
        from.decrementOutgoingTransfers();
        to.decrementIncomingTransfers();
        from.removeFile(fileName);
        toFile.setComplete(true);
        isComplete = true;
    }

    public void cancel() {
        from.decrementOutgoingTransfers();
        to.decrementIncomingTransfers();
        to.removeFile(fileName);
        fromFile.setComplete(true);
        isCancelled = true;
    }

    private boolean isTeleportingTransfer() {
        return from instanceof TeleportingSatellite || to instanceof TeleportingSatellite;
    }

    public int getProgress() {
        return progress;
    }
}
