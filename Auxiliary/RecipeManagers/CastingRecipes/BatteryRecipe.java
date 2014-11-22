/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class BatteryRecipe extends PylonRecipe {

	public BatteryRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), -2, -2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 0, -2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 2, -2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 2, 0);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 2, 2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 0, 2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), -2, 2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), -2, 0);

		this.addAuxItem(ChromaStacks.chromaDust, -4, 0);
		this.addAuxItem(ChromaStacks.chromaDust, 4, 0);
		this.addAuxItem(ChromaStacks.chromaDust, 0, 4);
		this.addAuxItem(ChromaStacks.chromaDust, 0, -4);

		this.addAuxItem(ChromaStacks.beaconDust, -4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, -4, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -2);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -4);

		this.addAuxItem(ChromaStacks.focusDust, -4, 4);
		this.addAuxItem(ChromaStacks.focusDust, -4, -4);
		this.addAuxItem(ChromaStacks.focusDust, 4, -4);
		this.addAuxItem(ChromaStacks.focusDust, 4, 4);

		this.addAuraRequirement(CrystalElement.BLACK, 30000);
		this.addAuraRequirement(CrystalElement.YELLOW, 30000);
		this.addAuraRequirement(CrystalElement.BLUE, 10000);
		this.addAuraRequirement(CrystalElement.PURPLE, 2000);
	}

}
