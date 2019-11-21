package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class NetworkOptimizerStructureV2 extends ChromaStructureBase {

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

		int i = x-4;
		int j = y-9;
		int k = z-4;

		array.setBlock(i + 0, j + 2, k + 2, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 0, j + 2, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 0, j + 2, k + 4, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 1, j + 1, k + 3, crystalstone, StoneTypes.MULTICHROMIC.ordinal());
		array.setBlock(i + 1, j + 2, k + 2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 1, j + 2, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 1, j + 2, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 1, j + 2, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 1, j + 2, k + 6, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 1, j + 3, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 1, j + 3, k + 4, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 1, j + 3, k + 5, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 1, j + 4, k + 4, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 2, j + 2, k + 1, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 2, j + 2, k + 2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 6, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 7, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 8, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 2, j + 3, k + 2, crystalstone, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(i + 2, j + 3, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 2, j + 3, k + 4, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 2, j + 3, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 2, j + 3, k + 6, crystalstone, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(i + 2, j + 4, k + 2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 4, k + 3, crystalstone, StoneTypes.BEAM.ordinal());
		array.setBlock(i + 2, j + 4, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 4, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 2, j + 4, k + 6, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 5, k + 2, crystalstone, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 2, j + 5, k + 4, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 2, j + 5, k + 6, crystalstone, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 2, j + 6, k + 2, crystalstone, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 2, j + 6, k + 6, crystalstone, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 2, j + 7, k + 2, crystalstone, StoneTypes.FOCUS.ordinal());
		array.setBlock(i + 2, j + 7, k + 6, crystalstone, StoneTypes.FOCUS.ordinal());
		array.setBlock(i + 3, j + 1, k + 7, crystalstone, StoneTypes.MULTICHROMIC.ordinal());
		array.setBlock(i + 3, j + 2, k + 1, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 6, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 7, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 2, k + 8, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 3, k + 1, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 3, j + 3, k + 2, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 3, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 3, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 3, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 3, k + 6, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 3, k + 7, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 3, j + 4, k + 2, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 4, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 4, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 4, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 4, k + 6, crystalstone, StoneTypes.BEAM.ordinal());
		array.setBlock(i + 3, j + 5, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 5, k + 4, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(i + 3, j + 5, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 6, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 3, j + 6, k + 4, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 6, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 4, j + 2, k + 0, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 4, j + 2, k + 1, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 6, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 7, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 8, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 4, j + 3, k + 1, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 3, k + 2, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 4, j + 3, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 3, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 3, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 3, k + 6, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 4, j + 3, k + 7, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 4, k + 1, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 4, k + 2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 4, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 4, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 4, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 4, k + 6, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 5, k + 2, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 5, k + 3, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(i + 4, j + 5, k + 4, crystalstone, StoneTypes.STABILIZER.ordinal());
		array.setBlock(i + 4, j + 5, k + 5, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(i + 4, j + 5, k + 6, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 6, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 4, j + 6, k + 4, ChromaBlocks.LUMA.getBlockInstance());
		array.setBlock(i + 4, j + 6, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 1, k + 1, crystalstone, StoneTypes.MULTICHROMIC.ordinal());
		array.setBlock(i + 5, j + 2, k + 0, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 1, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 2, k + 2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 6, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 7, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 3, k + 1, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 5, j + 3, k + 2, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 3, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 3, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 3, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 3, k + 6, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 3, k + 7, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 4, k + 7, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 5, j + 4, k + 2, crystalstone, StoneTypes.BEAM.ordinal());
		array.setBlock(i + 5, j + 4, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 4, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 4, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 4, k + 6, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 5, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 5, k + 4, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(i + 5, j + 5, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 6, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 5, j + 6, k + 4, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 6, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 6, j + 2, k + 0, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 6, j + 2, k + 1, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 6, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 7, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 6, j + 3, k + 2, crystalstone, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(i + 6, j + 3, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 6, j + 3, k + 4, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 6, j + 3, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 6, j + 3, k + 6, crystalstone, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(i + 6, j + 4, k + 2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 4, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 6, j + 4, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 4, k + 5, crystalstone, StoneTypes.BEAM.ordinal());
		array.setBlock(i + 6, j + 4, k + 6, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 5, k + 2, crystalstone, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 6, j + 5, k + 4, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 6, j + 5, k + 6, crystalstone, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 6, j + 6, k + 2, crystalstone, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 6, j + 6, k + 6, crystalstone, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 6, j + 7, k + 2, crystalstone, StoneTypes.FOCUS.ordinal());
		array.setBlock(i + 6, j + 7, k + 6, crystalstone, StoneTypes.FOCUS.ordinal());
		array.setBlock(i + 7, j + 1, k + 5, crystalstone, StoneTypes.MULTICHROMIC.ordinal());
		array.setBlock(i + 7, j + 2, k + 2, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 7, j + 2, k + 3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 7, j + 2, k + 4, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 7, j + 2, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 7, j + 2, k + 6, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 7, j + 3, k + 3, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 7, j + 3, k + 4, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 7, j + 3, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 7, j + 4, k + 4, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 8, j + 2, k + 4, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 8, j + 2, k + 5, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 8, j + 2, k + 6, crystalstone, StoneTypes.CORNER.ordinal());

		return array;
	}

}
