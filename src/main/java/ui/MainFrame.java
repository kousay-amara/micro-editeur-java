package ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import command.CommandHistory;
import model.Scene;
import model.Shape;

public class MainFrame extends Frame {
    private Scene scene;
    private WhiteboardPanel whiteboardPanel;
    private ToolbarPanel toolbarPanel;
    private ControlPanel controlPanel;
    private CommandHistory history;

    public MainFrame() {
        setTitle("Micro-Editeur");
        setSize(UIConstants.MAIN_WINDOW_WIDTH, UIConstants.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        scene = new Scene();
        history = new CommandHistory();
        whiteboardPanel = new WhiteboardPanel(scene, history);
        whiteboardPanel.initializeSelectionController();

        toolbarPanel = new ToolbarPanel(new ToolbarPanel.DropListener() {
            @Override
            public void onDrop(Shape prototype, int screenX, int screenY) {
                Point wbLoc = whiteboardPanel.getLocationOnScreen();
                if (screenX >= wbLoc.x && screenX < wbLoc.x + whiteboardPanel.getWidth()
                        && screenY >= wbLoc.y && screenY < wbLoc.y + whiteboardPanel.getHeight()) {
                    Shape clone = prototype.clone();
                    int localX = screenX - wbLoc.x;
                    int localY = screenY - wbLoc.y;
                    clone.move(localX - clone.getX(), localY - clone.getY());
                    whiteboardPanel.createShapeFromPrototype(clone);
                }
            }
        }, history);

        whiteboardPanel.setShapeToToolbarListener(new WhiteboardPanel.ShapeToToolbarListener() {
            @Override
            public boolean isOverToolbar(int screenX, int screenY) {
                Point tbLoc = toolbarPanel.getLocationOnScreen();
                return screenX >= tbLoc.x && screenX < tbLoc.x + toolbarPanel.getWidth()
                        && screenY >= tbLoc.y && screenY < tbLoc.y + toolbarPanel.getHeight();
            }

            @Override
            public boolean isOverTrash(int screenX, int screenY) {
                return toolbarPanel.isOverTrash(screenX, screenY);
            }

            @Override
            public void onDrop(Shape clone) {
                toolbarPanel.addPrototype(clone);
            }
        });

        controlPanel = new ControlPanel(history, whiteboardPanel);
        whiteboardPanel.setControlPanel(controlPanel);

        setLayout(new BorderLayout());
        add(toolbarPanel, BorderLayout.WEST);
        add(controlPanel, BorderLayout.NORTH);
        add(whiteboardPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
        whiteboardPanel.setToolbarWidth(toolbarPanel.getWidth());
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
