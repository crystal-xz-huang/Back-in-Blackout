package unsw.blackout;

import java.util.*;
import unsw.utils.Angle;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;

public abstract class Entity {
    private String id;
    private String type;
    private Angle position;
    private double height;
    private double range;
    private Map<String, File> files;
    private int incomingFiles;
    private int outgoingFiles;

    public Entity(String id, String type, Angle position, double height, double range) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.height = height;
        this.range = range;
        this.files = new HashMap<>();
        this.incomingFiles = 0;
        this.outgoingFiles = 0;
    }

    public abstract boolean supports(Entity to);

    public abstract int getSendBandwidth();

    public abstract int getReceiveBandwidth();

    public abstract int getStorageCapacity();

    public abstract int getFileCapacity();

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

    public double getRange() {
        return range;
    }

    public int getFileSize(String filename) {
        return files.get(filename).getSize();
    }

    public String getFileContent(String filename) {
        return files.get(filename).getContent();
    }

    public int getNumIncomingTransfers() {
        return incomingFiles;
    }

    public int getNumOutgoingTransfers() {
        return outgoingFiles;
    }

    public EntityInfoResponse getInfo() {
        Map<String, FileInfoResponse> fileInfo = new HashMap<>();
        for (String filename : files.keySet()) {
            fileInfo.put(filename, files.get(filename).getInfo());
        }
        return new EntityInfoResponse(id, position, height, type, fileInfo);
    }

    public boolean maxStorageReached(int size) {
        int storageUsed = files.values().stream().mapToInt(file -> file.getSize()).sum();
        return storageUsed + size > getStorageCapacity();
    }

    public boolean maxFilesReached() {
        return files.size() >= getFileCapacity();
    }

    public boolean hasSendBandwidth() {
        return getSendBandwidth() > 0;
    }

    public boolean hasReceiveBandwidth() {
        return getReceiveBandwidth() > 0;
    }

    public void addFile(String filename, String content, Boolean isComplete) {
        files.put(filename, new File(filename, content, isComplete));
    }

    public void addFile(File file) {
        files.put(file.getFileName(), file);
    }

    public void removeFile(String filename) {
        files.remove(filename);
    }

    public File sendTransfer(String fileName) {
        incrementOutgoingTransfers();
        return files.get(fileName);
    }

    public File receiveTransfer(File file) {
        File newFile = new File(file.getFileName(), file.getContent(), false);
        files.put(file.getFileName(), newFile);
        incrementIncomingTransfers();
        return newFile;
    }

    public void incrementIncomingTransfers() {
        incomingFiles++;
    }

    public void incrementOutgoingTransfers() {
        outgoingFiles++;
    }

    public void decrementIncomingTransfers() {
        incomingFiles--;
    }

    public void decrementOutgoingTransfers() {
        outgoingFiles--;
    }

    public boolean canSendFile(String filename) {
        return files.containsKey(filename) && files.get(filename).isComplete();
    }

    public boolean canReceiveFile(String filename) {
        return !files.containsKey(filename);
    }
}
