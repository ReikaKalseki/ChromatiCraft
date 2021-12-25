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
import java.util.HashSet;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Items.ItemTieredResource;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class CrystalStoneRecipe extends CastingRecipe implements CoreRecipe {

	private final HashSet<ProgressStage> requiredProgress = new HashSet();

	public CrystalStoneRecipe(StoneTypes s, int amt, IRecipe recipe) {
		super(calcOutput(s, amt), recipe);
	}

	public CrystalStoneRecipe(StoneTypes s, int amt, Object... recipe) {
		super(calcOutput(s, amt), getRecipe(s, amt, recipe));
		for (Object o : recipe) {
			if (o instanceof ItemStack) {
				ItemStack is = (ItemStack)o;
				if (ChromaItems.SHARD.matchWith(is) && is.getItemDamage() >= 16) {
					requiredProgress.add(ProgressStage.SHARDCHARGE);
				}
				else if (ChromaBlocks.RUNE.match(is)) {
					requiredProgress.add(ProgressStage.ALLCOLORS);
				}
				else if (ReikaItemHelper.matchStacks(ChromaStacks.iridCrystal, is)) {
					requiredProgress.add(ProgressStage.INFUSE);
				}
				else if (ChromaItems.TIERED.matchWith(is)) {
					requiredProgress.add(((ItemTieredResource)is.getItem()).getDiscoveryTier(is));
				}
			}
		}
	}

	private static IRecipe getRecipe(StoneTypes s, int amt, Object... recipe) {
		return ReikaRecipeHelper.getShapedRecipeFor(calcOutput(s, amt), recipe);
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
		c.addAll(requiredProgress);
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

}
