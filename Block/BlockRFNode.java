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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.ChromatiCraft.Base.BlockAttachableMini;
import Reika.ChromatiCraft.ModInterface.RFWeb;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockRFNode extends BlockAttachableMini {

	public BlockRFNode(Material mat) {
		super(mat);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		IIcon icon = Blocks.redstone_block.blockIcon;
		Fluid f = FluidRegistry.getFluid("redstone");
		if (f != null)
			icon = f.getIcon();
		blockIcon = icon;
	}

	@Override
	public int getColor(IBlockAccess iba, int x, int y, int z) {
		return 0xff3030;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void createFX(World world, int x, int y, int z, double dx, double dy, double dz, Random r) {

	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityRFNode();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		super.onNeighborBlockChange(world, x, y, z, b);
	}

	@Override
	protected void onBlockBreak(World world, int x, int y, int z) {
		RFWeb.getWeb(world).removeNode(world, x, y, z);
	}

	public static class TileEntityRFNode extends TileEntity {

		@Override
		public void updateEntity() {
			if (worldObj.isRemote) {

			}
			else {
				World world = worldObj;
				if (world.getTotalWorldTime()%8 == 0)
					RFWeb.getWeb(world).addNode(world, xCoord, yCoord, zCoord, ((BlockRFNode)this.getBlockType()).getSide(world, xCoord, yCoord, zCoord).getOpposite());
			}
		}

	}

}
