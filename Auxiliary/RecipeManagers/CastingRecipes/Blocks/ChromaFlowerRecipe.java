package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;

public class ChromaFlowerRecipe extends CastingRecipe {

	public ChromaFlowerRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public float getPenaltyMultiplier() {
		return 8;
	}

	@Override
	public int getPenaltyThreshold(){
		return super.getPenaltyThreshold()*4/5;
	}

}
