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

import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class CrystalStoneRecipe extends CastingRecipe implements CoreRecipe {

	public CrystalStoneRecipe(StoneTypes s, int amt, Object... recipe) {
		super(calcOutput(s, amt), recipe);
	}

	private static ItemStack calcOutput(StoneTypes s, int amt) {
		return new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), amt, s.ordinal());
	}

	@Override
	public final boolean canBeSimpleAutomated() {
		return true;
	}

	@Override
	protected final void getRequiredProgress(Collection<ProgressStage> c) {
		super.getRequiredProgress(c);
		c.add(ProgressStage.SHARDCHARGE);
	}

}
