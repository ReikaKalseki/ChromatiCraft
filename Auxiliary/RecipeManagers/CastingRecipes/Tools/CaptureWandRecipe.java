/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class CaptureWandRecipe extends MultiBlockCastingRecipe {

	public CaptureWandRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		//for now
		this.addAuxItem("stickWood", -2, -2);
		this.addAuxItem("stickWood", 2, 2);
		this.addAuxItem("stickWood", -2, 2);
		this.addAuxItem("stickWood", 2, -2);

		this.addAuxItem(ChromaStacks.chromaIngot, -2, 0);
		this.addAuxItem(ChromaStacks.chromaIngot, 2, 0);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, 2);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, -2);

		this.addAuxItem(ChromaStacks.bindingCrystal, -4, 0);
		this.addAuxItem(ChromaStacks.bindingCrystal, 4, 0);
		this.addAuxItem(ChromaStacks.bindingCrystal, 0, 4);
		this.addAuxItem(ChromaStacks.bindingCrystal, 0, -4);
	}

}
