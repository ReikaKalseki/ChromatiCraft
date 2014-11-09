/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.TileEntityCache;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public class CrystalNetworker implements TickHandler {

	public static final CrystalNetworker instance = new CrystalNetworker();

	private final TileEntityCache<CrystalNetworkTile> tiles = new TileEntityCache();
	private final HashMap<Integer, ArrayList<CrystalFlow>> flows = new HashMap();

	private int losTimer = 0;

	private CrystalNetworker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void clearOnUnload(WorldEvent.Unload evt) {
		int dim = evt.world.provider.dimensionId;
		this.clear(dim);
		for (WorldLocation c : tiles.keySet()) {
			CrystalNetworkTile te = tiles.get(c);
			if (te instanceof CrystalTransmitter)
				((CrystalTransmitter)te).clearTargets();
		}
		tiles.removeWorld(evt.world);
	}

	public boolean checkConnectivity(CrystalElement e, WorldLocation loc, int range) {
		return this.checkConnectivity(e, loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord, range);
	}

	public boolean checkConnectivity(CrystalElement e, World world, int x, int y, int z, int range) {
		CrystalPath p = new PylonFinder(e, world, x, y, z, range).findPylon();
		return p != null && p.canTransmit();
	}

	public CrystalSource getConnectivity(CrystalElement e, World world, int x, int y, int z, int range) {
		CrystalPath p = new PylonFinder(e, world, x, y, z, range).findPylon();
		return p != null && p.canTransmit() ? p.transmitter : null;
	}

	public void makeRequest(CrystalReceiver r, CrystalElement e, int amount, int range) {
		this.makeRequest(r, e, amount, r.getWorld(), r.getX(), r.getY(), r.getZ(), range);
	}

	public void makeRequest(CrystalReceiver r, CrystalElement e, int amount, World world, int x, int y, int z, int range) {
		if (amount <= 0)
			return;
		CrystalFlow p = new PylonFinder(e, world, x, y, z, range).findPylon(r, amount);
		if (p != null) {
			this.addFlow(world, p);
		}
	}

	private void addFlow(World world, CrystalFlow p) {
		ArrayList<CrystalFlow> li = flows.get(world.provider.dimensionId);
		if (li == null) {
			li = new ArrayList();
			flows.put(world.provider.dimensionId, li);
		}
		li.add(p);
	}

	public void tick(TickType type, Object... data) {
		losTimer++;
		boolean doCheckLOS = false;
		if (losTimer >= 40) {
			losTimer = 0;
			doCheckLOS = true;
		}
		for (int dim : flows.keySet()) {
			Iterator<CrystalFlow> it = flows.get(dim).iterator();
			while (it.hasNext()) {
				CrystalFlow p = it.next();
				if (p.transmitter.canConduct() && p.canTransmit()) {
					int amt = p.drain();
					if (amt > 0) {
						p.receiver.receiveElement(p.element, amt);
						p.transmitter.drain(p.element, amt);
						if (p.isComplete()) {
							p.resetTiles();
							it.remove();
						}
					}
				}
				else {
					p.receiver.onPathBroken(p.element);
					p.resetTiles();
					it.remove();
				}
			}

			if (doCheckLOS) {
				it = flows.get(dim).iterator();
				while (it.hasNext()) {
					CrystalFlow p = it.next();
					if (!p.checkLineOfSight()) {
						p.receiver.onPathBroken(p.element);
						p.resetTiles();
						it.remove();
					}
				}
			}
		}
	}

	public void clear(int dim) {
		//do not clear tiles!
		ArrayList<CrystalFlow> li = flows.get(dim);
		if (li != null) {
			for (CrystalFlow f : li) {
				f.resetTiles();
			}
			flows.remove(dim);
		}
	}

	public void addTile(CrystalNetworkTile te) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			tiles.put(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()), te);
		}
	}

	public void removeTile(CrystalNetworkTile te) {
		tiles.remove(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()));
		ArrayList<CrystalFlow> li = flows.get(te.getWorld().provider.dimensionId);
		if (li != null) {
			Iterator<CrystalFlow> it = li.iterator();
			while (it.hasNext()) {
				CrystalFlow p = it.next();
				if (p.contains(te)) {
					p.resetTiles();
					p.receiver.onPathBroken(p.element);
					it.remove();
				}
			}
		}
	}

	public void breakPaths(CrystalNetworkTile te) {
		ArrayList<CrystalFlow> li = flows.get(te.getWorld().provider.dimensionId);
		if (li != null) {
			Iterator<CrystalFlow> it = li.iterator();
			while (it.hasNext()) {
				CrystalFlow p = it.next();
				if (p.contains(te)) {
					p.receiver.onPathBroken(p.element);
					p.resetTiles();
					it.remove();
				}
			}
		}
	}

	ArrayList<CrystalTransmitter> getTransmittersWithinDofXYZ(World world, int x, int y, int z, double dist, CrystalElement e) {
		ArrayList<CrystalTransmitter> li = new ArrayList();
		for (WorldLocation c : tiles.keySet()) {
			if (c.dimensionID == world.provider.dimensionId) {
				CrystalNetworkTile tile = tiles.get(c);
				if (tile instanceof CrystalTransmitter) {
					CrystalTransmitter te = (CrystalTransmitter)tile;
					if (te.canConduct()) {
						if (e == null || te.isConductingElement(e)) {
							double d = te.getDistanceSqTo(x, y, z);
							//ReikaJavaLibrary.pConsole(e+": "+d+": "+te);
							double send = te.getSendRange();
							if (d <= Math.min(dist*dist, send*send)) {
								li.add(te);
							}
						}
					}
				}
			}
		}
		return li;
	}
	/*
	ArrayList<CrystalNetworkTile> getTilesWithinDofXYZ(World world, int x, int y, int z, double dist) {
		return this.getTransmittersWithinDofXYZ(world, x, y, z, dist, null);
	}*/

	@Override
	public TickType getType() {
		return TickType.SERVER;
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "Crystal Networker";
	}

}
