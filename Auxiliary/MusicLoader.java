/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.PopupWriter;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.IO.ReikaFileReader.ConnectionErrorHandler;
import Reika.DragonAPI.IO.ReikaFileReader.FileReadException;
import Reika.DragonAPI.IO.ReikaFileReader.FileWriteException;
import Reika.DragonAPI.IO.ReikaFileReader.HashType;
import Reika.DragonAPI.IO.ReikaFileReader.WriteCallback;
import Reika.DragonAPI.Libraries.IO.ReikaFormatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MusicLoader implements ConnectionErrorHandler {

	private static final String mcDir = DragonAPICore.getMinecraftDirectoryString();

	private static final String hashURL = "https://cache.techjargaming.com/reika/list.php?dir=ccmusic/";
	private static final String musicURL = "https://cache.techjargaming.com";

	public static final String musicPath = mcDir+"/mods/Reika/ChromatiCraft/Music/";

	public static final MusicLoader instance = new MusicLoader();

	private final ArrayList<MusicData> downloadingMusic = new ArrayList();

	private MusicDownloader downloader;
	private Thread downloadThread;

	private boolean nonAccessible = false;

	private final ArrayList<String> bigWarnings = new ArrayList();

	private MusicLoader() {

	}

	public void checkAndStartDownloads() {
		URL url = null;
		try {
			url = URI.create(hashURL).toURL();
		}
		catch (MalformedURLException e) {
			this.logError("Music URL invalid", true);
			e.printStackTrace();
		}
		ArrayList<String> li = ReikaFileReader.getFileAsLines(url, 10000, true, this, null);
		for (String s : li) {
			MusicData dat = MusicData.construct(s);
			if (dat.match()) {

			}
			else {
				downloadingMusic.add(dat);
			}
		}
		if (!nonAccessible && !downloadingMusic.isEmpty()) {
			ChromatiCraft.logger.log("Some music tracks need to be redownloaded:");
			downloader = new MusicDownloader();
			for (MusicData dat : downloadingMusic) {
				ChromatiCraft.logger.log("Track '"+dat.getDisplayName()+"' is either missing or out of date. Redownloading.");
				downloader.totalSize += dat.size;
			}
			ChromatiCraft.logger.log("Projected total download size: "+downloader.totalSize+" bytes");
			downloadThread = new Thread(downloader, "ChromatiCraft Music Download");
			downloadThread.start();
		}
	}

	public float getDownloadProgress() {
		return downloader != null ? downloader.getCompletion() : 1F;
	}

	public boolean isDownloadComplete() {
		return downloadThread == null || !downloadThread.isAlive();
	}

	@Override
	public void onServerRedirected() {
		this.logError("Music Server access redirected!?", true);
	}

	@Override
	public void onNoInternet() {
		this.logError("Is your internet disconnected?", false);
	}

	@Override
	public void onServerNotFound() {
		this.logError("Music Server not found!", true);
	}

	@Override
	public void onTimedOut() {
		this.logError("Timed Out", false);
	}

	public void onClientReceiveWarning(EntityPlayer ep) {
		for (String s : bigWarnings) {
			String sg = s+" the file server may be inaccessible. Check your internet settings, and please notify Reika if the server is not accessible.";
			PopupWriter.instance.addMessage(sg);
		}
	}

	private void logError(String msg, boolean bigWarn) {
		nonAccessible = true;
		ChromatiCraft.logger.logError("Error accessing online music data file: "+msg);
		if (bigWarn) {
			bigWarnings.add("Downloading the remote music assets failed: "+msg);
		}
	}

	private static class MusicData {

		private final String track;
		private final String path;
		private final long size;
		private final String hash;

		private MusicData(String p, String tr, String h, long s) {
			track = tr;
			path = p;
			size = s;
			hash = h;
		}

		public String getDisplayName() {
			return ReikaStringParser.capFirstChar(track);
		}

		private static MusicData construct(String line) {
			String[] parts = line.split("\\|");
			String path = parts[0];
			String hash = parts[1];
			String size = parts[2];
			String name = path.substring(path.lastIndexOf('/')+1, path.length()-4);
			return new MusicData(path, name, hash, Long.parseLong(size));
		}

		private String getLocalHash() {
			File f = new File(this.getLocalPath());
			return f.exists() ? ReikaFileReader.getHash(f, HashType.MD5) : "";
		}

		private String getLocalPath() {
			return musicPath+track+".ogg";
		}

		private boolean match() {
			return this.getLocalHash().equals(hash);
		}

	}

	public static class MusicDownloader implements Runnable, WriteCallback {

		private long totalSize = 0;
		private long downloaded = 0;

		@Override
		public void run() {
			long time = System.currentTimeMillis();
			ChromatiCraft.logger.log("Download thread starting...");
			for (MusicData dat : instance.downloadingMusic) {
				this.tryDownload(dat, 5);
			}
			long duration = System.currentTimeMillis()-time;
			ChromatiCraft.logger.log("All downloads complete. Elapsed time: "+ReikaFormatHelper.millisToHMSms(duration));
		}

		public float getCompletion() {
			return (float)downloaded/totalSize;
		}

		private void tryDownload(MusicData dat, int max) {
			for (int i = 0; i < max; i++) {
				try {
					this.download(dat);
					break;
				}
				catch (FileReadException e) {
					boolean end = i == max-1;
					String text = end ? "Skipping file." : "Retrying...";
					ChromatiCraft.logger.logError("Could not read music track '"+dat.getDisplayName()+"'. "+text);
					e.printStackTrace();
					if (end)
						break;
				}
				catch (FileWriteException e) {
					ChromatiCraft.logger.logError("Could not save music track '"+dat.getDisplayName()+"'. Skipping file.");
					e.printStackTrace();
					break;
				}
				catch (IOException e) {
					ChromatiCraft.logger.logError("Could not download music track '"+dat.getDisplayName()+"'. Skipping file.");
					e.printStackTrace();
					break;
				}
			}
		}

		private void download(MusicData dat) throws IOException {
			String local = dat.getLocalPath();
			File f = new File(local);
			f.getParentFile().mkdirs();
			f.delete();
			f.createNewFile();
			URLConnection c = new URL(dat.path).openConnection();
			InputStream in = c.getInputStream();
			OutputStream out = new FileOutputStream(f);

			long time = System.currentTimeMillis();
			ReikaFileReader.copyFile(in, out, 4096, this);
			long duration = System.currentTimeMillis()-time;

			ChromatiCraft.logger.log("Download of '"+dat.getDisplayName()+"' complete. Elapsed time: "+ReikaFormatHelper.millisToHMSms(duration));

			in.close();
			out.close();
		}

		@Override
		public void onWrite(byte[] data) {
			downloaded += data.length;
		}

	}

}
