package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private String fileName;
    private String content; // content of the file
    private String data; // data that has been transferred

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

    // Total size of the complete file
    public int getSize() {
        return content.length();
    }

    // Final content of the file
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Data that has been transferred
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void updateData(int progress) {
        data = content.substring(0, progress);
    }

    // Whether the file has been completely transferred for this entity
    // sender is always complete
    public boolean isComplete() {
        return data.equals(content);
    }

    public void setComplete() {
        this.data = content;
    }

    public FileInfoResponse getInfo() {
        return new FileInfoResponse(fileName, data, getSize(), isComplete());
    }

    /**
     * Remove the remaining t bytes from the content
     * and add the remaining data to the data
     * @param progress - the number of bytes that have been transferred so far
     */
    public void removeRemainingTBytes(int progress) {
        String remainingData = content.substring(progress);
        remainingData = remainingData.replaceAll("t", "");
        data += remainingData;
    }

    /**
     * Remove the t bytes from the content
     */
    public void removeTBytes() {
        content = content.replaceAll("t", "");
    }
}
