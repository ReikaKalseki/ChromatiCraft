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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class ChromaHelpData {

	public static final ChromaHelpData instance = new ChromaHelpData();

	private final BlockMap<HelpKey> data = new BlockMap();

	private static final String NBT_TAG = "ChromaExploreHelp";

	private ChromaHelpData() {
		this.addKey(ChromaBlocks.CRYSTAL, "crystals");

		this.addKey(ChromaBlocks.RAINBOWLEAF, "rainbowleaf");

		this.addKey(ChromaBlocks.DYELEAF, "dyeleaf");
		this.addKey(ChromaBlocks.DECAY, "dyeleaf");

		this.addKey(ChromaBlocks.PYLON, ChromaTiles.PYLON.getBlockMetadata(), "pylon");
		this.addKey(ChromaBlocks.PYLONSTRUCT, 3, "pylon");
		this.addKey(ChromaBlocks.PYLONSTRUCT, 4, "pylon");
		this.addKey(ChromaBlocks.PYLONSTRUCT, 5, "pylon");

		this.addKey(ChromaTiles.DIMENSIONCORE.getBlock(), ChromaTiles.DIMENSIONCORE.getBlockMetadata(), "dimensioncore");

		this.addKey(ChromaBlocks.RUNE, "rune");

		for (int i = 0; i < TieredOres.list.length; i++) {
			this.addKey(ChromaBlocks.TIEREDORE, i, "ore_"+TieredOres.list[i].name().toLowerCase(Locale.ENGLISH));
		}

		for (int i = 0; i < TieredPlants.list.length; i++) {
			this.addKey(ChromaBlocks.TIEREDPLANT, i, "plant_"+TieredPlants.list[i].name().toLowerCase(Locale.ENGLISH));
		}

		for (int i = 0; i < Flowers.list.length; i++) {
			this.addKey(ChromaBlocks.DECOFLOWER, i, "flower_"+Flowers.list[i].name().toLowerCase(Locale.ENGLISH));
		}
	}

	private void addKey(ChromaBlocks b, String s) {
		data.put(b.getBlockInstance(), new HelpKey(s));
	}

	private void addKey(ChromaBlocks b, int meta, String s) {
		data.put(b.getBlockInstance(), meta, new HelpKey(s));
	}

	private void addKey(Block b, int meta, String s) {
		data.put(b, meta, new HelpKey(s));
	}

	private HelpKey getKey(Block b, int meta) {
		return data.get(b, meta);
	}

	public String getText(Block b, int meta) {
		HelpKey key = this.getKey(b, meta);
		return key != null ? key.getText() : null;
	}

	public String getText(World world, int x, int y, int z) {
		return this.getText(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}
	/*
	public String getText(World world, MovingObjectPosition mov) {
		return this.getText(world, mov.blockX, mov.blockY, mov.blockZ);
	}*/

	public Collection<String> getHelpKeys() {
		Collection<String> c = new ArrayList();
		for (HelpKey h : data.values()) {
			String s = h.key;
			if (!c.contains(s))
				c.add(s);
		}
		return c;
	}

	private static class HelpKey {

		private final String key;

		private HelpKey(String xml) {
			key = xml;
		}

		public String getText() {
			return ChromaDescriptions.getHoverText(key);
		}

	}

	public void markDiscovered(EntityPlayer ep, Block b, int meta) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = nbt.getCompoundTag(NBT_TAG);
		String sg = String.format("%d:%d", Block.getIdFromBlock(b), meta);
		boolean has = tag.getBoolean(sg);
		if (!has) {
			tag.setBoolean(sg, true);
			nbt.setTag(NBT_TAG, tag);
			ReikaPlayerAPI.syncCustomDataFromClient(ep);
		}
	}

	public boolean hasDiscovered(EntityPlayer ep, Block b, int meta) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound tag = nbt.getCompoundTag(NBT_TAG);
		String sg = String.format("%d:%d", Block.getIdFromBlock(b), meta);
		return tag.getBoolean(sg);
	}

}
