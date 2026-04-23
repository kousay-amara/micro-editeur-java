package command;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {
    private List<Command> undoStack;
    private List<Command> redoStack;
    
    public CommandHistory() {
        this.undoStack = new ArrayList<>();
        this.redoStack = new ArrayList<>();
    }
    
    public void execute(Command command) {
        command.execute();
        undoStack.add(command);
        redoStack.clear();
    }
    
    // Pour EditShapeCommand: les changements sont déjà appliqués, juste ajouter à history
    public void addToHistory(Command command) {
        undoStack.add(command);
        redoStack.clear();
    }
    
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.remove(undoStack.size() - 1);
            command.undo();
            redoStack.add(command);
        }
    }
    
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.remove(redoStack.size() - 1);
            command.execute();
            undoStack.add(command);
        }
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

}
