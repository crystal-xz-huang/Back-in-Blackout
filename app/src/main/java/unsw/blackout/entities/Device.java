package unsw.blackout.entities;

import unsw.blackout.files.FileStorage;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public abstract class Device extends Entity {
    public Device(String deviceId, String type, Angle position, FileStorage files) {
        super(deviceId, type, position, MathsHelper.RADIUS_OF_JUPITER, files);
    }

    /**
     * Devices do not support communication with other devices (cannot send files to other devices)
     * Devices however, can upload/download files to/from satellites
     */
    @Override
    public boolean supports(Entity dest) {
        if (dest instanceof Device) {
            return false;
        }
        return true;
    }

    @Override
    public int getSendBandwidth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getReceiveBandwidth() {
        return Integer.MAX_VALUE;
    }
}
