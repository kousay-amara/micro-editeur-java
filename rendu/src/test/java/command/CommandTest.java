package command;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import model.Scene;
import model.Rectangle;
import model.RegularPolygon;
import model.Group;
import model.Shape;
import java.awt.Color;
import java.util.List;

/**
 * Command Pattern Tests - Minimal suite.
 * Focus: Undo/Redo mechanism (critical requirement).
 * Each command type tested once + one comprehensive undo/redo test.
 */
public class CommandTest {
    private Scene scene;
    private CommandHistory history;

    @Before
    public void setUp() {
        scene = new Scene();
        history = new CommandHistory();
    }

    // ========== Individual Command Tests ==========

    @Test
    public void testAddShapeCommand() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.BLUE);
        history.execute(new AddShapeCommand(scene, rect));
        assertEquals(1, scene.getShapes().size());
    }

    @Test
    public void testRemoveShapeCommand() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.BLUE);
        scene.addShape(rect);
        history.execute(new RemoveShapeCommand(scene, rect));
        assertEquals(0, scene.getShapes().size());
    }

    @Test
    public void testMoveShapeCommand() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.BLUE);
        scene.addShape(rect);
        history.execute(new MoveShapeCommand(rect, 50, 60));
        assertEquals(50, rect.getX());
        assertEquals(60, rect.getY());
    }

    @Test
    public void testGroupCommand() {
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Rectangle r2 = new Rectangle(50, 60, 80, 40, Color.RED);
        scene.addShape(r1);
        scene.addShape(r2);

        history.execute(new GroupCommand(scene, List.of(r1, r2)));
        assertEquals(1, scene.getShapes().size());
        assertTrue(scene.getShapes().get(0).isGroup());
    }

    @Test
    public void testGroupCommandPreservesSceneOrderOnUndo() {
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Rectangle r2 = new Rectangle(50, 60, 80, 40, Color.RED);
        Rectangle r3 = new Rectangle(90, 100, 60, 30, Color.GREEN);
        scene.addShape(r1);
        scene.addShape(r2);
        scene.addShape(r3);

        history.execute(new GroupCommand(scene, List.of(r3, r1)));

        assertEquals(2, scene.getShapes().size());
        assertTrue(scene.getShapes().get(0).isGroup());
        Group group = (Group) scene.getShapes().get(0);
        assertEquals(List.of(r1, r3), group.getChildren());

        history.undo();

        assertEquals(List.of(r1, r2, r3), scene.getShapes());
    }

    @Test
    public void testUngroupCommand() {
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Rectangle r2 = new Rectangle(50, 60, 80, 40, Color.RED);
        Group group = new Group();
        group.add(r1);
        group.add(r2);
        scene.addShape(group);

        history.execute(new UngroupCommand(scene, group));
        assertEquals(2, scene.getShapes().size());
    }

    @Test
    public void testEditShapeCommand() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.BLUE);
        scene.addShape(rect);

        ShapeMemento before = new ShapeMemento(rect);
        rect.setWidth(200);
        ShapeMemento after = new ShapeMemento(rect);

        EditShapeCommand cmd = EditShapeCommand.createForExecution(rect, before, after);
        history.execute(cmd);
        assertEquals(200, rect.getWidth());
    }

    // ========== COMPREHENSIVE UNDO/REDO TEST ==========

    @Test
    public void testCompleteUndoRedoSequence() {
        // Setup: Add 2 shapes
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Rectangle r2 = new Rectangle(50, 60, 80, 40, Color.RED);
        history.execute(new AddShapeCommand(scene, r1));
        history.execute(new AddShapeCommand(scene, r2));
        assertEquals(2, scene.getShapes().size());

        // Group them
        history.execute(new GroupCommand(scene, List.of(r1, r2)));
        assertEquals(1, scene.getShapes().size());
        assertTrue(scene.getShapes().get(0).isGroup());

        // Move the group
        Group group = (Group) scene.getShapes().get(0);
        history.execute(new MoveShapeCommand(group, 100, 100));
        assertEquals(100, group.getX());

        // === UNDO ALL ===
        history.undo(); // Undo move
        assertEquals(10, r1.getX()); // Back to original position

        history.undo(); // Undo group
        assertEquals(2, scene.getShapes().size()); // Back to 2 separate shapes

        history.undo(); // Undo add r2
        assertEquals(1, scene.getShapes().size());

        history.undo(); // Undo add r1
        assertEquals(0, scene.getShapes().size());

        // === REDO ALL ===
        history.redo(); // Re-add r1
        assertEquals(1, scene.getShapes().size());

        history.redo(); // Re-add r2
        assertEquals(2, scene.getShapes().size());

        history.redo(); // Re-group
        assertEquals(1, scene.getShapes().size());
        assertTrue(scene.getShapes().get(0).isGroup());

        history.redo(); // Re-move
        group = (Group) scene.getShapes().get(0);
        assertEquals(100, group.getX());
    }
}
