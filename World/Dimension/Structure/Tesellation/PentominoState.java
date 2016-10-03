package Reika.ChromatiCraft.World.Dimension.Structure.Tesellation;


public class PentominoState {

	public final Pentomino shape;

	private int rotation;

	public PentominoState(Pentomino p, int r) {
		shape = p;
		rotation = r;
	}

	public void rotate() {
		rotation += 90;
	}

	public int getRotation() {
		return rotation;
	}

}
