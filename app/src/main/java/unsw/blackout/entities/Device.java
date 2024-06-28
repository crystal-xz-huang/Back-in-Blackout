package unsw.blackout.entities;

import unsw.blackout.files.FileStorage;
import unsw.utils.Angle;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public abstract class Device extends Entity {
    public Device(String deviceId, String type, Angle position, FileStorage files) {
        super(deviceId, type, position, RADIUS_OF_JUPITER, files);
    }

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
