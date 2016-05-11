/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Processing;

import java.util.HashMap;
import java.util.Locale;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaEnchants;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

@Strippable(value = {"buildcraft.api.transport.IPipeConnection"})
public class TileEntityEnchantDecomposer extends InventoriedRelayPowered implements IFluidHandler, IPipeConnection, OperationInterval {

	private static final ElementTagCompound required = new ElementTagCompound();

	public static final int CAPACITY = 6000;

	public int processTimer;

	private final HybridTank tank = new HybridTank("enchantdecomp", CAPACITY);

	private EntityItem entity;

	@Override
	public void markDirty() {
		super.markDirty();

		entity = inv[0] != null ? new InertItem(worldObj, inv[0]) : null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (this.canProcess()) {
			processTimer++;
			if (processTimer >= this.getProcessTime()) {
				this.process();
				processTimer = 0;
			}
		}
		else {
			processTimer = 0;
		}
	}

	public int getProcessTime() {
		if (inv[0] == null)
			return 1;
		HashMap<Enchantment, Integer> map = ReikaEnchantmentHelper.getEnchantments(inv[0]);
		int t = 0;
		for (Integer lvl : map.values()) {
			t += 10*lvl*lvl;
		}
		return t;
	}

	public static int getChromaValue(ItemStack is) {
		int val = 0;
		HashMap<Enchantment, Integer> map = ReikaEnchantmentHelper.getEnchantments(is);
		for (Enchantment e : map.keySet()) {
			val += ReikaMathLibrary.roundDownToX(100, (int)(100*Math.pow(getEnchantPowerValue(e), map.get(e))));
		}
		return val;
	}

	private static double getEnchantPowerValue(Enchantment e) {
		if (e == ChromaEnchants.WEAPONAOE.getEnchantment())
			return 1.75;
		if (e == Enchantment.silkTouch)
			return 1.5;
		if (e == Enchantment.infinity)
			return 1.5;
		if (e == Enchantment.fortune)
			return 1.25;
		if (e == Enchantment.power)
			return 1.25;
		if (e == Enchantment.protection)
			return 1.25;
		if (e == Enchantment.sharpness)
			return 1.125;
		if (e == Enchantment.unbreaking)
			return 1.125;
		String n = e.getName().toLowerCase(Locale.ENGLISH);
		if (n.contains("soul") && n.contains("bound"))
			return 2;
		return 1;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return slot == 0 && isItemValid(is);
	}

	public static boolean isItemValid(ItemStack is) {
		return ReikaEnchantmentHelper.hasEnchantments(is);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ENCHANTDECOMP;
	}

	private boolean canProcess() {
		if (inv[0] == null)
			return false;
		if (!energy.containsAtLeast(required))
			return false;
		if (!isItemValid(inv[0]))
			return false;
		int val = this.getChromaValue(inv[0]);
		if (!tank.canTakeIn(val))
			return false;
		return true;
	}

	private void process() {
		tank.addLiquid(this.getChromaValue(inv[0]), FluidRegistry.getFluid("chroma"));
		ReikaInventoryHelper.decrStack(0, inv);
		this.drainEnergy(required);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		processTimer = NBT.getInteger("time");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("time", processTimer);

		tank.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		tank.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);
	}

	static {
		required.addTag(CrystalElement.PURPLE, 50);
		required.addTag(CrystalElement.BLACK, 20);
	}

	@Override
	protected ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	public static ElementTagCompound getTags() {
		return required.copy();
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 1000;
	}

	public int getCookProgressScaled(int a) {
		return this.canProcess() ? processTimer * a / this.getProcessTime() : 0;
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.canDrain(from, resource.getFluid()) ? tank.drain(resource.amount, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.canDrain(from, null) ? tank.drain(maxDrain, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return ReikaFluidHelper.isFluidDrainableFromTank(fluid, tank);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	@ModDependent(ModList.BCTRANSPORT)
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		return ConnectOverride.DEFAULT;
	}

	@Override
	public int getIconState(int side) {
		return side > 1 && this.canProcess() ? 1 : 0;
	}

	public int getFluidScaled(int i) {
		return i * tank.getLevel() / tank.getCapacity();
	}

	public EntityItem getEntityItem() {
		return entity;
	}

	public int getChromaLevel() {
		return tank.getLevel();
	}

	@Override
	public float getOperationFraction() {
		return !this.canProcess() ? 0 : processTimer/(float)(Math.max(1, this.getProcessTime()));
	}

	@Override
	public OperationState getState() {
		return inv[0] != null && isItemValid(inv[0]) ? (energy.containsAtLeast(required) ? OperationState.RUNNING : OperationState.PENDING) : OperationState.INVALID;
	}

}
