package Reika.ChromatiCraft.World.Dimension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Auxiliary.MonumentCompletionRitual;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Instantiable.IO.CustomMusic;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import paulscode.sound.StreamThread;


public class ChromaDimensionalAudioHandler {

	private static final Random rand = new Random();

	static final ArrayList<DimensionMusic> music = new ArrayList();
	private static final ArrayList<DimensionMusic> freshTracks = new ArrayList();

	private static int musicCooldown;

	@SideOnly(Side.CLIENT)
	static ISound currentMusic;

	@SideOnly(Side.CLIENT)
	static void playMusic() {
		if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			if (!MonumentCompletionRitual.areRitualsRunning()) {
				SoundHandler sh = Minecraft.getMinecraft().getSoundHandler();
				StreamThread th = ReikaSoundHelper.getStreamingThread(sh);
				if (th == null || !th.isAlive()) {
					sh.stopSounds();
					ReikaSoundHelper.restartStreamingSystem(sh);
				}
				//ReikaJavaLibrary.pConsole(s.path+":"+sh.isSoundPlaying(s));
				if (currentMusic != null && ReikaObfuscationHelper.isDeObfEnvironment() && Keyboard.isKeyDown(Keyboard.KEY_END)) {
					sh.stopSound(currentMusic);
					musicCooldown = 0;
				}
				if (currentMusic != null && sh.isSoundPlaying(currentMusic)) {
					return;
				}
				if (musicCooldown > 0) {
					musicCooldown--;
					return;
				}

				DimensionMusic s = selectTrack(Minecraft.getMinecraft().thePlayer);
				if (s != null)
					s.play(sh);

				currentMusic = s;
				musicCooldown = 300+rand.nextInt(900);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static DimensionMusic selectTrack(EntityPlayer ep) {
		if (freshTracks.isEmpty()) {
			freshTracks.addAll(music);
			Collections.shuffle(freshTracks);
		}
		DimensionMusic s = freshTracks.remove(0);
		while (!s.canPlay(ep) && !freshTracks.isEmpty()) {
			s = freshTracks.remove(0);
		}
		return s.canPlay(ep) ? s : null;
	}

	@SideOnly(Side.CLIENT)
	static void ensureSoundOn() {
		if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			for (SoundCategory s : SoundCategory.values()) {
				if (requiresSounds(s)) {
					GameSettings gs = Minecraft.getMinecraft().gameSettings;
					gs.setSoundLevel(s, Math.max(gs.getSoundLevel(s), 0.15F)); //15%
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static boolean requiresSounds(SoundCategory s) {
		switch(s) {
			case AMBIENT:
			case ANIMALS:
			case BLOCKS:
			case MASTER:
			case PLAYERS:
				//case WEATHER:
				return true;
			default:
				return false;
		}
	}

	static class DimensionMusic extends CustomMusic {

		private final boolean isCompletionGated;

		DimensionMusic(String path, boolean b) {
			super(path);

			isCompletionGated = b;
		}

		public final boolean canPlay(EntityPlayer ep) {
			return isCompletionGated ? ProgressStage.CTM.isPlayerAtStage(ep) : true;
		}

	}
}
