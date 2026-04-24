package command;

import model.Shape;
import ui.ToolbarPanel;

public class RemovePrototypeCommand implements Command {
    private final ToolbarPanel toolbarPanel;
    private final Shape prototype;
    private int index = -1;

    public RemovePrototypeCommand(ToolbarPanel toolbarPanel, Shape prototype) {
        this.toolbarPanel = toolbarPanel;
        this.prototype = prototype;
    }

    @Override
    public void execute() {
        int removedIndex = toolbarPanel.removePrototypeDirect(prototype);
        if (removedIndex >= 0) {
            index = removedIndex;
        }
    }

    @Override
    public void undo() {
        if (index >= 0) {
            toolbarPanel.addPrototypeDirect(prototype, index);
        }
    }

    @Override
    public String getDescription() {
        return "Supprimer prototype " + prototype.getType();
    }
}
