/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;
import java.util.Arrays;

import mrtjp.projectred.api.IBundledTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.CompoundConsole;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Items.Tools.ItemCrystalCard;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Instantiable.Data.Sorter;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.RegionMap;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Interfaces.TileEntity.ToggleTile;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

@Strippable(value={"mrtjp.projectred.api.IBundledTile"})
public class TileEntityCrystalConsole extends InventoriedChromaticBase implements BreakAction, IBundledTile {

	public static final int SLOTS = 4;

	private int slotCount = SLOTS;

	private boolean valid;

	private boolean[] states = new boolean[slotCount];

	public ForgeDirection placedDir = ForgeDirection.EAST;

	private ForgeDirection facing = ForgeDirection.EAST;

	private CompoundConsole console;

	private RegionMap<Integer> buttons = new RegionMap();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CONSOLE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (valid) {

		}
	}

	public void toggle(int slot, boolean on) {
		if (!worldObj.isRemote && console != null) {
			ItemStack is = inv[slot];
			if (ChromaItems.CARD.matchWith(is)) {
				ArrayList<TileControl> li = ItemCrystalCard.getControllers(is);
				for (TileControl loc : li) {
					loc.toggle(this, on);
				}
			}
		}
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.8F, 0.5F);
		states[slot] = on;
		this.syncAllData(false);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (!world.isRemote)
			this.validateMultiblock(world, x, y, z);
	}

	private void validateMultiblock(World world, int x, int y, int z) {
		BlockArray arr = new BlockArray();
		arr.recursiveAdd(world, x, y, z, this.getBlockType());
		boolean flag = this.isValidShape(arr);
		CompoundConsole con = flag ? new CompoundConsole() : null;

		Sorter<ForgeDirection> s = new Sorter(ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.SOUTH, ForgeDirection.NORTH);
		ForgeDirection face = null;
		if (flag) {
			for (Coordinate c : arr.keySet()) {
				TileEntityCrystalConsole te = (TileEntityCrystalConsole)c.getTileEntity(world);
				if (te.placedDir.offsetY == 0)
					s.increment(te.placedDir);
			}
			ArrayList<ForgeDirection> li = s.getSorted();
			for (ForgeDirection dir : li) {
				if (this.isDirectionValidForShape(dir, arr)) {
					face = dir;
					break;
				}
			}
		}

		for (Coordinate c : arr.keySet()) {
			TileEntityCrystalConsole te = (TileEntityCrystalConsole)c.getTileEntity(world);
			te.valid = flag;
			te.console = con;
			if (flag) {
				if (face != null) {
					te.facing = face;
				}
				te.calcSlots(arr);
				te.calcButtons(arr);
			}
			te.syncAllData(true);
			c.triggerBlockUpdate(world, false);
			te.scheduleBlockUpdate(2);
		}
	}

	private void calcButtons(BlockArray arr) {
		switch(facing) {
			case EAST:
				break;
			case NORTH:
				break;
			case SOUTH:
				break;
			case WEST:
				break;
			default:
				break;
		}
	}

	private boolean isDirectionValidForShape(ForgeDirection dir, BlockArray arr) {
		if (arr.getSizeX() == 1) {
			return dir.offsetX != 0;
		}
		else {
			return dir.offsetZ != 0;
		}
	}

	private void calcSlots(BlockArray arr) {
		slotCount = 3;
		boolean flag = false;
		switch(facing) {
			case EAST:
				flag = arr.hasBlock(xCoord, yCoord, zCoord+1);
				break;
			case NORTH:
				flag = arr.hasBlock(xCoord-1, yCoord, zCoord);
				break;
			case SOUTH:
				flag = arr.hasBlock(xCoord+1, yCoord, zCoord);
				break;
			case WEST:
				flag = arr.hasBlock(xCoord, yCoord, zCoord+1);
				break;
			default:
				break;
		}
		if (flag)
			slotCount = 4;
		states = Arrays.copyOf(states, slotCount);
	}

	public void setFacing(ForgeDirection dir) {
		if (dir == facing.getOpposite())
			facing = dir;
	}

	public boolean getState(int state) {
		return states[state];
	}

	public boolean isValid() {
		return valid;
	}

	public ForgeDirection getConsoleFace() {
		return facing;
	}

	private boolean isValidShape(BlockArray arr) {
		return arr.getVolume() == arr.getSize() && (arr.getSizeX() == 1 || arr.getSizeZ() == 1);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		NBT.setBoolean("valid", valid);

		NBT.setInteger("states", ReikaArrayHelper.booleanToBitflags(states));

		NBT.setInteger("slots", slotCount);

		NBT.setInteger("face", facing.ordinal());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		valid = NBT.getBoolean("valid");

		slotCount = NBT.getInteger("slots");

		states = ReikaArrayHelper.booleanFromBitflags(NBT.getInteger("states"), slotCount);

		facing = dirs[NBT.getInteger("face")];
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("place", placedDir.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		placedDir = dirs[NBT.getInteger("place")];
	}

	@Override
	public void breakBlock() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te instanceof TileEntityCrystalConsole) {
				((TileEntityCrystalConsole)te).validateMultiblock(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
			}
		}
	}

	@Override
	public boolean canExtractItem(int i, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return SLOTS;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		return is.getItem() == ChromaItems.CARD.getItemInstance();
	}

	@Override
	public byte[] getBundledSignal(int dir) {
		return console != null ? console.getBundled() : null;
	}

	@Override
	public boolean canConnectBundled(int side) {
		return side != this.getConsoleFace().ordinal();
	}

	public static enum ControlMode {
		REDSTONE(),
		TOGGLETILE(),
		BUNDLED();

		private void toggle(boolean on, TileEntityCrystalConsole te, WorldLocation loc, Object... data) {
			switch(this) {
				case BUNDLED:
					int ch = (Integer)data[0];
					te.console.getBundled()[ch] = (byte)(on ? (Integer)data[1] : 0);
					break;
				case REDSTONE:
					break;
				case TOGGLETILE:
					TileEntity tile = loc.getTileEntity();
					if (tile instanceof ToggleTile) {
						((ToggleTile)te).setEnabled(on);
					}
					break;
			}
		}

		public ControlMode next() {
			ControlMode[] list = values();
			return this.ordinal() == list.length-1 ? list[0] : list[this.ordinal()+1];
		}
	}

	public static class TileControl {


		private final ControlMode mode;
		private final Object[] data;
		private final WorldLocation location;

		private TileControl(ControlMode m, WorldLocation loc, Object... dat) {
			mode = m;
			location = loc;
			data = dat;
		}

		public void toggle(TileEntityCrystalConsole te, boolean on) {
			mode.toggle(on, te, location, data);
		}

		public static TileControl createFromNBT(NBTTagCompound NBT) {
			ControlMode mode = ControlMode.values()[NBT.getInteger("mode")];
			WorldLocation loc = WorldLocation.readFromNBT("loc", NBT);
			NBTTagCompound tag = NBT.getCompoundTag("data");
			ArrayList li = new ArrayList();
			switch(mode) {
				case BUNDLED:
					li.add(tag.getInteger("channel"));
					li.add(tag.getInteger("signal"));
					break;
				case REDSTONE:
					li.add(tag.getInteger("signal"));
					break;
				case TOGGLETILE:
					break;
			}
			return new TileControl(mode, loc, li.toArray());
		}

		public void writeToNBT(NBTTagCompound NBT) {
			NBT.setInteger("mode", mode.ordinal());
			NBTTagCompound tag = new NBTTagCompound();
			switch(mode) {
				case BUNDLED:
					tag.setInteger("channel", (Integer)data[0]);
					tag.setInteger("signal", (Integer)data[1]);
					break;
				case REDSTONE:
					tag.setInteger("signal", (Integer)data[0]);
					break;
				case TOGGLETILE:
					break;
			}
			NBT.setTag("data", tag);
			location.writeToNBT("loc", NBT);
		}

	}

	public int getSlotCount() {
		return slotCount;
	}

	public TileEntity getLeftBlock() {
		switch(this.getConsoleFace()) {
			case EAST:
				return this.getAdjacentTileEntity(ForgeDirection.SOUTH);
			case NORTH:
				return this.getAdjacentTileEntity(ForgeDirection.EAST);
			case WEST:
				return this.getAdjacentTileEntity(ForgeDirection.NORTH);
			case SOUTH:
				return this.getAdjacentTileEntity(ForgeDirection.WEST);
			default:
				return null;
		}
	}

	public TileEntity getRightBlock() {
		switch(this.getConsoleFace()) {
			case EAST:
				return this.getAdjacentTileEntity(ForgeDirection.NORTH);
			case NORTH:
				return this.getAdjacentTileEntity(ForgeDirection.WEST);
			case WEST:
				return this.getAdjacentTileEntity(ForgeDirection.SOUTH);
			case SOUTH:
				return this.getAdjacentTileEntity(ForgeDirection.EAST);
			default:
				return null;
		}
	}

}
