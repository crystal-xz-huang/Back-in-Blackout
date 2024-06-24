package unsw.blackout.entities;

import unsw.blackout.files.FileStorage;
import unsw.utils.Angle;

public class LaptopDevice extends Device {
    private final double range = 100000;

    public LaptopDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position, new FileStorage());
    }

    @Override
    public double getRange() {
        return range;
    }
}
