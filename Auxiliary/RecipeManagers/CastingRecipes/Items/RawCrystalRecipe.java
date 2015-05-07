package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;

public class RawCrystalRecipe extends CastingRecipe implements CoreRecipe {

	public RawCrystalRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public int getNumberProduced() {
		return 2;
	}

}
