/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class EtherealPendantRecipe extends MultiBlockCastingRecipe {

	public EtherealPendantRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromatiCraft.luma, -2, 0);
		this.addAuxItem(ChromatiCraft.luma, 2, 0);
		this.addAuxItem(ChromatiCraft.luma, 0, 2);
		this.addAuxItem(ChromatiCraft.luma, 0, -2);

		this.addAuxItem(ChromaBlocks.GLASS.getStackOf(CrystalElement.BLACK), -4, 0);
		this.addAuxItem(ChromaBlocks.GLASS.getStackOf(CrystalElement.BLACK), 4, 0);
		this.addAuxItem(ChromaBlocks.GLASS.getStackOf(CrystalElement.BLACK), 0, 4);
		this.addAuxItem(ChromaBlocks.GLASS.getStackOf(CrystalElement.BLACK), 0, -4);
		this.addAuxItem(ChromaBlocks.GLASS.getStackOf(CrystalElement.BLUE), 2, 2);
		this.addAuxItem(ChromaBlocks.GLASS.getStackOf(CrystalElement.BLUE), -2, 2);
		this.addAuxItem(ChromaBlocks.GLASS.getStackOf(CrystalElement.BLUE), 2, -2);
		this.addAuxItem(ChromaBlocks.GLASS.getStackOf(CrystalElement.BLUE), -2, -2);

		this.addAuxItem(Items.string, -2, -4);
		this.addAuxItem(Items.string, 2, -4);

		this.addAuxItem(ChromaStacks.spaceIngot, -2, 4);
		this.addAuxItem(ChromaStacks.spaceIngot, 2, 4);

		this.addAuxItem(ChromaStacks.auraDust, 4, -2);
		this.addAuxItem(ChromaStacks.auraDust, 4, 2);
		this.addAuxItem(ChromaStacks.auraDust, -4, -2);
		this.addAuxItem(ChromaStacks.auraDust, -4, 2);
	}

}
