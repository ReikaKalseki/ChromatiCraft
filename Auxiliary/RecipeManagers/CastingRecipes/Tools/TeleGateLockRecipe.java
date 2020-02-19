/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class TeleGateLockRecipe extends PylonCastingRecipe {

	public TeleGateLockRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.enderDust, -4, 2);
		this.addAuxItem(ChromaStacks.enderDust, -2, 4);
		this.addAuxItem(ChromaStacks.chargedLimeShard, -4, 4);

		this.addAuxItem(ChromaStacks.teleDust, 4, -2);
		this.addAuxItem(ChromaStacks.teleDust, 2, -4);
		this.addAuxItem(ChromaStacks.chargedLightBlueShard, 4, -4);

		this.addAuxItem(ChromaStacks.chromaIngot, 2, -2);
		this.addAuxItem(ChromaStacks.chromaIngot, -2, 2);

		this.addAuxItem(ChromaStacks.spaceDust, -2, 0);
		this.addAuxItem(ChromaStacks.spaceDust, 2, 0);
		this.addAuxItem(ChromaStacks.spaceDust, 0, 2);
		this.addAuxItem(ChromaStacks.spaceDust, 0, -2);

		this.addAuraRequirement(CrystalElement.LIME, 6000);
		this.addAuraRequirement(CrystalElement.BLACK, 2000);
		this.addAuraRequirement(CrystalElement.LIGHTBLUE, 3000);
	}

	@Override
	public int getNumberProduced() {
		return 16;
	}

}
