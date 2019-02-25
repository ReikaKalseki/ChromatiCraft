package Reika.ChromatiCraft.Block.Dimension;

import Reika.ChromatiCraft.ChromatiCraft;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class BlockBedrockCrack extends Block {

	public BlockBedrockCrack(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setResistance(Blocks.bedrock.blockResistance);
		this.setHardness(12);
	}

	public void harvestBlock() {

	}

}
