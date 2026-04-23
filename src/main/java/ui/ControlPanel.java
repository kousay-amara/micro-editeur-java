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
    private CommandHistory history;
    private WhiteboardPanel whiteboardPanel;

    public ControlPanel(CommandHistory history, WhiteboardPanel whiteboardPanel) {
        this.history = history;
        this.whiteboardPanel = whiteboardPanel;
        setLayout(new GridLayout(0, 1));

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

        // Initialiser l'état des boutons
        updateButtonStates();
    }

    public void updateButtonStates() {
        undoBtn.setEnabled(history.canUndo());
        redoBtn.setEnabled(history.canRedo());
    }
}
