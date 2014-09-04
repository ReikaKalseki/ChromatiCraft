package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalLensRecipe extends MultiBlockCastingRecipe {

	public CrystalLensRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(this.getShard(CrystalElement.WHITE), -2, -2);
		this.addAuxItem(this.getShard(CrystalElement.WHITE), -2, 2);
		this.addAuxItem(this.getShard(CrystalElement.WHITE), 2, -2);
		this.addAuxItem(this.getShard(CrystalElement.WHITE), 2, 2);

		this.addAuxItem(this.getShard(CrystalElement.BLUE), -2, 0);
		this.addAuxItem(this.getShard(CrystalElement.BLUE), 2, 0);
		this.addAuxItem(this.getShard(CrystalElement.BLUE), 0, -2);
		this.addAuxItem(this.getShard(CrystalElement.BLUE), 0, 2);
	}

}
