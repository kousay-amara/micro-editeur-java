package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import command.AddPrototypeCommand;
import command.CommandHistory;
import command.RemovePrototypeCommand;
import model.Shape;

public class ToolbarPanel extends Panel {
    private final CommandHistory history;
    private final ShapeRenderer shapeRenderer;
    private final List<ToolbarItem> items = new ArrayList<>();
    private final Panel itemsPanel;
    private final Panel trashPanel;
    private final DropListener dropListener;

    public interface DropListener {
        void onDrop(Shape prototype, int screenX, int screenY);
    }

    private static class ToolbarItem {
        private final Shape prototype;
        private final Panel panel;

        private ToolbarItem(Shape prototype, Panel panel) {
            this.prototype = prototype;
            this.panel = panel;
        }
    }

    public ToolbarPanel(DropListener dropListener, CommandHistory history) {
        this.dropListener = dropListener;
        this.history = history;
        this.shapeRenderer = new AwtShapeRenderer();
        setLayout(new BorderLayout(0, 10));
        setPreferredSize(new Dimension(UIConstants.TOOLBAR_WIDTH, 0));
        setBackground(UIConstants.TOOLBAR_BACKGROUND);

        itemsPanel = new Panel(null);
        itemsPanel.setBackground(UIConstants.TOOLBAR_BACKGROUND);
        add(itemsPanel, BorderLayout.CENTER);

        trashPanel = new Panel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                drawTrash(g, getWidth(), getHeight());
            }
        };
        trashPanel.setPreferredSize(new Dimension(UIConstants.TOOLBAR_WIDTH, UIConstants.TOOLBAR_TRASH_HEIGHT));
        trashPanel.setBackground(UIConstants.TOOLBAR_BACKGROUND);
        add(trashPanel, BorderLayout.SOUTH);

        addPrototypeDirect(new model.Rectangle(0, 0, 120, 80, Color.BLUE), items.size());
        addPrototypeDirect(new model.RegularPolygon(0, 0, 6, 50, Color.RED), items.size());
    }

    public void addPrototype(Shape prototype) {
        prototype.move(-prototype.getX(), -prototype.getY());
        history.execute(new AddPrototypeCommand(this, prototype));
    }

    public void removePrototype(Shape prototype) {
        history.execute(new RemovePrototypeCommand(this, prototype));
    }

    public boolean isOverTrash(int screenX, int screenY) {
        Point trashLocation = trashPanel.getLocationOnScreen();
        return screenX >= trashLocation.x && screenX < trashLocation.x + trashPanel.getWidth()
                && screenY >= trashLocation.y && screenY < trashLocation.y + trashPanel.getHeight();
    }

    public int getPrototypeCount() {
        return items.size();
    }

    public List<Shape> getPrototypesSnapshot() {
        ArrayList<Shape> snapshot = new ArrayList<>();
        for (ToolbarItem item : items) {
            Shape clone = item.prototype.clone();
            normalizePrototype(clone);
            snapshot.add(clone);
        }
        return snapshot;
    }

    public void replacePrototypes(List<Shape> prototypes) {
        items.clear();
        itemsPanel.removeAll();

        for (Shape prototype : prototypes) {
            addPrototypeDirect(prototype, items.size());
        }

        relayoutItems();
        revalidate();
        repaint();
    }

    public void addPrototypeDirect(Shape prototype, int index) {
        normalizePrototype(prototype);
        ToolbarItem item = createItem(prototype);
        int insertionIndex = Math.max(0, Math.min(index, items.size()));
        items.add(insertionIndex, item);
        itemsPanel.add(item.panel);
        relayoutItems();
        revalidate();
        repaint();
    }

    public int removePrototypeDirect(Shape prototype) {
        for (int i = 0; i < items.size(); i++) {
            ToolbarItem item = items.get(i);
            if (item.prototype == prototype) {
                itemsPanel.remove(item.panel);
                items.remove(i);
                relayoutItems();
                revalidate();
                repaint();
                return i;
            }
        }
        return -1;
    }

    private ToolbarItem createItem(Shape prototype) {
        final boolean[] dragging = {false};

        Panel panel = new Panel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                shapeRenderer.renderPreview(prototype, g, getWidth(), getHeight());
                g.setColor(Color.DARK_GRAY);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };

        panel.setBackground(UIConstants.TOOLBAR_ITEM_BACKGROUND);
        panel.setSize(UIConstants.TOOLBAR_ITEM_PANEL_WIDTH, UIConstants.TOOLBAR_ITEM_PANEL_HEIGHT);

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                dragging[0] = true;
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragging[0] = false;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragging[0]) {
                    Point screenPos = e.getLocationOnScreen();
                    if (isOverTrash(screenPos.x, screenPos.y)) {
                        removePrototype(prototype);
                    } else {
                        dropListener.onDrop(prototype, screenPos.x, screenPos.y);
                    }
                }
                dragging[0] = false;
            }
        });

        return new ToolbarItem(prototype, panel);
    }

    private void relayoutItems() {
        int y = UIConstants.TOOLBAR_ITEM_TOP_START;
        for (ToolbarItem item : items) {
            item.panel.setLocation(UIConstants.TOOLBAR_ITEM_LEFT_PADDING, y);
            y += UIConstants.TOOLBAR_ITEM_SPACING;
        }
        itemsPanel.repaint();
    }

    private void normalizePrototype(Shape prototype) {
        clearSelectionRecursively(prototype);
        prototype.move(-prototype.getX(), -prototype.getY());
    }

    private void clearSelectionRecursively(Shape shape) {
        shape.setSelected(false);
        if (!shape.isGroup()) {
            return;
        }

        for (Shape child : shape.getChildren()) {
            clearSelectionRecursively(child);
        }
    }

    private void drawTrash(Graphics g, int width, int height) {
        int boxX = UIConstants.TRASH_BOX_X;
        int boxY = UIConstants.TRASH_BOX_Y;
        int boxW = width - 20;
        int boxH = height - 12;

        g.setColor(UIConstants.TRASH_FILL_COLOR);
        g.fillRoundRect(boxX, boxY, boxW, boxH, UIConstants.TRASH_BOX_X, UIConstants.TRASH_BOX_X);
        g.setColor(UIConstants.TRASH_OUTLINE_COLOR);
        g.drawRoundRect(boxX, boxY, boxW, boxH, UIConstants.TRASH_BOX_X, UIConstants.TRASH_BOX_X);

        int canW = UIConstants.TRASH_CAN_WIDTH;
        int canH = UIConstants.TRASH_CAN_HEIGHT;
        int canX = boxX + (boxW - canW) / 2;
        int canY = boxY + (boxH - canH) / 2 + 2;

        g.setColor(UIConstants.TRASH_CAN_COLOR);
        g.fillRoundRect(canX, canY + 6, canW, canH, UIConstants.TRASH_ICON_CORNER_RADIUS, UIConstants.TRASH_ICON_CORNER_RADIUS);
        g.fillRect(canX - 4, canY, canW + 8, 6);
        g.fillRect(canX + canW / 2 - 6, canY - 4, 12, 4);

        g.setColor(Color.WHITE);
        g.drawLine(canX + 7, canY + 11, canX + 7, canY + canH - 1);
        g.drawLine(canX + canW / 2, canY + 12, canX + canW / 2, canY + canH - 2);
        g.drawLine(canX + canW - 7, canY + 11, canX + canW - 7, canY + canH - 1);
    }
}
