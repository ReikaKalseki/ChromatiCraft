/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class PendantRecipe extends MultiBlockCastingRecipe {

	public PendantRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Blocks.glowstone, -2, -2);
		this.addAuxItem(Items.string, 0, -2);
		this.addAuxItem(Blocks.glowstone, 2, -2);

		this.addAuxItem(Items.quartz, -2, 0);
		this.addAuxItem(Items.quartz, 2, 0);

		this.addAuxItem(Items.ender_pearl, -2, 2);
		this.addAuxItem(Items.ender_pearl, 2, 2);

		this.addAuxItem(Items.diamond, 0, 2);

		//this.addRune(CrystalElement.elements[out.getItemDamage()], 0, -1, -4);

		//this.addAuraRequirement(CrystalElement.PURPLE, 2000);
		//this.addAuraRequirement(CrystalElement.WHITE, 1000);
		//this.addAuraRequirement(CrystalElement.elements[out.getItemDamage()], 5000);
	}

	@Override
	public int getDuration() {
		return 4*super.getDuration();
	}
}
