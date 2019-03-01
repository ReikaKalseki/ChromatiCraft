package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class NetworkOptimizerRecipe extends PylonCastingRecipe {

	public NetworkOptimizerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.avolite, 2, 0);
		this.addAuxItem(ChromaStacks.avolite, -2, 0);
		this.addAuxItem(ChromaStacks.lumenGem, 0, 2);
		this.addAuxItem(ChromaStacks.lumenGem, 0, -2);

		this.addAuxItem(ChromaStacks.resocrystal, 2, 2);
		this.addAuxItem(ChromaStacks.resocrystal, -2, 2);
		this.addAuxItem(ChromaStacks.resocrystal, 2, -2);
		this.addAuxItem(ChromaStacks.resocrystal, -2, -2);

		ItemStack[] edge = {ChromaStacks.focusDust, ChromaStacks.auraDust, ChromaStacks.spaceDust, ChromaStacks.beaconDust};

		for (int i = 0; i < 4; i++) {
			this.addAuxItem(edge[i], i*2-4, -4);
			this.addAuxItem(edge[i], 4-i*2, 4);
			this.addAuxItem(edge[i], 4, i*2-4);
			this.addAuxItem(edge[i], -4, 4-i*2);
		}

		for (int i = 0; i < 16; i++) {
			this.addAuraRequirement(CrystalElement.elements[i], 1000);
		}

		this.addAuraRequirement(CrystalElement.BLACK, 14000);
		this.addAuraRequirement(CrystalElement.YELLOW, 11000);
		this.addAuraRequirement(CrystalElement.BLUE, 5000);
		this.addAuraRequirement(CrystalElement.PURPLE, 2000);
	}

}
