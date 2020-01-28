package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Collections.ItemCollection;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.common.eventhandler.Event.Result;

public class RecursiveCastingAutomationSystem extends CastingAutomationSystem {

	//private boolean isChainCrafting;
	private RecipeChain prereqs;

	private final HashSet<String> priorityRecipes = new HashSet();

	private final IngredientCache cachedIngredients = new IngredientCache();

	public boolean recursionEnabled = false;

	public RecursiveCastingAutomationSystem(CastingAutomationBlock te) {
		super(te);
	}

	@Override
	public void tick(World world) {
		super.tick(world);
		if (this.isIdle() && prereqs != null) {
			RecipePrereq p = prereqs.getNextInQueue();
			//ReikaJavaLibrary.pConsole("QUEUED TO: "+p);
			if (p != null) {
				super.setRecipe(p.recipe, p.craftsRemaining, currentPlayer);
			}
		}
	}

	public boolean isRecursiveCrafting() {
		return prereqs != null;
	}

	public void cacheIngredient(ItemStack is) {
		cachedIngredients.add(is);
	}

	@Override
	protected ItemCollection getExtraItems(Object item, int amt, boolean simulate, boolean allowMultiple) {
		if (item instanceof Block) {
			item = new ItemMatch((Block)item);
		}
		else if (item instanceof Item) {
			item = new ItemMatch((Item)item);
		}
		else if (item instanceof ItemStack) {
			item = new ItemMatch((ItemStack)item);
		}
		Ingredient i = cachedIngredients.data.get(item);
		return i != null ? i.found : null;
	}

	@Override
	protected void onTriggerCrafting(CastingRecipe r, int cycles) {
		if (prereqs != null) {
			prereqs.craft(r, cycles);
			if (prereqs.isDone()) {
				//ReikaJavaLibrary.pConsole("Recursive crafting is done.");
				this.recoverCachedIngredients();
				prereqs = null;
			}
		}
	}

	private void recoverCachedIngredients() {
		Iterator<Entry<ItemMatch, Ingredient>> it = cachedIngredients.data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<ItemMatch, Ingredient> e = it.next();
			Ingredient i = e.getValue();
			if (this.recoverIngredient(i)) {
				it.remove();
			}
		}
	}

	private boolean recoverIngredient(Ingredient i) {
		Collection<ItemStack> c = new ArrayList(i.found.getItems());
		for (ItemStack is : c) {
			if (this.recoverItem(is)) {
				i.found.removeItem(is);
			}
		}
		return i.found.isEmpty();
	}

	@Override
	public void setRecipe(CastingRecipe c, int amt, EntityPlayer ep) {
		this.recoverCachedIngredients();
		prereqs = null;
		if (c != null && recursionEnabled && tile.canRecursivelyRequest(c) && cachedIngredients.isEmpty()) {
			prereqs = new RecipeChain(tile.getAvailableRecipes());
			RecipePrereq pre = prereqs.createPrereq(null, new ItemMatch(c.getOutput()), c, amt*c.getOutput().stackSize);
			Result res = this.determinePrerequisites(pre);
			int tries = 0;
			while (res == Result.DEFAULT && tries < 60) {
				res = this.determinePrerequisites(pre);
				tries++;
			}
			if (res == Result.ALLOW) {
				//take items, set up crafting system
				this.intakeNecessaryItems();
				//ReikaJavaLibrary.pConsole("Found recursive crafting for "+c+":");
				//ReikaJavaLibrary.pConsole(prereqs.toString());
			}
			else {
				//ReikaJavaLibrary.pConsole("Could not recursively craft "+c+"; "+res);
				//either no valid recipe paths, or uncraftable items
				prereqs = null;
			}
			super.setRecipe(null, 0, ep);
		}
		else {
			super.setRecipe(c, amt, ep);
		}
	}

	private void intakeNecessaryItems() {
		for (ItemMatch im : prereqs.getAllUsedItems()) { //second verification pass
			int amt = prereqs.getDeficit(im);
			int has = this.countItem(im);
			if (has < amt) {
				//ReikaJavaLibrary.pConsole("Missing items on second check, cannot craft!");
				prereqs = null;
				return;
			}
		}
		cachedIngredients.clear();
		for (ItemMatch im : prereqs.getAllUsedItems()) {
			int amt = prereqs.getDeficit(im);
			if (amt > 0) {
				Collection<ItemStack> is = this.findItems(im, amt, false);
				if (is == null) {
					ReikaJavaLibrary.pConsole("Got null list for "+im+"?!?!");
				}
				cachedIngredients.add(im, is);
			}
		}
		//ReikaJavaLibrary.pConsole("Took in intermediate ingredients: "+cachedIngredients.toString());
	}

	private Result determinePrerequisites(RecipePrereq r) {
		prereqs.calculateItems();
		CountMap<ItemMatch> needed = new CountMap();
		for (ItemMatch im : prereqs.getAllUsedItems()) {
			int amt = prereqs.getDeficit(im);
			if (amt > 0) {
				int has = this.countItem(im);
				if (has < amt) {
					int craft = amt-has;
					needed.increment(im, craft);
				}
			}
		}
		if (needed.getTotalCount() == 0) {
			return Result.ALLOW;
		}
		for (ItemMatch im : needed.keySet()) {
			RecipePrereq c = prereqs.selectRecipeToMake(r, im, needed.get(im));
			if (c != null) {

			}
			else {
				return Result.DENY;
			}
		}
		return Result.DEFAULT;
	}

	public void toggleRecipePriority(CastingRecipe cr) {
		String s = cr.getIDString();
		if (priorityRecipes.contains(s)) {
			priorityRecipes.remove(s);
		}
		else {
			priorityRecipes.add(s);
		}
	}

	public boolean isRecipePriority(CastingRecipe cr) {
		return priorityRecipes.contains(cr.getIDString());
	}

	private int getRecipeValue(CastingRecipe cr) {
		int base = this.getRecipeEfficiencyValue(cr);
		if (this.isRecipePriority(cr))
			base += 1000000;
		if (this.hasAllIngredients(cr))
			base += 100000000;
		return base;
	}

	private int getRecipeEfficiencyValue(CastingRecipe cr) {
		int out = cr.getOutput().stackSize;
		ElementTagCompound tag = cr.getInputElements(true);
		return (out*1000)/(cr.getInputCount()+tag.getTotalEnergy());
	}

	private boolean hasAllIngredients(CastingRecipe cr) {
		if (cr instanceof MultiBlockCastingRecipe) {
			MultiBlockCastingRecipe mb = (MultiBlockCastingRecipe)cr;
			ItemStack ctr = mb.getMainInput();
			if (!this.hasItem(ctr, mb.getRequiredCentralItemCount()))
				return false;
			for (ItemMatch im : mb.getAuxItems().values()) {
				if (!this.hasItem(im, 1))
					return false;
			}
		}
		else {
			Object[] in = cr.getInputArray();
			for (Object o : in) {
				if (!this.hasItem(o, 1)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setBoolean("recursion", recursionEnabled);

		ReikaNBTHelper.writeCollectionToNBT(priorityRecipes, NBT, "priority");
		NBTTagCompound tag = new NBTTagCompound();
		cachedIngredients.writeToNBT(tag);
		NBT.setTag("ingredients", tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		recursionEnabled = NBT.getBoolean("recursion");

		ReikaNBTHelper.readCollectionFromNBT(priorityRecipes, NBT, "priority");
		cachedIngredients.readFromNBT(NBT.getCompoundTag("ingredients"));
	}

	@Override
	public void onBreak(World world) {
		super.onBreak(world);
		cachedIngredients.drop(world, this.getX(), this.getY(), this.getZ());
	}

	private class RecipeChain {

		private final HashMap<ItemMatch, RecipePrereq> recipesByItem = new HashMap();
		private final HashMap<String, RecipePrereq> recipesByRecipe = new HashMap();
		private final HashSet<String> validRecipes = new HashSet();
		private final CountMap<ItemMatch> totalNeeded = new CountMap();
		private final CountMap<ItemMatch> totalProduction = new CountMap();

		private RecipePrereq root;

		private RecipeChain(Collection<CastingRecipe> c) {
			for (CastingRecipe cr : c) {
				validRecipes.add(cr.getIDString());
			}
		}

		public Collection<ItemMatch> getAllUsedItems() {
			return totalNeeded.keySet();
		}

		public RecipePrereq getCachedRecipe(ItemMatch im) {
			RecipePrereq ret = recipesByItem.get(im);
			return ret;
		}

		public RecipePrereq getCachedRecipe(CastingRecipe cr) {
			RecipePrereq ret = recipesByRecipe.get(cr.getIDString());
			return ret;
		}

		public RecipePrereq selectRecipeToMake(RecipePrereq p, ItemMatch im, int needed) {
			RecipePrereq pre = this.getCachedRecipe(im);
			if (pre != null) {
				p.dependencies.add(pre);
				pre.totalItemsNeeded += needed;
				return pre;
			}
			Collection<CastingRecipe> c = new ArrayList();
			for (KeyedItemStack is : im.getItemList()) {
				c.addAll(RecipesCastingTable.instance.getAllRecipesMaking(is.getItemStack()));
			}
			if (c.isEmpty())
				return null;
			int max = -1;
			CastingRecipe sel = null;
			for (CastingRecipe cr : c) {
				if (this.isRecursable(cr)) {
					int w = RecursiveCastingAutomationSystem.this.getRecipeValue(cr);
					if (sel == null || w > max) {
						sel = cr;
						max = w;
					}
				}
			}
			return sel != null ? this.createPrereq(p, im, sel, needed) : null;
		}

		public RecipePrereq createPrereq(RecipePrereq p, ItemMatch im, CastingRecipe sel, int needed) {
			RecipePrereq ret = new RecipePrereq(im, sel, needed);
			recipesByItem.put(im, ret);
			recipesByRecipe.put(sel.getIDString(), ret);
			if (p != null)
				p.dependencies.add(ret);
			if (p == null && root == null)
				root = ret;
			return ret;
		}

		public boolean isRecursable(CastingRecipe cr) {
			return validRecipes.contains(cr.getIDString());
		}

		public int getDeficit(ItemMatch im) {
			return totalNeeded.get(im)-totalProduction.get(im);
		}

		private void calculateItems() {
			for (RecipePrereq r : recipesByItem.values()) {
				r.calculate();
			}

			totalNeeded.clear();
			totalProduction.clear();
			for (RecipePrereq r : recipesByItem.values()) {
				for (int i = 0; i < r.craftsRemaining; i++) {
					totalProduction.increment(r.item, r.recipe.getOutput().stackSize);
					totalNeeded.increment(r.recipe.getItemCounts());
				}
			}

			//Trim surplus
			//TODO?
		}

		public void craft(CastingRecipe r, int cycles) {
			RecipePrereq r0 = this.getCachedRecipe(r);
			if (r0 != null && r0.craft(cycles)) {
				this.removeRecipe(r0);
				for (RecipePrereq r2 : recipesByItem.values()) {
					r2.dependencies.remove(r);
				}
			}
		}

		public boolean isDone() {
			return recipesByItem.isEmpty();
		}

		private void removeRecipe(RecipePrereq r0) {
			recipesByItem.remove(r0.item);
			recipesByRecipe.remove(r0.recipe.getIDString());
		}

		public RecipePrereq getNextInQueue() {
			for (RecipePrereq req : recipesByItem.values()) {
				if (req.isReady()) {
					return req;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return recipesByItem.values().toString();
		}

	}

	private static class RecipePrereq {

		private final CastingRecipe recipe;
		private final ItemMatch item;

		private int totalItemsNeeded;
		private int craftsRemaining;

		private final Collection<RecipePrereq> dependencies = new HashSet();

		private RecipePrereq(ItemMatch im, CastingRecipe cr, int n) {
			recipe = cr;
			item = im;
			totalItemsNeeded = n;
		}

		private boolean craft(int cycles) {
			craftsRemaining -= Math.min(craftsRemaining, cycles);
			return craftsRemaining <= 0;
		}

		private void calculate() {
			craftsRemaining = MathHelper.ceiling_float_int(totalItemsNeeded/(float)recipe.getOutput().stackSize);
		}

		public boolean isReady() {
			for (RecipePrereq r : dependencies) {
				if (r.craftsRemaining > 0)
					return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return recipe.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof RecipePrereq && ((RecipePrereq)o).recipe.equals(recipe);
		}

		@Override
		public String toString() {
			return "("+recipe.toString()+") x"+totalItemsNeeded+" / "+craftsRemaining;
		}

	}

	private static class IngredientCache {

		private HashMap<ItemMatch, Ingredient> data = new HashMap();

		public int subtract(ItemMatch im, int amt) {
			Ingredient i = data.get(im);
			int ret = i.removeItems(amt);
			if (i.found.isEmpty())
				data.remove(im);
			return ret;
		}

		public Collection<Ingredient> getItems() {
			return Collections.unmodifiableCollection(data.values());
		}

		public boolean isEmpty() {
			return data.isEmpty();
		}

		public void add(ItemStack is) {
			for (Ingredient i : data.values()) {
				if (i.seek.match(is)) {
					i.found.add(is);
					return;
				}
			}
			this.add(new ItemMatch(is), is);
		}

		private void add(ItemMatch im, ItemStack is) {
			Ingredient i = data.get(im);
			if (i == null) {
				ItemCollection ic = new ItemCollection();
				ic.add(is);
				i = new Ingredient(im, ic);
				data.put(im, i);
			}
			else {
				i.found.add(is);
			}
		}

		public void add(ItemMatch im, Collection<ItemStack> is) {
			Ingredient i = data.get(im);
			if (i == null) {
				i = new Ingredient(im, new ItemCollection(is));
				data.put(im, i);
			}
			else {
				i.addItems(is);
			}
		}

		public int count(ItemMatch im) {
			Ingredient i = data.get(im);
			return i != null ? i.found.count() : 0;
		}

		public void drop(World world, int x, int y, int z) {
			for (Ingredient i : data.values()) {
				i.found.drop(world, x, y, z);
			}
		}

		public void writeToNBT(NBTTagCompound NBT) {
			NBTTagList li = new NBTTagList();
			for (Entry<ItemMatch, Ingredient> e : data.entrySet()) {
				NBTTagCompound tag = new NBTTagCompound();
				NBTTagCompound key = new NBTTagCompound();
				NBTTagCompound value = new NBTTagCompound();

				e.getKey().writeToNBT(key);
				e.getValue().writeToNBT(value);

				tag.setTag("key", key);
				tag.setTag("value", value);
				li.appendTag(tag);
			}
			NBT.setTag("data", li);
		}

		public void readFromNBT(NBTTagCompound NBT) {
			this.clear();
			NBTTagList li = NBT.getTagList("data", NBTTypes.COMPOUND.ID);
			data.clear();
			for (Object o : li.tagList) {
				NBTTagCompound tag = (NBTTagCompound)o;
				NBTTagCompound key = tag.getCompoundTag("key");
				NBTTagCompound value = tag.getCompoundTag("value");

				ItemMatch im = ItemMatch.readFromNBT(key);
				Ingredient i = Ingredient.readFromNBT(value);
				data.put(im, i);
			}
		}

		public void clear() {
			data.clear();
		}

		@Override
		public String toString() {
			return data.values().toString();
		}

	}

	private static class Ingredient {

		private final ItemMatch seek;
		private final ItemCollection found;

		private Ingredient(ItemMatch im, ItemCollection is) {
			seek = im;
			found = is;
		}

		public void addItems(Collection<ItemStack> is) {
			found.add(is);
		}

		public int removeItems(int amt) {
			return found.removeItems(amt);
		}

		@Override
		public String toString() {
			return seek.toString()+" > "+found.toString();
		}

		public void writeToNBT(NBTTagCompound NBT) {
			NBTTagCompound tag1 = new NBTTagCompound();
			NBTTagCompound tag2 = new NBTTagCompound();
			found.writeToNBT(tag1);
			seek.writeToNBT(tag2);
			NBT.setTag("item", tag1);
			NBT.setTag("match", tag2);
			//NBT.setInteger("amount", amount);
		}

		public static Ingredient readFromNBT(NBTTagCompound NBT) {
			NBTTagCompound tag1 = NBT.getCompoundTag("item");
			NBTTagCompound tag2 = NBT.getCompoundTag("match");
			ItemCollection is = new ItemCollection();
			is.readFromNBT(tag1);
			ItemMatch im = ItemMatch.readFromNBT(tag2);
			Ingredient ret = new Ingredient(im, is);
			//ret.amount = NBT.getInteger("amount");
			return ret;
		}

	}

}
