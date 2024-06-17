package unsw.blackout.files;

import unsw.blackout.SpaceEntity;

/**
 * Represents a file transfer between two space entities
 **/
public class FileTransfer {
    private String fileName;
    private SpaceEntity fromEntity;
    private SpaceEntity toEntity;
    private int finalSize; // in bytes
    private int transferredBytes; // in bytes

    public FileTransfer(String fileName, SpaceEntity fromEntity, SpaceEntity toEntity, int finalSize) {
        this.fileName = fileName;
        this.fromEntity = fromEntity;
        this.toEntity = toEntity;
        this.finalSize = finalSize;
        this.transferredBytes = 0;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SpaceEntity getFromEntity() {
        return fromEntity;
    }

    public void setFromEntity(SpaceEntity fromEntity) {
        this.fromEntity = fromEntity;
    }

    public SpaceEntity getToEntity() {
        return toEntity;
    }

    public void setToEntity(SpaceEntity toEntity) {
        this.toEntity = toEntity;
    }

    public int getFinalSize() {
        return finalSize;
    }

    public void setFinalSize(int finalSize) {
        this.finalSize = finalSize;
    }

    public int getTransferredBytes() {
        return transferredBytes;
    }

    public void updateProgress(int transferredBytes) {
        this.transferredBytes += transferredBytes;
        if (transferredBytes > finalSize) {
            this.transferredBytes = finalSize;
        }
    }

    public boolean isComplete() {
        return transferredBytes >= finalSize;
    }
}
