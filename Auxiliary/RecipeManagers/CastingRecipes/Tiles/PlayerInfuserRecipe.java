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

public class PlayerInfuserRecipe extends MultiBlockCastingRecipe {

	public PlayerInfuserRecipe(ItemStack out, InfuserRecipe ir) {
		super(out, ir.getOutput());

		this.addAuxItem(ChromaStacks.auraIngot, 2, 0);
		this.addAuxItem(ChromaStacks.auraIngot, -2, 0);
		this.addAuxItem(ChromaStacks.auraIngot, 4, -2);
		this.addAuxItem(ChromaStacks.auraIngot, -4, -2);

		this.addAuxItem(Items.diamond, 2, 2);
		this.addAuxItem(Items.diamond, -2, 2);

		this.addAuxItem(Items.diamond, -4, 2);
		this.addAuxItem(Items.diamond, 4, 2);

		this.addAuxItem(ChromaStacks.resocrystal, 2, -2);
		this.addAuxItem(ChromaStacks.resocrystal, 0, -2);
		this.addAuxItem(ChromaStacks.resocrystal, -2, -2);

		this.addAuxItem(ChromaStacks.complexIngot, 0, 2);

		this.addRunes(ir.getRunes());
	}

}
