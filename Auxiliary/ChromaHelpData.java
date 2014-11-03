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

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import Reika.DragonAPI.Instantiable.Data.BlockMap;
import Reika.DragonAPI.Instantiable.Data.CoordinateData;

public class ChromaHelpData {

	private static final HashMap<PlayerEntry, HelpEntry> data = new HashMap();

	private static PlayerEntry getPlayerEntry(EntityPlayer ep, CoordinateData dat) {
		return new PlayerEntry(ep.getUniqueID(), dat);
	}

	//public static boolean hasEntryFor(EntityPlayer ep, CoordinateData dat) {
	//	return data.containsKey(getPlayerEntry(ep, dat));
	//}

	public static HelpEntry getEntryFor(EntityPlayer ep, CoordinateData dat) {
		return data.get(getPlayerEntry(ep, dat));
	}

	private static class PlayerEntry {

		public final UUID uid;
		public final CoordinateData data;

		public PlayerEntry(UUID id, CoordinateData dat) {
			uid = id;
			data = dat;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof PlayerEntry) {
				PlayerEntry p = (PlayerEntry)o;
				return p.uid.equals(uid) && p.data.equals(data);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return uid.hashCode()^data.hashCode();
		}

	}

	public static class HelpEntry {

		public void render(RenderGameOverlayEvent evt) {

		}

		public String getTitle() {
			return "Test";
		}

	}

	public static class ChromaHelpKeys {

		public static final ChromaHelpKeys instance = new ChromaHelpKeys();

		private final BlockMap<HelpKey> data = new BlockMap();

		private ChromaHelpKeys() {

		}

		private void addKey(Block b, String s) {
			data.put(b, new HelpKey(s));
		}

		private void addKey(Block b, int meta, String s) {
			data.put(b, meta, new HelpKey(s));
		}

		public HelpKey getKey(Block b, int meta) {
			return data.get(b, meta);
		}

		public HelpKey getKey(World world, int x, int y, int z) {
			return this.getKey(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
		}

		public HelpKey getKey(World world, MovingObjectPosition mov) {
			return this.getKey(world, mov.blockX, mov.blockY, mov.blockZ);
		}

	}

	public static class HelpKey {

		private HelpKey(String text) {

		}

	}

}
