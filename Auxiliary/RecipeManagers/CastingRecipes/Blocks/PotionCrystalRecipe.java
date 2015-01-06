/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class PotionCrystalRecipe extends PylonRecipe {

	public PotionCrystalRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(new ItemStack(Blocks.obsidian), -2, 2);
		this.addAuxItem(new ItemStack(Blocks.gold_block), 0, 2);
		this.addAuxItem(new ItemStack(Blocks.obsidian), 2, 2);

		this.addAuxItem(new ItemStack(Blocks.glowstone), 2, -2);
		this.addAuxItem(new ItemStack(Blocks.redstone_block), -2, -2);

		this.addAuraRequirement(CrystalElement.WHITE, 10000);
		this.addAuraRequirement(CrystalElement.BLUE, 1000);
		this.addAuraRequirement(CrystalElement.PURPLE, 5000);
		this.addAuraRequirement(CrystalElement.elements[out.getItemDamage()], 20000);
	}

	@Override
	public int getDuration() {
		return 4*super.getDuration();
	}

}
