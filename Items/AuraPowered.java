/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import Reika.ChromatiCraft.Magic.ElementTagCompound;

import net.minecraft.item.ItemStack;

public interface AuraPowered {

	public ElementTagCompound getRequirements(ItemStack is);

}
