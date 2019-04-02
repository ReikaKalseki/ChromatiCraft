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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.LinkedTile;
import Reika.ChromatiCraft.Base.BlockAttachableMini;
import Reika.ChromatiCraft.Base.TileEntity.LinkedTileDedicated;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockRedstonePod extends BlockAttachableMini {

	public BlockRedstonePod(Material mat) {
		super(mat);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ((BlockTieredOre)ChromaBlocks.TIEREDORE.getBlockInstance()).getGeodeIcon(3);
	}

	@Override
	public int getColor(IBlockAccess iba, int x, int y, int z) {
		return 0xff3030;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void createFX(World world, int x, int y, int z, double dx, double dy, double dz, Random r) {
		ReikaParticleHelper.spawnColoredParticleAt(world, dx, dy, dz, this.getColor(world, x, y, z));
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityRedstonePod();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		super.onNeighborBlockChange(world, x, y, z, b);
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedstonePod) {
			TileEntityRedstonePod tp = (TileEntityRedstonePod)te;
			if (tp.isPrimary() && tp.isLinked()) {
				WorldLocation loc = tp.getLinkTarget();
				if (!loc.isWithinSquare(world, x, y, z, 2))
					this.updateNeighbors(world, loc.xCoord, loc.yCoord, loc.zCoord, loc.getBlockMetadata());
			}
		}
	}

	@Override
	protected void onBlockBreak(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedstonePod) {
			((TileEntityRedstonePod)te).drop();
		}
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int s) {
		return this.isProvidingStrongPower(iba, x, y, z, s);
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntity te = iba.getTileEntity(x, y, z);
		ForgeDirection dir = this.getSide(iba, x, y, z);
		if (te instanceof TileEntityRedstonePod && dir.ordinal() == s) {
			TileEntityRedstonePod tp = (TileEntityRedstonePod)te;
			if (!tp.isPrimary() && tp.isLinked()) {
				WorldLocation loc = tp.getLinkTarget();
				TileEntity te2 = loc.getTileEntity();
				if (te2 instanceof TileEntityRedstonePod) {
					ForgeDirection dir2 = this.getSide(iba, te2.xCoord, te2.yCoord, te2.zCoord).getOpposite();
					int dx = te2.xCoord+dir2.offsetX;
					int dy = te2.yCoord+dir2.offsetY;
					int dz = te2.zCoord+dir2.offsetZ;
					if (dx == x && dy == y && dz == z)
						return 0;
					return loc.getWorld().getBlockPowerInput(dx, dy, dz);
				}
			}
		}
		return 0;
	}

	public static class TileEntityRedstonePod extends LinkedTileDedicated {

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);


		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);


		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord);
		}

		@Override
		public boolean canDrop(EntityPlayer ep) {
			return true;
		}

		@Override
		public void syncAllData(boolean fullNBT) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void setPlacer(EntityPlayer ep) {

		}

		@Override
		protected ItemStack getDrop() {
			return new ItemStack(this.getBlockType());
		}

		@Override
		public boolean canLinkTo(World world, int x, int y, int z) {
			return true;
		}

		@Override
		protected void createRandomLinkID() {

		}

		@Override
		public void assignLinkID(LinkedTile other) {

		}

	}

}
