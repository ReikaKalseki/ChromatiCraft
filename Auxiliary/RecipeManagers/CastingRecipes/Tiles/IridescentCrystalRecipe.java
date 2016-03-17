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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;

public class IridescentCrystalRecipe extends PylonRecipe {

	public IridescentCrystalRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.iridChunk, -2, 0);
		this.addAuxItem(ChromaStacks.iridChunk, -4, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 2, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 4, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 0, -2);
		this.addAuxItem(ChromaStacks.iridChunk, 0, -4);

		this.addAuxItem(Blocks.obsidian, -4, 2);
		this.addAuxItem(Blocks.obsidian, -2, 2);
		this.addAuxItem(Blocks.obsidian, 0, 2);
		this.addAuxItem(Blocks.obsidian, 2, 2);
		this.addAuxItem(Blocks.obsidian, 4, 2);

		this.addAuxItem(Blocks.glowstone, 2, -2);
		this.addAuxItem(Blocks.glowstone, -2, -2);

		this.addAuraRequirement(CrystalElement.YELLOW, 15000);
		this.addAuraRequirement(CrystalElement.BLACK, 25000);
		this.addAuraRequirement(CrystalElement.PURPLE, 10000);
	}

	@Override
	public int getDuration() {
		return 4*super.getDuration();
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {
		super.onCrafted(te, ep);
		//ProgressStage.POWERCRYSTAL.stepPlayerTo(ep);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 128;
	}

}
