package persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import model.Group;
import model.Rectangle;
import model.RegularPolygon;
import model.Shape;
import org.junit.Test;

public class ShapePersistenceServiceTest {
    private final ShapePersistenceService service = new ShapePersistenceService();

    @Test
    public void testSaveAndLoadLeafShapes() throws IOException {
        Path tempFile = Files.createTempFile("shapes", ".dat");

        Rectangle rectangle = new Rectangle(120, 90, 140, 80, Color.BLUE);
        rectangle.setCornerRadius(16);
        rectangle.setRotation(22.5);
        rectangle.setRotationCenter(140, 100);

        RegularPolygon polygon = new RegularPolygon(260, 180, 5, 70, Color.RED);
        polygon.setRotation(45);
        polygon.setRotationCenter(250, 170);

        try {
            service.saveShapes(List.of(rectangle, polygon), tempFile.toFile());
            List<Shape> loadedShapes = service.loadShapes(tempFile.toFile());

            assertEquals(2, loadedShapes.size());

            Rectangle loadedRectangle = (Rectangle) loadedShapes.get(0);
            assertEquals(rectangle.getX(), loadedRectangle.getX());
            assertEquals(rectangle.getY(), loadedRectangle.getY());
            assertEquals(rectangle.getWidth(), loadedRectangle.getWidth());
            assertEquals(rectangle.getHeight(), loadedRectangle.getHeight());
            assertEquals(rectangle.getCornerRadius(), loadedRectangle.getCornerRadius());
            assertEquals(rectangle.getRotation(), loadedRectangle.getRotation(), 0.001);
            assertEquals(rectangle.getRotationCenterX(), loadedRectangle.getRotationCenterX());
            assertEquals(rectangle.getRotationCenterY(), loadedRectangle.getRotationCenterY());
            assertEquals(rectangle.getColor().getRGB(), loadedRectangle.getColor().getRGB());

            RegularPolygon loadedPolygon = (RegularPolygon) loadedShapes.get(1);
            assertEquals(polygon.getX(), loadedPolygon.getX());
            assertEquals(polygon.getY(), loadedPolygon.getY());
            assertEquals(polygon.getSides(), loadedPolygon.getSides());
            assertEquals(polygon.getSideLength(), loadedPolygon.getSideLength());
            assertEquals(polygon.getRotation(), loadedPolygon.getRotation(), 0.001);
            assertEquals(polygon.getRotationCenterX(), loadedPolygon.getRotationCenterX());
            assertEquals(polygon.getRotationCenterY(), loadedPolygon.getRotationCenterY());
            assertEquals(polygon.getColor().getRGB(), loadedPolygon.getColor().getRGB());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testSaveAndLoadNestedGroups() throws IOException {
        Path tempFile = Files.createTempFile("groups", ".dat");

        Rectangle rectangle = new Rectangle(60, 50, 90, 40, Color.GREEN);
        rectangle.setRotation(10);
        rectangle.setRotationCenter(65, 55);

        RegularPolygon polygon = new RegularPolygon(130, 120, 6, 40, Color.ORANGE);
        polygon.setRotation(30);
        polygon.setRotationCenter(130, 120);

        Group innerGroup = new Group();
        innerGroup.add(rectangle);
        innerGroup.add(polygon);
        innerGroup.setRotation(15);

        Group outerGroup = new Group();
        outerGroup.add(innerGroup);
        outerGroup.setRotation(60);

        try {
            service.saveShapes(List.of(outerGroup), tempFile.toFile());
            List<Shape> loadedShapes = service.loadShapes(tempFile.toFile());

            assertEquals(1, loadedShapes.size());
            assertTrue(loadedShapes.get(0) instanceof Group);

            Group loadedOuterGroup = (Group) loadedShapes.get(0);
            assertEquals(60, loadedOuterGroup.getRotation(), 0.001);
            assertEquals(1, loadedOuterGroup.getChildren().size());
            assertTrue(loadedOuterGroup.getChildren().get(0) instanceof Group);

            Group loadedInnerGroup = (Group) loadedOuterGroup.getChildren().get(0);
            assertEquals(15, loadedInnerGroup.getRotation(), 0.001);
            assertEquals(2, loadedInnerGroup.getChildren().size());
            assertTrue(loadedInnerGroup.getChildren().get(0) instanceof Rectangle);
            assertTrue(loadedInnerGroup.getChildren().get(1) instanceof RegularPolygon);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
