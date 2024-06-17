package unsw.blackout;

import unsw.utils.Angle;
import unsw.blackout.files.File;

public abstract class Satellite extends SpaceEntity implements Orbit {
    public Satellite(String satelliteId, String type, double height, Angle position, int range) {
        super(satelliteId, type, position, height, range);
    }

    public abstract int getReceiveBandwidth();

    public abstract int getSendBandwidth();

    public abstract int getMaxStorage();

    public abstract int getMaxFiles();

    public abstract boolean hasStorageFor(File file);

    public abstract int getAvailableStorage();

    @Override
    public boolean supports(SpaceEntity dest) {
        return true;
    }
}
