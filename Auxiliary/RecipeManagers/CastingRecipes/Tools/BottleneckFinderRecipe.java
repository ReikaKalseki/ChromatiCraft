/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;


public class BottleneckFinderRecipe extends MultiBlockCastingRecipe {

	public BottleneckFinderRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.rawCrystal, -2, 4);
		this.addAuxItem(ChromaStacks.rawCrystal, 2, -4);
		this.addAuxItem(ChromaStacks.rawCrystal, 4, 2);
		this.addAuxItem(ChromaStacks.rawCrystal, -4, -2);
		this.addAuxItem(ChromaStacks.rawCrystal, -2, 2);
		this.addAuxItem(ChromaStacks.rawCrystal, 2, -2);
		this.addAuxItem(ChromaStacks.rawCrystal, 2, 2);
		this.addAuxItem(ChromaStacks.rawCrystal, -2, -2);

		this.addAuxItem(ChromaStacks.lumenGem, -2, -4);
		this.addAuxItem(ChromaStacks.lumenGem, 4, -2);
		this.addAuxItem(ChromaStacks.lumenGem, -4, 2);
		this.addAuxItem(ChromaStacks.lumenGem, 2, 4);
		this.addAuxItem(ChromaStacks.lumenGem, -4, -4);
		this.addAuxItem(ChromaStacks.lumenGem, 4, -4);
		this.addAuxItem(ChromaStacks.lumenGem, -4, 4);
		this.addAuxItem(ChromaStacks.lumenGem, 4, 4);
		this.addAuxItem(ChromaStacks.lumenGem, -4, 0);
		this.addAuxItem(ChromaStacks.lumenGem, 4, 0);
		this.addAuxItem(ChromaStacks.lumenGem, 0, 4);
		this.addAuxItem(ChromaStacks.lumenGem, 0, -4);

		this.addAuxItem(ChromaStacks.bindingCrystal, -2, 0);
		this.addAuxItem(ChromaStacks.bindingCrystal, 2, 0);
		this.addAuxItem(ChromaStacks.bindingCrystal, 0, 2);
		this.addAuxItem(ChromaStacks.bindingCrystal, 0, -2);
	}

}
