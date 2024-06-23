package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public abstract class Device extends Entity {
    /**
     * Create a new device with the specified parameters
     * @param deviceId
     * @param type
     * @param position Angle relative to the x-axis
     * @param range
     */
    public Device(String deviceId, String type, Angle position, int range) {
        super(deviceId, type, position, MathsHelper.RADIUS_OF_JUPITER, range);
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

    @Override
    public int getStorageCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getFileCapacity() {
        return Integer.MAX_VALUE;
    }
}
