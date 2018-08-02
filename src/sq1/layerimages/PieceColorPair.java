package sq1.layerimages;

import java.util.List;

import javafx.scene.paint.Color;

/**
 * Represents a string of the form <code>UBR={red,green}</code>. It parses the representing string, checks for errors
 * and if present throws a {@link PieceToColorParseException} and if everything is correct, sets the colors in the
 * {@link Piece}.
 * 
 * Checks if
 * <ul>
 * <li>exactly one equals sign ({@literal =}) is present</li>
 * <li>the first part (before the equal sign) is a valid {@link Piece}</li>
 * <li>all entries in the curly braces are comma separated and represent a valid {@link ColorScheme#COLORS}
 * </ul>
 */
public class PieceColorPair {
    private Piece piece;

    private String positionText;
    private String[] colorStrings;

    public PieceColorPair(String input) {
        String[] positionSplits = input.trim().split("=");
        if (positionSplits.length == 2) {
            // cut away the first letter since we do not want to differ between U and D
            positionText = positionSplits[0].trim().toUpperCase();
            colorStrings = getColorStrings(positionSplits[1]);
        }
    }

    public void associateColors(List<Piece> pieces) throws PieceToColorParseException {
        piece = parseAndGetPiece(positionText, pieces);
        if (piece != null) {
            parseAndSetColors(colorStrings, piece);
        }
    }

    public Piece getPiece() {
        return piece;
    }

    private static Piece parseAndGetPiece(String input, List<Piece> availablePieces) throws PieceToColorParseException {
        Piece piece = null;

        if (input.length() >= 2) {
            String positionWithoutLayer = input.substring(1).toUpperCase();

            Piece.Layer readLayer;
            Piece.Position readPosition;
            try {
                readLayer = Piece.Layer.valueOf(input.substring(0, 1));
                readPosition = Piece.Position.valueOf(positionWithoutLayer);
            } catch (Exception e) {
                throw new PieceToColorParseException(input + " is not a valid piece.");
            }

            if (availablePieces != null) {
                piece = availablePieces.get(readPosition.ordinal());
            }
            // ignore piece from other layer
            if (piece != null && piece.layer != readLayer) {
                piece = null;
            }

        } else {
            throw new PieceToColorParseException(input + " is not a valid piece.");
        }

        return piece;
    }

    public void reverseColors() {
        if (colorStrings.length > 1) {
            String temp = colorStrings[0];
            colorStrings[0] = colorStrings[1];
            colorStrings[1] = temp;
        }
    }

    public void toggleLayer() {
        char layerChar = positionText.charAt(0);
        if (layerChar == 'D') {
            positionText = 'U' + positionText.substring(1);
        } else if (layerChar == 'U') {
            positionText = 'D' + positionText.substring(1);
        }
    }

    private static String[] getColorStrings(String input) {
        String colorUnionText = input.trim();
        String colorText = colorUnionText.substring(1, colorUnionText.length() - 1);
        String[] colorSplits = colorText.split(",");
        for (int i = 0; i < colorSplits.length; i++) {
            colorSplits[i] = colorSplits[i].trim();
        }

        return colorSplits;
    }

    private static void parseAndSetColors(String[] colors, Piece piece) throws PieceToColorParseException {
        if (colors == null || colors.length == 0) {
            throw new PieceToColorParseException(piece.toString() + " should contain at least 1 color");
        } else {
            Color firstColor = getSideColorFrom(colors[0]);
            Color secondColor = null;

            if (colors.length == 1) {
                if (!piece.pos.isEdge) {
                    throw new PieceToColorParseException(piece.toString() + " contained only " + colors.length + " color but should contain exactly 2.");
                }
            } else if (colors.length > 1) {
                secondColor = getSideColorFrom(colors[1]);
                if (piece.pos.isEdge) {
                    Toast.makeText("Warning: All colors for " + piece.toString() + " after the first one are ignored as this is an edge.");
                } else if (colors.length > 2) {
                    Toast.makeText("Warning: All colors for corner " + piece.toString() + " after the first two are ignored.");
                }
            }
            setColors(piece, new Color[] { firstColor, secondColor });
        }
    }

    private static void setColors(Piece piece, Color[] colors) {
        piece.setFirstSideColors(colors[0]);
        piece.setSecondSideColor(colors[1]);
        piece.setTopColor(ColorScheme.WHITE);
    }

    private static Color getSideColorFrom(String colorText) throws PieceToColorParseException {
        Color color = ColorScheme.COLORS.get(colorText.trim().toLowerCase());

        if (color == null) {
            throw new PieceToColorParseException(colorText + " is not a valid side color, only \"blue, red, green, orange\" are allowed (case insensitive).");
        }

        return color;
    }

    @Override
    public String toString() {
        if (colorStrings.length == 1) {
            return positionText + "={" + colorStrings[0] + "}";
        } else if (colorStrings.length > 1) {
            return positionText + "={" + colorStrings[0] + ", " + colorStrings[1] + "}";
        }
        return super.toString();
    }
}
