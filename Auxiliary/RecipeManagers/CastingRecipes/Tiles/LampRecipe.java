/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class LampRecipe extends MultiBlockCastingRecipe {

	public LampRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ReikaItemHelper.inksac, -2, 0);
		this.addAuxItem(ReikaItemHelper.inksac, 2, 0);

		this.addAuxItem(new ItemStack(Items.gold_ingot), -4, 0);
		this.addAuxItem(new ItemStack(Items.gold_ingot), 4, 0);

		this.addAuxItem(ReikaItemHelper.stoneSlab, -2, 2);
		this.addAuxItem(ReikaItemHelper.stoneSlab, 0, 2);
		this.addAuxItem(ReikaItemHelper.stoneSlab, 2, 2);

		this.addAuxItem(new ItemStack(Items.iron_ingot), -2, -2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 0, -2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 2, -2);

		this.addAuxItem(new ItemStack(Blocks.redstone_block), 0, -4);
	}

}
