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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ProgressionTrigger;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Base.TileEntityBase;

public class BlockCrystalPylon extends BlockCrystalTile implements ProgressionTrigger {

	public BlockCrystalPylon(Material mat) {
		super(mat);
		//this.setBlockUnbreakable();
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c == null)
			return null;
		switch(c) {
		case PYLON:
			return null;
		default:
			return this.getBlockAABB(x, y, z);
		}
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		TileEntityBase te = (TileEntityBase)world.getTileEntity(x, y, z);
		if (te instanceof CrystalSource)
			return -1;
		return te.isPlacer(ep) ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z) : -1;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		switch(meta) {
		case 0:
			return ChromaIcons.TRANSPARENT.getIcon();
		case 1:
			return ChromaIcons.REPEATER.getIcon();
		case 2:
			return ChromaIcons.MULTIREPEATER.getIcon();
		}
		return Blocks.stone.getIcon(0, 0);
	}

	@Override
	public ProgressStage[] getTriggers(EntityPlayer ep, World world, int x, int y, int z) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		return c == ChromaTiles.PYLON ? new ProgressStage[]{ProgressStage.PYLON} : null;
	}

}
