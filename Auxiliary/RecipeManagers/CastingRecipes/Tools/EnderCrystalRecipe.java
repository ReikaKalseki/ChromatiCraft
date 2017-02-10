/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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

public class EnderCrystalRecipe extends PylonCastingRecipe {

	public EnderCrystalRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.enderIngot, -2, 0);
		this.addAuxItem(ChromaStacks.enderIngot, 2, 0);

		this.addAuxItem(ChromaStacks.fieryIngot, 0, 2);
		this.addAuxItem(ChromaStacks.fieryIngot, 0, 4);

		this.addAuxItem(Blocks.obsidian, -2, 2);
		this.addAuxItem(Blocks.obsidian, -2, 4);
		this.addAuxItem(Blocks.obsidian, 2, 2);
		this.addAuxItem(Blocks.obsidian, 2, 4);

		this.addAuxItem(ChromaStacks.auraIngot, 0, -2);

		this.addAuxItem(ChromaStacks.chromaIngot, -2, -2);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, -4);
		this.addAuxItem(ChromaStacks.chromaIngot, 2, -2);

		this.addAuxItem(Items.glowstone_dust, -4, -2);
		this.addAuxItem(Items.glowstone_dust, 4, -2);
		this.addAuxItem(Items.glowstone_dust, -2, -4);
		this.addAuxItem(Items.glowstone_dust, 2, -4);
		this.addAuxItem(Items.glowstone_dust, -4, -4);
		this.addAuxItem(Items.glowstone_dust, 4, -4);


		this.addAuxItem(Blocks.iron_block, -4, 4);
		this.addAuxItem(Blocks.iron_block, 4, 4);

		this.addAuraRequirement(CrystalElement.BLACK, 25000);
		this.addAuraRequirement(CrystalElement.MAGENTA, 25000);
		this.addAuraRequirement(CrystalElement.RED, 5000);
		this.addAuraRequirement(CrystalElement.PINK, 10000);
	}

}
