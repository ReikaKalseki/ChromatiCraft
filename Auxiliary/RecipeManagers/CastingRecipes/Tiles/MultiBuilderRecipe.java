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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;


public class MultiBuilderRecipe extends MultiBlockCastingRecipe {

	public MultiBuilderRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		for (int i = -4; i <= 4; i += 2) {
			this.addAuxItem(Blocks.obsidian, -4, i);
			this.addAuxItem(Blocks.obsidian, 4, i);
			this.addAuxItem(Math.abs(i) == 4 ? new ItemStack(Blocks.redstone_block) : new ItemStack(Items.gold_ingot), -2, i);
			this.addAuxItem(Math.abs(i) == 4 ? new ItemStack(Blocks.redstone_block) : new ItemStack(Items.gold_ingot), 2, i);
		}
		this.addAuxItem(Blocks.quartz_block, 0, -2);
		this.addAuxItem(Items.emerald, 0, 2);
		this.addAuxItem(Items.diamond, 0, -4);
		this.addAuxItem(Items.diamond, 0, 4);
	}

}
