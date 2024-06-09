package unsw.blackout.device;

import unsw.utils.Angle;

public class LaptopDevice extends Device {
    public LaptopDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position, 100000);
    }
}
