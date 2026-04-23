package ui.strategy;

import model.Group;
import model.Shape;
import ui.UIConstants;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

/**
 * Property editor for Group shapes.
 */
public class GroupPropertyEditor implements PropertyEditorStrategy {

    private TextField xField;
    private TextField yField;
    private TextField rotationField;
    private TextField scaleField;

    @Override
    public void setupFields(Shape shape, Panel panel) {
        Group group = (Group) shape;

        xField = PropertyEditorSupport.addField(panel, "Center X:", group.getGroupCenterX());
        yField = PropertyEditorSupport.addField(panel, "Center Y:", group.getGroupCenterY());
        rotationField = PropertyEditorSupport.addField(panel, "Rotation:", (int) group.getRotation());
        scaleField = PropertyEditorSupport.addField(panel, "Scale %:", UIConstants.DEFAULT_SCALE_PERCENT);
    }

    @Override
    public void applyChanges(Shape shape) throws NumberFormatException {
        Group group = (Group) shape;

        int newCenterX = Integer.parseInt(xField.getText());
        int newCenterY = Integer.parseInt(yField.getText());
        double newRotation = Double.parseDouble(rotationField.getText());
        double scalePercent = Math.max(UIConstants.MIN_SCALE_PERCENT, Double.parseDouble(scaleField.getText()));

        double scaleFactor = scalePercent / 100.0;
        if (Math.abs(scaleFactor - 1.0) > 0.01) {
            group.scaleFromCenter(scaleFactor);
        }

        double rotationDiff = newRotation - group.getRotation();
        if (Math.abs(rotationDiff) > UIConstants.ROTATION_DELTA_EPSILON) {
            group.rotateAroundCenter(rotationDiff);
        }

        int currentCenterX = group.getGroupCenterX();
        int currentCenterY = group.getGroupCenterY();
        group.move(newCenterX - currentCenterX, newCenterY - currentCenterY);
    }
}
