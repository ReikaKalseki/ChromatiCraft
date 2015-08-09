/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.IO.CustomMusic;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaDimensionTicker implements TickHandler {

	public static final ChromaDimensionTicker instance = new ChromaDimensionTicker();

	private final Random rand = new Random();

	public final int dimID = ExtraChromaIDs.DIMID.getValue();
	private final Collection<Ticket> tickets = new ArrayList();
	private final ArrayList<CustomMusic> music = new ArrayList();

	private ChromaDimensionTicker() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			this.registerMusic();
	}

	@SideOnly(Side.CLIENT)
	private void registerMusic() {
		music.add(new CustomMusic("Reika/ChromatiCraft/Sounds/dimension_score.ogg"));

		for (CustomMusic s : music) {
			DirectResourceManager.getInstance().registerCustomPath(s.path, SoundCategory.MUSIC, true);
		}
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		switch(type) {
			case WORLD:
				World world = (World)tickData[0];
				if (world.provider.dimensionId == dimID) {
					world.ambientTickCountdown = Integer.MAX_VALUE;

					if (!world.isRemote) {
						this.unloadChunks();
					}
				}
				break;
			case CLIENT:
				this.playMusic();
				break;
			default:
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	private void playMusic() {
		if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.provider.dimensionId == dimID) {
			SoundHandler sh = Minecraft.getMinecraft().getSoundHandler();
			for (CustomMusic s : music) {
				if (sh.isSoundPlaying(s)) {
					return;
				}
			}
			CustomMusic s = music.get(rand.nextInt(music.size()));
			sh.playSound(s);
		}
	}

	private void unloadChunks() {
		for (Ticket t : tickets) {
			for (ChunkCoordIntPair p : t.getChunkList()) {
				ForgeChunkManager.unforceChunk(t, p);
			}
		}
		tickets.clear();
	}

	public void scheduleTicketUnload(Ticket t) {
		tickets.add(t);
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.WORLD, TickType.CLIENT);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.END;
	}

	@Override
	public String getLabel() {
		return "Chroma Dimension Tag";
	}

}
