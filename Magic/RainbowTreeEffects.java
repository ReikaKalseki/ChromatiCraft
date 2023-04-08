/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.NonconvertibleBiome;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityBiomeReverter;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.IO.NBTFile.SimpleNBTFile;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTIO;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaChunkHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.MystCraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class RainbowTreeEffects {

	public static final RainbowTreeEffects instance = new RainbowTreeEffects();

	private final HashMap<Integer, PersistentRainbowTreeFX> persistent = new HashMap();
	private boolean loaded = false;

	private static final NBTIO<PersistentRainbowTreeFX> converter = new NBTIO<PersistentRainbowTreeFX>() {

		@Override
		public PersistentRainbowTreeFX createFromNBT(NBTBase nbt) {
			return PersistentRainbowTreeFX.readFromNBT((NBTTagCompound)nbt);
		}

		@Override
		public NBTBase convertToNBT(PersistentRainbowTreeFX obj) {
			NBTTagCompound nbt = new NBTTagCompound();
			obj.writeToNBT(nbt);
			return nbt;
		}

	};

	private RainbowTreeEffects() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void save(World world) {
		NBTTagCompound tag = new NBTTagCompound();
		this.saveData(tag);
		SimpleNBTFile nf = new SimpleNBTFile(this.getFile(world));
		nf.data = tag;
		try {
			nf.save();
		}
		catch (IOException e) {
			ChromatiCraft.logger.logError("Failed to save rainbow tree FX cache!");
			e.printStackTrace();
		}
	}

	public void load(World world) {
		SimpleNBTFile nf = new SimpleNBTFile(this.getFile(world));
		try {
			nf.load();
		}
		catch (IOException e) {
			ChromatiCraft.logger.logError("Failed to load rainbow tree FX cache!");
			e.printStackTrace();
		}
		if (nf.data != null) {
			this.loadData(nf.data);
		}
	}

	private File getFile(World world) {
		return new File(world.getSaveHandler().getWorldDirectory(), "ChromatiCraft_Data/RainbowTreeFX.dat");
	}

	private void saveData(NBTTagCompound tag) {
		NBTTagList li = new NBTTagList();
		ReikaNBTHelper.writeMapToNBT(persistent, li, null, converter);
		tag.setTag("persistent", tag);
	}

	private void loadData(NBTTagCompound tag) {
		NBTTagList li = tag.getTagList("persistent", NBTTypes.COMPOUND.ID);
		ReikaNBTHelper.readMapFromNBT(persistent, li, null, converter);
	}

	@SubscribeEvent
	@ModDependent(ModList.MYSTCRAFT)
	public void dissolveDecay(BlockTickEvent evt) {
		if (!evt.world.isRemote && evt.getBlock() == MystCraftHandler.getInstance().decayID) {
			if (this.isCoordinateClearing(evt.world, evt.xCoord, evt.yCoord, evt.zCoord)) {
				evt.setBlock(Blocks.air);
				for (int i = 0; i < 6; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					int dx = evt.xCoord+dir.offsetX;
					int dy = evt.yCoord+dir.offsetY;
					int dz = evt.zCoord+dir.offsetZ;
					Block b = evt.world.getBlock(dx, dy, dz);
					if (b == MystCraftHandler.getInstance().decayID) {
						evt.world.scheduleBlockUpdate(dx, dy, dz, b, 10);
					}
				}
			}
		}
	}

	private void loadIfNecessary(World world) {
		if (!loaded) {
			this.load(world);
			loaded = true;
		}
		if (!persistent.containsKey(world.provider.dimensionId)) {
			persistent.put(world.provider.dimensionId, new PersistentRainbowTreeFX(world));
		}
	}

	private boolean isCoordinateClearing(World world, int x, int y, int z) {
		this.loadIfNecessary(world);
		long time = world.getTotalWorldTime();
		PersistentRainbowTreeFX fx = persistent.get(world.provider.dimensionId);
		Iterator<Entry<BlockBox, Long>> it = fx.decayCleaning.entrySet().iterator();
		while (it.hasNext()) {
			Entry<BlockBox, Long> e = it.next();
			long val = e.getValue();
			if (val >= 0 && val < time)
				it.remove();
			else if (e.getKey().isBlockInside(x, y, z))
				return true;
		}
		return false;
	}

	public void addDecayClearing(World world, long duration) {
		this.addDecayClearing(world, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, duration);
	}

	public void addDecayClearing(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, long duration) {
		this.loadIfNecessary(world);
		PersistentRainbowTreeFX fx = persistent.get(world.provider.dimensionId);
		fx.decayCleaning.put(new BlockBox(minX, minY, minZ, maxX, maxY, maxZ), duration >= 0 ? world.getTotalWorldTime()+duration : -1);
		this.save(world);
	}

	public void doRainbowTreeEffects(World world, int x, int y, int z, float chanceFactor, double rangeFactor, Random r, boolean spreadForest) {
		if (!world.isRemote) {
			//this.loadIfNecessary(world);
			if (ModList.THAUMCRAFT.isLoaded()) {
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/25))
					this.fightTaint(world, x, y, z, rangeFactor);
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/20))
					this.fightEerie(world, x, y, z, rangeFactor);
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/10))
					this.convertPureNodeMagic(world, x, y, z, rangeFactor);
			}
			if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/20))
					this.fightInstability(world, x, y, z);
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/10))
					this.fightDecay(world, x, y, z, rangeFactor);
			}
			if (spreadForest && ChromaOptions.RAINBOWSPREAD.getState() && ReikaRandomHelper.doWithChance(chanceFactor*100/50) && world.getBiomeGenForCoords(x, z) == ChromatiCraft.rainbowforest)
				this.convertToRainbowForest(world, x, y, z, rangeFactor);
			//this.save(world);
		}
	}

	private void fightDecay(World world, int x, int y, int z, double rangeFactor) {
		if (MystCraftHandler.getInstance().decayID != null) {
			int r = (int)(64*rangeFactor);
			int rx = ReikaRandomHelper.getRandomPlusMinus(x, r);
			int rz = ReikaRandomHelper.getRandomPlusMinus(z, r);
			ReikaChunkHelper.removeBlocksFromChunk(world, rx, rz, MystCraftHandler.getInstance().decayID, -1);
		}
	}

	private void fightInstability(World world, int x, int y, int z) {
		if (ModList.MYSTCRAFT.isLoaded())
			ReikaMystcraftHelper.decrInstabilityForAge(world, 1);
	}

	public void addInstability(World world, int x, int y, int z) {
		if (ModList.MYSTCRAFT.isLoaded())
			ReikaMystcraftHelper.addInstabilityForAge(world, (short)1);
	}

	private void convertPureNodeMagic(World world, int x, int y, int z, double rangeFactor) {
		int dr = (int)(64*rangeFactor);
		int rx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
		int rz = ReikaRandomHelper.getRandomPlusMinus(z, dr);

		if (world.checkChunksExist(rx, 0, rz, rx, 255, rz)) {
			int r = 3;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = rx+i;
					int dz = rz+k;
					BiomeGenBase biome = world.getBiomeGenForCoords(dx, dz);
					int id = biome.biomeID;
					if (id == ThaumIDHandler.Biomes.MAGICFOREST.getID()) {
						BiomeGenBase natural = ReikaWorldHelper.getNaturalGennedBiomeAt(world, dx, dz);
						if (natural != null && ChromatiCraft.isRainbowForest(natural)) {
							ReikaWorldHelper.setBiomeForXZ(world, dx, dz, natural, true);
						}
					}
				}
			}
		}
	}

	private void fightEerie(World world, int x, int y, int z, double rangeFactor) {
		int dr = (int)(32*rangeFactor);
		int rx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
		int rz = ReikaRandomHelper.getRandomPlusMinus(z, dr);

		if (world.checkChunksExist(rx, 0, rz, rx, 255, rz)) {
			int r = 3;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = rx+i;
					int dz = rz+k;
					BiomeGenBase biome = world.getBiomeGenForCoords(dx, dz);
					int id = biome.biomeID;
					if (id == ThaumIDHandler.Biomes.EERIE.getID()) {
						BiomeGenBase natural = ReikaWorldHelper.getNaturalGennedBiomeAt(world, dx, dz);
						if (natural != null) {
							ReikaWorldHelper.setBiomeForXZ(world, dx, dz, natural, true);
						}
					}
				}
			}
		}
	}

	private void fightTaint(World world, int x, int y, int z, double rangeFactor) {
		int dr = (int)(32*rangeFactor);
		int rx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
		int rz = ReikaRandomHelper.getRandomPlusMinus(z, dr);

		if (world.checkChunksExist(rx, 0, rz, rx, 255, rz)) {
			int r = 3;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = rx+i;
					int dz = rz+k;
					BiomeGenBase biome = world.getBiomeGenForCoords(dx, dz);
					int id = biome.biomeID;
					if (id == ThaumIDHandler.Biomes.TAINT.getID()) {
						//ReikaJavaLibrary.pConsole(dx+", "+dz, Side.CLIENT);
						BiomeGenBase natural = ReikaWorldHelper.getNaturalGennedBiomeAt(world, dx, dz);
						if (natural != null) {
							if (natural.biomeID == id) {
								natural = BiomeGenBase.forest;//ChromatiCraft.recoveredBiome;//BiomeGenBase.forest;
							}
							ReikaWorldHelper.setBiomeForXZ(world, dx, dz, natural, true);
						}
					}
				}
			}
		}
	}

	private void convertToRainbowForest(World world, int x, int y, int z, double rangeFactor) {
		int dr = (int)(32*rangeFactor);
		int rx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
		int rz = ReikaRandomHelper.getRandomPlusMinus(z, dr);

		if (world.checkChunksExist(rx, 0, rz, rx, 255, rz)) {
			int r = 3;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = rx+i;
					int dz = rz+k;
					if (!TileEntityBiomeReverter.stopConversion(world, dx, dz)) {
						BiomeGenBase biome = world.getBiomeGenForCoords(dx, dz);
						if (biome != ChromatiCraft.rainbowforest && !BiomeGlowingCliffs.isGlowingCliffs(biome) && !(biome instanceof NonconvertibleBiome)) {
							ReikaWorldHelper.setBiomeForXZ(world, dx, dz, ChromatiCraft.rainbowforest, true);
						}
					}
				}
			}
		}
	}

	private static class PersistentRainbowTreeFX {

		private final int dimensionID;

		private final HashMap<BlockBox, Long> decayCleaning = new HashMap();

		private PersistentRainbowTreeFX(World world) {
			this(world.provider.dimensionId);
		}

		private PersistentRainbowTreeFX(int id) {
			dimensionID = id;
		}

		private void writeToNBT(NBTTagCompound NBT) {
			NBTTagList li = new NBTTagList();
			for (Entry<BlockBox, Long> e : decayCleaning.entrySet()) {
				NBTTagCompound tag = new NBTTagCompound();
				e.getKey().writeToNBT(tag);
				tag.setLong("time", e.getValue());
				li.appendTag(tag);
			}
			NBT.setTag("cleaning", li);
			NBT.setInteger("dimID", dimensionID);
		}

		private static PersistentRainbowTreeFX readFromNBT(NBTTagCompound NBT) {
			PersistentRainbowTreeFX fx = new PersistentRainbowTreeFX(NBT.getInteger("dimID"));
			NBTTagList li = NBT.getTagList("cleaning", NBTTypes.COMPOUND.ID);
			for (Object o : li.tagList) {
				NBTTagCompound tag = (NBTTagCompound)o;
				BlockBox box = BlockBox.readFromNBT(tag);
				fx.decayCleaning.put(box, tag.getLong("time"));
			}
			return fx;
		}

	}
}
