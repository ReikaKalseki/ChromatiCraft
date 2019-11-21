package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class LumenTreeStructure extends ChromaStructureBase {

	private boolean addTreeSender;

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		for (int i = 0; i <= 12; i++) {
			int dy = y-i;
			if (i == 0) {
				array.setBlock(x, dy, z-1, Blocks.glass);
				array.setBlock(x+1, dy, z, Blocks.glass);
				array.setBlock(x+1, dy, z-1, Blocks.glass);
			}
			else {
				int meta = (i == 3 || i == 5 || i == 7 || i == 9) ? 15 : 11;
				array.setBlock(x, dy, z, crystalstone, meta);
				array.setBlock(x, dy, z-1, crystalstone, meta);
				array.setBlock(x+1, dy, z, crystalstone, meta);
				array.setBlock(x+1, dy, z-1, crystalstone, meta);
			}

			if (i > 1) {
				array.addEmpty(x-1, dy, z, false, false);
				array.addEmpty(x-1, dy, z-1, false, false);
				array.addEmpty(x-1, dy, z-2, false, false);
				array.addEmpty(x-1, dy, z+1, false, false);
				array.addEmpty(x+2, dy, z, false, false);
				array.addEmpty(x+2, dy, z-1, false, false);
				array.addEmpty(x+2, dy, z+1, false, false);
				array.addEmpty(x+2, dy, z-2, false, false);
				array.addEmpty(x, dy, z-2, false, false);
				array.addEmpty(x+1, dy, z-2, false, false);
				array.addEmpty(x, dy, z+1, false, false);
				array.addEmpty(x+1, dy, z+1, false, false);

				Block b2 = ChromaBlocks.POWERTREE.getBlockInstance();
				array.addBlock(x-1, dy, z, b2);
				array.addBlock(x-1, dy, z-1, b2);
				array.addBlock(x-1, dy, z-2, b2);
				array.addBlock(x-1, dy, z+1, b2);
				array.addBlock(x+2, dy, z, b2);
				array.addBlock(x+2, dy, z-1, b2);
				array.addBlock(x+2, dy, z+1, b2);
				array.addBlock(x+2, dy, z-2, b2);
				array.addBlock(x, dy, z-2, b2);
				array.addBlock(x+1, dy, z-2, b2);
				array.addBlock(x, dy, z+1, b2);
				array.addBlock(x+1, dy, z+1, b2);
			}
		}

		array.setBlock(x-1, y-1, z, crystalstone, 14);
		array.setBlock(x-1, y-1, z-1, crystalstone, 14);

		array.setBlock(x+2, y-1, z, crystalstone, 14);
		array.setBlock(x+2, y-1, z-1, crystalstone, 14);

		array.setBlock(x, y-1, z-2, crystalstone, 14);
		array.setBlock(x+1, y-1, z-2, crystalstone, 14);

		array.setBlock(x, y-1, z+1, crystalstone, 14);
		array.setBlock(x+1, y-1, z+1, crystalstone, 14);

		FilledBlockArray treeSend = ChromaStructures.TREE_SENDER.getArray(world, x, y, z);
		if (addTreeSender) {
			array.addAll(treeSend);
		}
		else {
			for (Coordinate c : treeSend.keySet()) {
				array.addEmpty(c.xCoord, c.yCoord, c.zCoord, false, false);
				Block bk = treeSend.getBlockAt(c.xCoord, c.yCoord, c.zCoord);
				int meta = treeSend.getMetaAt(c.xCoord, c.yCoord, c.zCoord);
				array.addBlock(c.xCoord, c.yCoord, c.zCoord, bk, meta);
			}
		}

		return array;
	}

}
