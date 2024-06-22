package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class TeleportingSatellite extends Satellite {
    private final double velocity = 1000;
    private final double range = 200000;
    private final int sendBandwidth = 10;
    private final int receiveBandwidth = 15;
    private final int maxStorage = 200;
    private int direction = MathsHelper.ANTI_CLOCKWISE;
    private boolean teleported = false;

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position, 200000);
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
        // If the satellite has teleported, reset the flag and do not move for the rest of the turn
        if (teleported) {
            teleported = false;
        }

        Angle newPosition = Orbit.getNewPosition(velocity, getHeight(), getPosition(), direction);
        // Teleport to 0 degrees if the satellite is at 180 degrees and change direction
        if (newPosition.toDegrees() >= 180) {
            setPosition(Angle.fromDegrees(0));
            setDirection(MathsHelper.CLOCKWISE);
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

    public boolean hasTeleported() {
        return teleported;
    }

    @Override
    public boolean maxStorageReached(int size) {
        return getStorageUsed() + size > maxStorage;
    }

    @Override
    public boolean maxFilesReached() {
        return false;
    }

    @Override
    public int getSendBandwidth() {
        return sendBandwidth;
    }

    @Override
    public int getReceiveBandwidth() {
        return receiveBandwidth;
    }
}
