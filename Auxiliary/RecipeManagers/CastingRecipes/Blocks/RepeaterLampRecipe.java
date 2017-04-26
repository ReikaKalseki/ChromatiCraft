package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Block.Decoration.BlockRepeaterLight;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;


public class RepeaterLampRecipe extends CastingRecipe {

	public RepeaterLampRecipe(ChromaTiles c) {
		super(calcOutput(c), ReikaRecipeHelper.getShapelessRecipeFor(calcOutput(c), c.getCraftedProduct()));
	}

	private static ItemStack calcOutput(ChromaTiles c) {
		return ChromaBlocks.REPEATERLAMP.getStackOfMetadata(BlockRepeaterLight.getMetadataFor(c));
	}

	@Override
	public int getNumberProduced() {
		return 12;
	}

	@Override
	public int getExperience() {
		return 1; //1/5 of normal
	}

}
