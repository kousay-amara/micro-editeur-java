package model;

import java.awt.Color;
import util.GeometryUtils;

public class RegularPolygon extends ShapeLeaf {
    private int sides;
    private int sideLength;
    
    public RegularPolygon(int x, int y, int sides, int sideLength, Color color) {
        super(x, y, color);
        this.sides = sides;
        this.sideLength = sideLength;
        setRotationCenter(x, y);
    }
    
    // Getters/Setters
    public int getSides() { return sides; }
    public int getSideLength() { return sideLength; }
    public Color getColor() { return color; }
    public void setSides(int s) { this.sides = s; }
    public void setSideLength(int sl) { this.sideLength = sl; }
    public void setColor(Color c) { this.color = c; }
    
    private int[] getXPoints() {
        return getXPointsForCenterAndPivot(x, rotationCenterX, rotationCenterY);
    }

    private int[] getYPoints() {
        return getYPointsForCenterAndPivot(y, rotationCenterX, rotationCenterY);
    }

    public int[] getXPointsForCenterAndPivot(int centerX, int pivotX, int pivotY) {
        int r = GeometryUtils.computeRadius(sides, sideLength);
        int[] xs = new int[sides];
        for (int i = 0; i < sides; i++) {
            double baseAngle = Math.toRadians(90) + 2 * Math.PI * i / sides;
            double baseX = centerX + r * Math.cos(baseAngle);
            double baseY = y + r * Math.sin(baseAngle);
            xs[i] = GeometryUtils.rotatePointX(baseX, baseY, pivotX, pivotY, rotation);
        }
        return xs;
    }
    
    public int[] getYPointsForCenterAndPivot(int centerY, int pivotX, int pivotY) {
        int r = GeometryUtils.computeRadius(sides, sideLength);
        int[] ys = new int[sides];
        for (int i = 0; i < sides; i++) {
            double baseAngle = Math.toRadians(90) + 2 * Math.PI * i / sides;
            double baseX = x + r * Math.cos(baseAngle);
            double baseY = centerY + r * Math.sin(baseAngle);
            ys[i] = GeometryUtils.rotatePointY(baseX, baseY, pivotX, pivotY, rotation);
        }
        return ys;
    }
    
    @Override
    public boolean contains(int px, int py) {
        int[] xs = getXPoints();
        int[] ys = getYPoints();

        // Ray casting algorithm: count intersections of ray from point to infinity
        // If odd number of intersections, point is inside polygon
        int count = 0;
        for (int i = 0; i < sides; i++) {
            int x1 = xs[i];
            int y1 = ys[i];
            int x2 = xs[(i + 1) % sides];
            int y2 = ys[(i + 1) % sides];

            // Check if ray from (px, py) to right crosses edge (x1,y1)-(x2,y2)
            if ((y1 <= py && py < y2) || (y2 <= py && py < y1)) {
                // Compute x-coordinate of intersection
                double xIntersect = x1 + (double)(py - y1) / (y2 - y1) * (x2 - x1);
                if (px < xIntersect) {
                    count++;
                }
            }
        }

        return count % 2 == 1;
    }
    
    @Override
    public String getType() {
        return "Polygon";
    }
    
    @Override
    public RegularPolygon clone() {
        return (RegularPolygon) super.clone();
    }

    public void scaleFromCenter(double factor) {
        if (factor <= 0) {
            return;
        }

        int newSideLength = Math.max(1, (int) Math.round(sideLength * factor));
        sideLength = newSideLength;
    }
}
