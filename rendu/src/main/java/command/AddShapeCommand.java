package command;

import model.Scene;
import model.Shape;

public class AddShapeCommand implements Command {
    private Scene scene;
    private Shape shape;
    
    public AddShapeCommand(Scene scene, Shape shape) {
        this.scene = scene;
        this.shape = shape;
    }
    
    @Override
    public void execute() {
        scene.addShape(shape);
    }
    
    @Override
    public void undo() {
        scene.removeShape(shape);
    }
    
    @Override
    public String getDescription() {
        return "Ajouter " + shape.getType();
    }
}
