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
import java.util.Iterator;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.TileEntityCache;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public class CrystalNetworker implements TickHandler {

	public static final CrystalNetworker instance = new CrystalNetworker();

	private final TileEntityCache<CrystalNetworkTile> tiles = new TileEntityCache();
	private final ArrayList<CrystalFlow> flows = new ArrayList();

	private int losTimer = 0;

	private CrystalNetworker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void clearOnUnload(WorldEvent.Unload evt) {
		this.clear();
		for (Coordinate c : tiles.keySet()) {
			CrystalNetworkTile te = tiles.get(c);
			if (te instanceof CrystalTransmitter)
				((CrystalTransmitter)te).clearTargets();
		}
		tiles.clear();
	}

	public boolean checkConnectivity(CrystalElement e, WorldLocation loc, int range) {
		return this.checkConnectivity(e, loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord, range);
	}

	public boolean checkConnectivity(CrystalElement e, World world, int x, int y, int z, int range) {
		CrystalPath p = new PylonFinder(e, world, x, y, z, range).findPylon();
		return p != null && p.canTransmit();
	}

	public void makeRequest(CrystalReceiver r, CrystalElement e, int amount, int range) {
		this.makeRequest(r, e, amount, r.getWorld(), r.getX(), r.getY(), r.getZ(), range);
	}

	public void makeRequest(CrystalReceiver r, CrystalElement e, int amount, World world, int x, int y, int z, int range) {
		if (amount <= 0)
			return;
		CrystalFlow p = new PylonFinder(e, world, x, y, z, range).findPylon(r, amount);
		if (p != null) {
			flows.add(p);
		}
	}

	public void tick(TickType type, Object... data) {
		Iterator<CrystalFlow> it = flows.iterator();
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
				p.receiver.onPathBroken();
				p.resetTiles();
				it.remove();
			}
		}

		losTimer++;
		if (losTimer >= 40) {
			losTimer = 0;
			it = flows.iterator();
			while (it.hasNext()) {
				CrystalFlow p = it.next();
				if (!p.checkLineOfSight()) {
					p.receiver.onPathBroken();
					p.resetTiles();
					it.remove();
				}
			}
		}
	}

	public void clear() {
		//do not clear tiles!
		for (int i = 0; i < flows.size(); i++) {
			CrystalFlow f = flows.get(i);
			f.resetTiles();
		}
		flows.clear();
	}

	public void addTile(CrystalNetworkTile te) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			tiles.put(new Coordinate(te.getX(), te.getY(), te.getZ()), te);
		}
	}

	public void removeTile(CrystalNetworkTile te) {
		tiles.remove(new Coordinate(te.getX(), te.getY(), te.getZ()));
		Iterator<CrystalFlow> it = flows.iterator();
		while (it.hasNext()) {
			CrystalFlow p = it.next();
			if (p.contains(te)) {
				p.resetTiles();
				p.receiver.onPathBroken();
				it.remove();
			}
		}
	}

	public void breakPaths(CrystalNetworkTile te) {
		Iterator<CrystalFlow> it = flows.iterator();
		while (it.hasNext()) {
			CrystalFlow p = it.next();
			if (p.contains(te)) {
				p.receiver.onPathBroken();
				p.resetTiles();
				it.remove();
			}
		}
	}

	ArrayList<CrystalNetworkTile> getTransmittersWithinDofXYZ(World world, int x, int y, int z, double dist, CrystalElement e) {
		dist = dist*dist;
		ArrayList<CrystalNetworkTile> li = new ArrayList();
		for (Coordinate c : tiles.keySet()) {
			CrystalNetworkTile tile = tiles.get(c);
			if (tile instanceof CrystalTransmitter) {
				CrystalTransmitter te = (CrystalTransmitter)tile;
				if (te.canConduct()) {
					if (e == null || te.isConductingElement(e)) {
						double send = te.getSendRange()*te.getSendRange();
						double d = te.getDistanceSqTo(x, y, z);
						if (d <= Math.min(dist, send)) {
							li.add(te);
						}
					}
				}
			}
		}
		return li;
	}

	ArrayList<CrystalNetworkTile> getTilesWithinDofXYZ(World world, int x, int y, int z, double dist) {
		return this.getTransmittersWithinDofXYZ(world, x, y, z, dist, null);
	}

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
