package unsw.blackout;

import java.util.*;
import unsw.utils.Angle;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;

public abstract class Entity implements FileTransferring {
    private String id;
    private String type;
    private Angle position;
    private double height;
    private double range;
    private Map<String, File> files = new HashMap<>();
    private int numIncomingTransfers = 0;
    private int numOutgoingTransfers = 0;

    public Entity(String id, String type, Angle position, double height, double range) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.height = height;
        this.range = range;
    }

    public abstract boolean supports(Entity to);

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

    public File getFile(String filename) {
        return files.get(filename);
    }

    public EntityInfoResponse getInfo() {
        Map<String, FileInfoResponse> fileInfo = new HashMap<>();
        List<File> files = new ArrayList<>(this.files.values());
        files.stream().forEach(file -> fileInfo.put(file.getFileName(), file.getFileInfoResponse()));
        return new EntityInfoResponse(id, position, height, type, fileInfo);
    }

    public void addFile(String filename, String content, Boolean isComplete) {
        files.put(filename, new File(filename, content, isComplete));
    }

    public void removeFile(String filename) {
        files.remove(filename);
    }

    public boolean hasFile(String filename) {
        return files.containsKey(filename);
    }

    public int getFileSize(String filename) {
        return files.get(filename).getSize();
    }

    public String getFileContent(String filename) {
        return files.get(filename).getContent();
    }

    public boolean isFileComplete(String filename) {
        return files.get(filename).isComplete();
    }

    public void setFileComplete(String filename, boolean isComplete) {
        files.get(filename).setComplete(isComplete);
    }

    public void updateFile(String filename, String content) {
        files.get(filename).setContent(content);
    }

    public void incrementIncomingTransfers() {
        numIncomingTransfers++;
    }

    public void incrementOutgoingTransfers() {
        numOutgoingTransfers++;
    }

    public void decrementIncomingTransfers() {
        numIncomingTransfers--;
    }

    public void decrementOutgoingTransfers() {
        numOutgoingTransfers--;
    }

    @Override
    public int getSendingSpeed() {
        if (numOutgoingTransfers == 0)
            return 0;
        return getSendBandwidth() / numOutgoingTransfers;
    }

    @Override
    public int getReceivingSpeed() {
        if (numIncomingTransfers == 0)
            return 0;
        return getReceiveBandwidth() / numIncomingTransfers;
    }

    @Override
    public boolean hasReceiveBandwidth() {
        return getReceivingSpeed() < getReceiveBandwidth();
    }

    @Override
    public boolean hasSendBandwidth() {
        return getSendingSpeed() < getSendBandwidth();
    }

    public int getStorageUsed() {
        return files.values().stream().mapToInt(file -> file.getSize()).sum();
    }

    public int getNumFiles() {
        return files.size();
    }
}
