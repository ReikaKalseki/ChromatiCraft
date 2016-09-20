package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;


public class AvoLampRecipe extends MultiBlockCastingRecipe {

	public AvoLampRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Items.iron_ingot, 2, 0);
		this.addAuxItem(Items.iron_ingot, 0, 2);
		this.addAuxItem(Items.iron_ingot, -2, 4);
		this.addAuxItem(Items.iron_ingot, -2, 0);
		this.addAuxItem(Items.iron_ingot, 2, 4);
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

}
