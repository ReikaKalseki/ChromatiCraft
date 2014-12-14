/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class EnhancedPendantRecipe extends PylonRecipe {

	public EnhancedPendantRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(new ItemStack(Items.diamond), -2, -2);
		this.addAuxItem(ChromaItems.PENDANT.getStackOfMetadata(out.getItemDamage()), 0, -2);
		this.addAuxItem(new ItemStack(Items.diamond), 2, -2);

		this.addAuxItem(new ItemStack(Items.gold_ingot), -2, 0);
		this.addAuxItem(new ItemStack(Items.gold_ingot), 2, 0);

		this.addAuxItem(new ItemStack(Items.ender_eye), -2, 2);
		this.addAuxItem(new ItemStack(Items.ender_eye), 2, 2);

		this.addAuxItem(new ItemStack(Items.ghast_tear), 0, 2);

		this.addAuraRequirement(CrystalElement.PURPLE, 8000);
		this.addAuraRequirement(CrystalElement.WHITE, 2000);
		this.addAuraRequirement(CrystalElement.elements[out.getItemDamage()], 16000);
	}

	@Override
	public int getDuration() {
		return 8*super.getDuration();
	}

}
