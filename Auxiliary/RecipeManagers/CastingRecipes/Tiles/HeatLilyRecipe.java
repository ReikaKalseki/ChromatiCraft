/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class HeatLilyRecipe extends TempleCastingRecipe {

	public HeatLilyRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.ORANGE, -3, -1, 0);
		this.addRune(CrystalElement.ORANGE, 3, -1, 0);
	}

	@Override
	public int getNumberProduced() {
		return 3;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}

}
