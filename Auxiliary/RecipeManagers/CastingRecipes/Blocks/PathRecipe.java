/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class PathRecipe extends TempleCastingRecipe {

	public PathRecipe(ItemStack main, int meta, ItemStack outer) {
		super(ChromaBlocks.PATH.getStackOfMetadata(meta), getRecipe(ChromaBlocks.PATH.getStackOfMetadata(meta), main, outer));

		this.addRune(CrystalElement.LIME, 1, -1, -3);
		this.addRune(CrystalElement.BROWN, -1, -1, 3);
	}

	private static IRecipe getRecipe(ItemStack out, ItemStack main, ItemStack outer) {
		return ReikaRecipeHelper.getShapedRecipeFor(out, "OSO", "SMS", "OSO", 'O', outer, 'M', main, 'S', Blocks.stonebrick);
	}

	public PathRecipe(ItemStack main, int meta, Item outer) {
		this(main, meta, new ItemStack(outer));
	}

	public PathRecipe(ItemStack main, int meta, Block outer) {
		this(main, meta, new ItemStack(outer));
	}

	@Override
	public int getNumberProduced() {
		return 16;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

}
