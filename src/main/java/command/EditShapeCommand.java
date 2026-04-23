package command;

import model.Shape;

/**
 * Command to edit a shape's properties.
 *
 * Usage:
 * - createForAlreadyExecuted(): Shape has been modified, changes already applied
 * - createForExecution(): Changes will be applied via execute()
 */
public class EditShapeCommand implements Command {
    private ShapeMemento originalState;
    private ShapeMemento newState;
    private Shape shape;
    private boolean alreadyExecuted;

    /**
     * Private constructor - use factory methods instead.
     */
    private EditShapeCommand(Shape shape, ShapeMemento originalState, ShapeMemento newState,
                            boolean alreadyExecuted) {
        this.shape = shape;
        this.originalState = originalState;
        this.newState = newState;
        this.alreadyExecuted = alreadyExecuted;
    }

    /**
     * Create a command where changes have ALREADY been applied to the shape.
     * Used by PropertyDialog (changes applied inline).
     */
    public static EditShapeCommand createForAlreadyExecuted(Shape shape,
                                                            ShapeMemento originalState,
                                                            ShapeMemento newState) {
        return new EditShapeCommand(shape, originalState, newState, true);
    }

    /**
     * Create a command where changes will be applied via execute().
     * Used for programmatic edits.
     */
    public static EditShapeCommand createForExecution(Shape shape,
                                                      ShapeMemento originalState,
                                                      ShapeMemento newState) {
        return new EditShapeCommand(shape, originalState, newState, false);
    }

    @Override
    public void execute() {
        if (!alreadyExecuted) {
            newState.restore();
        }
        alreadyExecuted = true;
    }

    @Override
    public void undo() {
        originalState.restore();
        alreadyExecuted = false;
    }

    @Override
    public String getDescription() {
        return "Éditer " + shape.getType();
    }

    public ShapeMemento getOriginalState() {
        return originalState;
    }

    public ShapeMemento getNewState() {
        return newState;
    }
}
