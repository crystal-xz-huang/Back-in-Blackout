package blackout;

import static unsw.utils.MathsHelper.CLOCKWISE;

import unsw.blackout.algorithms.Orbit;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class MyTestHelpers {

    // Calculate the number of minutes until the satellite is in range of the device
    public static int minutesUntilInRange(double satelliteHeight, Angle satelliteAngle, Angle deviceAngle,
            double deviceRange, int minutes, double velocity, int direction) {
        double distance = MathsHelper.getDistance(satelliteHeight, satelliteAngle, deviceAngle);
        boolean isVisible = MathsHelper.isVisible(satelliteHeight, satelliteAngle, deviceAngle);
        if (distance <= deviceRange && isVisible) {
            return minutes;
        } else {
            Angle newPosition = Orbit.getNewPosition(velocity, satelliteHeight, satelliteAngle, MathsHelper.CLOCKWISE);
            return minutesUntilInRange(satelliteHeight, newPosition, deviceAngle, deviceRange, minutes + 1, velocity,
                    direction);
        }
    }

    // Calculate the number of minutes until the satellite is out of range of the device
    public static int minutesUntilOutOfRange(double satelliteHeight, Angle satelliteAngle, Angle deviceAngle,
            double deviceRange, int minutes, int velocity, int direction) {
        double distance = MathsHelper.getDistance(satelliteHeight, satelliteAngle, deviceAngle);
        boolean isVisible = MathsHelper.isVisible(satelliteHeight, satelliteAngle, deviceAngle);
        if (distance > deviceRange || !isVisible) {
            return minutes;
        } else {
            Angle newPosition = Orbit.getNewPosition(velocity, satelliteHeight, satelliteAngle, MathsHelper.CLOCKWISE);
            return minutesUntilOutOfRange(satelliteHeight, newPosition, deviceAngle, deviceRange, minutes + 1, velocity,
                    direction);
        }
    }

    public static Angle getNewPosition(double velocity, double height, Angle position, int direction, int minutes) {
        Angle angularDisplacement = Angle.fromRadians((velocity / height) * minutes);
        Angle newPosition;
        if (direction == CLOCKWISE) {
            newPosition = position.subtract(angularDisplacement);
        } else {
            newPosition = position.add(angularDisplacement);
        }
        return Orbit.normalizeAngle(newPosition);
    }

    // Calculate the number of minutes until the satellite is out of range of the other satellite
    public static int minutesUntilOutOfRange(double satelliteHeight, Angle satelliteAngle, double otherHeight,
            Angle otherAngle, double satelliteRange, int minutes, int satelliteVelocity, int otherVelocity,
            int direction) {
        double distance = MathsHelper.getDistance(satelliteHeight, satelliteAngle, otherHeight, otherAngle);
        boolean isVisible = MathsHelper.isVisible(satelliteHeight, satelliteAngle, otherHeight, otherAngle);
        if (distance > satelliteRange || !isVisible) {
            return minutes;
        } else {
            Angle newAngle1 = Orbit.getNewPosition(satelliteVelocity, satelliteHeight, satelliteAngle,
                    MathsHelper.CLOCKWISE);
            Angle newAngle2 = Orbit.getNewPosition(otherVelocity, otherHeight, otherAngle, MathsHelper.CLOCKWISE);
            return minutesUntilOutOfRange(satelliteHeight, newAngle1, otherHeight, newAngle2, satelliteRange,
                    minutes + 1, satelliteVelocity, otherVelocity, direction);
        }
    }
}
