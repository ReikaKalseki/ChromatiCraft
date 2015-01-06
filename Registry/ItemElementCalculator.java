/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class ItemElementCalculator {

	public static final ItemElementCalculator instance = new ItemElementCalculator();
	private HashMap<List<Integer>, ElementTagCompound> cache = new HashMap();

	//In case of multiple recipes, need to take cheapest tag of each color possible, as risk exploit otherwise
	private ElementTagCompound getFromVanillaCrafting(ItemStack is) {
		ArrayList<IRecipe> li = ReikaRecipeHelper.getAllRecipesByOutput(CraftingManager.getInstance().getRecipeList(), is);
		ElementTagCompound tag = new ElementTagCompound();
		for (int i = 0; i < li.size(); i++) {
			IRecipe ir = li.get(i);
			if (ir instanceof ShapedRecipes) {
				ShapedRecipes sr = (ShapedRecipes)ir;
				for (int k = 0; k < sr.recipeItems.length; k++) {
					ItemStack in = sr.recipeItems[k];
					ElementTagCompound value = this.getTagForItem(in);
					tag.minimizeWith(value);
				}
			}
			else if (ir instanceof ShapelessRecipes) {
				ShapelessRecipes sr = (ShapelessRecipes)ir;
				for (int k = 0; k < sr.recipeItems.size(); k++) {
					ItemStack in = (ItemStack)sr.recipeItems.get(k);
					ElementTagCompound value = this.getTagForItem(in);
					tag.minimizeWith(value);
				}
			}
			else if (ir instanceof ShapedOreRecipe) {
				ShapedOreRecipe sr = (ShapedOreRecipe)ir;
				for (int k = 0; k < sr.getInput().length; k++) {
					Object in = sr.getInput()[k];
					ElementTagCompound value = this.getTagForItemOrList(in);
					tag.minimizeWith(value);
				}
			}
			else if (ir instanceof ShapelessOreRecipe) {
				ShapelessOreRecipe sr = (ShapelessOreRecipe)ir;
				for (int k = 0; k < sr.getInput().size(); k++) {
					Object in = sr.getInput().get(k);
					ElementTagCompound value = this.getTagForItemOrList(in);
					tag.minimizeWith(value);
				}
			}
		}
		return null;
	}

	private ElementTagCompound getTagForItemOrList(Object in) {
		if (in instanceof ItemStack)
			return this.getTagForItem((ItemStack)in);
		else if (in instanceof ArrayList) {
			ArrayList li = (ArrayList)in;
			ElementTagCompound tag = new ElementTagCompound();
			for (int i = 0; i < li.size(); i++) {
				ItemStack is = (ItemStack)li.get(i);
				ElementTagCompound value = this.getTagForItem(is);
				tag.minimizeWith(value);
			}
			return tag;
		}
		else
			return null;
	}

	public ElementTagCompound getTagForItem(ItemStack is) {
		if (is == null)
			return null;
		List key = Arrays.asList(is.getItem(), is.getItemDamage());
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		else {
			ElementTagCompound tag = this.calculateTag(is);
			cache.put(key, tag);
			return tag;
		}
	}

	//check : Crafting, TE3 machines, BC laser table, ChromatiCraft manufacture, TiC
	private ElementTagCompound calculateTag(ItemStack is) {
		/*
		ElementTagCompound mod = ElementController.getRequestedTagFor(is); //this allows mods to create exploits...
		if (mod != null) {
			return mod;
		}
		 */

		ElementTagCompound tag = new ElementTagCompound();
		try {
			tag.minimizeWith(this.getFromVanillaCrafting(is));
		}
		catch (Exception e) {
			//ChromatiCraft.logger.logError("Error fetching element data for "+is);
			e.printStackTrace();
		}
		return tag;
	}

}
