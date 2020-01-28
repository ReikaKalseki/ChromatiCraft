/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;


public class HoverPadLampRecipe extends CastingRecipe {

	public HoverPadLampRecipe(ChromaBlocks b) {
		super(calcOutput(), ReikaRecipeHelper.getShapedRecipeFor(calcOutput(), getRecipe(b)));
	}

	private static Object[] getRecipe(ChromaBlocks b) {
		return new Object[]{" g ", "grg", " g ", 'r', b.getStackOfMetadata(0), 'g', Items.glowstone_dust};
	}

	private static ItemStack calcOutput() {
		return ChromaBlocks.PAD.getStackOfMetadata(1);
	}

	@Override
	public int getNumberProduced() {
		return 12;
	}

	@Override
	public int getExperience() {
		return 1;
	}

}
