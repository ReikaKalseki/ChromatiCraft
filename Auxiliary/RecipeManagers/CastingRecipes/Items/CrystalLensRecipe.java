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
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalLensRecipe extends MultiBlockCastingRecipe {

	public CrystalLensRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.focusDust, -2, -2);
		this.addAuxItem(ChromaStacks.focusDust, -2, 2);
		this.addAuxItem(ChromaStacks.focusDust, 2, -2);
		this.addAuxItem(ChromaStacks.focusDust, 2, 2);

		this.addAuxItem(this.getShard(CrystalElement.WHITE), -2, 0);
		this.addAuxItem(this.getShard(CrystalElement.WHITE), 2, 0);
		this.addAuxItem(this.getShard(CrystalElement.BLUE), 0, -2);
		this.addAuxItem(this.getShard(CrystalElement.BLUE), 0, 2);
	}

}
