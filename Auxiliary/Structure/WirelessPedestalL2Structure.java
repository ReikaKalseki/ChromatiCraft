package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;

public class WirelessPedestalL2Structure extends WirelessPedestalStructure {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = super.getArray(world, x, y, z);

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				if (i != 0 && k != 0) {
					array.setBlock(x+i, y-3, z+k, b, StoneTypes.GLOWCOL.ordinal());
				}
			}
		}

		for (int i = -3; i <= 3; i++) {
			for (int k = -3; k <= 3; k++) {
				int m = StoneTypes.SMOOTH.ordinal();
				if (Math.abs(k) == 3 && Math.abs(i) == 3)
					m = StoneTypes.STABILIZER.ordinal();
				else if (Math.abs(k) == 3 || Math.abs(i) == 3)
					m = StoneTypes.RESORING.ordinal();
				if (m != StoneTypes.SMOOTH.ordinal()) {
					array.setBlock(x+i, y-4, z+k, b, m);
				}
			}
		}

		return array;
	}

}
