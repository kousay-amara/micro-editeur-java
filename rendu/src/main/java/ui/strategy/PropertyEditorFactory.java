package ui.strategy;

import model.Shape;
import model.Rectangle;
import model.RegularPolygon;
import model.Group;

/**
 * Factory to create appropriate PropertyEditorStrategy for a shape.
 */
public class PropertyEditorFactory {

    public static PropertyEditorStrategy createEditor(Shape shape) {
        if (shape instanceof Rectangle) {
            return new RectanglePropertyEditor();
        } else if (shape instanceof RegularPolygon) {
            return new PolygonPropertyEditor();
        } else if (shape instanceof Group) {
            return new GroupPropertyEditor();
        } else {
            throw new IllegalArgumentException("Unknown shape type: " + shape.getClass().getName());
        }
    }
}
