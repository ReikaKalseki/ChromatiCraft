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

public class SmelteryDistributorRecipe extends TempleCastingRecipe {

	public SmelteryDistributorRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.LIME, -1, 0, -3);
		this.addRune(CrystalElement.ORANGE, 4, 0, -2);
		this.addRune(CrystalElement.CYAN, -4, 0, 0);
		this.addRune(CrystalElement.BROWN, -2, 0, -4);
	}

}
