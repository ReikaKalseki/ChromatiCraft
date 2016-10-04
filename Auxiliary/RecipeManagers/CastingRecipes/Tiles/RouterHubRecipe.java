/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class RouterHubRecipe extends MultiBlockCastingRecipe {

	public RouterHubRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Items.redstone, 0, -4);
		this.addAuxItem(Blocks.redstone_block, 0, -2);
		this.addAuxItem(Items.iron_ingot, -2, -2);
		this.addAuxItem(Items.iron_ingot, 2, -2);
		this.addAuxItem(Items.diamond, -2, 0);
		this.addAuxItem(Items.diamond, 2, 0);
		this.addAuxItem(Blocks.obsidian, -2, 2);
		this.addAuxItem(Items.gold_ingot, 0, 2);
		this.addAuxItem(Blocks.obsidian, 2, 2);
		this.addAuxItem(Blocks.obsidian, -2, 4);
		this.addAuxItem(ReikaItemHelper.stoneSlab, 0, 4);
		this.addAuxItem(Blocks.obsidian, 2, 4);
		this.addAuxItem(ChromaStacks.enderIngot, -4, 0);
		this.addAuxItem(ChromaStacks.enderIngot, 4, 0);
		this.addAuxItem(ChromaStacks.enderIngot, -4, 4);
		this.addAuxItem(ChromaStacks.enderIngot, 4, 4);
	}

}
