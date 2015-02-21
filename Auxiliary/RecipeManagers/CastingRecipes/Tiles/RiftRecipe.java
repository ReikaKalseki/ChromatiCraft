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

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class RiftRecipe extends PylonRecipe {

	public RiftRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuraRequirement(CrystalElement.BLACK, 5000);
		this.addAuraRequirement(CrystalElement.LIME, 5000);
		this.addAuraRequirement(CrystalElement.YELLOW, 2000);

		this.addAuxItem(this.getChargedShard(CrystalElement.RED), -2, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.LIME), 2, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLUE), -2, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 2, 2);

		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 0, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 0, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 2, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), -2, 0);
	}

	@Override
	public int getDuration() {
		return 200+super.getDuration();
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

}
