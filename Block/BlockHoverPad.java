package Reika.ChromatiCraft.Block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import Reika.ChromatiCraft.ChromatiCraft;


public class BlockHoverPad extends Block {

	public BlockHoverPad(Material mat) {
		super(mat);

		this.setResistance(10);
		this.setHardness(2);

		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

}
