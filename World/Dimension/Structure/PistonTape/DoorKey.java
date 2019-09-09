package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import Reika.DragonAPI.Instantiable.RGBColorData;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class DoorKey {

	private final DoorValue[] colors;

	public final int colorCount;
	public final int index;

	public DoorKey(int idx, int n, RGBColorData[] clr) {
		index = idx;
		colorCount = n;
		colors = new DoorValue[colorCount];
		for (int i = 0; i < colorCount; i++) {
			colors[i] = new DoorValue(i, clr[i]);
		}
	}

	public DoorValue getValue(int idx) {
		return colors[idx];
	}

	public void setTarget(int idx, Coordinate c) {
		colors[idx].target = c;
	}

	public class DoorValue {

		public final int index;
		private RGBColorData color;
		private Coordinate target;

		private DoorValue(int i, RGBColorData clr) {
			index = i;
			color = clr;
		}

		public RGBColorData getColor() {
			return color.copy();
		}

		public int getRenderColor() {
			return color.getRenderColor();
		}

		public DoorKey getParent() {
			return DoorKey.this;
		}

		public Coordinate getTargetLocation() {
			return target;
		}

	}

}
