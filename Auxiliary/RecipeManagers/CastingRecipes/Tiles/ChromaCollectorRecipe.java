/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;

public class ChromaCollectorRecipe extends CastingRecipe implements CoreRecipe {

	public ChromaCollectorRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 1;
	}

	@Override
	public int getPenaltyThreshold() {
		return 1;
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0;
	}

}
