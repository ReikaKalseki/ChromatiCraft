package Reika.ChromatiCraft.API;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;


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

	public static void addCastingRecipe(IRecipe ir) {
		if (!loaded) {
			System.out.println("Class did not initialize correctly, recipes cannot be added!");
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

	public static void addTempleCastingRecipe(IRecipe ir, Map<List<Integer>, Integer> runes) {
		if (!loaded) {
			System.out.println("Class did not initialize correctly, recipes cannot be added!");
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
				rune.invoke(r, runes.get(key).intValue(), key.get(0).intValue(), key.get(1).intValue(), key.get(2).intValue());
			}
			add.invoke(instance, r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addMultiBlockCastingRecipe(ItemStack out, ItemStack ctr, Map<List<Integer>, Integer> runes, Map<List<Integer>, ItemStack> items) {
		if (!loaded) {
			System.out.println("Class did not initialize correctly, recipes cannot be added!");
			return;
		}
		if (!isValid(out)) {
			System.out.println("You cannot add alternate recipes for native ChromatiCraft items!");
			return;
		}
		try {
			Object r = instTemple.newInstance(out, ctr);
			for (List<Integer> key : runes.keySet()) {
				rune.invoke(r, runes.get(key).intValue(), key.get(0).intValue(), key.get(1).intValue(), key.get(2).intValue());
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

	public static void addPylonCastingRecipe(ItemStack out, ItemStack ctr, Map<List<Integer>, Integer> runes, Map<List<Integer>, ItemStack> items, Map<Integer, Integer> energy) {
		if (!loaded) {
			System.out.println("Class did not initialize correctly, recipes cannot be added!");
			return;
		}
		if (!isValid(out)) {
			System.out.println("You cannot add alternate recipes for native ChromatiCraft items!");
			return;
		}
		try {
			Object r = instTemple.newInstance(out, ctr);
			for (List<Integer> key : runes.keySet()) {
				rune.invoke(r, runes.get(key).intValue(), key.get(0).intValue(), key.get(1).intValue(), key.get(2).intValue());
			}
			for (List<Integer> key : items.keySet()) {
				aux.invoke(r, items.get(key), key.get(0).intValue(), key.get(1).intValue());
			}
			for (Integer e : energy.keySet()) {
				aura.invoke(r, e.intValue(), energy.get(e).intValue());
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
			instCast = cast.getConstructor(ItemStack.class, IRecipe.class);

			temple = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe$TempleCastingRecipe");
			instTemple = temple.getConstructor(ItemStack.class, IRecipe.class);
			rune = temple.getMethod("addRune", int.class, int.class, int.class, int.class);
			rune.setAccessible(true);

			multi = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe$MultiBlockCastingRecipe");
			instMulti = multi.getConstructor(ItemStack.class, ItemStack.class);
			aux = multi.getMethod("addAuxItem", ItemStack.class, int.class, int.class);
			aux.setAccessible(true);

			pylon = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe$PylonRecipe");
			instPylon = pylon.getConstructor(ItemStack.class, ItemStack.class);
			aux = pylon.getMethod("addAuxItem", int.class, int.class);
			aura.setAccessible(true);

			recipes = Class.forName("Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable");
			instance = recipes.getField("instance").get(null);
			add = recipes.getDeclaredMethod("addRecipe", cast);
			add.setAccessible(true);

			loaded = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
