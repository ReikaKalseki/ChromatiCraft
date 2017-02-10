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

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class MinerRecipe extends PylonCastingRecipe {

	public MinerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.conductiveIngot, -2, 0);
		this.addAuxItem(ChromaStacks.conductiveIngot, 2, 0);

		this.addAuxItem(ChromaStacks.auraDust, -2, -4);
		this.addAuxItem(ChromaStacks.auraDust, 2, -4);

		this.addAuxItem(ChromaStacks.focusDust, 0, -2);

		this.addAuxItem(ChromaStacks.voidCoreHigh, 0, 2);

		this.addAuxItem(ChromaStacks.beaconDust, -4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 0);

		this.addAuxItem(ChromaStacks.orthocrystal, -4, 2);
		this.addAuxItem(ChromaStacks.orthocrystal, 4, 2);

		this.addAuxItem(ChromaStacks.orthocrystal, -4, -2);
		this.addAuxItem(ChromaStacks.orthocrystal, 4, -2);

		this.addAuxItem(ChromaStacks.echoCrystal, -2, 4);
		this.addAuxItem(ChromaStacks.echoCrystal, 0, 4);
		this.addAuxItem(ChromaStacks.echoCrystal, 2, 4);

		this.addAuxItem(ChromaStacks.resocrystal, 0, -4);

		this.addAuxItem(ChromaStacks.complexIngot, -4, -4);
		this.addAuxItem(ChromaStacks.complexIngot, -4, 4);
		this.addAuxItem(ChromaStacks.complexIngot, 4, -4);
		this.addAuxItem(ChromaStacks.complexIngot, 4, 4);

		this.addAuxItem(ChromaStacks.enderIngot, -2, -2);
		this.addAuxItem(ChromaStacks.enderIngot, -2, 2);
		this.addAuxItem(ChromaStacks.enderIngot, 2, -2);
		this.addAuxItem(ChromaStacks.enderIngot, 2, 2);

		this.addAuraRequirement(CrystalElement.BROWN, 120000);
		this.addAuraRequirement(CrystalElement.PURPLE, 80000);
		this.addAuraRequirement(CrystalElement.YELLOW, 90000);
	}

}
