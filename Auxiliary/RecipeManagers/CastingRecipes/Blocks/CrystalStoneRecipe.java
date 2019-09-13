/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;

public class CrystalStoneRecipe extends CastingRecipe implements CoreRecipe {

	private static final CrystalStoneRecipe[] recipesByMeta = new CrystalStoneRecipe[16];

	public CrystalStoneRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public boolean canBeSimpleAutomated() {
		return true;
	}

	public static CrystalStoneRecipe getRecipeForMeta(int meta) {
		return recipesByMeta[meta];
	}

}
