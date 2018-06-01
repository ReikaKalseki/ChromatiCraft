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

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class LegacyTileAcceleratorRecipe implements IRecipe {

	static {
		RecipeSorter.register("chromaticraft:tileaccel", LegacyTileAcceleratorRecipe.class, Category.SHAPELESS, "after:minecraft:shaped after:minecraft:shapeless");
	}

	@Override
	public boolean matches(InventoryCrafting ics, World world) {
		int c = 0;
		for (int i = 0; i < ics.getSizeInventory(); i++) {
			ItemStack in = ics.getStackInSlot(i);
			if (in != null) {
				if (ReikaItemHelper.matchStacks(in, ChromaTiles.ADJACENCY.getCraftedProduct()))
					c++;
				else
					return false;
			}
		}
		return c == 1;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ics) {
		int tier = 0;
		for (int i = 0; i < ics.getSizeInventory(); i++) {
			ItemStack in = ics.getStackInSlot(i);
			if (in != null) {
				if (ReikaItemHelper.matchStacks(in, ChromaTiles.ADJACENCY.getCraftedProduct())) {
					tier = in.stackTagCompound == null ? tier : in.stackTagCompound.getInteger("tier");
				}
			}
		}
		ItemStack is = ChromaItems.ADJACENCY.getStackOf(CrystalElement.LIGHTBLUE);
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("tier", tier);
		return is;
	}

	@Override
	public int getRecipeSize() {
		return 1;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ChromaItems.ADJACENCY.getStackOf(CrystalElement.LIGHTBLUE);
	}

}
