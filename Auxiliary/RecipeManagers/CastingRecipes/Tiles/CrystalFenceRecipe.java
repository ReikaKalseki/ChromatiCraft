/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class CrystalFenceRecipe extends MultiBlockCastingRecipe {

	public CrystalFenceRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Items.diamond, -2, -4);
		this.addAuxItem(Items.diamond, 0, -4);
		this.addAuxItem(Items.diamond, 2, -4);

		this.addAuxItem(Blocks.gold_block, 0, -2);

		this.addAuxItem(Blocks.obsidian, -2, 2);
		this.addAuxItem(Blocks.obsidian, 0, 2);
		this.addAuxItem(Blocks.obsidian, 2, 2);

		this.addAuxItem(ReikaItemHelper.stoneSlab, -4, 4);
		this.addAuxItem(ReikaItemHelper.stoneSlab, -2, 4);
		this.addAuxItem(ReikaItemHelper.stoneSlab, 0, 4);
		this.addAuxItem(ReikaItemHelper.stoneSlab, 2, 4);
		this.addAuxItem(ReikaItemHelper.stoneSlab, 4, 4);

		this.addAuxItem(ChromaStacks.beaconDust, -2, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -2);

		this.addAuxItem(ChromaStacks.auraDust, -4, 0);
		this.addAuxItem(ChromaStacks.auraDust, 4, 0);
		this.addAuxItem(ChromaStacks.auraDust, -2, 0);
		this.addAuxItem(ChromaStacks.auraDust, 2, 0);

		this.addAuxItem(ChromaStacks.auraDust, -4, 2);
		this.addAuxItem(ChromaStacks.auraDust, 4, 2);

		this.addAuxItem(ChromaStacks.auraIngot, -4, -4);
		this.addAuxItem(ChromaStacks.auraIngot, 4, -4);

		this.addAuxItem(ChromaStacks.auraIngot, -4, -2);
		this.addAuxItem(ChromaStacks.auraIngot, 4, -2);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 2;
	}

}
