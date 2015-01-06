/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class CrystalClusterRecipe extends TempleCastingRecipe {

	public CrystalClusterRecipe(ItemStack out) {
		super(out, getRecipe(out));

		//this.addRune(color, rx, ry, rz);
	}

	private static IRecipe getRecipe(ItemStack out) {
		ItemStack is1 = ChromaItems.CLUSTER.getStackOfMetadata(2*out.getItemDamage()%8);
		ItemStack is2 = ChromaItems.CLUSTER.getStackOfMetadata(1+2*out.getItemDamage()%8);

		ItemStack ctr = getShard(CrystalElement.WHITE);

		Object[] o = {" A ", "BFB", " A ", 'A', is1, 'B', is2, 'F', ctr};
		return ReikaRecipeHelper.getShapedRecipeFor(out, o);
	}

}
