package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private String filename;
    private String content;
    private int size;
    private boolean isTransferComplete; // is transfer for this file complete?

    public File(String filename, String content) {
        this.filename = filename;
        this.content = content;
        this.size = content.length();
        this.isTransferComplete = true;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }

    public FileInfoResponse getFileInfoResponse() {
        return new FileInfoResponse(filename, content, size, isTransferComplete);
    }

}
