package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;


public class ShieldedCellRecipe extends CastingRecipe {

	public ShieldedCellRecipe(ItemStack out) {
		super(out, getRecipe(out));
	}

	private static IRecipe getRecipe(ItemStack out) {
		Item cell = AppEngHandler.getInstance().get4KCell();
		Object[] in = {"rSr", "SCS", "SgS", 'S', ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.STONE.ordinal()), 'C', cell, 'r', Items.redstone, 'g', Items.quartz};
		return ReikaRecipeHelper.getShapedRecipeFor(out, in);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 1;
	}

	@Override
	public int getPenaltyThreshold() {
		return 1;
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0;
	}

}
