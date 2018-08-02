package sq1.layerimages;

public class Filename {
    private String value;

    public Filename(String filename) {
        this.value = filename;
    }

    public void reset() {
        value = "";
    }

    public String add(String positionToAdd) {
        if (value.length() != 0) {
            value += "+";
        }
        value += positionToAdd;

        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
