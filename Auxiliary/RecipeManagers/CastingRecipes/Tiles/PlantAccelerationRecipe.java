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

public class PlantAccelerationRecipe extends TempleCastingRecipe {

	public PlantAccelerationRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.LIGHTBLUE, 2, -1, -4);
		this.addRune(CrystalElement.GREEN, -2, -1, 4);
	}

	@Override
	public int getNumberProduced() {
		return 2;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}

}
