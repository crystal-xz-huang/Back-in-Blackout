package unsw.blackout;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.CLOCKWISE;
import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;

public class RelaySatellite extends Satellite {
    private final double range = 300000;
    private final double velocity = 1500;
    private int direction;

    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position, 300000);

        if (position.toDegrees() >= 140 && position.toDegrees() < 345) {
            this.direction = CLOCKWISE;
        } else {
            this.direction = ANTI_CLOCKWISE;
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

    @Override
    public void orbit() {
        double position = this.getPosition().toDegrees();
        double distance = Angle.fromRadians(velocity / getHeight()).toDegrees();

        double newPosition = position;
        if (direction == CLOCKWISE) {
            newPosition -= distance;
        } else {
            newPosition += distance;
        }

        newPosition = (newPosition % 360 + 360) % 360;

        if (newPosition >= 190 && newPosition < 345) {
            this.setDirection(CLOCKWISE);
        } else if (newPosition <= 140) {
            this.setDirection(ANTI_CLOCKWISE);
        }

        this.setPosition(Angle.fromDegrees(newPosition));
    }

    @Override
    public int getDefaultSendBandwidth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getDefaultReceiveBandwidth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getStorageCapacity() {
        return 0;
    }

    @Override
    public int getFileCapacity() {
        return Integer.MAX_VALUE;
    }

}
