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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class StorageCrystalRecipe extends MultiBlockCastingRecipe {

	public StorageCrystalRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(0), -4, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(1), -2, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(2), 0, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(3), 2, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(4), 4, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(5), 4, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(6), 4, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(7), 4, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(8), 4, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(9), 2, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(10), 0, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(11), -2, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(12), -4, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(13), -4, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(14), -4, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(15), -4, -2);

		this.addRune(CrystalElement.BLUE, 4, 0, 1);
		this.addRune(CrystalElement.BLUE, 4, 0, -1);
		this.addRune(CrystalElement.GREEN, -4, 0, 1);
		this.addRune(CrystalElement.GREEN, -4, 0, -1);
		this.addRune(CrystalElement.RED, 1, 0, -4);
		this.addRune(CrystalElement.RED, -1, 0, -4);
		this.addRune(CrystalElement.YELLOW, 1, 0, 4);
		this.addRune(CrystalElement.YELLOW, -1, 0, 4);
		this.addRune(CrystalElement.WHITE, 4, 0, -4);
		this.addRune(CrystalElement.WHITE, -4, 0, 4);
		this.addRune(CrystalElement.BLACK, -4, 0, -4);
		this.addRune(CrystalElement.BLACK, 4, 0, 4);
	}

	@Override
	public int getDuration() {
		return 800;
	}

}
