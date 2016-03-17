/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class PylonTurboRecipe extends PylonRecipe {

	public PylonTurboRecipe(ItemStack out, ItemStack main, RecipeCrystalRepeater repeater) {
		super(out, main);

		this.addAuxItem(ChromaStacks.iridChunk, 0, -4);
		this.addAuxItem(Blocks.obsidian, 0, -2);

		this.addAuxItem(ChromaStacks.purityDust, 2, 0);
		this.addAuxItem(ChromaStacks.purityDust, -2, 0);

		this.addAuxItem(ChromaStacks.glowbeans, 4, 0);
		this.addAuxItem(ChromaStacks.glowbeans, -4, 0);

		this.addAuxItem(Items.glowstone_dust, 4, 2);
		this.addAuxItem(Items.glowstone_dust, -4, 2);

		this.addAuxItem(ChromaStacks.chargedWhiteShard, 2, -2);
		this.addAuxItem(ChromaStacks.chargedWhiteShard, -2, -2);

		this.addAuxItem(ChromaStacks.focusDust, 2, -4);
		this.addAuxItem(ChromaStacks.focusDust, -2, -4);

		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), -2, 2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), 0, 2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), 2, 2);

		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), 0, 4);

		this.addAuxItem(Items.iron_ingot, -4, 4);
		this.addAuxItem(Items.iron_ingot, 4, 4);

		this.addAuxItem(Items.gold_ingot, -2, 4);
		this.addAuxItem(Items.gold_ingot, 2, 4);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			this.addAuraRequirement(e, 50000);
			this.addRuneRingRune(e);
		}
		this.addAuraRequirement(CrystalElement.PURPLE, 250000);
		this.addAuraRequirement(CrystalElement.BLACK, 150000);
		this.addAuraRequirement(CrystalElement.BLUE, 100000);
		this.addAuraRequirement(CrystalElement.YELLOW, 200000);
		this.addAuraRequirement(CrystalElement.WHITE, 75000);

		this.addRunes(repeater.getRunes());
	}

	@Override
	public int getNumberProduced() {
		return 9;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}

	@Override
	public int getPenaltyThreshold() {
		return this.getTypicalCraftedAmount();
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0;
	}

}
