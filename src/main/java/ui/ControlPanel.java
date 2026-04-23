package ui;

import java.awt.Button;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import command.CommandHistory;

public class ControlPanel extends Panel {
    private Button undoBtn;
    private Button redoBtn;
    private Button saveBtn;
    private Button loadBtn;
    private CommandHistory history;
    private WhiteboardPanel whiteboardPanel;
    private final Runnable onSave;
    private final Runnable onLoad;

    public ControlPanel(CommandHistory history, WhiteboardPanel whiteboardPanel, Runnable onSave, Runnable onLoad) {
        this.history = history;
        this.whiteboardPanel = whiteboardPanel;
        this.onSave = onSave;
        this.onLoad = onLoad;
        setLayout(new GridLayout(1, 0, 8, 0));

        undoBtn = new Button("Undo");
        undoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                history.undo();
                updateButtonStates();
                whiteboardPanel.repaint();
            }
        });
        add(undoBtn);

        redoBtn = new Button("Redo");
        redoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                history.redo();
                updateButtonStates();
                whiteboardPanel.repaint();
            }
        });
        add(redoBtn);

        saveBtn = new Button("Save");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ControlPanel.this.onSave != null) {
                    ControlPanel.this.onSave.run();
                }
            }
        });
        add(saveBtn);

        loadBtn = new Button("Load");
        loadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ControlPanel.this.onLoad != null) {
                    ControlPanel.this.onLoad.run();
                }
            }
        });
        add(loadBtn);

        // Initialiser l'état des boutons
        updateButtonStates();
    }

    public void updateButtonStates() {
        undoBtn.setEnabled(history.canUndo());
        redoBtn.setEnabled(history.canRedo());
    }
}
