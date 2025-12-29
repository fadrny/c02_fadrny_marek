package model;

public class Edge {
    private double x;
    private final double dx;
    private final int minY;
    private int maxY;

    public Edge(int x1, int y1, int x2, int y2) {
        this.x = x1;
        this.minY = y1;
        this.maxY = y2;

        this.dx = (double) (x2 - x1) / (y2 - y1);
    }

    public double getX() {
        return x;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public void updateX() {
        x += dx;
    }
}
