package unsw.blackout.satellite;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    private final int velocity = 1000;
    private final int range = 200000;
    private final int sendBandwidth = 10;
    private final int receiveBandwidth = 15;

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
    }

}
