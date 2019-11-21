package Reika.ChromatiCraft.Auxiliary.Structure;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.ModInteract.ItemHandlers.CarpenterBlockHandler;


public class CloakingTowerStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int i = -2; i <= 2; i++) {
			int dx = x+i;
			for (int k = -2; k <= 2; k++) {
				int dz = z+k;
				for (int j = -4; j <= 5; j++) {
					int dy = y+j;
					ArrayList<Block> li = new ArrayList();
					if (ModList.CARPENTER.isLoaded()) {
						li.add(CarpenterBlockHandler.Blocks.BLOCK.getBlock());
						li.add(CarpenterBlockHandler.Blocks.SLOPE.getBlock());
						li.add(CarpenterBlockHandler.Blocks.FENCE.getBlock());
						li.add(CarpenterBlockHandler.Blocks.STAIRS.getBlock());
					}
					array.setEmpty(dx, dy, dz, false, false, li.toArray(new Block[li.size()]));

					if (Math.abs(i) == 2 && Math.abs(k) == 2 && j < 5) {
						int m = j == 4 ? 6 : 2;
						array.setBlock(dx, dy, dz, crystalstone, m);
					}
				}

				array.setBlock(dx, y-5, dz, crystalstone, 0);
			}
		}

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				int dx = x+i;
				int dz = z+k;
				int m = Math.abs(i) == 1 && Math.abs(k) == 1 ? 0 : 12;
				array.setBlock(dx, y+5, dz, crystalstone, m);

				m = Math.abs(i) == 1 && Math.abs(k) == 1 ? 8 : 12;
				array.setBlock(dx, y+6, dz, crystalstone, m);
			}
		}

		for (int i = -1; i <= 1; i++) {
			array.setBlock(x+i, y-5, z+2, crystalstone, 12);
			array.setBlock(x+i, y-5, z-2, crystalstone, 12);
			array.setBlock(x+2, y-5, z+i, crystalstone, 12);
			array.setBlock(x-2, y-5, z+i, crystalstone, 12);

			array.setBlock(x+i, y+4, z+2, crystalstone, 1);
			array.setBlock(x+i, y+4, z-2, crystalstone, 1);
			array.setBlock(x+2, y+4, z+i, crystalstone, 1);
			array.setBlock(x-2, y+4, z+i, crystalstone, 1);

			int m = i == 0 ? 12 : 0;

			array.setBlock(x+i, y+5, z+2, crystalstone, m);
			array.setBlock(x+i, y+5, z-2, crystalstone, m);
			array.setBlock(x+2, y+5, z+i, crystalstone, m);
			array.setBlock(x-2, y+5, z+i, crystalstone, m);

			array.setBlock(x-2, y-2-i, z+i, crystalstone, 0);
			array.setBlock(x-2, y+1-i, z+i, crystalstone, 0);

			array.setBlock(x+2, y-2+i, z+i, crystalstone, 0);
			array.setBlock(x+2, y+1+i, z+i, crystalstone, 0);

			array.setBlock(x+i, y-2+i, z-2, crystalstone, 0);
			array.setBlock(x+i, y+1+i, z-2, crystalstone, 0);

			array.setBlock(x+i, y-2-i, z+2, crystalstone, 0);
			array.setBlock(x+i, y+1-i, z+2, crystalstone, 0);
		}

		this.setTile(array, x, y, z, ChromaTiles.CLOAKING);

		return array;
	}

}
