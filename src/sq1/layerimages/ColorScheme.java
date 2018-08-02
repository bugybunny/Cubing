package sq1.layerimages;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

public class ColorScheme {
    public static final Color YELLOW = Color.valueOf("0xfffe45");
    public static final Color BLUE = Color.valueOf("0x0000ff");
    public static final Color RED = Color.valueOf("0xff4545");
    public static final Color GREEN = Color.valueOf("0x45f545");
    public static final Color ORANGE = Color.valueOf("0xffa645");
    public static final Color WHITE = Color.valueOf("0xfdfdfd");
    public static final Color GRAY = Color.GRAY;

    public static final Map<String, Color> COLORS = new HashMap<>(4);
    public static final Map<Integer, String> COLOR_INDICES = new HashMap<>(4);

    static {
        COLORS.put("blue", BLUE);
        COLORS.put("red", RED);
        COLORS.put("green", GREEN);
        COLORS.put("orange", ORANGE);

        COLOR_INDICES.put(1, "blue");
        COLOR_INDICES.put(2, "red");
        COLOR_INDICES.put(3, "green");
        COLOR_INDICES.put(4, "orange");
    }
}
