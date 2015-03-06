package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class OrePickRecipe extends TempleCastingRecipe {

	public OrePickRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRune(CrystalElement.BLACK, -1, -1, -1);
		this.addRune(CrystalElement.WHITE, 1, -1, 1);
		this.addRune(CrystalElement.PURPLE, 1, -1, -1);
		this.addRune(CrystalElement.BROWN, -1, -1, 1);
	}

}
