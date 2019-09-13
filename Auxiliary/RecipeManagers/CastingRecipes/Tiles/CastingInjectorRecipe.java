package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;


public class CastingInjectorRecipe extends MultiBlockCastingRecipe {

	public CastingInjectorRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		for (int i = -4; i <= 4; i += 2) {
			for (int k = -4; k <= 4; k += 2) {
				if (i != 0 || k != 0) {
					this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(0), i, k);
				}
			}
		}

		for (int i = -2; i <= 2; i += 2) {
			for (int k = -2; k <= 2; k += 2) {
				if (i != 0 || k != 0) {
					this.addAuxItem(ChromaStacks.elementDust, i, k);
				}
			}
		}

		this.addAuxItem(ChromaStacks.crystalLens, -2, -4);
		this.addAuxItem(ChromaStacks.crystalLens, 2, -4);
		this.addAuxItem(ChromaStacks.crystalLens, 0, -4);
		this.addAuxItem(ChromaStacks.crystalLens, 0, -2);

		for (int i = -2; i <= 2; i += 2) {
			this.addAuxItem(ChromaStacks.iridCrystal, -4, i);
			this.addAuxItem(ChromaStacks.iridCrystal, 4, i);

			this.addAuxItem(ChromaStacks.spaceIngot, -2, i);
			this.addAuxItem(ChromaStacks.spaceIngot, 2, i);
		}

		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.STABILIZER.ordinal()), -2, -2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.STABILIZER.ordinal()), 2, -2);


		this.addAuxItem(ChromaStacks.transformCore, 0, 2);
	}

}
