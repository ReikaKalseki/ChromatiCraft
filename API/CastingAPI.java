/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/** Use this class to add custom casting recipes. Note that each tier of recipes encompasses the last in terms of required actions/content,
 * and the various methods' arguments reflect that.
 * <br><br>
 * Call this during postinit.
 * <br><br>
 * Note that the recipe list is semi-immutable, so once you add recipes, they cannot be removed or modified.
 * <br><br>
 * Recipe Tiers:<br>
 * <b>Basic:</b> Basically a crafting table. Uses an IRecipe object, and is available very early in ChromatiCraft. Only produces basic items, and
 * nothing with strong magical energy in it.<br><br>
 * <b>Temple:</b> The table still uses the 3x3 recipe grid, but now runes are a required element of crafting, and the level-one structure has been
 * built around the table. Available with minor progression into ChromatiCraft, and is the first tier capable of producing items with significant
 * amounts of magic energy.<br><br>
 * <b>MultiBlock:</b> The 3x3 of the table has been replaced with a 5x5 of item stands around the table itself, allowing for more complex recipes.
 * Runes still play a role, though not all such recipes actually use them. This tier requires a moderate amount of ChromatiCraft progression.
 * Items produced by these sorts of recipes include tools and less powerful constructs.<br><br>
 * <b>Pylon:</b> This is the highest recipe level, available only in the late game. This is the tier where repeaters and the like are required,
 * and is the most expensive, technically complex, and time-consuming to set up and perform. Recipes of this type should give appropriate reward.
 * <br><br>
 * Argument Types:<br>
 * <b>{@code IRecipe:}</b> Used for the lower two tiers. This is a recipe object for a 3x3 grid; it can be shaped or shapeless.
 * The casting table supports the ore dictionary and metadata wildcards, and your recipe can reflect that, though it is optional.
 *<br><br>
 * <b>{@code Map<List<Integer>, CrystalElementProxy>:}</b> Used in all but the lowest tier. This declares runes at a given location relative to the
 * table, of the specified color. The list of integers should be created with {@code Arrays.asList(x,y,z)}. Take care not to place them in invalid
 * locations, such as overwriting each other or critical parts of the casting structure.
 * <br><br>
 * <b>{@code Map<List<Integer>, ItemStack>:}</b> Used by the higher two tiers. Declares other input items on stands around the table. For higher
 * tier recipes, this replaces the non-central items in a shaped recipe. The list of integers should be created with {@code Arrays.asList(x,z)},
 * and only even-numbered values from -4 to +4 are acceptable. Additionally, (0,0) is the table itself and will prevent the recipe from working.
 * <br><br>
 * <b>{@code Map<CrystalElementProxy, Integer>:}</b> Used by the highest tier of recipe. Declares the amount of crystal energy that is required
 * to perform this casting recipe. Most recipes have one to four input colors, and the amount of required energy is typically from 500 to 100000.
 * Note that excessively large values may render the recipe impossible as they exceed the table's energy storage capacity.
 * <br><br>
 *  Any recipes whose output is ChromatiCraft items will be rejected with an error log, as such recipes will damage the progression of the mod. */
public class CastingAPI {

	private static Class cast;
	private static Class temple;
	private static Class multi;
	private static Class pylon;

	private static Constructor instCast;
	private static Constructor instTemple;
	private static Constructor instMulti;
	private static Constructor instPylon;

	private static Method rune;
	private static Method aux;
	private static Method aura;

	private static Class recipes;
	private static Object instance;
	private static Method add;

	private static boolean loaded;

	/** Use this to add a level one "crafting type" casting recipe.
	 * Args: Recipe object */
	public static void addCastingRecipe(IRecipe ir) {
		if (!loaded) {
			System.out.println("Class did not initialize correctly, casting recipes cannot be added!");
			return;
		}
		ItemStack out = ir.getRecipeOutput();
		if (!isValid(out)) {
			System.out.println("You cannot add alternate recipes for native ChromatiCraft items!");
			return;
		}
		try {
			Object r = instCast.newInstance(out, ir);
			add.invoke(instance, r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Use this to add a level two "temple/rune type" casting recipe.
	 * Args: Recipe object, rune map (may NOT be null) */
	public static void addTempleCastingRecipe(IRecipe ir, Map<List<Integer>, CrystalElementProxy> runes) {
		if (!loaded) {
			System.out.println("Class did not initialize correctly, casting recipes cannot be added!");
			return;
		}
		ItemStack out = ir.getRecipeOutput();
		if (!isValid(out)) {
			System.out.println("You cannot add alternate recipes for native ChromatiCraft items!");
			return;
		}
		try {
			Object r = instTemple.newInstance(out, ir);
			for (List<Integer> key : runes.keySet()) {
				rune.invoke(r, runes.get(key), key.get(0).intValue(), key.get(1).intValue(), key.get(2).intValue());
			}
			add.invoke(instance, r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Use this to add a level three "multiblock" casting recipe.
	 * Args: Output item, central item, rune map (may be null), itemstack map (may NOT be null) */
	public static void addMultiBlockCastingRecipe(ItemStack out, ItemStack ctr, Map<List<Integer>, CrystalElementProxy> runes, Map<List<Integer>, ItemStack> items) {
		if (!loaded) {
			System.out.println("Class did not initialize correctly, casting recipes cannot be added!");
			return;
		}
		if (!isValid(out)) {
			System.out.println("You cannot add alternate recipes for native ChromatiCraft items!");
			return;
		}
		try {
			Object r = instMulti.newInstance(out, ctr);
			if (runes != null) {
				for (List<Integer> key : runes.keySet()) {
					rune.invoke(r, runes.get(key), key.get(0).intValue(), key.get(1).intValue(), key.get(2).intValue());
				}
			}
			for (List<Integer> key : items.keySet()) {
				aux.invoke(r, items.get(key), key.get(0).intValue(), key.get(1).intValue());
			}
			add.invoke(instance, r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Use this to add a level four "pylon" casting recipe.
	 * Args: Output item, central item, rune map (may be null), itemstack map (may NOT be null), energy map (may NOT be null) */
	public static void addPylonCastingRecipe(ItemStack out, ItemStack ctr, Map<List<Integer>, CrystalElementProxy> runes, Map<List<Integer>, ItemStack> items, Map<CrystalElementProxy, Integer> energy) {
		if (!loaded) {
			System.out.println("Class did not initialize correctly, casting recipes cannot be added!");
			return;
		}
		if (!isValid(out)) {
			System.out.println("You cannot add alternate recipes for native ChromatiCraft items!");
			return;
		}
		try {
			Object r = instPylon.newInstance(out, ctr);
			if (runes != null) {
				for (List<Integer> key : runes.keySet()) {
					rune.invoke(r, runes.get(key), key.get(0).intValue(), key.get(1).intValue(), key.get(2).intValue());
				}
			}
			for (List<Integer> key : items.keySet()) {
				aux.invoke(r, items.get(key), key.get(0).intValue(), key.get(1).intValue());
			}
			for (CrystalElementProxy e : energy.keySet()) {
				aura.invoke(r, e, energy.get(e).intValue());
			}
			add.invoke(instance, r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isValid(ItemStack out) {
		return !out.getItem().getClass().getName().startsWith("Reika.ChromatiCraft.Items");
	}

	static {
		try {
			loaded = false;

			cast = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe");
			instCast = cast.getDeclaredConstructor(ItemStack.class, IRecipe.class);
			instCast.setAccessible(true);

			temple = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe$TempleCastingRecipe");
			instTemple = temple.getDeclaredConstructor(ItemStack.class, IRecipe.class);
			instTemple.setAccessible(true);
			rune = temple.getDeclaredMethod("addRune", CrystalElementProxy.class, int.class, int.class, int.class);
			rune.setAccessible(true);

			multi = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe$MultiBlockCastingRecipe");
			instMulti = multi.getDeclaredConstructor(ItemStack.class, ItemStack.class);
			instMulti.setAccessible(true);
			aux = multi.getDeclaredMethod("addAuxItem", ItemStack.class, int.class, int.class);
			aux.setAccessible(true);

			pylon = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe$PylonRecipe");
			instPylon = pylon.getDeclaredConstructor(ItemStack.class, ItemStack.class);
			instPylon.setAccessible(true);
			aura = pylon.getDeclaredMethod("addAuraRequirement", CrystalElementProxy.class, int.class);
			aura.setAccessible(true);

			recipes = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable");
			instance = recipes.getField("instance").get(null);
			add = recipes.getDeclaredMethod("addModdedRecipe", cast);
			add.setAccessible(true);

			loaded = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
