/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class TintedLensRecipe extends MultiBlockCastingRecipe {

	public TintedLensRecipe(CrystalElement e, ItemStack main) {
		super(ChromaItems.LENS.getStackOf(e), main);

		this.addAuxItem(ChromaStacks.magicIngot, -2, -2);
		this.addAuxItem(ChromaStacks.magicIngot, -2, 2);
		this.addAuxItem(ChromaStacks.magicIngot, 2, -2);
		this.addAuxItem(ChromaStacks.magicIngot, 2, 2);

		this.addAuxItem(ChromaStacks.focusDust, -2, 0);
		this.addAuxItem(ChromaStacks.focusDust, 2, 0);
		this.addAuxItem(ChromaStacks.getShard(e), 0, -2);
		this.addAuxItem(ChromaStacks.getShard(e), 0, 2);
	}

}
