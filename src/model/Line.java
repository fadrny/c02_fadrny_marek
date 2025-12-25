package model;

import java.util.ArrayList;

public class Line implements Pointable {

    private Point p1, p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getStart() {
        return p1;
    }

    public Point getEnd() {
        return p2;
    }

    public void setStart(Point start) {
        this.p1 = start;
    }

    public void setEnd(Point end) {
        this.p2 = end;
    }

    public ArrayList<Point> getPoints(){
        ArrayList<Point> points = new ArrayList<>();
        points.add(p1);
        points.add(p2);
        return points;
    }

    public Point getPoint(int index){
        return switch (index) {
            case 0 -> p1;
            case 1 -> p2;
            default -> throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        };
    }

    public void editPoint(int index, Point point){
        switch(index){
            case 0:
                setStart(point);
                break;
            case 1:
                setEnd(point);
                break;
            default:
                throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }
}
