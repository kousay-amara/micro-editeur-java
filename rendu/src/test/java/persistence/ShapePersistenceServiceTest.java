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
        Path tmp = Files.createTempFile("shapes", ".dat");
        Rectangle rect = new Rectangle(120, 90, 140, 80, Color.BLUE);
        rect.setCornerRadius(16);
        rect.setRotation(22.5);
        RegularPolygon poly = new RegularPolygon(260, 180, 5, 70, Color.RED);
        poly.setRotation(45);
        try {
            service.saveShapes(List.of(rect, poly), tmp.toFile());
            List<Shape> shapes = service.loadShapes(tmp.toFile());

            assertEquals(2, shapes.size());

            Rectangle r = (Rectangle) shapes.get(0);
            assertEquals(120, r.getX());
            assertEquals(90, r.getY());
            assertEquals(140, r.getWidth());
            assertEquals(80, r.getHeight());
            assertEquals(16, r.getCornerRadius());
            assertEquals(22.5, r.getRotation(), 0.001);
            assertEquals(Color.BLUE.getRGB(), r.getColor().getRGB());

            RegularPolygon p = (RegularPolygon) shapes.get(1);
            assertEquals(260, p.getX());
            assertEquals(180, p.getY());
            assertEquals(5, p.getSides());
            assertEquals(70, p.getSideLength());
            assertEquals(45, p.getRotation(), 0.001);
            assertEquals(Color.RED.getRGB(), p.getColor().getRGB());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void testSaveAndLoadNestedGroups() throws IOException {
        Path tmp = Files.createTempFile("groups", ".dat");
        Group inner = new Group();
        inner.add(new Rectangle(60, 50, 90, 40, Color.GREEN));
        inner.add(new RegularPolygon(130, 120, 6, 40, Color.ORANGE));
        inner.setRotation(15);
        Group outer = new Group();
        outer.add(inner);
        outer.setRotation(60);
        try {
            service.saveShapes(List.of(outer), tmp.toFile());
            List<Shape> shapes = service.loadShapes(tmp.toFile());

            assertEquals(1, shapes.size());
            assertTrue(shapes.get(0) instanceof Group);

            Group loadedOuter = (Group) shapes.get(0);
            assertEquals(60, loadedOuter.getRotation(), 0.001);
            assertEquals(1, loadedOuter.getChildren().size());

            Group loadedInner = (Group) loadedOuter.getChildren().get(0);
            assertEquals(15, loadedInner.getRotation(), 0.001);
            assertEquals(2, loadedInner.getChildren().size());
            assertTrue(loadedInner.getChildren().get(0) instanceof Rectangle);
            assertTrue(loadedInner.getChildren().get(1) instanceof RegularPolygon);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
