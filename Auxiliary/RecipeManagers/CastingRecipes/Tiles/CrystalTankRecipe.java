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

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalTankRecipe extends PylonCastingRecipe {

	public CrystalTankRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuraRequirement(CrystalElement.BLACK, 6000);
		this.addAuraRequirement(CrystalElement.CYAN, 9000);

		this.addAuxItem(this.getChargedShard(CrystalElement.CYAN), 2, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.CYAN), -2, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.CYAN), 2, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.CYAN), -2, -2);

		this.addAuxItem(ChromaStacks.waterDust, 0, -2);
		this.addAuxItem(ChromaStacks.waterDust, -2, 0);
		this.addAuxItem(ChromaStacks.waterDust, 0, 2);
		this.addAuxItem(ChromaStacks.waterDust, 2, 0);

		this.addAuxItem(Blocks.glass, -2, -4);
		this.addAuxItem(Blocks.glass, 2, -4);
		this.addAuxItem(Blocks.glass, 4, -2);
		this.addAuxItem(Blocks.glass, 4, 2);
		this.addAuxItem(Blocks.glass, 2, 4);
		this.addAuxItem(Blocks.glass, -2, 4);
		this.addAuxItem(Blocks.glass, -4, 2);
		this.addAuxItem(Blocks.glass, -4, -2);

		this.addAuxItem(Blocks.obsidian, 0, 4);
		this.addAuxItem(Blocks.obsidian, -4, 0);
		this.addAuxItem(Blocks.obsidian, 0, -4);
		this.addAuxItem(Blocks.obsidian, 4, 0);

		this.addAuxItem(ChromaStacks.waterIngot, -4, -4);
		this.addAuxItem(ChromaStacks.waterIngot, -4, 4);
		this.addAuxItem(ChromaStacks.waterIngot, 4, 4);
		this.addAuxItem(ChromaStacks.waterIngot, 4, -4);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}
}
