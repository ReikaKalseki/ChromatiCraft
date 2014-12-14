/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class InvTickerRecipe extends MultiBlockCastingRecipe {

	public InvTickerRecipe(ItemStack out, ItemStack ctr) {
		super(out, ctr);

		this.addRune(CrystalElement.BLACK, -2, -1, -3);
		this.addRune(CrystalElement.LIGHTBLUE, -3, -1, -2);
		this.addRune(CrystalElement.LIGHTBLUE, 2, -1, 3);
		this.addRune(CrystalElement.BLACK, 3, -1, 2);

		this.addAuxItem(new ItemStack(Items.clock), -2, 0);
		this.addAuxItem(new ItemStack(Items.clock), 2, 0);
		this.addAuxItem(ChromaStacks.auraDust, 0, 2);
		this.addAuxItem(ChromaStacks.auraDust, 0, -2);

		this.addAuxItem(new ItemStack(Items.iron_ingot), -2, -2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), -2, 2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 2, -2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 2, 2);

		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), -2, -2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), -2, 2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), 2, -2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), 2, 2);
	}

}
