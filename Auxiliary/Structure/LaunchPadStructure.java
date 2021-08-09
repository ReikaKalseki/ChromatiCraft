package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class LaunchPadStructure extends ChromaStructureBase {

	private static final StoneTypes[][] structureMetas = {
			{StoneTypes.CORNER, StoneTypes.GROOVE2, StoneTypes.CORNER},
			{StoneTypes.GROOVE1, null, StoneTypes.GROOVE1},
			{StoneTypes.CORNER, StoneTypes.GROOVE2, StoneTypes.CORNER}
	};

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		this.setTile(array, x, y, z, ChromaTiles.LAUNCHPAD);

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				if (i == 0 && k == 0)
					continue;
				int dx = x+i;
				int dz = z+k;
				array.setBlock(dx, y, dz, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), structureMetas[i+1][k+1].ordinal());
			}
		}

		return array;
	}

}
