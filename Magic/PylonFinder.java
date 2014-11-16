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
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public class PylonFinder {

	private final LinkedList<WorldLocation> nodes = new LinkedList();
	private final CrystalNetworker net;
	private static final RayTracer tracer;

	//private final int stepRange;
	private final CrystalReceiver target;
	private final CrystalElement element;

	private static final HashMap<WorldLocation, EnumMap<CrystalElement, Collection<CrystalPath>>> paths = new HashMap();

	PylonFinder(CrystalElement e, CrystalReceiver r) {
		element = e;
		target = r;
		//stepRange = r;
		net = CrystalNetworker.instance;
	}

	CrystalPath findPylon() {
		CrystalPath p = this.checkExistingPaths();
		//ReikaJavaLibrary.pConsole(p != null ? p.nodes.size() : "null", Side.SERVER);
		if (p != null)
			return p;
		if (!this.anyConnectedSources())
			return null;

		this.findFrom(target);
		//ReikaJavaLibrary.pConsole(this.toString());
		if (this.isComplete()) {
			CrystalPath path = new CrystalPath(element, nodes);
			this.addValidPath(path);
			return path;
		}
		return null;
	}

	CrystalFlow findPylon(int amount, int maxthru) {
		CrystalPath p = this.checkExistingPaths();
		if (p != null)
			return new CrystalFlow(p, target, amount, maxthru);
		if (!this.anyConnectedSources())
			return null;

		this.findFrom(target);
		//ReikaJavaLibrary.pConsole(this.toString());
		if (this.isComplete()) {
			CrystalFlow flow = new CrystalFlow(target, element, amount, nodes, maxthru);
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
		return !net.getNearbyReceivers(s, element).isEmpty();
	}

	private CrystalPath checkExistingPaths() {
		EnumMap<CrystalElement, Collection<CrystalPath>> map = paths.get(new WorldLocation(target.getWorld(), target.getX(), target.getY(), target.getZ()));
		if (map != null) {
			Collection<CrystalPath> c = map.get(element);
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
		EnumMap<CrystalElement, Collection<CrystalPath>> map = paths.get(p.origin);
		if (map == null) {
			Collection<CrystalPath> c = new ArrayList();
			map = new EnumMap(CrystalElement.class);
			c.add(p);
			map.put(element, c);
			paths.put(p.origin, map);
		}
		else {
			Collection<CrystalPath> c = map.get(p.element);
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
		//ReikaJavaLibrary.pConsole(paths, Side.SERVER);
	}

	static void removePathsWithTile(CrystalNetworkTile te) {
		if (te == null)
			return;
		EnumMap<CrystalElement, Collection<CrystalPath>> map = paths.get(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()));
		if (map != null) {
			for (CrystalElement e : map.keySet()) {
				Collection<CrystalPath> c = map.get(e);
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

	private void findFrom(CrystalReceiver r) {
		if (nodes.contains(getLocation(r))) {
			return;
		}
		nodes.add(getLocation(r));
		ArrayList<CrystalTransmitter> li = net.getTransmittersTo(r, element);
		//ReikaJavaLibrary.pConsole(li, element == CrystalElement.BLACK);
		for (CrystalTransmitter te : li) {
			if (/*!te.needsLineOfSight() || */this.lineOfSight(r, te)) {
				if (te instanceof CrystalSource) {
					nodes.add(getLocation(te));
					return;
				}
				else if (te instanceof CrystalRepeater) {
					this.findFrom((CrystalRepeater)te);
				}
			}
		}
		if (!this.isComplete())
			nodes.removeLast();
	}

	private boolean lineOfSight(CrystalNetworkTile te1, CrystalNetworkTile te) {
		return lineOfSight(te1.getWorld(), te1.getX(), te1.getY(), te1.getZ(), te.getX(), te.getY(), te.getZ());
	}

	private boolean lineOfSight(World world, int x, int y, int z, CrystalNetworkTile te) {
		return lineOfSight(world, x, y, z, te.getX(), te.getY(), te.getZ());
	}

	public static boolean lineOfSight(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		RayTracer ray = tracer.setOrigins(x1, y1, z1, x2, y2, z2);
		return ray.isClearLineOfSight(world);
	}

	static {
		tracer = new RayTracer(0, 0, 0, 0, 0, 0);
		tracer.setInternalOffsets(0.5, 0.5, 0.5);
		tracer.softBlocksOnly = true;/*
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

}
