/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
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
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class ChromaHelpData {

	public static final ChromaHelpData instance = new ChromaHelpData();

	private final BlockMap<HelpKey> data = new BlockMap();

	private static final String NBT_TAG = "ChromaExploreHelp";

	private ChromaHelpData() {
		this.addKey(ChromaBlocks.CRYSTAL, "crystals");

		this.addKey(ChromaBlocks.RAINBOWLEAF, "rainbowleaf");

		this.addKey(ChromaBlocks.MUD, "mud");

		this.addKey(ChromaBlocks.DYELEAF, "dyeleaf");
		this.addKey(ChromaBlocks.DECAY, "dyeleaf");

		this.addKey(ChromaBlocks.PYLON, ChromaTiles.PYLON.getBlockMetadata(), "pylon");
		this.addKey(ChromaBlocks.PYLONSTRUCT, 3, "pylon");
		this.addKey(ChromaBlocks.PYLONSTRUCT, 4, "pylon");
		this.addKey(ChromaBlocks.PYLONSTRUCT, 5, "pylon");

		this.addKey(ChromaTiles.DIMENSIONCORE.getBlock(), ChromaTiles.DIMENSIONCORE.getBlockMetadata(), "dimensioncore");

		this.addKey(ChromaBlocks.RUNE, "rune");

		if (ModList.FORESTRY.isLoaded()) {
			this.addKey(ChromaBlocks.HIVE, "hive");
		}

		for (int i = 0; i < TieredOres.list.length; i++) {
			this.addKey(ChromaBlocks.TIEREDORE, i, "ore_"+TieredOres.list[i].name().toLowerCase(Locale.ENGLISH));
		}

		for (int i = 0; i < TieredPlants.list.length; i++) {
			this.addKey(ChromaBlocks.TIEREDPLANT, i, "plant_"+TieredPlants.list[i].name().toLowerCase(Locale.ENGLISH));
		}

		for (int i = 0; i < Flowers.list.length; i++) {
			this.addKey(ChromaBlocks.DECOFLOWER, i, "flower_"+Flowers.list[i].name().toLowerCase(Locale.ENGLISH));
		}

		data.put(Blocks.bedrock, new DimBedrockHelpKey());
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

	public String getText(World world, int x, int y, int z) {
		HelpKey hk = this.getKey(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
		return hk != null && hk.isValid(world, x, y, z) ? hk.getText() : null;
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

	private static class DimBedrockHelpKey extends HelpKey {

		protected DimBedrockHelpKey() {
			super("dimbedrock");
		}

		@Override
		public boolean isValid(World world, int x, int y, int z) {
			return world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue() && y <= 24;
		}

	}

	private static class HelpKey {

		protected final String key;

		protected HelpKey(String xml) {
			key = xml;
		}

		public boolean isValid(World world, int x, int y, int z) {
			return true;
		}

		public final String getText() {
			return ChromaDescriptions.getHoverText(key);
		}

	}

	public void markDiscovered(EntityPlayer ep, Block b, int meta) {
		NBTTagCompound nbt = ChromaResearchManager.instance.getRootNBTTag(ep);
		NBTTagCompound tag = nbt.getCompoundTag(NBT_TAG);
		String sg = String.format("%d:%d", Block.getIdFromBlock(b), meta);
		boolean has = tag.getBoolean(sg);
		if (!has) {
			tag.setBoolean(sg, true);
			nbt.setTag(NBT_TAG, tag);
			ReikaPlayerAPI.sendCustomDataFromClient(ep);
		}
	}

	public boolean hasDiscovered(EntityPlayer ep, Block b, int meta) {
		NBTTagCompound nbt = ChromaResearchManager.instance.getRootNBTTag(ep);
		NBTTagCompound tag = nbt.getCompoundTag(NBT_TAG);
		String sg = String.format("%d:%d", Block.getIdFromBlock(b), meta);
		return tag.getBoolean(sg);
	}

}
