/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class EnderBucketRecipe extends TempleCastingRecipe {

	public EnderBucketRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRuneRingRune(CrystalElement.LIME);
		this.addRuneRingRune(CrystalElement.CYAN);
	}

}
