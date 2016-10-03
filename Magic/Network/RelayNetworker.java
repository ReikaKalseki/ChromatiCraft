/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Relay.BlockLumenRelay.TileEntityLumenRelay;
import Reika.ChromatiCraft.Block.Relay.BlockRelayFilter.TileEntityRelayFilter;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityRelaySource;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRift;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.IO.PacketTarget.CompoundPlayerTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BoPBlockHandler;

public final class RelayNetworker {

	public static final RelayNetworker instance = new RelayNetworker(getConfigurableRange(), 48);

	private static final String LOGGER_ID = "lumenrelay";

	public final int maxRange;
	public final int maxDepth;

	static {
		ModularLogger.instance.addLogger(ChromatiCraft.instance, LOGGER_ID);
	}

	private RelayNetworker(int r, int d) {
		maxRange = r;
		maxDepth = d;
	}

	private static int getConfigurableRange() {
		return MathHelper.clamp_int(ChromaOptions.RELAYRANGE.getValue(), 8, 24);
	}

	public TileEntityRelaySource findRelaySource(World world, int x, int y, int z, ForgeDirection dir, CrystalElement e, int amt, int dist) {
		if (amt <= 0)
			return null;
		RelayFinder rf = new RelayFinder(new Coordinate(x, y, z), Math.min(dist, maxRange), maxDepth, e, amt);
		rf.look = dir;
		RelayPath path = rf.find(world);
		if (path != null) {
			if (path.source.getEnergy(e) > 0)
				path.transmit(e);
			return path.source;
		}
		return null;
	}

	private static class RelayPath {

		public final TileEntityRelaySource source;
		public final Coordinate target;
		public final CrystalElement color;

		private final ArrayList<Coordinate> path;

		private RelayPath(TileEntityRelaySource src, Coordinate c, CrystalElement e, LinkedList<Coordinate> li) {
			source = src;
			target = c;
			color = e;
			path = new ArrayList();
			while (!li.isEmpty()) {
				path.add(li.removeLast()); //reverse list
			}
			ModularLogger.instance.log(LOGGER_ID, "Relay pathfinding complete from "+source+" to "+target+" for "+e);
		}

		public void transmit(CrystalElement e) {
			if (!source.worldObj.isRemote) {
				List<Integer> dat = new ArrayList();
				for (Coordinate c : path) {
					dat.add(c.xCoord);
					dat.add(c.yCoord);
					dat.add(c.zCoord);
				}
				dat.add(e.ordinal());

				ReikaPacketHelper.sendNIntPacket(ChromatiCraft.packetChannel, ChromaPackets.RELAYCONNECT.ordinal(), this.getTarget(), dat);
			}
		}

		private PacketTarget getTarget() {
			Collection<EntityPlayerMP> li = new ArrayList();
			for (Object o : source.worldObj.playerEntities) {
				EntityPlayerMP ep = (EntityPlayerMP)o;
				for (Coordinate c : path) {
					if (c.getDistanceTo(ep) <= 64) {
						li.add(ep);
						break;
					}
				}
			}
			return new CompoundPlayerTarget(li);
		}

	}

	private static class RelayFinder {

		private final Coordinate target;
		private final int maxRange;
		private final int maxDepth;
		private final CrystalElement color;
		private final int amount;

		private ForgeDirection look = ForgeDirection.UNKNOWN;

		private final LinkedList<Coordinate> path = new LinkedList();

		private RelayFinder(Coordinate loc, int r, int d, CrystalElement e, int amt) {
			target = loc;
			maxRange = r;
			maxDepth = d;
			color = e;
			amount = amt;
			path.addFirst(target);
			ModularLogger.instance.log(LOGGER_ID, "Relay pathfinding start @ "+loc+" for "+amt+" of "+e);
		}

		private RelayPath find(World world) {
			return this.findFrom(world, target, 0);
		}

		private RelayPath findFrom(World world, Coordinate start, int depth) {
			if (depth > maxDepth)
				return null;
			for (int i = 1; i < maxRange; i++) {
				Coordinate c = start.offset(look, i);
				Block b = c.getBlock(world);
				int meta = c.getBlockMetadata(world);
				if (ChromaTiles.getTileFromIDandMetadata(b, meta) == ChromaTiles.RELAYSOURCE) {
					path.addLast(c);
					return new RelayPath((TileEntityRelaySource)c.getTileEntity(world), target, color, path);
				}
				else if (b == ChromaBlocks.RELAY.getBlockInstance()) {
					path.addLast(c);
					TileEntityLumenRelay te = (TileEntityLumenRelay)c.getTileEntity(world);
					if (te.canTransmit(color)) {
						look = te.getInput();
						return this.findFrom(world, c, depth+1);
					}
				}
				else if (b == ChromaBlocks.RELAYFILTER.getBlockInstance()) {
					TileEntityRelayFilter te = (TileEntityRelayFilter)c.getTileEntity(world);
					//ReikaJavaLibrary.pConsole(color+": "+te.canTransmit(color));
					if (!te.canTransmit(color)) {
						return null;
					}
					continue;
				}
				if (ChromaTiles.getTileFromIDandMetadata(b, meta) == ChromaTiles.RIFT) {
					TileEntityRift te = (TileEntityRift)c.getTileEntity(world);
					WorldLocation loc = te.getLinkTarget();
					if (loc != null) {
						World world2 = loc.getWorld();
						if (world2.provider.dimensionId == world.provider.dimensionId || PylonGenerator.instance.canGenerateIn(world2)) {
							path.addLast(c);
							return this.findFrom(world2, new Coordinate(loc), depth+1);
						}
						else {
							return null;
						}
					}
					else {
						return null;
					}
				}
				else {
					/*
					if (b.isOpaqueCube())
						return null;
					else if (b.getLightOpacity(world, c.xCoord, c.yCoord, c.zCoord) > 0)
						return null;
					 */
					if (!PylonFinder.isBlockPassable(world, c.xCoord, c.yCoord, c.zCoord) && !this.isBlockRelayTransparent(world, c.xCoord, c.yCoord, c.zCoord)) {
						return null;
					}
				}
			}
			return null;
		}

		private boolean isBlockRelayTransparent(World world, int x, int y, int z) {
			Block b = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if (b == ChromaBlocks.ROUTERNODE.getBlockInstance())
				return true;
			if (b == ChromaBlocks.DECOFLOWER.getBlockInstance() && meta == Flowers.FLOWIVY.ordinal())
				return true;
			if (b == Blocks.yellow_flower || b == Blocks.red_flower)
				return true;
			if (ModList.BOP.isLoaded() && BoPBlockHandler.getInstance().isFlower(b))
				return true;
			return false;
		}

	}

}
