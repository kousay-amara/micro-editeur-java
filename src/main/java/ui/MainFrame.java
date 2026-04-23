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
import command.CommandHistory;
import model.Scene;
import model.Shape;
import persistence.ShapePersistenceService;

public class MainFrame extends Frame {
    private static final String TOOLBAR_STATE_FILENAME = ".micro-editeur-toolbar.dat";

    private Scene scene;
    private WhiteboardPanel whiteboardPanel;
    private ToolbarPanel toolbarPanel;
    private ControlPanel controlPanel;
    private CommandHistory history;
    private final ShapePersistenceService persistenceService;
    private final File toolbarStateFile;

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
        restoreToolbarState();

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

        controlPanel = new ControlPanel(history, whiteboardPanel, new Runnable() {
            @Override
            public void run() {
                saveDocument();
            }
        }, new Runnable() {
            @Override
            public void run() {
                loadDocument();
            }
        });
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

    private void saveDocument() {
        FileDialog dialog = new FileDialog(this, "Sauvegarder le document", FileDialog.SAVE);
        dialog.setFile("document.mfg");
        dialog.setVisible(true);

        File selectedFile = resolveSelectedFile(dialog);
        if (selectedFile == null) {
            return;
        }

        try {
            persistenceService.saveShapes(scene.getShapes(), selectedFile);
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
            scene.replaceShapes(persistenceService.loadShapes(selectedFile));
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
            toolbarPanel.replacePrototypes(persistenceService.loadShapes(toolbarStateFile));
        } catch (IOException e) {
            showMessage("Etat toolbar ignore", "Impossible de recharger l'etat precedent de la toolbar.");
        }
    }

    private void persistToolbarState() {
        try {
            persistenceService.saveShapes(toolbarPanel.getPrototypesSnapshot(), toolbarStateFile);
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
