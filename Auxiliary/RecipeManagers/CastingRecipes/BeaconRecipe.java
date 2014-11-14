package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class BeaconRecipe extends PylonRecipe {

	public BeaconRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(new ItemStack(Items.diamond), 0, -2);
		this.addAuxItem(new ItemStack(Items.diamond), 0, -4);

		this.addAuraRequirement(CrystalElement.RED, 120000);
		this.addAuraRequirement(CrystalElement.BLACK, 2000);

		this.addAuxItem(new ItemStack(Blocks.lapis_block), -4, 4);
		this.addAuxItem(new ItemStack(Blocks.obsidian), -2, 4);
		this.addAuxItem(new ItemStack(Blocks.obsidian), 0, 4);
		this.addAuxItem(new ItemStack(Blocks.obsidian), 2, 4);
		this.addAuxItem(new ItemStack(Blocks.lapis_block), 4, 4);

		this.addAuxItem(new ItemStack(Items.quartz), -4, 0);
		this.addAuxItem(new ItemStack(Items.quartz), 4, 0);

		this.addAuxItem(new ItemStack(Items.gold_ingot), -2, 2);
		this.addAuxItem(new ItemStack(Items.gold_ingot), 0, 2);
		this.addAuxItem(new ItemStack(Items.gold_ingot), 2, 2);
	}

}
