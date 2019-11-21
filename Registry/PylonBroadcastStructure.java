package Reika.ChromatiCraft.Registry;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.PylonStructure;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;

public class PylonBroadcastStructure extends PylonStructure {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = super.getArray(world, x, y, z);
		y -= 9;

		array.setBlock(x, y, z, b, StoneTypes.STABILIZER.ordinal());

		for (int i = 1; i <= 2; i++) {
			array.setBlock(x+i, y, z, b, StoneTypes.RESORING.ordinal());
			array.setBlock(x-i, y, z, b, StoneTypes.RESORING.ordinal());
			array.setBlock(x, y, z+i, b, StoneTypes.RESORING.ordinal());
			array.setBlock(x, y, z-i, b, StoneTypes.RESORING.ordinal());
		}

		for (int i = -3; i <= 3; i++) {
			int m = Math.abs(i) == 3 || i == 0 ? StoneTypes.EMBOSSED.ordinal() : StoneTypes.BRICKS.ordinal();
			array.setBlock(x+i, y, z+5, b, m);
			array.setBlock(x+i, y, z-5, b, m);
			array.setBlock(x+5, y, z+i, b, m);
			array.setBlock(x-5, y, z+i, b, m);
		}


		for (int i = -2; i <= 2; i++) {
			array.setBlock(x+i, y, z+4, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+i, y, z-4, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+4, y, z+i, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-4, y, z+i, ChromaBlocks.CHROMA.getBlockInstance(), 0);

			array.setBlock(x+i, y-1, z+4, b, 0);
			array.setBlock(x+i, y-1, z-4, b, 0);
			array.setBlock(x+4, y-1, z+i, b, 0);
			array.setBlock(x-4, y-1, z+i, b, 0);
		}

		for (int i = 3; i <= 4; i++) {
			array.setBlock(x+i, y, z+3, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+i, y, z-3, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x-i, y, z+3, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x-i, y, z-3, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+3, y, z+i, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x-3, y, z+i, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+3, y, z-i, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x-3, y, z-i, b, StoneTypes.BRICKS.ordinal());
		}

		for (int i = 2; i <= 3; i++) {
			array.setBlock(x+i, y, z+2, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+i, y, z-2, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-i, y, z+2, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-i, y, z-2, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+2, y, z+i, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-2, y, z+i, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+2, y, z-i, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-2, y, z-i, ChromaBlocks.CHROMA.getBlockInstance(), 0);

			array.setBlock(x+i, y-1, z+2, b, 0);
			array.setBlock(x+i, y-1, z-2, b, 0);
			array.setBlock(x-i, y-1, z+2, b, 0);
			array.setBlock(x-i, y-1, z-2, b, 0);
			array.setBlock(x+2, y-1, z+i, b, 0);
			array.setBlock(x-2, y-1, z+i, b, 0);
			array.setBlock(x+2, y-1, z-i, b, 0);
			array.setBlock(x-2, y-1, z-i, b, 0);
		}

		for (int i = 1; i <= 4; i++) {
			int m = i == 4 ? StoneTypes.MULTICHROMIC.ordinal() : StoneTypes.COLUMN.ordinal();
			array.setBlock(x-3, y+i, z-5, b, m);
			array.setBlock(x-5, y+i, z-3, b, m);
			array.setBlock(x+3, y+i, z-5, b, m);
			array.setBlock(x+5, y+i, z-3, b, m);
			array.setBlock(x-3, y+i, z+5, b, m);
			array.setBlock(x-5, y+i, z+3, b, m);
			array.setBlock(x+3, y+i, z+5, b, m);
			array.setBlock(x+5, y+i, z+3, b, m);
		}

		for (int i = 1; i <= 6; i++) {
			int m = i == 3 ? StoneTypes.GLOWCOL.ordinal() : (i == 6 ? StoneTypes.FOCUS.ordinal() : StoneTypes.COLUMN.ordinal());
			array.setBlock(x+5, y+i, z, b, m);
			array.setBlock(x-5, y+i, z, b, m);
			array.setBlock(x, y+i, z+5, b, m);
			array.setBlock(x, y+i, z-5, b, m);
		}

		return array;
	}
}
