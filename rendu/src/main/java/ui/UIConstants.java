package ui;

import java.awt.Color;

/**
 * Centralize all UI constants (dimensions, colors, margins).
 * Change values here to update everywhere.
 */
public final class UIConstants {

    // ==================== WINDOW DIMENSIONS ====================
    public static final int MAIN_WINDOW_WIDTH = 1000;
    public static final int MAIN_WINDOW_HEIGHT = 600;

    // ==================== TOOLBAR DIMENSIONS ====================
    public static final int TOOLBAR_WIDTH = 90;
    public static final int TOOLBAR_HEIGHT = 0;  // Dynamic (BorderLayout)
    public static final int TOOLBAR_ITEM_PANEL_WIDTH = 70;
    public static final int TOOLBAR_ITEM_PANEL_HEIGHT = 55;
    public static final int TOOLBAR_ITEM_SPACING = 63;
    public static final int TOOLBAR_ITEM_LEFT_PADDING = 10;
    public static final int TOOLBAR_ITEM_TOP_START = 10;

    public static final int TOOLBAR_TRASH_HEIGHT = 68;
    public static final int TOOLBAR_TRASH_CORNER_RADIUS = 14;

    // ==================== PREVIEW RENDERING ====================
    public static final int PREVIEW_MARGIN = 5;

    // ==================== COLORS ====================
    public static final Color TOOLBAR_BACKGROUND = new Color(245, 245, 245);
    public static final Color TOOLBAR_ITEM_BACKGROUND = Color.LIGHT_GRAY;
    public static final Color TOOLBAR_ITEM_BORDER = Color.DARK_GRAY;

    public static final Color WHITEBOARD_BACKGROUND = Color.WHITE;

    public static final Color SHAPE_OUTLINE_COLOR = Color.BLACK;

    public static final Color TRASH_CAN_COLOR = new Color(180, 60, 60);
    public static final Color TRASH_OUTLINE_COLOR = Color.GRAY;
    public static final Color TRASH_FILL_COLOR = new Color(235, 235, 235);

    // ==================== MARGINS & PADDING ====================
    public static final int MARGIN_DEFAULT = 10;
    public static final int PADDING_FIELD = 5;

    // ==================== DIALOG DIMENSIONS ====================
    public static final int PROPERTY_DIALOG_WIDTH = 360;
    public static final int PROPERTY_DIALOG_HEIGHT = 380;

    // ==================== PROPERTY DIALOG GRID ====================
    public static final int PROPERTY_DIALOG_COLS = 2;
    public static final int PROPERTY_DIALOG_ROW_GAP = 5;
    public static final int PROPERTY_DIALOG_COL_GAP = 5;

    // ==================== BOUNDS CLAMPING ====================
    public static final int MIN_SHAPE_SIZE = 1;
    public static final int MIN_POLYGON_SIDES = 3;

    // ==================== CORNER RADIUS ====================
    public static final int DEFAULT_CORNER_RADIUS = 0;

    // ==================== ROTATION DEFAULTS ====================
    public static final double DEFAULT_ROTATION = 0.0;
    public static final double ROTATION_DELTA_EPSILON = 0.01;

    // ==================== SCALE DEFAULTS ====================
    public static final int DEFAULT_SCALE_PERCENT = 100;
    public static final double MIN_SCALE_PERCENT = 1.0;

    // ==================== COLOR PALETTE ====================
    public static final Color[] COLOR_PALETTE = {
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.YELLOW,
        Color.BLACK
    };

    public static final String[] COLOR_NAMES = {
        "Blue", "Red", "Green", "Yellow", "Black"
    };

    // ==================== TRASH DIMENSIONS ====================
    public static final int TRASH_CAN_WIDTH = 28;
    public static final int TRASH_CAN_HEIGHT = 24;
    public static final int TRASH_BOX_X = 10;
    public static final int TRASH_BOX_Y = 6;
    public static final int TRASH_ICON_CORNER_RADIUS = 8;

    // Private constructor to prevent instantiation
    private UIConstants() {
        throw new AssertionError("Cannot instantiate UIConstants");
    }
}
