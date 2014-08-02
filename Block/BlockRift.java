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
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.ChromatiCraft.Base.BlockModelledChromaTile;
import Reika.ChromatiCraft.TileEntity.TileEntityRift;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;

public class BlockRift extends BlockModelledChromaTile {

	private Icon halo;

	public BlockRift(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setHardness(1000);
		this.setResistance(10000);
		stepSound = soundClothFootstep;
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
	public Icon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		((TileEntityRift)world.getBlockTileEntity(x, y, z)).passThrough();
	}

	@Override
	public final boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(World world, int x, int y, int z, int side)
	{
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int s)
	{
		return ((TileEntityRift)iba.getBlockTileEntity(x, y, z)).getRedstoneLevel(ForgeDirection.VALID_DIRECTIONS[s]);
	}

	@Override
	public void registerIcons(IconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:rift");
		halo = ico.registerIcon("chromaticraft:rift_halo");
	}

	public Icon getHalo() {
		return halo;
	}


}
