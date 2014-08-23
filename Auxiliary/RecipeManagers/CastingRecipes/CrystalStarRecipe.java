package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.MultiBlockCastingRecipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class CrystalStarRecipe extends MultiBlockCastingRecipe {

	public CrystalStarRecipe(ItemStack main, ItemStack out) {
		super(main, out);
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			this.addAuxItem(ChromaStacks.crystalCore, dir.offsetX*2, dir.offsetZ*2);
		}
	}

}
