package unsw.blackout;

import unsw.utils.Angle;

public abstract class Satellite extends SpaceEntity implements Orbit {
    public Satellite(String satelliteId, String type, double height, Angle position, int range) {
        super(satelliteId, type, position, height, range);
    }

    @Override
    public boolean supports(SpaceEntity dest) {
        return true;
    }
}
