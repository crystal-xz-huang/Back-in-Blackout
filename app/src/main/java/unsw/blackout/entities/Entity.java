package unsw.blackout.entities;

import java.util.*;
import unsw.utils.Angle;
import unsw.blackout.files.File;
import unsw.blackout.files.FileStorage;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;

public abstract class Entity {
    private String id;
    private String type;
    private Angle position;
    private double height;
    private FileStorage files;

    public Entity(String id, String type, Angle position, double height, FileStorage files) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.height = height;
        this.files = files;
    }

    public abstract boolean supports(Entity to);

    public abstract int getSendBandwidth();

    public abstract int getReceiveBandwidth();

    public abstract double getRange();

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Angle getPosition() {
        return position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public double getHeight() {
        return height;
    }

    public EntityInfoResponse getInfo() {
        List<File> filesList = files.getFiles();
        Map<String, FileInfoResponse> fileInfo = new HashMap<>();
        for (File file : filesList) {
            fileInfo.put(file.getFileName(), file.getInfo());
        }
        return new EntityInfoResponse(id, position, height, type, fileInfo);
    }

    public int getSendingSpeed() {
        int outgoingFiles = files.getNumOutgoingFiles();
        if (outgoingFiles == 0) {
            return getSendBandwidth();
        }
        return getSendBandwidth() / outgoingFiles;
    }

    public int getReceivingSpeed() {
        int incomingFiles = files.getNumIncomingFiles();
        if (incomingFiles == 0) {
            return getReceiveBandwidth();
        }
        return getReceiveBandwidth() / incomingFiles;
    }

    public boolean hasSendBandwidth() {
        return getSendingSpeed() > 0;
    }

    public boolean hasReceiveBandwidth() {
        return getReceivingSpeed() > 0;
    }

    public FileStorage getFiles() {
        return files;
    }

    public boolean canSendFile(String fileName) {
        File file = files.getFile(fileName);
        return file != null && file.isComplete();
    }

    public boolean canReceiveFile(String fileName) {
        File file = files.getFile(fileName);
        return file == null;
    }
}
