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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class CompoundRuneRecipe extends MultiBlockCastingRecipe {

	public CompoundRuneRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.auraDust, 2, -2);
		this.addAuxItem(ChromaStacks.auraDust, -2, -2);
		this.addAuxItem(ChromaStacks.auraDust, -2, 2);
		this.addAuxItem(ChromaStacks.auraDust, 2, 2);

		this.addAuxItem(ChromaStacks.bindingCrystal, 0, -2);
		this.addAuxItem(ChromaStacks.bindingCrystal, 0, 2);
		this.addAuxItem(Items.glowstone_dust, 2, 0);
		this.addAuxItem(Items.glowstone_dust, -2, 0);

		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 0), -4, -4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 1), -2, -4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 2), 0, -4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 3), 2, -4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 4), 4, -4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 5), 4, -2);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 6), 4, 0);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 7), 4, 2);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 8), 4, 4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 9), 2, 4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 10), 0, 4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 11), -2, 4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 12), -4, 4);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 13), -4, 2);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 14), -4, 0);
		this.addAuxItem(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 15), -4, -2);
	}

	@Override
	protected void getRequiredProgress(Collection<ProgressStage> c) {
		super.getRequiredProgress(c);
		c.add(ProgressStage.INFUSE);
	}

	@Override
	public final boolean canBeSimpleAutomated() {
		return true;
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 32;
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

}
