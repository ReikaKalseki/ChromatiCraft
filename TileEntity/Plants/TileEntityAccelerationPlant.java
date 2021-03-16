/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Plants;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityMagicPlant;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.ModRegistry.ModWoodList;


public class TileEntityAccelerationPlant extends TileEntityMagicPlant {

	@Override
	public ForgeDirection getGrowthDirection() {
		return null;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PLANTACCEL;
	}

	public boolean isActive() {
		return true;
	}

	@Override
	public boolean isTickingTE() {
		return false;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean isPlantable(World world, int x, int y, int z) {
		if (this.isBlockViable(world, x, y+1, z))
			return true;
		if (this.isBlockViable(world, x, y-1, z))
			return true;
		BlockArray b = new BlockArray();
		b.recursiveAddWithBoundsMetadata(world, x, y, z, this.getTile().getBlock(), this.getTile().getBlockMetadata(), x, y-256, z, x, y+256, z);
		b.recursiveAddWithBoundsMetadata(world, x, y+1, z, this.getTile().getBlock(), this.getTile().getBlockMetadata(), x, y-256, z, x, y+256, z);
		b.recursiveAddWithBoundsMetadata(world, x, y-1, z, this.getTile().getBlock(), this.getTile().getBlockMetadata(), x, y-256, z, x, y+256, z);
		if (!(this.isBlockViable(world, x, b.getMaxY()+1, z)))
			if (!(this.isBlockViable(world, x, b.getMinY()-1, z)))
				return false;
		if (ChromaTiles.getTile(world, x, y+1, z) == ChromaTiles.PLANTACCEL)
			return true;
		if (ChromaTiles.getTile(world, x, y-1, z) == ChromaTiles.PLANTACCEL)
			return true;
		return false;
	}

	private boolean isBlockViable(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.leaves || b == Blocks.leaves2 || ModWoodList.isModLeaf(b, world.getBlockMetadata(x, y, z)))
			return true;
		if (b.isOpaqueCube() && b.getMaterial().isSolid())
			return true;
		return false;
	}

}
