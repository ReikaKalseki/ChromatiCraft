/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class ProgressionManager {

	public static final ProgressionManager instance = new ProgressionManager();

	private static final String NBT_TAG = "Chroma_Progression";

	public static enum ProgressStage {

		ENTRY(),
		CRYSTALS(), //placeholders
		RUNEUSE(),
		FINAL();

		private static final ProgressStage[] list = values();

	}

	public ProgressStage getPlayerProgressionStage(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		int val = Math.min(Math.max(0, nbt.getInteger(NBT_TAG)), ProgressStage.list.length-1);
		return ProgressStage.list[val];
	}

	public boolean isPlayerAtStage(EntityPlayer ep, ProgressStage s) {
		return this.getPlayerProgressionStage(ep).ordinal() >= s.ordinal();
	}

	public boolean progressPlayer(EntityPlayer ep) {
		return this.setPlayerStage(ep, this.getPlayerProgressionStage(ep).ordinal()+1);
	}

	public boolean setPlayerStage(EntityPlayer ep, int val) {
		if (val < 0 || val >= ProgressStage.list.length)
			return false;
		this.setPlayerStage(ep, ProgressStage.list[val]);
		return true;
	}

	public void setPlayerStage(EntityPlayer ep, ProgressStage s) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		nbt.setInteger(NBT_TAG, s.ordinal());
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	public void maxPlayerProgression(EntityPlayer ep) {
		this.setPlayerStage(ep, ProgressStage.list[ProgressStage.list.length-1]);
	}

}
