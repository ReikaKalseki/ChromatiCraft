/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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

public class CaptureWandRecipe extends MultiBlockCastingRecipe {

	public CaptureWandRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		//for now
		this.addAuxItem(new ItemStack(Items.stick), -2, -2);
		this.addAuxItem(new ItemStack(Items.stick), 2, 2);
		this.addAuxItem(new ItemStack(Items.stick), -2, 2);
		this.addAuxItem(new ItemStack(Items.stick), 2, -2);

		this.addAuxItem(new ItemStack(Items.iron_ingot), -2, 0);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 2, 0);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 0, 2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 0, -2);

		this.addAuxItem(ChromaStacks.bindingCrystal, -4, 0);
		this.addAuxItem(ChromaStacks.bindingCrystal, 4, 0);
		this.addAuxItem(ChromaStacks.bindingCrystal, 0, 4);
		this.addAuxItem(ChromaStacks.bindingCrystal, 0, -4);
	}

}
