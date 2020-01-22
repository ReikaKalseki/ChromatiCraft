/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Progression;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Instantiable.IO.NBTFile;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

import cpw.mods.fml.common.FMLLog;


public class ProgressionLoadHandler implements PlayerTracker {

	public static final ProgressionLoadHandler instance = new ProgressionLoadHandler();

	private String baseFilepath;

	private final HashMap<UUID, ProgressCache> progressCache = new HashMap();

	private final PlayerMap<NBTTagCompound> cachedBackup = new PlayerMap();

	private ProgressionLoadHandler() {

	}

	public void initLevelData(MinecraftServer instance) {
		baseFilepath = DimensionManager.getCurrentSaveRootDirectory()+"/ChromatiCraft_Data/ProgressionCache/";
		progressCache.clear();
	}

	private final String getFilepath(EntityPlayer ep) {
		return this.getFilepath(ep.getUniqueID());
	}

	private final String getFilepath(UUID uid) {
		return baseFilepath+uid.toString()+".dat";
	}

	public void load() {
		File f = new File(baseFilepath);
		if (f.exists()) {
			ArrayList<File> li = ReikaFileReader.getAllFilesInFolder(f, ".dat");
			for (File in : li) {
				ProgressCache pc = ProgressCache.readFromFile(in);
				if (pc != null) {
					progressCache.put(pc.uid, pc);
				}
			}
		}
		else {
			f.getParentFile().mkdirs();
			f.mkdir();
		}
	}

	public void saveAll() {
		ReikaFileReader.emptyDirectory(new File(baseFilepath));
		for (ProgressCache pc : progressCache.values()) {
			File f = new File(this.getFilepath(pc.uid));
			pc.writeToFile(f);
		}
	}

	private void savePlayer(EntityPlayer ep) {
		ProgressCache pc = progressCache.get(ep.getUniqueID());
		if (pc != null) {
			File f = new File(this.getFilepath(ep.getUniqueID()));
			ReikaFileReader.clearFile(f);
			pc.writeToFile(f);
		}
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		this.updateProgressCache(player);
		ProgressionLinking.instance.attemptSyncAllInGroup(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		this.updateProgressCache(player);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player, int dimFrom, int dimTo) {
		this.updateProgressCache(player);
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		this.updateProgressCache(player);
	}

	public void clearProgressCache(EntityPlayer ep) {
		progressCache.remove(ep.getUniqueID());
	}

	public void updateProgressCache(EntityPlayer ep) {
		ProgressCache pc = progressCache.get(ep.getUniqueID());
		if (pc == null) {
			pc = new ProgressCache(ep);
			progressCache.put(ep.getUniqueID(), pc);
		}
		else {
			if (pc.hasMoreProgressionThan(ep)) { //progression lost?
				pc.copyTo(ep);
				ChromatiCraft.logger.log("Restoring progression for "+ep.getCommandSenderName()+" from cache, as it had more progression than they did!");
				FMLLog.bigWarning("ChromatiCraft: Player %s just lost some of their progression!", ep.getCommandSenderName());
			}
			pc.update(ep);
		}
		this.savePlayer(ep);
	}

	@Deprecated
	public ProgressCache getProgressCache(World world, UUID uid) {
		ProgressCache pc = progressCache.get(uid);
		if (pc == null) {
			EntityPlayer ep = world.func_152378_a(uid);
			if (ep != null) {
				pc = new ProgressCache(ep);
				progressCache.put(uid, pc);
			}
		}
		else if (!pc.uid.equals(uid)) {
			pc = null;
			progressCache.remove(uid);
		}
		return pc;
	}

	public NBTTagCompound attemptToLoadBackup(EntityPlayer ep) {
		if (!ReikaPlayerAPI.isFake(ep))
			ChromatiCraft.logger.log("Attempting to load backup progression for "+ep);
		NBTTagCompound tag = cachedBackup.get(ep);
		if (tag == null) {
			File f = this.getBackupFile(ep);
			if (f.exists() && f.length() > 0) {
				try {
					tag = ReikaFileReader.readUncompressedNBT(f);
					cachedBackup.put(ep, tag);
				}
				catch (Exception e) {
					e.printStackTrace();
					ChromatiCraft.logger.logError("Could not read progression data backup for "+ep.getCommandSenderName()+"!");
				}
			}
		}
		return tag;
	}

	public void updateBackup(EntityPlayer ep) {
		File f = this.getBackupFile(ep);
		if (!f.exists())
			f.getParentFile().mkdirs();
		if (f.exists())
			f.delete();
		try {
			f.createNewFile();
			cachedBackup.put(ep, ReikaPlayerAPI.getDeathPersistentNBT(ep));
			ReikaFileReader.writeUncompressedNBT(ReikaPlayerAPI.getDeathPersistentNBT(ep), f);
		}
		catch (IOException e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not save progression data backup for "+ep.getCommandSenderName()+"!");
		}
	}

	private File getBackupFile(EntityPlayer ep) {
		return new File(DimensionManager.getCurrentSaveRootDirectory()+"/ChromatiCraft_Data/ProgressionBackup", ep.getUniqueID().toString()+".dat");
	}

	public static class ProgressCache {

		private final HashSet<ProgressStage> cache = new HashSet();
		private final UUID uid;

		private ProgressCache(EntityPlayer ep) {
			this(ep.getUniqueID());
			this.update(ep);
		}

		private void copyTo(EntityPlayer ep) {
			HashSet<ProgressStage> set = new HashSet(cache);
			for (ProgressStage p : set) {
				p.forceOnPlayer(ep, false);
			}
		}

		private boolean hasMoreProgressionThan(EntityPlayer ep) {
			for (ProgressStage p : new HashSet<ProgressStage>(cache)) { //CME protection
				if (!p.isPlayerAtStage(ep))
					return true;
			}
			return false;
		}

		private ProgressCache(UUID id) {
			uid = id;
		}

		public void update(EntityPlayer ep) {
			cache.clear();
			cache.addAll(ProgressionManager.instance.getStagesFor(ep));
			//ReikaJavaLibrary.pConsole(cache);
		}

		public boolean containsProgress(ProgressStage p) {
			return cache.contains(p);
		}

		private static ProgressCache readFromFile(File f) {
			ProgressFile pf = new ProgressFile(f);
			try {
				pf.load();
				ProgressCache pc = new ProgressCache(pf.uid);
				for (String s : pf.entries) {
					ProgressStage p = ProgressStage.valueOf(s);
					pc.cache.add(p);
				}
				return pc;
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not load cached player progress: "+f.getName());
				e.printStackTrace();
				return null;
			}
		}

		private void writeToFile(File f) {
			ProgressFile pf = new ProgressFile(f);
			pf.uid = uid;
			for (ProgressStage p : cache) {
				pf.entries.add(p.name());
			}
			try {
				pf.save();
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not save cached player progress: "+f.getName());
				e.printStackTrace();
			}
		}

		@Override
		public String toString() {
			return uid+": "+cache.toString();
		}

	}

	private static class ProgressFile extends NBTFile {

		private UUID uid;
		private final HashSet<String> entries = new HashSet();

		private ProgressFile(File f) {
			super(f);
			encryptData = true;
		}

		@Override
		protected void readHeader(NBTTagCompound header) {
			uid = UUID.fromString(header.getString("id"));
		}

		@Override
		protected void readData(NBTTagList li) {
			for (Object o : li.tagList) {
				String s = ((NBTTagCompound)o).getString("tag");
				entries.add(s);
			}
		}

		@Override
		protected void readExtraData(NBTTagCompound extra) {

		}

		@Override
		protected void writeHeader(NBTTagCompound header) {
			header.setString("id", uid.toString());
		}

		@Override
		protected void writeData(NBTTagList li) {
			for (String s : entries) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("tag", s);
				li.appendTag(tag);
			}
		}

		@Override
		protected NBTTagCompound writeExtraData() {
			return null;
		}

	}

}
