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
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;

public class BlockCrystalPylon extends BlockCrystalTile {

	public BlockCrystalPylon(Material mat) {
		super(mat);
		this.setBlockUnbreakable();
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		switch(c) {
		case PYLON:
			return null;
		default:
			return this.getBlockAABB(x, y, z);
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		switch(meta) {
		case 0:
			return ChromaIcons.TRANSPARENT.getIcon();
		case 1:
			return ChromaIcons.REPEATER.getIcon();
		}
		return Blocks.stone.getIcon(0, 0);
	}

}
