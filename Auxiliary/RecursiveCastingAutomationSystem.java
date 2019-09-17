package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.common.eventhandler.Event.Result;

public class RecursiveCastingAutomationSystem extends CastingAutomationSystem {

	private RecipeChain prereqs;

	private final HashSet<String> priorityRecipes = new HashSet();

	private final ArrayList<ItemStack> cachedIngredients = new ArrayList();

	public RecursiveCastingAutomationSystem(CastingAutomationBlock te) {
		super(te);
	}

	@Override
	public void setRecipe(CastingRecipe c, int amt) {
		if (c != null && tile.canRecursivelyRequest(c)) {
			try {
				prereqs = new RecipeChain(tile.getAvailableRecipes());
				RecipePrereq pre = prereqs.createPrereq(null, new ItemMatch(c.getOutput()), c, amt*c.getOutput().stackSize);
				Result res = this.determinePrerequisites(pre);
				while (res == Result.DEFAULT) {
					res = this.determinePrerequisites(pre);
				}
				if (res == Result.ALLOW) {
					//take items, set up crafting system
					ReikaJavaLibrary.pConsole("Found recursive crafting for "+c+":");
					ReikaJavaLibrary.pConsole(prereqs.toString());
				}
				else {
					ReikaJavaLibrary.pConsole("Could not recursively craft "+c);
					//either no valid recipe paths, or uncraftable items
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.setRecipe(c, amt);
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

	public void setRecipePriority(CastingRecipe cr, boolean has) {
		String s = cr.getIDString();
		if (has) {
			priorityRecipes.add(s);
		}
		else {
			priorityRecipes.remove(s);
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

		ReikaNBTHelper.writeCollectionToNBT(priorityRecipes, NBT, "priority");
		ReikaNBTHelper.writeCollectionToNBT(cachedIngredients, NBT, "ingredients");
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		ReikaNBTHelper.readCollectionFromNBT(priorityRecipes, NBT, "priority");
		ReikaNBTHelper.readCollectionFromNBT(cachedIngredients, NBT, "ingredients");
	}

	@Override
	public void onBreak(World world) {
		super.onBreak(world);
		ReikaItemHelper.dropItems(world, this.getX()+0.5, this.getY()+0.5, this.getZ()+0.5, cachedIngredients);
	}

	private class RecipeChain {

		private final HashMap<ItemMatch, RecipePrereq> recipes = new HashMap();
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
			RecipePrereq ret = recipes.get(im);
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
			recipes.put(im, ret);
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
			totalNeeded.clear();
			totalProduction.clear();
			for (RecipePrereq r : recipes.values()) {
				totalProduction.increment(r.item, r.recipe.getOutput().stackSize);
				totalNeeded.increment(r.recipe.getItemCounts());
			}

			//Trim surplus
			//TODO

			for (RecipePrereq r : recipes.values()) {
				r.calculate();
			}
		}

		public void craft(RecipePrereq r) {
			if (r.craft()) {
				recipes.remove(r.item);
				for (RecipePrereq r2 : recipes.values()) {
					r2.dependencies.remove(r);
				}
			}
		}

		public RecipePrereq getNextInQueue() {
			for (RecipePrereq req : recipes.values()) {
				if (req.isReady()) {
					return req;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return recipes.values().toString();
		}

	}

	private static class RecipePrereq {

		private final CastingRecipe recipe;
		private final ItemMatch item;

		private int depth;

		private int totalItemsNeeded;
		private int craftsRemaining;

		private final Collection<RecipePrereq> dependencies = new HashSet();

		private RecipePrereq(ItemMatch im, CastingRecipe cr, int n) {
			recipe = cr;
			item = im;
			totalItemsNeeded = n;
		}

		private boolean craft() {
			craftsRemaining--;
			return craftsRemaining == 0;
		}

		private void calculate() {
			craftsRemaining = MathHelper.ceiling_float_int(totalItemsNeeded/(float)recipe.getOutput().stackSize);
		}

		public boolean isReady() {
			return dependencies.isEmpty();
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

}
