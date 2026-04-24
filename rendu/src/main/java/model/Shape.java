package model;
import java.util.List;

public interface Shape extends Cloneable {
    // Géométrie
    void move(int dx, int dy);
    boolean contains(int x, int y);
    
    // Propriétés
    int getX();
    int getY();
    double getRotation();
    void setRotation(double rotation);
    String getType();
    void setSelected(boolean selected);
    boolean isSelected();
    
    // Prototype
    Shape clone();
    
    // Composite (groupe)
    void add(Shape s);
    void remove(Shape s);
    List<Shape> getChildren();
    boolean isGroup();
}
