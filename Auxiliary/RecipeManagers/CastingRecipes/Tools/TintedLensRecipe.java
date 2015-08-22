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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class TintedLensRecipe extends MultiBlockCastingRecipe {

	public TintedLensRecipe(CrystalElement e, ItemStack main, Item in, int amt) {
		this(e, main, new ItemStack(in), amt);
	}

	public TintedLensRecipe(CrystalElement e, ItemStack main, ItemStack in, int amt) {
		super(ChromaItems.LENS.getCraftedMetadataProduct(amt, e.ordinal()), main);

		this.addAuxItem(in, -2, -2);
		this.addAuxItem(in, -2, 2);
		this.addAuxItem(in, 2, -2);
		this.addAuxItem(in, 2, 2);

		this.addAuxItem(ChromaStacks.focusDust, -2, 0);
		this.addAuxItem(ChromaStacks.focusDust, 2, 0);
		this.addAuxItem(ChromaStacks.getShard(e), 0, -2);
		this.addAuxItem(ChromaStacks.getShard(e), 0, 2);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 64;
	}

}
