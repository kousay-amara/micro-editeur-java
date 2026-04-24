package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import command.CommandHistory;
import model.Scene;
import model.Shape;
import persistence.ShapePersistenceService;
import ui.ToolbarPanel.DropListener;
import ui.WhiteboardPanel.ShapeToToolbarListener;

public class MainFrame extends Frame {
    private static final String TOOLBAR_STATE_FILENAME = ".micro-editeur-toolbar.dat";

    private Scene scene;
    private WhiteboardPanel whiteboardPanel;
    private ToolbarPanel toolbarPanel;
    private ControlPanel controlPanel;
    private CommandHistory history;
    private final ShapePersistenceService persistenceService;
    private final File toolbarStateFile;
    private DropListener toolbarDropListener;
    private ShapeToToolbarListener shapeToToolbarListener;

    public MainFrame() {
        setTitle("Micro-Editeur");
        setSize(UIConstants.MAIN_WINDOW_WIDTH, UIConstants.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        scene = new Scene();
        history = new CommandHistory();
        persistenceService = new ShapePersistenceService();
        toolbarStateFile = new File(System.getProperty("user.home"), TOOLBAR_STATE_FILENAME);
        whiteboardPanel = new WhiteboardPanel(scene, history);
        whiteboardPanel.initializeSelectionController();

        toolbarDropListener = this::handleToolbarDrop;
        toolbarPanel = new ToolbarPanel(toolbarDropListener, history);
        restoreToolbarState();

        shapeToToolbarListener = createShapeToToolbarListener();
        whiteboardPanel.setShapeToToolbarListener(shapeToToolbarListener);

        controlPanel = new ControlPanel(history, whiteboardPanel, this::saveDocument, this::loadDocument);
        whiteboardPanel.setControlPanel(controlPanel);

        setLayout(new BorderLayout());
        add(toolbarPanel, BorderLayout.WEST);
        add(controlPanel, BorderLayout.NORTH);
        add(whiteboardPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                persistToolbarState();
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
        whiteboardPanel.setToolbarWidth(toolbarPanel.getWidth());
    }

    public static void main(String[] args) {
        new MainFrame();
    }

    private void handleToolbarDrop(Shape prototype, int screenX, int screenY) {
        Point whiteboardLocation = whiteboardPanel.getLocationOnScreen();
        if (!isInsideWhiteboard(screenX, screenY, whiteboardLocation)) {
            return;
        }

        Shape clone = prototype.clone();
        int localX = screenX - whiteboardLocation.x;
        int localY = screenY - whiteboardLocation.y;
        clone.move(localX - clone.getX(), localY - clone.getY());
        whiteboardPanel.createShapeFromPrototype(clone);
    }

    private boolean isInsideWhiteboard(int screenX, int screenY, Point whiteboardLocation) {
        return screenX >= whiteboardLocation.x
                && screenX < whiteboardLocation.x + whiteboardPanel.getWidth()
                && screenY >= whiteboardLocation.y
                && screenY < whiteboardLocation.y + whiteboardPanel.getHeight();
    }

    private ShapeToToolbarListener createShapeToToolbarListener() {
        return new ShapeToToolbarListener() {
            @Override
            public boolean isOverToolbar(int screenX, int screenY) {
                Point toolbarLocation = toolbarPanel.getLocationOnScreen();
                return screenX >= toolbarLocation.x
                        && screenX < toolbarLocation.x + toolbarPanel.getWidth()
                        && screenY >= toolbarLocation.y
                        && screenY < toolbarLocation.y + toolbarPanel.getHeight();
            }

            @Override
            public boolean isOverTrash(int screenX, int screenY) {
                return toolbarPanel.isOverTrash(screenX, screenY);
            }

            @Override
            public void onDrop(Shape clone) {
                toolbarPanel.addPrototype(clone);
            }
        };
    }

    private void saveDocument() {
        FileDialog dialog = new FileDialog(this, "Sauvegarder le document", FileDialog.SAVE);
        dialog.setFile("document.mfg");
        dialog.setVisible(true);

        File selectedFile = resolveSelectedFile(dialog);
        if (selectedFile == null) {
            return;
        }

        try {
            List<Shape> currentShapes = scene.getShapes();
            persistenceService.saveShapes(currentShapes, selectedFile);
            showMessage("Sauvegarde", "Document sauvegarde.");
        } catch (IOException e) {
            showMessage("Erreur de sauvegarde", "Impossible de sauvegarder le document.");
        }
    }

    private void loadDocument() {
        FileDialog dialog = new FileDialog(this, "Charger un document", FileDialog.LOAD);
        dialog.setVisible(true);

        File selectedFile = resolveSelectedFile(dialog);
        if (selectedFile == null) {
            return;
        }

        try {
            List<Shape> loadedShapes = persistenceService.loadShapes(selectedFile);
            scene.replaceShapes(loadedShapes);
            history.clear();
            whiteboardPanel.onHistoryChanged();
        } catch (IOException e) {
            showMessage("Erreur de chargement", "Fichier de sauvegarde invalide.");
        }
    }

    private void restoreToolbarState() {
        if (!toolbarStateFile.isFile()) {
            return;
        }

        try {
            List<Shape> toolbarPrototypes = persistenceService.loadShapes(toolbarStateFile);
            toolbarPanel.replacePrototypes(toolbarPrototypes);
        } catch (IOException e) {
            showMessage("Etat toolbar ignore", "Impossible de recharger l'etat precedent de la toolbar.");
        }
    }

    private void persistToolbarState() {
        try {
            List<Shape> toolbarPrototypes = toolbarPanel.getPrototypesSnapshot();
            persistenceService.saveShapes(toolbarPrototypes, toolbarStateFile);
        } catch (IOException e) {
            showMessage("Erreur toolbar", "Impossible de sauvegarder l'etat de la toolbar.");
        }
    }

    private File resolveSelectedFile(FileDialog dialog) {
        if (dialog.getDirectory() == null || dialog.getFile() == null) {
            return null;
        }
        return new File(dialog.getDirectory(), dialog.getFile());
    }

    private void showMessage(String title, String message) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.add(new Label(message), BorderLayout.CENTER);

        Button okButton = new Button("OK");
        okButton.addActionListener(e -> dialog.dispose());

        Panel buttonPanel = new Panel();
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
