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

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.WorldLocation;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public class CrystalNetworker implements TickHandler {

	public static final CrystalNetworker instance = new CrystalNetworker();

	private final ArrayList<CrystalNetworkTile> tiles = new ArrayList();
	private final ArrayList<CrystalFlow> flows = new ArrayList();

	private int losTimer = 0;

	private CrystalNetworker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void clearOnUnload(WorldEvent.Unload evt) {
		this.clear();
		for (int i = 0; i < tiles.size(); i++) {
			CrystalNetworkTile te = tiles.get(i);
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

	public void tick(Object... data) {
		Iterator<CrystalFlow> it = flows.iterator();
		while (it.hasNext()) {
			CrystalFlow p = it.next();
			if (p.transmitter.canConduct() && p.canTransmit()) {
				int amt = p.drain();
				p.receiver.receiveElement(p.element, amt);
				if (p.isComplete()) {
					p.resetTiles();
					it.remove();
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
			if (!tiles.contains(te)) {
				tiles.add(te);
			}
		}
	}

	public void removeTile(CrystalNetworkTile te) {
		tiles.remove(te);
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
		for (int i = 0; i < tiles.size(); i++) {
			CrystalNetworkTile tile = tiles.get(i);
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
	public Phase getPhase() {
		return Phase.START;
	}

	@Override
	public String getLabel() {
		return "Crystal Networker";
	}

}
