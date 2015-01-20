/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Network;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Block.BlockLumenRelay.TileEntityLumenRelay;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityRelaySource;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public final class RelayNetworker {

	public static final RelayNetworker instance = new RelayNetworker(16);

	public final int maxRange;

	private RelayNetworker(int r) {
		maxRange = r;
	}

	public TileEntityRelaySource findRelaySource(World world, int x, int y, int z, ForgeDirection dir, CrystalElement e, int amt, int dist) {
		RelayFinder rf = new RelayFinder(world, new Coordinate(x, y, z), Math.min(dist, maxRange), e, amt);
		rf.look = dir;
		RelayPath path = rf.find();
		if (path != null) {
			path.transmit(e);
			return path.source;
		}
		return null;
	}

	private static class RelayPath {

		public final TileEntityRelaySource source;
		public final Coordinate target;

		private final LinkedList<Coordinate> path;

		private RelayPath(TileEntityRelaySource src, Coordinate c, LinkedList<Coordinate> li) {
			source = src;
			target = c;
			path = li;
		}

		//Trigger render fx
		public void transmit(CrystalElement e) {

		}

	}

	private static class RelayFinder {

		private final World world;
		private final Coordinate target;
		private final int maxRange;
		private final CrystalElement color;
		private final int amount;

		private ForgeDirection look = ForgeDirection.UNKNOWN;

		private final LinkedList<Coordinate> path = new LinkedList();

		private RelayFinder(World world, Coordinate loc, int r, CrystalElement e, int amt) {
			this.world = world;
			target = loc;
			maxRange = r;
			color = e;
			amount = amt;
			path.addFirst(target);
		}

		private RelayPath find() {
			return this.findFrom(target);
		}

		private RelayPath findFrom(Coordinate start) {
			for (int i = 1; i < maxRange; i++) {
				Coordinate c = start.offset(look, i);
				Block b = c.getBlock(world);
				int meta = c.getBlockMetadata(world);
				if (ChromaTiles.getTileFromIDandMetadata(b, meta) == ChromaTiles.RELAYSOURCE) {
					path.addLast(c);
					return new RelayPath((TileEntityRelaySource)c.getTileEntity(world), target, path);
				}
				else if (b == ChromaBlocks.RELAY.getBlockInstance()) {
					path.addLast(c);
					TileEntityLumenRelay te = (TileEntityLumenRelay)c.getTileEntity(world);
					if (te.canTransmit(color)) {
						look = te.getInput();
						return this.findFrom(c);
					}
				}
				else {
					if (b.isOpaqueCube())
						return null;
					else if (b.getLightOpacity(world, c.xCoord, c.yCoord, c.zCoord) > 0)
						return null;
				}
			}
			return null;
		}

	}

}
