package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private String fileName;
    private String content; // content of the file
    private String data; // currently transferred data
    private int size; // total size of the complete file
    private boolean isComplete; // whether the file has been completely transferred for this entity

    public File(String fileName, String content, boolean isComplete) {
        this.fileName = fileName;
        this.content = content;
        this.size = content.length();
        if (isComplete) {
            this.data = content;
            this.isComplete = true;
        } else {
            this.data = "";
            this.isComplete = false;
        }
    }

    public String getFileName() {
        return fileName;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public void removeTBytes() {
        content = content.replaceAll("t", "");
    }

    public FileInfoResponse getFileInfoResponse() {
        return new FileInfoResponse(fileName, data, size, isComplete);
    }

}
