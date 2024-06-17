package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private String filename;
    private String content;
    private int size; // final size of the file
    private String data; // currently transferred data

    public File(String filename, String content) {
        this.filename = filename;
        this.content = content;
        this.size = content.length();
        this.data = "";
    }

    public boolean isTransferComplete() {
        return data.equals(content);
    }

    public String getFilename() {
        return filename;
    }

    public String getData() {
        return data;
    }

    public int getFileSize() {
        return size;
    }

    public FileInfoResponse getFileInfoResponse() {
        return new FileInfoResponse(filename, data, size, isTransferComplete());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        File file = (File) obj;
        return filename.equals(file.filename);
    }
}
