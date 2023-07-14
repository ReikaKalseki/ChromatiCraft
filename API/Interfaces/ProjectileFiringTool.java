/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Implement this on tools that fire projectiles, to allow CC systems to fire them */
public interface ProjectileFiringTool {

	public void fire(ItemStack is, World world, EntityPlayer ep, boolean randomVec);

	/** How many ticks per shot */
	public int getAutofireRate();

}
