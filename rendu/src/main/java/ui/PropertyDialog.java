package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import model.Shape;
import command.ShapeMemento;
import ui.strategy.PropertyEditorStrategy;
import ui.strategy.PropertyEditorFactory;

/**
 * Dialog for editing shape properties.
 * Uses PropertyEditorStrategy to eliminate duplication.
 */
public class PropertyDialog extends Dialog {
    private final Shape shape;
    private final ShapeMemento originalState;
    private final Runnable repaintCallback;
    private boolean applied = false;

    private PropertyEditorStrategy editor;
    private Panel contentPanel;

    public PropertyDialog(Frame parent, Shape shape, Runnable repaintCallback) {
        super(parent, "Edit Shape Properties", true);
        this.shape = shape;
        this.repaintCallback = repaintCallback;
        this.originalState = new ShapeMemento(shape);

        this.editor = PropertyEditorFactory.createEditor(shape);

        setLayout(new BorderLayout());
        contentPanel = new Panel();
        contentPanel.setLayout(new GridLayout(0, UIConstants.PROPERTY_DIALOG_COLS,
                                             UIConstants.PROPERTY_DIALOG_COL_GAP,
                                             UIConstants.PROPERTY_DIALOG_ROW_GAP));

        editor.setupFields(shape, contentPanel);

        add(contentPanel, BorderLayout.CENTER);

        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(createButton("Ok", true));
        buttonPanel.add(createButton("Appliquer", false));
        buttonPanel.add(createCancelButton());

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(UIConstants.PROPERTY_DIALOG_WIDTH, UIConstants.PROPERTY_DIALOG_HEIGHT);
        setLocationRelativeTo(parent);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelChanges();
            }
        });
    }

    private boolean applyChanges() {
        try {
            editor.applyChanges(shape);
            return true;
        } catch (NumberFormatException e) {
            showError("Valeur invalide. Veuillez entrer des nombres valides.");
            return false;
        }
    }

    private void showError(String message) {
        Dialog errorDialog = new Dialog(this, "Erreur d'entrée", true);
        errorDialog.setLayout(new BorderLayout(8, 8));
        errorDialog.add(new Label(message), BorderLayout.CENTER);
        Button ok = new Button("OK");
        ok.addActionListener(ae -> errorDialog.dispose());
        Panel p = new Panel();
        p.add(ok);
        errorDialog.add(p, BorderLayout.SOUTH);
        errorDialog.pack();
        errorDialog.setLocationRelativeTo(null);
        errorDialog.setVisible(true);
    }

    private Button createButton(String label, boolean closeOnClick) {
        Button button = new Button(label);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!applyChanges()) {
                    return;
                }
                applied = true;
                repaintIfNeeded();
                if (closeOnClick) {
                    setVisible(false);
                }
            }
        });
        return button;
    }

    private Button createCancelButton() {
        Button button = new Button("Annuler");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelChanges();
            }
        });
        return button;
    }

    private void cancelChanges() {
        originalState.restore();
        applied = false;
        repaintIfNeeded();
        setVisible(false);
    }

    private void repaintIfNeeded() {
        if (repaintCallback != null) {
            repaintCallback.run();
        }
    }

    public boolean wasApplied() {
        return applied;
    }

    public ShapeMemento getOriginalState() {
        return originalState;
    }
}
