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

public class ItemCollectorRecipe extends PylonCastingRecipe {

	public ItemCollectorRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.auraIngot, -4, -4);
		this.addAuxItem(ChromaStacks.auraIngot, 4, -4);
		this.addAuxItem(ChromaStacks.auraIngot, -4, 4);
		this.addAuxItem(ChromaStacks.auraIngot, 4, 4);

		this.addAuxItem(ChromaStacks.voidDust, 0, -4);
		this.addAuxItem(ChromaStacks.voidDust, 0, 4);
		this.addAuxItem(ChromaStacks.teleDust, 4, 0);
		this.addAuxItem(ChromaStacks.teleDust, -4, 0);

		this.addAuxItem(Blocks.iron_bars, -2, -4);
		this.addAuxItem(Blocks.iron_bars, 2, -4);

		this.addAuxItem(Blocks.iron_bars, -2, 4);
		this.addAuxItem(Blocks.iron_bars, 2, 4);

		this.addAuxItem(Blocks.iron_bars, -4, -2);
		this.addAuxItem(Blocks.iron_bars, -4, 2);

		this.addAuxItem(Blocks.iron_bars, 4, 2);
		this.addAuxItem(Blocks.iron_bars, 4, -2);

		this.addAuxItem(Items.redstone, -2, 0);
		this.addAuxItem(Items.redstone, 2, 0);
		this.addAuxItem(Blocks.hopper, 0, 2);
		this.addAuxItem(Items.slime_ball, 0, -2);

		this.addAuxItem(ChromaStacks.enderIngot, -2, -2);
		this.addAuxItem(ChromaStacks.enderIngot, -2, 2);
		this.addAuxItem(ChromaStacks.enderIngot, 2, 2);
		this.addAuxItem(ChromaStacks.enderIngot, 2, -2);

		this.addAuraRequirement(CrystalElement.BLACK, 1000);
		this.addAuraRequirement(CrystalElement.LIME, 8000);
		this.addAuraRequirement(CrystalElement.BROWN, 2000);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 4;
	}

	@Override
	public int getNumberProduced() {
		return 2;
	}

}
