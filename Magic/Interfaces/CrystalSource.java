/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Registry.CrystalElement;

public interface CrystalSource extends CrystalTransmitter, LumenTile {

	public int getTransmissionStrength();

	public boolean drain(CrystalElement e, int amt);

	/** Higher number = higher priority */
	public int getSourcePriority();

	public boolean canTransmitTo(CrystalReceiver te);

	public void onUsedBy(EntityPlayer ep, CrystalElement e);

	public boolean playerCanUse(EntityPlayer ep);

}
