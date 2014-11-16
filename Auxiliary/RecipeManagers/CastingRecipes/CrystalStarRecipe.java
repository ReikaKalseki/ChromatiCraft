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
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;

public class CrystalStarRecipe extends MultiBlockCastingRecipe {

	public CrystalStarRecipe(ItemStack out, ItemStack main) {
		super(out, main);
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			this.addAuxItem(ChromaStacks.crystalCore, dir.offsetX*2, dir.offsetZ*2);
		}

		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+0), -4, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+1), -2, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+2), 0, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+3), 2, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+4), 4, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+5), 4, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+6), 4, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+7), 4, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+8), 4, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+9), 2, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+10), 0, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+11), -2, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+12), -4, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+13), -4, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+14), -4, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+15), -4, -2);
	}

	@Override
	public int getDuration() {
		return 4*super.getDuration();
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

}
