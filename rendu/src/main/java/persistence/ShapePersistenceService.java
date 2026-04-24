package persistence;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import model.Group;
import model.Rectangle;
import model.RegularPolygon;
import model.Shape;
import model.ShapeLeaf;

public class ShapePersistenceService {
    public void saveShapes(List<Shape> shapes, File file) throws IOException {
        if (file == null) {
            throw new IOException("Aucun fichier de sauvegarde n'a ete fourni.");
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Impossible de creer le dossier de sauvegarde.");
        }

        ArrayList<ShapeData> data = new ArrayList<>();
        for (Shape shape : shapes) {
            data.add(toData(shape));
        }

        try (ObjectOutputStream output = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            output.writeObject(data);
        }
    }

    public List<Shape> loadShapes(File file) throws IOException {
        if (file == null || !file.isFile()) {
            throw new IOException("Le fichier selectionne est introuvable.");
        }

        Object payload;
        try (ObjectInputStream input = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            payload = input.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("Format de sauvegarde invalide.", e);
        }

        if (!(payload instanceof List<?>)) {
            throw new IOException("Format de sauvegarde invalide.");
        }

        List<?> rawItems = (List<?>) payload;
        ArrayList<Shape> shapes = new ArrayList<>();
        for (Object rawItem : rawItems) {
            if (!(rawItem instanceof ShapeData)) {
                throw new IOException("Format de sauvegarde invalide.");
            }
            shapes.add(fromData((ShapeData) rawItem));
        }
        return shapes;
    }

    private ShapeData toData(Shape shape) throws IOException {
        ShapeData data = new ShapeData();
        data.type = shape.getType();
        data.rotation = shape.getRotation();

        if (shape instanceof Group) {
            for (Shape child : shape.getChildren()) {
                data.children.add(toData(child));
            }
            return data;
        }

        if (!(shape instanceof ShapeLeaf)) {
            throw new IOException("Type de forme non supporte: " + shape.getClass().getName());
        }

        ShapeLeaf leaf = (ShapeLeaf) shape;
        data.x = leaf.getX();
        data.y = leaf.getY();
        data.rotationCenterX = leaf.getRotationCenterX();
        data.rotationCenterY = leaf.getRotationCenterY();

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            data.colorRgb = rectangle.getColor().getRGB();
            data.width = rectangle.getWidth();
            data.height = rectangle.getHeight();
            data.cornerRadius = rectangle.getCornerRadius();
            return data;
        }

        if (shape instanceof RegularPolygon) {
            RegularPolygon polygon = (RegularPolygon) shape;
            data.colorRgb = polygon.getColor().getRGB();
            data.sides = polygon.getSides();
            data.sideLength = polygon.getSideLength();
            return data;
        }

        throw new IOException("Type de forme non supporte: " + shape.getClass().getName());
    }

    private Shape fromData(ShapeData data) throws IOException {
        if (data == null || data.type == null) {
            throw new IOException("Forme invalide dans la sauvegarde.");
        }

        if ("Group".equals(data.type)) {
            Group group = new Group();
            for (ShapeData child : data.children) {
                group.add(fromData(child));
            }
            group.setRotation(data.rotation);
            return group;
        }

        if ("Rectangle".equals(data.type)) {
            Rectangle rectangle = new Rectangle(
                    data.x, data.y, data.width, data.height,
                    toColor(data.colorRgb));
            rectangle.setCornerRadius(data.cornerRadius);
            rectangle.setRotation(data.rotation);
            rectangle.setRotationCenter(data.rotationCenterX, data.rotationCenterY);
            return rectangle;
        }

        if ("Polygon".equals(data.type)) {
            RegularPolygon polygon = new RegularPolygon(
                    data.x, data.y, data.sides, data.sideLength,
                    toColor(data.colorRgb));
            polygon.setRotation(data.rotation);
            polygon.setRotationCenter(data.rotationCenterX, data.rotationCenterY);
            return polygon;
        }

        throw new IOException("Type de forme inconnu dans la sauvegarde: " + data.type);
    }

    private Color toColor(int rgb) {
        return new Color(rgb, true);
    }
}
