package unsw.blackout.entities;

import unsw.blackout.algorithms.Orbit;
import unsw.blackout.files.FileStorage;
import unsw.utils.Angle;
import static unsw.utils.MathsHelper.CLOCKWISE;

public class ElephantSatellite extends Satellite {
    private final double range = 400000;
    private final double velocity = 2500;
    private final int sendBandwidth = 20;
    private final int receiveBandwidth = 20;
    private final int direction = CLOCKWISE;

    public ElephantSatellite(String id, String type, Angle position, double height) {
        super(id, type, position, height, new FileStorage(90));
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

    @Override
    public boolean supports(Entity dest) {
        return dest instanceof DesktopDevice || dest instanceof LaptopDevice
                || (dest instanceof Satellite && !(dest instanceof TeleportingSatellite));
    }
}
