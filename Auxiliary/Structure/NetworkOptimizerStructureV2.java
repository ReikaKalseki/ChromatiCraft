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

		array.setBlock(i + 0, j + 2, k + 2, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 0, j + 2, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 0, j + 2, k + 4, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 1, j + 1, k + 3, b, StoneTypes.MULTICHROMIC.ordinal());
		array.setBlock(i + 1, j + 2, k + 2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 1, j + 2, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 1, j + 2, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 1, j + 2, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 1, j + 2, k + 6, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 1, j + 3, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 1, j + 3, k + 4, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 1, j + 3, k + 5, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 1, j + 4, k + 4, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 2, j + 2, k + 1, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 2, j + 2, k + 2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 6, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 7, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 2, k + 8, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 2, j + 3, k + 2, b, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(i + 2, j + 3, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 2, j + 3, k + 4, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 2, j + 3, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 2, j + 3, k + 6, b, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(i + 2, j + 4, k + 2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 4, k + 3, b, StoneTypes.BEAM.ordinal());
		array.setBlock(i + 2, j + 4, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 4, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 2, j + 4, k + 6, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 2, j + 5, k + 2, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 2, j + 5, k + 4, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 2, j + 5, k + 6, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 2, j + 6, k + 2, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 2, j + 6, k + 6, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 2, j + 7, k + 2, b, StoneTypes.FOCUS.ordinal());
		array.setBlock(i + 2, j + 7, k + 6, b, StoneTypes.FOCUS.ordinal());
		array.setBlock(i + 3, j + 1, k + 7, b, StoneTypes.MULTICHROMIC.ordinal());
		array.setBlock(i + 3, j + 2, k + 1, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 6, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 2, k + 7, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 2, k + 8, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 3, k + 1, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 3, j + 3, k + 2, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 3, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 3, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 3, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 3, k + 6, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 3, k + 7, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 3, j + 4, k + 2, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 4, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 4, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 3, j + 4, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 4, k + 6, b, StoneTypes.BEAM.ordinal());
		array.setBlock(i + 3, j + 5, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 5, k + 4, b, StoneTypes.RESORING.ordinal());
		array.setBlock(i + 3, j + 5, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 6, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 3, j + 6, k + 4, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 3, j + 6, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 4, j + 2, k + 0, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 4, j + 2, k + 1, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 6, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 7, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 2, k + 8, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 4, j + 3, k + 1, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 3, k + 2, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 4, j + 3, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 3, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 3, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 3, k + 6, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 4, j + 3, k + 7, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 4, k + 1, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 4, k + 2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 4, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 4, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 4, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 4, k + 6, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 4, j + 5, k + 2, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 5, k + 3, b, StoneTypes.RESORING.ordinal());
		array.setBlock(i + 4, j + 5, k + 4, b, StoneTypes.STABILIZER.ordinal());
		array.setBlock(i + 4, j + 5, k + 5, b, StoneTypes.RESORING.ordinal());
		array.setBlock(i + 4, j + 5, k + 6, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 6, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 4, j + 6, k + 4, ChromaBlocks.LUMA.getBlockInstance());
		array.setBlock(i + 4, j + 6, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 1, k + 1, b, StoneTypes.MULTICHROMIC.ordinal());
		array.setBlock(i + 5, j + 2, k + 0, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 1, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 2, k + 2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 6, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 2, k + 7, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 3, k + 1, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 5, j + 3, k + 2, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 3, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 3, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 3, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 3, k + 6, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 3, k + 7, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 4, j + 4, k + 7, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 5, j + 4, k + 2, b, StoneTypes.BEAM.ordinal());
		array.setBlock(i + 5, j + 4, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 4, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 5, j + 4, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 4, k + 6, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 5, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 5, k + 4, b, StoneTypes.RESORING.ordinal());
		array.setBlock(i + 5, j + 5, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 6, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 5, j + 6, k + 4, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 5, j + 6, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 6, j + 2, k + 0, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 6, j + 2, k + 1, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 6, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 2, k + 7, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 6, j + 3, k + 2, b, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(i + 6, j + 3, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 6, j + 3, k + 4, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 6, j + 3, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 6, j + 3, k + 6, b, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(i + 6, j + 4, k + 2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 4, k + 3, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 6, j + 4, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 4, k + 5, b, StoneTypes.BEAM.ordinal());
		array.setBlock(i + 6, j + 4, k + 6, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 6, j + 5, k + 2, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 6, j + 5, k + 4, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 6, j + 5, k + 6, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 6, j + 6, k + 2, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 6, j + 6, k + 6, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(i + 6, j + 7, k + 2, b, StoneTypes.FOCUS.ordinal());
		array.setBlock(i + 6, j + 7, k + 6, b, StoneTypes.FOCUS.ordinal());
		array.setBlock(i + 7, j + 1, k + 5, b, StoneTypes.MULTICHROMIC.ordinal());
		array.setBlock(i + 7, j + 2, k + 2, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 7, j + 2, k + 3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 7, j + 2, k + 4, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 7, j + 2, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 1);
		array.setBlock(i + 7, j + 2, k + 6, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 7, j + 3, k + 3, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 7, j + 3, k + 4, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 7, j + 3, k + 5, ChromaBlocks.LUMA.getBlockInstance(), 2);
		array.setBlock(i + 7, j + 4, k + 4, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(i + 8, j + 2, k + 4, b, StoneTypes.CORNER.ordinal());
		array.setBlock(i + 8, j + 2, k + 5, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(i + 8, j + 2, k + 6, b, StoneTypes.CORNER.ordinal());

		return array;
	}

}
