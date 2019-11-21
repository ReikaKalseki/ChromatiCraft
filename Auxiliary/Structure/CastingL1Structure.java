package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class CastingL1Structure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int i = -6; i <= 6; i++) {
			for (int k = 0; k < 6; k++) {
				int dy = y+k;
				array.setEmpty(x-6, dy, z+i, true, true);
				array.setEmpty(x+6, dy, z+i, true, true);
				array.setEmpty(x+i, dy, z-6, true, true);
				array.setEmpty(x+i, dy, z+6, true, true);
			}
		}

		for (int i = 1; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+1+dir.offsetY;
			int dz = z+dir.offsetZ;
			array.setEmpty(dx, dy, dz, false, false);
		}

		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			for (int k = 3; k <= 5; k++) {
				int dx = x+k*dir.offsetX;
				int dz = z+k*dir.offsetZ;
				array.addBlock(dx, y, dz, crystalstone, 0);
				array.addBlock(dx, y, dz, ChromaBlocks.RUNE.getBlockInstance());
			}

			int dx = x+dir.offsetX*6;
			int dz = z+dir.offsetZ*6;
			for (int k = 1; k <= 5; k++) {
				int meta2 = k == 1 ? 8 : 2;
				int dy = y+k;
				array.setBlock(dx, dy, dz, crystalstone, meta2);
			}
		}

		for (int i = -6; i <= 6; i++) {
			array.setBlock(x-6, y, z+i, crystalstone, 0);
			array.setBlock(x+6, y, z+i, crystalstone, 0);
			array.setBlock(x+i, y, z-6, crystalstone, 0);
			array.setBlock(x+i, y, z+6, crystalstone, 0);
		}

		for (int k = 1; k <= 4; k++) {
			int meta2 = k == 1 ? 0 : 2;
			int dy = y+k;
			array.setBlock(x+6, dy, z+6, crystalstone, meta2);
			array.setBlock(x-6, dy, z+6, crystalstone, meta2);
			array.setBlock(x+6, dy, z-6, crystalstone, meta2);
			array.setBlock(x-6, dy, z-6, crystalstone, meta2);
		}

		for (int k = 1; k <= 6; k++) {
			int meta2 = k == 1 || k == 5 ? 0 : (k == 6 ? 7 : 2);
			int dy = y+k;
			array.setBlock(x+6, dy, z+3, crystalstone, meta2);
			array.setBlock(x+6, dy, z-3, crystalstone, meta2);
			array.setBlock(x-6, dy, z+3, crystalstone, meta2);
			array.setBlock(x-6, dy, z-3, crystalstone, meta2);
			array.setBlock(x+3, dy, z-6, crystalstone, meta2);
			array.setBlock(x-3, dy, z-6, crystalstone, meta2);
			array.setBlock(x-3, dy, z+6, crystalstone, meta2);
			array.setBlock(x+3, dy, z+6, crystalstone, meta2);
		}

		for (int i = -5; i <= 5; i++) {
			if (i != 3 && i != -3 && i != 0) {
				int dy = Math.abs(i) < 3 ? y+6 : y+5;
				array.setBlock(x-6, dy, z+i, crystalstone, 1);
				array.setBlock(x+6, dy, z+i, crystalstone, 1);
				array.setBlock(x+i, dy, z-6, crystalstone, 1);
				array.setBlock(x+i, dy, z+6, crystalstone, 1);
			}
		}

		for (int i = -3; i <= 3; i++) {
			for (int k = 0; k <= 1; k++) {
				if (k == 0 || Math.abs(i)%2 == 1) {
					int dy = y+k;
					array.addBlock(x-3, dy, z+i, crystalstone, 0);
					array.addBlock(x+3, dy, z+i, crystalstone, 0);
					array.addBlock(x+i, dy, z-3, crystalstone, 0);
					array.addBlock(x+i, dy, z+3, crystalstone, 0);

					array.addBlock(x-3, dy, z+i, ChromaBlocks.RUNE.getBlockInstance());
					array.addBlock(x+3, dy, z+i, ChromaBlocks.RUNE.getBlockInstance());
					array.addBlock(x+i, dy, z-3, ChromaBlocks.RUNE.getBlockInstance());
					array.addBlock(x+i, dy, z+3, ChromaBlocks.RUNE.getBlockInstance());
				}
			}
		}

		array.setBlock(x-6, y+5, z-6, Blocks.coal_block);
		array.setBlock(x+6, y+5, z-6, Blocks.coal_block);
		array.setBlock(x+6, y+5, z+6, Blocks.coal_block);
		array.setBlock(x-6, y+5, z+6, Blocks.coal_block);

		array.setBlock(x, y+6, z-6, Blocks.lapis_block);
		array.setBlock(x, y+6, z+6, Blocks.lapis_block);
		array.setBlock(x+6, y+6, z, Blocks.lapis_block);
		array.setBlock(x-6, y+6, z, Blocks.lapis_block);

		array.addBlock(x+1, y, z, crystalstone, 0);
		array.addBlock(x-1, y, z, crystalstone, 0);
		array.addBlock(x, y, z+1, crystalstone, 0);
		array.addBlock(x, y, z-1, crystalstone, 0);

		array.addBlock(x+1, y, z, ChromaBlocks.RUNE.getBlockInstance());
		array.addBlock(x-1, y, z, ChromaBlocks.RUNE.getBlockInstance());
		array.addBlock(x, y, z+1, ChromaBlocks.RUNE.getBlockInstance());
		array.addBlock(x, y, z-1, ChromaBlocks.RUNE.getBlockInstance());

		array.addBlock(x+1, y+1, z, Blocks.fire); //to prevent lightning causing issues
		array.addBlock(x-1, y+1, z, Blocks.fire);
		array.addBlock(x, y+1, z+1, Blocks.fire);
		array.addBlock(x, y+1, z-1, Blocks.fire);

		return array;
	}

}
