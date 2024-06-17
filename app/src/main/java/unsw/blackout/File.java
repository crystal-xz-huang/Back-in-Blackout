package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private String filename;
    private String content; // content of the file
    private int size; // final size of the file
    private int downloadedBytes; // number of bytes downloaded
    private boolean isTransferComplete;

    public File(String filename, String content, int downloadedBytes) {
        this.filename = filename;
        this.content = content;
        this.size = content.length();
        this.downloadedBytes = downloadedBytes;
        this.isTransferComplete = false;
    }

    public File(String filename, String content) {
        this.filename = filename;
        this.content = content;
        this.size = content.length();
        this.downloadedBytes = content.length(); // instantly uploaded
        this.isTransferComplete = true;
    }

    public String getFilename() {
        return filename;
    }

    public int getSize() {
        return size;
    }

    public String getContent() {
        return content;
    }

    public String getData() {
        return content.substring(0, downloadedBytes);
    }

    public void setTransferComplete(Boolean isTransferComplete) {
        this.isTransferComplete = isTransferComplete;
    }

    public boolean isTransferComplete() {
        return isTransferComplete;
    }

    public FileInfoResponse getFileInfoResponse() {
        return new FileInfoResponse(filename, getData(), size, isTransferComplete);
    }

    public void setDownloadedBytes(int downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }
}
