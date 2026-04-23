package ui.strategy;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import ui.UIConstants;

final class PropertyEditorSupport {

    private PropertyEditorSupport() {
    }

    static TextField addField(Panel panel, String label, int value) {
        panel.add(new Label(label));
        TextField field = new TextField(String.valueOf(value));
        panel.add(field);
        return field;
    }

    static Choice createColorChoice(Color currentColor) {
        Choice colorChoice = new Choice();
        for (String colorName : UIConstants.COLOR_NAMES) {
            colorChoice.add(colorName);
        }

        if (Color.RED.equals(currentColor)) {
            colorChoice.select("Red");
        } else if (Color.GREEN.equals(currentColor)) {
            colorChoice.select("Green");
        } else if (Color.YELLOW.equals(currentColor)) {
            colorChoice.select("Yellow");
        } else if (Color.BLACK.equals(currentColor)) {
            colorChoice.select("Black");
        } else {
            colorChoice.select("Blue");
        }

        return colorChoice;
    }

    static Color getSelectedColor(Choice colorChoice) {
        String selected = colorChoice.getSelectedItem();
        if ("Red".equals(selected)) {
            return Color.RED;
        }
        if ("Green".equals(selected)) {
            return Color.GREEN;
        }
        if ("Yellow".equals(selected)) {
            return Color.YELLOW;
        }
        if ("Black".equals(selected)) {
            return Color.BLACK;
        }
        return Color.BLUE;
    }
}
