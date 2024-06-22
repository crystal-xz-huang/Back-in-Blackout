package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class StandardSatellite extends Satellite {
    private final double velocity = 2500;
    private final int direction = MathsHelper.CLOCKWISE;
    private final int sendBandwith = 1;
    private final int receiveBandwith = 1;
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
    }

    @Override
    public int getSendBandwidth() {
        return sendBandwith;
    }

    @Override
    public int getReceiveBandwidth() {
        return receiveBandwith;
    }

    @Override
    public boolean maxStorageReached(int size) {
        return getStorageUsed() + size > maxStorage;
    }

    @Override
    public boolean maxFilesReached() {
        return getNumFiles() >= maxFiles;
    }

}
