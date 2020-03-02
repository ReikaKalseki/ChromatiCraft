/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.MultiBlockCheck;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FocusAcceleratable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemCollision;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal.CrystalTier;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Collections.ThreadSafeSet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value={"buildcraft.api.transport.IPipeConnection"})
public abstract class TileEntityAuraInfuser extends InventoriedChromaticBase implements ItemOnRightClick, ItemCollision, OwnedTile, InertIInv,
IPipeConnection, OperationInterval, MultiBlockChromaTile, FocusAcceleratable, LocationCached, IFluidHandler {

	private InertItem item;

	private int craftingTick = 0;
	private boolean hasStructure = true;

	private static final ElementTagCompound required = new ElementTagCompound();

	private EntityPlayer craftingPlayer;

	private static final int DURATION = 608;

	private int focusCrystalTotal;
	private boolean allExquisite = false;

	private int fluidCooldown = 0;

	protected final HashSet<Coordinate> focusCrystalSpots = new HashSet();
	private final ArrayList<Coordinate> chromaLocations = new ArrayList();

	private static final ThreadSafeSet<WorldLocation> cache = new ThreadSafeSet();

	static {
		required.addTag(CrystalElement.PURPLE, 500);
		required.addTag(CrystalElement.BLACK, 2500);
	}

	@Override
	public final void updateEntity(World world, int x, int y, int z, int meta) {
		if (hasStructure/* && energy.containsAtLeast(required)*/) {
			if (craftingTick > 0) {
				this.tickCrafting(world, x, y, z);
			}
		}
		else {
			craftingTick = 0;
		}

		if (!world.isRemote) {
			if (fluidCooldown > 0)
				fluidCooldown--;
		}

		if (world.isRemote && hasStructure) {
			ChromaFX.doFocusCrystalParticles(world, x, y, z, this);
			if (craftingTick == 0)
				this.doAmbientParticles(world, x, y, z);
		}

		if (DragonAPICore.debugtest)
			this.getStructure().getArray(world, x, y, z).place();
	}

	protected void doAmbientParticles(World world, int x, int y, int z) {

	}

	protected abstract ChromaStructures getStructure();

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.validateStructure();
		cache.add(new WorldLocation(this));
	}

	public final void validateStructure() {
		focusCrystalTotal = 0;
		allExquisite = true;
		focusCrystalSpots.clear();
		chromaLocations.clear();
		ChromaStructures struct = this.getStructure();
		struct.getStructure().resetToDefaults();
		FilledBlockArray arr = struct.getArray(worldObj, xCoord, yCoord, zCoord);
		hasStructure = arr.matchInWorld();
		this.collectChromaLocations(arr);
		if (hasStructure) {
			this.collectFocusCrystalLocations(arr);
			this.countFocusCrystals(arr);
		}
		else {
			if (craftingTick > 0) {
				this.killCrafting();
			}
			craftingTick = 0;
		}
		this.markDirty();
		this.syncAllData(false);
	}

	@Override
	public final ChromaStructures getPrimaryStructure() {
		return this.getStructure();
	}

	@Override
	public final Coordinate getStructureOffset() {
		return null;
	}

	public final boolean canStructureBeInspected() {
		return true;
	}

	private void collectChromaLocations(FilledBlockArray arr) {
		for (Coordinate c : arr.keySet()) {
			if (arr.getBlockAt(c.xCoord, c.yCoord, c.zCoord) == ChromaBlocks.CHROMA.getBlockInstance()) {
				chromaLocations.add(c);
			}
		}
	}

	protected final Collection<Coordinate> getChromaLocations() {
		return Collections.unmodifiableCollection(chromaLocations);
	}

	protected abstract void collectFocusCrystalLocations(FilledBlockArray arr);

	private void countFocusCrystals(FilledBlockArray arr) {
		for (Coordinate c2 : focusCrystalSpots) {
			if (ChromaTiles.getTile(worldObj, c2.xCoord, c2.yCoord, c2.zCoord) == ChromaTiles.FOCUSCRYSTAL) {
				TileEntityFocusCrystal te = (TileEntityFocusCrystal)c2.getTileEntity(worldObj);
				CrystalTier ct = te.getTier();
				if (ct.ordinal() > 0) {
					int power = ReikaMathLibrary.intpow2(2, ct.getEffectiveOrdinal()-1);
					focusCrystalTotal += power;
					if (!ct.isMaxPower())
						allExquisite = false;
					te.addConnection(this, true);
				}
				else {
					focusCrystalTotal = 0;
					allExquisite = false;
					break;
				}
			}
		}
	}

	private void killCrafting() {
		ChromaSounds.ERROR.playSoundAtBlock(this);
	}

	public final boolean hasStructure() {
		return hasStructure;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");

		craftingTick = NBT.getInteger("craft");

		focusCrystalTotal = NBT.getInteger("focus");
		allExquisite = NBT.getBoolean("exq");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);

		NBT.setInteger("craft", craftingTick);

		NBT.setBoolean("exq", allExquisite);
		NBT.setInteger("focus", focusCrystalTotal);
	}

	private void tickCrafting(World world, int x, int y, int z) {
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);

		if (!this.canCraft()) {
			craftingTick = 0;
			this.killCrafting();
			return;
		}

		int sp = this.getCraftSpeed();
		if (sp == 4 && craftingTick%152 == 0)
			ChromaSounds.INFUSION_SHORT.playSoundAtBlock(this);
		else if (craftingTick%304 == 0)
			ChromaSounds.INFUSION.playSoundAtBlock(this);

		craftingTick -= 1;

		this.onCraftingTick(world, x, y, z);

		if (craftingTick == 0) {
			;//this.drainEnergy(required);
			this.craft();
			if (world.isRemote) {
				this.craftParticles(world, x, y, z);
			}
			craftingPlayer = null;
		}
	}

	protected void onCraftingTick(World world, int x, int y, int z) {

	}

	private int getCraftSpeed() {
		if (allExquisite && focusCrystalTotal >= 16)
			return 4;
		else if (focusCrystalTotal >= 8)
			return 2;
		return 1;
	}

	protected final EntityPlayer getCraftingPlayer() {
		return craftingPlayer;
	}

	private void craft() {
		ChromaSounds.INFUSE.playSoundAtBlock(this);
		this.onCraft();
		ChromaStructures struct = this.getStructure();
		struct.getStructure().resetToDefaults();
		FilledBlockArray arr = struct.getArray(worldObj, xCoord, yCoord, zCoord);
		for (int i = 0; i < arr.getSize(); i++) {
			Coordinate c = arr.getNthBlock(i);
			int dx = c.xCoord;
			int dy = c.yCoord;
			int dz = c.zCoord;
			if (arr.hasBlockAt(dx, dy, dz, ChromaBlocks.CHROMA.getBlockInstance(), 0))
				worldObj.setBlock(dx, dy, dz, Blocks.air);
		}
		this.validateStructure();
		this.scheduleCallback(new MultiBlockCheck(this), 20);
		this.scheduleCallback(new MultiBlockCheck(this), 100);
		this.scheduleCallback(new MultiBlockCheck(this), 200);
		this.markDirty();
	}

	protected abstract void onCraft();

	@SideOnly(Side.CLIENT)
	private void craftParticles(World world, int x, int y, int z) {
		for (int i = 0; i < 360; i += 15) {
			double ang = Math.toRadians(ReikaRandomHelper.getRandomPlusMinus(i, 5));
			double v = 0.075;
			double vx = v*Math.sin(ang);
			double vz = v*Math.cos(ang);
			EntityFlareFX fx = new EntityFlareFX(CrystalElement.WHITE, world, x+0.5, y+0.5, z+0.5, vx, 0, vz);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public final void markDirty() {
		super.markDirty();

		ItemStack is = inv[0];
		boolean flag = false;
		if (item == null)
			flag = is != null;
		else if (!ReikaItemHelper.matchStacks(inv[0], item.getEntityItem()))
			flag = true;
		if (flag) {
			item = is != null ? new InertItem(worldObj, is) : null;
		}

		if (this.canCraft()) {
			if (craftingTick == 0)
				craftingTick = DURATION/this.getCraftSpeed();
		}
		else {
			if (craftingTick > 0) {
				this.killCrafting();
			}
			craftingTick = 0;
		}
	}

	protected final boolean canCraft() {
		return this.getCraftingPlayer() != null && ProgressStage.ALLOY.isPlayerAtStage(this.getCraftingPlayer()) && this.isReady();
	}

	protected abstract boolean isReady();

	@SideOnly(Side.CLIENT)
	protected abstract void spawnParticles(World world, int x, int y, int z);

	@Override
	public final boolean canExtractItem(int side, ItemStack is, int slot) {
		return false;
	}

	@Override
	public final int getSizeInventory() {
		return 1;
	}

	@Override
	protected final void animateWithTick(World world, int x, int y, int z) {

	}

	public final EntityItem getItem() {
		return item;
	}
	/*
	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int getMaxStorage() {
		return 5000;
	}
	 */
	@Override
	public final ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		if (!this.isOwnedByPlayer(ep))
			return item;
		this.validateStructure();
		if (!hasStructure) {
			if (inv[0] != null && item == null) {
				ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, inv[0]);
				inv[0] = null;
			}
			return item;
		}
		if (item != null && !this.isItemValidForSlot(0, item))
			return item;
		if (item != null && ReikaItemHelper.matchStacks(item, inv[0]) && ItemStack.areItemStackTagsEqual(item, inv[0])) {
			if (inv[0].stackSize+item.stackSize <= this.getInventoryStackLimit()) {
				inv[0].stackSize += item.stackSize;
				item = null;
			}
			else if (inv[0].stackSize < this.getInventoryStackLimit()) {
				inv[0].stackSize++;
				item.stackSize--;
			}
		}
		else if (inv[0] != null) {
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, inv[0]);
			inv[0] = null;
		}

		if (item != null && inv[0] == null) {
			if (item.stackSize <= this.getInventoryStackLimit()) {
				inv[0] = item.copy();
				item = null;
			}
			else {
				inv[0] = ReikaItemHelper.getSizedItemStack(item, 1);
				item.stackSize--;
			}
		}

		craftingPlayer = ep;
		this.syncAllData(true);
		return item;
	}

	public final boolean onItemCollision(EntityItem ei) {
		if (!worldObj.isRemote) {
			ItemStack is = ei.getEntityItem();
			if (ei.delayBeforeCanPickup == 0 && this.isItemValidForSlot(0, is)) {
				if (inv[0] == null || ReikaItemHelper.matchStacks(is, inv[0])) {
					int has = inv[0] != null ? inv[0].stackSize : 0;
					int max = is.stackSize;
					int add = Math.min(max, is.getMaxStackSize()-has);
					if (add > 0) {
						craftingTick = 0;
						inv[0] = ReikaItemHelper.getSizedItemStack(is, has+add);
						is.stackSize -= add;
						craftingPlayer = ReikaItemHelper.getDropper(ei);
						this.syncAllData(true);
						if (is.stackSize <= 0)
							return true;
					}
				}
			}
		}
		return false;
	}

	public final int getCraftingTick() {
		return craftingTick;
	}

	@Override
	@ModDependent(ModList.BCTRANSPORT)
	public final ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		return ConnectOverride.DISCONNECT;
	}

	@Override
	public final float getOperationFraction() {
		return 1F-craftingTick/(float)(DURATION/this.getCraftSpeed());
	}

	@Override
	public final OperationState getState() {
		return this.canCraft() ? hasStructure ? OperationState.RUNNING : OperationState.PENDING : OperationState.INVALID;
	}

	@Override
	public final float getAccelerationFactor() {
		return this.getCraftSpeed() == 1 ? 0 : this.getCraftSpeed();
	}

	@Override
	public final float getMaximumAcceleratability() {
		return 4;
	}

	@Override
	public final float getProgressToNextStep() {
		if (focusCrystalTotal < 8) {
			return focusCrystalTotal/8F;
		}
		if (focusCrystalTotal >= 16)
			return 0;
		if (!allExquisite)
			return 0;
		return (focusCrystalTotal-8)/8F;
	}

	@Override
	public final void recountFocusCrystals() {
		this.validateStructure();
	}

	@Override
	public final Collection<Coordinate> getRelativeFocusCrystalLocations() {
		Collection<Coordinate> ret = new ArrayList();
		for (Coordinate c : focusCrystalSpots) {
			ret.add(c.offset(-xCoord, -yCoord, -zCoord));
		}
		return ret;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		this.writeOwnerData(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		this.readOwnerData(is);
	}

	public final void breakBlock() {
		cache.remove(new WorldLocation(this));
	}

	public static void clearCache() {
		cache.clear();
	}

	public static Set<WorldLocation> getCache() {
		return Collections.unmodifiableSet(cache);
	}

	@Override
	public final int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.canFill(from, resource.getFluid()) && resource.amount >= 1000 && (!doFill || this.tryAddBucketToStructure()) ? 1000 : 0;
	}

	private boolean tryAddBucketToStructure() {
		Collection<Coordinate> li = this.getChromaLocations();
		for (Coordinate c : li) {
			if (c.getBlock(worldObj).isAir(worldObj, c.xCoord, c.yCoord, c.zCoord) || (c.getBlock(worldObj) == ChromaBlocks.CHROMA.getBlockInstance() && c.getBlockMetadata(worldObj) != 0)) {
				c.setBlock(worldObj, ChromaBlocks.CHROMA.getBlockInstance());
				ReikaSoundHelper.playSoundFromServerAtBlock(worldObj, c.xCoord, c.yCoord, c.zCoord, "game.neutral.swim", 1, 0.5F+rand.nextFloat(), true);
				fluidCooldown = 2;
				return true;
			}
		}
		return false;
	}

	@Override
	public final FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public final FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public final boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluidCooldown == 0 && fluid == ChromatiCraft.chroma && !worldObj.isRemote;
	}

	@Override
	public final boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public final FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[0];
	}

}
