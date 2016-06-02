/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import cpw.mods.fml.common.registry.GameRegistry;


public class TileEntityFluxGooCreator extends InventoriedRelayPowered implements OperationInterval {

	private static final ElementTagCompound required = new ElementTagCompound();

	private static final WeightedRandom<Fluid> fluidRand = new WeightedRandom();

	static {
		required.addTag(CrystalElement.LIGHTGRAY, 100);
		required.addTag(CrystalElement.BLACK, 100);

		fluidRand.addEntry(FluidRegistry.getFluid("fluxGoo"), 50);
		fluidRand.addEntry(FluidRegistry.getFluid("fluxGas"), 200);
		fluidRand.addEntry(FluidRegistry.getFluid("fluidDeath"), 2);
	}

	public int processTimer;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FLUXMAKER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (inv[0] != null && this.isItemValid(inv[0])) {

			Fluid f = fluidRand.getRandomEntry();
			if (f != null) {
				ReikaInventoryHelper.decrStack(0, inv);
				this.spawnBlock(world, x, y, z, f);
			}
		}
	}

	private void spawnBlock(World world, int x, int y, int z, Fluid f) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

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
		return slot == 0 && this.isItemValid(is);
	}

	private boolean isItemValid(ItemStack is) {
		if (is.getItem() == GameRegistry.findItem(ModList.THAUMCRAFT.modLabel, "ItemCrystalEssence") && is.stackTagCompound != null) {
			AspectList al = new AspectList();
			al.readFromNBT(is.stackTagCompound);
			return al.size() == 1 && al.getAmount(Aspect.ENTROPY) > 0;
		}
		return false;
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

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return this.isAcceptingColor(e);
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
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
	}

	public int getCookProgressScaled(int a) {
		return this.canProcess() ? processTimer * a / this.getProcessTime() : 0;
	}

	@Override
	public float getOperationFraction() {
		return !this.canProcess() ? 0 : processTimer/(float)(Math.max(1, this.getProcessTime()));
	}

	@Override
	public OperationState getState() {
		return inv[0] != null && this.isItemValid(inv[0]) ? (energy.containsAtLeast(required) ? OperationState.RUNNING : OperationState.PENDING) : OperationState.INVALID;
	}

	public int getProcessTime() {
		if (inv[0] == null)
			return 1;
		return 80;
	}

	private boolean canProcess() {
		if (inv[0] == null)
			return false;
		if (!energy.containsAtLeast(required))
			return false;
		if (!this.isItemValid(inv[0]))
			return false;
		return true;
	}

}
