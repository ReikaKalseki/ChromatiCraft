package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

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
import Reika.DragonAPI.Instantiable.Data.Maps.BranchingMap;
import Reika.DragonAPI.Instantiable.Data.Maps.BranchingMap.Topology;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

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
				RecipePrereq pre = new RecipePrereq(null, new ItemMatch(c.getOutput()), c, amt*c.getOutput().stackSize);
				prereqs = new RecipeChain(pre);
				if (this.determinePrerequisites(pre)) {
					prereqs.calculate();

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

	private boolean determinePrerequisites(RecipePrereq r) {
		CountMap<ItemMatch> used = r.recipe.getItemCounts();
		for (ItemMatch im : used.keySet()) {
			int amt = used.get(im);
			int has1 = this.countItem(im);
			int has2 = prereqs.surplus.get(im);
			if (has1 >= amt) { //none needed

			}
			else if (has1+has2 >= amt) { //use from surplus, but no crafts
				int num = amt-has1;
				prereqs.surplus.increment(im, -num);
			}
			else {
				int needed = amt-has1-has2;
				prereqs.surplus.increment(im, -has2);
				RecipePrereq c = this.selectRecipeToMake(r, im, needed);
				if (c != null) {
					this.queuePrereq(r, c);
				}
				else {
					return false;
				}
			}
		}
		return true;
	}

	private RecipePrereq selectRecipeToMake(RecipePrereq parent, ItemMatch im, int needed) {
		Collection<CastingRecipe> c = new ArrayList();
		for (KeyedItemStack is : im.getItemList()) {
			c.addAll(RecipesCastingTable.instance.getAllRecipesMaking(is.getItemStack()));
		}
		if (c.isEmpty())
			return null;
		int max = -1;
		CastingRecipe sel = null;
		for (CastingRecipe cr : c) {
			if (this.canRecurseTo(cr, parent, im)) {
				int w = this.getRecipeValue(cr);
				if (sel == null || w > max) {
					sel = cr;
					max = w;
				}
			}
		}
		return sel != null ? new RecipePrereq(parent, im, sel, needed) : null;
	}

	private boolean canRecurseTo(CastingRecipe cr, RecipePrereq parent, ItemMatch im) {
		LinkedList<String> path = prereqs.getRecipePath(parent);
		return !path.contains(cr.getIDString());
	}

	private void queuePrereq(RecipePrereq p, RecipePrereq c) {
		prereqs.addRecipe(p, c);
		this.determinePrerequisites(c);
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

	private static class RecipeChain {

		private final RecipePrereq root;

		private final BranchingMap<RecipePrereq> recipes = new BranchingMap();
		//private final HashSet<String> usedRecipes = new HashSet();
		private final CountMap<ItemMatch> surplus = new CountMap();

		private Topology<RecipePrereq> map;

		private RecipeChain(RecipePrereq root) {
			this.root = root;
			this.addRecipe(null, root);
			//usedRecipes.add(root.recipe.getIDString());
		}

		private void addRecipe(RecipePrereq parent, RecipePrereq rc) {
			if (parent != null)
				recipes.addChild(parent, rc);
			else
				recipes.addChildless(rc);
			//usedRecipes.add(rc.recipe.getIDString());
			if (rc.surplus > 0)
				surplus.increment(rc.item, rc.surplus);
		}

		private void calculate() {
			map = recipes.getTopology();
		}

		private RecipePrereq getNextInQueue(Random rand) {
			Collection<RecipePrereq> c = map.getByDepth(map.getMaxDepth());
			return ReikaJavaLibrary.getRandomCollectionEntry(rand, c);
		}

		private LinkedList<String> getRecipePath(RecipePrereq r) {
			LinkedList<RecipePrereq> li = recipes.getPathTo(r);
			LinkedList<String> ret = new LinkedList();
			for (RecipePrereq rp : li) {
				ret.add(rp.recipe.getIDString());
			}
			return ret;
		}

		@Override
		public String toString() {
			return recipes.toString();
		}

	}

	private static class RecipePrereq {

		/** What recipe is this a prereq for */
		private final RecipePrereq parent;
		private final CastingRecipe recipe;
		private final ItemMatch item;
		public final int totalCraftsNeeded;
		private final int surplus;

		private int craftsRemaining;

		private RecipePrereq(RecipePrereq p, ItemMatch im, CastingRecipe cr, int itemsNeeded) {
			parent = p;
			recipe = cr;
			item = im;
			int per = cr.getOutput().stackSize;
			totalCraftsNeeded = MathHelper.ceiling_float_int(itemsNeeded/(float)per);
			craftsRemaining = totalCraftsNeeded;
			int crafted = totalCraftsNeeded*per;
			surplus = crafted-itemsNeeded;
		}

		private boolean craft() {
			craftsRemaining--;
			return craftsRemaining == 0;
		}

		private RecipePrereq getRoot() {
			RecipePrereq p = parent;
			while (p.parent != null) {
				p = p.parent;
			}
			return p;
		}

		@Override
		public String toString() {
			return "("+recipe.toString()+") x"+totalCraftsNeeded;
		}

		public String getChain() {
			LinkedList<RecipePrereq> li = new LinkedList();
			RecipePrereq o = this;
			while (o != null) {
				li.add(o);
				o = o.parent;
			}
			Collections.reverse(li);
			StringBuilder sb = new StringBuilder();
			sb.append("Recipe prereq chain:\n");
			int i = 0;
			for (RecipePrereq at : li) {
				sb.append(ReikaStringParser.getNOf(" ", i*4)+at.toString()+"\n");
				i++;
			}
			return sb.toString();
		}

	}

}
