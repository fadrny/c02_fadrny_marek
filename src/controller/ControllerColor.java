package controller;

import java.awt.*;

/**
 * Class to manage selected color.
 */
public class ControllerColor {

    private int currentColorIndex = 0;

    private final Color[] availableColors = {
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.ORANGE,
            Color.PINK
    };

    public Color getCurrentColor() {
        return availableColors[currentColorIndex];
    }

    public void switchColor(boolean backwards) {
        currentColorIndex = backwards ? (currentColorIndex - 1 + availableColors.length) % availableColors.length : (currentColorIndex + 1) % availableColors.length;
    }

}
