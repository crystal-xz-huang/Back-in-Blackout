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
        Angle angularDisplacement = Angle.fromRadians(velocity / height);
        Angle newPosition;
        if (direction == MathsHelper.CLOCKWISE) {
            newPosition = position.subtract(angularDisplacement);
        } else {
            newPosition = position.add(angularDisplacement);
        }
        return newPosition;
    }

    /**
     * Check and reverse the direction if boundaries are exceeded
     * @param position the current position
     * @param direction the current direction
     * @return new direction after checking boundaries
     */
    public static int checkAndReverseDirection(Angle position, int direction, double lowerBound, double upperBound) {
        double degrees = position.toDegrees();
        if (direction == MathsHelper.CLOCKWISE && degrees <= lowerBound) {
            return MathsHelper.ANTI_CLOCKWISE;
        } else if (direction == MathsHelper.ANTI_CLOCKWISE && degrees >= upperBound) {
            return MathsHelper.CLOCKWISE;
        }
        return direction;
    }

    /**
     * Orbit around Jupiter for 1 minute
     */
    public void orbit();

    public int getDirection();

    public double getVelocity();

}
