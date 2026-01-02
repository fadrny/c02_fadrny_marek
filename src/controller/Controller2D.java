package controller;

import helpers.Helpers;
import model.*;
import rasterize.*;
import view.Panel;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Controller2D {

    private final Panel panel;
    private final LineRasterizerDDA lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;
    private final ScanLine scanLineRasterizer;
    private final PolygonClipper polygonClipper;

    private final ArrayList<Line> lines = new ArrayList<>();
    private Line currentLine;

    private final ArrayList<Polygon> polygons = new ArrayList<>();
    private Polygon currentPolygon = new Polygon();

    private boolean drawingRectangle = false;

    private Point middleClickPoint = null;
    private Pointable draggedPointable = null;
    private int draggedPointIndex = -1;

    private int fillMode = 0;

    // Clipping
    private boolean clippingMode = false;
    private Polygon subjectPolygon = new Polygon();
    private Polygon clipPolygon;
    private int clippingEditTarget = 0;

    private final ControllerColor controllerColor = new ControllerColor();

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.lineRasterizer = new LineRasterizerDDA(panel.getRaster());
        this.polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        this.scanLineRasterizer = new ScanLine(panel.getRaster());
        this.polygonClipper = new PolygonClipper();
        setupListeners();
        
        // Initialize default clipper
        initDefaultClipper();
        updateStatus();
    }

    private void initDefaultClipper() {
        clipPolygon = new Polygon();
        int centerX = 400;
        int centerY = 300;
        int radius = 150;
        for (int i = 0; i < 8; i++) { // drawing symmetrical octagon
            double angle = Math.toRadians(i * 45);
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            clipPolygon.pushPoint(new Point(x, y, Color.RED));
        }
    }

    private void updateStatus() {
        String modeString = "";
        if (clippingMode)
            panel.setStatus("Clipping Mode (Target: " + (clippingEditTarget == 0 ? "Subject" : "Clipper") + ") - [MMB] Edit Point | [RMB] Add Point | [1/2] Target | [K] Exit Clipping mode | [C] Clear Target", null, "");
        else {
            switch (fillMode) {
                case 0: modeString = "Lines"; break;
                case 1: modeString = "Seed Fill (background)"; break;
                case 2: modeString = "Seed Fill (border)"; break;
                case 3: modeString = "Scan Line"; break;
            }
            panel.setStatus((fillMode == 3 ? "" : "[LMB] ") + "Mode: " + modeString + " [F] | Color: ", controllerColor.getCurrentColor(), " [Space] | Polygon [RMB] | Rectangle [R] | New Polygon [P] | Edit [MMB] | Clear [C] | [K] Clipping mode");
        }
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
                        if (clippingMode) // Disabled in clipping mode
                            return;

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
                                BorderSeedFiller borderSeedFiller = new BorderSeedFiller(panel.getRaster(),controllerColor.getCurrentColor());
                                borderSeedFiller.fill(new Point(e.getX(), e.getY()), controllerColor.getCurrentColor());
                                panel.repaint();
                                break;
                            case 3: // scan line fill
                                // Scan line happens in drawScene for existing polygons
                                break;
                        }
                        break;

                    case 2: // Middle button pressed
                        middleClickPoint = new Point(e.getX(), e.getY());

                        double minDistance = Double.MAX_VALUE;
                        ArrayList<Pointable> allPointables = new ArrayList<>();
                        
                        if (clippingMode) {
                            if (subjectPolygon != null)
                                allPointables.add(subjectPolygon);
                            if (clipPolygon != null)
                                allPointables.add(clipPolygon);
                        } else {
                            allPointables.addAll(lines);
                            allPointables.addAll(polygons);
                            if (currentLine != null) allPointables.add(currentLine);
                            if (currentPolygon != null) allPointables.add(currentPolygon);
                        }

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

                        if (clippingMode) {
                            Polygon target = (clippingEditTarget == 1) ? clipPolygon : subjectPolygon;
                            Color c = (clippingEditTarget == 1) ? Color.RED : Color.GREEN;
                            
                            if(e.isShiftDown() && target.getSize() > 0)
                                target.pushPoint(Helpers.lockAngle(target.getPoint(target.getSize()-1), new Point(e.getX(), e.getY(), c), 45));
                            else
                                target.pushPoint(new Point(e.getX(), e.getY(), c));
                        } else {
                            if(e.isShiftDown() && currentPolygon.getSize() > 0)
                                currentPolygon.pushPoint(Helpers.lockAngle(currentPolygon.getPoint(currentPolygon.getSize()-1), new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()), 45));
                            else
                                currentPolygon.pushPoint(new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()));
                        }

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
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK && fillMode == 0) { // Left button dragged

                    if(e.isShiftDown())
                        currentLine.setEnd(Helpers.lockAngle(currentLine.getStart(), new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()), 45));
                    else
                        currentLine.setEnd(new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()));

                } else if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) { // Right button dragged
                     if (!clippingMode) {
                        if(e.isShiftDown() && currentPolygon.getSize() > 1)
                            currentPolygon.updateLastPoint(Helpers.lockAngle(currentPolygon.getPoint(currentPolygon.getSize()-2), new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()), 45));
                        else
                            currentPolygon.updateLastPoint(new Point(e.getX(), e.getY(), controllerColor.getCurrentColor()));
                     }

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
                if (e.getKeyCode() == KeyEvent.VK_K) { // Toggle Clipping Mode
                    clippingMode = !clippingMode;
                    if (clippingMode) {
                        // Ensure defaults
                        if (clipPolygon == null || clipPolygon.getSize() < 3) initDefaultClipper();
                        if (subjectPolygon == null) subjectPolygon = new Polygon();
                    }
                    updateStatus();
                    drawScene();
                } else if (clippingMode) {
                    if (e.getKeyCode() == KeyEvent.VK_1) {
                        clippingEditTarget = 0; // Subject
                        updateStatus();
                    } else if (e.getKeyCode() == KeyEvent.VK_2) {
                        clippingEditTarget = 1; // Clipper
                        updateStatus();
                    } else if (e.getKeyCode() == KeyEvent.VK_C) {
                        if (clippingEditTarget == 0) subjectPolygon = new Polygon();
                        else initDefaultClipper();
                        drawScene();
                    }
                } else {
                    // Standard Mode Keys
                    if (e.getKeyCode() == KeyEvent.VK_C) {
                        lines.clear();
                        currentLine = null;
                        currentPolygon = new Polygon();
                        polygons.clear();
                        drawScene();
                    } else if (e.getKeyCode() == KeyEvent.VK_P) {
                        polygons.add(currentPolygon);
                        currentPolygon = new Polygon();
                        drawScene();
                    } else if (e.getKeyCode() == KeyEvent.VK_R) {
                        drawingRectangle = !drawingRectangle;
                        polygons.add(currentPolygon);
                        currentPolygon = new Rectangle();
                        drawScene();
                    } else if (e.getKeyCode() == KeyEvent.VK_F) {
                        fillMode = (fillMode + 1) % 4; // Cycle through 0, 1, 2, 3
                        updateStatus();
                        drawScene();
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_SPACE) { 
                    controllerColor.switchColor(e.isShiftDown()); 
                    updateStatus();
                    
                    if (!clippingMode) {
                        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK && currentLine != null)
                            currentLine.setEnd(new Point(currentLine.getEnd().getX(), currentLine.getEnd().getY(), controllerColor.getCurrentColor()));
                        if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK && currentPolygon.getSize() > 0)
                            currentPolygon.updateLastPointsColor(controllerColor.getCurrentColor());
                    }
                    drawScene();
                }
            }
        });

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

        if (clippingMode) {
            // Draw Subject
            if (subjectPolygon != null) {
                polygonRasterizer.rasterize(subjectPolygon);
            }
            // Draw Clipper
            if (clipPolygon != null) {
                polygonRasterizer.rasterize(clipPolygon);
            }

            // Clipping and Fill
            if (subjectPolygon != null && clipPolygon != null && subjectPolygon.getSize() > 2 && clipPolygon.getSize() > 2) {
                Polygon result = polygonClipper.clip(subjectPolygon, clipPolygon);
                
                scanLineRasterizer.rasterize(result, Color.BLUE);

                // Outline Result
                for (int i = 0; i < result.getSize(); i++) {
                    Point p1 = result.getPoint(i);
                    Point p2 = result.getPoint((i + 1) % result.getSize());
                    // temp points for outline
                    Point yp1 = new Point(p1.getX(), p1.getY(), Color.YELLOW);
                    Point yp2 = new Point(p2.getX(), p2.getY(), Color.YELLOW);
                    lineRasterizer.rasterize(new Line(yp1, yp2));
                }
            }

        } else {
            // Fill if mode is 3
            if (fillMode == 3) {
                 for (Polygon poly : polygons) {
                     scanLineRasterizer.rasterize(poly, controllerColor.getCurrentColor());
                 }
                 if (currentPolygon != null && currentPolygon.getSize() > 2) {
                     scanLineRasterizer.rasterize(currentPolygon, controllerColor.getCurrentColor());
                 }
            }

            if (!lines.isEmpty())
                lineRasterizer.rasterizeIterable(lines);
            if (currentLine != null)
                lineRasterizer.rasterize(currentLine);
            if (!polygons.isEmpty())
                polygonRasterizer.rasterizeIterable(polygons);
            if (currentPolygon != null)
                polygonRasterizer.rasterize(currentPolygon);
        }

        panel.repaint();
    }
}
