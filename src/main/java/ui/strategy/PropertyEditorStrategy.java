package ui.strategy;

import model.Shape;
import java.awt.Panel;

/**
 * Strategy pattern: Each shape type has its own property editor.
 * Eliminates duplication in PropertyDialog.
 */
public interface PropertyEditorStrategy {

    /**
     * Setup all fields for this shape type.
     */
    void setupFields(Shape shape, Panel panel);

    /**
     * Apply changes from fields back to shape.
     */
    void applyChanges(Shape shape) throws NumberFormatException;
}
