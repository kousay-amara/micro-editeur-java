package model;

import java.util.ArrayList;
import java.util.List;
import util.GeometryUtils;

public class Group implements Shape {
    private List<Shape> children = new ArrayList<>();
    private Group parent;
    private boolean selected = false;
    private double rotation = 0;
    
    private static final class Bounds {
        private final int minX;
        private final int minY;
        private final int maxX;
        private final int maxY;

        private Bounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    @Override
    public void add(Shape s) {
        children.add(s);
        if (s instanceof Group) {
            ((Group) s).setParent(this);
        }
    }

    public void addAt(int index, Shape s) {
        if (index >= 0 && index <= children.size()) {
            children.add(index, s);
            if (s instanceof Group) {
                ((Group) s).setParent(this);
            }
        } else {
            add(s);
        }
    }

    @Override
    public void remove(Shape s) {
        children.remove(s);
        if (s instanceof Group) {
            ((Group) s).setParent(null);
        }
    }
    
    @Override
    public List<Shape> getChildren() {
        return new ArrayList<>(children);
    }
    
    @Override
    public boolean isGroup() {
        return true;
    }
    
    @Override
    public void move(int dx, int dy) {
        for (Shape s : children) {
            s.move(dx, dy);
        }
    }
    
    @Override
    public boolean contains(int x, int y) {
        for (Shape child : children) {
            if (child.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        for (Shape child : children) {
            child.setSelected(selected);
        }
    }
    
    @Override
    public boolean isSelected() {
        return selected;
    }
    
    @Override
    public int getX() {
        return getGroupCenterX();
    }
    
    @Override
    public int getY() {
        return getGroupCenterY();
    }

    @Override
    public double getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(double rot) {
        this.rotation = rot;
    }

    @Override
    public String getType() {
        return "Group";
    }
    
    @Override
    public Shape clone() {
        Group cloned = new Group();
        cloned.rotation = rotation;
        for (Shape s : children) {
            cloned.add(s.clone());
        }
        return cloned;
    }

    public Group getParent() {
        return parent;
    }

    public void setParent(Group parent) {
        this.parent = parent;
    }

    public int getGroupCenterX() {
        Bounds bounds = getBounds();
        return (bounds.minX + bounds.maxX) / 2;
    }

    public int getGroupCenterY() {
        Bounds bounds = getBounds();
        return (bounds.minY + bounds.maxY) / 2;
    }

    public void scaleFromCenter(double factor) {
        if (factor <= 0 || children.isEmpty()) {
            return;
        }

        int centerX = getGroupCenterX();
        int centerY = getGroupCenterY();

        for (Shape child : children) {
            scaleChildFromCenter(child, centerX, centerY, factor);
        }
    }

    public void rotateAroundCenter(double angle) {
        if (angle == 0 || children.isEmpty()) {
            return;
        }

        int centerX = getGroupCenterX();
        int centerY = getGroupCenterY();
        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        for (Shape child : children) {
            int dx = child.getX() - centerX;
            int dy = child.getY() - centerY;
            int newDx = (int) (dx * cos + dy * sin);
            int newDy = (int) (-dx * sin + dy * cos);
            child.move(newDx - dx, newDy - dy);
            shiftRotationRecursively(child, angle);
        }
        rotation += angle;
    }

    private void shiftRotationRecursively(Shape shape, double angle) {
        if (shape instanceof Group) {
            Group group = (Group) shape;
            group.rotation += angle;
            for (Shape child : group.children) {
                shiftRotationRecursively(child, angle);
            }
            return;
        }

        shape.setRotation(shape.getRotation() + angle);
    }

    private void scaleChildFromCenter(Shape child, int centerX, int centerY, double factor) {
        if (child instanceof Group) {
            Group childGroup = (Group) child;
            int oldCenterX = childGroup.getGroupCenterX();
            int oldCenterY = childGroup.getGroupCenterY();
            childGroup.scaleFromCenter(factor);
            int newCenterX = GeometryUtils.scalePoint(oldCenterX, centerX, factor);
            int newCenterY = GeometryUtils.scalePoint(oldCenterY, centerY, factor);
            childGroup.move(newCenterX - childGroup.getGroupCenterX(), newCenterY - childGroup.getGroupCenterY());
            return;
        }

        if (child instanceof Rectangle) {
            scaleRectangleFromCenter((Rectangle) child, centerX, centerY, factor);
            return;
        }

        if (child instanceof RegularPolygon) {
            scalePolygonFromCenter((RegularPolygon) child, centerX, centerY, factor);
            return;
        }

        int newX = GeometryUtils.scalePoint(child.getX(), centerX, factor);
        int newY = GeometryUtils.scalePoint(child.getY(), centerY, factor);
        child.move(newX - child.getX(), newY - child.getY());
    }

    private void scaleRectangleFromCenter(Rectangle rectangle, int centerX, int centerY, double factor) {
        int oldPivotX = rectangle.getRotationCenterX();
        int oldPivotY = rectangle.getRotationCenterY();

        int newWidth = Math.max(1, (int) Math.round(rectangle.getWidth() * factor));
        int newHeight = Math.max(1, (int) Math.round(rectangle.getHeight() * factor));
        int newCenterX = GeometryUtils.scalePoint(rectangle.getX(), centerX, factor);
        int newCenterY = GeometryUtils.scalePoint(rectangle.getY(), centerY, factor);

        rectangle.setWidth(newWidth);
        rectangle.setHeight(newHeight);
        rectangle.move(newCenterX - rectangle.getX(), newCenterY - rectangle.getY());
        rectangle.setRotationCenter(
                GeometryUtils.scalePoint(oldPivotX, centerX, factor),
                GeometryUtils.scalePoint(oldPivotY, centerY, factor));
    }

    private void scalePolygonFromCenter(RegularPolygon polygon, int centerX, int centerY, double factor) {
        int oldCenterX = polygon.getX();
        int oldCenterY = polygon.getY();
        int oldPivotX = polygon.getRotationCenterX();
        int oldPivotY = polygon.getRotationCenterY();

        int newCenterX = GeometryUtils.scalePoint(oldCenterX, centerX, factor);
        int newCenterY = GeometryUtils.scalePoint(oldCenterY, centerY, factor);
        int newSideLength = Math.max(1, (int) Math.round(polygon.getSideLength() * factor));

        polygon.setSideLength(newSideLength);
        polygon.move(newCenterX - polygon.getX(), newCenterY - polygon.getY());
        polygon.setRotationCenter(
                GeometryUtils.scalePoint(oldPivotX, centerX, factor),
                GeometryUtils.scalePoint(oldPivotY, centerY, factor));
    }


    private Bounds getBounds() {
        if (children.isEmpty()) {
            return new Bounds(0, 0, 0, 0);
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Shape child : children) {
            Bounds childBounds = getBounds(child);
            minX = Math.min(minX, childBounds.minX);
            minY = Math.min(minY, childBounds.minY);
            maxX = Math.max(maxX, childBounds.maxX);
            maxY = Math.max(maxY, childBounds.maxY);
        }

        return new Bounds(minX, minY, maxX, maxY);
    }

    private Bounds getBounds(Shape shape) {
        if (shape instanceof Group) {
            return ((Group) shape).getBounds();
        }

        if (shape instanceof Rectangle) {
            return getRectangleBounds((Rectangle) shape);
        }

        if (shape instanceof RegularPolygon) {
            return getPolygonBounds((RegularPolygon) shape);
        }

        return new Bounds(shape.getX(), shape.getY(), shape.getX(), shape.getY());
    }

    private Bounds getRectangleBounds(Rectangle rectangle) {
        int x = rectangle.getX() - rectangle.getWidth() / 2;
        int y = rectangle.getY() - rectangle.getHeight() / 2;
        int width = rectangle.getWidth();
        int height = rectangle.getHeight();
        double rotation = Math.toRadians(-rectangle.getRotation());
        int pivotX = rectangle.getRotationCenterX();
        int pivotY = rectangle.getRotationCenterY();

        if (rotation == 0) {
            return new Bounds(x, y, x + width, y + height);
        }
        double[][] corners = {
                {x, y},
                {x + width, y},
                {x + width, y + height},
                {x, y + height}
        };

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (double[] corner : corners) {
            double translatedX = corner[0] - pivotX;
            double translatedY = corner[1] - pivotY;
            int rotatedX = (int) Math.round(translatedX * Math.cos(rotation) - translatedY * Math.sin(rotation) + pivotX);
            int rotatedY = (int) Math.round(translatedX * Math.sin(rotation) + translatedY * Math.cos(rotation) + pivotY);
            minX = Math.min(minX, rotatedX);
            minY = Math.min(minY, rotatedY);
            maxX = Math.max(maxX, rotatedX);
            maxY = Math.max(maxY, rotatedY);
        }

        return new Bounds(minX, minY, maxX, maxY);
    }

    private Bounds getPolygonBounds(RegularPolygon polygon) {
        int radius = GeometryUtils.computeRadius(polygon.getSides(), polygon.getSideLength());
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int i = 0; i < polygon.getSides(); i++) {
            double baseAngle = Math.toRadians(90) + 2 * Math.PI * i / polygon.getSides();
            double baseX = polygon.getX() + radius * Math.cos(baseAngle);
            double baseY = polygon.getY() + radius * Math.sin(baseAngle);
            int pointX = GeometryUtils.rotatePointX(baseX, baseY, polygon.getRotationCenterX(), polygon.getRotationCenterY(), polygon.getRotation());
            int pointY = GeometryUtils.rotatePointY(baseX, baseY, polygon.getRotationCenterX(), polygon.getRotationCenterY(), polygon.getRotation());
            minX = Math.min(minX, pointX);
            minY = Math.min(minY, pointY);
            maxX = Math.max(maxX, pointX);
            maxY = Math.max(maxY, pointY);
        }

        return new Bounds(minX, minY, maxX, maxY);
    }



}
