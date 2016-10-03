/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.IO.NBTFile;


public class ProgressionCacher implements PlayerTracker {

	public static final ProgressionCacher instance = new ProgressionCacher();

	private final String baseFilepath;

	private final HashMap<UUID, ProgressCache> progressCache = new HashMap();

	private ProgressionCacher() {
		baseFilepath = DragonAPICore.getMinecraftDirectoryString()+"/ChromatiCraft_Data/ProgressionCache/";
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

	public void updateProgressCache(EntityPlayer ep) {
		ProgressCache pc = progressCache.get(ep.getUniqueID());
		if (pc == null) {
			pc = new ProgressCache(ep);
			progressCache.put(ep.getUniqueID(), pc);
		}
		else {
			pc.update(ep);
		}
		this.savePlayer(ep);
	}

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

	public static class ProgressCache {

		private final HashSet<ProgressStage> cache = new HashSet();
		private final UUID uid;

		private ProgressCache(EntityPlayer ep) {
			this(ep.getUniqueID());
			this.update(ep);
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
