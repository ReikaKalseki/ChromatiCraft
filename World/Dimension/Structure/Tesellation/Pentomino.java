package Reika.ChromatiCraft.World.Dimension.Structure.Tesellation;

import Reika.DragonAPI.Instantiable.Data.WeightedRandom;


public enum Pentomino {

	P(22, "##", "##", "# "),
	F(14, " ##", "## ", " # "),
	Y(14, "#", "##", "#", "#"),
	L(8, "# ", "# ", "# ", "##"),
	N(8, "# ", "##", " #", " #"),
	T(7, "###", " # ", " # "),
	U(4, "# #", "###"),
	V(4, "#  ", "#  ", "###"),
	W(4, "#  ", "## ", " ##"),
	Z(4, "## ", " # ", " ##"),
	X(3, " # ", "###", " # "),
	I(2, "#", "#", "#", "#", "#");

	private final int pieceWeight;
	private final boolean[][] shape;

	private static final WeightedRandom<Pentomino> rand = new WeightedRandom();

	public static final Pentomino[] pieceList = values();

	private Pentomino(int w, String... patt) {
		pieceWeight = w;
		shape = new boolean[5][5];
		for (int i = 0; i < patt.length; i++) {
			String s = patt[i];
			for (int k = 0; k < s.length(); k++) {
				char c = s.charAt(k);
				if (c == '#')
					shape[i][k] = true;
			}
		}
	}

	public static Pentomino getWeightedRandom() {
		return rand.getRandomEntry();
	}

	static {
		for (int i = 0; i < pieceList.length; i++) {
			Pentomino p = pieceList[i];
			rand.addEntry(p, p.pieceWeight);
		}
	}

}
