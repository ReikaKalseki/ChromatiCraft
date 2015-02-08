package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class BiomePainterRecipe extends PylonRecipe {

	public BiomePainterRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Blocks.sand, -4, 0);
		this.addAuxItem(Blocks.sand, 4, 0);
		this.addAuxItem(Blocks.stone, -4, 4);
		this.addAuxItem(Blocks.stone, 4, 4);
		this.addAuxItem(Blocks.ice, -2, 4);
		this.addAuxItem(Blocks.ice, 2, 4);
		this.addAuxItem(Blocks.grass, 0, -4);
		this.addAuxItem(Blocks.grass, 0, 4);
		this.addAuxItem(new ItemStack(Blocks.sapling), 0, -2);
		this.addAuxItem(new ItemStack(Blocks.sapling, 1, 1), 0, 2);
		this.addAuxItem(new ItemStack(Blocks.sapling, 1, 2), 2, 0);
		this.addAuxItem(new ItemStack(Blocks.sapling, 1, 3), -2, 0);
		this.addAuxItem(new ItemStack(Blocks.sapling, 1, 4), -4, 2);
		this.addAuxItem(new ItemStack(Blocks.sapling, 1, 5), 4, 2);
		this.addAuxItem(Items.water_bucket, -4, -2);
		this.addAuxItem(Items.lava_bucket, 4, -2);
		this.addAuxItem(Blocks.netherrack, -2, -4);
		this.addAuxItem(Blocks.end_stone, 2, -4);

		this.addAuxItem(ChromaStacks.chromaDust, -2, 2);
		this.addAuxItem(ChromaStacks.chromaDust, 2, 2);
		this.addAuxItem(ChromaStacks.chromaDust, -2, -2);
		this.addAuxItem(ChromaStacks.chromaDust, 2, -2);
		this.addAuxItem(ChromaStacks.auraDust, -4, -4);
		this.addAuxItem(ChromaStacks.auraDust, 4, -4);

		this.addAuraRequirement(CrystalElement.BLACK, 10000);
		this.addAuraRequirement(CrystalElement.GREEN, 50000);
		this.addAuraRequirement(CrystalElement.GRAY, 50000);
		this.addAuraRequirement(CrystalElement.CYAN, 25000);
		this.addAuraRequirement(CrystalElement.BROWN, 5000);
	}

}
