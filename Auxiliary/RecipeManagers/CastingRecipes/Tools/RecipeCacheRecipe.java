package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class RecipeCacheRecipe extends MultiBlockCastingRecipe {

	public RecipeCacheRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.auraDust, 0, 2);
		this.addAuxItem(ChromaStacks.auraDust, 0, -2);
		this.addAuxItem(ChromaStacks.auraDust, -2, 0);
		this.addAuxItem(ChromaStacks.auraDust, 2, 0);

		this.addAuxItem(Items.iron_ingot, -2, 2);
		this.addAuxItem(Items.iron_ingot, 2, -2);

		this.addAuxItem(ReikaItemHelper.lapisDye, -4, 4);
		this.addAuxItem(ReikaItemHelper.lapisDye, 4, -4);
		this.addAuxItem(ChromaStacks.spaceDust, -4, 2);
		this.addAuxItem(ChromaStacks.spaceDust, -2, 4);
		this.addAuxItem(ChromaStacks.spaceDust, 2, -4);
		this.addAuxItem(ChromaStacks.spaceDust, 4, -2);

		this.addAuxItem(Items.gold_ingot, -4, -4);
		this.addAuxItem(Items.gold_ingot, 4, 4);
	}

	@Override
	public int getNumberProduced() {
		return 3;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 9;
	}

}
