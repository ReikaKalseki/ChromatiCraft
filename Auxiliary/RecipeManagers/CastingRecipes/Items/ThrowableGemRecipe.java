/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;


public class ThrowableGemRecipe extends TempleCastingRecipe {

	public ThrowableGemRecipe(CrystalElement e) {
		super(getOutput(e), getRecipe(e));

		this.addRuneRingRune(e);
	}

	private static IRecipe getRecipe(CrystalElement e) {
		return ReikaRecipeHelper.getShapedRecipeFor(getOutput(e), "RRR", "RER", "RRR", 'R', Items.redstone, 'E', ChromaItems.ELEMENTAL.getStackOf(e));
	}

	private static ItemStack getOutput(CrystalElement e) {
		return ChromaItems.THROWGEM.getCraftedMetadataProduct(8, e.ordinal());
	}

}
