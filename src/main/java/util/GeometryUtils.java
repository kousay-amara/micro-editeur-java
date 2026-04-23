package util;

/**
 * Centralizes geometry calculations used across rendering and bounds computation.
 * Eliminates duplication in AwtShapeRenderer, Group, and WhiteboardPanel.
 */
public final class GeometryUtils {

    public static int computeRadius(int sides, int sideLength) {
        return (int) (sideLength / (2 * Math.sin(Math.PI / sides)));
    }

    public static int rotatePointX(double pointX, double pointY, int pivotX, int pivotY, double rotation) {
        double angle = Math.toRadians(-rotation);
        double translatedX = pointX - pivotX;
        double translatedY = pointY - pivotY;
        return (int) Math.round(translatedX * Math.cos(angle) - translatedY * Math.sin(angle) + pivotX);
    }

    public static int rotatePointY(double pointX, double pointY, int pivotX, int pivotY, double rotation) {
        double angle = Math.toRadians(-rotation);
        double translatedX = pointX - pivotX;
        double translatedY = pointY - pivotY;
        return (int) Math.round(translatedX * Math.sin(angle) + translatedY * Math.cos(angle) + pivotY);
    }

    public static int[] getBounds(int[] xPoints, int[] yPoints) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int i = 0; i < xPoints.length; i++) {
            minX = Math.min(minX, xPoints[i]);
            minY = Math.min(minY, yPoints[i]);
            maxX = Math.max(maxX, xPoints[i]);
            maxY = Math.max(maxY, yPoints[i]);
        }
        return new int[] {minX, minY, maxX, maxY};
    }

    public static int[] getRotatedRectangleBounds(int x, int y, int width, int height, int pivotX, int pivotY, double rotation) {
        if (rotation == 0) {
            return new int[] {x, y, x + width, y + height};
        }

        double angle = Math.toRadians(-rotation);
        double[][] corners = {
                {x, y},
                {x + width, y},
                {x + width, y + height},
                {x, y + height}
        };

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (double[] corner : corners) {
            double translatedX = corner[0] - pivotX;
            double translatedY = corner[1] - pivotY;
            int rotatedX = (int) Math.round(translatedX * Math.cos(angle) - translatedY * Math.sin(angle) + pivotX);
            int rotatedY = (int) Math.round(translatedX * Math.sin(angle) + translatedY * Math.cos(angle) + pivotY);
            minX = Math.min(minX, rotatedX);
            minY = Math.min(minY, rotatedY);
            maxX = Math.max(maxX, rotatedX);
            maxY = Math.max(maxY, rotatedY);
        }

        return new int[] {minX, minY, maxX, maxY};
    }

    public static int scalePoint(int value, int center, double factor) {
        return center + (int) Math.round((value - center) * factor);
    }

    private GeometryUtils() {
        throw new AssertionError("Cannot instantiate GeometryUtils");
    }
}
