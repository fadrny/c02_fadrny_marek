package model;

import java.util.ArrayList;

public interface Pointable {

    ArrayList<Point>  getPoints();

    Point getPoint(int index);

    void editPoint(int index, Point point);

}
