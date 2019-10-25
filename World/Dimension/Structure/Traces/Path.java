package Reika.ChromatiCraft.World.Dimension.Structure.Traces;

import java.awt.Point;
import java.util.LinkedList;

public class Path {

	public final int index;
	private final LinkedList<Point> data = new LinkedList();

	Path(int i) {
		index = i;
	}

}
