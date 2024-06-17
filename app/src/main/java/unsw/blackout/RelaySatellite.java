package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class RelaySatellite extends Satellite {
    private final double range = 300000;
    private final double velocity = 1500;
    private int direction;
    private boolean reverseDirection = false;

    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position, 300000);
        if (position.toDegrees() < 140 || position.toDegrees() > 190) {
            if (position.toDegrees() > 345 || position.toDegrees() < 140) {
                setDirection(MathsHelper.CLOCKWISE);
            } else {
                setDirection(MathsHelper.ANTI_CLOCKWISE);
            }
        }
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

    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Only travels in the region between 140° and 190°
     * When it reaches one side of the region its direction reverses and it travels in the opposite direction.
     * However, this correction is not applied immediately, but only after the next orbit.
     */
    @Override
    public void orbit() {
        // Check if the direction needs to be reversed
        if (reverseDirection) {
            if (getPosition().toDegrees() < 140) {
                setPosition(Angle.fromDegrees(140));
                setDirection(MathsHelper.ANTI_CLOCKWISE);
            } else if (getPosition().toDegrees() > 190) {
                setPosition(Angle.fromDegrees(190));
                setDirection(MathsHelper.CLOCKWISE);
            }
            reverseDirection = false;
        }

        Angle newPosition = Orbit.getNewPosition(getVelocity(), getHeight(), getPosition(), getDirection());

        // Check if the satellite has reached the boundary
        if (newPosition.toDegrees() < 140 || newPosition.toDegrees() > 190) {
            reverseDirection = true;
        }

        setPosition(newPosition);
    }

    @Override
    public int getMaxStorage() {
        return Integer.MIN_VALUE;
    }

    @Override
    public int getMaxFiles() {
        return Integer.MIN_VALUE;
    }

}
