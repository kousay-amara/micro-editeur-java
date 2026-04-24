package command;

import model.Shape;
import ui.ToolbarPanel;

public class AddPrototypeCommand implements Command {
    private final ToolbarPanel toolbarPanel;
    private final Shape prototype;
    private int index = -1;

    public AddPrototypeCommand(ToolbarPanel toolbarPanel, Shape prototype) {
        this.toolbarPanel = toolbarPanel;
        this.prototype = prototype;
    }

    @Override
    public void execute() {
        if (index < 0) {
            index = toolbarPanel.getPrototypeCount();
        }
        toolbarPanel.addPrototypeDirect(prototype, index);
    }

    @Override
    public void undo() {
        int removedIndex = toolbarPanel.removePrototypeDirect(prototype);
        if (removedIndex >= 0) {
            index = removedIndex;
        }
    }

    @Override
    public String getDescription() {
        return "Ajouter prototype " + prototype.getType();
    }
}
