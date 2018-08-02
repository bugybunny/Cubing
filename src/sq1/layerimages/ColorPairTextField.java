package sq1.layerimages;

import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class ColorPairTextField extends TextField {

    private static final KeyCombination reverseShortcut = new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination toggleLayerShortcut = new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN);

    public ColorPairTextField() {
    }

    public ColorPairTextField(String text) {
        super(text);

        addShortcuts();
    }

    private void addShortcuts() {
        addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (reverseShortcut.match(event)) {
                try {
                    IndexRange selectionBefore = getSelection();
                    PieceColorPair selectedPair = getSurroundingPair();
                    selectedPair.reverseColors();
                    replaceCurrentSelectionWith(selectedPair, selectionBefore);
                } catch (InvalidSelectionException e) {
                }
            } else if (toggleLayerShortcut.match(event)) {
                try {
                    IndexRange selectionBefore = getSelection();
                    PieceColorPair selectedPair = getSurroundingPair();
                    selectedPair.toggleLayer();
                    replaceCurrentSelectionWith(selectedPair, selectionBefore);
                } catch (InvalidSelectionException e) {
                }
            } else if (event.getCode().isDigitKey() && (event.isAltDown() || event.isMetaDown())) {
                IndexRange selectionBefore = getSelection();
                String replacingColor = ColorScheme.COLOR_INDICES.get(Integer.parseInt(event.getCode().getName()));
                replaceSurroundingColor(replacingColor, selectionBefore);
            }

        });
    }

    private PieceColorPair getSurroundingPair() throws InvalidSelectionException {
        selectSurroundingPairRange();
        return new PieceColorPair(getSelectedText());
    }

    private void replaceCurrentSelectionWith(PieceColorPair replacingPair, IndexRange selectionBefore) {
        String replacingText = replacingPair.toString();
        if (!isFirstPairSelected()) {
            replacingText = " " + replacingText;
        }
        replaceSelection(replacingText);
        restoreSelection(selectionBefore);
    }

    public void selectPositionString(int indexOfSurroundingPair) {
        requestFocus();
        int startOfPair = getStartOfPair(indexOfSurroundingPair);
        String wholeText = getText();
        int indexOfEqualSign = wholeText.indexOf('=', startOfPair);

        IndexRange trimmedRange = getTrimmedRange(wholeText.substring(startOfPair, indexOfEqualSign));
        selectRange(startOfPair + trimmedRange.getStart(), startOfPair + trimmedRange.getEnd() + 1);
    }

    public static IndexRange getTrimmedRange(String text) {
        int startOffset = 0;
        while (text.charAt(startOffset) == ' ') {
            startOffset++;
        }

        int endOffset = text.length() - 1;
        while (text.charAt(endOffset) == ' ') {
            endOffset--;
        }

        return new IndexRange(startOffset, endOffset);
    }

    public void selectPair(int indexOfPair) {
        requestFocus();
        String wholeText = getText();
        int startOfPair = getStartOfPair(indexOfPair);
        int endOfPair = wholeText.indexOf(';', startOfPair);

        if (endOfPair == -1) {
            endOfPair = wholeText.length();
        }

        selectRange(startOfPair, endOfPair);
    }

    private int getStartOfPair(int indexOfPair) {
        int startOfPair = 0;
        if (indexOfPair != 1) {
            startOfPair = ordinalIndexOf(getText(), ";", indexOfPair - 1) + 1;
        }

        return startOfPair;
    }

    public void replaceSurroundingColor(String colorText, IndexRange selectionBefore) {
        if (colorText != null) {
            try {
                selectSurroundingColor();
                replaceSelection(colorText);
                restoreSelection(selectionBefore);
            } catch (InvalidSelectionException e) {
            }
        }
    }

    private void restoreSelection(IndexRange selectionBefore) {
        selectRange(selectionBefore.getStart(), selectionBefore.getEnd());
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1) {
            pos = str.indexOf(substr, pos + 1);
        }
        return pos;
    }

    public void selectSurroundingPairRange() throws InvalidSelectionException {
        requestFocus();
        IndexRange surroundingPairRange = getSurroundingPairRange();
        if (surroundingPairRange == null) {
            throw new InvalidSelectionException();
        }

        selectRange(surroundingPairRange.getStart(), surroundingPairRange.getEnd());
    }

    public void selectSurroundingColor() throws InvalidSelectionException {
        requestFocus();
        IndexRange surroundingColorRange = getSurroundingColorRange();
        if (surroundingColorRange == null) {
            throw new InvalidSelectionException();
        }
        int startOffset = 0;
        if (getText().charAt(surroundingColorRange.getStart()) == ' ') {
            startOffset = 1;
        }
        selectRange(surroundingColorRange.getStart() + startOffset, surroundingColorRange.getEnd());
    }

    public IndexRange getSurroundingPairRange() {
        IndexRange currentSelection = getSelection();
        IndexRange surroundingPairRange = null;

        // do nothing if a ; is selected as we have at least 2 pairs selected
        if (!getSelectedText().contains(";")) {
            String wholeText = getText();
            int indexOfNextSemicolon = wholeText.indexOf(';', currentSelection.getEnd());
            if (indexOfNextSemicolon == -1) {
                indexOfNextSemicolon = wholeText.length();
            }
            int indexOfPreviousSemicolon = wholeText.substring(0, currentSelection.getStart()).lastIndexOf(';');
            surroundingPairRange = new IndexRange(indexOfPreviousSemicolon + 1, indexOfNextSemicolon);
        }

        return surroundingPairRange;
    }

    public IndexRange getSurroundingColorRange() {
        IndexRange currentSelection = getSelection();
        IndexRange surroundingColorRange = null;

        String selectedText = getSelectedText();
        if (!(selectedText.contains(";") || selectedText.contains("{") || selectedText.contains("}") || selectedText.contains(",") || selectedText.contains("="))) {
            String wholeText = getText();
            int indexOfPreviousBrace = wholeText.substring(0, currentSelection.getStart()).lastIndexOf('{');
            int indexOfPreviousComma = wholeText.substring(0, currentSelection.getStart()).lastIndexOf(',');
            int startIndex = Math.max(indexOfPreviousBrace, indexOfPreviousComma);

            int endIndex = findNextIndexOf(wholeText, currentSelection.getEnd(), ',', '}');

            surroundingColorRange = new IndexRange(startIndex + 1, endIndex);
        }

        return surroundingColorRange;
    }

    private static int findNextIndexOf(String str, int startIndex, char c1, char c2) {
        for (int i = startIndex; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == c1 || c == c2) {
                return i;
            }
        }

        return -1;
    }

    private static int findLastIndexOf(String str, int startIndex, char c1, char c2) {
        for (int i = startIndex; i >= 0; i--) {
            char c = str.charAt(i);
            if (c == c1 || c == c2) {
                return i;
            }
        }

        return -1;
    }

    private boolean isFirstPairSelected() {
        int selectionStart = getSelection().getStart();
        return !getText().substring(0, selectionStart).contains(";");
    }

    class InvalidSelectionException extends Exception {
    }

}
