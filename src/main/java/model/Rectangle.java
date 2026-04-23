package model;

import java.awt.Color;

public class Rectangle extends ShapeLeaf {
    private int width, height;
    private int cornerRadius;
    
    public Rectangle(int centerX, int centerY, int width, int height, Color color) {
        super(centerX, centerY, color);
        this.width = width;
        this.height = height;
        this.cornerRadius = 0;
        setRotationCenter(centerX, centerY);
    }

    // Getters/Setters  (x, y) = centre du rectangle
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCornerRadius() { return cornerRadius; }
    public Color getColor() { return color; }
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public void setCornerRadius(int a) { this.cornerRadius = a; }
    public void setColor(Color c) { this.color = c; }
    
    @Override
    public boolean contains(int px, int py) {
        int left = x - width / 2;
        int top  = y - height / 2;
        if (rotation == 0) {
            return px >= left && px <= left + width
                && py >= top  && py <= top  + height;
        }

        double angle = Math.toRadians(rotation);
        double translatedX = px - rotationCenterX;
        double translatedY = py - rotationCenterY;
        double rotatedX = translatedX * Math.cos(angle) - translatedY * Math.sin(angle) + rotationCenterX;
        double rotatedY = translatedX * Math.sin(angle) + translatedY * Math.cos(angle) + rotationCenterY;

        return rotatedX >= left && rotatedX <= left + width
            && rotatedY >= top  && rotatedY <= top  + height;
    }
    
    @Override
    public String getType() {
        return "Rectangle";
    }
    
    @Override
    public Rectangle clone() {
        return (Rectangle) super.clone();
    }

    public void scaleFromCenter(double factor) {
        if (factor <= 0) {
            return;
        }
        width  = Math.max(1, (int) Math.round(width  * factor));
        height = Math.max(1, (int) Math.round(height * factor));
    }
}
