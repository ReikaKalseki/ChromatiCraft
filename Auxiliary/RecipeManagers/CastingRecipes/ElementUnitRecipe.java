package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;

public class ElementUnitRecipe extends MultiBlockCastingRecipe {

	public ElementUnitRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(0), -4, -4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(1), -2, -4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(2), 0, -4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(3), 2, -4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(4), 4, -4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(5), 4, -2);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(6), 4, 0);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(7), 4, 2);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(8), 4, 4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(9), 2, 4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(10), 0, 4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(11), -2, 4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(12), -4, 4);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(13), -4, 2);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(14), -4, 0);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOfMetadata(15), -4, -2);
	}

	@Override
	protected void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {
		ProgressionManager.instance.stepPlayerTo(ep, ProgressStage.STONES);
	}

}
