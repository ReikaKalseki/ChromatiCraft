package Reika.ChromatiCraft.World.Dimension.Structure.RayBlend;


public class PuzzleProfile {

	public final int gridSize;
	public final boolean allowCaging;
	public final boolean markIntersections;

	public PuzzleProfile(int s) {
		this(s, false, false);
	}

	public PuzzleProfile(int s, boolean c, boolean sect) {
		gridSize = s;
		allowCaging = c;
		markIntersections = sect;
	}

	@Override
	public String toString() {
		return gridSize+"x"+gridSize+" ["+allowCaging+"-"+markIntersections+"]";
	}

}
