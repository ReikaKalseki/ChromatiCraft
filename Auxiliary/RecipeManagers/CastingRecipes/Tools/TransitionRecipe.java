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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class TransitionRecipe extends MultiBlockCastingRecipe {

	public TransitionRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		//for now
		this.addAuxItem(Items.stick, -2, -2);
		this.addAuxItem(Items.stick, 2, 2);
		this.addAuxItem(Items.stick, -2, 2);
		this.addAuxItem(Items.stick, 2, -2);

		this.addAuxItem(ChromaStacks.magicIngot, -2, 0);
		this.addAuxItem(ChromaStacks.magicIngot, 2, 0);
		this.addAuxItem(ChromaStacks.magicIngot, 0, 2);
		this.addAuxItem(ChromaStacks.magicIngot, 0, -2);

		this.addAuxItem(ChromaStacks.auraDust, -4, 0);
		this.addAuxItem(ChromaStacks.auraDust, 4, 0);
		this.addAuxItem(ChromaStacks.auraDust, 0, 4);
		this.addAuxItem(ChromaStacks.auraDust, 0, -4);
	}

}
