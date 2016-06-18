package Reika.ChromatiCraft.Block.Worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import Reika.ChromatiCraft.ChromatiCraft;


public class BlockDecoGen extends Block {

	public BlockDecoGen(Material mat) {
		super(mat);

		this.setHardness(2);
		this.setResistance(20);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	public static enum Deco {
		OCEANSPIKES();

		public static final Deco[] list = values();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.decoRender;
	}

}
