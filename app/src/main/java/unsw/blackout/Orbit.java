package unsw.blackout;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.CLOCKWISE;

public interface Orbit {
    private static Angle normalizeAngle(Angle angle) {
        double degrees = angle.toDegrees();
        double normalizedDegrees = (degrees % 360 + 360) % 360;
        return Angle.fromDegrees(normalizedDegrees);
    }

    public static Angle getNewPosition(double velocity, double height, Angle position, int direction) {
        Angle angularDisplacement = Angle.fromRadians(velocity / height);
        Angle newPosition;
        if (direction == CLOCKWISE) {
            newPosition = position.subtract(angularDisplacement);
        } else {
            newPosition = position.add(angularDisplacement);
        }
        return normalizeAngle(newPosition);
    }

    public void orbit();

    public int getDirection();

    public double getVelocity();

}
