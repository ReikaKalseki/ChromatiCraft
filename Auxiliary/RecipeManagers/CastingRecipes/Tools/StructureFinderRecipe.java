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


public class StructureFinderRecipe extends TempleCastingRecipe {

	public StructureFinderRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.BLACK, -3, -1, 2);
		this.addRune(CrystalElement.BLUE, 3, -1, 1);
		this.addRune(CrystalElement.LIME, 1, -1, -3);
		this.addRune(CrystalElement.YELLOW, -2, -1, -4);
	}

}
