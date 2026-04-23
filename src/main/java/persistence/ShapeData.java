package persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

final class ShapeData implements Serializable {
    private static final long serialVersionUID = 1L;

    String type;
    int x;
    int y;
    double rotation;
    int rotationCenterX;
    int rotationCenterY;
    int colorRgb;
    int width;
    int height;
    int cornerRadius;
    int sides;
    int sideLength;
    List<ShapeData> children = new ArrayList<>();
}
