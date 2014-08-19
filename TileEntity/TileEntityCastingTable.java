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

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable.CastingRecipe;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalBase;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalReceiver;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntityCastingTable extends InventoriedCrystalBase implements CrystalReceiver {

	private ElementTagCompound energy = new ElementTagCompound();
	private CastingRecipe activeRecipe = null;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TABLE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (!world.isRemote && this.getTicksExisted() == 1) {
			for (int i = 0; i < 16; i++)
				this.requestEnergy(CrystalElement.elements[i], 50000);
			;//this.evaluateRecipeAndRequest();
		}

		if (activeRecipe != null) {

		}
		else {

		}

		//ReikaJavaLibrary.pConsole(energy, Side.SERVER);
	}

	@Override
	public void markDirty() {
		super.markDirty();

		CastingRecipe r = this.getValidRecipe();
		this.changeRequests(r);
	}

	private void changeRequests(CastingRecipe r) {
		if (r == null) {
			CrystalNetworker.instance.breakPaths(this);
		}
		else if (r != activeRecipe) {
			ElementTagCompound tag = r.getRequiredAura();
			tag.subtract(energy);
			for (CrystalElement e : tag.elementSet()) {
				this.requestEnergy(e, tag.getValue(e));
			}
		}
		activeRecipe = r;
	}

	private CastingRecipe getValidRecipe() {
		ItemStack[] items = new ItemStack[0];
		CastingRecipe r = RecipesCastingTable.instance.getRecipe(inv[0], items);
		return r != null && r.matchRunes(worldObj, xCoord, yCoord, zCoord) ? r : null;
	}

	private void evaluateRecipeAndRequest() {
		CastingRecipe r = this.getValidRecipe();
		if (r != null && r != activeRecipe) {
			ElementTagCompound tag = r.getRequiredAura();
			tag.subtract(energy);
			for (CrystalElement e : tag.elementSet()) {
				this.requestEnergy(e, tag.getValue(e));
			}
		}
	}

	private void requestEnergy(CrystalElement e, int amount) {
		CrystalNetworker.instance.makeRequest(this, e, amount, worldObj, xCoord, yCoord, zCoord, this.getReceiveRange());
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
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public void receiveElement(CrystalElement e, int amt) {
		energy.addValueToColor(e, amt);
	}

	public int getEnergy(CrystalElement e) {
		return energy.getValue(e);
	}

	@Override
	public void onPathBroken() {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		energy.readFromNBT("energy", NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		energy.writeToNBT("energy", NBT);
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return 10;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

}
