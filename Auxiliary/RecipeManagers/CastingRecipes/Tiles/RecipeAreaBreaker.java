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
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;


public class RecipeAreaBreaker extends MultiBlockCastingRecipe {

	public RecipeAreaBreaker(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.energyPowder, -2, 0);
		this.addAuxItem(ChromaStacks.energyPowder, 2, 0);

		this.addAuxItem(ChromaStacks.grayShard, 2, 2);
		this.addAuxItem(ChromaStacks.grayShard, -2, 2);
		this.addAuxItem(ChromaStacks.grayShard, 2, -2);
		this.addAuxItem(ChromaStacks.grayShard, -2, -2);

		this.addAuxItem(ChromaStacks.teleDust, 0, -2);
		this.addAuxItem(ChromaStacks.teleDust, 0, 2);
	}

}
