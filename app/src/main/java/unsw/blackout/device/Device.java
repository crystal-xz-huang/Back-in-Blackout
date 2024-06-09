package unsw.blackout.device;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;
import unsw.blackout.*;

public abstract class Device {
    private String deviceId;
    private String type;
    private Angle position; // angle relative to the x-axis on Jupiter's ring
    private int range; // max range from which it can connect to satellites
    private final double height; // radius of Jupiter
    private List<File> files;

    /**
     * Create a new device with the specified parameters
     * @param deviceId
     * @param type
     * @param position Angle relative to the x-axis
     * @param range
     */
    public Device(String deviceId, String type, Angle position, int range) {
        this.deviceId = deviceId;
        this.type = type;
        this.position = position;
        this.range = range;
        this.height = RADIUS_OF_JUPITER;
        this.files = new ArrayList<>();
    }

    public void addFile(File file) {
        files.add(file);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Angle getPosition() {
        return position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getHeight() {
        return height;
    }

    public List<File> getFiles() {
        return files;
    }
}
