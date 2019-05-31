package Reika.ChromatiCraft.World.Dimension.Structure.RayBlend;


public class PuzzleProfile {

	public final int gridSize;
	public final boolean allowCaging;

	public PuzzleProfile(int s) {
		this(s, false);
	}

	public PuzzleProfile(int s, boolean c) {
		gridSize = s;
		allowCaging = c;
	}

}
