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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.SequenceMap;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ProgressionManager {

	public static final ProgressionManager instance = new ProgressionManager();

	private static final String NBT_TAG = "Chroma_Progression";
	private static final String NBT_TAG2 = "Chroma_Element_Discovery";

	private final SequenceMap<ProgressStage> progressMap = new SequenceMap();

	public static enum ProgressStage {

		CRYSTALS(), //Found a crystal
		RUNEUSE(), //Placed runes
		MULTIBLOCK(), //Assembled a multiblock
		PYLON(), //Found pylon
		LINK(), //Made a network connection/high-tier crafting
		FINAL();

	}

	private ProgressionManager() {
		progressMap.addParent(ProgressStage.RUNEUSE, ProgressStage.CRYSTALS);
		progressMap.addParent(ProgressStage.MULTIBLOCK, ProgressStage.RUNEUSE);
		progressMap.addParent(ProgressStage.LINK, ProgressStage.MULTIBLOCK);
		progressMap.addParent(ProgressStage.LINK, ProgressStage.PYLON);
	}

	private Collection<ProgressStage> getPlayerData(EntityPlayer ep) {
		NBTTagList li = this.getNBTList(ep);
		Collection<ProgressStage> c = new ArrayList();
		ProgressStage[] list = ProgressStage.values();
		for (int i = 0; i < li.tagCount(); i++) {
			int val = ((NBTTagInt)li.tagList.get(i)).func_150287_d();
			c.add(list[val]);
		}
		return c;
	}

	private NBTTagList getNBTList(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		if (!nbt.hasKey(NBT_TAG))
			nbt.setTag(NBT_TAG, new NBTTagList());
		NBTTagList li = nbt.getTagList(NBT_TAG, NBTTypes.INT.ID);
		return li;
	}

	public boolean isPlayerAtStage(EntityPlayer ep, ProgressStage s) {
		return this.getPlayerData(ep).contains(s);
	}

	public boolean stepPlayerTo(EntityPlayer ep, ProgressStage s) {
		Collection<ProgressStage> c = progressMap.getParents(s);
		for (ProgressStage s2 : c) {
			if (!this.isPlayerAtStage(ep, s2))
				return false;
		}
		this.setPlayerStage(ep, s, true);
		return true;
	}

	public boolean setPlayerStage(EntityPlayer ep, int val, boolean set) {
		if (val < 0 || val >= ProgressStage.values().length)
			return false;
		this.setPlayerStage(ep, ProgressStage.values()[val], set);
		return true;
	}

	public void setPlayerStage(EntityPlayer ep, ProgressStage s, boolean set) {
		NBTTagList li = this.getNBTList(ep);
		NBTBase tag = new NBTTagInt(s.ordinal());
		if (set) {
			li.appendTag(tag);
		}
		else {
			li.tagList.remove(tag);
			Collection<ProgressStage> c = progressMap.getRecursiveChildren(s);
			for (ProgressStage s2 : c) {
				NBTBase tag2 = new NBTTagInt(s2.ordinal());
				li.tagList.remove(tag2);
			}
		}
		ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(NBT_TAG, li);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
		this.updateChunks(ep);
	}

	public void resetPlayerProgression(EntityPlayer ep) {
		NBTTagList li = this.getNBTList(ep);
		li.tagList.clear();
		ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(NBT_TAG, li);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
		this.updateChunks(ep);
	}

	public void maxPlayerProgression(EntityPlayer ep) {
		ProgressStage[] list = ProgressStage.values();
		for (int i = 0; i < list.length; i++) {
			this.setPlayerStage(ep, list[i], true);
		}
	}

	public void setPlayerDiscoveredColor(EntityPlayer ep, CrystalElement e, boolean disc) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(NBT_TAG2);
		nbt.setBoolean(e.name(), disc);
		this.updateChunks(ep);
	}

	private void updateChunks(EntityPlayer ep) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			ReikaRenderHelper.rerenderAllChunks();
		else
			ReikaPacketHelper.sendUpdatePacket(DragonAPIInit.packetChannel, PacketIDs.RERENDER.ordinal(), ep.worldObj, 0, 0, 0);
	}

	public boolean hasPlayerDiscoveredColor(EntityPlayer ep, CrystalElement e) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep).getCompoundTag(NBT_TAG2);
		return nbt.getBoolean(e.name());
	}

}
