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

import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;


public class CrystalGlassRecipe extends CastingRecipe {

	public CrystalGlassRecipe(CrystalElement e) {
		super(ChromaBlocks.GLASS.getStackOfMetadata(e.ordinal()), getRecipe(e));
	}

	private static IRecipe getRecipe(CrystalElement e) {
		return ReikaRecipeHelper.getShapedRecipeFor(ChromaBlocks.GLASS.getStackOfMetadata(e.ordinal()), "GSG", "SGS", "GSG", 'G', Blocks.glass, 'S', ChromaItems.SHARD.getStackOf(e));
	}

	@Override
	public int getNumberProduced() {
		return 16;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 128;
	}

}
