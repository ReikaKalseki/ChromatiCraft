/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CrystalStarRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.StorageCrystalRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesCastingTable {

	public static final RecipesCastingTable instance = new RecipesCastingTable();
	private final HashMap<RecipeType, ArrayList<CastingRecipe>> recipes = new HashMap();

	private RecipesCastingTable() {
		this.addRecipe(new CrystalStarRecipe(new ItemStack(Items.nether_star), ChromaStacks.crystalStar));
		this.addRecipe(new StorageCrystalRecipe(ChromaStacks.rawCrystal, ChromaItems.STORAGE.getStackOf()));
	}

	private void addRecipe(CastingRecipe r) {
		ArrayList<CastingRecipe> li = recipes.get(r.type);
		if (li == null) {
			li = new ArrayList();
			recipes.put(r.type, li);
		}
		li.add(r);
	}

	public static enum RecipeType {
		CRAFTING(5, 250),
		TEMPLE(40, 2000),
		MULTIBLOCK(200, 15000),
		PYLON(500, Integer.MAX_VALUE);

		public final int experience;
		public final int levelUp;

		public static final RecipeType[] typeList = values();

		private RecipeType(int xp, int lvl) {
			experience = xp;
			levelUp = lvl;
		}

		public int getRequiredXP() {
			return this == CRAFTING ? 0 : typeList[this.ordinal()-1].levelUp;
		}

		public RecipeType next() {
			return this == PYLON ? this : typeList[this.ordinal()+1];
		}

		public boolean isAtLeast(RecipeType r) {
			return this.ordinal() >= r.ordinal();
		}

		public boolean isMoreThan(RecipeType r) {
			return this.ordinal() > r.ordinal();
		}
	}

	public CastingRecipe getRecipe(TileEntityCastingTable table, ArrayList<RecipeType> type) {
		ItemStack main = table.getStackInSlot(0);
		ArrayList<CastingRecipe> li = new ArrayList();
		for (int i = 0; i < type.size(); i++) {
			ArrayList<CastingRecipe> list = recipes.get(type.get(i));
			if (list != null)
				li.addAll(list);
		}
		for (int i = 0; i < li.size(); i++) {
			CastingRecipe r = li.get(i);
			if (r.match(table))
				return r;
		}
		return null;
	}

}
