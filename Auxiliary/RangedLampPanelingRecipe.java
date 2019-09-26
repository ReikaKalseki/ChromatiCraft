/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;


public class RangedLampPanelingRecipe implements IRecipe {

	static {
		RecipeSorter.register("chromaticraft:rangedlamp", RangedLampPanelingRecipe.class, Category.SHAPELESS, "after:minecraft:shaped after:minecraft:shapeless");
	}

	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		return this.getCraftingResult(ic) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {
		ArrayList<ItemStack> c = ReikaInventoryHelper.convertCraftToItemList(ic);
		if (c.size() != 1)
			return null;
		ItemStack is = c.get(0);
		if (!ChromaBlocks.LAMPBLOCK.match(is))
			return null;
		is = is.copy();
		is.stackSize = 1;
		if (is.getItemDamage() >= 16)
			is.setItemDamage(is.getItemDamage()-16);
		else
			is.setItemDamage(is.getItemDamage()+16);
		return is;
	}

	@Override
	public int getRecipeSize() {
		return 1;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ChromaBlocks.LAMPBLOCK.getStackOf();
	}

}
