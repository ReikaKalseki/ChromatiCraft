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

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;


public class SelectiveGlassRecipe extends CastingRecipe {

	private final boolean enhanced;

	public SelectiveGlassRecipe(ItemStack out, IRecipe recipe, boolean t2) {
		super(out, recipe);
		enhanced = t2;
	}

	@Override
	public int getExperience() {
		return super.getExperience()/2;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return enhanced ? 16 : 32;
	}

	@Override
	public int getNumberProduced() {
		return enhanced ? 18 : 9;
	}

}
