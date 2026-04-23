package model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.awt.Color;

/**
 * Model Tests - Minimal suite.
 * Tests shape creation, movement, and grouping.
 * Excludes: graphical rendering (per specification).
 */
public class ModelTest {
    private Scene scene;

    @Before
    public void setUp() {
        scene = new Scene();
    }

    // ========== Rectangle Tests ==========

    @Test
    public void testRectangleCreationAndMovement() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.BLUE);
        assertEquals(10, rect.getX());
        assertEquals(20, rect.getY());

        rect.move(30, 40);
        assertEquals(40, rect.getX());
        assertEquals(60, rect.getY());
    }

    @Test
    public void testRectangleClone() {
        Rectangle original = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Shape cloned = original.clone();

        assertTrue(cloned instanceof Rectangle);
        Rectangle rect = (Rectangle) cloned;
        assertEquals(original.getWidth(), rect.getWidth());
        assertEquals(original.getHeight(), rect.getHeight());
    }

    // ========== RegularPolygon Tests ==========

    @Test
    public void testPolygonCreationAndMovement() {
        RegularPolygon poly = new RegularPolygon(10, 20, 6, 50, Color.RED);
        assertEquals(6, poly.getSides());
        assertEquals(50, poly.getSideLength());

        poly.move(20, 30);
        assertEquals(30, poly.getX());
        assertEquals(50, poly.getY());
    }

    @Test
    public void testPolygonClone() {
        RegularPolygon original = new RegularPolygon(10, 20, 6, 50, Color.RED);
        Shape cloned = original.clone();

        assertTrue(cloned instanceof RegularPolygon);
        RegularPolygon poly = (RegularPolygon) cloned;
        assertEquals(6, poly.getSides());
    }

    // ========== Scene Management ==========

    @Test
    public void testSceneAddRemoveShapes() {
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Rectangle r2 = new Rectangle(50, 60, 80, 40, Color.RED);

        scene.addShape(r1);
        scene.addShape(r2);
        assertEquals(2, scene.getShapes().size());

        scene.removeShape(r1);
        assertEquals(1, scene.getShapes().size());
        assertTrue(scene.getShapes().contains(r2));
    }

    @Test
    public void testSceneSelection() {
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Rectangle r2 = new Rectangle(50, 60, 80, 40, Color.RED);

        scene.addShape(r1);
        scene.addShape(r2);

        scene.addSelectedShape(r1);
        scene.addSelectedShape(r2);

        assertEquals(2, scene.getSelectedShapes().size());
        assertTrue(scene.isShapeSelected(r1));
        assertTrue(scene.isShapeSelected(r2));
    }

    // ========== Group Tests (Composite Pattern) ==========

    @Test
    public void testGroupCreationAndHierarchy() {
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Rectangle r2 = new Rectangle(50, 60, 80, 40, Color.RED);

        Group group = new Group();
        group.add(r1);
        group.add(r2);

        assertEquals(2, group.getChildren().size());
        assertTrue(group.isGroup());
    }

    @Test
    public void testGroupMovement() {
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Rectangle r2 = new Rectangle(50, 60, 80, 40, Color.RED);

        Group group = new Group();
        group.add(r1);
        group.add(r2);

        int originalX1 = r1.getX();
        group.move(30, 40);

        // All children moved by same amount
        assertEquals(originalX1 + 30, r1.getX());
    }

    @Test
    public void testNestedGroups() {
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Rectangle r2 = new Rectangle(50, 60, 80, 40, Color.RED);

        Group inner = new Group();
        inner.add(r1);
        inner.add(r2);

        Group outer = new Group();
        outer.add(inner);

        assertEquals(1, outer.getChildren().size());
        assertTrue(outer.getChildren().get(0).isGroup());
        Group retrieved = (Group) outer.getChildren().get(0);
        assertEquals(2, retrieved.getChildren().size());
    }

    @Test
    public void testGroupClone() {
        Rectangle r1 = new Rectangle(10, 20, 100, 50, Color.BLUE);
        Group original = new Group();
        original.add(r1);

        Shape cloned = original.clone();
        assertTrue(cloned instanceof Group);

        Group group = (Group) cloned;
        assertEquals(1, group.getChildren().size());
    }
}
