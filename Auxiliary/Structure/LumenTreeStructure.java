package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityPowerTree;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class LumenTreeStructure extends ChromaStructureBase {

	private boolean addTreeSender;
	private int leafStage;

	public void setShowLeaves(int stage) {
		leafStage = stage;
	}

	public void disableLeaves() {
		leafStage = 0;
	}

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
		this.disableLeaves();
	}

	static void getLeafBlocks(FilledBlockArray array, int x, int y, int z) {
		for (int c = 0; c < 16; c++) {
			CrystalElement e = CrystalElement.elements[c];
			int max = TileEntityPowerTree.maxLeafCount(e);
			for (int i = 0; i < max; i++) {
				Coordinate cc = TileEntityPowerTree.getLeafLocation(e, i);
				cc = cc.offset(0, array.getMaxY(), 0); //tile location
				array.setBlock(cc.xCoord, cc.yCoord, cc.zCoord, ChromaBlocks.POWERTREE.getBlockInstance(), e.ordinal());
			}
		}
	}

	static void getTrunkBlocks(FilledBlockArray array, int x, int y, int z) {
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
	}

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		if (this.isDisplay())
			this.setShowLeaves(20);
		this.getTrunkBlocks(array, x, y, z);
		if (leafStage > 0)
			this.getLeafBlocks(array, x, y, z);

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
