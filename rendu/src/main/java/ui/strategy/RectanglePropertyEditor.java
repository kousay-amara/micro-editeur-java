package ui.strategy;

import model.Rectangle;
import model.Shape;
import ui.UIConstants;
import java.awt.Color;
import java.awt.Choice;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

/**
 * Property editor for Rectangle shapes.
 */
public class RectanglePropertyEditor implements PropertyEditorStrategy {

    private TextField xField;
    private TextField yField;
    private TextField widthField;
    private TextField heightField;
    private TextField cornerRadiusField;
    private TextField rotationField;
    private TextField rotationCenterXField;
    private TextField rotationCenterYField;
    private TextField scaleField;
    private Choice colorChoice;

    @Override
    public void setupFields(Shape shape, Panel panel) {
        Rectangle rect = (Rectangle) shape;

        xField = PropertyEditorSupport.addField(panel, "Centre X:", rect.getX());
        yField = PropertyEditorSupport.addField(panel, "Centre Y:", rect.getY());
        widthField = PropertyEditorSupport.addField(panel, "Width:", rect.getWidth());
        heightField = PropertyEditorSupport.addField(panel, "Height:", rect.getHeight());
        cornerRadiusField = PropertyEditorSupport.addField(panel, "Corner Radius:", rect.getCornerRadius());
        rotationField = PropertyEditorSupport.addField(panel, "Rotation:", (int) rect.getRotation());
        rotationCenterXField = PropertyEditorSupport.addField(panel, "Centre rotation X:", rect.getRotationCenterX());
        rotationCenterYField = PropertyEditorSupport.addField(panel, "Centre rotation Y:", rect.getRotationCenterY());
        scaleField = PropertyEditorSupport.addField(panel, "Scale %:", UIConstants.DEFAULT_SCALE_PERCENT);

        colorChoice = PropertyEditorSupport.createColorChoice(rect.getColor());
        panel.add(new Label("Color:"));
        panel.add(colorChoice);
    }

    @Override
    public void applyChanges(Shape shape) throws NumberFormatException {
        Rectangle rect = (Rectangle) shape;

        int newX = Integer.parseInt(xField.getText());
        int newY = Integer.parseInt(yField.getText());
        int width = Math.max(UIConstants.MIN_SHAPE_SIZE, Integer.parseInt(widthField.getText()));
        int height = Math.max(UIConstants.MIN_SHAPE_SIZE, Integer.parseInt(heightField.getText()));
        int cornerRadius = Math.max(0, Integer.parseInt(cornerRadiusField.getText()));
        double rotation = Double.parseDouble(rotationField.getText());
        int rotationCenterX = Integer.parseInt(rotationCenterXField.getText());
        int rotationCenterY = Integer.parseInt(rotationCenterYField.getText());
        double scalePercent = Math.max(UIConstants.MIN_SCALE_PERCENT, Double.parseDouble(scaleField.getText()));
        Color color = PropertyEditorSupport.getSelectedColor(colorChoice);

        rect.move(newX - rect.getX(), newY - rect.getY());
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setCornerRadius(cornerRadius);
        rect.setRotationCenter(rotationCenterX, rotationCenterY);
        rect.setRotation(rotation);
        rect.setColor(color);

        double scaleFactor = scalePercent / 100.0;
        if (Math.abs(scaleFactor - 1.0) > 0.01) {
            rect.scaleFromCenter(scaleFactor);
        }
    }
}
