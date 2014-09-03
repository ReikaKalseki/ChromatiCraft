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
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.MultiBlockCastingRecipe;

public class CrystalCoreRecipe extends MultiBlockCastingRecipe {

	public CrystalCoreRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.primaryCluster, -2, 0);
		this.addAuxItem(ChromaStacks.primaryCluster, 2, 0);

		this.addAuxItem(ChromaStacks.secondaryCluster, 0, -2);
		this.addAuxItem(ChromaStacks.secondaryCluster, 0, 2);

		//this.addRune(color, rx, ry, rz);
	}

}
