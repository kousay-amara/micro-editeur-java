package ui.strategy;

import model.RegularPolygon;
import model.Shape;
import ui.UIConstants;
import java.awt.Color;
import java.awt.Choice;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

/**
 * Property editor for RegularPolygon shapes.
 */
public class PolygonPropertyEditor implements PropertyEditorStrategy {

    private TextField xField;
    private TextField yField;
    private TextField sidesField;
    private TextField sideLengthField;
    private TextField rotationField;
    private TextField rotationCenterXField;
    private TextField rotationCenterYField;
    private TextField scaleField;
    private Choice colorChoice;

    @Override
    public void setupFields(Shape shape, Panel panel) {
        RegularPolygon poly = (RegularPolygon) shape;

        xField = PropertyEditorSupport.addField(panel, "Centre X:", poly.getX());
        yField = PropertyEditorSupport.addField(panel, "Centre Y:", poly.getY());
        sidesField = PropertyEditorSupport.addField(panel, "Sides:", poly.getSides());
        sideLengthField = PropertyEditorSupport.addField(panel, "Side Length:", poly.getSideLength());
        rotationField = PropertyEditorSupport.addField(panel, "Rotation:", (int) poly.getRotation());
        rotationCenterXField = PropertyEditorSupport.addField(panel, "Centre rotation X:", poly.getRotationCenterX());
        rotationCenterYField = PropertyEditorSupport.addField(panel, "Centre rotation Y:", poly.getRotationCenterY());
        scaleField = PropertyEditorSupport.addField(panel, "Scale %:", UIConstants.DEFAULT_SCALE_PERCENT);

        colorChoice = PropertyEditorSupport.createColorChoice(poly.getColor());
        panel.add(new Label("Color:"));
        panel.add(colorChoice);
    }

    @Override
    public void applyChanges(Shape shape) throws NumberFormatException {
        RegularPolygon poly = (RegularPolygon) shape;

        int newX = Integer.parseInt(xField.getText());
        int newY = Integer.parseInt(yField.getText());
        int sides = Math.max(UIConstants.MIN_POLYGON_SIDES, Integer.parseInt(sidesField.getText()));
        int sideLength = Math.max(UIConstants.MIN_SHAPE_SIZE, Integer.parseInt(sideLengthField.getText()));
        double rotation = Double.parseDouble(rotationField.getText());
        int rotationCenterX = Integer.parseInt(rotationCenterXField.getText());
        int rotationCenterY = Integer.parseInt(rotationCenterYField.getText());
        double scalePercent = Math.max(UIConstants.MIN_SCALE_PERCENT, Double.parseDouble(scaleField.getText()));
        Color color = PropertyEditorSupport.getSelectedColor(colorChoice);

        poly.move(newX - poly.getX(), newY - poly.getY());
        poly.setSides(sides);
        poly.setSideLength(sideLength);
        poly.setRotationCenter(rotationCenterX, rotationCenterY);
        poly.setRotation(rotation);
        poly.setColor(color);

        double scaleFactor = scalePercent / 100.0;
        if (Math.abs(scaleFactor - 1.0) > 0.01) {
            poly.scaleFromCenter(scaleFactor);
        }
    }
}
