package ui;

import model.Scene;
import model.Shape;
import model.Group;
import command.*;
import java.util.ArrayList;
import java.util.List;

public class SelectionController {

    private final Scene scene;
    private final CommandHistory history;
    private final WhiteboardPanel whiteboard;

    public SelectionController(Scene scene, CommandHistory history, WhiteboardPanel whiteboard) {
        this.scene = scene;
        this.history = history;
        this.whiteboard = whiteboard;
    }

    public void toggleSelection(Shape shape, boolean multiSelect) {
        if (!multiSelect) {
            List<Shape> toDeselect = new ArrayList<>(scene.getSelectedShapes());
            for (Shape s : toDeselect) {
                s.setSelected(false);
                scene.removeSelectedShape(s);
            }
        }

        if (shape != null) {
            if (scene.isShapeSelected(shape)) {
                shape.setSelected(false);
                scene.removeSelectedShape(shape);
            } else {
                shape.setSelected(true);
                scene.addSelectedShape(shape);
            }
        }

        whiteboard.repaint();
    }

    public void selectShapesInRect(int x, int y, int w, int h) {
        for (Shape shape : scene.getShapes()) {
            if (shape.getX() >= x && shape.getX() <= x + w &&
                shape.getY() >= y && shape.getY() <= y + h) {
                shape.setSelected(true);
                scene.addSelectedShape(shape);
            }
        }
        whiteboard.repaint();
    }

    public void clearSelection() {
        List<Shape> toDeselect = new ArrayList<>(scene.getSelectedShapes());
        for (Shape s : toDeselect) {
            s.setSelected(false);
            scene.removeSelectedShape(s);
        }
        whiteboard.repaint();
    }

    public Shape findTopLevelShape(Shape shape) {
        if (shape != null) {
            if (shape instanceof Group) {
                Group group = (Group) shape;
                Group parent = group.getParent();
                if (parent != null) {
                    return findTopLevelShape(parent);
                }
                return group;
            }
            for (Shape rootShape : scene.getShapes()) {
                if (findInGroup(rootShape, shape)) {
                    return rootShape;
                }
            }
        }
        return shape;
    }

    private boolean findInGroup(Shape container, Shape target) {
        if (!container.isGroup()) return false;
        Group group = (Group) container;
        if (group.getChildren().contains(target)) return true;
        for (Shape child : group.getChildren()) {
            if (findInGroup(child, target)) return true;
        }
        return false;
    }

    public void groupSelectedShapes() {
        List<Shape> selected = scene.getSelectedShapes();
        if (selected.size() < 2) {
            return;
        }

        for (Shape s : selected) {
            s.setSelected(false);
        }
        scene.clearSelectedShapes();

        executeCommand(new GroupCommand(scene, new ArrayList<>(selected)));
    }

    public void ungroupShape(Group group) {
        if (group == null || !group.isGroup()) {
            return;
        }

        group.setSelected(false);
        scene.removeSelectedShape(group);

        executeCommand(new UngroupCommand(scene, group));
    }

    public void editSelectedShape() {
        List<Shape> selected = scene.getSelectedShapes();
        if (selected.isEmpty()) {
            return;
        }

        Shape shapeToEdit = selected.get(0);

        PropertyDialog dialog = new PropertyDialog(null, shapeToEdit, whiteboard::repaint);
        dialog.setVisible(true);

        if (dialog.wasApplied()) {
            ShapeMemento newState = new ShapeMemento(shapeToEdit);
            EditShapeCommand cmd = EditShapeCommand.createForAlreadyExecuted(
                shapeToEdit,
                dialog.getOriginalState(),
                newState
            );
            addCommandToHistory(cmd);
        }
    }

    private void executeCommand(Command cmd) {
        history.execute(cmd);
        updateUI();
    }

    private void addCommandToHistory(Command cmd) {
        history.addToHistory(cmd);
        updateUI();
    }

    private void updateUI() {
        whiteboard.onHistoryChanged();
    }
}
