package command;

import model.Scene;
import model.Shape;
import model.Group;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GroupCommand implements Command {
    private final Scene scene;
    private final List<Shape> shapes;
    private final List<Integer> originalIndices;
    private final Group group;
    private final int groupIndex;

    public GroupCommand(Scene scene, List<Shape> shapes) {
        this.scene = scene;
        List<IndexedShape> indexedShapes = new ArrayList<>();
        List<Shape> sceneShapes = scene.getShapes();

        for (Shape shape : shapes) {
            int index = sceneShapes.indexOf(shape);
            if (index >= 0) {
                indexedShapes.add(new IndexedShape(shape, index));
            }
        }

        indexedShapes.sort(Comparator.comparingInt(indexedShape -> indexedShape.index));

        this.shapes = new ArrayList<>();
        this.originalIndices = new ArrayList<>();
        for (IndexedShape indexedShape : indexedShapes) {
            this.shapes.add(indexedShape.shape);
            this.originalIndices.add(indexedShape.index);
        }

        this.group = new Group();
        this.groupIndex = originalIndices.isEmpty() ? sceneShapes.size() : originalIndices.get(0);
    }

    @Override
    public void execute() {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            scene.removeShape(shape);
        }

        for (Shape shape : shapes) {
            group.add(shape);
        }
        scene.addShapeAt(groupIndex, group);
    }

    @Override
    public void undo() {
        scene.removeShape(group);

        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            group.remove(shape);
            scene.addShapeAt(originalIndices.get(i), shape);
        }
    }

    @Override
    public String getDescription() {
        return "Grouper " + shapes.size() + " formes";
    }

    private static final class IndexedShape {
        private final Shape shape;
        private final int index;

        private IndexedShape(Shape shape, int index) {
            this.shape = shape;
            this.index = index;
        }
    }
}
