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

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.LocationCached;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class BlockShiftLock extends BlockContainer {

	private static int[][] keyCodes = new int[BlockLockKey.LockChannel.lockList.length][16];
	private static int[] gateCodes = new int[BlockLockKey.LockChannel.lockList.length];

	private IIcon[] icons = new IIcon[2];

	public BlockShiftLock(Material mat) {
		super(mat);
		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
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
			icons[i] = ico.registerIcon("chromaticraft:dimstruct/shiftlock_"+i);
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntity te = iba.getTileEntity(x, y, z);
		return te instanceof TileEntityShiftLock && ((TileEntityShiftLock)te).isOpen ? icons[1] : icons[0];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && ReikaItemHelper.matchStackWithBlock(is, this))
			return false;
		if (ep.capabilities.isCreativeMode) {
			TileEntityShiftLock te = (TileEntityShiftLock)world.getTileEntity(x, y, z);
			/*
			if (ChromaItems.SHARD.matchWith(is)) {
				te.addColor(CrystalElement.elements[is.getItemDamage()%16]);
			}
			else if (is == null && ep.isSneaking()) {
				te.colors.clear();
			}
			else if (is != null && ReikaItemHelper.matchStackWithBlock(is, Blocks.obsidian)) {
				world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			}
			 */
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
		return 0;//ChromatiCraft.proxy.shiftLockRender;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te instanceof TileEntityShiftLock && ((TileEntityShiftLock)te).isOpen ? null : ReikaAABBHelper.getBlockAABB(x, y, z);
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
		return new TileEntityShiftLock();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		((TileEntityShiftLock)world.getTileEntity(x, y, z)).breakBlock();
		super.breakBlock(world, x, y, z, b, meta);
	}

	public static class TileEntityShiftLock extends TileEntity implements LocationCached {

		private boolean isOpen;
		private int channel;
		private static Collection<WorldLocation> cache = new HashSet();
		private boolean ticked = false;

		private void cache() {
			cache.add(new WorldLocation(this));
		}

		private void open() {
			isOpen = true;
			ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, Blocks.stone, 2, 1);
			ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, Blocks.stone, 2, 1);
		}

		private void close() {
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
				this.cache();
				this.close();
				ticked = true;
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("open", isOpen);
			NBT.setInteger("room", channel);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			isOpen = NBT.getBoolean("open");
			channel = NBT.getInteger("room");
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

		private void recalcColors() {
			boolean flag = true;
			/*
			for (CrystalElement e : colors) {
				if (keyCodes[channel][e.ordinal()] <= 0) {
					flag = false;
					break;
				}
			}*/
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

		@Override
		public void breakBlock() {
			cache.remove(new WorldLocation(this));
		}

		public int getChannel() {
			return channel;
		}

		private void recalcGate() {
			this.updateState(gateCodes[channel] == 0);
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
	}

	public static boolean isOpen(CrystalElement e, int structIndex) {
		return keyCodes[structIndex][e.ordinal()] > 0;
	}

	public static void openColor(CrystalElement e, World world, int structIndex) {
		//ReikaJavaLibrary.pConsole("add "+e+" @ "+structIndex);
		keyCodes[structIndex][e.ordinal()]++;
		//ReikaJavaLibrary.pConsole(Arrays.deepToString(keyCodes));
		updateTiles(world);
	}

	public static void closeColor(CrystalElement e, World world, int structIndex) {
		//ReikaJavaLibrary.pConsole("remove "+e+" @ "+structIndex);
		keyCodes[structIndex][e.ordinal()]--;
		//ReikaJavaLibrary.pConsole(Arrays.deepToString(keyCodes));
		updateTiles(world);
	}

	private static void updateTiles(World world) {
		for (WorldLocation loc : TileEntityShiftLock.cache) {
			TileEntityShiftLock te = (TileEntityShiftLock)world.getTileEntity(loc.xCoord, loc.yCoord, loc.zCoord);
			if (te == null) {
				ReikaJavaLibrary.pConsole(loc+" has no TileEntity!!");
				continue;
			}
			te.recalc();
		}
	}

	public static void markOpenGate(World world, int structIndex) {
		gateCodes[structIndex]--;
		updateTiles(world);
	}

	public static void markClosedGate(World world, int structIndex) {
		gateCodes[structIndex]++;
		updateTiles(world);
	}

	public static void resetCaches(LocksGenerator g) {
		int n = BlockLockKey.LockChannel.lockList.length;
		keyCodes = new int[n][16];
		for (int i = 0; i < n; i++) {
			gateCodes[i] = g.getNumberGates(i);
		}
	}

}
