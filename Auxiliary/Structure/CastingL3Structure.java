package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class CastingL3Structure extends CastingL2Structure {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = super.getArray(world, x, y, z);

		for (int i = -7; i <= 7; i++) {
			array.addBlock(x-7, y, z+i, b, 0);
			array.addBlock(x+7, y, z+i, b, 0);
			array.addBlock(x+i, y, z-7, b, 0);
			array.addBlock(x+i, y, z+7, b, 0);

			array.addBlock(x-7, y, z+i, b, 12);
			array.addBlock(x+7, y, z+i, b, 12);
			array.addBlock(x+i, y, z-7, b, 12);
			array.addBlock(x+i, y, z+7, b, 12);
		}

		for (int i = -8; i <= 8; i++) {
			array.setBlock(x-8, y, z+i, Blocks.obsidian);
			array.setBlock(x+8, y, z+i, Blocks.obsidian);
			array.setBlock(x+i, y, z-8, Blocks.obsidian);
			array.setBlock(x+i, y, z+8, Blocks.obsidian);
		}

		for (int i = 1; i <= 4; i++) {
			int dy = y+i;
			if (i == 3) {
				Block b2 = ChromaBlocks.RUNE.getBlockInstance();
				array.setBlock(x-2, dy, z-8, b2);
				array.setBlock(x-6, dy, z-8, b2);
				array.setBlock(x+2, dy, z-8, b2);
				array.setBlock(x+6, dy, z-8, b2);

				array.setBlock(x-2, dy, z+8, b2);
				array.setBlock(x-6, dy, z+8, b2);
				array.setBlock(x+2, dy, z+8, b2);
				array.setBlock(x+6, dy, z+8, b2);

				array.setBlock(x-8, dy, z-2, b2);
				array.setBlock(x-8, dy, z-6, b2);
				array.setBlock(x-8, dy, z+2, b2);
				array.setBlock(x-8, dy, z+6, b2);

				array.setBlock(x+8, dy, z+6, b2);
				array.setBlock(x+8, dy, z+2, b2);
				array.setBlock(x+8, dy, z-6, b2);
				array.setBlock(x+8, dy, z-2, b2);
			}
			else {
				Block b2 = i == 4 ? ChromaTiles.REPEATER.getBlock() : b;
				int meta2 = i == 4 ? ChromaTiles.REPEATER.getBlockMetadata() : 0;
				int meta3 = i == 1 ? StoneTypes.RESORING.ordinal() : meta2;
				array.setBlock(x-2, dy, z-8, b2, meta2);
				array.setBlock(x-6, dy, z-8, b2, meta2);
				array.setBlock(x+2, dy, z-8, b2, meta2);
				array.setBlock(x+6, dy, z-8, b2, meta2);

				array.setBlock(x-2, dy, z+8, b2, meta2);
				array.setBlock(x-6, dy, z+8, b2, meta2);
				array.setBlock(x+2, dy, z+8, b2, meta2);
				array.setBlock(x+6, dy, z+8, b2, meta2);

				array.setBlock(x-8, dy, z-2, b2, meta2);
				array.setBlock(x-8, dy, z-6, b2, meta2);
				array.setBlock(x-8, dy, z+2, b2, meta2);
				array.setBlock(x-8, dy, z+6, b2, meta2);

				array.setBlock(x+8, dy, z+6, b2, meta2);
				array.setBlock(x+8, dy, z+2, b2, meta2);
				array.setBlock(x+8, dy, z-6, b2, meta2);
				array.setBlock(x+8, dy, z-2, b2, meta2);

				if (meta3 != meta2) {
					array.addBlock(x-2, dy, z-8, b2, meta3);
					array.addBlock(x-6, dy, z-8, b2, meta3);
					array.addBlock(x+2, dy, z-8, b2, meta3);
					array.addBlock(x+6, dy, z-8, b2, meta3);

					array.addBlock(x-2, dy, z+8, b2, meta3);
					array.addBlock(x-6, dy, z+8, b2, meta3);
					array.addBlock(x+2, dy, z+8, b2, meta3);
					array.addBlock(x+6, dy, z+8, b2, meta3);

					array.addBlock(x-8, dy, z-2, b2, meta3);
					array.addBlock(x-8, dy, z-6, b2, meta3);
					array.addBlock(x-8, dy, z+2, b2, meta3);
					array.addBlock(x-8, dy, z+6, b2, meta3);

					array.addBlock(x+8, dy, z+6, b2, meta3);
					array.addBlock(x+8, dy, z+2, b2, meta3);
					array.addBlock(x+8, dy, z-6, b2, meta3);
					array.addBlock(x+8, dy, z-2, b2, meta3);
				}
			}
		}

		for (int i = 1; i <= 3; i++) {
			int dy = y+i;
			int meta = i == 1 ? 0 : i == 2 ? 2 : 7;
			array.setBlock(x-8, dy, z-8, b, meta);
			array.setBlock(x+8, dy, z-8, b, meta);
			array.setBlock(x-8, dy, z+8, b, meta);
			array.setBlock(x+8, dy, z+8, b, meta);
		}

		array.setBlock(x-6, y+5, z-6, Blocks.glowstone);
		array.setBlock(x+6, y+5, z-6, Blocks.glowstone);
		array.setBlock(x+6, y+5, z+6, Blocks.glowstone);
		array.setBlock(x-6, y+5, z+6, Blocks.glowstone);

		array.setBlock(x, y+6, z-6, Blocks.diamond_block);
		array.setBlock(x, y+6, z+6, Blocks.diamond_block);
		array.setBlock(x+6, y+6, z, Blocks.diamond_block);
		array.setBlock(x-6, y+6, z, Blocks.diamond_block);

		array.remove(x, y, z);

		return array;
	}

}
