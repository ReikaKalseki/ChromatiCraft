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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class CrystalSeedRecipe extends TempleCastingRecipe {

	private final boolean enhanced;

	public CrystalSeedRecipe(ItemStack out, CrystalElement e, boolean en) {
		super(out, ReikaRecipeHelper.getShapedRecipeFor(out, "GSG", "SsS", "GSG", 'G', Items.glowstone_dust, 'S', getUsedShard(en, e), 's', Items.wheat_seeds));

		Coordinate c = runeRing.getNthBlock(e.ordinal());
		this.addRune(e, c.xCoord, c.yCoord, c.zCoord);

		enhanced = en;
	}

	private static ItemStack getUsedShard(boolean enhanced, CrystalElement e) {
		return enhanced ? getChargedShard(e) : getShard(e);
	}

	@Override
	public int getNumberProduced() {
		return enhanced ? 8 : 1;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 4;
	}

}
