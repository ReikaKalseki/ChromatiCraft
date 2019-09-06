package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.Arrays;

import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.ColorData;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

public class DoorKey {

	private final boolean[] bits;

	private final DoorValue[] colors;

	public final int colorCount;
	public final int ID;
	public final int index;

	public DoorKey(int idx, int id, int n) {
		index = idx;
		ID = id;
		colorCount = n;
		bits = ReikaArrayHelper.booleanFromBitflags(ID, n*3);
		colors = new DoorValue[colorCount];
		for (int i = 0; i < colorCount; i++) {
			int base = i*3;
			colors[i] = new DoorValue(i, this.genColor(bits[base], bits[base+1], bits[base+2]));
		}
	}

	private ColorData genColor(boolean r, boolean g, boolean b) {
		return new ColorData(r, g, b);
	}

	public DoorValue getValue(int idx) {
		return colors[idx];
	}

	public boolean verify(boolean[] bits) {
		return Arrays.equals(this.bits, bits);
	}

	public void setTarget(int idx, Coordinate c) {
		colors[idx].target = c;
	}

	public boolean isValid(boolean allowBlack) {
		if (allowBlack)
			return true;
		for (int i = 0; i < colors.length; i++) {
			if (colors[i].color.isBlack())
				return false;
		}
		return true;
	}

	public class DoorValue {

		public final int index;
		private ColorData color;
		private Coordinate target;

		private DoorValue(int i, ColorData clr) {
			index = i;
			color = clr;
		}

		public ColorData getColor() {
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
