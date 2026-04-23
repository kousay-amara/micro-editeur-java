package command;

import model.Shape;

public class MoveShapeCommand implements Command {
    private Shape shape;
    private int oldX, oldY;
    private int newX, newY;
    
    public MoveShapeCommand(Shape shape, int newX, int newY) {
        this.shape = shape;
        this.oldX = shape.getX();
        this.oldY = shape.getY();
        this.newX = newX;
        this.newY = newY;
    }
    
    @Override
    public void execute() {
        int dx = newX - oldX;
        int dy = newY - oldY;
        shape.move(dx, dy);
    }
    
    @Override
    public void undo() {
        int dx = oldX - newX;
        int dy = oldY - newY;
        shape.move(dx, dy);
    }
    
    @Override
    public String getDescription() {
        return "Déplacer " + shape.getType();
    }
}
