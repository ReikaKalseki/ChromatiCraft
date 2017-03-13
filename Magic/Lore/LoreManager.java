/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Lore;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaOverlays;
import Reika.ChromatiCraft.Magic.Lore.KeyAssemblyPuzzle.TileGroup;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class LoreManager {

	public static final LoreManager instance = new LoreManager();

	private static final String NBT_TAG = "loretowers";

	private KeyAssemblyPuzzle puzzle;

	private final MultiMap<Towers, TileGroup> towerGroups = new MultiMap().setNullEmpty();

	private LoreManager() {

	}

	public void triggerLore(EntityPlayer ep, Towers t) {
		this.setPlayerScanned(ep, t, true);
		if (ep instanceof EntityPlayerMP) {
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.LORENOTE.ordinal(), (EntityPlayerMP)ep, t.ordinal());
		}
	}

	@SideOnly(Side.CLIENT)
	public void addLoreNote(EntityPlayer ep, Towers t) {
		this.setPlayerScanned(ep, t, true);
		ReikaSoundHelper.playClientSound(ChromaSounds.LOREHEX, ep, 1, 1, false);
		ChromaOverlays.instance.addLoreNote(ep, t);
	}

	/** In block coords */
	public Towers getTower(World world, int cx, int cz) {
		this.initTowers(world);
		return Towers.getTowerForChunk(cx, cz);
	}

	public void initTowers(World world) {
		if (!Towers.initialized())
			Towers.loadPositions(world, 64*16);
	}

	/** In block coords */
	public ChunkCoordIntPair getNearestTowerChunk(World world, int cx, int cz) {
		this.initTowers(world);
		ChunkCoordIntPair ret = null;
		double mind = Double.POSITIVE_INFINITY;
		for (int i = 0; i < Towers.towerList.length; i++) {
			Towers t = Towers.towerList[i];
			ChunkCoordIntPair p = t.getRootPosition();
			double d = ReikaMathLibrary.py3d(cx-p.chunkXPos, 0, cz-p.chunkZPos);
			if (ret == null || d < mind) {
				ret = p;
				mind = d;
			}
		}
		return ret;
	}

	public KeyAssemblyPuzzle getPuzzle(EntityPlayer ep) {
		this.initTowers(ep.worldObj);
		if (puzzle == null || puzzle.getSeed() != KeyAssemblyPuzzle.calcSeed(ep))
			puzzle = KeyAssemblyPuzzle.generatePuzzle(ep);
		return puzzle;
	}

	public void preparePuzzle(EntityPlayer ep) {
		this.initTowers(ep.worldObj);
		if (towerGroups.isEmpty()) {
			for (int i = 0; i < Towers.towerList.length; i++) {
				Towers t = Towers.towerList[i];
				this.getGroupsForTower(ep, t);
			}
		}
	}

	public Collection<TileGroup> getGroupsForTower(EntityPlayer ep, Towers t) {
		Collection<TileGroup> c = towerGroups.get(t);

		if (c == null) {
			KeyAssemblyPuzzle p = this.getPuzzle(ep);
			c = p.getRandomGroupsForTower(t);
			towerGroups.put(t, c);
		}
		return Collections.unmodifiableCollection(c);
	}

	public void setPlayerScanned(EntityPlayer ep, Towers t, boolean set) {
		NBTTagCompound NBT = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);
		tag.setBoolean(t.name(), set);
		NBT.setTag(NBT_TAG, tag);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	public boolean hasPlayerScanned(EntityPlayer ep, Towers t) {
		NBTTagCompound NBT = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);
		return tag.getBoolean(t.name());
	}

	public boolean hasScannedAllTowers(EntityPlayer ep) {
		for (int i = 0; i < Towers.towerList.length; i++) {
			Towers t = Towers.towerList[i];
			if (!this.hasPlayerScanned(ep, t))
				return false;
		}
		return true;
	}

	public boolean hasPlayerCompletedBoard(EntityPlayer ep) {
		NBTTagCompound NBT = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);
		return tag.getBoolean("complete");
	}

	public void setBoardCompletion(EntityPlayer ep, boolean set) {
		NBTTagCompound NBT = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);
		tag.setBoolean("complete", set);
		NBT.setTag(NBT_TAG, tag);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	public void completeBoard(EntityPlayer ep) {
		this.setBoardCompletion(ep, true);
		if (ep.worldObj.isRemote) {
			ReikaSoundHelper.playClientSound(ChromaSounds.LORECOMPLETE, ep, 1, 1, false);
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.LOREPUZZLECOMPLETE.ordinal(), new PacketTarget.ServerTarget());
		}
		else {
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
		}
	}

}
