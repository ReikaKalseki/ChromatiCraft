package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.AppEngHandler;

public class VoidStorageRecipe extends PylonRecipe {

	public VoidStorageRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuraRequirement(CrystalElement.BLACK, 5000);
		this.addAuraRequirement(CrystalElement.BROWN, 5000);
		this.addAuraRequirement(CrystalElement.PURPLE, 5000);
		this.addAuraRequirement(CrystalElement.WHITE, 5000);

		this.addAuxItem(new ItemStack(AppEngHandler.getInstance().quartzGlass), -2, -2);
		this.addAuxItem(new ItemStack(Items.redstone), 2, -2);
		this.addAuxItem(new ItemStack(AppEngHandler.getInstance().quartzGlass), 2, -2);

		this.addAuxItem(new ItemStack(Items.redstone), -2, 0);
		this.addAuxItem(new ItemStack(Items.redstone), 2, 0);

		this.addAuxItem(new ItemStack(Items.iron_ingot), -2, 2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 0, 2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 2, 2);
	}

}
