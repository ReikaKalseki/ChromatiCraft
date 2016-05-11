/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ContainerCastingAuto extends CoreContainer {

	private final TileEntityCastingAuto tile;

	private ItemStack recipeFilter;

	private final InventoryCrafting inventory = new InventoryCrafting(this, 1, 1);

	public ContainerCastingAuto(TileEntityCastingAuto te, EntityPlayer player)
	{
		super(player, te);
		int var6;
		int var7;

		tile = te;

		this.addSlotToContainer(new Slot(inventory, 0, 8, 34));

		/*
		for (var6 = 0; var6 < 3; ++var6)
			for (var7 = 0; var7 < 9; ++var7)
				this.addSlotToContainer(new Slot(player.inventory, var7 + var6 * 9 + 9, 8 + var7 * 18, 112 + var6 * 18));
		for (var6 = 0; var6 < 9; ++var6)
			this.addSlotToContainer(new Slot(player.inventory, var6, 8 + var6 * 18, 170));
		 */

		for (var6 = 0; var6 < 9; ++var6)
			for (var7 = 0; var7 < 4; ++var7)
				this.addSlotToContainer(new Slot(player.inventory, var6 + var7 * 9, 146 + var7 * 18, 57 + var6 * 18));

		//inventory.setInventorySlotContents(0, te.getCurrentRecipeOutput());

		this.onCraftMatrixChanged(inventory);
	}

	@Override
	public ItemStack slotClick(int slot, int par2, int par3, EntityPlayer ep) {
		boolean inGUI = slot == 0;
		if (inGUI) {
			ItemStack held = ep.inventory.getItemStack();
			ItemStack is = held != null ? ReikaItemHelper.getSizedItemStack(held, 1) : null;
			inventory.setInventorySlotContents(slot, is);
			return held;
		}
		else
			return super.slotClick(slot, par2, par3, ep);
	}

	@Override
	public void onCraftMatrixChanged(IInventory ii)
	{
		super.onCraftMatrixChanged(ii);

		if (inventory == ii) {
			ItemStack is = inventory.getStackInSlot(0);
			recipeFilter = is;
		}
	}

	public boolean isRecipeValid(CastingRecipe cr) {
		if (recipeFilter == null)
			return true;
		ItemStack out = cr.getOutput();
		return ReikaItemHelper.matchStacks(recipeFilter, out) && ItemStack.areItemStackTagsEqual(recipeFilter, out);
	}

	@Override
	public void onContainerClosed(EntityPlayer ep) {
		super.onContainerClosed(ep);
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}

	@Override
	public boolean allowShiftClicking(EntityPlayer player, int slot, ItemStack stack) {
		return false;
	}

}
