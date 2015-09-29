package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;


public class FenceAuxRecipe extends CastingRecipe {

	public FenceAuxRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

}
