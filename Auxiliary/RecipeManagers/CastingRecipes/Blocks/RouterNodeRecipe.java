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
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class RouterNodeRecipe extends CastingRecipe {

	public RouterNodeRecipe(ItemStack out, boolean in) {
		super(out, getRecipe(out, in));
	}

	private static IRecipe getRecipe(ItemStack out, boolean in) {
		return ReikaRecipeHelper.getShapedRecipeFor(out, " R ", "RIR", "AAA", 'R', in ? ReikaItemHelper.lapisDye : Items.redstone, 'I', Items.iron_ingot, 'A', in ? ChromaStacks.auraDust : ChromaStacks.beaconDust);
	}

	@Override
	public int getNumberProduced() {
		return 2;
	}

	@Override
	public boolean crafts(ItemStack is) {
		return super.crafts(this.stripUncaredTags(is));
	}

	private ItemStack stripUncaredTags(ItemStack is) {
		if (is.stackTagCompound == null)
			return is;
		is = is.copy();
		is.stackTagCompound.removeTag("target");
		return is;
	}

}
