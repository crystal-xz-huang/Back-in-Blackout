package unsw.blackout.entities;

import unsw.blackout.algorithms.Orbit;
import unsw.blackout.files.FileStorage;
import unsw.utils.Angle;
import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;
import static unsw.utils.MathsHelper.CLOCKWISE;

public class TeleportingSatellite extends Satellite {
    private final double velocity = 1000;
    private final double range = 200000;
    private final int sendBandwidth = 10;
    private final int receiveBandwidth = 15;
    private int direction = ANTI_CLOCKWISE;
    private boolean teleported = false;

    public TeleportingSatellite(String id, String type, Angle position, double height) {
        super(id, type, position, height, new FileStorage(200));
    }

    public boolean hasTeleported() {
        return teleported;
    }

    private void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    public double getVelocity() {
        return velocity;
    }

    @Override
    public double getRange() {
        return range;
    }

    @Override
    public int getDirection() {
        return direction;
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
        teleported = false;

        Angle newPosition = Orbit.getNewPosition(velocity, getHeight(), getPosition(), direction);
        double degrees = newPosition.toDegrees();

        if (getPosition().toDegrees() == 180 && !teleported) {
            setPosition(newPosition);
            teleported = false;
            return;
        }

        if (direction == ANTI_CLOCKWISE && degrees >= 180 && getPosition().toDegrees() < 180) {
            setPosition(Angle.fromDegrees(360));
            setDirection(CLOCKWISE);
            teleported = true;
        } else if (direction == CLOCKWISE && degrees < 180) {
            setPosition(Angle.fromDegrees(0));
            setDirection(ANTI_CLOCKWISE);
            teleported = true;
        } else {
            setPosition(newPosition);
        }
    }

    @Override
    public boolean supports(Entity dest) {
        return dest instanceof Satellite || dest instanceof HandheldDevice || dest instanceof LaptopDevice;
    }
}
