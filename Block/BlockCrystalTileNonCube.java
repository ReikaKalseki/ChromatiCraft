/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;

public class BlockCrystalTileNonCube extends BlockCrystalTile {

	public BlockCrystalTileNonCube(Material mat) {
		super(mat);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		switch(c) {
		case ACCELERATOR:
		case AURAPOINT:
			return null;
		default:
			return this.getBlockAABB(x, y, z);
		}
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		AxisAlignedBB box = this.getBlockAABB(x, y, z);
		double r = 0;
		switch(c) {
		case ACCELERATOR:
			r = 0.3125;
			box = this.getBlockAABB(x, y, z).contract(r, r, r);
		case AURAPOINT:
			r = 0.125;
			box = this.getBlockAABB(x, y, z).contract(r, r, r);
		default:
			break;
		}
		this.setBounds(box, x, y, z);
		return box;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		switch(meta) {
		case 0:
		case 1:
			return ChromaIcons.TRANSPARENT.getIcon();
		case 2:
			return ChromaIcons.GLOWFRAME_TRANS.getIcon();
		}
		return Blocks.stone.getIcon(0, 0);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:transparent");
	}

}
