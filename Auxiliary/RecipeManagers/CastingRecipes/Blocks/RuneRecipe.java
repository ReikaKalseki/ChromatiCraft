/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class RuneRecipe extends CastingRecipe {

	public RuneRecipe(ItemStack out, int meta) {
		super(out, getRecipe(out, meta));
	}

	private static ShapedRecipes getRecipe(ItemStack out, int meta) {
		ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(meta);
		return ReikaRecipeHelper.getShapedRecipeFor(out, "SSS", "SCS", "SSS", 'C', shard, 'S', ChromaBlocks.PYLONSTRUCT.getStackOf());
	}

}
