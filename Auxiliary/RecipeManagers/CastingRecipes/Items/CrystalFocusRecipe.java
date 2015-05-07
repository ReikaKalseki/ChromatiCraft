/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalFocusRecipe extends MultiBlockCastingRecipe {

	public CrystalFocusRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(this.getChargedShard(CrystalElement.BLUE), -2, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLUE), 2, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 0, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 0, 2);

		this.addAuxItem(this.getChargedShard(CrystalElement.PURPLE), -2, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.PURPLE), -2, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.PURPLE), 2, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.PURPLE), 2, 2);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 32;
	}

}
