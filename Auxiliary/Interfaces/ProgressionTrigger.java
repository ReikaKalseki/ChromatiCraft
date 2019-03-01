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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;

public interface ProgressionTrigger {

	//public boolean canTrigger(EntityPlayer ep, World world, int x, int y, int z);

	public ProgressStage[] getTriggers(EntityPlayer ep, World world, int x, int y, int z);

}
