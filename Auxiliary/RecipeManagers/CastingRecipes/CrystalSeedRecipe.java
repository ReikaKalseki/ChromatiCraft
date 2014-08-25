package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class CrystalSeedRecipe extends TempleCastingRecipe {

	private static final BlockArray runes = new BlockArray();

	public CrystalSeedRecipe(ItemStack out, CrystalElement e) {
		super(out, ReikaRecipeHelper.getShapedRecipeFor(out, "GSG", "SsS", "GSG", 'G', Items.glowstone_dust, 'S', getShard(e), 's', Items.wheat_seeds));

		int[] xyz = runes.getNthBlock(e.ordinal());
		this.addRune(e, xyz[0], xyz[1], xyz[2]);
	}

	static {
		runes.addBlockCoordinate(-2, -1, -2);
		runes.addBlockCoordinate(-1, -1, -2);
		runes.addBlockCoordinate(0, -1, -2);
		runes.addBlockCoordinate(1, -1, -2);
		runes.addBlockCoordinate(2, -1, -2);
		runes.addBlockCoordinate(2, -1, -1);
		runes.addBlockCoordinate(2, -1, 0);
		runes.addBlockCoordinate(2, -1, 1);
		runes.addBlockCoordinate(2, -1, 2);
		runes.addBlockCoordinate(1, -1, 2);
		runes.addBlockCoordinate(0, -1, 2);
		runes.addBlockCoordinate(-1, -1, 2);
		runes.addBlockCoordinate(-2, -1, 2);
		runes.addBlockCoordinate(-2, -1, 1);
		runes.addBlockCoordinate(-2, -1, 0);
		runes.addBlockCoordinate(-2, -1, -1);
	}

}
