package model;

import java.awt.*;

public class Rectangle extends Polygon {

    public Rectangle() {
        super();
    }

    @Override
    public void pushPoint(Point point) {
        if (getSize() < 2) {
            super.pushPoint(point);
        } else if (getSize() == 2) {
            Point[] corners = calculateRectanglePoints(getPoint(0), getPoint(1), point);
            super.pushPoint(corners[2]);
            super.pushPoint(corners[3]);
        }
    }

    @Override
    public void updateLastPoint(Point point) {
        if (getSize() <= 2) {
            super.updateLastPoint(point);
        } else if (getSize() == 4) {
            Point[] corners = calculateRectanglePoints(getPoint(0), getPoint(1), point);
            super.editPoint(2, corners[2]);
            super.editPoint(3, corners[3]);
        }
    }

    private static Point[] calculateRectanglePoints(Point p1, Point p2, Point p3) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double lenSq = dx * dx + dy * dy;

        if (lenSq == 0) {
            return new Point[]{p1, p2, p3, p3};
        }

        // Výpočet projekce bodu p3 na kolmici k základně
        double k = ((p3.getX() - p1.getX()) * -dy + (p3.getY() - p1.getY()) * dx) / lenSq;

        int offX = (int) (k * -dy);
        int offY = (int) (k * dx);

        return new Point[]{p1, p2, new Point(p2.getX() + offX, p2.getY() + offY), new Point(p1.getX() + offX, p1.getY() + offY)};
    }
}
