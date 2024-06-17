package unsw.blackout;

public class FileTransfer {
    private File file;
    private SpaceEntity sender; // id of the entity sending the file
    private SpaceEntity receiver; // id of the entity receiving the file
    private int progress; // number of bytes transferred
    private int transferRate; // number of bytes transferred per second

    public FileTransfer(File file, SpaceEntity sender, SpaceEntity receiver) {
        this.file = file;
        this.sender = sender;
        this.receiver = receiver;
        this.progress = 0;
        this.transferRate = 0;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return file.getFilename();
    }

    public int getFinalSize() {
        return file.getSize();
    }

    public String getContent() {
        return file.getContent();
    }

    public SpaceEntity getSender() {
        return sender;
    }

    public SpaceEntity getReceiver() {
        return receiver;
    }

    public int getTransferredBytes() {
        return progress;
    }

    public void setTransferRate(int transferRate) {
        this.transferRate = transferRate;
    }

    public void updateProgress() {
        if (progress + transferRate > file.getSize()) {
            file.setTransferComplete(true);
        } else {
            progress += transferRate;
        }

    }

    public boolean isComplete() {
        return progress == file.getSize();
    }
}
