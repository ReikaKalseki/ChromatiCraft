/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator.StructureGenStatus;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.IO.MapOutput;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class StructureMapCommand extends DragonCommandBase {

	public static final int PACKET_COMPILE = 2048; //packet size in bytes = 4*(1+n*3)

	private static final Random rand = new Random();
	private final static HashMap<Integer, StructureMap> activeMaps = new HashMap();

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		Object[] ret = this.getPlayer(ics, args);
		EntityPlayerMP ep = (EntityPlayerMP)ret[0];
		if ((boolean)ret[1]) {
			String[] nargs = new String[args.length-1];
			System.arraycopy(args, 1, nargs, 0, nargs.length);
			args = nargs;
		}
		if (args.length < 2) {
			this.sendChatToSender(ics, EnumChatFormatting.RED.toString()+"Illegal arguments. Use <seed> [structType] [range]");
			return;
		}
		int range = Integer.parseInt(args[1]);
		int x = MathHelper.floor_double(ep.posX) >> 4;
		int z = MathHelper.floor_double(ep.posZ) >> 4;
		long start = System.currentTimeMillis();

		ChromaStructures s = ChromaStructures.valueOf(args[0].toUpperCase(Locale.ENGLISH));

		this.generateMap(ep, start, x, z, range, s);
	}

	private void generateMap(EntityPlayerMP ep, long start, int x, int z, int range, ChromaStructures s) {
		int hash = rand.nextInt();

		int dim = ep.worldObj.provider.dimensionId;
		String name = ep.worldObj.getWorldInfo().getWorldName()+"/["+ep.worldObj.getSaveHandler().getWorldDirectoryName()+"]";
		if (DragonAPICore.isSinglePlayer()) {
			this.startCollecting(hash, name, dim, x, z, range);
		}
		else {
			ReikaPacketHelper.sendStringIntPacket(DragonAPIInit.packetChannel, ChromaPackets.STRUCTMAPSTART.ordinal(), ep, name, hash, dim, x, z, range);
		}

		ArrayList<Integer> dat = new ArrayList();
		dat.add(hash);
		int n = 0;
		for (int dx = x-range; dx <= x+range; dx++) {
			for (int dz = z-range; dz <= z+range; dz++) {
				StructureGenStatus f = DungeonGenerator.instance.getGenStatus(s, (WorldServer)ep.worldObj, dx, dz);
				//ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, ChromaPackets.STRUCTMAPDAT.ordinal(), ep, hash, dx, dz, b.biomeID);
				n++;
				if (dx == 0 && dz == 0)
					ReikaJavaLibrary.pConsole("DS: "+f);
				if (DragonAPICore.isSinglePlayer()) {
					this.addDataPoint(hash, dx, dz, f);
				}
				else {
					dat.add(dx);
					dat.add(dz);
					dat.add(f.ordinal());
				}
				if (n >= PACKET_COMPILE) {
					if (DragonAPICore.isSinglePlayer()) {

					}
					else {
						ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, ChromaPackets.STRUCTMAPDAT.ordinal(), ep, dat);
						n = 0;
						dat.clear();
						dat.add(hash);
					}
				}
			}
		}
		//in case leftover
		if (dat.size() > 1) {
			//pad to fit normal packet size expectation
			int m = (dat.size()-1)/3;
			StructureGenStatus f = DungeonGenerator.instance.getGenStatus(s, (WorldServer)ep.worldObj, x, z);
			if (DragonAPICore.isSinglePlayer()) {
				this.addDataPoint(hash, x, z, f);
			}
			else {
				for (int i = m; i < PACKET_COMPILE; i++) {
					dat.add(x);
					dat.add(z);
					dat.add(f.ordinal());
				}
				ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, ChromaPackets.STRUCTMAPDAT.ordinal(), ep, dat);
				n = 0;
				dat.clear();
				dat.add(hash);
			}
		}
		if (DragonAPICore.isSinglePlayer()) {
			this.finishCollectingAndMakeImage(hash);
		}
		else {
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, ChromaPackets.STRUCTMAPEND.ordinal(), ep, hash);
		}
	}

	private Object[] getPlayer(ICommandSender ics, String[] args) {
		try {
			return new Object[]{this.getCommandSenderAsPlayer(ics), false};
		}
		catch (Exception e) {
			EntityPlayerMP ep = ReikaPlayerAPI.getPlayerByNameAnyWorld(args[0]);
			if (ep == null) {
				this.sendChatToSender(ics, "If you specify a player, they must exist.");
				throw new IllegalArgumentException(e);
			}
			return new Object[]{ep, true};
		}
	}

	@Override
	public String getCommandString() {
		return "ccstructmap";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void startCollecting(int hash, String world, int dim, int x, int z, int range) {
		StructureMap map = new StructureMap(world, dim, x, z, range);
		activeMaps.put(hash, map);
	}

	@SideOnly(Side.CLIENT)
	public static void addDataPoint(int hash, int x, int z, StructureGenStatus data) {
		StructureMap map = activeMaps.get(hash);
		if (map != null) {
			map.addPoint(x, z, data);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void finishCollectingAndMakeImage(int hash) {
		StructureMap map = activeMaps.remove(hash);
		if (map != null) {
			try {
				map.addGrid();
				String path = map.createImage();
				long dur = System.currentTimeMillis()-map.startTime;
				ReikaChatHelper.sendChatToPlayer(Minecraft.getMinecraft().thePlayer, EnumChatFormatting.GREEN+"File created in "+dur+" ms: "+path);
			}
			catch (IOException e) {
				ReikaChatHelper.sendChatToPlayer(Minecraft.getMinecraft().thePlayer, EnumChatFormatting.RED+"Failed to create file: "+e.toString());
				e.printStackTrace();
			}
		}
	}

	private static class StructureMap extends MapOutput<StructureGenStatus> {

		private StructureMap(String name, int dim, int x, int z, int r) {
			super(name, dim, x, z, r, 1, -1, false);
		}

		@Override
		protected int getColor(int x, int z, StructureGenStatus data) {
			return data.renderColor;
		}

	}

}
