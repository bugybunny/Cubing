package sq1;

/**
 * Takes a sequence of Square-1 moves and mirrors the top moves to bottom and
 * vice versa.
 * 
 * Usage example: java sq1.NotationMirrorer /1,0/2,2/0,-1/3,3/
 * 
 * @author <marco.syfrig@gmail.com>
 */
public class NotationMirrorer {

	public static void main(String[] args) {
		// String normalAlg = "/1,0/2,2/0,-1/3,3/";
		System.out.print(args[0]);
		System.out.println("\t-->\t" + mirrorAlg(args[0]));
	}

	private static String mirrorAlg(String normalAlg) {
		boolean slashAtEnd = false;

		StringBuilder mirroredAlg = new StringBuilder(normalAlg.length());
		if (normalAlg.startsWith("/")) {
			normalAlg = normalAlg.substring(1);
			mirroredAlg.append("/");
		}
		if (normalAlg.endsWith("/")) {
			slashAtEnd = true;
			normalAlg = normalAlg.substring(0, normalAlg.length() - 1);
		}

		String[] parts = normalAlg.split("/");
		for (String part : parts) {
			String[] singleMoves = part.split(",");
			if (singleMoves.length != 2) {
				System.err.println("Please enter a valid move sequence with only a single comma between two /.");
				System.exit(1);
			}
			int newTop = -Integer.parseInt(singleMoves[1]);
			int newBottom = -Integer.parseInt(singleMoves[0]);
			mirroredAlg.append(newTop);
			mirroredAlg.append(',');
			mirroredAlg.append(newBottom);
			mirroredAlg.append('/');
		}
		if (!slashAtEnd) {
			mirroredAlg.deleteCharAt(mirroredAlg.length());
		}
		return mirroredAlg.toString();
	}

}
