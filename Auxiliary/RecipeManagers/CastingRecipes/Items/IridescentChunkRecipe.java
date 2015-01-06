/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class IridescentChunkRecipe extends MultiBlockCastingRecipe {

	public IridescentChunkRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.iridCrystal, -2, 0);
		this.addAuxItem(ChromaStacks.iridCrystal, 2, 0);
		this.addAuxItem(ChromaStacks.iridCrystal, 0, 2);
		this.addAuxItem(ChromaStacks.iridCrystal, 0, -2);

		this.addAuxItem(ChromaStacks.resonanceDust, -2, -2);
		this.addAuxItem(ChromaStacks.resonanceDust, 2, -2);
		this.addAuxItem(ChromaStacks.focusDust, -2, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 2);
	}

}
