package unsw.blackout;

import java.util.List;

public interface FileTransferring {
    public boolean canUploadFile(File file);

    public boolean canDownloadFile(File file);

    public List<File> getDownloadingFiles();

    public List<File> getUploadingFiles();

    public List<File> getAvailableFiles();

    public void uploadFile(File file);

    public void downloadFile(File file);
}
