package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import java.util.Collection;

import net.minecraft.init.Items;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;

public class TankExpandingRecipe extends PylonCastingRecipe {

	public static final int PARALLEL = 8;

	public TankExpandingRecipe() {
		super(ChromaBlocks.TANK.getStackOfMetadata(2), ChromaBlocks.TANK.getStackOfMetadata(0));

		this.addAuxItem(ChromaStacks.aqua, -2, 0);
		this.addAuxItem(ChromaStacks.aqua, 2, 0);
		this.addAuxItem(ChromaStacks.aqua, 0, 2);
		this.addAuxItem(ChromaStacks.aqua, 0, -2);
		this.addAuxItem(ChromaStacks.enderDust, -2, -2);
		this.addAuxItem(ChromaStacks.enderDust, 2, -2);
		this.addAuxItem(ChromaStacks.enderDust, -2, 2);
		this.addAuxItem(ChromaStacks.enderDust, 2, 2);
		this.addAuxItem(ChromaStacks.enderDust, -4, -4);
		this.addAuxItem(ChromaStacks.enderDust, 4, -4);
		this.addAuxItem(ChromaStacks.enderDust, -4, 4);
		this.addAuxItem(ChromaStacks.enderDust, 4, 4);

		this.addAuxItem(Items.quartz, 0, -4);
		this.addAuxItem(ChromaStacks.voidDust, -4, 0);
		this.addAuxItem(ChromaStacks.voidDust, 4, 0);
		this.addAuxItem(ChromaStacks.voidDust, 0, 4);

		this.addAuraRequirement(CrystalElement.CYAN, 3000*PARALLEL/8);
	}

	@Override
	protected void getRequiredProgress(Collection<ProgressStage> c) {
		super.getRequiredProgress(c);
		c.add(ProgressStage.CTM);
	}

	@Override
	public int getRequiredCentralItemCount() {
		return PARALLEL;
	}

	@Override
	public int getNumberProduced() {
		return PARALLEL;
	}

	@Override
	public boolean canBeStacked() {
		return true;
	}

	@Override
	protected float getConsecutiveStackingTimeFactor(TileEntityCastingTable te) {
		return 0.4F;
	}

}
