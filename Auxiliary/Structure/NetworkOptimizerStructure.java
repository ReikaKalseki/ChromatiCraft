package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;

@Deprecated
public class NetworkOptimizerStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int i = -4; i <= 4; i++) {
			for (int k = -4; k <= 4; k++) {
				for (int j = 1; j >= -7; j--) {
					array.setEmpty(x+i, y+j, z+k, false, false);
				}
			}
		}

		this.setTile(array, x, y, z, ChromaTiles.OPTIMIZER);

		for (int j = 3; j <= 7; j++) {
			int r = j == 7 ? 2 : 1;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					if (j == 3) {
						array.setBlock(x+i, y-j, z+k, ChromaBlocks.LUMA.getBlockInstance(), 0);
					}
					else {
						StoneTypes s = j == 5 && Math.abs(i) == r && Math.abs(k) == r ? StoneTypes.BRICKS : StoneTypes.SMOOTH;
						array.setBlock(x+i, y-j, z+k, crystalstone, s.ordinal());
					}
				}
			}
		}

		for (int j = 3; j <= 6; j++) {
			for (int i = -1; i <= 1; i++) {
				array.setBlock(x+i, y-j, z-2, ChromaBlocks.LUMA.getBlockInstance());
				array.setBlock(x+i, y-j, z+2, ChromaBlocks.LUMA.getBlockInstance());
				array.setBlock(x-2, y-j, z+i, ChromaBlocks.LUMA.getBlockInstance());
				array.setBlock(x+2, y-j, z+i, ChromaBlocks.LUMA.getBlockInstance());
			}
		}

		for (int i = -1; i <= 1; i++) {
			array.setBlock(x+i, y-6, z-3, ChromaBlocks.LUMA.getBlockInstance());
			array.setBlock(x+i, y-6, z+3, ChromaBlocks.LUMA.getBlockInstance());
			array.setBlock(x-3, y-6, z+i, ChromaBlocks.LUMA.getBlockInstance());
			array.setBlock(x+3, y-6, z+i, ChromaBlocks.LUMA.getBlockInstance());
		}

		int r = 4;
		for (int i = -r; i <= r; i++) {
			array.setBlock(x+i, y-7, z-r, crystalstone, StoneTypes.SMOOTH.ordinal());
			array.setBlock(x+i, y-7, z+r, crystalstone, StoneTypes.SMOOTH.ordinal());
			array.setBlock(x-r, y-7, z+i, crystalstone, StoneTypes.SMOOTH.ordinal());
			array.setBlock(x+r, y-7, z+i, crystalstone, StoneTypes.SMOOTH.ordinal());
		}

		r = 3;
		for (int i = -r; i <= r; i++) {
			array.setBlock(x+i, y-7, z-r, ChromaBlocks.LUMA.getBlockInstance(), 0);
			array.setBlock(x+i, y-7, z+r, ChromaBlocks.LUMA.getBlockInstance(), 0);
			array.setBlock(x-r, y-7, z+i, ChromaBlocks.LUMA.getBlockInstance(), 0);
			array.setBlock(x+r, y-7, z+i, ChromaBlocks.LUMA.getBlockInstance(), 0);
		}

		for (int j = 2; j <= 6; j++) {
			StoneTypes s = StoneTypes.COLUMN;
			if (j == 2)
				s = StoneTypes.STABILIZER;
			else if (j == 4)
				s = StoneTypes.GLOWCOL;
			array.setBlock(x+2, y-j, z+2, crystalstone, s.ordinal());
			array.setBlock(x-2, y-j, z+2, crystalstone, s.ordinal());
			array.setBlock(x+2, y-j, z-2, crystalstone, s.ordinal());
			array.setBlock(x-2, y-j, z-2, crystalstone, s.ordinal());
		}

		array.setBlock(x-1, y-4, z+1, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(x+1, y-4, z+1, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(x-1, y-4, z-1, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(x+1, y-4, z-1, crystalstone, StoneTypes.CORNER.ordinal());

		array.setBlock(x-1, y-4, z, crystalstone, StoneTypes.GROOVE2.ordinal());
		array.setBlock(x+1, y-4, z, crystalstone, StoneTypes.GROOVE2.ordinal());
		array.setBlock(x, y-4, z+1, crystalstone, StoneTypes.GROOVE1.ordinal());
		array.setBlock(x, y-4, z-1, crystalstone, StoneTypes.GROOVE1.ordinal());

		return array;
	}

}
