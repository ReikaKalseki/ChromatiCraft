package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class DeathFogRecipe extends TempleCastingRecipe {

	public DeathFogRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.MAGENTA, -2, 0, 5);
		this.addRune(CrystalElement.LIGHTGRAY, 4, 0, 2);
		this.addRune(CrystalElement.PINK, 3, 0, -4);
		this.addRune(CrystalElement.GRAY, -4, 0, -2);
	}

}
