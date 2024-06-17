package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class StandardSatellite extends Satellite {
    private final double velocity = 2500;
    private final int direction = MathsHelper.CLOCKWISE;
    private final int sendBandwith = 1;
    private final int receiveBandwith = 1;

    // private final int MAX_FILES = 3;
    // private final int MAX_STORAGE = 80;
    // private int storedBytes = 0;
    // private int storedFiles = 0;

    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position, 150000);
    }

    // /**
    //  * Check if the satellite can upload the file
    //  * (1) File exists and is fully downloaded
    //  * (2) Has available storage
    //  * (3) Has available send bandwidth
    //  * @param file
    //  * @return true if the satellite can upload the file
    //  */
    // @Override
    // public boolean canUploadFile(File file) {
    //     return file.isDownloaded() && hasAvailableStorage() && hasAvailableSendBandwidth();

    // }

    @Override
    public int getReceiveBandwidth() {
        return receiveBandwith;
    }

    @Override
    public int getSendBandwidth() {
        return sendBandwith;
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
    public void orbit(double minutes) {
        Angle newPosition = Orbit.getNewPosition(minutes, velocity, getHeight(), getPosition(), direction);
        setPosition(newPosition);
    }
}
