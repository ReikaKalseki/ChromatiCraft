/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.RangeTracker.ConfigurableRangeTracker;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.ItemWithItemFilter.Filter;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.ThreadSafeTileCache;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ReikaXPFluidHelper;

public class TileEntityItemCollector extends InventoriedRelayPowered implements NBTTile, LocationCached, IFluidHandler, RangeUpgradeable {

	private int experience = 0;
	public boolean canIntake = false;

	public static final int MAXRANGE = 24;

	private final ConfigurableRangeTracker range = new ConfigurableRangeTracker(MAXRANGE, 24, 1);

	public int filterLimit;

	private Filter[] filter = new Filter[5*9];
	private final StepTimer scanTimer = new StepTimer(200);

	private static final ElementTagCompound required = new ElementTagCompound();

	private static final ThreadSafeTileCache cache = new ThreadSafeTileCache().setTileClass(TileEntityItemCollector.class);

	public static boolean haltCollection = false;

	static {
		required.addTag(CrystalElement.LIME, 25);
		required.addTag(CrystalElement.BLACK, 5);
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	public int getExperience() {
		return experience;
	}

	public int getMaxFilterCount() {
		if (worldObj != null && worldObj.isRemote)
			return filterLimit;
		int adj = TileEntityAdjacencyUpgrade.getAdjacentUpgrade(this, CrystalElement.PURPLE);
		if (adj <= 0)
			return 9;
		return Math.min(filter.length, 9+(9*adj/2));
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 27;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ITEMCOLLECTOR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		range.update(this);

		if (canIntake && !world.isRemote) {
			filterLimit = this.getMaxFilterCount();
			scanTimer.update();
			if (scanTimer.checkCap()) {
				this.doScan(world, x, y, z);
			}
		}
	}

	private void doScan(World world, int x, int y, int z) {
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-this.getRange(), y-this.getYRange(), z-this.getRange(), x+this.getRange()+1, y+this.getYRange()+1, z+this.getRange()+1);
		List<Entity> li = world.selectEntitiesWithinAABB(Entity.class, box, ReikaEntityHelper.itemOrXPSelector);
		for (Entity e : li) {
			if (this.checkAbsorb(e)) {
				e.setDead();
			}
		}

		//WorldLocation loc = new WorldLocation(this);
		//if (!cache.contains(loc))
		//	cache.add(loc);
	}

	private int getYRange() {
		return Math.max(Math.min(4, this.getRange()), this.getRange()/4);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
		canIntake = true;
		range.initialize(this);
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}

	public static boolean absorbItem(World world, Entity e) {
		if (haltCollection)
			return false;
		return cache.lookForMatch(world, true, (WorldLocation loc, TileEntity te) -> {
			return ((TileEntityItemCollector)te).checkAbsorb(e);
		}, (WorldLocation loc, TileEntity te) -> {
			ChromaAux.logTileCacheError(world, loc, te, ChromaTiles.ITEMCOLLECTOR);
		});
	}

	public boolean checkAbsorb(Entity e) {
		if (e.isDead)
			return false;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		if (!this.isInWorld())
			return false;
		if (!canIntake)
			return false;
		if (e.worldObj.provider.dimensionId != worldObj.provider.dimensionId)
			return false;
		if (this.hasRedstoneSignal())
			return false;
		if (!energy.containsAtLeast(required))
			return false;
		if (e instanceof EntityItem || e instanceof EntityXPOrb) {
			if (Math.abs(e.posX-x-0.5) <= this.getRange() && Math.abs(e.posY-y-0.5) <= this.getYRange() && Math.abs(e.posZ-z-0.5) <= this.getRange()) {
				if (e instanceof EntityItem) {
					EntityItem ei = (EntityItem)e;
					if (this.canAbsorbItem(ei)) {
						return this.absorbItem(worldObj, x, y, z, ei);
					}
				}
				else {
					this.absorbXP(worldObj, x, y, z, (EntityXPOrb)e);
					this.drainEnergy(required);
					return true;
				}
			}
		}
		return false;
	}

	private boolean canAbsorbItem(EntityItem ei) {
		if (ei.getEntityData().getBoolean(TileEntityItemInserter.DROP_TAG))
			return false;
		ItemStack is = ei.getEntityItem();
		for (int i = 0; i < this.getMaxFilterCount(); i++) {
			if (filter[i] != null && filter[i].match(is)) {
				return true;
			}
		}
		return false;
	}

	private boolean match(ItemStack is, ItemStack filter) {
		if (ChromaItems.SEED.matchWith(is))
			return ChromaItems.SEED.matchWith(filter) && is.getItemDamage()%16 == filter.getItemDamage()%16;
		return ReikaItemHelper.matchStacks(is, filter) && ItemStack.areItemStackTagsEqual(is, filter);
	}

	private boolean absorbItem(World world, int x, int y, int z, EntityItem ent) {
		ItemStack is = ent.getEntityItem();

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te instanceof IInventory) {
				if (ReikaInventoryHelper.addToIInv(is, (IInventory)te))
					return true;
			}
		}

		int targetslot = this.checkForStack(is);
		if (targetslot != -1) {
			if (inv[targetslot] == null)
				inv[targetslot] = is.copy();
			else
				inv[targetslot].stackSize += is.stackSize;
		}
		else {
			return false;
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.pop", 0.1F+0.5F*rand.nextFloat(), rand.nextFloat());
		ent.playSound("random.pop", 0.5F, 2F);
		this.drainEnergy(required);
		return true;
	}

	private void absorbXP(World world, int x, int y, int z, EntityXPOrb xp) {
		int val = xp.getXpValue();
		experience += val;
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.orb", 0.1F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
		xp.playSound("random.pop", 0.5F, 2F);
	}

	private int checkForStack(ItemStack is) {
		int target = -1;
		Item id = is.getItem();
		int meta = is.getItemDamage();
		int size = is.stackSize;
		int firstempty = -1;

		for (int k = 0; k < inv.length; k++) { //Find first empty slot
			if (inv[k] == null) {
				firstempty = k;
				k = inv.length;
			}
		}
		for (int j = 0; j < inv.length; j++) {
			if (inv[j] != null) {
				if (ReikaItemHelper.matchStacks(is, inv[j])) {
					if (ItemStack.areItemStackTagsEqual(is, inv[j])) {
						if (inv[j].stackSize+size <= this.getInventoryStackLimit()) {
							target = j;
							j = inv.length;
						}
						else {
							int diff = is.getMaxStackSize() - inv[j].stackSize;
							inv[j].stackSize += diff;
							is.stackSize -= diff;
						}
					}
				}
			}
		}

		if (target == -1)
			target = firstempty;
		return target;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);
		experience = NBT.getInteger("xp");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
		NBT.setInteger("xp", experience);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		this.saveFilters(NBT);
		range.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		this.readFilters(NBT);
		range.readFromNBT(NBT);
	}

	private void saveFilters(NBTTagCompound NBT) {
		NBTTagCompound fil = new NBTTagCompound();
		for (int i = 0; i < filter.length; i++) {
			Filter is = filter[i];
			if (is != null) {
				NBTTagCompound tag = new NBTTagCompound();
				is.writeToNBT(tag);
				fil.setTag("filter_"+i, tag);
			}
		}
		NBT.setTag("filter", fil);
	}

	private void readFilters(NBTTagCompound NBT) {
		filter = new Filter[filter.length];
		NBTTagCompound fil = NBT.getCompoundTag("filter");
		for (int i = 0; i < filter.length; i++) {
			String name = "filter_"+i;
			if (fil.hasKey(name)) {
				NBTTagCompound tag = fil.getCompoundTag(name);
				if (tag.hasKey("filterType")) {
					filter[i] = new Filter();
					filter[i].readFromNBT(tag);
				}
				else {
					ItemStack is = ItemStack.loadItemStackFromNBT(tag);
					filter[i] = is != null ? new Filter(is) : null;
				}
			}
		}
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		this.saveFilters(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		if (is.stackTagCompound == null)
			return;
		this.readFilters(is.stackTagCompound);
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 48000;
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return true;
	}

	public void setMapping(int slot, ItemStack is) {
		if (slot >= this.getMaxFilterCount())
			return;
		filter[slot] = is == null ? null : new Filter(is);
		this.syncAllData(true);
	}

	public void toggleNBT(int slot) {
		if (slot >= this.getMaxFilterCount() || filter[slot] == null)
			return;
		filter[slot].toggleNBT();
		this.syncAllData(true);
	}

	public ItemStack getMapping(int slot) {
		return filter[slot] != null ? filter[slot].getDisplay() : null;
	}

	public boolean hasNBT(int slot) {
		return filter[slot] != null && filter[slot].hasNBT();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return resource.getFluidID() == ReikaXPFluidHelper.getFluid().getFluidID() ? this.drain(from, resource.amount, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		FluidStack fs = experience > 0 ? ReikaXPFluidHelper.getFluid(experience) : null;
		if (fs != null) {
			if (fs.amount > maxDrain)
				fs.amount = maxDrain;
			if (doDrain)
				experience -= ReikaXPFluidHelper.getXPForAmount(fs.amount);
		}
		return fs;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return ReikaXPFluidHelper.fluidsExist() && experience > 0 && fluid.equals(ReikaXPFluidHelper.getFluidType());
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{};
	}

	public void decreaseRange(int amt) {
		if (range.decrement(this, amt))
			this.syncAllData(false);
	}

	public void increaseRange(int amt) {
		if (range.increment(this, amt))
			this.syncAllData(false);
	}

	public int getRange() {
		return range.getRange();
	}

	public static void clearCache() {
		cache.clear();
	}

	@Override
	public void upgradeRange(double r) {

	}

}
