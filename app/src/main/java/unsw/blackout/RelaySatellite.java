package unsw.blackout;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.CLOCKWISE;
import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;

public class RelaySatellite extends Satellite {
    private final double range = 300000;
    private final double velocity = 1500;
    private int direction;

    public RelaySatellite(String id, String type, Angle position, double height) {
        super(id, type, position, height, new FileStorage(0));

        if (position.toDegrees() >= 140 && position.toDegrees() < 345) {
            this.direction = CLOCKWISE;
        } else {
            this.direction = ANTI_CLOCKWISE;
        }
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    public double getRange() {
        return this.range;
    }

    @Override
    public double getVelocity() {
        return this.velocity;
    }

    @Override
    public int getDirection() {
        return this.direction;
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
    public void orbit() {
        Angle newPosition = Orbit.getNewPosition(velocity, getHeight(), getPosition(), direction);
        double degrees = newPosition.toDegrees();
        if (degrees >= 190 && degrees < 345) {
            this.setDirection(CLOCKWISE);
        } else if (degrees <= 140) {
            this.setDirection(ANTI_CLOCKWISE);
        }

        this.setPosition(Angle.fromDegrees(degrees));
    }

}
