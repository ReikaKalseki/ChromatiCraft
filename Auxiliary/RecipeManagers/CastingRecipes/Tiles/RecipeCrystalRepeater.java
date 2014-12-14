/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class RecipeCrystalRepeater extends MultiBlockCastingRecipe {

	public RecipeCrystalRepeater(ItemStack out, ItemStack main) {
		super(out, main);

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

		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), 0, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), 0, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), 2, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(31), -2, 0);

		this.addRune(CrystalElement.BLACK, 0, -1, 5);
		this.addRune(CrystalElement.BLACK, 0, -1, -5);
		this.addRune(CrystalElement.BLACK, 5, -1, 0);
		this.addRune(CrystalElement.BLACK, -5, -1, 0);

		this.addRune(CrystalElement.WHITE, 5, -1, 5);
		this.addRune(CrystalElement.WHITE, -5, -1, -5);
		this.addRune(CrystalElement.WHITE, 5, -1, -5);
		this.addRune(CrystalElement.WHITE, -5, -1, 5);

		this.addRune(CrystalElement.RED, -5, -1, -4);
		this.addRune(CrystalElement.RED, 5, -1, 4);
		this.addRune(CrystalElement.BLUE, 5, -1, -4);
		this.addRune(CrystalElement.BLUE, -5, -1, 4);

		this.addRune(CrystalElement.GREEN, -4, -1, -5);
		this.addRune(CrystalElement.GREEN, 4, -1, 5);
		this.addRune(CrystalElement.YELLOW, 4, -1, -5);
		this.addRune(CrystalElement.YELLOW, -4, -1, 5);
	}

	@Override
	public int getDuration() {
		return 4*super.getDuration();
	}

	@Override
	public int getNumberProduced() {
		return 16;
	}

}
