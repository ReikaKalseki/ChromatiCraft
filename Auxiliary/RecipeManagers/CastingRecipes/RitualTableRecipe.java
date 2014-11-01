/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;

public class RitualTableRecipe extends CastingRecipe {

	public RitualTableRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {
		//achievement
	}

}
