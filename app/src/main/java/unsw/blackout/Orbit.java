package unsw.blackout;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.CLOCKWISE;

public interface Orbit {
    /**
     * Normalize the angle given in radians to the range [0, 2*PI)
     * @param angle
     * @return normalized angle
     */
    public static Angle normalizeAngle(Angle angle) {
        double degrees = angle.toDegrees();
        double normalizedDegrees = (degrees % 360 + 360) % 360;
        return Angle.fromDegrees(normalizedDegrees);
    }

    /**
     * Get the new position of the orbit after the specified number of minutes
     * @param minutes
     * @param velocity
     * @param height
     * @param position
     * @param direction
     * @return new position of the orbit
     */
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

    /**
     * Orbit around Jupiter for 1 minute
     */
    public void orbit();

    public int getDirection();

    public double getVelocity();

}
