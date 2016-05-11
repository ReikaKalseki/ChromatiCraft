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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class CobbleGenRecipe extends TempleCastingRecipe {

	public CobbleGenRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.ORANGE, 4, -1, -3);
		this.addRune(CrystalElement.CYAN, 4, -1, 3);
		this.addRune(CrystalElement.BROWN, -4, -1, -1);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 2;
	}

}
