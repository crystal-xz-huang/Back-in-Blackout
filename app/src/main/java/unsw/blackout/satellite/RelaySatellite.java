package unsw.blackout.satellite;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    private final int velocity = 1500;
    private final int range = 300000;

    // unlimited bandwidth
    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
    }

}
