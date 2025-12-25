package model;

import java.util.ArrayList;
import java.awt.Color;

public class Polygon implements Pointable {

    private final ArrayList<Point> points;

    public Polygon() {
        this.points = new ArrayList<>();
    }

    public ArrayList<Point> getPoints() {
        return new ArrayList<>(points);
    }

    public void pushPoint(Point point) {
        this.points.add(point);
    }

    public Point getPoint(int index) {
        if (index < 0 || index >= points.size())
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        return points.get(index);
    }

    public void updateLastPoint(Point point) {
        if (points.isEmpty())
            throw new IllegalStateException("No points in the polygon to update.");
        points.set(points.size() - 1, point);
    }

    public void updateLastPointsColor(Color color) {
        if (points.isEmpty())
            throw new IllegalStateException("No points in the polygon to update.");
        Point lastPoint = points.getLast();
        points.set(points.size() - 1, new Point(lastPoint.getX(), lastPoint.getY(), color));
    }

    public void editPoint(int index, Point point) {
        if (index < 0 || index >= points.size())
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        points.set(index, point);
    }

    public int getSize() {
        return points.size();
    }
}
