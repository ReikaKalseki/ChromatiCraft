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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class GuardianStoneRecipe extends PylonCastingRecipe {

	public GuardianStoneRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+CrystalElement.WHITE.ordinal()), -2, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+CrystalElement.WHITE.ordinal()), -2, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+CrystalElement.WHITE.ordinal()), -2, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+CrystalElement.WHITE.ordinal()), 0, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+CrystalElement.WHITE.ordinal()), 2, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+CrystalElement.WHITE.ordinal()), 2, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+CrystalElement.WHITE.ordinal()), 2, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+CrystalElement.WHITE.ordinal()), 0, -2);

		this.addAuraRequirement(CrystalElement.RED, 10000);
		this.addAuraRequirement(CrystalElement.BLUE, 500);
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 4;
	}

}
