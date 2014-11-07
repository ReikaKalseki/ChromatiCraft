package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class IridescentChunkRecipe extends MultiBlockCastingRecipe {

	public IridescentChunkRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.iridCrystal, -2, 0);
		this.addAuxItem(ChromaStacks.iridCrystal, 2, 0);
		this.addAuxItem(ChromaStacks.iridCrystal, 0, 2);
		this.addAuxItem(ChromaStacks.iridCrystal, 0, -2);

		this.addAuxItem(ChromaStacks.elementDust, -2, -2);
		this.addAuxItem(ChromaStacks.elementDust, 2, -2);
		this.addAuxItem(ChromaStacks.elementDust, -2, 2);
		this.addAuxItem(ChromaStacks.elementDust, 2, 2);
	}

}
