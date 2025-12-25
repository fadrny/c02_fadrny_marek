package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

import java.util.List;

public class PolygonRasterizer {

    private final LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {

        List<Point> points = polygon.getPoints();
        int n = points.size();

        for (int i = 0; i < n; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % n); // Wrap around to the first point
            lineRasterizer.rasterize(new Line(p1, p2));
        }
    }

    public void rasterizeIterable(Iterable<Polygon> polygons) {
        for (Polygon polygon : polygons) {
            rasterize(polygon);
        }
    }

}
