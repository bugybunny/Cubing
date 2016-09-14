package sq1;

public enum KnownShape {

    SQUARE("cececece"), MUFFIN("ccecceee"), SHIELD("ccceecee"), RIGHT_FIST("cceecece"), LEFT_PAW("ccceceee"), BARREL(
            "cceeccee"), SCALLOP("cccceeee"), KITE("cceceece"), LEFT_FIST("ccececee"), RIGHT_PAW(
                    "ccceeece"), TRIFORCE("eeceeceec"), PAIR("ccceecc"), L("ccccece"), LINE("cccecce");

    String shape;

    KnownShape(String shape) {
        this.shape = shape;
    }

    public String getShape() {
        return shape;
    }

}
