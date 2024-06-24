package unsw.blackout;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    private final double range = 200000;

    public DesktopDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position, new FileStorage());
    }

    @Override
    public double getRange() {
        return range;
    }
}
