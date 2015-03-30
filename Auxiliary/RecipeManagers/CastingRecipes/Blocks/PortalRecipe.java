package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RecipeCrystalRepeater;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;

public class PortalRecipe extends PylonRecipe {

	public PortalRecipe(ItemStack out, ItemStack main, RecipeCrystalRepeater repeater) {
		super(out, main);

		this.addAuxItem(ChromaStacks.enderDust, -2, 0);
		this.addAuxItem(ChromaStacks.enderDust, 2, 0);
		this.addAuxItem(ChromaStacks.enderDust, 0, 2);
		this.addAuxItem(ChromaStacks.enderDust, 0, -2);

		this.addAuxItem(ChromaStacks.spaceDust, -2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, -2, 2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, 2);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			this.addAuraRequirement(e, e.isPrimary() ? 100000 : 50000);
			this.addRune(e, runeRing.getNthBlock(i)[0], runeRing.getNthBlock(i)[1], runeRing.getNthBlock(i)[2]);
		}
		this.addRunes(repeater.getRunes());
	}

	@Override
	public int getNumberProduced() {
		return 9;
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {

	}

	@Override
	public void onRecipeTick(TileEntityCastingTable te) {

	}

	@Override
	public ChromaSounds getSoundOverride(int soundTimer) {
		return null;
	}

	@Override
	protected void getRequiredProgress(Collection<ProgressStage> c) {
		c.addAll(ProgressionManager.instance.getPrereqs(ProgressStage.DIMENSION));
	}

	@Override
	public int getExperience() {
		return super.getExperience()*50;
	}

	@Override
	public int getDuration() {
		return super.getDuration()*16;
	}

}
