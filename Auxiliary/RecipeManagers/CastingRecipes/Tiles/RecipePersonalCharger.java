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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class RecipePersonalCharger extends PylonCastingRecipe {

	public RecipePersonalCharger(ItemStack out, ItemStack main) {
		super(out, main);

		for (CrystalElement e : CrystalElement.elements)
			this.addRuneRingRune(e);

		this.addAuxItem(ChromaStacks.spaceDust, 2, 0);
		this.addAuxItem(ChromaStacks.spaceDust, -2, 0);
		this.addAuxItem(ChromaStacks.spaceDust, 0, 2);
		this.addAuxItem(ChromaStacks.spaceDust, 0, -2);

		this.addAuxItem(ChromaStacks.beaconDust, 2, 2);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -2);
		this.addAuxItem(ChromaStacks.beaconDust, -2, -2);

		for (int i = -2; i <= 2; i += 2) {
			this.addAuxItem(Items.glowstone_dust, -4, i);
			this.addAuxItem(Items.glowstone_dust, 4, i);
			this.addAuxItem(Items.glowstone_dust, i, 4);
			this.addAuxItem(Items.glowstone_dust, i, -4);
		}

		this.addAuxItem(Items.diamond, -4, -4);
		this.addAuxItem(Items.diamond, 4, -4);
		this.addAuxItem(Items.diamond, -4, 4);
		this.addAuxItem(Items.diamond, 4, 4);

		this.addAuraRequirement(CrystalElement.BLACK, 25000);
		this.addAuraRequirement(CrystalElement.PURPLE, 5000);
		this.addAuraRequirement(CrystalElement.WHITE, 10000);
		this.addAuraRequirement(CrystalElement.YELLOW, 16000);
	}

	@Override
	public int getDuration() {
		return super.getDuration()*12;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}

	@Override
	public int getPenaltyThreshold() {
		return 16;
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0;
	}

	@Override
	public int getExperience() {
		return 4*super.getExperience();
	}

}
