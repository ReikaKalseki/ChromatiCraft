package Reika.ChromatiCraft.World.Dimension.Structure.RayBlend;


public class PuzzleProfile {

	public final int gridSize;
	public final boolean allowCaging;
	public final boolean markIntersections;
	public final float initialFill;

	public PuzzleProfile(int s, float f) {
		this(s, f, false, false);
	}

	public PuzzleProfile(int s, float f, boolean c, boolean sect) {
		gridSize = s;
		initialFill = f;
		allowCaging = c;
		markIntersections = sect;
	}

	@Override
	public String toString() {
		return gridSize+"x"+gridSize+" ["+allowCaging+"-"+markIntersections+"]";
	}

}
