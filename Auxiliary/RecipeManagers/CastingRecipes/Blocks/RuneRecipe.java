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

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class RuneRecipe extends CastingRecipe implements CoreRecipe {

	public RuneRecipe(CrystalElement e) {
		super(genOutput(e), getRecipe(e, false));
	}

	static ShapedRecipes getRecipe(CrystalElement e, boolean enhanced) {
		ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(enhanced ? 16+e.ordinal() : e.ordinal());
		return ReikaRecipeHelper.getShapedRecipeFor(genOutput(e), "SSS", "SCS", "SSS", 'C', shard, 'S', ChromaBlocks.PYLONSTRUCT.getStackOf());
	}

	static ItemStack genOutput(CrystalElement e) {
		return ChromaBlocks.RUNE.getStackOf(e);
	}

	@Override
	public int getExperience() {
		return 2*super.getExperience();
	}

	@Override
	public void getRequiredProgress(Collection<ProgressStage> c) {
		super.getRequiredProgress(c);
		c.add(ProgressStage.ALLCOLORS);
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

}
