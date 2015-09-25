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

import java.util.Collection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader.AssetData;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader.RemoteAsset;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader.RemoteAssetRepository;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MusicLoader {

	public static final MusicLoader instance = new MusicLoader();

	private static final String mcDir = DragonAPICore.getMinecraftDirectoryString();

	private static final String hashURL = "https://cache.techjargaming.com/reika/list.php?dir=ccmusic/";
	//private static final String musicURL = "https://cache.techjargaming.com";

	public static final String musicPath = mcDir+"/mods/Reika/ChromatiCraft/Music/";

	private final MusicFolder folder = new MusicFolder();

	private MusicLoader() {

	}

	public void registerAssets() {
		RemoteAssetLoader.instance.registerAssets(folder);
	}

	private static class MusicFolder extends RemoteAssetRepository {

		private MusicFolder() {
			super(ChromatiCraft.instance);
		}

		@Override
		public String getRepositoryURL() {
			return hashURL;
		}

		@Override
		protected RemoteAsset parseAsset(String line) {
			return new MusicAsset(this);
		}

		@Override
		public String getDisplayName() {
			return "ChromatiCraft Dimension Music";
		}

		private Collection<RemoteAsset> getMusicAssets() {
			return this.getAssets();
		}

		@Override
		public String getLocalPath() {
			return musicPath;
		}
	}

	private static class MusicAsset extends RemoteAsset {

		private String track;

		private MusicAsset(MusicFolder f) {
			super(ChromatiCraft.instance, f);
		}

		@Override
		protected AssetData constructData(String line) {
			String[] parts = line.split("\\|");
			String path = parts[0];
			String hash = parts[1];
			String size = parts[2];
			String name = path.substring(path.lastIndexOf('/')+1, path.length()-4);
			return new AssetData(this, path, name, hash, Long.parseLong(size));
		}

		@Override
		public String getDisplayName() {
			return "Music Track '"+track+"'";
		}

		@Override
		public String setFilename(String line) {
			String[] parts = line.split("\\|");
			String path = parts[0];
			String name = path.substring(path.lastIndexOf('/')+1, path.length()-4);
			track = name;
			return track;
		}

		@Override
		public String setExtension(String line) {
			String[] parts = line.split("\\|");
			String path = parts[0];
			String name = path.substring(path.lastIndexOf('/')+1, path.length()-4);
			return path.substring(path.length()-3, path.length());
		}
	}

	public Collection<String> getMusicFiles() {
		return folder.getAvailableResources();
	}

}
