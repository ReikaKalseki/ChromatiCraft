/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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

	public SelectiveGlassRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public int getExperience() {
		return super.getExperience()/2;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 128;
	}

	@Override
	public int getNumberProduced() {
		return 6;
	}

}
