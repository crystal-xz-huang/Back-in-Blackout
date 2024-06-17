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
    public static Angle getNewPosition(double minutes, double velocity, double height, Angle position, int direction) {
        Angle displacement = Angle.fromRadians((velocity * minutes) / height);
        Angle newPosition;
        if (direction == MathsHelper.CLOCKWISE) {
            newPosition = position.subtract(displacement);
        } else {
            newPosition = position.add(displacement);
        }
        return newPosition;
    }

    /**
     * Orbit around Jupiter for the specified number of minutes
     * Updates the postiion and direction of the entity after the orbit
     * @param minutes
     */
    public void orbit(double minutes);

    public int getDirection();

    public double getVelocity();

}
