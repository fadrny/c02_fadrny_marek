package model;

import java.util.ArrayList;
import java.util.List;

public class PolygonClipper {

    public Polygon clip(Polygon subject, Polygon clipper) {
        if (clipper.getSize() < 3)
            return new Polygon();

        List<Point> inputPoints = subject.getPoints();
        List<Point> clipPoints = clipper.getPoints();

        // Determine orientation of the clipper
        boolean isClipperCW = isClockwise(clipPoints);

        List<Point> outputPoints = inputPoints;

        // Clip against each edge of the clipping polygon
        for (int i = 0; i < clipPoints.size(); i++) {
            Point p1 = clipPoints.get(i);
            Point p2 = clipPoints.get((i + 1) % clipPoints.size());

            List<Point> input = outputPoints;
            outputPoints = new ArrayList<>();

            if (input.isEmpty())
                break;

            Point S = input.get(input.size() - 1);

            for (Point E : input) {
                if (isInside(E, p1, p2, isClipperCW)) {
                    if (!isInside(S, p1, p2, isClipperCW)) {
                        outputPoints.add(getIntersection(S, E, p1, p2));
                    }
                    outputPoints.add(E);
                } else if (isInside(S, p1, p2, isClipperCW)) {
                    outputPoints.add(getIntersection(S, E, p1, p2));
                }
                S = E;
            }
        }

        Polygon result = new Polygon();
        for (Point p : outputPoints) {
            result.pushPoint(p);
        }
        return result;
    }

    private boolean isClockwise(List<Point> points) {
        long sum = 0;
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());
            sum += (long) (p2.getX() - p1.getX()) * (p2.getY() + p1.getY());
        }
        return sum < 0;
    }

    private boolean isInside(Point p, Point edgeStart, Point edgeEnd, boolean isClipperCW) {
        double vecEdgeX = edgeEnd.getX() - edgeStart.getX();
        double vecEdgeY = edgeEnd.getY() - edgeStart.getY();
        double vecPointX = p.getX() - edgeStart.getX();
        double vecPointY = p.getY() - edgeStart.getY();

        // Cross Product (2D)
        double crossProduct = vecEdgeX * vecPointY - vecEdgeY * vecPointX;

        // Check side based on orientation
        return isClipperCW ? crossProduct >= 0 : crossProduct <= 0;
    }

    private Point getIntersection(Point p1, Point p2, Point clip1, Point clip2) {
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();
        double x3 = clip1.getX(), y3 = clip1.getY();
        double x4 = clip2.getX(), y4 = clip2.getY();

        double det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        if (Math.abs(det) < 0.000000001)
            return new Point((int) x1, (int) y1);

        double xi = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / det;
        double yi = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / det;

        return new Point((int) Math.round(xi), (int) Math.round(yi));
    }
}
