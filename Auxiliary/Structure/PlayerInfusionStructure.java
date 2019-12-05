package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;

public class PlayerInfusionStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		y -= 3;

		for (int i = -3; i <= 3; i++) {
			for (int k = -3; k <= 3; k++) {
				if (Math.abs(i) < 4 && Math.abs(k) < 4) {
					array.setBlock(x+i, y, z+k, crystalstone, StoneTypes.SMOOTH.ordinal());
					array.setBlock(x+i, y+1, z+k, ChromaBlocks.CHROMA.getBlockInstance(), 0);
				}
			}
		}

		array.setBlock(x, y+1, z, crystalstone, StoneTypes.COLUMN.ordinal());
		array.setBlock(x, y+2, z, crystalstone, StoneTypes.FOCUS.ordinal());
		this.setTile(array, x, y+3, z, ChromaTiles.PLAYERINFUSER);

		array.setBlock(x+2, y+1, z, crystalstone, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x-2, y+1, z, crystalstone, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x, y+1, z+2, crystalstone, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x, y+1, z-2, crystalstone, StoneTypes.STABILIZER.ordinal());

		for (int i = -4; i <= 4; i++) {
			if (i == 0)
				continue;
			StoneTypes s = StoneTypes.BEAM;
			if (Math.abs(i) <= 1 || Math.abs(i) == 4)
				s = StoneTypes.CORNER;
			array.setBlock(x+i, y+1, z+4, crystalstone, s.ordinal());
			array.setBlock(x+i, y+1, z-4, crystalstone, s.ordinal());
			array.setBlock(x-4, y+1, z+i, crystalstone, s.ordinal());
			array.setBlock(x+4, y+1, z+i, crystalstone, s.ordinal());
		}


		for (int i = -1; i <= 1; i++) {
			StoneTypes s = i == 0 ? StoneTypes.BRICKS : StoneTypes.CORNER;
			array.setBlock(x+i, y+1, z+3, crystalstone, s.ordinal());
			array.setBlock(x+i, y+1, z-3, crystalstone, s.ordinal());
			array.setBlock(x-3, y+1, z+i, crystalstone, s.ordinal());
			array.setBlock(x+3, y+1, z+i, crystalstone, s.ordinal());
		}

		return array;
	}

}
