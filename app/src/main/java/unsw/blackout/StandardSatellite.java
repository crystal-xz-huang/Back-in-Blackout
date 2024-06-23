package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class StandardSatellite extends Satellite {
    private final double velocity = 2500;
    private final int direction = MathsHelper.CLOCKWISE;
    private final int defaultSendBandwidth = 1;
    private final int defaultReceiveBandwith = 1;
    private final int maxStorage = 80;
    private final int maxFiles = 3;

    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position, 150000);
    }

    @Override
    public double getVelocity() {
        return velocity;
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public void orbit() {
        Angle newPosition = Orbit.getNewPosition(velocity, getHeight(), getPosition(), direction);
        setPosition(newPosition);
        System.out.println("Position: " + this.getPosition().toDegrees());
    }

    @Override
    public int getDefaultSendBandwidth() {
        return defaultSendBandwidth;
    }

    @Override
    public int getDefaultReceiveBandwidth() {
        return defaultReceiveBandwith;
    }

    @Override
    public int getStorageCapacity() {
        return maxStorage;
    }

    @Override
    public int getFileCapacity() {
        return maxFiles;
    }

}
