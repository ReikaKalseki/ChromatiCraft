/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalLaserRecipe extends PylonRecipe {

	public CrystalLaserRecipe(ItemStack main, ItemStack out) {
		super(main, out);

		this.addAuraRequirement(CrystalElement.BLUE, 25000);
		this.addAuraRequirement(CrystalElement.YELLOW, 5000);

		this.addAuxItem(Blocks.glass, -4, 0);
		this.addAuxItem(ChromaStacks.crystalFocus, -2, 0);
		this.addAuxItem(ChromaStacks.crystalMirror, 2, 0);
		this.addAuxItem(Blocks.quartz_block, 2, 0);
		this.addAuxItem(Items.diamond, 4, 0);
		this.addAuxItem(Blocks.obsidian, 2, -2);
		this.addAuxItem(Blocks.obsidian, 2, 2);
		this.addAuxItem(Blocks.obsidian, -2, -2);
		this.addAuxItem(Blocks.obsidian, -2, 2);
		this.addAuxItem(Blocks.redstone_block, 0, -2);
		this.addAuxItem(Blocks.redstone_block, 0, 2);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 32;
	}

}
