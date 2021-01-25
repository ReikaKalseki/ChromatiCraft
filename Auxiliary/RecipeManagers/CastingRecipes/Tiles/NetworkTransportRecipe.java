/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class NetworkTransportRecipe extends PylonCastingRecipe {

	public NetworkTransportRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		for (int i = 0; i >= -4; i -= 2) {
			this.addAuxItem(Blocks.obsidian, -4, i);
			this.addAuxItem(ChromaStacks.auraDust, -2, i);
			if (i != 0)
				this.addAuxItem(Blocks.quartz_block, 0, i);
			this.addAuxItem(ChromaStacks.auraDust, 2, i);
			this.addAuxItem(Blocks.obsidian, 4, i);

			this.addAuxItem(Blocks.obsidian, -4, i);
			this.addAuxItem(ChromaStacks.auraDust, -2, i);
			if (i != 0)
				this.addAuxItem(Blocks.quartz_block, 0, i);
			this.addAuxItem(ChromaStacks.auraDust, 2, i);
			this.addAuxItem(Blocks.obsidian, 4, i);
		}

		for (int i = 4; i >= -4; i -= 2) {
			ItemStack is = Math.abs(i) == 4 ? ChromaStacks.chromaIngot : (Math.abs(i) == 2 ? ChromaStacks.conductiveIngot : new ItemStack(Blocks.glowstone));
			this.addAuxItem(is, i, 2);

			is = i == 0 ? ChromaStacks.auraIngot : new ItemStack(Items.iron_ingot);
			this.addAuxItem(is, i, 4);
		}

		this.addAuraRequirement(CrystalElement.LIME, 24000);
		this.addAuraRequirement(CrystalElement.GRAY, 24000);
	}

	@Override
	public int getNumberProduced() {
		return 2;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

	@Override
	public int getDuration() {
		return super.getDuration()*5/2;
	}

}
