package unsw.blackout.files;

import unsw.response.models.FileInfoResponse;

public class File {
    private String filename;
    private String content; // current content of the file
    private int size; // final size of the file
    private boolean isComplete;

    public File(String filename, String content) {
        this.filename = filename;
        this.content = content;
        this.size = content.length();
        this.isComplete = true; // Files added to devices are complete by default
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

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public FileInfoResponse getFileInfoResponse() {
        return new FileInfoResponse(filename, content, size, isComplete);
    }
}
