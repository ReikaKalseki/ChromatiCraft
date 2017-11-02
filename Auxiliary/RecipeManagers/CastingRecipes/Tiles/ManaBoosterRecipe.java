package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class ManaBoosterRecipe extends PylonCastingRecipe {

	private static final Object[] ringH = {"Botania:manaResource:19", Items.blaze_powder, ChromaStacks.beaconDust, "Botania:quartz:5"};
	private static final Object[] ringV = {"Botania:manaResource:18", "Botania:manaResource:1", ChromaStacks.auraDust, "Botania:manaResource:12"};

	public ManaBoosterRecipe(ItemStack out) {
		super(out, ReikaItemHelper.lookupItem("Botania:spark"));

		this.addAuxItem(ChromaStacks.lumenGem, -2, -2);
		this.addAuxItem(ChromaStacks.lumenGem, 2, -2);
		this.addAuxItem(ChromaStacks.lumenGem, -2, 2);
		this.addAuxItem(ChromaStacks.lumenGem, 2, 2);

		this.addAuxItem(ReikaItemHelper.parseItem("Botania:rune:11"), -2, 0);
		this.addAuxItem(ReikaItemHelper.parseItem("Botania:rune:11"), 2, 0);
		this.addAuxItem(ReikaItemHelper.parseItem("Botania:rune:8"), 0, 2);
		this.addAuxItem(ReikaItemHelper.parseItem("Botania:rune:8"), 0, -2);

		for (int i = 0; i < 4; i++) {
			int idx = i*2-4;
			this.addAuxItem(ReikaItemHelper.parseItem(ringH[3-i]), 4, idx+2);
			this.addAuxItem(ReikaItemHelper.parseItem(ringH[3-i]), -4, -idx-2);
			this.addAuxItem(ReikaItemHelper.parseItem(ringV[i]), idx, 4);
			this.addAuxItem(ReikaItemHelper.parseItem(ringV[i]), -idx, -4);
		}

		this.addAuraRequirement(CrystalElement.BLACK, 18000);
		this.addAuraRequirement(CrystalElement.GRAY, 9000);
		this.addAuraRequirement(CrystalElement.YELLOW, 24000);

	}

}
