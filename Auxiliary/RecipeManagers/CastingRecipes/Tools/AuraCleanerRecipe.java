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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AuraCleanerRecipe extends PylonCastingRecipe {

	public AuraCleanerRecipe(ItemStack out, ItemStack in) {
		super(out, in);

		this.addAuxItem(Blocks.glowstone, 0, -2);

		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), -2, 0);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), 2, 0);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), -2, 2);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), 0, 2);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), 2, 2);

		this.addRune(CrystalElement.BLACK, -3, -1, 3);
		this.addRune(CrystalElement.BLACK, 3, -1, -3);

		this.addRune(CrystalElement.WHITE, 0, -1, -4);
		this.addRune(CrystalElement.WHITE, 0, -1, 4);

		this.addAuxItem(Items.diamond, -2, 4);
		this.addAuxItem(Items.diamond, 2, 4);
		this.addAuxItem(Items.emerald, 0, -4);

		this.addAuraRequirement(CrystalElement.BLACK, 20000);
		this.addAuraRequirement(CrystalElement.WHITE, 10000);
		this.addAuraRequirement(CrystalElement.GRAY, 5000);
		this.addAuraRequirement(CrystalElement.PINK, 5000);
		this.addAuraRequirement(CrystalElement.MAGENTA, 10000);
	}

}
