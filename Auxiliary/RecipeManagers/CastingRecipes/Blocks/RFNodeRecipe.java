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

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class RFNodeRecipe extends TempleCastingRecipe {

	public RFNodeRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.LIME, -1, 0, -3);
		this.addRune(CrystalElement.YELLOW, 1, 0, 3);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 12;
	}

	@Override
	public int getNumberProduced() {
		return 1;
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

}
