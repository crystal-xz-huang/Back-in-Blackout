package unsw.blackout;

import java.util.*;
import unsw.utils.Angle;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;

public abstract class SpaceEntity {
    private String id;
    private String type;
    private Angle position;
    private double height;
    private double range;
    private List<File> files;

    public SpaceEntity(String id, String type, Angle position, double height, double range) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.height = height;
        this.range = range;
        this.files = new ArrayList<>();
    }

    public EntityInfoResponse getInfo() {
        Map<String, FileInfoResponse> fileInfo = new HashMap<>();
        for (File file : files) {
            fileInfo.put(file.getFilename(), file.getFileInfoResponse());
        }
        return new EntityInfoResponse(id, position, height, type, fileInfo);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Angle getPosition() {
        return position;
    }

    public double getHeight() {
        return height;
    }

    public double getRange() {
        return range;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public void addFile(String filename, String content) {
        File file = new File(filename, content);
        files.add(file);
    }

    public void addFile(String filename, String content, int downloadedBytes) {
        File file = new File(filename, content, downloadedBytes);
        files.add(file);
    }

    public void removeFile(String fileName) {
        files.removeIf(file -> file.getFilename().equals(fileName));
    }

    public File getFile(String fileName) {
        for (File file : files) {
            if (file.getFilename().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public List<File> listFiles() {
        return files;
    }

    public void startFileTransfer(String filename) {
        File file = getFile(filename);
        if (file != null) {
            file.setTransferComplete(false);
        }
    }

    public abstract boolean supports(SpaceEntity dest);

    public abstract int getSendBandwidth();

    public abstract int getReceiveBandwidth();

    public abstract int getMaxFiles();

    public abstract int getMaxStorage();
}
