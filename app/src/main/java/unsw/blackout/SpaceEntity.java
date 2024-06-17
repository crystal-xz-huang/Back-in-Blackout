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

    public List<File> getFiles() {
        return files;
    }

    public double getRange() {
        return range;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public void addFile(File file) {
        files.add(file);
    }

    public abstract boolean supports(SpaceEntity dest);

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        SpaceEntity other = (SpaceEntity) obj;
        return id.equals(other.id);
    }
}
