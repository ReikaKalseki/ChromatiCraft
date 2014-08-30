/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.XPProducer;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityCrystalFurnace extends ChargedCrystalPowered implements XPProducer {

	private static final ElementTagCompound smelt = new ElementTagCompound();

	public static final int MULTIPLY = 2;

	static {
		smelt.addTag(CrystalElement.ORANGE, 1000);
		smelt.addTag(CrystalElement.WHITE, 200);
		smelt.addTag(CrystalElement.PURPLE, 200);
	}

	private float xp;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (inv[1] != null) {
			if (this.canSmelt()) {
				this.smelt();
			}
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		if (slot == 0)
			return this.getStoredEnergy() == 0;
		return slot == 2;
	}

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		if (slot == 0)
			return ChromaItems.STORAGE.matchWith(is);
		return false;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FURNACE;
	}

	private boolean canSmelt() {
		if (!this.hasEnoughEnergy())
			return false;
		ItemStack out = FurnaceRecipes.smelting().getSmeltingResult(inv[1]);
		if (out == null)
			return false;
		out = out.copy();
		out.stackSize *= this.getMultiplyRate(out);
		if (inv[2] == null)
			return true;
		if (!ReikaItemHelper.matchStacks(out, inv[2]) || !ItemStack.areItemStackTagsEqual(out, inv[2]))
			return false;
		if (out.stackSize+inv[2].stackSize > Math.min(this.getInventoryStackLimit(), out.getMaxStackSize()))
			return false;
		return true;
	}

	private void smelt() {
		ItemStack is = FurnaceRecipes.smelting().getSmeltingResult(inv[1]).copy();
		is.stackSize *= this.getMultiplyRate(is);
		ReikaInventoryHelper.addOrSetStack(is, inv, 2);
		xp += FurnaceRecipes.smelting().func_151398_b(inv[1])*6;
		ReikaInventoryHelper.decrStack(1, inv);
		this.useEnergy(smelt);
	}

	private int getMultiplyRate(ItemStack is) {
		return MULTIPLY; //need way to solve exploits
	}

	private boolean hasEnoughEnergy() {
		for (CrystalElement e : smelt.elementSet()) {
			if (this.getStoredEnergy(e) < smelt.getValue(e))
				return false;
		}
		return true;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void clearXP() {
		xp = 0;
	}

	@Override
	public float getXP() {
		return xp;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		xp = NBT.getFloat("xp");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setFloat("xp", xp);
	}

}
