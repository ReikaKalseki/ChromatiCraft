package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Magic.CastingTuning.CastingTuningManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class CastingL2Structure extends CastingL1Structure {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = super.getArray(world, x, y, z);

		for (Coordinate c : CastingTuningManager.instance.getTuningKeyLocations()) {
			array.addBlock(x+c.xCoord, y+c.yCoord+1, z+c.zCoord, ChromaBlocks.RUNE.getBlockInstance());
		}

		for (int i = -5; i <= 5; i++) {
			for (int k = -5; k <= 5; k++) {
				int dx = x+i;
				int dz = z+k;
				array.remove(dx, y, dz);
				array.addBlock(dx, y, dz, crystalstone, 0);
				array.addBlock(dx, y, dz, ChromaBlocks.RUNE.getBlockInstance());
			}
		}

		for (int i = -5; i <= 5; i++) {
			if (i != 0 && Math.abs(i) != 3) {
				array.setBlock(x-6, y, z+i, Blocks.quartz_block, 0);
				array.setBlock(x+6, y, z+i, Blocks.quartz_block, 0);
				array.setBlock(x+i, y, z-6, Blocks.quartz_block, 0);
				array.setBlock(x+i, y, z+6, Blocks.quartz_block, 0);
			}
		}

		for (int i = -3; i <= 3; i++) {
			int dy = y+1;
			array.remove(x-3, dy, z+i);
			array.remove(x+3, dy, z+i);
			array.remove(x+i, dy, z-3);
			array.remove(x+i, dy, z+3);
		}

		for (int i = -2; i <= 2; i++) {
			array.remove(x-2, y, z+i);
			array.remove(x+2, y, z+i);
			array.remove(x+i, y, z-2);
			array.remove(x+i, y, z+2);
		}

		array.remove(x, y, z);

		array.setBlock(x-6, y+5, z-6, Blocks.redstone_block);
		array.setBlock(x+6, y+5, z-6, Blocks.redstone_block);
		array.setBlock(x+6, y+5, z+6, Blocks.redstone_block);
		array.setBlock(x-6, y+5, z+6, Blocks.redstone_block);

		array.setBlock(x, y+6, z-6, Blocks.gold_block);
		array.setBlock(x, y+6, z+6, Blocks.gold_block);
		array.setBlock(x+6, y+6, z, Blocks.gold_block);
		array.setBlock(x-6, y+6, z, Blocks.gold_block);
		return array;
	}

}
