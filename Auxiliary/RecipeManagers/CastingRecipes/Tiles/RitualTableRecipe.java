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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;

public class RitualTableRecipe extends CastingRecipe {

	public RitualTableRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {
		super.onCrafted(te, ep);
		//achievement
	}

}
