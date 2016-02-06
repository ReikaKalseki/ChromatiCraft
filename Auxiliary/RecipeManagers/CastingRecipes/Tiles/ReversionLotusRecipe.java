/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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

public class ReversionLotusRecipe extends TempleCastingRecipe {

	public ReversionLotusRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.GREEN, 2, -1, -5);
		this.addRune(CrystalElement.GREEN, -2, -1, 5);
	}

	@Override
	public int getNumberProduced() {
		return 6;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 32;
	}

}
