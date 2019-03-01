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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class WirelessTransmitterRecipe extends MultiBlockCastingRecipe {

	public WirelessTransmitterRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addRuneRingRune(CrystalElement.BLACK);
		this.addRuneRingRune(CrystalElement.YELLOW);
		this.addRuneRingRune(CrystalElement.BLUE);
		this.addRuneRingRune(CrystalElement.LIME);

		for (int i = -4; i <= 4; i += 2) {
			for (int k = -4; k <= 4; k += 2) {
				if (i != 0 || k != 0)
					this.addAuxItem(Items.iron_ingot, i, k);
			}
		}

		this.addAuxItem(ChromaStacks.conductiveIngot, -2, -4);
		this.addAuxItem(ChromaStacks.conductiveIngot, 2, -4);

		for (int i = -4; i <= 4; i += 2) {
			if (i != 0) {
				this.addAuxItem(ChromaStacks.resocrystal, i, i);
				this.addAuxItem(ChromaStacks.resocrystal, -i, i);
			}
		}

		this.addAuxItem(ChromaStacks.energyPowder, -2, 0);
		this.addAuxItem(ChromaStacks.energyPowder, 2, 0);
		this.addAuxItem(ChromaStacks.energyPowder, 0, 2);
		this.addAuxItem(ChromaStacks.energyPowder, 0, -2);

		this.addAuxItem(ChromaStacks.auraDust, 0, 4);
		this.addAuxItem(ChromaStacks.auraDust, 0, -4);
		this.addAuxItem(ChromaStacks.auraDust, -4, 0);
		this.addAuxItem(ChromaStacks.auraDust, 4, 0);
	}

}
