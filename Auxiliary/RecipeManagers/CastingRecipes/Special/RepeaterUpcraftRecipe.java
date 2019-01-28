/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RepeaterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RecipeCrystalRepeater;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;

public class RepeaterUpcraftRecipe extends RepeaterRecipe {

	public RepeaterUpcraftRecipe(RecipeCrystalRepeater r) {
		super(ChromaTiles.REPEATER, ChromaStacks.elementUnit);

		this.addAuxItems(r);
		this.addAuxItem(ChromaStacks.crystalPowder, -2, -2);
		this.addAuxItem(ChromaStacks.crystalPowder, 2, -2);
		this.addAuxItem(ChromaStacks.crystalPowder, -2, 2);
		this.addAuxItem(ChromaStacks.crystalPowder, 2, 2);

		this.addAuxItem(ChromaTiles.WEAKREPEATER.getCraftedProduct(), -4, -4);
		this.addAuxItem(ChromaTiles.WEAKREPEATER.getCraftedProduct(), 4, -4);
		this.addAuxItem(ChromaTiles.WEAKREPEATER.getCraftedProduct(), -4, 4);
		this.addAuxItem(ChromaTiles.WEAKREPEATER.getCraftedProduct(), 4, 4);
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

	@Override
	public boolean canBeStacked() {
		return true;
	}

	@Override
	public float getConsecutiveStackingTimeFactor(TileEntityCastingTable te) {
		return 0.75F;
	}

	@Override
	public int getExperience() {
		return 0;
	}

	@Override
	public int getDuration() {
		return super.getDuration()*3;
	}

}
