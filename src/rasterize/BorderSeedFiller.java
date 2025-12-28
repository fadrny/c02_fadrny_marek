package rasterize;

import model.Point;

import java.awt.*;
import java.util.Stack;

public class BorderSeedFiller implements Filler {

    private Raster raster;
    private Color borderColor;

    public BorderSeedFiller(Raster raster, Color borderColor) {
        this.raster = raster;
        this.borderColor = borderColor;
    }

    public void setRaster(Raster raster) {
        this.raster = raster;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    @Override
    public void fill(Point startPoint, Color fillColor) {
        borderSeedFill(startPoint, fillColor);
    }

    private void borderSeedFill(Point startPoint, Color fillColor) {

        Stack<Point> seedStack = new Stack<>();

        int fillColorRGB = fillColor.getRGB();
        int borderColorRGB = borderColor.getRGB();

        int startX = startPoint.getX();
        int startY = startPoint.getY();

        if (startX < 0 || startX >= raster.getWidth() || startY < 0 || startY >= raster.getHeight())
            return;

        int startPixelColor = raster.getPixel(startX, startY);

        // If starting pixel is already the fill color or the border color, nothing to do
        if (startPixelColor == fillColorRGB || startPixelColor == borderColorRGB)
            return;

        seedStack.push(startPoint);

        while (!seedStack.isEmpty()) {

            Point cp = seedStack.pop();
            int x = cp.getX();
            int y = cp.getY();

            // Check bounds
            if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight())
                continue;

            int currentPixelColor = raster.getPixel(x, y);

            // Stop if we hit the border color or already filled pixel
            if (currentPixelColor == borderColorRGB || currentPixelColor == fillColorRGB)
                continue;

            // Color the current pixel
            raster.setPixel(x, y, fillColorRGB);

            // Add neighbors to stack
            seedStack.push(new Point(x + 1, y));
            seedStack.push(new Point(x - 1, y));
            seedStack.push(new Point(x, y + 1));
            seedStack.push(new Point(x, y - 1));
        }
    }
}
