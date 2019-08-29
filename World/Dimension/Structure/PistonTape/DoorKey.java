package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.Arrays;
import java.util.HashSet;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.ColorData;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

public class DoorKey {

	private final boolean[] bits;

	private final ColorData[] colors;

	public final int colorCount;
	public final int ID;

	private final HashSet<Coordinate> door = new HashSet();

	public DoorKey(int id, int n) {
		ID = id;
		colorCount = n;
		bits = ReikaArrayHelper.booleanFromBitflags(ID, n*3);
		colors = new ColorData[colorCount];
		for (int i = 0; i < colorCount; i++) {
			int base = i*3;
			colors[i] = this.genColor(bits[base], bits[base+1], bits[base+2]);
		}
	}

	private ColorData genColor(boolean r, boolean g, boolean b) {
		return new ColorData(r, g, b);
	}

	public int[] getRenderColors() {
		int[] ret = new int[colors.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = colors[i].getRenderColor();
		}
		return ret;
	}

	public boolean verify(boolean[] bits) {
		return Arrays.equals(this.bits, bits);
	}

	public void setOpen(World world, boolean open) {
		for (Coordinate c : door) {
			BlockChromaDoor.setOpen(world, c.xCoord, c.yCoord, c.zCoord, open);
		}
	}

	public void addDoorLocation(int x, int y, int z) {
		door.add(new Coordinate(x, y, z));
	}

}
