/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import net.minecraft.block.material.Material;

public class BlockCrystalPylon extends BlockCrystalTile {

	public BlockCrystalPylon(Material mat) {
		super(mat);
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

}
