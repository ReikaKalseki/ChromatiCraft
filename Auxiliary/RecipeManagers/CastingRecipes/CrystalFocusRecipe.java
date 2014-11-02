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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalFocusRecipe extends MultiBlockCastingRecipe {

	public CrystalFocusRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(this.getChargedShard(CrystalElement.BLUE), -2, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLUE), 2, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 0, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 0, -2);
	}

}
