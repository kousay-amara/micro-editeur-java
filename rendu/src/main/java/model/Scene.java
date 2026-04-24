package model;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private List<Shape> shapes;
    private List<SceneListener> listeners;
    private List<Shape> selectedShapes = new ArrayList<>();
    
    public Scene() {
        this.shapes = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        notifyListeners();
    }
    
    public Shape getShapeAt(int x, int y) {
        List<Shape> shapes = getShapes();
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.contains(x, y)) {
                return shape;
            }
        }
        return null;
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
        notifyListeners();
    }
    
    public void addShapeAt(int index, Shape shape) {
        shapes.add(index, shape);
        notifyListeners();
    }

    public void replaceShapes(List<Shape> newShapes) {
        shapes = new ArrayList<>(newShapes);
        selectedShapes.clear();
        for (Shape shape : shapes) {
            clearSelectionRecursively(shape);
        }
        notifyListeners();
    }
    
    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }
    
    public void addListener(SceneListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(SceneListener listener) {
        listeners.remove(listener);
    }
    
    public void addSelectedShape(Shape shape) {
        if (!selectedShapes.contains(shape)) {
            selectedShapes.add(shape);
            notifyListeners();
        }
    }

    public void removeSelectedShape(Shape shape) {
        selectedShapes.remove(shape);
        notifyListeners();
    }

    public void clearSelectedShapes() {
        selectedShapes.clear();
        notifyListeners();
    }

    public List<Shape> getSelectedShapes() {
        return new ArrayList<>(selectedShapes);
    }

    public boolean isShapeSelected(Shape shape) {
        return selectedShapes.contains(shape);
    }
    
    public void notifyListeners() {
        for (SceneListener listener : listeners) {
            listener.onShapeChanged();
        }
    }

    private void clearSelectionRecursively(Shape shape) {
        shape.setSelected(false);
        if (!shape.isGroup()) {
            return;
        }

        for (Shape child : shape.getChildren()) {
            clearSelectionRecursively(child);
        }
    }
}
