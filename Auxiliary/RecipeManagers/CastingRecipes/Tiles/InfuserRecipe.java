/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class InfuserRecipe extends MultiBlockCastingRecipe {

	public InfuserRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(new ItemStack(Items.iron_ingot), 2, 2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), -2, 2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 4, 2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), -4, 2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 4, 0);
		this.addAuxItem(new ItemStack(Items.iron_ingot), -4, 0);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 2, -2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), -2, -2);
	}

}
