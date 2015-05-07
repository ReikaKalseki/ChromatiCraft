/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class BlockColoredLock extends BlockContainer {

	private IIcon[] icons = new IIcon[2];

	public BlockColoredLock(Material mat) {
		super(mat);
		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 2; i++) {
			icons[i] = ico.registerIcon("chromaticraft:basic/colorlock_"+i);
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntity te = iba.getTileEntity(x, y, z);
		return te instanceof TileEntityColorLock && ((TileEntityColorLock)te).isOpen ? icons[1] : icons[0];
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te instanceof TileEntityColorLock && ((TileEntityColorLock)te).isOpen ? null : ReikaAABBHelper.getBlockAABB(x, y, z);
	}

	@Override
	public int getRenderColor(int meta) {
		return ReikaColorAPI.mixColors(CrystalElement.elements[meta].getColor(), 0xffffff, 0.8F);
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getRenderColor(iba.getBlockMetadata(x, y, z));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityColorLock();
	}

	public static class TileEntityColorLock extends TileEntity {

		private boolean isOpen;

		public void open() {
			isOpen = true;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public void close() {
			isOpen = false;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("open", isOpen);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			isOpen = NBT.getBoolean("open");
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
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

	}

}
