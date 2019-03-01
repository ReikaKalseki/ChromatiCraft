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
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class LumenLampRecipe extends TempleCastingRecipe {

	public LumenLampRecipe(ItemStack out, CrystalElement e) {
		super(out, getRecipe(out, e));

		this.addRune(CrystalElement.BLACK, 0, -1, -3);
		this.addRune(CrystalElement.BLUE, 0, -1, 3);
	}

	private static IRecipe getRecipe(ItemStack out, CrystalElement e) {
		return ReikaRecipeHelper.getShapedRecipeFor(out, "GSG", "SLS", "GSG", 'G', Blocks.glass, 'L', Blocks.redstone_lamp, 'S', getShard(e));
	}

	@Override
	public int getNumberProduced() {
		return 16;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 4;
	}

}
