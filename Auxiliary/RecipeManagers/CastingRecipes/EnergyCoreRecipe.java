/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class EnergyCoreRecipe extends MultiBlockCastingRecipe {

	public EnergyCoreRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(this.getShard(CrystalElement.YELLOW), -2, -2);
		this.addAuxItem(this.getShard(CrystalElement.YELLOW), -4, -4);

		this.addAuxItem(this.getShard(CrystalElement.YELLOW), 2, -2);
		this.addAuxItem(this.getShard(CrystalElement.YELLOW), 4, -4);

		this.addAuxItem(this.getShard(CrystalElement.YELLOW), -2, 2);
		this.addAuxItem(this.getShard(CrystalElement.YELLOW), -4, 4);

		this.addAuxItem(this.getShard(CrystalElement.YELLOW), 2, 2);
		this.addAuxItem(this.getShard(CrystalElement.YELLOW), 4, 4);

		this.addAuxItem(this.getShard(CrystalElement.WHITE), 2, 0);
		this.addAuxItem(this.getShard(CrystalElement.WHITE), -2, 0);
		this.addAuxItem(this.getShard(CrystalElement.WHITE), 0, 2);
		this.addAuxItem(this.getShard(CrystalElement.WHITE), 0, -2);

		this.addRune(CrystalElement.YELLOW, -3, 0, -2);
		this.addRune(CrystalElement.YELLOW, 3, 0, 2);
	}

}
