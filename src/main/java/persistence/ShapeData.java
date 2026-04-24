package persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

final class ShapeData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private int x;
    private int y;
    private double rotation;
    private int rotationCenterX;
    private int rotationCenterY;
    private int colorRgb;
    private int width;
    private int height;
    private int cornerRadius;
    private int sides;
    private int sideLength;
    private final List<ShapeData> children = new ArrayList<>();

    String getType()           { return type; }
    int getX()                 { return x; }
    int getY()                 { return y; }
    double getRotation()       { return rotation; }
    int getRotationCenterX()   { return rotationCenterX; }
    int getRotationCenterY()   { return rotationCenterY; }
    int getColorRgb()          { return colorRgb; }
    int getWidth()             { return width; }
    int getHeight()            { return height; }
    int getCornerRadius()      { return cornerRadius; }
    int getSides()             { return sides; }
    int getSideLength()        { return sideLength; }
    List<ShapeData> getChildren() { return children; }

    void setType(String type)           { this.type = type; }
    void setX(int x)                    { this.x = x; }
    void setY(int y)                    { this.y = y; }
    void setRotation(double rotation)   { this.rotation = rotation; }
    void setRotationCenterX(int cx)     { this.rotationCenterX = cx; }
    void setRotationCenterY(int cy)     { this.rotationCenterY = cy; }
    void setColorRgb(int rgb)           { this.colorRgb = rgb; }
    void setWidth(int width)            { this.width = width; }
    void setHeight(int height)          { this.height = height; }
    void setCornerRadius(int r)         { this.cornerRadius = r; }
    void setSides(int sides)            { this.sides = sides; }
    void setSideLength(int len)         { this.sideLength = len; }
}
