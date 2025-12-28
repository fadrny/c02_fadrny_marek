package controller;

import helpers.Helpers;
import model.*;
import rasterize.BorderSeedFiller;
import rasterize.LineRasterizerDDA;
import rasterize.PolygonRasterizer;
import rasterize.FloodSeedFiller;
import view.Panel;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Controller2D {

    private final Panel panel;
    private final LineRasterizerDDA lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    private final ArrayList<Line> lines = new ArrayList<>();
    private Line currentLine;

    private final ArrayList<Polygon> polygons = new ArrayList<>();
    private Polygon currentPolygon = new Polygon();

    private boolean drawingRectangle = false;

    private Point middleClickPoint = null;
    private Pointable draggedPointable = null;
    private int draggedPointIndex = -1;

    private int fillMode = 0;

    private final ControllerColor controllerColor = new ControllerColor();

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.lineRasterizer = new LineRasterizerDDA(panel.getRaster());
        this.polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        setupListeners();
    }

    /**
     * Sets up the event listeners for user interactions.
     */
    private void setupListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                switch (e.getButton()) {
                    case 1: // Left button pressed

                        switch (fillMode) {
                            case 0: // standard line drawing mode
                                if (currentLine != null)
                                    lines.add(currentLine);
                                currentLine = new Line(
                                        new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()),
                                        new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()));
                                drawScene();
                                break;
                            case 1: // flood seed fill (fills while its finding same pixels colors as was the startPoint)
                                FloodSeedFiller floodSeedFiller = new FloodSeedFiller(panel.getRaster());
                                floodSeedFiller.fill(new Point(e.getX(), e.getY()), controllerColor.getCurrentColor());
                                panel.repaint();
                                break;
                            case 2: // border seed fill (checks for the same border color as the selected color)
                                BorderSeedFiller borderSeedFiller = new BorderSeedFiller(panel.getRaster(),
                                        controllerColor.getCurrentColor());
                                borderSeedFiller.fill(new Point(e.getX(), e.getY()), controllerColor.getCurrentColor());
                                panel.repaint();
                                break;
                        }
                        break;

                    case 2: // Middle button pressed
                        middleClickPoint = new Point(e.getX(), e.getY());

                        double minDistance = Double.MAX_VALUE;
                        ArrayList<Pointable> allPointables = new ArrayList<>(lines);
                        allPointables.addAll(polygons);
                        if (currentLine != null) allPointables.add(currentLine);
                        if (currentPolygon != null) allPointables.add(currentPolygon);

                        for (Pointable pointable : allPointables) { // Go through all pointables and find the closest point
                            ArrayList<Point> points = pointable.getPoints();
                            for (int i = 0; i < points.size(); i++) {
                                Point point = points.get(i);
                                double distance = Math.sqrt(Math.pow(point.getX() - e.getX(), 2) + Math.pow(point.getY() - e.getY(), 2)); // Calculates distance between point and mouse click using Pythagoras
                                if (distance < minDistance) {
                                    minDistance = distance;
                                    draggedPointable = pointable;
                                    draggedPointIndex = i;
                                }
                            }
                        }

                        drawScene();
                        break;

                    case 3: // Right button pressed

                        if(e.isShiftDown() && currentPolygon.getSize() > 0)
                            currentPolygon.pushPoint(Helpers.lockAngle(currentPolygon.getPoint(currentPolygon.getSize()-1), new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()), 45));
                        else
                            currentPolygon.pushPoint(new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()));

                        drawScene();
                        break;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 2) { // Middle button released - reset dragged point
                    draggedPointable = null;
                    draggedPointIndex = -1;
                    middleClickPoint = null;
                }
                else if (e.getButton() == 3 && drawingRectangle && currentPolygon.getSize() == 4){ // Right button released - finish drawing rectangle
                    drawingRectangle = false;
                    polygons.add(currentPolygon);
                    currentPolygon = new Polygon();
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) { // Left button dragged

                    if(e.isShiftDown())
                        currentLine.setEnd(Helpers.lockAngle(currentLine.getStart(), new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()), 45));
                    else
                        currentLine.setEnd(new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()));

                } else if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) { // Right button dragged

                    if(e.isShiftDown() && currentPolygon.getSize() > 1)
                        currentPolygon.updateLastPoint(Helpers.lockAngle(currentPolygon.getPoint(currentPolygon.getSize()-2), new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()), 45));
                    else
                        currentPolygon.updateLastPoint(new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()));

                } else if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) == MouseEvent.BUTTON2_DOWN_MASK) { // Middle button dragged
                    if (middleClickPoint != null && draggedPointable != null && draggedPointIndex != -1) {
                        Point closestPoint = draggedPointable.getPoint(draggedPointIndex);
                        int deltaX = e.getX() - middleClickPoint.getX();
                        int deltaY = e.getY() - middleClickPoint.getY();
                        Point newPoint = new Point(closestPoint.getX() + deltaX, closestPoint.getY() + deltaY, closestPoint.getColor());
                        draggedPointable.editPoint(draggedPointIndex, newPoint);
                        middleClickPoint = new Point(e.getX(), e.getY());
                    }
                }

                drawScene();
            }
        });

        panel.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) { // 'C' key pressed to clear
                    lines.clear();
                    currentLine = null;
                    currentPolygon = new Polygon();
                    polygons.clear();

                    drawScene();
                } else if (e.getKeyCode() == KeyEvent.VK_P) { // 'P' key pressed to start a new polygon
                    polygons.add(currentPolygon);
                    currentPolygon = new Polygon();
                    drawScene();
                } else if (e.getKeyCode() == KeyEvent.VK_R) { // 'R' key pressed to toggle Rectangle drawing
                    drawingRectangle = !drawingRectangle;
                    polygons.add(currentPolygon);
                    currentPolygon = new Rectangle();
                    drawScene();
                } else if (e.getKeyCode() == KeyEvent.VK_F) { // 'F' key pressed to toggle Fill mode
                    fillMode = (fillMode + 1) % 3;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) { // Space key pressed to change color

                    controllerColor.switchColor(e.isShiftDown()); // switch backwards if shift is down

                    // if left button down, update the current line end point color
                    if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK && currentLine != null)
                        currentLine.setEnd(new Point(currentLine.getEnd().getX(), currentLine.getEnd().getY(), controllerColor.getCurrentColor()));

                    // if right button down, update color of last point in current polygon
                    if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK && currentPolygon.getSize() > 0)
                        currentPolygon.updateLastPointsColor(controllerColor.getCurrentColor());

                    drawScene();
                }
            }
        });

        // handle window resize
        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                panel.resize();
                lineRasterizer.updateRaster(panel.getRaster());
                drawScene();
            }
        });
    }

    private void drawScene() {
        panel.clear();

        if (!lines.isEmpty())
            lineRasterizer.rasterizeIterable(lines);
        if (currentLine != null)
            lineRasterizer.rasterize(currentLine);
        if (!polygons.isEmpty())
            polygonRasterizer.rasterizeIterable(polygons);
        if (currentPolygon != null)
            polygonRasterizer.rasterize(currentPolygon);


        panel.repaint();
    }
}