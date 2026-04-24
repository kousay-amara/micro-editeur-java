package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import model.Group;
import model.Rectangle;
import model.RegularPolygon;
import model.Shape;
import util.GeometryUtils;

/**
 * Centralizes every AWT drawing concern.
 */
public class AwtShapeRenderer implements ShapeRenderer {
    private static final int PREVIEW_MARGIN = 5;

    @Override
    public void render(Shape shape, Graphics graphics) {
        if (shape instanceof Group) {
            renderGroup((Group) shape, graphics);
        } else if (shape instanceof Rectangle) {
            renderRectangle((Rectangle) shape, graphics);
        } else if (shape instanceof RegularPolygon) {
            renderPolygon((RegularPolygon) shape, graphics);
        }
    }

    @Override
    public void renderPreview(Shape shape, Graphics graphics, int panelWidth, int panelHeight) {
        if (shape instanceof Rectangle) {
            renderRectanglePreview((Rectangle) shape, graphics, panelWidth, panelHeight);
        } else if (shape instanceof RegularPolygon) {
            renderPolygonPreview((RegularPolygon) shape, graphics, panelWidth, panelHeight);
        } else if (shape instanceof Group) {
            renderGroupPreview((Group) shape, graphics, panelWidth, panelHeight);
        }
    }

    private void renderGroup(Group group, Graphics graphics) {
        for (Shape child : group.getChildren()) {
            render(child, graphics);
        }
    }

    private void renderRectangle(Rectangle rectangle, Graphics graphics) {
        int rx = rectangle.getX() - rectangle.getWidth() / 2;
        int ry = rectangle.getY() - rectangle.getHeight() / 2;
        Color drawColor = rectangle.isSelected() ? lighten(rectangle.getColor()) : rectangle.getColor();
        Graphics2D g2 = (Graphics2D) graphics.create();
        rotateAroundPoint(g2, rectangle.getRotationCenterX(), rectangle.getRotationCenterY(), rectangle.getRotation());
        g2.setColor(drawColor);
        g2.fillRoundRect(rx, ry, rectangle.getWidth(), rectangle.getHeight(),
                rectangle.getCornerRadius(), rectangle.getCornerRadius());
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(rx, ry, rectangle.getWidth(), rectangle.getHeight(),
                rectangle.getCornerRadius(), rectangle.getCornerRadius());
        g2.dispose();
    }

    private void renderPolygon(RegularPolygon polygon, Graphics graphics) {
        int radius = GeometryUtils.computeRadius(polygon.getSides(), polygon.getSideLength());
        int[] xPoints = buildPolygonXPoints(
                polygon.getX(),
                polygon.getY(),
                polygon.getRotationCenterX(),
                polygon.getRotationCenterY(),
                polygon.getSides(),
                radius,
                polygon.getRotation());
        int[] yPoints = buildPolygonYPoints(
                polygon.getX(),
                polygon.getY(),
                polygon.getRotationCenterX(),
                polygon.getRotationCenterY(),
                polygon.getSides(),
                radius,
                polygon.getRotation());
        Color drawColor = polygon.isSelected() ? lighten(polygon.getColor()) : polygon.getColor();
        graphics.setColor(drawColor);
        graphics.fillPolygon(xPoints, yPoints, polygon.getSides());
        graphics.setColor(Color.BLACK);
        graphics.drawPolygon(xPoints, yPoints, polygon.getSides());
    }

    private void renderRectanglePreview(Rectangle rectangle, Graphics graphics, int panelWidth, int panelHeight) {
        int cornerRadius = rectangle.getCornerRadius() > 0 ? 4 : 0;
        int width = panelWidth - 2 * PREVIEW_MARGIN;
        int height = panelHeight - 2 * PREVIEW_MARGIN;
        Graphics2D g2 = (Graphics2D) graphics.create();
        double scaleX = rectangle.getWidth() == 0 ? 1.0 : (double) width / rectangle.getWidth();
        double scaleY = rectangle.getHeight() == 0 ? 1.0 : (double) height / rectangle.getHeight();
        int left = rectangle.getX() - rectangle.getWidth() / 2;
        int top  = rectangle.getY() - rectangle.getHeight() / 2;
        double pivotX = PREVIEW_MARGIN + (rectangle.getRotationCenterX() - left) * scaleX;
        double pivotY = PREVIEW_MARGIN + (rectangle.getRotationCenterY() - top)  * scaleY;
        rotateAroundPoint(g2, pivotX, pivotY, rectangle.getRotation());
        g2.setColor(rectangle.getColor());
        g2.fillRoundRect(
                PREVIEW_MARGIN,
                PREVIEW_MARGIN,
                width,
                height,
                cornerRadius,
                cornerRadius);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(
                PREVIEW_MARGIN,
                PREVIEW_MARGIN,
                width,
                height,
                cornerRadius,
                cornerRadius);
        g2.dispose();
    }

    private void renderPolygonPreview(RegularPolygon polygon, Graphics graphics, int panelWidth, int panelHeight) {
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;
        int radius = Math.min(centerX, centerY) - PREVIEW_MARGIN;
        int actualRadius = Math.max(1, GeometryUtils.computeRadius(polygon.getSides(), polygon.getSideLength()));
        double scale = (double) radius / actualRadius;
        int pivotX = centerX + (int) Math.round((polygon.getRotationCenterX() - polygon.getX()) * scale);
        int pivotY = centerY + (int) Math.round((polygon.getRotationCenterY() - polygon.getY()) * scale);
        int[] xPoints = buildPolygonXPoints(centerX, centerY, pivotX, pivotY, polygon.getSides(), radius, polygon.getRotation());
        int[] yPoints = buildPolygonYPoints(centerX, centerY, pivotX, pivotY, polygon.getSides(), radius, polygon.getRotation());
        graphics.setColor(polygon.getColor());
        graphics.fillPolygon(xPoints, yPoints, polygon.getSides());
        graphics.setColor(Color.BLACK);
        graphics.drawPolygon(xPoints, yPoints, polygon.getSides());
    }

    private void renderGroupPreview(Group group, Graphics graphics, int panelWidth, int panelHeight) {
        int[] bounds = getBounds(group);
        int contentWidth = Math.max(1, bounds[2] - bounds[0]);
        int contentHeight = Math.max(1, bounds[3] - bounds[1]);
        int availableWidth = Math.max(1, panelWidth - 2 * PREVIEW_MARGIN);
        int availableHeight = Math.max(1, panelHeight - 2 * PREVIEW_MARGIN);
        double scale = Math.min((double) availableWidth / contentWidth, (double) availableHeight / contentHeight);

        int scaledWidth = (int) Math.round(contentWidth * scale);
        int scaledHeight = (int) Math.round(contentHeight * scale);
        int originX = PREVIEW_MARGIN + (availableWidth - scaledWidth) / 2;
        int originY = PREVIEW_MARGIN + (availableHeight - scaledHeight) / 2;

        renderPreviewScaled(group, graphics, scale, originX, originY, bounds[0], bounds[1]);

        graphics.setColor(Color.BLACK);
        graphics.drawRect(
                PREVIEW_MARGIN,
                PREVIEW_MARGIN,
                panelWidth - 2 * PREVIEW_MARGIN,
                panelHeight - 2 * PREVIEW_MARGIN);
    }

    private void renderPreviewScaled(Shape shape, Graphics graphics, double scale, int originX, int originY, int minX, int minY) {
        if (shape instanceof Group) {
            for (Shape child : ((Group) shape).getChildren()) {
                renderPreviewScaled(child, graphics, scale, originX, originY, minX, minY);
            }
            return;
        }

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            int left = rectangle.getX() - rectangle.getWidth() / 2;
            int top  = rectangle.getY() - rectangle.getHeight() / 2;
            int x = originX + (int) Math.round((left - minX) * scale);
            int y = originY + (int) Math.round((top  - minY) * scale);
            int width = Math.max(1, (int) Math.round(rectangle.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(rectangle.getHeight() * scale));
            int radius = Math.max(0, (int) Math.round(rectangle.getCornerRadius() * scale));

            Graphics2D g2 = (Graphics2D) graphics.create();
            double pivotX = originX + (rectangle.getRotationCenterX() - minX) * scale;
            double pivotY = originY + (rectangle.getRotationCenterY() - minY) * scale;
            rotateAroundPoint(g2, pivotX, pivotY, rectangle.getRotation());
            g2.setColor(rectangle.getColor());
            g2.fillRoundRect(x, y, width, height, radius, radius);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, width, height, radius, radius);
            g2.dispose();
            return;
        }

        if (shape instanceof RegularPolygon) {
            RegularPolygon polygon = (RegularPolygon) shape;
            int centerX = originX + (int) Math.round((polygon.getX() - minX) * scale);
            int centerY = originY + (int) Math.round((polygon.getY() - minY) * scale);
            int radius = Math.max(1, (int) Math.round(GeometryUtils.computeRadius(polygon.getSides(), polygon.getSideLength()) * scale));
            int pivotX = originX + (int) Math.round((polygon.getRotationCenterX() - minX) * scale);
            int pivotY = originY + (int) Math.round((polygon.getRotationCenterY() - minY) * scale);
            int[] xPoints = buildPolygonXPoints(centerX, centerY, pivotX, pivotY, polygon.getSides(), radius, polygon.getRotation());
            int[] yPoints = buildPolygonYPoints(centerX, centerY, pivotX, pivotY, polygon.getSides(), radius, polygon.getRotation());

            graphics.setColor(polygon.getColor());
            graphics.fillPolygon(xPoints, yPoints, polygon.getSides());
            graphics.setColor(Color.BLACK);
            graphics.drawPolygon(xPoints, yPoints, polygon.getSides());
        }
    }

    private int[] getBounds(Shape shape) {
        if (shape instanceof Group) {
            Group group = (Group) shape;
            if (group.getChildren().isEmpty()) {
                return new int[] {0, 0, 1, 1};
            }

            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;

            for (Shape child : group.getChildren()) {
                int[] childBounds = getBounds(child);
                minX = Math.min(minX, childBounds[0]);
                minY = Math.min(minY, childBounds[1]);
                maxX = Math.max(maxX, childBounds[2]);
                maxY = Math.max(maxY, childBounds[3]);
            }
            return new int[] {minX, minY, maxX, maxY};
        }

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            return GeometryUtils.getRotatedRectangleBounds(
                    rectangle.getX() - rectangle.getWidth() / 2,
                    rectangle.getY() - rectangle.getHeight() / 2,
                    rectangle.getWidth(),
                    rectangle.getHeight(),
                    rectangle.getRotationCenterX(),
                    rectangle.getRotationCenterY(),
                    rectangle.getRotation());
        }

        if (shape instanceof RegularPolygon) {
            RegularPolygon polygon = (RegularPolygon) shape;
            int radius = GeometryUtils.computeRadius(polygon.getSides(), polygon.getSideLength());
            int[] xPoints = buildPolygonXPoints(
                    polygon.getX(),
                    polygon.getY(),
                    polygon.getRotationCenterX(),
                    polygon.getRotationCenterY(),
                    polygon.getSides(),
                    radius,
                    polygon.getRotation());
            int[] yPoints = buildPolygonYPoints(
                    polygon.getX(),
                    polygon.getY(),
                    polygon.getRotationCenterX(),
                    polygon.getRotationCenterY(),
                    polygon.getSides(),
                    radius,
                    polygon.getRotation());
            return GeometryUtils.getBounds(xPoints, yPoints);
        }

        return new int[] {shape.getX(), shape.getY(), shape.getX() + 1, shape.getY() + 1};
    }


    private int[] buildPolygonXPoints(int centerX, int centerY, int pivotX, int pivotY, int sides, int radius, double rotation) {
        int[] points = new int[sides];
        for (int i = 0; i < sides; i++) {
            double baseAngle = Math.toRadians(90) + 2 * Math.PI * i / sides;
            double pointX = centerX + radius * Math.cos(baseAngle);
            double pointY = centerY + radius * Math.sin(baseAngle);
            points[i] = GeometryUtils.rotatePointX(pointX, pointY, pivotX, pivotY, rotation);
        }
        return points;
    }

    private int[] buildPolygonYPoints(int centerX, int centerY, int pivotX, int pivotY, int sides, int radius, double rotation) {
        int[] points = new int[sides];
        for (int i = 0; i < sides; i++) {
            double baseAngle = Math.toRadians(90) + 2 * Math.PI * i / sides;
            double pointX = centerX + radius * Math.cos(baseAngle);
            double pointY = centerY + radius * Math.sin(baseAngle);
            points[i] = GeometryUtils.rotatePointY(pointX, pointY, pivotX, pivotY, rotation);
        }
        return points;
    }



    private void rotateAroundPoint(Graphics2D graphics, double pivotX, double pivotY, double rotation) {
        if (rotation == 0) {
            return;
        }
        graphics.transform(AffineTransform.getRotateInstance(Math.toRadians(-rotation), pivotX, pivotY));
    }

    private Color lighten(Color color) {
        int red = (int) (color.getRed() + (255 - color.getRed()) * 0.5f);
        int green = (int) (color.getGreen() + (255 - color.getGreen()) * 0.5f);
        int blue = (int) (color.getBlue() + (255 - color.getBlue()) * 0.5f);
        return new Color(red, green, blue);
    }
}
