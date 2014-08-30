package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class PendantRecipe extends PylonRecipe {

	public PendantRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(new ItemStack(Blocks.glowstone), -2, -2);
		this.addAuxItem(new ItemStack(Items.string), 0, -2);
		this.addAuxItem(new ItemStack(Blocks.glowstone), 2, -2);

		this.addAuxItem(new ItemStack(Items.quartz), -2, 0);
		this.addAuxItem(new ItemStack(Items.quartz), 2, 0);

		this.addAuxItem(new ItemStack(Items.ender_pearl), -2, 2);
		this.addAuxItem(new ItemStack(Items.ender_pearl), 2, 2);

		this.addAuxItem(new ItemStack(Items.diamond), 0, 2);

		this.addAuraRequirement(CrystalElement.PURPLE, 2000);
		this.addAuraRequirement(CrystalElement.WHITE, 1000);
	}
}
