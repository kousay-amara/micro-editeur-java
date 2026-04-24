package model;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public abstract class ShapeLeaf implements Shape {
    protected int x, y;
    protected Color color;
    protected double rotation;
    protected int rotationCenterX;
    protected int rotationCenterY;
    protected boolean selected = false;
    
    public ShapeLeaf(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.rotation = 0;
        this.rotationCenterX = x;
        this.rotationCenterY = y;
    }
    
    @Override
    public int getX() { return x; }
    
    @Override
    public int getY() { return y; }

    @Override
    public double getRotation() { return rotation; }

    @Override
    public void setRotation(double rotation) { this.rotation = rotation; }

    public int getRotationCenterX() { return rotationCenterX; }

    public int getRotationCenterY() { return rotationCenterY; }

    public void setRotationCenterX(int rotationCenterX) { this.rotationCenterX = rotationCenterX; }

    public void setRotationCenterY(int rotationCenterY) { this.rotationCenterY = rotationCenterY; }

    public void setRotationCenter(int rotationCenterX, int rotationCenterY) {
        this.rotationCenterX = rotationCenterX;
        this.rotationCenterY = rotationCenterY;
    }
    
    @Override
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        rotationCenterX += dx;
        rotationCenterY += dy;
    }
    
    @Override
    public void add(Shape s) {
        throw new UnsupportedOperationException("Impossible d'ajouter à une feuille");
    }

    @Override
    public void remove(Shape s) {
        throw new UnsupportedOperationException("Impossible de retirer d'une feuille");
    }
    
    @Override
    public List<Shape> getChildren() {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isGroup() {
        return false;
    }
    
    @Override
    public ShapeLeaf clone() {
        try {
            return (ShapeLeaf) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public abstract boolean contains(int x, int y);
    
    @Override
    public abstract String getType();
}
