package command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import model.Group;
import model.Rectangle;
import model.RegularPolygon;
import model.Shape;

public class ShapeMemento {
    private Shape shape;
    private int x;
    private int y;
    private Color color;
    private double rotation;
    private int width;
    private int height;
    private int cornerRadius;
    private int sides;
    private int sideLength;
    private int rotationCenterX;
    private int rotationCenterY;
    private int groupCenterX;
    private int groupCenterY;
    private List<ShapeMemento> childrenMementos;

    public ShapeMemento(Shape shape) {
        this.shape = shape;
        this.x = shape.getX();
        this.y = shape.getY();
        this.rotation = shape.getRotation();
        this.childrenMementos = new ArrayList<>();

        if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            this.width = rect.getWidth();
            this.height = rect.getHeight();
            this.cornerRadius = rect.getCornerRadius();
            this.rotationCenterX = rect.getRotationCenterX();
            this.rotationCenterY = rect.getRotationCenterY();
            this.color = rect.getColor();
        } else if (shape instanceof RegularPolygon) {
            RegularPolygon poly = (RegularPolygon) shape;
            this.sides = poly.getSides();
            this.sideLength = poly.getSideLength();
            this.rotationCenterX = poly.getRotationCenterX();
            this.rotationCenterY = poly.getRotationCenterY();
            this.color = poly.getColor();
        } else if (shape instanceof Group) {
            Group group = (Group) shape;
            this.groupCenterX = group.getGroupCenterX();
            this.groupCenterY = group.getGroupCenterY();
            for (Shape child : group.getChildren()) {
                childrenMementos.add(new ShapeMemento(child));
            }
        }
    }

    public void restore() {
        if (shape instanceof Group) {
            Group group = (Group) shape;
            List<Shape> children = group.getChildren();
            for (int i = 0; i < children.size() && i < childrenMementos.size(); i++) {
                childrenMementos.get(i).restore();
            }
            int currentCenterX = group.getGroupCenterX();
            int currentCenterY = group.getGroupCenterY();
            group.move(groupCenterX - currentCenterX, groupCenterY - currentCenterY);
            group.setRotation(rotation);
        } else {
            shape.move(x - shape.getX(), y - shape.getY());
            shape.setRotation(rotation);

            if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                rect.setWidth(width);
                rect.setHeight(height);
                rect.setCornerRadius(cornerRadius);
                rect.setRotationCenter(rotationCenterX, rotationCenterY);
                rect.setColor(color);
            } else if (shape instanceof RegularPolygon) {
                RegularPolygon poly = (RegularPolygon) shape;
                poly.setSides(sides);
                poly.setSideLength(sideLength);
                poly.setRotationCenter(rotationCenterX, rotationCenterY);
                poly.setColor(color);
            }
        }
    }

    public Shape getShape() {
        return shape;
    }
}
