/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
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
