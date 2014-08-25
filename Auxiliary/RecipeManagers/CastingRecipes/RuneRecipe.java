package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class RuneRecipe extends CastingRecipe {

	public RuneRecipe(ItemStack out, int meta) {
		super(out, getRecipe(out, meta));
	}

	private static ShapedRecipes getRecipe(ItemStack out, int meta) {
		ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(meta);
		return ReikaRecipeHelper.getShapedRecipeFor(out, "SSS", "SCS", "SSS", 'C', shard, 'S', ChromaBlocks.PYLONSTRUCT.getStackOf());
	}

}
