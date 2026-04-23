package ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import model.Scene;
import model.SceneListener;
import model.Shape;
import model.Rectangle;
import model.RegularPolygon;
import model.Group;
import command.*;
import java.awt.Point;


public class WhiteboardPanel extends Canvas implements SceneListener {
    private Scene scene;
    private CommandHistory history;
    private ControlPanel controlPanel;
    private SelectionController selectionController;
    private final ShapeRenderer shapeRenderer;
    private Shape draggedShape = null;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private int shapeStartX = 0;
    private int shapeStartY = 0;

    public interface ShapeToToolbarListener {
        boolean isOverToolbar(int screenX, int screenY);
        boolean isOverTrash(int screenX, int screenY);
        void onDrop(Shape clone);
    }

    private ShapeToToolbarListener shapeToToolbarListener;
    private int toolbarWidth = 0;

    public void setShapeToToolbarListener(ShapeToToolbarListener listener) {
        this.shapeToToolbarListener = listener;
    }

    public void setToolbarWidth(int width) {
        this.toolbarWidth = width;
    }

    public void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    public void initializeSelectionController() {
        this.selectionController = new SelectionController(scene, history, this);
    }

    // Helper pour exécuter un command et mettre à jour les boutons
    private void executeCommand(Command cmd) {
        history.execute(cmd);
        onHistoryChanged();
    }

    public void onHistoryChanged() {
        if (controlPanel != null) {
            controlPanel.updateButtonStates();
        }
        repaint();
    }

    public WhiteboardPanel(Scene scene, CommandHistory history) {
        this.scene = scene;
        this.history = history;
        this.controlPanel = null;  // Sera set par MainFrame
        this.shapeRenderer = new AwtShapeRenderer();
        setBackground(Color.WHITE);
        scene.addListener(this);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
                    handleRightClick(e);
                } else {
                    handleMousePressed(e);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    handleRightClick(e);
                } else {
                    handleMouseReleased(e);
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
        

    }
    
    
    private void handleMousePressed(MouseEvent e) {
        Shape shape = scene.getShapeAt(e.getX(), e.getY());
        Shape topShape = selectionController.findTopLevelShape(shape);

        if (e.isControlDown()) {
            selectionController.toggleSelection(topShape, true);
        } else {
            if (topShape != null) {
                draggedShape = topShape;
                dragStartX = e.getX();
                dragStartY = e.getY();
                shapeStartX = topShape.getX();
                shapeStartY = topShape.getY();
                selectionController.toggleSelection(topShape, false);
            } else {
                selectionController.clearSelection();
            }
        }
        repaint();
    }

private void handleRightClick(MouseEvent e) {
    Shape shape = scene.getShapeAt(e.getX(), e.getY());
    Shape topShape = selectionController.findTopLevelShape(shape);

    List<Shape> selected = scene.getSelectedShapes();

    if (topShape != null && !scene.isShapeSelected(topShape)) {
        selectionController.clearSelection();
        selectionController.toggleSelection(topShape, false);
        selected = scene.getSelectedShapes();
        repaint();
    }

    if (selected.isEmpty() && topShape == null) return;

    PopupMenu popup = new PopupMenu();

    if (selected.size() == 1) {
        MenuItem editItem = new MenuItem("Edit");
        editItem.addActionListener(ae -> selectionController.editSelectedShape());
        popup.add(editItem);
    }

    if (selected.size() > 1) {
        MenuItem groupItem = new MenuItem("Group");
        groupItem.addActionListener(ae -> selectionController.groupSelectedShapes());
        popup.add(groupItem);
    }

    if (topShape instanceof Group) {
        MenuItem ungroupItem = new MenuItem("De-group");
        ungroupItem.addActionListener(ae -> selectionController.ungroupShape((Group) topShape));
        popup.add(ungroupItem);
    }

    if (popup.getItemCount() > 0) {
        add(popup);
        popup.show(this, e.getX(), e.getY());
    }
}

    
    private void handleMouseDragged(MouseEvent e) {
        if (draggedShape != null) {
            int dx = e.getX() - dragStartX;
            int dy = e.getY() - dragStartY;
            draggedShape.move(dx, dy);
            clampShapeToBounds(draggedShape);
            dragStartX = e.getX();
            dragStartY = e.getY();
            repaint();
        }
    }

    private void clampShapeToBounds(Shape shape) {
        int panelW = getWidth();
        int panelH = getHeight();
        int newX = clampedCenterX(shape, panelW);
        int newY = clampedCenterY(shape, panelH);
        shape.move(newX - shape.getX(), newY - shape.getY());
    }

    private int clampedCenterX(Shape shape, int panelW) {
        if (shape instanceof Rectangle) {
            int half = ((Rectangle) shape).getWidth() / 2;
            return Math.max(-toolbarWidth, Math.min(shape.getX(), panelW - half));
        }
        if (shape instanceof RegularPolygon) {
            RegularPolygon poly = (RegularPolygon) shape;
            int r = (int) (poly.getSideLength() / (2 * Math.sin(Math.PI / poly.getSides())));
            return Math.max(-toolbarWidth, Math.min(shape.getX(), panelW - r));
        }
        return Math.max(-toolbarWidth, Math.min(shape.getX(), panelW));
    }

    private int clampedCenterY(Shape shape, int panelH) {
        if (shape instanceof Rectangle) {
            int half = ((Rectangle) shape).getHeight() / 2;
            return Math.max(half, Math.min(shape.getY(), panelH - half));
        }
        if (shape instanceof RegularPolygon) {
            RegularPolygon poly = (RegularPolygon) shape;
            int r = (int) (poly.getSideLength() / (2 * Math.sin(Math.PI / poly.getSides())));
            return Math.max(r, Math.min(shape.getY(), panelH - r));
        }
        return Math.max(0, Math.min(shape.getY(), panelH));
    }
        
private void handleMouseReleased(MouseEvent e) {
    if (draggedShape != null) {
        if (shapeToToolbarListener != null) {
            Point screenPos = e.getLocationOnScreen();
            if (shapeToToolbarListener.isOverTrash(screenPos.x, screenPos.y)) {
                draggedShape.move(shapeStartX - draggedShape.getX(), shapeStartY - draggedShape.getY());
                draggedShape.setSelected(false);
                scene.removeSelectedShape(draggedShape);
                executeCommand(new RemoveShapeCommand(scene, draggedShape));
                draggedShape = null;
                repaint();
                return;
            }
            if (shapeToToolbarListener.isOverToolbar(screenPos.x, screenPos.y)) {
                draggedShape.move(shapeStartX - draggedShape.getX(), shapeStartY - draggedShape.getY());
                shapeToToolbarListener.onDrop(draggedShape.clone());
                draggedShape = null;
                repaint();
                return;
            }
        }

        int newX = draggedShape.getX();
        int newY = draggedShape.getY();

        if (newX != shapeStartX || newY != shapeStartY) {
            draggedShape.move(shapeStartX - newX, shapeStartY - newY);
            executeCommand(new MoveShapeCommand(draggedShape, newX, newY));
        }

        draggedShape = null;
    }
    repaint();
}

    public void createShapeFromPrototype(Shape shape) {
        executeCommand(new AddShapeCommand(scene, shape));
    }

    
    @Override
    public void onShapeChanged() {
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (Shape shape : scene.getShapes()) {
            shapeRenderer.render(shape, g);
        }
    }
}
