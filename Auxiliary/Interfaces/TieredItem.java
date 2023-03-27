/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Magic.Progression.ProgressStage;

public interface TieredItem {

	public ProgressStage getDiscoveryTier(ItemStack is);
	public boolean isTiered(ItemStack is);

}
