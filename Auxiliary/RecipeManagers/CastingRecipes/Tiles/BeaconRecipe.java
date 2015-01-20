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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class BeaconRecipe extends PylonRecipe {

	public BeaconRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Items.diamond, 0, -2);
		this.addAuxItem(Items.diamond, 0, -4);

		this.addAuraRequirement(CrystalElement.RED, 120000);
		this.addAuraRequirement(CrystalElement.BLACK, 2000);

		this.addAuxItem(Blocks.lapis_block, -4, 4);
		this.addAuxItem(Blocks.obsidian, -2, 4);
		this.addAuxItem(Blocks.obsidian, 0, 4);
		this.addAuxItem(Blocks.obsidian, 2, 4);
		this.addAuxItem(Blocks.lapis_block, 4, 4);

		this.addAuxItem(Items.quartz, -4, 0);
		this.addAuxItem(Items.quartz, 4, 0);

		this.addAuxItem(Items.gold_ingot, -2, 2);
		this.addAuxItem(Items.gold_ingot, 0, 2);
		this.addAuxItem(Items.gold_ingot, 2, 2);
	}

}
