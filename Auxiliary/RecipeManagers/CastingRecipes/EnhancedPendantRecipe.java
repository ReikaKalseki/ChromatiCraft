package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class EnhancedPendantRecipe extends PendantRecipe {

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
	}

}
