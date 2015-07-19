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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Interfaces.WrapperTile;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker.CrystalLink;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;

public class PylonFinder {

	private final LinkedList<WorldLocation> nodes = new LinkedList();
	private final Collection<WorldLocation> blacklist = new HashSet();
	private final MultiMap<WorldLocation, WorldLocation> duplicates = new MultiMap(new MultiMap.HashSetFactory());

	private final CrystalNetworker net;
	private static final RayTracer tracer;

	//private final int stepRange;
	private final CrystalReceiver target;
	private final CrystalElement element;
	private final EntityPlayer user;

	private int steps = 0;
	private int stepsThisTick = 0;
	private boolean suspended = false;
	public static final int MAX_STEPS_PER_TICK = 1000;

	private static boolean invalid = false;

	private static final HashMap<WorldLocation, EnumMap<CrystalElement, ArrayList<CrystalPath>>> paths = new HashMap();

	//private final HashMap<ChunkCoordIntPair, ChunkCopy> chunkCache = new HashMap();

	PylonFinder(CrystalElement e, CrystalReceiver r, EntityPlayer ep) {
		element = e;
		target = r;
		//stepRange = r;
		net = CrystalNetworker.instance;
		blacklist.add(this.getLocation(r));
		user = ep;
	}

	CrystalPath findPylon() {
		return this.findPylonWith(0);
	}

	CrystalPath findPylonWith(int thresh) {
		invalid = false;
		CrystalPath p = this.checkExistingPaths(thresh);
		//ReikaJavaLibrary.pConsole(p != null ? p.nodes.size() : "null", Side.SERVER);
		if (p != null)
			return p;
		if (!this.anyConnectedSources())
			return null;

		this.findFrom(target, thresh);
		//ReikaJavaLibrary.pConsole(this.toString());
		if (this.isComplete()) {
			CrystalPath path = new CrystalPath(net, element, nodes);
			if (!(target instanceof WrapperTile))
				this.addValidPath(path);
			return path;
		}
		return null;
	}

	CrystalFlow findPylon(int amount, int maxthru) {
		return this.findPylon(amount, maxthru, 0);
	}

	CrystalFlow findPylon(int amount, int maxthru, int thresh) {
		invalid = false;
		CrystalPath p = this.checkExistingPaths(thresh);
		if (p != null)
			return new CrystalFlow(net, p, target, amount, maxthru);
		if (!this.anyConnectedSources())
			return null;

		this.findFrom(target, thresh);
		//ReikaJavaLibrary.pConsole(this.toString());
		if (this.isComplete()) {
			CrystalFlow flow = new CrystalFlow(net, target, element, amount, nodes, maxthru);
			//ReikaJavaLibrary.pConsole(flow.checkLineOfSight()+":"+flow);
			if (!(target instanceof WrapperTile))
				this.addValidPath(flow.asPath());
			return flow;
		}
		return null;
	}

	private boolean anyConnectedSources() {
		ArrayList<CrystalSource> li = net.getAllSourcesFor(element, true);
		for (CrystalSource s : li) {
			if (this.isSourceConnected(s))
				return true;
		}
		return false;
	}

	private boolean isSourceConnected(CrystalSource s) {
		return target instanceof WrapperTile || !net.getNearbyReceivers(s, element).isEmpty();
	}

	private CrystalPath checkExistingPaths(int thresh) {
		EnumMap<CrystalElement, ArrayList<CrystalPath>> map = paths.get(getLocation(target));
		if (map != null) {
			ArrayList<CrystalPath> c = map.get(element);
			if (c != null) {
				Iterator<CrystalPath> it = c.iterator();
				while (it.hasNext()) {
					CrystalPath p = it.next();
					if (!p.stillValid()) {
						//ReikaJavaLibrary.pConsole("rem "+p, Side.SERVER);
						it.remove();
					}
					else
						return p;
				}
			}
		}
		return null;
	}

	private void addValidPath(CrystalPath p) {
		EnumMap<CrystalElement, ArrayList<CrystalPath>> map = paths.get(p.origin);
		if (map == null) {
			ArrayList<CrystalPath> c = new ArrayList();
			map = new EnumMap(CrystalElement.class);
			c.add(p);
			map.put(element, c);
			paths.put(p.origin, map);
		}
		else {
			ArrayList<CrystalPath> c = map.get(p.element);
			if (c == null) {
				c = new ArrayList();
				c.add(p);
				map.put(element, c);
			}
			else {
				if (!c.contains(p))
					c.add(p);
				//ReikaJavaLibrary.pConsole(c.size(), Side.SERVER);
			}
		}
		for (int i = 0; i < p.nodes.size(); i++) {
			WorldLocation loc = p.nodes.get(i);
			TileEntity te = loc.getTileEntity();
			if (te instanceof CrystalRepeater) {
				CrystalRepeater tile = (CrystalRepeater)te;
				tile.setSignalDepth(p.element, p.nodes.size()-1-i);
			}
		}
		//ReikaJavaLibrary.pConsole(paths, Side.SERVER);
		Collections.sort(paths.get(p.origin).get(p.element));
	}

	static void removePathsWithTile(CrystalNetworkTile te) {
		if (te == null)
			return;
		EnumMap<CrystalElement, ArrayList<CrystalPath>> map = paths.get(getLocation(te));
		if (map != null) {
			for (CrystalElement e : map.keySet()) {
				ArrayList<CrystalPath> c = map.get(e);
				Iterator<CrystalPath> it = c.iterator();
				while (it.hasNext()) {
					CrystalPath p = it.next();
					if (p.contains(te))
						it.remove();
				}
			}
		}
	}

	@Override
	public String toString() {
		return element+": "+target;//+" by "+stepRange;
	}

	public boolean isComplete() {
		return nodes.size() >= 2 && nodes.getLast().getTileEntity() instanceof CrystalSource;
	}

	private void findFrom(CrystalReceiver r, int thresh) {
		if (invalid)
			return;
		WorldLocation loc = getLocation(r);
		if (nodes.contains(loc)) {
			return;
		}
		steps++;
		stepsThisTick++;
		if (steps > 200) {
			//return;
		}
		/*
		if (stepsThisTick >= MAX_STEPS_PER_TICK) {
			stepsThisTick = 0;
			this.suspendUntilNextTick(r);
			return;
		}
		 */
		nodes.add(loc);
		ArrayList<CrystalTransmitter> li = net.getTransmittersTo(r, element);
		for (CrystalTransmitter te : li) {
			WorldLocation loc2 = getLocation(te);
			if (!blacklist.contains(loc2) && !duplicates.containsValue(loc2)) {
				CrystalLink l = net.getLink(loc2, loc);
				if (te != target) {
					if (te.needsLineOfSight() && !l.hasLineOfSight()) {
						l.recalculateLOS();
						if (!l.hasLineOfSight())
							continue;
					}

					if (te instanceof CrystalSource && this.isConnectableSource((CrystalSource)te, thresh)) {
						net.addLink(l, true);
						nodes.add(loc2);
						return;
					}
					else if (te instanceof CrystalRepeater) {
						net.addLink(l, true);
						Collection<WorldLocation> others = new ArrayList(li);
						others.remove(te);
						duplicates.put(loc2, others);
						this.findFrom((CrystalRepeater)te, thresh);
					}
				}
			}
		}
		if (!this.isComplete()) {
			nodes.removeLast();
			blacklist.add(loc);
			duplicates.remove(loc);
		}
	}

	private boolean isConnectableSource(CrystalSource te, int thresh) {
		return te.canTransmitTo(target) && te.getEnergy(element) >= thresh && (user == null || te.playerCanUse(user));
	}

	/*
	private void suspendUntilNextTick(CrystalReceiver r) {
		suspended = true;
	}
	 */

	static boolean lineOfSight(WorldLocation l1, WorldLocation l2) {
		return lineOfSight(l1.getWorld(), l1.xCoord, l1.yCoord, l1.zCoord, l2.xCoord, l2.yCoord, l2.zCoord);
	}

	private boolean lineOfSight(CrystalNetworkTile te1, CrystalNetworkTile te) {
		return lineOfSight(te1.getWorld(), te1.getX(), te1.getY(), te1.getZ(), te.getX(), te.getY(), te.getZ());
	}

	private boolean lineOfSight(World world, int x, int y, int z, CrystalNetworkTile te) {
		return lineOfSight(world, x, y, z, te.getX(), te.getY(), te.getZ());
	}

	public static boolean lineOfSight(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		tracer.setOrigins(x1, y1, z1, x2, y2, z2);
		tracer.offset(0.5, 0.5, 0.5);
		return tracer.isClearLineOfSight(world);
	}

	static {
		tracer = new RayTracer(0, 0, 0, 0, 0, 0);
		tracer.softBlocksOnly = true;
		tracer.addTransparentBlock(Blocks.glass);
		tracer.addTransparentBlock(Blocks.snow_layer, 0);
		/*
		tracer.addOpaqueBlock(Blocks.standing_sign);
		tracer.addOpaqueBlock(Blocks.reeds);
		tracer.addOpaqueBlock(Blocks.carpet);
		tracer.addOpaqueBlock(Blocks.tallgrass);
		tracer.addOpaqueBlock(Blocks.deadbush);
		tracer.addOpaqueBlock(Blocks.rail);
		tracer.addOpaqueBlock(Blocks.web);
		tracer.addOpaqueBlock(Blocks.torch);
		tracer.addOpaqueBlock(Blocks.redstone_torch);
		tracer.addOpaqueBlock(Blocks.unlit_redstone_torch);
		tracer.addOpaqueBlock(Blocks.powered_comparator);
		tracer.addOpaqueBlock(Blocks.unpowered_comparator);
		tracer.addOpaqueBlock(Blocks.powered_repeater);
		tracer.addOpaqueBlock(Blocks.unpowered_repeater);
		tracer.addOpaqueBlock(Blocks.wheat);
		tracer.addOpaqueBlock(Blocks.carrots);
		tracer.addOpaqueBlock(Blocks.potatoes);*/
	}

	static final WorldLocation getLocation(CrystalNetworkTile te) {
		return new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ());
	}

	static void stopAllSearches() {
		invalid = true;
	}
	/*
	void receiveChunk(Chunk c) {
		ChunkCoordIntPair key = c.getChunkCoordIntPair();
		ChunkCopy copy = new ChunkCopy(c);
		chunkCache.put(key, copy);
	}

	private static class ChunkCopy extends Chunk/*implements IBlockAccess*//* {

		private final short[][][] data = new short[16][256][16];

		private ChunkCopy(Chunk c) {
			super(c.worldObj, c.xPosition, c.zPosition);
			for (int i = 0; i < 16; i++) {
				for (int k = 0; k < 16; k++) {
					for (int j = 0; j < 256; j++) {
						data[i][j][k] = encode(c.getBlock(i, j, k), c.getBlockMetadata(i, j, k));
					}
				}
			}
		}

		private static short encode(Block b, int meta) {
			return (short)(Block.getIdFromBlock(b)+(meta << 12));
		}

		private static BlockKey decode(short id) {
			return new BlockKey(Block.getBlockById(id & 4095), (id >> 12));
		}

		@Override
		public Block getBlock(int x, int y, int z) {
			return this.decode(data[x&15][y][z&15]).blockID;
		}

		@Override
		public int getBlockMetadata(int x, int y, int z) {
			return this.decode(data[x&15][y][z&15]).metadata;
		}
		/*
		@Override
		public boolean isAirBlock(int x, int y, int z) {
			return this.getBlock(x, y, z).getMaterial() == Material.air;
		}

		@Override
		public TileEntity getTileEntity(int x, int y, int z) {return null;}

		@Override
		@SideOnly(Side.CLIENT)
		public int getLightBrightnessForSkyBlocks(int x, int y, int z, int p_72802_4_) {return 0;}

		@Override
		public int isBlockProvidingPowerTo(int x, int y, int z, int side) {return 0;}

		@Override
		@SideOnly(Side.CLIENT)
		public BiomeGenBase getBiomeGenForCoords(int x, int z) {return null;}

		@Override
		@SideOnly(Side.CLIENT)
		public int getHeight() {return 256;}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean extendedLevelsInChunkCache() {return false;}

		@Override
		public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {return false;}
	 *//*
}

static class ChunkRequest {

	final PylonFinder pathfinder;
	final WorldChunk chunk;

	private ChunkRequest(PylonFinder f, WorldChunk wc) {
		pathfinder = f;
		chunk = wc;
	}

}
	  */
}
