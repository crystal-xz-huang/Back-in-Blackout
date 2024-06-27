package unsw.blackout.transfers;

import unsw.blackout.entities.*;
import unsw.blackout.files.FileStorage;

public class TeleportingTransfer extends Transfer {
    private String fileName;
    private FileStorage fromFiles;
    private FileStorage toFiles;

    public TransientTransfer(String fileName, FileStorage fromFiles, FileStorage toFiles, Entity from, Entity to) {
        super(fileName, fromFiles, toFiles, from, to);
        this.fileName = fileName;
        this.fromFiles = fromFiles;
        this.toFiles = toFiles;
    }

    public void progress() {
        if (isComplete || isCancelled) {
            return;
        }
        if (teleported()) {
            updateTeleportingTransfer();
        } else {
            resume();
        }
    }

    public void resume() {
        if (getFrom() instanceof Device) {
            fromFiles.removeTBytes(fileName);
            fromFiles.setComplete(fileName);
            super.cancel();
        } else {
            toFiles.removeRemainingTBytes(fileName, getTransferredBytes());
            super.complete();
        }
    }

    public boolean teleported() {
        Entity from = getFrom();
        Entity to = getTo();
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
