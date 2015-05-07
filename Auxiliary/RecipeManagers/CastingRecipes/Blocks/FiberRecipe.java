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
import Reika.ChromatiCraft.Registry.CrystalElement;

@Deprecated
public class FiberRecipe extends TempleCastingRecipe {

	public FiberRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.YELLOW, -1, -1, -5);
		this.addRune(CrystalElement.YELLOW, 1, -1, 5);

		this.addRune(CrystalElement.WHITE, -1, -1, 5);
		this.addRune(CrystalElement.WHITE, 1, -1, -5);
	}

	@Override
	public int getDuration() {
		return 16*super.getDuration();
	}

	@Override
	public int getNumberProduced() {
		return 16;
	}

}
