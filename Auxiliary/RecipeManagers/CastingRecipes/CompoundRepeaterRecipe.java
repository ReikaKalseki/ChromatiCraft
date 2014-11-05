package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class CompoundRepeaterRecipe extends MultiBlockCastingRecipe {

	public CompoundRepeaterRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.beaconDust, -2, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -2);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 2);

		this.addAuxItem(ChromaStacks.beaconDust, 0, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 0, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 0);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 0);

		this.addAuxItem(ChromaStacks.auraDust, -4, -4);
		this.addAuxItem(ChromaStacks.focusDust, -2, -4);
		this.addAuxItem(ChromaStacks.auraDust, 0, -4);
		this.addAuxItem(ChromaStacks.focusDust, 2, -4);
		this.addAuxItem(ChromaStacks.auraDust, 4, -4);
		this.addAuxItem(ChromaStacks.focusDust, 4, -2);
		this.addAuxItem(ChromaStacks.auraDust, 4, 0);
		this.addAuxItem(ChromaStacks.focusDust, 4, 2);
		this.addAuxItem(ChromaStacks.auraDust, 4, 4);
		this.addAuxItem(ChromaStacks.focusDust, 2, 4);
		this.addAuxItem(ChromaStacks.auraDust, 0, 4);
		this.addAuxItem(ChromaStacks.focusDust, -2, 4);
		this.addAuxItem(ChromaStacks.auraDust, -4, 4);
		this.addAuxItem(ChromaStacks.focusDust, -4, 2);
		this.addAuxItem(ChromaStacks.auraDust, -4, 0);
		this.addAuxItem(ChromaStacks.focusDust, -4, -2);
	}

}
