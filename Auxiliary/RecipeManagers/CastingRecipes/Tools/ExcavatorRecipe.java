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

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class ExcavatorRecipe extends MultiBlockCastingRecipe {

	public ExcavatorRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		//for now
		this.addAuxItem(Items.stick, -2, -2);
		this.addAuxItem(Items.stick, 2, 2);
		this.addAuxItem(Items.stick, -2, 2);
		this.addAuxItem(Items.stick, 2, -2);

		this.addAuxItem(ChromaStacks.chromaIngot, -2, 0);
		this.addAuxItem(ChromaStacks.chromaIngot, 2, 0);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, 2);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, -2);

		this.addAuxItem(this.getChargedShard(CrystalElement.BROWN), -4, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.BROWN), 4, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.BROWN), 0, 4);
		this.addAuxItem(this.getChargedShard(CrystalElement.BROWN), 0, -4);
	}

}
