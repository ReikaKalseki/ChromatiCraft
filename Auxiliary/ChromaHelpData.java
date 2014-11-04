/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockMap;

public class ChromaHelpData {

	public static final ChromaHelpData instance = new ChromaHelpData();

	private final BlockMap<HelpKey> data = new BlockMap();

	private ChromaHelpData() {
		this.addKey(ChromaBlocks.CRYSTAL, "crystals");
		this.addKey(ChromaBlocks.RAINBOWLEAF, "rainbowleaf");
		this.addKey(ChromaBlocks.PYLON, ChromaTiles.PYLON.getBlockMetadata(), "pylon");
		this.addKey(ChromaBlocks.PYLONSTRUCT, 3, "pylon");
		this.addKey(ChromaBlocks.PYLONSTRUCT, 4, "pylon");
		this.addKey(ChromaBlocks.PYLONSTRUCT, 5, "pylon");
		this.addKey(ChromaBlocks.RUNE, "rune");
		this.addKey(ChromaBlocks.TIEREDORE, 0, "ore0");
		this.addKey(ChromaBlocks.TIEREDORE, 1, "ore1");
		this.addKey(ChromaBlocks.TIEREDORE, 2, "ore2");
		this.addKey(ChromaBlocks.TIEREDORE, 3, "ore3");
		this.addKey(ChromaBlocks.TIEREDPLANT, 0, "plant0");
		this.addKey(ChromaBlocks.TIEREDPLANT, 1, "plant1");
		this.addKey(ChromaBlocks.TIEREDPLANT, 2, "plant2");
		this.addKey(ChromaBlocks.TIEREDPLANT, 3, "plant3");
		this.addKey(ChromaBlocks.TIEREDPLANT, 4, "plant4");
	}

	private void addKey(ChromaBlocks b, String s) {
		data.put(b.getBlockInstance(), new HelpKey(s));
	}

	private void addKey(ChromaBlocks b, int meta, String s) {
		data.put(b.getBlockInstance(), meta, new HelpKey(s));
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

	private static class HelpKey {

		private final String key;

		private HelpKey(String xml) {
			key = xml;
		}

		public String getText() {
			return ChromaDescriptions.getHoverText(key);
		}

	}

}
