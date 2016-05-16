package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;


public class RecipeCaveTrail extends CastingRecipe {

	public RecipeCaveTrail(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public int getNumberProduced() {
		return 8;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}

	@Override
	public int getExperience() {
		return super.getExperience()/2;
	}

}
