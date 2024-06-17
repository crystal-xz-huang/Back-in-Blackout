package unsw.blackout;

import unsw.blackout.files.File;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class TeleportingSatellite extends Satellite {
    private final double velocity = 1000;
    private final double range = 200000;
    private final int sendBandwidth = 10;
    private final int receiveBandwidth = 15;
    private int direction = MathsHelper.ANTI_CLOCKWISE;

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position, 200000);
    }

    @Override
    public int getReceiveBandwidth() {
        return receiveBandwidth;
    }

    @Override
    public int getSendBandwidth() {
        return sendBandwidth;
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
    public void orbit(double minutes) {
        Angle newPosition = Orbit.getNewPosition(minutes, velocity, getHeight(), getPosition(), direction);
        // Teleport to 0 degrees if the satellite is at 180 degrees and change direction
        if (newPosition.toDegrees() >= 180) {
            setPosition(Angle.fromDegrees(0));
            setDirection(MathsHelper.CLOCKWISE);
        } else {
            setPosition(newPosition);
        }
    }

    /**
     * Supports handhelds and laptops only (along with other satellites)
     */
    @Override
    public boolean supports(SpaceEntity dest) {
        if (dest instanceof Satellite) {
            return true;
        } else if (dest instanceof Device) {
            String type = dest.getType();
            return type.equals("HandheldDevice") || type.equals("LaptopDevice");
        }
        return false;
    }

    @Override
    public int getMaxStorage() {
        return 200;
    }

    @Override
    public int getMaxFiles() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean hasStorageFor(File file) {
        int storedBytes = listFiles().stream().mapToInt(f -> f.getSize()).sum();
        return storedBytes + file.getSize() <= getMaxStorage();
    }

    @Override
    public int getAvailableStorage() {
        int storedBytes = listFiles().stream().mapToInt(f -> f.getSize()).sum();
        return getMaxStorage() - storedBytes;
    }

}
