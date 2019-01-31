/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Lore;

import java.util.Collection;
import java.util.Collections;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaOverlays;
import Reika.ChromatiCraft.Magic.Lore.KeyAssemblyPuzzle.TileGroup;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;


public class LoreManager {

	public static final LoreManager instance = new LoreManager();

	private static final String NBT_TAG = "loretowers";

	private KeyAssemblyPuzzle puzzle;
	private RosettaStone rosetta;

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
		if (!Towers.initialized(world))
			Towers.loadPositions(world, 64*16*2);
	}

	/** Block coords */
	public Towers getNearestTower(World world, double x, double z) {
		this.initTowers(world);
		Towers ret = null;
		double mind = Double.POSITIVE_INFINITY;
		for (int i = 0; i < Towers.towerList.length; i++) {
			Towers t = Towers.towerList[i];
			ChunkCoordIntPair p = t.getRootPosition();
			Coordinate p2 = t.getGeneratedLocation();
			double d1 = ReikaMathLibrary.py3d(x-p.chunkXPos, 0, z-p.chunkZPos);
			double d2 = p2 != null ? p2.getDistanceTo(x, p2.yCoord, z) : Double.POSITIVE_INFINITY;
			double d = Math.min(d1, d2);
			if (ret == null || d < mind) {
				ret = t;
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
		NBTTagCompound NBT = ChromaResearchManager.instance.getRootNBTTag(ep);
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);
		tag.setBoolean(t.name(), set);
		NBT.setTag(NBT_TAG, tag);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	public boolean hasPlayerScanned(EntityPlayer ep, Towers t) {
		NBTTagCompound NBT = ChromaResearchManager.instance.getRootNBTTag(ep);
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
		NBTTagCompound NBT = ChromaResearchManager.instance.getRootNBTTag(ep);
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);
		return tag.getBoolean("complete");
	}

	public void setBoardCompletion(EntityPlayer ep, boolean set) {
		NBTTagCompound NBT = ChromaResearchManager.instance.getRootNBTTag(ep);
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
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.LOREPUZZLECOMPLETE.ordinal(), PacketTarget.server);
		}
		else {
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
		}
	}

	public RosettaStone getOrCreateRosetta(EntityPlayer ep) {
		if (rosetta == null)
			rosetta = new RosettaStone(ep);
		return rosetta;
	}

	public void clearOnLogout() {
		rosetta = null;
		puzzle = null;
		towerGroups.clear();
	}

	public void sendTowersToClient(EntityPlayerMP ep) {
		this.initTowers(DimensionManager.getWorld(0));
		NBTTagCompound data = new NBTTagCompound();
		NBTTagList li = new NBTTagList();
		for (Towers t : Towers.towerList) {
			NBTTagCompound tag = new NBTTagCompound();
			ChunkCoordIntPair p = t.getRootPosition();
			tag.setInteger("x", ~p.chunkXPos); //very weak but nontrivial-if-manual encryption
			tag.setInteger("z", ~p.chunkZPos);
			tag.setInteger("idx", t.ordinal());
			li.appendTag(tag);
		}
		data.setTag("list", li);
		ReikaPacketHelper.sendNBTPacket(ChromatiCraft.packetChannel, ChromaPackets.TOWERLOC.ordinal(), data, new PacketTarget.PlayerTarget(ep));
	}

	@SideOnly(Side.CLIENT)
	public void readTowersFromServer(NBTTagCompound data) {
		NBTTagList li = data.getTagList("list", NBTTypes.COMPOUND.ordinal());
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			int x = ~tag.getInteger("x");
			int z = ~tag.getInteger("z");
			int idx = tag.getInteger("idx");
			Towers.towerList[idx].setLocationFromServer(x, z);
		}
	}

}
