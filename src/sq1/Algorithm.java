package sq1;

import java.security.InvalidParameterException;

/**
 * Represents a Square-1 algorithm consisting of /, and [-12,12]. Example:
 * {@code /1,0/2,2/0,-1/3,3/}. This class can generate and store the mirror,
 * which is {@code /0,-1/-2,-2/1,0/-3,-3/}, and the inverse, which is
 * {@code /-3,-3/0,1/-2,-2/-1,0/} for the above example. The constructor checks
 * for the correctness of the algorithm.
 * 
 * @author <marco.syfrig@gmail.com>
 */
public class Algorithm {
    public static final Algorithm EMPTY_ALGORITHM = new Algorithm("");

    public final String alg;
    private Algorithm mirror;
    private Algorithm reverse;
    private boolean slashAtBeginning;
    private boolean slashAtEnd;

    public Algorithm(String alg) {
        this(alg, null);
    }

    protected Algorithm(String alg, Algorithm mirror) {
        alg = alg.replaceAll("\\s+", "");

        String[] parts = alg.split("/");
        for (String part : parts) {
            // allow /// as algorithm even though it's useless
            if (!part.isEmpty()) {
                String[] singleMoves = part.split(",");
                if (singleMoves.length != 2) {
                    throw new InvalidParameterException(
                            "Enter a valid move sequence with only a single comma between two /.");
                }
                if (singleMoves.length == 2) {
                    if (!isValidMove(singleMoves[0]) || !isValidMove(singleMoves[1])) {
                        throw new InvalidParameterException(
                                "Enter only numbers in the range [-12, 12] between two commas. You entered " + part
                                        + ".");
                    }
                }
            }
        }

        this.alg = alg;
        this.mirror = mirror;
        slashAtBeginning = alg.startsWith("/");
        if (alg.length() > 1) {
            slashAtEnd = alg.endsWith("/");
        }
    }

    public static boolean isValidMove(String move) {
        try {
            int value = Integer.parseInt(move);
            return value >= -12 && value <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Mirrors the top moves to bottom and vice versa from {@link alg}.
     * 
     * Example: If {@code this} represents {@code /1,0/2,2/0,-1/3,3/} then it
     * returns an instance that represents {@code /0,-1/-2,-2/1,0/-3,-3/}.
     */
    public Algorithm mirror() {
        if (mirror != null || this.equals(EMPTY_ALGORITHM)) {
            return mirror;
        }

        String mirroredAlg = negateAlg(alg, true, false);
        mirror = new Algorithm(mirroredAlg, this);
        return mirror;
    }

    public Algorithm reverse() {
        if (reverse != null) {
            return reverse;
        }

        String reversedAlg = negateAlg(alg, false, true);

        reverse = new Algorithm(reversedAlg);
        reverse.reverse = this;
        return reverse;
    }

    private String negateAlg(String original, boolean mirror, boolean reverse) {
        if (original.equals("/")) {
            return original;
        }

        StringBuilder negatedAlg = new StringBuilder(alg.length());
        String tempAlg = alg;
        if (slashAtBeginning) {
            tempAlg = tempAlg.substring(1);
            negatedAlg.append("/");
        }
        if (slashAtEnd) {
            tempAlg = tempAlg.substring(0, tempAlg.length() - 1);
        }

        String[] parts = tempAlg.split("/");
        for (String part : parts) {
            String negatedMove = negateMove(part, mirror);
            if (reverse) {
                negatedAlg.insert(0, negatedMove);
                negatedAlg.insert(0, '/');
            } else {
                negatedAlg.append(negatedMove);
                negatedAlg.append('/');
            }
        }
        if (!slashAtEnd) {
            if (reverse) {
                negatedAlg.deleteCharAt(0);
            } else {
                negatedAlg.deleteCharAt(negatedAlg.length());
            }
        }

        return negatedAlg.toString();
    }

    /**
     * @param move
     *            top and bottom movement in the form {@code 1,-2} for example
     * @param switchTopBottom
     *            {@code true} if top and bottom should be switched so
     *            {@code 1,-2} becomes {@code 2,-1}
     */
    public static String negateMove(String move, boolean switchTopBottom) {
        int offset = 0;
        if (switchTopBottom) {
            offset = 1;
        }

        // use the move itself if it is only a /
        String negatedMove = move;

        String[] singleMoves = move.split(",");
        if (singleMoves.length == 2) {
            // for mirroring bottom and top just use the the old top for bottom
            // and vice versa
            int newTop = -Integer.parseInt(singleMoves[(0 + offset) % singleMoves.length]);
            int newBottom = -Integer.parseInt(singleMoves[(1 + offset) % singleMoves.length]);
            negatedMove = newTop + "," + newBottom;
        }

        return negatedMove;
    }

    public String getAlg() {
        return alg;
    }

    public Algorithm getMirror() {
        return mirror;
    }

    public Algorithm getReverse() {
        return reverse;
    }

    @Override
    public String toString() {
        return alg;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alg == null) ? 0 : alg.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Algorithm other = (Algorithm) obj;
        if (alg == null) {
            if (other.alg != null) {
                return false;
            }
        } else if (!alg.equals(other.alg)) {
            return false;
        }
        return true;
    }
}
