/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Locks;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class BlockColoredLock extends BlockDimensionStructureTile {

	//private static int[][] keyCodes = new int[BlockLockKey.LockChannel.lockList.length][16];
	//private static int[] gateCodes = new int[BlockLockKey.LockChannel.lockList.length];
	//private static int[] whiteLock = new int[BlockLockKey.LockChannel.lockList.length];

	private IIcon[] icons = new IIcon[2];

	public BlockColoredLock(Material mat) {
		super(mat);
	}
	/*
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	 */
	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 2; i++) {
			icons[i] = ico.registerIcon("chromaticraft:dimstruct/colorlock_"+i);
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && ReikaItemHelper.matchStackWithBlock(is, this))
			return false;
		if (ep.capabilities.isCreativeMode) {
			TileEntityColorLock te = (TileEntityColorLock)world.getTileEntity(x, y, z);
			if (ChromaItems.SHARD.matchWith(is)) {
				te.addColor(CrystalElement.elements[is.getItemDamage()%16]);
			}
			else if (is == null && ep.isSneaking()) {
				te.colors.clear();
			}
			else if (is != null && ReikaItemHelper.matchStackWithBlock(is, Blocks.obsidian)) {
				world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			}
			te.recalc();
		}
		world.markBlockForUpdate(x, y, z);
		//ReikaJavaLibrary.pConsole(Arrays.deepToString(keyCodes));
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.colorLockRender;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te instanceof TileEntityColorLock && ((TileEntityColorLock)te).isOpen ? null : ReikaAABBHelper.getBlockAABB(x, y, z);
	}

	/*
	@Override
	public int getRenderColor(int meta) {
		return ReikaColorAPI.mixColors(CrystalElement.elements[meta].getColor(), 0xffffff, 0.8F);
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getRenderColor(iba.getBlockMetadata(x, y, z));
	}
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityColorLock();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		super.breakBlock(world, x, y, z, b, meta);
	}

	public static class TileEntityColorLock extends StructureBlockTile<LocksGenerator> {

		private boolean isOpen;
		private int channel;
		private HashSet<CrystalElement> colors = new HashSet();
		private HashSet<CrystalElement> closedColors = new HashSet();
		private boolean ticked = false;
		private int queueTick;

		public TileEntityColorLock addColor(CrystalElement e) {
			colors.add(e);
			closedColors.add(e);
			return this;
		}

		private void open() {
			isOpen = true;
			ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, Blocks.stone, 2, 1);
			ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, Blocks.stone, 2, 1);
		}

		private void close() {
			if (queueTick > 0)
				return;
			isOpen = false;
			ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, Blocks.stone, 2, 1);
			ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, Blocks.stone, 2, 1);
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			if (!ticked) {
				this.close();
				closedColors.addAll(colors);
				ticked = true;
			}
			if (queueTick > 0) {
				queueTick--;
				if (queueTick == 0) {
					this.recalc();
				}
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("open", isOpen);
			NBT.setInteger("room", channel);

			NBTTagList li = new NBTTagList();
			for (CrystalElement e : colors) {
				li.appendTag(new NBTTagInt(e.ordinal()));
			}
			NBT.setTag("colors", li);

			NBTTagList li2 = new NBTTagList();
			for (CrystalElement e : closedColors) {
				li2.appendTag(new NBTTagInt(e.ordinal()));
			}
			NBT.setTag("closed_colors", li2);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			isOpen = NBT.getBoolean("open");
			channel = NBT.getInteger("room");

			colors.clear();
			NBTTagList li = NBT.getTagList("colors", NBTTypes.INT.ID);
			for (Object o : li.tagList) {
				NBTTagInt tag = (NBTTagInt)o;
				colors.add(CrystalElement.elements[tag.func_150287_d()]);
			}

			closedColors.clear();
			NBTTagList li2 = NBT.getTagList("closed_colors", NBTTypes.INT.ID);
			for (Object o : li2.tagList) {
				NBTTagInt tag = (NBTTagInt)o;
				closedColors.add(CrystalElement.elements[tag.func_150287_d()]);
			}

			//ReikaJavaLibrary.pConsole(colors+":"+FMLCommonHandler.instance().getEffectiveSide(), worldObj != null && this.getBlockMetadata() == 0);
		}

		private void recalcColors() {
			boolean flag = true;
			closedColors.clear();
			LocksGenerator g = this.getGenerator();
			if (g.getWhiteLock(channel) <= 0) {
				for (CrystalElement e : colors) {
					if (g.getColorCode(channel, e) <= 0) {
						flag = false;
						closedColors.add(e);
					}
				}
			}
			this.updateState(flag);
		}

		private void updateState(boolean flag) {
			if (flag != isOpen) {
				if (flag)
					this.open();
				else
					this.close();
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public Collection<CrystalElement> getColors() {
			return Collections.unmodifiableCollection(colors);
		}

		public Collection<CrystalElement> getClosedColors() {
			return Collections.unmodifiableCollection(closedColors);
		}

		public int getChannel() {
			return channel;
		}

		public boolean isHeldOpen() {
			return isOpen && queueTick > 0;
		}

		private void recalcGate() {
			//ReikaJavaLibrary.pConsole(((LocksGenerator)DimensionStructureType.LOCKS.getGenerator(uid)).getGateCode(channel), channel == 0);
			this.updateState(this.getGenerator().getGateCode(channel) == 0);
		}

		public void recalc() {
			if (this.getBlockMetadata() == 0)
				this.recalcColors();
			else if (this.getBlockMetadata() == 1)
				this.recalcGate();
		}

		public void setChannel(int ch) {
			channel = ch;
		}

		public void queueTick(int time) {
			queueTick = time;
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.LOCKS;
		}
	}

}
