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

import java.util.Collection;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RepeaterRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;

public class RecipeCrystalRepeater extends RepeaterRecipe {

	public RecipeCrystalRepeater(ItemStack main) {
		super(ChromaTiles.REPEATER, main);

		this.addAuxItem(ChromaStacks.beaconDust, -4, -4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 0, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 0, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, -4, -2);

		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), -2, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), 2, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), -2, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), 2, 2);

		//this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), 0, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), 2, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), -2, 0);

		this.addAuxItem(ChromaStacks.lumenGem, 0, -2);
		this.addAuxItem(ChromaStacks.lumenGem, 0, 2);
	}

	@Override
	public int getDuration() {
		return 4*super.getDuration();
	}

	@Override
	public int getNumberProduced() {
		return 16;
	}

	@Override
	public int getExperience() {
		return 2*super.getExperience();
	}

	@Override
	public void getRequiredProgress(Collection<ProgressStage> c) {
		super.getRequiredProgress(c);

		c.addAll(ProgressionManager.instance.getPrereqs(ProgressStage.REPEATER));
	}

}
