package unsw.blackout.entities;

import unsw.blackout.algorithms.Orbit;
import unsw.blackout.files.FileStorage;
import unsw.utils.Angle;
import static unsw.utils.MathsHelper.CLOCKWISE;

public class StandardSatellite extends Satellite {
    private final double velocity = 2500;
    private final double range = 150000;
    private final int sendBandwidth = 1;
    private final int receiveBandwidth = 1;
    private final int direction = CLOCKWISE;

    public StandardSatellite(String id, String type, Angle position, double height) {
        super(id, type, position, height, new FileStorage(80, 3));
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
    public double getRange() {
        return range;
    }

    @Override
    public int getSendBandwidth() {
        return sendBandwidth;
    }

    @Override
    public int getReceiveBandwidth() {
        return receiveBandwidth;
    }

    @Override
    public void orbit() {
        Angle newPosition = Orbit.getNewPosition(velocity, getHeight(), getPosition(), direction);
        setPosition(newPosition);
    }

}
