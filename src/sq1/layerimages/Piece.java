package sq1.layerimages;

import javafx.scene.paint.Color;

public class Piece {
    enum Position {
        BL, B(true), BR, R(true), FR, F(true), FL, L(true);

        boolean isEdge = false;

        Position() {
            this(false);
        }

        Position(boolean isEdge) {
            this.isEdge = isEdge;
        }
    }

    enum Layer {
        U, D;
    }

    /** xs.length == ys.length */
    public final double[] xs;
    /** xs.length == ys.length */
    public final double[] ys;
    private double[] pointsInOrder;

    public final Layer layer;
    public final Position pos;

    private Color topColor = ColorScheme.GRAY;
    private Color[] sideColors = { ColorScheme.GRAY, ColorScheme.GRAY };

    public Piece(double[] xs, double[] ys, Layer layer, Position pos) {
        this.xs = xs;
        this.ys = ys;
        this.layer = layer;
        this.pos = pos;
    }

    public double[] getPointsInOrder() {
        if (pointsInOrder == null) {
            pointsInOrder = new double[xs.length + ys.length];
            for (int i = 0; i < xs.length; i++) {
                pointsInOrder[i * 2] = xs[i];
                pointsInOrder[i * 2 + 1] = ys[i];
            }
        }

        return pointsInOrder;
    }

    public boolean isEdge() {
        return xs.length == 3;
    }

    public Color getTopColor() {
        return topColor;
    }

    public void setTopColor(Color topColor) {
        this.topColor = topColor;
    }

    public Color[] getSideColors() {
        return sideColors;
    }

    public void setFirstSideColors(Color firstSideColor) {
        sideColors[0] = getColorOrDefault(firstSideColor);
    }

    public void setSecondSideColor(Color secondSideColor) {
        sideColors[1] = getColorOrDefault(secondSideColor);
    }

    private static Color getColorOrDefault(Color color) {
        if (color == null) {
            color = ColorScheme.GRAY;
        }
        return color;
    }

    @Override
    public String toString() {
        return layer.toString() + pos.toString();
    }
}
