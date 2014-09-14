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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class GuardianStoneRecipe extends PylonRecipe {

	public GuardianStoneRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(CrystalElement.WHITE.ordinal()), -2, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(CrystalElement.WHITE.ordinal()), -2, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(CrystalElement.WHITE.ordinal()), -2, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(CrystalElement.WHITE.ordinal()), 0, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(CrystalElement.WHITE.ordinal()), 2, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(CrystalElement.WHITE.ordinal()), 2, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(CrystalElement.WHITE.ordinal()), 2, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(CrystalElement.WHITE.ordinal()), 0, -2);

		this.addAuraRequirement(CrystalElement.RED, 10000);
		this.addAuraRequirement(CrystalElement.BLUE, 500);
	}

}
