package unsw.blackout.entities;

import unsw.blackout.files.FileStorage;
import unsw.utils.Angle;

public class HandheldDevice extends Device {
    private final double range = 50000;

    public HandheldDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position, new FileStorage());
    }

    @Override
    public double getRange() {
        return range;
    }
}
