package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AuraCleanerRecipe extends TempleCastingRecipe {

	public AuraCleanerRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.BLACK, -3, -1, 3);
		this.addRune(CrystalElement.BLACK, 3, -1, -3);

		this.addRune(CrystalElement.WHITE, 0, -1, -2);
		this.addRune(CrystalElement.WHITE, 0, -1, 2);
	}

}
