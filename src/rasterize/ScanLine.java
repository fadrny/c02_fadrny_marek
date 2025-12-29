package rasterize;

import model.Point;
import model.Polygon;
import model.Edge;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.awt.Color;

public class ScanLine {

    private final Raster raster;

    public ScanLine(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(Polygon polygon, Color fillColor) {
        if (polygon.getSize() < 3)
            return;

        List<Edge> edges = new ArrayList<>();
        List<Point> points = polygon.getPoints();

        // Build Edge Table
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());

            // Ignore horizontal lines
            if (p1.getY() == p2.getY())
                continue;

            // p1 is always the upper point
            Edge edge;
            if (p1.getY() < p2.getY()) {
                edge = new Edge(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            } else {
                edge = new Edge(p2.getX(), p2.getY(), p1.getX(), p1.getY());
            }

            // Include yMin, exclude yMax.
            edge.setMaxY(edge.getMaxY() - 1);

            // Add only if valid edge
            if (edge.getMinY() <= edge.getMaxY()) {
                edges.add(edge);
            }
        }

        if (edges.isEmpty())
            return;

        // Sort Edge Table by yMin
        edges.sort(Comparator.comparingInt(Edge::getMinY));

        // Determine y range
        int yMin = edges.getFirst().getMinY();
        int yMax = 0;
        for (Edge e : edges) {
            if (e.getMaxY() > yMax) yMax = e.getMaxY();
        }

        List<Edge> activeEdges = new ArrayList<>();
        int edgeIndex = 0;

        // Process Scan-lines
        for (int y = yMin; y <= yMax; y++) {

            // Move edges to Active Edge Table if y == yMin
            while (edgeIndex < edges.size() && edges.get(edgeIndex).getMinY() == y) {
                activeEdges.add(edges.get(edgeIndex));
                edgeIndex++;
            }

            // Remove finished edges from the Active Edge Table (where y > maxY)
            int currentY = y;
            activeEdges.removeIf(edge -> edge.getMaxY() < currentY);

            // Sort Active Edge Table by current x coordinate
            activeEdges.sort(Comparator.comparingDouble(Edge::getX));

            // Fill pixels between the edges
            for (int i = 0; i < activeEdges.size(); i += 2) {
                if (i + 1 >= activeEdges.size()) break;

                Edge e1 = activeEdges.get(i);
                Edge e2 = activeEdges.get(i + 1);

                // Round x coordinates
                int xStart = (int) Math.round(e1.getX());
                int xEnd = (int) Math.round(e2.getX());

                // Fill span
                for (int x = xStart; x <= xEnd; x++) {
                    raster.setPixel(x, currentY, fillColor.getRGB());
                }
            }

            // Update x
            for (Edge edge : activeEdges) {
                edge.updateX();
            }
        }
    }
}
