package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;

public class BoostedLumenTreeStructure extends LumenTreeStructure {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = super.getArray(world, x, y, z);

		for (int dy = y-12; dy <= y-10; dy++) {
			for (int dx = x-1; dx <= x+2; dx++) {
				for (int dz = z+1; dz >= z-2; dz--) {
					if (dx == x && dz == z)
						continue;
					if (dx == x+1 && dz == z)
						continue;
					if (dx == x && dz == z-1)
						continue;
					if (dx == x+1 && dz == z-1)
						continue;
					array.addEmpty(dx, dy, dz, false, false);
				}
			}
		}

		for (int i = 13; i <= 16; i++) {
			int dy = y-i;
			int meta = i <= 14 ? StoneTypes.SMOOTH.ordinal() : StoneTypes.COLUMN.ordinal();
			array.setBlock(x, dy, z, crystalstone, meta);
			array.setBlock(x, dy, z-1, crystalstone, meta);
			array.setBlock(x+1, dy, z, crystalstone, meta);
			array.setBlock(x+1, dy, z-1, crystalstone, meta);
		}

		array.setBlock(x, y-13, z+1, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(x+1, y-13, z+1, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(x, y-13, z-2, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(x+1, y-13, z-2, crystalstone, StoneTypes.RESORING.ordinal());

		array.setBlock(x-1, y-13, z, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(x-1, y-13, z-1, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(x+2, y-13, z, crystalstone, StoneTypes.RESORING.ordinal());
		array.setBlock(x+2, y-13, z-1, crystalstone, StoneTypes.RESORING.ordinal());

		array.setBlock(x-1, y-13, z+1, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(x-1, y-13, z-2, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+2, y-13, z+1, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+2, y-13, z-2, crystalstone, StoneTypes.BRICKS.ordinal());

		array.setBlock(x, y-13, z+2, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+1, y-13, z+2, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(x, y-13, z-3, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+1, y-13, z-3, crystalstone, StoneTypes.BRICKS.ordinal());

		array.setBlock(x-2, y-13, z, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(x-2, y-13, z-1, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+3, y-13, z, crystalstone, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+3, y-13, z-1, crystalstone, StoneTypes.BRICKS.ordinal());

		array.setBlock(x-2, y-13, z+1, crystalstone, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x-1, y-13, z+2, crystalstone, StoneTypes.STABILIZER.ordinal());

		array.setBlock(x-2, y-13, z-2, crystalstone, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x-1, y-13, z-3, crystalstone, StoneTypes.STABILIZER.ordinal());

		array.setBlock(x+3, y-13, z+1, crystalstone, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x+2, y-13, z+2, crystalstone, StoneTypes.STABILIZER.ordinal());

		array.setBlock(x+3, y-13, z-2, crystalstone, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x+2, y-13, z-3, crystalstone, StoneTypes.STABILIZER.ordinal());

		array.setBlock(x-3, y-13, z+1, crystalstone, StoneTypes.BEAM.ordinal());
		array.setBlock(x-1, y-13, z+3, crystalstone, StoneTypes.BEAM.ordinal());

		array.setBlock(x-3, y-13, z-2, crystalstone, StoneTypes.BEAM.ordinal());
		array.setBlock(x-1, y-13, z-4, crystalstone, StoneTypes.BEAM.ordinal());

		array.setBlock(x+4, y-13, z+1, crystalstone, StoneTypes.BEAM.ordinal());
		array.setBlock(x+2, y-13, z+3, crystalstone, StoneTypes.BEAM.ordinal());

		array.setBlock(x+4, y-13, z-2, crystalstone, StoneTypes.BEAM.ordinal());
		array.setBlock(x+2, y-13, z-4, crystalstone, StoneTypes.BEAM.ordinal());

		array.setBlock(x-2, y-13, z+2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x-2, y-13, z-3, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+3, y-13, z+2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+3, y-13, z-3, crystalstone, StoneTypes.SMOOTH.ordinal());

		array.setBlock(x-4, y-13, z+1, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(x-4, y-13, z-2, crystalstone, StoneTypes.CORNER.ordinal());

		array.setBlock(x-1, y-13, z+4, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(x+2, y-13, z+4, crystalstone, StoneTypes.CORNER.ordinal());

		array.setBlock(x-1, y-13, z-5, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(x+2, y-13, z-5, crystalstone, StoneTypes.CORNER.ordinal());

		array.setBlock(x+5, y-13, z-2, crystalstone, StoneTypes.CORNER.ordinal());
		array.setBlock(x+5, y-13, z+1, crystalstone, StoneTypes.CORNER.ordinal());

		array.setBlock(x-4, y-13, z, crystalstone, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x-4, y-13, z-1, crystalstone, StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x, y-13, z+4, crystalstone, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x+1, y-13, z+4, crystalstone, StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x, y-13, z-5, crystalstone, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x+1, y-13, z-5, crystalstone, StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x+5, y-13, z-1, crystalstone, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x+5, y-13, z, crystalstone, StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x-1, y-14, z+1, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+2, y-14, z+1, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x-1, y-14, z-2, crystalstone, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+2, y-14, z-2, crystalstone, StoneTypes.SMOOTH.ordinal());

		array.setBlock(x-1, y-14, z, crystalstone, StoneTypes.GROOVE1.ordinal());
		array.setBlock(x+2, y-14, z, crystalstone, StoneTypes.GROOVE1.ordinal());
		array.setBlock(x-1, y-14, z-1, crystalstone, StoneTypes.GROOVE1.ordinal());
		array.setBlock(x+2, y-14, z-1, crystalstone, StoneTypes.GROOVE1.ordinal());

		array.setBlock(x, y-14, z+1, crystalstone, StoneTypes.GROOVE2.ordinal());
		array.setBlock(x+1, y-14, z+1, crystalstone, StoneTypes.GROOVE2.ordinal());
		array.setBlock(x, y-14, z-2, crystalstone, StoneTypes.GROOVE2.ordinal());
		array.setBlock(x+1, y-14, z-2, crystalstone, StoneTypes.GROOVE2.ordinal());

		Block c = ChromaBlocks.CHROMA.getBlockInstance();

		array.setBlock(x-3, y-13, z, c, 0);
		array.setBlock(x-3, y-13, z-1, c, 0);

		array.setBlock(x, y-13, z+3, c, 0);
		array.setBlock(x+1, y-13, z+3, c, 0);

		array.setBlock(x, y-13, z-4, c, 0);
		array.setBlock(x+1, y-13, z-4, c, 0);

		array.setBlock(x+4, y-13, z-1, c, 0);
		array.setBlock(x+4, y-13, z, c, 0);

		return array;
	}
}
