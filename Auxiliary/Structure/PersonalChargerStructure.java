package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ColoredStructureBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class PersonalChargerStructure extends ColoredStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block r = ChromaBlocks.RUNE.getBlockInstance();

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				array.setBlock(x+i, y, z+k, b, 0);
			}
		}
		array.setBlock(x, y, z, b, 14);

		for (int i = 1; i <= 4; i++) {
			int m = i == 4 ? 7 : 2;
			array.setBlock(x+1, y+i, z+1, b, m);
			array.setBlock(x-1, y+i, z+1, b, m);
			array.setBlock(x+1, y+i, z-1, b, m);
			array.setBlock(x-1, y+i, z-1, b, m);
		}

		array.setBlock(x+2, y, z+2, b, 12);
		array.setBlock(x-2, y, z+2, b, 12);
		array.setBlock(x+2, y, z-2, b, 12);
		array.setBlock(x-2, y, z-2, b, 12);

		array.setBlock(x+2, y+1, z+2, b, 2);
		array.setBlock(x-2, y+1, z+2, b, 2);
		array.setBlock(x+2, y+1, z-2, b, 2);
		array.setBlock(x-2, y+1, z-2, b, 2);

		CrystalElement e = this.getCurrentColor();
		array.setBlock(x+2, y+2, z+2, r, e.ordinal());
		array.setBlock(x-2, y+2, z+2, r, e.ordinal());
		array.setBlock(x+2, y+2, z-2, r, e.ordinal());
		array.setBlock(x-2, y+2, z-2, r, e.ordinal());

		for (int i = -1; i <= 1; i++) {
			array.setBlock(x+2, y, z+i, b, 11);
			array.setBlock(x-2, y, z+i, b, 11);

			array.setBlock(x+i, y, z+2, b, 10);
			array.setBlock(x+i, y, z-2, b, 10);
		}

		this.setTile(array, x, y+6, z, ChromaTiles.PERSONAL);

		return array;
	}

}
