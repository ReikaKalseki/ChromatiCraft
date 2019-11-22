package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class TreeSendFocusStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		LumenTreeStructure.getTrunkBlocks(array, x, y, z);

		for (int i = -2; i <= 3; i += 5) {
			for (int k = -2; k <= 3; k += 5) {
				int dx = x+i;
				int dz = z+k-1;
				int dy = y-12;
				array.setBlock(dx, dy, dz, crystalstone, StoneTypes.COLUMN.ordinal());
				array.setBlock(dx, dy+1, dz, crystalstone, StoneTypes.FOCUS.ordinal());
			}
		}

		return array;
	}

}
