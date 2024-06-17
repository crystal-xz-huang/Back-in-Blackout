package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public interface Orbit {
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
        Angle displacement = Angle.fromRadians(velocity / height);
        Angle newPosition;
        if (direction == MathsHelper.CLOCKWISE) {
            newPosition = position.subtract(displacement);
        } else {
            newPosition = position.add(displacement);
        }
        return newPosition;
    }

    /**
     * Orbit around Jupiter for 1 minute
     */
    public void orbit();

    public int getDirection();

    public double getVelocity();

}
