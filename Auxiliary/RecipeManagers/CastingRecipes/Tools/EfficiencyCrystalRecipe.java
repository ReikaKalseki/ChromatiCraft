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


public class EfficiencyCrystalRecipe extends PylonCastingRecipe {

	public EfficiencyCrystalRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addRune(CrystalElement.WHITE, -4, -1, 2);
		this.addRune(CrystalElement.BLACK, 3, -1, 2);
		this.addRune(CrystalElement.BLACK, 0, -1, -3);
		this.addRune(CrystalElement.WHITE, 0, -1, -4);
		this.addRuneRingRune(CrystalElement.YELLOW);
		this.addRuneRingRune(CrystalElement.GRAY);
		this.addRuneRingRune(CrystalElement.PURPLE);

		this.addAuxItem(ChromaStacks.energyPowder, -2, 0);
		this.addAuxItem(ChromaStacks.energyPowder, 2, 0);
		this.addAuxItem(ChromaStacks.energyPowder, 0, 2);
		this.addAuxItem(ChromaStacks.energyPowder, 0, -2);
		this.addAuxItem(ChromaStacks.energyPowder, -4, 2);
		this.addAuxItem(ChromaStacks.energyPowder, -4, -2);
		this.addAuxItem(ChromaStacks.energyPowder, 4, 2);
		this.addAuxItem(ChromaStacks.energyPowder, 4, -2);

		this.addAuxItem(ChromaStacks.rawCrystal, 2, 2);
		this.addAuxItem(ChromaStacks.rawCrystal, 2, -2);
		this.addAuxItem(ChromaStacks.rawCrystal, -2, 2);
		this.addAuxItem(ChromaStacks.rawCrystal, -2, -2);

		this.addAuxItem(ChromaStacks.iridChunk, -4, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 4, 0);

		this.addAuxItem(ChromaStacks.voidDust, 0, 4);
		this.addAuxItem(ChromaStacks.voidDust, 0, -4);

		this.addAuxItem(ChromaStacks.glowbeans, -2, -4);
		this.addAuxItem(ChromaStacks.glowbeans, 2, -4);
		this.addAuxItem(ChromaStacks.glowbeans, -2, 4);
		this.addAuxItem(ChromaStacks.glowbeans, 2, 4);

		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), -4, 4);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), 4, 4);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), -4, -4);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), 4, -4);

		this.addAuraRequirement(CrystalElement.BLACK, 40000);
		this.addAuraRequirement(CrystalElement.WHITE, 60000);
		this.addAuraRequirement(CrystalElement.RED, 20000);
		this.addAuraRequirement(CrystalElement.MAGENTA, 20000);
		this.addAuraRequirement(CrystalElement.BLUE, 10000);
		this.addAuraRequirement(CrystalElement.YELLOW, 24000);
	}

	@Override
	public int getDuration() {
		return super.getDuration()*2;
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0;
	}

}
