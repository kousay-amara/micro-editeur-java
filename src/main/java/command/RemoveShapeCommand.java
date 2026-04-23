package command;

import model.Scene;
import model.Shape;

public class RemoveShapeCommand implements Command {
    private Scene scene;
    private Shape shape;
    private int index;
    
    public RemoveShapeCommand(Scene scene, Shape shape) {
        this.scene = scene;
        this.shape = shape;
        this.index = scene.getShapes().indexOf(shape);
    }
    
    @Override
    public void execute() {
        scene.removeShape(shape);
    }
    
    @Override
    public void undo() {
        scene.addShapeAt(index, shape);
    }
    
    @Override
    public String getDescription() {
        return "Supprimer " + shape.getType();
    }
}
