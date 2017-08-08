/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.API.CastingAPI;
import Reika.ChromatiCraft.API.CrystalElementProxy;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


/** Use this to fetch registered casting table recipes. This allows you to query various properties, but take note that recipes are read-only. */
public class CastingRecipeViewer {

	private static HashMap<Object, List<APICastingRecipe>> recipeList;

	public static List<APICastingRecipe> getRecipes() {
		ArrayList<APICastingRecipe> li = new ArrayList();
		for (List<APICastingRecipe> in : recipeList.values()) {
			li.addAll(in);
		}
		return li;
	}

	static {
		try {
			Class c = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable");
			Field inst = c.getField("instance");
			Object o = inst.get(null);
			Field recipes = c.getDeclaredField("recipes");
			recipes.setAccessible(true);
			recipeList = (HashMap<Object, List<APICastingRecipe>>)recipes.get(o);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** Implemented by the casting recipes themselves. This interface is implemented by all the recipes. */
	public static interface APICastingRecipe {

		/** The recipe tier, from 0 to 3. Corresponds to the four tiers described in {@link CastingAPI}. Use this to check before casting to
		 * higher-level recipe interfaces */
		public int getTier();

		public ItemStack getOutput();

		/** Whether a player is permitted to perform a given recipe, such as progression and other data. Null or fake players are not permitted. */
		public boolean canRunRecipe(EntityPlayer ep);

		/** The typical number of times a player runs this recipe. Some may go as large as Int.MAX (for recipes that have no upper bound), and others
		 * may be as low as one. */
		public int getTypicalCraftedAmount();

		/** How many ticks the recipe takes to complete once all its requirements are satisfied. Generally 5-1800 ticks. */
		public int getDuration();

		/** How much "Casting Experience" is yielded by the recipe. This is NOT normal Minecraft XP. */
		public int getExperience();

		/** A 3x3 ItemStack grid used for recipe display purposes, like in NEI and the lexicon. */
		@SideOnly(Side.CLIENT)
		public ItemStack[] getArrayForDisplay();

		/** Whether a given item is used in the recipe. */
		public boolean usesItem(ItemStack is);

		/** Some recipes modify NBT on the input item. Use this to determine the output. Null is permitted. */
		public NBTTagCompound getOutputTag(NBTTagCompound input);

	}

	/** Rune-level crafting. */
	public static interface RuneRecipe extends APICastingRecipe {

		/** The rune locations. The list is of relative XYZ coords, with a given color at each position. */
		public Map<List<Integer>, CrystalElementProxy> getRunePositions();

	}

	/** Multiblock (Item Casting Stand, 5x5 grid) recipes */
	public static interface MultiRecipe extends RuneRecipe {

		/** The item input locations. The list is of relative XZ coords, with a given itemstack or set thereof (OreDict).  */
		public Map<List<Integer>, Set<KeyedItemStack>> getInputItems();

		/** The central item. */
		public ItemStack getMainInput();

	}

	/** Pylon and repeater recipes. */
	public static interface LumenRecipe extends MultiRecipe {

		/** The lumen cost for a given color. */
		public int getEnergyCost(CrystalElementProxy e);

	}
}
