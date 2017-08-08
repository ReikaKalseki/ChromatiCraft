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
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class LampRecipe extends MultiBlockCastingRecipe {

	public LampRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem("dyeBlack", -2, 0);
		this.addAuxItem("dyeBlack", 2, 0);

		this.addAuxItem(Items.gold_ingot, -4, 0);
		this.addAuxItem(Items.gold_ingot, 4, 0);

		this.addAuxItem(ReikaItemHelper.stoneSlab, -2, 2);
		this.addAuxItem(ReikaItemHelper.stoneSlab, 0, 2);
		this.addAuxItem(ReikaItemHelper.stoneSlab, 2, 2);

		this.addAuxItem(Items.iron_ingot, -2, -2);
		this.addAuxItem(Items.iron_ingot, 0, -2);
		this.addAuxItem(Items.iron_ingot, 2, -2);

		this.addAuxItem(Blocks.redstone_block, 0, -4);
	}

}
