package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.HashSet;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.ColorData;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

public class DoorKey {

	private final boolean[] bits;

	private final ColorData color1;
	private final ColorData color2;
	private final ColorData color3;

	public final int ID;

	private final HashSet<Coordinate> door;

	public DoorKey(int id, HashSet<Coordinate> set) {
		ID = id;
		bits = ReikaArrayHelper.booleanFromBitflags(ID, 9);
		color1 = this.genColor(bits[0], bits[1], bits[2]);
		color2 = this.genColor(bits[3], bits[4], bits[5]);
		color3 = this.genColor(bits[6], bits[7], bits[8]);
		door = set;
	}

	private ColorData genColor(boolean r, boolean g, boolean b) {
		return new ColorData(r, g, b);
	}

	public int[] getRenderColors() {
		return new int[] {color1.getRenderColor(), color2.getRenderColor(), color3.getRenderColor()};
	}

	public void setOpen(World world, boolean open) {
		for (Coordinate c : door) {
			BlockChromaDoor.setOpen(world, c.xCoord, c.yCoord, c.zCoord, open);
		}
	}

	public ColorData getColor1() {
		return color1;
	}

	public ColorData getColor2() {
		return color2;
	}

	public ColorData getColor3() {
		return color3;
	}

}
