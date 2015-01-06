/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class IridescentCrystalRecipe extends PylonRecipe {

	public IridescentCrystalRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.iridChunk, -2, 0);
		this.addAuxItem(ChromaStacks.iridChunk, -4, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 2, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 4, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 0, -2);
		this.addAuxItem(ChromaStacks.iridChunk, 0, -4);

		this.addAuxItem(new ItemStack(Blocks.obsidian), -4, 2);
		this.addAuxItem(new ItemStack(Blocks.obsidian), -2, 2);
		this.addAuxItem(new ItemStack(Blocks.obsidian), 0, 2);
		this.addAuxItem(new ItemStack(Blocks.obsidian), 2, 2);
		this.addAuxItem(new ItemStack(Blocks.obsidian), 4, 2);

		this.addAuxItem(new ItemStack(Blocks.glowstone), 2, -2);
		this.addAuxItem(new ItemStack(Blocks.glowstone), -2, -2);

		this.addAuraRequirement(CrystalElement.YELLOW, 5000);
		this.addAuraRequirement(CrystalElement.BLACK, 10000);
		this.addAuraRequirement(CrystalElement.PURPLE, 5000);
	}

	@Override
	public int getDuration() {
		return 4*super.getDuration();
	}

}
