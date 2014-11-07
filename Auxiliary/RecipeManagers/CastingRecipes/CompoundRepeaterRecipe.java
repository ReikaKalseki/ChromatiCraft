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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaTiles;

public class CompoundRepeaterRecipe extends MultiBlockCastingRecipe {

	public CompoundRepeaterRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.beaconDust, -4, -4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 0, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 0, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, -4, -2);

		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 0, -2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 0, 2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 2, 0);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), -2, 0);

		this.addAuxItem(ChromaStacks.focusDust, 2, -2);
		this.addAuxItem(ChromaStacks.focusDust, 2, 2);
		this.addAuxItem(ChromaStacks.focusDust, -2, -2);
		this.addAuxItem(ChromaStacks.focusDust, -2, 2);
	}

	@Override
	public int getDuration() {
		return 8*super.getDuration();
	}

}
