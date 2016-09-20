package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class AvoLaserRecipe extends PylonRecipe {

	public AvoLaserRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), -4, 2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), -4, 4);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), -2, 4);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 0, 4);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 2, 4);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 4, 4);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 4, 2);

		this.addAuxItem(Items.diamond, -2, 2);
		this.addAuxItem(Items.emerald, 0, 2);
		this.addAuxItem(Items.diamond, 2, 2);

		this.addAuxItem(Items.gold_ingot, -4, 0);
		this.addAuxItem(Items.diamond, -2, 0);
		this.addAuxItem(Items.diamond, 2, 0);
		this.addAuxItem(Items.gold_ingot, 4, 0);

		this.addAuxItem(Items.gold_ingot, -4, -2);
		this.addAuxItem(ChromaStacks.avolite, -2, -2);
		this.addAuxItem(ChromaStacks.avolite, 0, -2);
		this.addAuxItem(ChromaStacks.avolite, 2, -2);
		this.addAuxItem(Items.gold_ingot, 4, -2);

		this.addAuxItem(ChromaStacks.focusDust, -2, -4);
		this.addAuxItem(ChromaStacks.avolite, 0, -4);
		this.addAuxItem(ChromaStacks.focusDust, 2, -4);

		this.addAuraRequirement(CrystalElement.PINK, 40000);
		this.addAuraRequirement(CrystalElement.YELLOW, 20000);
		this.addAuraRequirement(CrystalElement.BLUE, 10000);
	}

}
