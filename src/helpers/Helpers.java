package helpers;
import model.Point;

public class Helpers {


    /**
     * Helper function to lock the angle between two points.
     */
    public static Point lockAngle(Point start, Point end, double angleIncrementDegrees) {
        double deltaX = end.getX() - start.getX();
        double deltaY = end.getY() - start.getY();
        double angleRadians = Math.atan2(deltaY, deltaX); // Angle between X-axis and the line
        double angleDegrees = Math.toDegrees(angleRadians);

        double lockedAngleDegrees = Math.round(angleDegrees / angleIncrementDegrees) * angleIncrementDegrees; // Lock to nearest increment
        double lockedAngleRadians = Math.toRadians(lockedAngleDegrees);

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        int lockedX = start.getX() + (int) Math.round(distance * Math.cos(lockedAngleRadians));
        int lockedY = start.getY() + (int) Math.round(distance * Math.sin(lockedAngleRadians));

        return new Point(lockedX, lockedY, end.getColor());
    }
}
