package unsw.blackout.entities;

import unsw.blackout.algorithms.Orbit;
import unsw.blackout.files.FileStorage;
import unsw.utils.Angle;

public abstract class Satellite extends Entity implements Orbit {
    public Satellite(String id, String type, Angle position, double height, FileStorage files) {
        super(id, type, position, height, files);
    }

    @Override
    public boolean supports(Entity dest) {
        return true;
    }
}
