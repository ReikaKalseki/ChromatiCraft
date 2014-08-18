/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

public interface ProgrammableSpawner {

	public Class<? extends EntityLiving> getSpawnerEntity(ItemStack is);

	public void setSpawnerType(ItemStack is, Class<? extends EntityLiving> cl);

}
