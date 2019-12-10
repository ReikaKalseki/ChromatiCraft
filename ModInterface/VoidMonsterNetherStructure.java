package Reika.ChromatiCraft.ModInterface;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class VoidMonsterNetherStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		this.setTile(array, x, y, z, ChromaTiles.VOIDTRAP);

		x -= 2;
		y -= 6;
		z -= 2;

		for (int i = -2; i <= 6; i++) {
			for (int k = -2; k <= 6; k++) {
				for (int j = 0; j <= 4; j++) {
					if (i < 0 || k < 0 || i > 4 || k > 4)
						array.setEmpty(x + i, y + j, z + k, false, false);
					//array.setBlock(x + i, y + j, z + k, Blocks.ice);
				}
			}
		}

		array.setBlock(x + 0, y + 0, z + 0, crystalstone, 8);
		array.setBlock(x + 0, y + 0, z + 2, crystalstone, 7);
		array.setBlock(x + 0, y + 0, z + 4, crystalstone, 8);
		array.setBlock(x + 0, y + 1, z + 0, crystalstone, 3);
		array.setBlock(x + 0, y + 1, z + 4, crystalstone, 3);
		array.setBlock(x + 0, y + 2, z + 0, crystalstone, 6);
		array.setBlock(x + 0, y + 2, z + 1, crystalstone, 1);
		array.setBlock(x + 0, y + 2, z + 2, crystalstone, 1);
		array.setBlock(x + 0, y + 2, z + 3, crystalstone, 1);
		array.setBlock(x + 0, y + 2, z + 4, crystalstone, 6);
		array.setBlock(x + 1, y + 0, z + 1, crystalstone, 0);
		array.setBlock(x + 1, y + 0, z + 3, crystalstone, 0);
		array.setBlock(x + 1, y + 1, z + 1, crystalstone, 2);
		array.setBlock(x + 1, y + 1, z + 3, crystalstone, 2);
		array.setBlock(x + 1, y + 2, z + 0, crystalstone, 1);
		array.setBlock(x + 1, y + 2, z + 1, Blocks.tnt);
		array.setBlock(x + 1, y + 2, z + 2, crystalstone, 15);
		array.setBlock(x + 1, y + 2, z + 3, Blocks.tnt);
		array.setBlock(x + 1, y + 2, z + 4, crystalstone, 1);
		array.setBlock(x + 1, y + 3, z + 1, crystalstone, 2);
		array.setBlock(x + 1, y + 3, z + 3, crystalstone, 2);
		array.setBlock(x + 1, y + 4, z + 1, crystalstone, 9);
		array.setBlock(x + 1, y + 4, z + 3, crystalstone, 9);
		array.setBlock(x + 2, y + 0, z + 0, crystalstone, 7);
		array.setBlock(x + 2, y + 0, z + 4, crystalstone, 7);
		array.setBlock(x + 2, y + 2, z + 0, crystalstone, 1);
		array.setBlock(x + 2, y + 2, z + 1, crystalstone, 15);
		array.setBlock(x + 2, y + 2, z + 2, crystalstone, 14);
		array.setBlock(x + 2, y + 2, z + 3, crystalstone, 15);
		array.setBlock(x + 2, y + 2, z + 4, crystalstone, 1);
		array.setBlock(x + 3, y + 0, z + 1, crystalstone, 0);
		array.setBlock(x + 3, y + 0, z + 3, crystalstone, 0);
		array.setBlock(x + 3, y + 1, z + 1, crystalstone, 2);
		array.setBlock(x + 3, y + 1, z + 3, crystalstone, 2);
		array.setBlock(x + 3, y + 2, z + 0, crystalstone, 1);
		array.setBlock(x + 3, y + 2, z + 1, Blocks.tnt);
		array.setBlock(x + 3, y + 2, z + 2, crystalstone, 15);
		array.setBlock(x + 3, y + 2, z + 3, Blocks.tnt);
		array.setBlock(x + 3, y + 2, z + 4, crystalstone, 1);
		array.setBlock(x + 3, y + 3, z + 1, crystalstone, 2);
		array.setBlock(x + 3, y + 3, z + 3, crystalstone, 2);
		array.setBlock(x + 3, y + 4, z + 1, crystalstone, 9);
		array.setBlock(x + 3, y + 4, z + 3, crystalstone, 9);
		array.setBlock(x + 4, y + 0, z + 0, crystalstone, 8);
		array.setBlock(x + 4, y + 0, z + 2, crystalstone, 7);
		array.setBlock(x + 4, y + 0, z + 4, crystalstone, 8);
		array.setBlock(x + 4, y + 1, z + 0, crystalstone, 3);
		array.setBlock(x + 4, y + 1, z + 4, crystalstone, 3);
		array.setBlock(x + 4, y + 2, z + 0, crystalstone, 6);
		array.setBlock(x + 4, y + 2, z + 1, crystalstone, 1);
		array.setBlock(x + 4, y + 2, z + 2, crystalstone, 1);
		array.setBlock(x + 4, y + 2, z + 3, crystalstone, 1);
		array.setBlock(x + 4, y + 2, z + 4, crystalstone, 6);
		array.setBlock(x + 0, y + 3, z + 0, Blocks.redstone_wire);
		array.setBlock(x + 0, y + 3, z + 1, Blocks.redstone_wire);
		array.setBlock(x + 0, y + 3, z + 2, Blocks.redstone_wire);
		array.setBlock(x + 0, y + 3, z + 3, Blocks.redstone_wire);
		array.setBlock(x + 0, y + 3, z + 4, Blocks.redstone_wire);
		array.setBlock(x + 1, y + 3, z + 0, Blocks.redstone_wire);
		array.setBlock(x + 1, y + 3, z + 4, Blocks.redstone_wire);
		array.setBlock(x + 2, y + 3, z + 0, Blocks.redstone_wire);
		array.setBlock(x + 4, y + 3, z + 0, Blocks.redstone_wire);
		array.setBlock(x + 4, y + 3, z + 1, Blocks.redstone_wire);
		array.setBlock(x + 4, y + 3, z + 2, Blocks.redstone_wire);
		array.setBlock(x + 4, y + 3, z + 3, Blocks.redstone_wire);
		array.setBlock(x + 4, y + 3, z + 4, Blocks.redstone_wire);
		array.setBlock(x + 2, y + 3, z + 4, Blocks.redstone_wire);
		array.setBlock(x + 3, y + 3, z + 0, Blocks.redstone_wire);
		array.setBlock(x + 3, y + 3, z + 4, Blocks.redstone_wire);

		array.setBlock(x + 2, y + 1, z + 0, Blocks.redstone_torch, 5);
		array.setBlock(x + 2, y + 1, z + 4, Blocks.redstone_torch, 5);
		array.setBlock(x + 0, y + 1, z + 2, Blocks.redstone_torch, 5);
		array.setBlock(x + 4, y + 1, z + 2, Blocks.redstone_torch, 5);

		array.addBlock(x + 2, y + 1, z + 0, Blocks.unlit_redstone_torch, 5);
		array.addBlock(x + 2, y + 1, z + 4, Blocks.unlit_redstone_torch, 5);
		array.addBlock(x + 0, y + 1, z + 2, Blocks.unlit_redstone_torch, 5);
		array.addBlock(x + 4, y + 1, z + 2, Blocks.unlit_redstone_torch, 5);

		array.setBlock(x + 0, y + 4, z + 0, Blocks.air);
		array.setBlock(x + 0, y + 4, z + 1, Blocks.air);
		array.setBlock(x + 0, y + 4, z + 2, Blocks.air);
		array.setBlock(x + 0, y + 4, z + 3, Blocks.air);
		array.setBlock(x + 0, y + 4, z + 4, Blocks.air);
		array.setBlock(x + 0, y + 5, z + 0, Blocks.air);
		array.setBlock(x + 0, y + 5, z + 1, Blocks.air);
		array.setBlock(x + 0, y + 5, z + 2, Blocks.air);
		array.setBlock(x + 0, y + 5, z + 3, Blocks.air);
		array.setBlock(x + 0, y + 5, z + 4, Blocks.air);
		array.setBlock(x + 0, y + 6, z + 0, Blocks.air);
		array.setBlock(x + 0, y + 6, z + 1, Blocks.air);
		array.setBlock(x + 0, y + 6, z + 2, Blocks.air);
		array.setBlock(x + 0, y + 6, z + 3, Blocks.air);
		array.setBlock(x + 0, y + 6, z + 4, Blocks.air);
		array.setBlock(x + 1, y + 3, z + 2, Blocks.air);
		array.setBlock(x + 1, y + 4, z + 0, Blocks.air);
		array.setBlock(x + 1, y + 4, z + 2, Blocks.air);
		array.setBlock(x + 1, y + 4, z + 4, Blocks.air);
		array.setBlock(x + 1, y + 5, z + 0, Blocks.air);
		array.setBlock(x + 1, y + 5, z + 1, Blocks.air);
		array.setBlock(x + 1, y + 5, z + 2, Blocks.air);
		array.setBlock(x + 1, y + 5, z + 3, Blocks.air);
		array.setBlock(x + 1, y + 5, z + 4, Blocks.air);
		array.setBlock(x + 1, y + 6, z + 0, Blocks.air);
		array.setBlock(x + 1, y + 6, z + 1, Blocks.air);
		array.setBlock(x + 1, y + 6, z + 2, Blocks.air);
		array.setBlock(x + 1, y + 6, z + 3, Blocks.air);
		array.setBlock(x + 1, y + 6, z + 4, Blocks.air);
		array.setBlock(x + 2, y + 3, z + 1, Blocks.air);
		array.setBlock(x + 2, y + 3, z + 2, Blocks.air);
		array.setBlock(x + 2, y + 3, z + 3, Blocks.air);
		array.setBlock(x + 2, y + 4, z + 0, Blocks.air);
		array.setBlock(x + 2, y + 4, z + 1, Blocks.air);
		array.setBlock(x + 2, y + 4, z + 2, Blocks.air);
		array.setBlock(x + 2, y + 4, z + 3, Blocks.air);
		array.setBlock(x + 2, y + 4, z + 4, Blocks.air);
		array.setBlock(x + 2, y + 5, z + 0, Blocks.air);
		array.setBlock(x + 2, y + 5, z + 1, Blocks.air);
		array.setBlock(x + 2, y + 5, z + 2, Blocks.air);
		array.setBlock(x + 2, y + 5, z + 3, Blocks.air);
		array.setBlock(x + 2, y + 5, z + 4, Blocks.air);
		array.setBlock(x + 2, y + 6, z + 0, Blocks.air);
		array.setBlock(x + 2, y + 6, z + 1, Blocks.air);
		array.setBlock(x + 2, y + 6, z + 3, Blocks.air);
		array.setBlock(x + 2, y + 6, z + 4, Blocks.air);
		array.setBlock(x + 3, y + 3, z + 2, Blocks.air);
		array.setBlock(x + 3, y + 4, z + 0, Blocks.air);
		array.setBlock(x + 3, y + 4, z + 2, Blocks.air);
		array.setBlock(x + 3, y + 4, z + 4, Blocks.air);
		array.setBlock(x + 3, y + 5, z + 0, Blocks.air);
		array.setBlock(x + 3, y + 5, z + 1, Blocks.air);
		array.setBlock(x + 3, y + 5, z + 2, Blocks.air);
		array.setBlock(x + 3, y + 5, z + 3, Blocks.air);
		array.setBlock(x + 3, y + 5, z + 4, Blocks.air);
		array.setBlock(x + 3, y + 6, z + 0, Blocks.air);
		array.setBlock(x + 3, y + 6, z + 1, Blocks.air);
		array.setBlock(x + 3, y + 6, z + 2, Blocks.air);
		array.setBlock(x + 3, y + 6, z + 3, Blocks.air);
		array.setBlock(x + 3, y + 6, z + 4, Blocks.air);
		array.setBlock(x + 4, y + 4, z + 0, Blocks.air);
		array.setBlock(x + 4, y + 4, z + 1, Blocks.air);
		array.setBlock(x + 4, y + 4, z + 2, Blocks.air);
		array.setBlock(x + 4, y + 4, z + 3, Blocks.air);
		array.setBlock(x + 4, y + 4, z + 4, Blocks.air);
		array.setBlock(x + 4, y + 5, z + 0, Blocks.air);
		array.setBlock(x + 4, y + 5, z + 1, Blocks.air);
		array.setBlock(x + 4, y + 5, z + 2, Blocks.air);
		array.setBlock(x + 4, y + 5, z + 3, Blocks.air);
		array.setBlock(x + 4, y + 5, z + 4, Blocks.air);
		array.setBlock(x + 4, y + 6, z + 0, Blocks.air);
		array.setBlock(x + 4, y + 6, z + 1, Blocks.air);
		array.setBlock(x + 4, y + 6, z + 2, Blocks.air);
		array.setBlock(x + 4, y + 6, z + 3, Blocks.air);
		array.setBlock(x + 4, y + 6, z + 4, Blocks.air);

		return array;
	}

}
