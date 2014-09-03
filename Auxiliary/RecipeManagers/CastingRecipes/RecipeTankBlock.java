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

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.MultiBlockCastingRecipe;

public class RecipeTankBlock extends MultiBlockCastingRecipe {

	public RecipeTankBlock(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(new ItemStack(Blocks.glass), 0, 2);
		this.addAuxItem(new ItemStack(Blocks.glass), 0, -2);
		this.addAuxItem(new ItemStack(Blocks.glass), 2, 0);
		this.addAuxItem(new ItemStack(Blocks.glass), -2, 0);
		this.addAuxItem(new ItemStack(Blocks.glass), 2, 2);
		this.addAuxItem(new ItemStack(Blocks.glass), -2, 2);
		this.addAuxItem(new ItemStack(Blocks.glass), 2, -2);
		this.addAuxItem(new ItemStack(Blocks.glass), -2, -2);
	}

}
