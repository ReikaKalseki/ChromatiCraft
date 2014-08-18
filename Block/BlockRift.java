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

import Reika.ChromatiCraft.Base.BlockModelledChromaTile;
import Reika.ChromatiCraft.TileEntity.TileEntityRift;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRift extends BlockModelledChromaTile {

	private IIcon halo;

	public BlockRift(Material par2Material) {
		super(par2Material);
		this.setHardness(1000);
		this.setResistance(10000);
		stepSound = soundTypeCloth;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return ReikaAABBHelper.getBlockAABB(x, y, z);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block id) {
		((TileEntityRift)world.getTileEntity(x, y, z)).passThrough();
	}

	@Override
	public final boolean canProvidePower() {
		return true;
	}
	/*
	@Override
	public boolean shouldCheckWeakPower(World world, int x, int y, int z, int side)
	{
		return true;
	}*/

	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int s)
	{
		return ((TileEntityRift)iba.getTileEntity(x, y, z)).getRedstoneLevel(ForgeDirection.VALID_DIRECTIONS[s]);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:rift");
		halo = ico.registerIcon("chromaticraft:rift_halo");
	}

	public IIcon getHalo() {
		return halo;
	}


}
