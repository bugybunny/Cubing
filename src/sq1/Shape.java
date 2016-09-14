package sq1;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

/**
 * Represents a Square-1 shape with letters 'c' for corner and 'e' for edge in
 * ccw direction.
 * 
 * Example: eeeeeecececccccc results in top='eeeeeecece' and bottom='cccccc'.
 * Calling {@link #mirror()} in the full shape to be 'cccccceeeeeecece'.
 * 
 * @author <marco.syfrig@gmail.com>
 */
public class Shape {
    public static final Path SHAPE_FILE = Paths.get("shapes_from_website_plus_mirrors.txt");

    public final String top;
    public final String bottom;
    /**
     * Alg that solves this shape into cubeshape. Shape has to be aligned
     * correctly for this which is not considered in this alg.
     */
    private Algorithm cubeshapeAlg;

    public static final int EDGE_PORTION = 1;
    public static final int CORNER_PORTION = 2;
    public static final int TOTAL_PARTS_PER_SIDE = 12;

    protected Shape(String top, String bottom) {
        this(top, bottom, Algorithm.EMPTY_ALGORITHM);
    }

    protected Shape(String top, String bottom, Algorithm cubeshapeAlg) {
        this.top = top;
        this.bottom = bottom;
        this.cubeshapeAlg = cubeshapeAlg;
    }

    public Shape(String shape) {
        this(shape, Algorithm.EMPTY_ALGORITHM);
    }

    public Shape(String shape, Algorithm cubeshapeAlg) {
        if (shape.length() != 16) {
            throw new InvalidParameterException(
                    "Must be exactly 16 characters long and contain only 'c' and 'e', case does not matter.");
        }

        int middle = getTopBottomBreakIndex(shape.toLowerCase());

        top = shape.substring(0, middle);
        bottom = shape.substring(middle);
        this.cubeshapeAlg = cubeshapeAlg;
    }

    private static int getTopBottomBreakIndex(String shape) {
        int remainingTopParts = TOTAL_PARTS_PER_SIDE;
        int i = 0;
        while (i < shape.length()) {
            char piece = shape.charAt(i);
            if (piece == 'c') {
                remainingTopParts -= CORNER_PORTION;
            } else if (piece == 'e') {
                remainingTopParts -= EDGE_PORTION;
            } else {
                throw new InvalidParameterException(
                        "Shape description must only contain 'c' and 'e' but contained '" + piece + "'.");
            }

            if (remainingTopParts <= 0) {
                if (remainingTopParts < 0) {
                    throw new InvalidParameterException("Impossible shape!");
                }
                break;
            }
            i++;
        }

        return i + 1;
    }

    public boolean isSameShape(Shape other) {
        return isSameShape(other, false);
    }

    public boolean isSameShape(Shape other, boolean considerMirror) {
        boolean isSame = isRotation(top, other.top) && isRotation(bottom, other.bottom);

        if (considerMirror && !isSame) {
            isSame = isRotation(top, other.bottom) && isRotation(bottom, other.top);
        }
        return isSame;
    }

    private static boolean isRotation(String s1, String s2) {
        return (s1.length() == s2.length()) && ((s1 + s1).contains(s2));
    }

    public Shape mirror() {
        return new Shape(bottom, top, cubeshapeAlg.mirror());
    }

    public Algorithm getCubeshapeAlg() {
        return cubeshapeAlg;
    }

    public void setCubeshapeAlg(Algorithm cubeshapeAlg) {
        this.cubeshapeAlg = cubeshapeAlg;
    }

    public String getShapeString() {
        return top + bottom;
    }

    @Override
    public String toString() {
        return getShapeString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bottom == null) ? 0 : bottom.hashCode());
        result = prime * result + ((top == null) ? 0 : top.hashCode());
        return result;
    }

    /**
     * Rotated shapes are considered equal, mirrored are not. If it is a string
     * that represents the same shape (again, rotated is ok but not mirrored) it
     * is also considered equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof String && isSameShape(new Shape((String) obj))) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Shape other = (Shape) obj;
        return isSameShape(other);
    }
}
