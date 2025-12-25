package rasterize;

import model.Point;

public class LineRasterizerDDA extends LineRasterizer {

    public LineRasterizerDDA(Raster raster) {
        super(raster);
    }

    /**
      * <p>Draws a line using the DDA (Digital Differential Analyzer) algorithm and interpolates colors between the start and end points.</p>
      *
      * <p>DDA is a simple incremental line rasterization algorithm that uses floating-point arithmetic to calculate pixel positions.</p>
      * </br>
      * <strong>Advantages:</strong>
      * <ul>
      *     <li>Simple implementation</li>
      *     <li>Works well for lines of all slopes</li>
      * </ul>
      *
      * <strong>Disadvantages:</strong>
      * <ul>
      *     <li>Uses floating-point arithmetic (slower than integer)</li>
      * </ul>
      * </br>
      * @param p1       start point
      * @param p2       end point
      */

    @Override
    protected void drawLine(Point p1, Point p2) {
        int dx = p2.getX() - p1.getX();
        int dy = p2.getY() - p1.getY();

        float x = p1.getX();
        float y = p1.getY();

        int steps;
        float xIncrement, yIncrement;

        steps = Math.max(Math.abs(dx), Math.abs(dy));

        xIncrement = dx / (float) steps;
        yIncrement = dy / (float) steps;

        for (int i = 0; i <= steps; i++) {
            // Calculate the interpolated color
            float t = (steps == 0) ? (float) 0 : (float) i / (float) steps;
            int r = (int)(p1.getColor().getRed() * (1 - t) + p2.getColor().getRed() * t);
            int g = (int)(p1.getColor().getGreen() * (1 - t) + p2.getColor().getGreen() * t);
            int b = (int)(p1.getColor().getBlue() * (1 - t) + p2.getColor().getBlue() * t);
            int color = (r << 16) | (g << 8) | b;

            raster.setPixel(Math.round(x), Math.round(y), color);
            x += xIncrement;
            y += yIncrement;
        }
    }

}

