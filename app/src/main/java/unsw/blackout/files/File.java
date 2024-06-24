package unsw.blackout.files;

import unsw.response.models.FileInfoResponse;

public class File {
    private String fileName;
    private String content;
    private String data;

    public File(String fileName, String content, boolean isComplete) {
        this.fileName = fileName;
        this.content = content;
        if (isComplete) {
            this.data = content;
        } else {
            this.data = "";
        }
    }

    public String getFileName() {
        return fileName;
    }

    public int getSize() {
        return content.length();
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

    public void updateData(int progress) {
        data = content.substring(0, progress);
    }

    public boolean isComplete() {
        return data.equals(content);
    }

    public void setComplete() {
        this.data = content;
    }

    public FileInfoResponse getInfo() {
        return new FileInfoResponse(fileName, data, getSize(), isComplete());
    }

    public void removeRemainingTBytes(int progress) {
        String remainingData = content.substring(progress);
        remainingData = remainingData.replaceAll("t", "");
        content = content.substring(0, progress) + remainingData;
    }

    public void removeTBytes() {
        content = content.replaceAll("t", "");
    }
}
