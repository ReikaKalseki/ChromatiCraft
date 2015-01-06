/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Magic.ElementTagCompound;

public interface AuraPowered {

	public ElementTagCompound getRequirements(ItemStack is);

}
