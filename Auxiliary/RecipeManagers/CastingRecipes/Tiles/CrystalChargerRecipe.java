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
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class CrystalChargerRecipe extends MultiBlockCastingRecipe {

	public CrystalChargerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ReikaItemHelper.stoneSlab, 0, 2);
		this.addAuxItem(Blocks.obsidian, 2, -2);
		this.addAuxItem(Blocks.obsidian, 2, 2);
		this.addAuxItem(Blocks.obsidian, -2, -2);
		this.addAuxItem(Blocks.obsidian, -2, 2);

		this.addAuxItem(this.getShard(CrystalElement.WHITE), 2, 0);
		this.addAuxItem(this.getShard(CrystalElement.WHITE), -2, 0);
		this.addAuxItem(this.getShard(CrystalElement.WHITE), 0, -2);
	}

	@Override
	public int getDuration() {
		return 2*super.getDuration();
	}

}
