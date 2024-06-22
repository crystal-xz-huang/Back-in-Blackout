package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class RelaySatellite extends Satellite {
    private final double range = 300000;
    private final double velocity = 1500;
    private int direction;

    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position, 300000);
        if (position.toDegrees() >= 190 && position.toDegrees() < 345) {
            direction = MathsHelper.CLOCKWISE;
        } else if ((position.toDegrees() >= 140 && position.toDegrees() < 190)) {
            direction = MathsHelper.CLOCKWISE;
        } else {
            direction = MathsHelper.ANTI_CLOCKWISE;
        }
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
        Angle displacement = Angle.fromRadians(velocity / getHeight());
        double degrees = this.getPosition().toDegrees();
        switch (direction) {
        case MathsHelper.ANTI_CLOCKWISE:
            if (degrees >= 190 && degrees < 345) {
                this.setPosition(this.getPosition().subtract(displacement));
                this.direction = MathsHelper.CLOCKWISE;
            } else {
                this.setPosition(this.getPosition().add(displacement));
            }
            break;
        case MathsHelper.CLOCKWISE:
            if (degrees <= 140 && degrees >= -15) {
                this.setPosition(this.getPosition().add(displacement));
                this.direction = MathsHelper.ANTI_CLOCKWISE;
            } else {
                this.setPosition(this.getPosition().subtract(displacement));
            }
            break;
        default:
            break;
        }

        /*
        if (degrees >= 190 && degrees < 345) {
            this.setPosition(this.getPosition().subtract(displacement));
            this.direction = MathsHelper.CLOCKWISE;
        } else if (degrees <= 140) {
            this.setPosition(this.getPosition().add(displacement));
            this.direction = MathsHelper.ANTI_CLOCKWISE;
        } else if (this.direction == MathsHelper.ANTI_CLOCKWISE) {
            this.setPosition(this.getPosition().add(displacement));
        } else if (this.direction == MathsHelper.CLOCKWISE) {
            this.setPosition(this.getPosition().subtract(displacement));
        }
        */
    }

    @Override
    public boolean hasReceiveBandwidth() {
        return true;
    }

    @Override
    public boolean hasSendBandwidth() {
        return true;
    }

    @Override
    public boolean maxStorageReached(int size) {
        return false;
    }

    @Override
    public boolean maxFilesReached() {
        return false;
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
