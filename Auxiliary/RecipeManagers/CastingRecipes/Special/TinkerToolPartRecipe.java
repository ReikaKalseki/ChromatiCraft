package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.TinkerPart;


public class TinkerToolPartRecipe extends CastingRecipe {

	private final TinkerPart part;

	public TinkerToolPartRecipe(TinkerPart p) {
		super(p.getItem(ExtraChromaIDs.CHROMAMATID.getValue()), getRecipe(p));
		part = p;
	}

	private static IRecipe getRecipe(TinkerPart p) {
		ItemStack out = p.getItem(ExtraChromaIDs.CHROMAMATID.getValue());
		ItemStack[] in = ReikaArrayHelper.getArrayOf(ChromaStacks.complexIngot, MathHelper.ceiling_float_int(p.getIngotCost()));
		in = Arrays.copyOf(in, in.length+1);
		in[in.length-1] = p.getPattern();
		return ReikaRecipeHelper.getShapelessRecipeFor(out, in);
	}

	@Override
	public int getNumberProduced() {
		return part.getIngotCost() >= 1 ? 1 : 2;
	}

}
