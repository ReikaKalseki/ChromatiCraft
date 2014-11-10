package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class InventoryLinkRecipe extends PylonRecipe {

	public InventoryLinkRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(new ItemStack(Items.ender_pearl), -2, 0);
		this.addAuxItem(new ItemStack(Items.ender_pearl), 2, 0);
		this.addAuxItem(new ItemStack(Items.ender_pearl), 0, 2);
		this.addAuxItem(new ItemStack(Items.ender_pearl), 0, -2);

		this.addAuxItem(ReikaItemHelper.lapisDye, -4, 0);
		this.addAuxItem(ReikaItemHelper.lapisDye, 4, 0);
		this.addAuxItem(ReikaItemHelper.lapisDye, 0, 4);
		this.addAuxItem(ReikaItemHelper.lapisDye, 0, -4);

		this.addAuraRequirement(CrystalElement.LIME, 5000);
		this.addAuraRequirement(CrystalElement.BLACK, 1000);
	}

}
