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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalFence;

public class BlockCrystalFence extends Block {

	public BlockCrystalFence(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(1);
		this.setResistance(600);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new CrystalFenceAuxTile();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		CrystalFenceAuxTile te = new CrystalFenceAuxTile();
		world.setTileEntity(x, y, z, te);

		for (int k = 1; k < 8; k++) {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+k*dir.offsetX;
				int dz = z+k*dir.offsetZ;
				ChromaTiles c = ChromaTiles.getTile(world, dx, y, dz);
				if (c == ChromaTiles.FENCE) {
					this.addToFence(te, (TileEntityCrystalFence)world.getTileEntity(dx, y, dz));
					return;
				}
				else if (world.getBlock(dx, y, dz) == this) {
					this.addToFence(te, ((CrystalFenceAuxTile)world.getTileEntity(dx, y, dz)).getFenceController());
				}
			}
		}
	}

	private void addToFence(CrystalFenceAuxTile te, TileEntityCrystalFence con) {
		te.setTile(con);
		//world.setBlockMetadataWithNotify(x, y, z, 1, 3);
		te.addToFence();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		CrystalFenceAuxTile te = (CrystalFenceAuxTile)world.getTileEntity(x, y, z);
		if (te != null) {
			te.removeFromFence();
		}
		super.breakBlock(world, x, y, z, old, oldmeta);
	}

	public static class CrystalFenceAuxTile extends TileEntity {

		private int tileX;
		private int tileY;
		private int tileZ;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void setTile(TileEntityCrystalFence te) {
			tileX = te.xCoord;
			tileY = te.yCoord;
			tileZ = te.zCoord;
		}

		public void addToFence() {
			TileEntityCrystalFence te = this.getFenceController();
			if (te != null)
				te.addCoordinate(worldObj, xCoord, yCoord, zCoord);
		}

		public void removeFromFence() {
			TileEntityCrystalFence te = this.getFenceController();
			if (te != null)
				te.removeCoordinate(worldObj, xCoord, yCoord, zCoord);
		}

		public TileEntityCrystalFence getFenceController() {
			TileEntity te = worldObj.getTileEntity(tileX, tileY, tileZ);
			return te instanceof TileEntityCrystalFence ? (TileEntityCrystalFence)te : null;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("tx", tileX);
			NBT.setInteger("ty", tileY);
			NBT.setInteger("tz", tileZ);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			tileX = NBT.getInteger("tx");
			tileY = NBT.getInteger("ty");
			tileZ = NBT.getInteger("tz");
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
		}

	}

}
