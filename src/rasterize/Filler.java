package rasterize;

import model.Point;

import java.awt.*;

public interface Filler {

    void fill(Point startPoint, Color color);
}
