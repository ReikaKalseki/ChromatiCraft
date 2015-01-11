/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class RuneRecipe extends CastingRecipe {

	private final boolean isBoosted;

	public RuneRecipe(ItemStack out, int meta) {
		super(out, getRecipe(out, meta));
		isBoosted = meta >= 16;
	}

	private static ShapedRecipes getRecipe(ItemStack out, int meta) {
		ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(meta);
		return ReikaRecipeHelper.getShapedRecipeFor(out, "SSS", "SCS", "SSS", 'C', shard, 'S', ChromaBlocks.PYLONSTRUCT.getStackOf());
	}

	@Override
	public int getExperience() {
		return isBoosted ? 4*super.getExperience() : 2*super.getExperience();
	}

	@Override
	protected void getRequiredProgress(Collection<ProgressStage> c) {
		super.getRequiredProgress(c);
		c.add(ProgressStage.ALLCOLORS);
	}

}
