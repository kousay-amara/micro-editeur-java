package ui;

import java.awt.Graphics;
import model.Shape;

/**
 * Rendering contract kept outside of the model so shapes stay UI-agnostic.
 */
public interface ShapeRenderer {
    void render(Shape shape, Graphics graphics);

    void renderPreview(Shape shape, Graphics graphics, int panelWidth, int panelHeight);
}
