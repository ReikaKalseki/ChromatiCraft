package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class RelayRecipe extends MultiBlockCastingRecipe {

	public RelayRecipe(CrystalElement e) {
		super(new ItemStack(ChromaBlocks.PYLONSTRUCT.getItem(), 1, e.ordinal()), ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(0));

		this.addAuxItem(Items.glowstone_dust, 0, -4);
		this.addAuxItem(Items.glowstone_dust, -2, -2);
		this.addAuxItem(Items.glowstone_dust, 2, -2);
		this.addAuxItem(this.getChargedShard(e), 0, -2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(0), 0, 2);

		int[] xyz = runeRing.getNthBlock(e.ordinal());
		this.addRune(e, xyz[0], xyz[1], xyz[2]);
	}

	@Override
	public boolean canRunRecipe(EntityPlayer ep) {
		return super.canRunRecipe(ep) && RecipesCastingTable.playerHasCrafted(ep, RecipeType.PYLON);
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

}
