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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@Deprecated
public class BlockFiberOptic extends BlockCrystalTile {

	private static IIcon inner;
	private static IIcon outer;

	public BlockFiberOptic(Material mat) {
		super(mat);
		this.setStepSound(soundTypeGlass);
		this.setLightLevel(0.5F);
		this.setHardness(0.05F);
	}

	@Override
	public int getRenderType() {
		return -1;//ChromatiCraft.proxy.fiberRender;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return 8;
	}

	@Override
	public int getRenderBlockPass() {
		return 0;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		inner = ico.registerIcon("chromaticraft:fiber");
		outer = ico.registerIcon("chromaticraft:fiber_end");
	}
	/*
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block id) {
		TileEntityFiberOptic te = (TileEntityFiberOptic)world.getTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityFiberOptic te = (TileEntityFiberOptic)world.getTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		double d = 0.125;
		TileEntityFiberOptic te = (TileEntityFiberOptic)world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		float minx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.WEST, true) ? 0 : 0.375F;
		float maxx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.EAST, true) ? 1 : 0.625F;
		float minz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.NORTH, true) ? 0 : 0.375F;
		float maxz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.SOUTH, true) ? 1 : 0.625F;
		float miny = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.DOWN, true) ? 0 : 0.375F;
		float maxy = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.UP, true) ? 1 : 0.625F;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x+minx, y+miny, z+minz, x+maxx, y+maxy, z+maxz);
		this.setBounds(box, x, y, z);
		return box;
	}
	 */
	@Override
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	public static IIcon getOuterIcon() {
		return outer;
	}

	public static IIcon getInnerIcon() {
		return inner;
	}

}
