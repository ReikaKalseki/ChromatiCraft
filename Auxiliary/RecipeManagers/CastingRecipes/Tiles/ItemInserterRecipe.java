package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class ItemInserterRecipe extends TempleCastingRecipe {

	public ItemInserterRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.LIGHTGRAY, -3, 0, 4);
		this.addRune(CrystalElement.LIME, 1, 0, -3);
	}

}
