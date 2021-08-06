/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
 * <b>{@code IRecipe:}</b> Used for the lower two tiers. This is a recipe object for a 3x3 grid; it can be shaped or shapeless, and may use OreDict.
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
public interface CastingAPI {

	/** Use this to add a level one "crafting type" casting recipe.
	 * Args: Recipe object
	 * Returns the casting recipe object. */
	public APICastingRecipe addCastingRecipe(IRecipe ir);

	/** Use this to add a level two "temple/rune type" casting recipe.
	 * Args: Recipe object, rune map (may NOT be null)
	 * Returns the casting recipe object. */
	public RuneTempleRecipe addTempleCastingRecipe(IRecipe ir, Map<List<Integer>, CrystalElementProxy> runes);

	/** Use this to add a level three "multiblock" casting recipe.
	 * Args: Output item, central item, rune map (may be null), itemstack map (may NOT be null)
	 * Returns the casting recipe object. */
	public MultiRecipe addMultiBlockCastingRecipe(ItemStack out, ItemStack ctr, Map<List<Integer>, CrystalElementProxy> runes, Map<List<Integer>, ItemStack> items);

	/** Use this to add a level four "pylon" casting recipe.
	 * Args: Output item, central item, rune map (may be null), itemstack map (may NOT be null), energy map (may NOT be null)
	 * Returns the casting recipe object. */
	public LumenRecipe addPylonCastingRecipe(ItemStack out, ItemStack ctr, Map<List<Integer>, CrystalElementProxy> runes, Map<List<Integer>, ItemStack> items, Map<CrystalElementProxy, Integer> energy);

	public static interface FXCallback {

		@SideOnly(Side.CLIENT)
		/** Spawn particles, play sounds, etc. */
		public void onEffectTick(TileEntity te, Object recipe, ItemStack output);

	}

	/** Implemented by the casting recipes themselves. This interface is implemented by all the recipes. */
	public static interface APICastingRecipe {

		/** Adds an FX callback for your own custom recipes. Will error if a callback is already present, or the recipe is native to the mod. */
		public void setFXHook(FXCallback call);

		/** The recipe tier, from 0 to 3. Corresponds to the four tiers described in {@link CastingAPI}. Use this to check before casting to
		 * higher-level recipe interfaces */
		public int getTier();

		public ItemStack getOutput();

		/** Whether a player is permitted to perform a given recipe, such as progression and other data. Null or fake players are not permitted. */
		public boolean canRunRecipe(TileEntity te, EntityPlayer ep);

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

		/** Some recipes modify NBT on the output item. Use this to determine the output. Null is permitted. The supplied tag is that of the main center item.*/
		public NBTTagCompound getOutputTag(EntityPlayer ep, NBTTagCompound input);

	}

	/** Rune-level crafting. */
	public static interface RuneTempleRecipe extends APICastingRecipe {

		/** The rune locations. The list is of relative XYZ coords, with a given color at each position. */
		public Map<List<Integer>, CrystalElementProxy> getRunePositions();

	}

	/** Multiblock (Item Casting Stand, 5x5 grid) recipes */
	public static interface MultiRecipe extends RuneTempleRecipe {

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
