package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;

public interface TieredItem {

	public ProgressStage getDiscoveryTier(ItemStack is);
	public boolean isTiered(ItemStack is);

}
