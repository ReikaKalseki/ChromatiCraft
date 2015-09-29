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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;

public class BlockCrystalFence extends Block {

	private IIcon input;
	private IIcon output;
	private IIcon end;

	public BlockCrystalFence(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(1);
		this.setResistance(600);

		this.setBlockBounds(0.25F, 0, 0.25F, 0.75F, 2, 0.75F);
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
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		CrystalFenceAuxTile te = (CrystalFenceAuxTile)iba.getTileEntity(x, y, z);
		if (s == te.input.ordinal()) {
			return input;
		}
		else if (s == te.output.ordinal()) {
			return output;
		}
		else if (s <= 1) {
			return end;
		}
		else {
			return blockIcon;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/fence_basic");
		output = ico.registerIcon("chromaticraft:basic/fence_output");
		input = ico.registerIcon("chromaticraft:basic/fence_input");
		end = ico.registerIcon("chromaticraft:basic/fence_end");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.25F, 0, 0.25F, 0.75F, 1, 0.75F);
	}

	/*
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		CrystalFenceAuxTile te = new CrystalFenceAuxTile();
		world.setTileEntity(x, y, z, te);

		for (int k = 1; k < 12; k++) {
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
		if (con != null) {
			te.setTile(con);
			//world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			te.addToFence();
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		CrystalFenceAuxTile te = (CrystalFenceAuxTile)world.getTileEntity(x, y, z);
		if (te != null) {
			te.removeFromFence();
		}
		super.breakBlock(world, x, y, z, old, oldmeta);
	}
	 */
	public static class CrystalFenceAuxTile extends TileEntity {

		//private int tileX;
		//private int tileY;
		//private int tileZ;

		private ForgeDirection input = ForgeDirection.DOWN;
		private ForgeDirection output = ForgeDirection.DOWN;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public ForgeDirection getInput() {
			return input;
		}

		public ForgeDirection getOutput() {
			return output;
		}

		public void setInput(ForgeDirection dir) {
			input = dir;
		}

		public void setOutput(ForgeDirection dir) {
			output = dir;
		}
		/*
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
		 */
		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			//NBT.setInteger("tx", tileX);
			//NBT.setInteger("ty", tileY);
			//NBT.setInteger("tz", tileZ);

			NBT.setInteger("in", input.ordinal());
			NBT.setInteger("out", output.ordinal());
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			//tileX = NBT.getInteger("tx");
			//tileY = NBT.getInteger("ty");
			//tileZ = NBT.getInteger("tz");

			input = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("in")];
			output = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("out")];
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
