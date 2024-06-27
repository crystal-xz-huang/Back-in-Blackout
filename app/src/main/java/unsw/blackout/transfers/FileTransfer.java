package unsw.blackout.transfers;

import unsw.blackout.entities.Device;
import unsw.blackout.entities.ElephantSatellite;
import unsw.blackout.entities.Entity;
import unsw.blackout.entities.TeleportingSatellite;
import unsw.blackout.files.FileStorage;

public class FileTransfer {
    private String fileName;
    private FileStorage fromFiles;
    private FileStorage toFiles;
    private Entity from;
    private Entity to;
    private int transferredBytes;
    private int size;
    private boolean isComplete = false;
    private boolean isCancelled = false;
    private boolean isPaused = false;

    public FileTransfer(String fileName, FileStorage fromFiles, FileStorage toFiles, Entity from, Entity to) {
        this.fileName = fileName;
        this.fromFiles = fromFiles;
        this.toFiles = toFiles;
        this.from = from;
        this.to = to;
        this.transferredBytes = 0;
        this.size = fromFiles.getFileSize(fileName);
    }

    public void resume() {
        if (isComplete || isCancelled) {
            return;
        }

        if (isPaused) {
            toFiles.setTransient(fileName, false);
        }
        if (teleported()) {
            updateTeleportingTransfer();
        } else {
            updateNormalTransfer();
        }
    }

    private void updateNormalTransfer() {
        int transferRate = Math.min(from.getSendingSpeed(), to.getReceivingSpeed());
        int transferredBytesIncrement = Math.min(transferRate, size - transferredBytes);
        transferredBytes += transferredBytesIncrement;
        if (transferredBytes == size) {
            complete();
        }
        toFiles.updateFileData(fileName, transferredBytes);
    }

    private void updateTeleportingTransfer() {
        if (from instanceof Device) {
            fromFiles.removeTBytes(fileName);
            fromFiles.setComplete(fileName);
            cancel();
        } else {
            toFiles.removeRemainingTBytes(fileName, transferredBytes);
            toFiles.setComplete(fileName);
            transferredBytes = size;
            complete();
        }
    }

    public void setOutOfRange() {
        if (teleported()) {
            return;
        } else if (to instanceof ElephantSatellite && !paused()) {
            pause();
        } else {
            cancel();
        }
    }

    private void complete() {
        fromFiles.decrementOutgoingFiles();
        toFiles.decrementIncomingFiles();
        toFiles.setComplete(fileName);
        isComplete = true;
    }

    private void pause() {
        fromFiles.decrementOutgoingFiles();
        toFiles.decrementIncomingFiles();
        toFiles.setTransient(fileName, true);
        isPaused = true;
    }

    private void cancel() {
        fromFiles.decrementOutgoingFiles();
        toFiles.decrementIncomingFiles();
        toFiles.removeFile(fileName);
        isCancelled = true;
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

    public boolean paused() {
        return toFiles.isTransient(fileName);
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

    public int getBytesRemaining() {
        return size - transferredBytes;
    }

}
