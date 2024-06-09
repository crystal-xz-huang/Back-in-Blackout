package unsw.blackout.satellite;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    private final int velocity = 2500;
    private final int range = 150000;
    private final int sendBandwidth = 1;
    private final int receiveBandwidth = 1;

    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
    }

}
