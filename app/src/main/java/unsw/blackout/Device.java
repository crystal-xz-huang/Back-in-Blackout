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

}
