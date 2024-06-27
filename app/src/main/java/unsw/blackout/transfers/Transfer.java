package unsw.blackout.transfers;

import unsw.blackout.entities.*;
import unsw.blackout.files.FileStorage;

public abstract class Transfer {
    private String fileName;
    private Entity from;
    private Entity to;
    private FileStorage fromFiles;
    private FileStorage toFiles;
    private int transferredBytes;
    private int size;
    private boolean isComplete = false;
    private boolean isCancelled = false;
    private boolean isPaused = false;

    public Transfer(String fileName, Entity from, Entity to) {
        this.fileName = fileName;
        this.from = from;
        this.to = to;
        this.transferredBytes = 0;
        this.size = fromFiles.getFileSize(fileName);
    }

    public void resume() {
        if (isComplete || isCancelled) {
            return;
        }
        int transferRate = Math.min(from.getSendingSpeed(), to.getReceivingSpeed());
        int transferredBytesIncrement = Math.min(transferRate, size - transferredBytes);
        transferredBytes += transferredBytesIncrement;
        if (transferredBytes == size) {
            complete();
        }
        toFiles.updateFileData(fileName, transferredBytes);
    }

    public void complete() {
        transferredBytes = size;
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

    public Entity getFrom() {
        return from;
    }

    public Entity getTo() {
        return to;
    }

    public int getBytesRemaining() {
        return size - transferredBytes;
    }

    public int getTransferredBytes() {
        return transferredBytes;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean teleported() {
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
}
