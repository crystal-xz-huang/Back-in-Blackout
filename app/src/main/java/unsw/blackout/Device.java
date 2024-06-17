package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public abstract class Device extends SpaceEntity {
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

    @Override
    public boolean supports(SpaceEntity dest) {
        if (dest instanceof Device) {
            return false;
        }
        return true;
    }

    @Override
    public int getReceiveBandwidth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getSendBandwidth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxFiles() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxStorage() {
        return Integer.MAX_VALUE;
    }
}
