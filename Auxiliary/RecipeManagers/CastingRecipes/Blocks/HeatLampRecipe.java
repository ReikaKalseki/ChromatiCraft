/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.HeatLilyRecipe;

public class HeatLampRecipe extends TempleCastingRecipe {

	public HeatLampRecipe(ItemStack out, IRecipe recipe, HeatLilyRecipe r) {
		super(out, recipe);

		this.addRunes(r.getRunes());
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}

}
