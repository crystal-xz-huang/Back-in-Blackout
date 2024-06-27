package unsw.blackout.transfers;

import unsw.blackout.entities.*;
import unsw.blackout.files.FileStorage;

public class TransientTransfer extends Transfer {
    private String fileName;
    private FileStorage fromFiles;
    private FileStorage toFiles;

    public TransientTransfer(String fileName, FileStorage fromFiles, FileStorage toFiles, Entity from, Entity to) {
        super(fileName, fromFiles, toFiles, from, to);
        this.fileName = fileName;
        this.fromFiles = fromFiles;
        this.toFiles = toFiles;
    }

    @Override
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
}
