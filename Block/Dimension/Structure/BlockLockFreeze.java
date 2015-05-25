/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class BlockLockFreeze extends BlockContainer {

	private IIcon[] activeIcon = new IIcon[2];

	public BlockLockFreeze(Material mat) {
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
		blockIcon = ico.registerIcon("chromaticraft:dimstruct/lockfreeze");
		activeIcon[0] = ico.registerIcon("chromaticraft:dimstruct/lockfreeze_button_inactive");
		activeIcon[1] = ico.registerIcon("chromaticraft:dimstruct/lockfreeze_button_active");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s <= 1 ? blockIcon : activeIcon[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntity te = iba.getTileEntity(x, y, z);
		return s <= 1 ? blockIcon : activeIcon[te instanceof TileEntityLockFreeze && ((TileEntityLockFreeze)te).isActive() ? 1 : 0];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		this.click(world, x, y, z, ep);
		return true;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {
		this.click(world, x, y, z, ep);
	}

	private void click(World world, int x, int y, int z, EntityPlayer ep) {
		TileEntityLockFreeze te = (TileEntityLockFreeze)world.getTileEntity(x, y, z);
		if (te.isActive())
			return;
		int time = 160;
		BlockColoredLock.freezeLocks(world, te.getChannel(), time);
		te.setTime(time);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLockFreeze();
	}

	public static class TileEntityLockFreeze extends TileEntity {

		private int timer = 0;

		public boolean isActive() {
			return timer > 0;
		}

		public int getChannel() {
			return this.getBlockMetadata();
		}

		private void setTime(int time) {
			timer = time;
			ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 1, 0.5F);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void updateEntity() {
			if (timer > 0) {
				int last = timer;
				timer--;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				if (timer == 0) {
					ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 2, 0.5F);
				}
				else if (timer == 5) {
					this.ding(1);
				}
				else if (timer == 10) {
					this.ding(2);
				}
				else if (timer == 20) {
					this.ding(3);
				}
				else if (timer == 40 || timer == 60 || timer == 80) {
					this.ding(4);
				}
			}
		}

		private void ding(int n) {
			ChromaSounds.DING.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, 2, n/2F);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("time", timer);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			timer = NBT.getInteger("time");
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
