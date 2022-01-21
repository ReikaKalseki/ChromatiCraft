/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ShardGroupingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class LumenCoreRecipe extends PylonCastingRecipe implements ShardGroupingRecipe {

	public LumenCoreRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		for (int i = -4; i <= 4; i += 2) {
			for (int k = -4; k <= 4; k += 2) {
				if (i != 0 || k != 0)
					this.addAuxItem(ChromaStacks.glowChunk, i, k);
			}
		}

		this.addAuxItem(ChromaStacks.purityDust, 0, -2);
		this.addAuxItem(ChromaStacks.purityDust, 0, 2);
		this.addAuxItem(ChromaStacks.purityDust, -2, 0);
		this.addAuxItem(ChromaStacks.purityDust, 2, 0);

		this.addAuraRequirement(CrystalElement.BLACK, 60000);
		this.addAuraRequirement(CrystalElement.YELLOW, 60000);
		this.addAuraRequirement(CrystalElement.BLUE, 60000);
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

}
