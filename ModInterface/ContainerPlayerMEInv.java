/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;


public class ContainerPlayerMEInv extends ContainerPlayer {

	public ContainerPlayerMEInv(EntityPlayer ep) {
		super(ep.inventory, !ep.worldObj.isRemote, ep);
	}

}
