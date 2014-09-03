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

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalTankRecipe extends PylonRecipe {

	public CrystalTankRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuraRequirement(CrystalElement.BLACK, 6000);
		this.addAuraRequirement(CrystalElement.CYAN, 9000);

		this.addAuxItem(this.getShard(CrystalElement.CYAN), 0, 2);
		this.addAuxItem(this.getShard(CrystalElement.CYAN), 0, -2);
		this.addAuxItem(this.getShard(CrystalElement.CYAN), 2, 0);
		this.addAuxItem(this.getShard(CrystalElement.CYAN), -2, 0);
		this.addAuxItem(this.getShard(CrystalElement.CYAN), 2, 2);
		this.addAuxItem(this.getShard(CrystalElement.CYAN), -2, 2);
		this.addAuxItem(this.getShard(CrystalElement.CYAN), 2, -2);
		this.addAuxItem(this.getShard(CrystalElement.CYAN), -2, -2);

		this.addAuxItem(new ItemStack(Blocks.glass), -4, -4);
		this.addAuxItem(new ItemStack(Blocks.glass), -2, -4);
		this.addAuxItem(new ItemStack(Blocks.glass), 0, -4);
		this.addAuxItem(new ItemStack(Blocks.glass), 2, -4);
		this.addAuxItem(new ItemStack(Blocks.glass), 4, -4);
		this.addAuxItem(new ItemStack(Blocks.glass), 4, -2);
		this.addAuxItem(new ItemStack(Blocks.glass), 4, 0);
		this.addAuxItem(new ItemStack(Blocks.glass), 4, 2);
		this.addAuxItem(new ItemStack(Blocks.glass), 4, 4);
		this.addAuxItem(new ItemStack(Blocks.glass), 2, 4);
		this.addAuxItem(new ItemStack(Blocks.glass), 0, 4);
		this.addAuxItem(new ItemStack(Blocks.glass), -2, 4);
		this.addAuxItem(new ItemStack(Blocks.glass), -4, 4);
		this.addAuxItem(new ItemStack(Blocks.glass), -4, 2);
		this.addAuxItem(new ItemStack(Blocks.glass), -4, 0);
		this.addAuxItem(new ItemStack(Blocks.glass), -4, -2);
	}
}
