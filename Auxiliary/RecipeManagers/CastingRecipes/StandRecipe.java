/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class StandRecipe extends TempleCastingRecipe {

	public StandRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.PURPLE, -2, 0, 3);
		this.addRune(CrystalElement.PURPLE, 2, 0, -3);
		this.addRune(CrystalElement.WHITE, -2, 0, -3);
		this.addRune(CrystalElement.WHITE, 2, 0, 3);
	}

}
