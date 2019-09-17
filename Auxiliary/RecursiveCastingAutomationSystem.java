package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class RecursiveCastingAutomationSystem extends CastingAutomationSystem {

	private LinkedList<RecipePrereq> prereqs = new LinkedList();

	private final HashSet<String> priorityRecipes = new HashSet();

	private final ArrayList<ItemStack> cachedIngredients = new ArrayList();

	public RecursiveCastingAutomationSystem(CastingAutomationBlock te) {
		super(te);
	}

	@Override
	public void setRecipe(CastingRecipe c, int amt) {
		if (tile.canRecursivelyRequest()) {
			this.determinePrerequisites(c);
			;//HashMap<CastingRecipe, Integer> li = this.determineMissingRecipes(tile.getTable(), c, amt);
		}
		super.setRecipe(c, amt);
	}

	private void determinePrerequisites(CastingRecipe c) {

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
			if (this.findItem(ctr, mb.getRequiredCentralItemCount(), true) == null)
				return false;
			for (ItemMatch im : mb.getAuxItems().values()) {
				if (this.findItem(im, 1, true) == null)
					return false;
			}
		}
		else {
			Object[] in = cr.getInputArray();
			for (Object o : in) {
				if (this.findItem(o, 1, true) == null) {
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

	private static class RecipePrereq {

		/** What recipe is this a prereq for */
		private final RecipePrereq parent;
		private final CastingRecipe recipe;
		public final int totalCraftsNeeded;

		private int craftsRemaining;

		private RecipePrereq(RecipePrereq p, CastingRecipe cr, int amt) {
			parent = p;
			recipe = cr;
			totalCraftsNeeded = amt;
			craftsRemaining = totalCraftsNeeded;
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

	}

}
