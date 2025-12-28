package rasterize;

import model.Point;

import java.awt.*;
import java.util.Stack;

public class FloodSeedFiller implements Filler {

    private Raster raster;

    public FloodSeedFiller(Raster raster) {
        this.raster = raster;
    }

    public void setRaster(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill(Point startPoint, Color color) {
        seedFill(startPoint, color);
    }

    private void seedFill(Point startPoint, Color color) {

        Stack<Point> seedStack = new Stack<>();

        // Store the background color - the color of the starting pixel
        int backgroundColor = raster.getPixel(startPoint.getX(), startPoint.getY());

        // If starting pixel is already the fill color, nothing to do
        if (backgroundColor == color.getRGB())
            return;

        seedStack.push(startPoint);

        while (!seedStack.isEmpty()) {

            Point cp = seedStack.pop();
            int x = cp.getX();
            int y = cp.getY();

            // Check bounds
            if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight())
                continue;

            // Only fill pixels that have the background color
            if (raster.getPixel(x, y) != backgroundColor)
                continue;

            // Color the current pixel
            raster.setPixel(x, y, color.getRGB());

            // Add neighbors to stack
            seedStack.push(new Point(x + 1, y));
            seedStack.push(new Point(x - 1, y));
            seedStack.push(new Point(x, y + 1));
            seedStack.push(new Point(x, y - 1));
        }
    }
}
