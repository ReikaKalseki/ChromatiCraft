package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;


public class RelayFilterRecipe extends MultiBlockCastingRecipe {

	public RelayFilterRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.focusDust, -2, 0);
		this.addAuxItem(ChromaStacks.focusDust, 2, 0);
		this.addAuxItem(ChromaStacks.focusDust, 0, 2);
		this.addAuxItem(ChromaStacks.focusDust, 0, -2);

		this.addAuxItem(ChromaStacks.beaconDust, -4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, -4, -2);

		this.addAuxItem(ChromaStacks.beaconDust, 4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -2);

		this.addAuxItem(ChromaStacks.beaconDust, 2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 4);

		this.addAuxItem(ChromaStacks.beaconDust, 2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, -4);

		this.addAuxItem(Items.iron_ingot, -4, -4);
		this.addAuxItem(Items.iron_ingot, 4, -4);
		this.addAuxItem(Items.iron_ingot, -4, 4);
		this.addAuxItem(Items.iron_ingot, 4, 4);

		this.addAuxItem(ChromaStacks.beaconDust, -2, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -2);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 2);

		this.addAuxItem(Blocks.glass, -4, 0);
		this.addAuxItem(Blocks.glass, 4, 0);
		this.addAuxItem(Blocks.glass, 0, 4);
		this.addAuxItem(Blocks.glass, 0, -4);
	}

	@Override
	public int getNumberProduced() {
		return 2;
	}

}
