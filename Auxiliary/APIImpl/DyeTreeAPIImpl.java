package Reika.ChromatiCraft.Auxiliary.APIImpl;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.ChromatiCraft.API.DyeTreeAPI;
import Reika.ChromatiCraft.Block.Dye.BlockDyeLeaf;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class DyeTreeAPIImpl implements DyeTreeAPI {

	public boolean isCCLeaf(Block b) {
		return b instanceof BlockDyeLeaf || b == ChromaBlocks.RAINBOWLEAF.getBlockInstance();
	}

	public ItemStack getDyeSapling(CrystalElementProxy e) {
		return ChromaBlocks.DYESAPLING.getStackOf((CrystalElement)e);
	}

	public ItemStack getDyeFlower(CrystalElementProxy e) {
		return ChromaBlocks.DYEFLOWER.getStackOf((CrystalElement)e);
	}

	public ItemStack getDyeLeaf(CrystalElementProxy e, boolean natural) {
		ChromaBlocks b = natural ? ChromaBlocks.DECAY : ChromaBlocks.DYELEAF;
		return b.getStackOf((CrystalElement)e);
	}

	public Block getRainbowLeaf() {
		return ChromaBlocks.RAINBOWLEAF.getBlockInstance();
	}

	public Block getRainbowSapling() {
		return ChromaBlocks.RAINBOWSAPLING.getBlockInstance();
	}

	@Override
	public Block getDyeSapling() {
		return ChromaBlocks.DYESAPLING.getBlockInstance();
	}

	@Override
	public Block getDyeFlower() {
		return ChromaBlocks.DYEFLOWER.getBlockInstance();
	}

	@Override
	public Block getDyeLeaf(boolean natural) {
		ChromaBlocks b = natural ? ChromaBlocks.DECAY : ChromaBlocks.DYELEAF;
		return b.getBlockInstance();
	}

}
