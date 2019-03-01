/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.MusicLoader;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionalAudioHandler.DimensionMusic;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader.RemoteAssetsDownloadCompleteEvent;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaDimensionTicker implements TickHandler {

	public static final ChromaDimensionTicker instance = new ChromaDimensionTicker();

	private final Random rand = new Random();

	public final int dimID = ExtraChromaIDs.DIMID.getValue();
	private final Collection<Ticket> tickets = new ArrayList();

	private ChromaDimensionTicker() {

	}

	@SideOnly(Side.CLIENT)
	public static ISound getCurrentMusic() {
		return ChromaDimensionalAudioHandler.currentMusic;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerMusic(RemoteAssetsDownloadCompleteEvent evt) {
		Collection<String> li = MusicLoader.instance.getMusicFiles();
		ChromatiCraft.logger.log(li.size()+" music tracks available for the dimension: "+li);
		for (String path : li) {
			DimensionMusic mus = new DimensionMusic(path, path.substring(0, path.length()-4).endsWith("_c"));
			ChromaDimensionalAudioHandler.music.add(mus);
			DirectResourceManager.getInstance().registerCustomPath(mus.path, ChromaClient.chromaCategory, true);
		}
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		switch(type) {
			case WORLD: {
				World world = (World)tickData[0];
				if (world.provider.dimensionId == dimID) {
					world.ambientTickCountdown = Integer.MAX_VALUE;

					if (!world.isRemote) {
						this.unloadChunks();
					}

					ChromaDimensionManager.tickPlayersInStructures(world);
					SkyRiverManager.tickSkyRiverServer(world);
					world.setAllowedSpawnTypes(false, true);
					if (!world.playerEntities.isEmpty()) {
						ChromaDimensionManager.dimensionAge++;
						if (!world.isRemote) {
							for (EntityPlayer ep : ((List<EntityPlayer>)world.playerEntities)) {
								if (ep.posY < 30 && !ReikaEntityHelper.canEntitySeeTheSky(ep) && ChromaDimensionManager.getStructurePlayerIsIn(ep) == null) {
									this.spawnVoidLumaFog(ep);
								}
							}
						}
					}
				}
				break;
			}
			case CLIENT:
				if (!ChromaDimensionalAudioHandler.music.isEmpty())
					ChromaDimensionalAudioHandler.playMusic();
				ChromaDimensionalAudioHandler.ensureSoundOn();
				SkyRiverManagerClient.handleSkyRiverMovementClient();
				break;
			case PLAYER:
				EntityPlayer ep = (EntityPlayer)tickData[0];
				if (ep.worldObj.provider.dimensionId == dimID) {
					if (!RegionMapper.isPointInCentralRegion(ep.posX, ep.posZ)) {
						OuterRegionsEvents.instance.tickPlayerInOuterRegion(ep);
						/*
						if (rand.nextInt(3200) == 0) {
							ChromaDimensionBiome b = BiomeDistributor.getBiome(MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posZ));
							if (b == Biomes.SKYLANDS.getBiome() || b == SubBiomes.VOIDLANDS.getBiome()) {
								for (EntityAurora e : WorldGenAurorae.generateAurorae(ep.worldObj, rand, ep.posX, ep.posY, ep.posZ))
									ChromaDimensionManager.addAurora(e);
							}
						}*/
					}
				}
				break;
			default:
				break;
		}
	}

	private void spawnVoidLumaFog(EntityPlayer ep) {
		double x = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 32);
		double z = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 32);
		double y = ReikaRandomHelper.getRandomBetween(-10, 0);
		EntityGlowCloud e = new EntityGlowCloud(ep.worldObj, x, y, z);
		if (e.getCanSpawnHere()) {
			ep.worldObj.spawnEntityInWorld(e);
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
		return EnumSet.of(TickType.WORLD, TickType.CLIENT, TickType.PLAYER);
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
