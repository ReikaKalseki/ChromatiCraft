/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class CrystalSeedRecipe extends TempleCastingRecipe {

	public CrystalSeedRecipe(ItemStack out, CrystalElement e) {
		super(out, ReikaRecipeHelper.getShapedRecipeFor(out, "GSG", "SsS", "GSG", 'G', Items.glowstone_dust, 'S', getShard(e), 's', Items.wheat_seeds));

		int[] xyz = runeRing.getNthBlock(e.ordinal());
		this.addRune(e, xyz[0], xyz[1], xyz[2]);
	}

}
