/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Locks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class BlockLockFreeze extends BlockDimensionStructureTile {

	private IIcon[] activeIcon = new IIcon[2];

	public BlockLockFreeze(Material mat) {
		super(mat);
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
		if (world.isRemote)
			return;
		TileEntityLockFreeze te = (TileEntityLockFreeze)world.getTileEntity(x, y, z);
		if (te.isActive())
			return;
		int time = 160;
		te.freeze(time);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLockFreeze();
	}

	public static class TileEntityLockFreeze extends StructureBlockTile<LocksGenerator> {

		private int timer = 0;

		public boolean isActive() {
			return timer > 0;
		}

		private void freeze(int time) {
			if (this.getGenerator() != null)
				this.getGenerator().freezeLocks(worldObj, this.getChannel(), time);
			this.setTime(time);
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
		public DimensionStructureType getType() {
			return DimensionStructureType.LOCKS;
		}

	}

}
