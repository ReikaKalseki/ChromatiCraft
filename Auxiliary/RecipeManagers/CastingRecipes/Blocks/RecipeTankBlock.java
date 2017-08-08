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

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class RecipeTankBlock extends MultiBlockCastingRecipe {

	public RecipeTankBlock(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Blocks.glass, 0, 2);
		this.addAuxItem(Blocks.glass, 0, -2);
		this.addAuxItem(Blocks.glass, 2, 0);
		this.addAuxItem(Blocks.glass, -2, 0);
		this.addAuxItem(Blocks.glass, 2, 2);
		this.addAuxItem(Blocks.glass, -2, 2);
		this.addAuxItem(Blocks.glass, 2, -2);
		this.addAuxItem(Blocks.glass, -2, -2);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 256;
	}

	@Override
	public int getPenaltyThreshold() {
		return super.getPenaltyThreshold()*2/3;
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

}
