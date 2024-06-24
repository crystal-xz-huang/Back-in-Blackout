package unsw.blackout;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.CLOCKWISE;
import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;

public class TeleportingSatellite extends Satellite {
    private final double velocity = 1000;
    private final double range = 200000;
    private final int defaultSendBandwidth = 10;
    private final int defaultReceiveBandwith = 15;
    private final int maxStorage = 200;
    private int direction = ANTI_CLOCKWISE;
    private boolean teleported = false;

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position, 200000);
    }

    public boolean hasTeleported() {
        return teleported;
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

    public void setDirection(int direction) {
        this.direction = direction;
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
        if (dest instanceof Satellite) {
            return true;
        } else if (dest instanceof Device) {
            String type = dest.getType();
            return type.equals("HandheldDevice") || type.equals("LaptopDevice");
        }
        return false;
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
        return Integer.MAX_VALUE;
    }

}
