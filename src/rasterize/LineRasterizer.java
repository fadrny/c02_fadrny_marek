package rasterize;

import model.Line;
import model.Point;

public abstract class LineRasterizer {
    Raster raster;

    public LineRasterizer(Raster raster){
        this.raster = raster;
    }

    public void updateRaster(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(Line line) {
        this.drawLine(line.getStart(), line.getEnd());
    }

    public void rasterizeIterable(Iterable<Line> lines) {
        for (Line line : lines) {
            this.rasterize(line);
        }
    }

    protected void drawLine(Point p1, Point p2) {
        // implemented by subclasses
    }
}

