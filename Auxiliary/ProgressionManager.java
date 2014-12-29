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
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.FakePlayer;
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
		DYETREE(), //Harvest a dye tree
		MULTIBLOCK(), //Assembled a multiblock
		RUNEUSE(), //Placed runes
		PYLON(), //Found pylon
		LINK(), //Made a network connection/high-tier crafting
		CHARGE(), //charge from a pylon
		ABILITY(), //use an ability
		RAINBOWLEAF(), //harvest a rainbow leaf
		CHROMA(), //step in liquid chroma
		STONES(), //craft all elemental stones together
		SHOCK(), //get hit by a pylon
		NETHER(), //go to the nether
		;

		public boolean stepPlayerTo(EntityPlayer ep) {
			return instance.stepPlayerTo(ep, this);
		}
	}

	private ProgressionManager() {
		progressMap.addParent(ProgressStage.RUNEUSE,	ProgressStage.CRYSTALS);
		progressMap.addParent(ProgressStage.MULTIBLOCK,	ProgressStage.RUNEUSE);
		progressMap.addParent(ProgressStage.LINK,		ProgressStage.MULTIBLOCK);
		progressMap.addParent(ProgressStage.LINK, 		ProgressStage.PYLON);
		progressMap.addParent(ProgressStage.CHARGE, 	ProgressStage.PYLON);
		progressMap.addParent(ProgressStage.CHARGE, 	ProgressStage.CRYSTALS);
		progressMap.addParent(ProgressStage.ABILITY, 	ProgressStage.CHARGE);
		progressMap.addParent(ProgressStage.ABILITY, 	ProgressStage.MULTIBLOCK);
		progressMap.addParent(ProgressStage.STONES, 	ProgressStage.MULTIBLOCK);
	}

	private Collection<ProgressStage> getPlayerData(EntityPlayer ep) {
		NBTTagList li = this.getNBTList(ep);
		Collection<ProgressStage> c = new ArrayList();
		ProgressStage[] list = ProgressStage.values();
		Iterator<NBTTagInt> it = li.tagList.iterator();
		while (it.hasNext()) {
			int val = it.next().func_150287_d();
			if (val < list.length)
				c.add(list[val]);
			else
				it.remove();
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

	private boolean stepPlayerTo(EntityPlayer ep, ProgressStage s) {
		if (ep instanceof FakePlayer)
			return false;
		if (this.isPlayerAtStage(ep, s))
			return false;
		Collection<ProgressStage> c = progressMap.getParents(s);
		for (ProgressStage s2 : c) {
			if (!this.isPlayerAtStage(ep, s2))
				return false;
		}
		this.setPlayerStage(ep, s, true);
		return true;
	}

	public boolean setPlayerStage(EntityPlayer ep, int val, boolean set) {
		if (ep instanceof FakePlayer)
			return false;
		if (val < 0 || val >= ProgressStage.values().length)
			return false;
		this.setPlayerStage(ep, ProgressStage.values()[val], set);
		return true;
	}

	public void setPlayerStage(EntityPlayer ep, ProgressStage s, boolean set) {
		if (ep instanceof FakePlayer)
			return;
		NBTTagList li = this.getNBTList(ep);
		NBTBase tag = new NBTTagInt(s.ordinal());
		boolean flag = false;
		if (set) {
			if (!li.tagList.contains(tag)) {
				flag = true;
				li.appendTag(tag);
			}
		}
		else {
			if (li.tagList.contains(tag)) {
				flag = true;
				li.tagList.remove(tag);
				Collection<ProgressStage> c = progressMap.getRecursiveChildren(s);
				for (ProgressStage s2 : c) {
					NBTBase tag2 = new NBTTagInt(s2.ordinal());
					li.tagList.remove(tag2);
				}
			}
		}
		if (flag) {
			ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(NBT_TAG, li);
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			this.updateChunks(ep);
		}
	}

	public void resetPlayerProgression(EntityPlayer ep) {
		NBTTagList li = this.getNBTList(ep);
		li.tagList.clear();
		ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(NBT_TAG, li);
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			this.setPlayerDiscoveredColor(ep, CrystalElement.elements[i], false);
		}
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
		this.updateChunks(ep);
	}

	public void maxPlayerProgression(EntityPlayer ep) {
		ProgressStage[] list = ProgressStage.values();
		for (int i = 0; i < list.length; i++) {
			this.setPlayerStage(ep, list[i], true);
		}
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			this.setPlayerDiscoveredColor(ep, CrystalElement.elements[i], true);
		}
	}

	public void setPlayerDiscoveredColor(EntityPlayer ep, CrystalElement e, boolean disc) {
		//ReikaJavaLibrary.pConsole(this.getPlayerData(ep));
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = nbt.getCompoundTag(NBT_TAG2);
		tag.setBoolean(e.name(), disc);
		nbt.setTag(NBT_TAG2, tag);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
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
