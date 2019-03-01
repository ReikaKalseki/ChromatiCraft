/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;


public class EssentiaNetwork {

	private static Class jarClass;
	private static Class alembicClass;
	private static Field filterField;
	private static Field amountField;
	private static Field alembicAspectField;
	private static Field alembicAmountField;

	private static final Comparator<NetworkEndpoint> pullComparator = new SuctionComparator(true);
	private static final Comparator<NetworkEndpoint> pushComparator = new SuctionComparator(false);

	private static final RayTracer tracer = new RayTracer(0, 0, 0, 0, 0, 0);

	private static final HashMap<Integer, EssentiaNetwork> networks = new HashMap();

	static {
		if (ModList.THAUMCRAFT.isLoaded()) {
			try {
				jarClass = Class.forName("thaumcraft.common.tiles.TileJarFillable");
				filterField = jarClass.getField("aspectFilter");
				amountField = jarClass.getField("amount");

				alembicClass = Class.forName("thaumcraft.common.tiles.TileAlembic");
				alembicAspectField = alembicClass.getField("aspect");
				alembicAmountField = alembicClass.getField("amount");
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not fetch Warded Jar class");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
			}
		}

		tracer.airOnly = true;
	}

	public static EssentiaNetwork getNetwork(World world) {
		int dim = world.provider.dimensionId;
		EssentiaNetwork e = networks.get(dim);
		if (e == null) {
			e = new EssentiaNetwork();
			networks.put(dim, e);
		}
		return e;
	}

	private final HashMap<Coordinate, NetworkEndpoint> endpoints = new HashMap();
	private final HashMap<Coordinate, ActiveEndpoint> activeLocations = new HashMap();
	private final HashSet<Coordinate> nodes = new HashSet();
	private final HashMap<ImmutablePair<Coordinate, Coordinate>, EssentiaPathCache> pathList = new HashMap();

	private long lastTick;

	private EssentiaNetwork() {

	}

	public void addNode(TileEntityEssentiaRelay te) {
		nodes.add(new Coordinate(te));
		pathList.clear();
		this.rebuildNetworkDisplay(te.worldObj);
	}

	public void addEndpoint(TileEntityEssentiaRelay caller, IEssentiaTransport te) {
		Coordinate loc = new Coordinate((TileEntity)te);
		NetworkEndpoint n = endpoints.get(loc);
		NetworkEndpoint n2 = this.createEndpoint(loc, te);
		if (n == null || n.getClass() != n2.getClass()) {
			if (n2 instanceof ActiveEndpoint) {
				activeLocations.put(loc, (ActiveEndpoint)n2);
			}
			endpoints.put(loc, n2);
			n2.nodeAccesses.add(new Coordinate(caller));
		}
		else {
			n.nodeAccesses.add(new Coordinate(caller));
		}
		this.rebuildNetworkDisplay(caller.worldObj);
	}

	private NetworkEndpoint createEndpoint(Coordinate loc, IEssentiaTransport te) {
		Aspect a = isFilteredJar(te);
		if (a != null)
			return new LabelledJarEndpoint(loc, te, a);
		if (te.getClass() == alembicClass) {
			return new AlembicEndpoint(loc, te);
		}
		return new NetworkEndpoint(loc, te);
	}

	/*
	public void merge(World world, EssentiaNetwork m) {
		for (NetworkEndpoint n : m.endpoints.values()) {
			NetworkEndpoint at = endpoints.get(n.point);
			if (at == null) {
				endpoints.put(n.point, n);
			}
			else {
				at.nodeAccesses.addAll(n.nodeAccesses);
			}
		}
		nodes.addAll(m.nodes);
		activeLocations.putAll(m.activeLocations);

		for (Coordinate loc : nodes) {
			TileEntity te = loc.getTileEntity(world);
			if (te instanceof TileEntityEssentiaRelay) {
				((TileEntityEssentiaRelay)te).network = this;
			}
		}

		//this.recalculatePaths();

		pathList.clear();
		this.rebuildNetworkDisplay(world);

		m.endpoints.clear();
		m.nodes.clear();
		m.pathList.clear();
	}*/

	private void rebuildNetworkDisplay(World world) {
		for (Coordinate loc : nodes) {
			TileEntityEssentiaRelay te = (TileEntityEssentiaRelay)loc.getTileEntity(world);
			te.networkCoords.clear();
			for (Coordinate node : nodes) {
				te.networkCoords.put(node, true);
			}
			for (Coordinate node : endpoints.keySet()) {
				te.networkCoords.put(node, false);
			}
		}
	}

	/*
	private void recalculatePaths() {
		pathList.clear();
		for (Coordinate loc : tiles.keySet()) {
			for (Coordinate loc2 : tiles.keySet()) {
				if (!loc.equals(loc2)) {
					ImmutablePair<Coordinate, Coordinate> locs = new ImmutablePair(loc, loc2);
					HashSet<Coordinate> seen = new HashSet();
					seen.add(loc);
					seen.add(loc2);
					ArrayList<Coordinate> li = this.findPathFrom(loc, loc2, seen);
					if (li != null) {
						ReikaJavaLibrary.pConsole(locs+": "+li);
						pathList.put(locs, li);
					}
				}
			}
		}
	}

	private ArrayList<Coordinate> findPathFrom(Coordinate loc, Coordinate loc2, HashSet<Coordinate> seen) {
		int r = TileEntityEssentiaRelay.SEARCH_RANGE;
		if (loc.isWithinDistOnAllCoords(loc2, r)) {
			return ReikaJavaLibrary.makeListFrom(loc, loc2);
		}
		else {
			ArrayList<Coordinate> li = (ArrayList<Coordinate>)nodes.get(loc);
			for (Coordinate loc3 : li) {
				if (!seen.contains(loc3)) {
					seen.add(loc3);
					ArrayList<Coordinate> li2 = this.findPathFrom(loc3, loc2, seen);
					if (li2 != null) {
						ArrayList<Coordinate> ret = new ArrayList();
						ret.addAll(li2);
						ret.add(0, loc);
						return ret;
					}
					seen.remove(loc3);
				}
			}
		}
		return null;
	}
	 */

	public EssentiaMovement tick(World world) {
		if (lastTick == world.getTotalWorldTime())
			return null;
		lastTick = world.getTotalWorldTime();
		Iterator<NetworkEndpoint> it = endpoints.values().iterator();
		while (it.hasNext()) {
			NetworkEndpoint n = it.next();
			if (!n.isValid(world)) {
				it.remove();
				activeLocations.remove(n.point);
			}
		}
		if (!activeLocations.isEmpty()) {
			ArrayList<EssentiaPath> li = new ArrayList();
			for (ActiveEndpoint from : activeLocations.values()) {
				AspectList al = from.getPush();
				if (al != null && !al.aspects.isEmpty()) {
					for (NetworkEndpoint to : endpoints.values()) {
						if (this.canTransfer(world, from, to)) {
							li.addAll(this.transferEssentia(world, from, to, al));
							if (al.aspects.isEmpty())
								break;
						}
					}
				}
				al = from.getPull();
				if (al != null && !al.aspects.isEmpty()) {
					for (NetworkEndpoint to : endpoints.values()) {
						if (this.canTransfer(world, to, from)) {
							li.addAll(this.transferEssentia(world, to, from, al));
							if (al.aspects.isEmpty())
								break;
						}
					}
				}
			}
			//ReikaJavaLibrary.pConsole(li, !li.isEmpty());
			return li.isEmpty() ? null : new EssentiaMovement(li);
		}
		return null;
	}

	private boolean canTransfer(World world, NetworkEndpoint from, NetworkEndpoint to) {
		if (from == to || from.point.equals(to.point))
			return false;
		if (from instanceof LabelledJarEndpoint)
			return !isJar(to.tile);
		if (isJar(from.tile) && isJar(to.tile))
			return to instanceof LabelledJarEndpoint && ((LabelledJarEndpoint)to).getPull() != null;
		return from.canEmit() && to.canReceive() && from.isValid(world) && to.isValid(world);
	}

	private ArrayList<EssentiaPath> transferEssentia(World world, NetworkEndpoint from, NetworkEndpoint to, AspectList al) {
		//ReikaJavaLibrary.pConsole("Attempting transfer of "+ReikaThaumHelper.aspectsToString(al)+" from "+from+" to "+to);
		ArrayList<EssentiaPath> ret = new ArrayList();
		Iterator<Entry<Aspect, Integer>> it = al.aspects.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Aspect, Integer> e = it.next();
			Aspect a = e.getKey();
			int amt = e.getValue();
			EssentiaPath p = this.transferEssentia(world, from, to, a, Math.min(amt, TileEntityEssentiaRelay.THROUGHPUT));
			if (p != null) {
				ret.add(p);
				e.setValue(amt-p.amount);
				if (e.getValue() <= 0)
					it.remove();
			}
		}
		//ReikaJavaLibrary.pConsole(ret, !ret.isEmpty());
		return ret;
	}

	private EssentiaPath transferEssentia(World world, NetworkEndpoint from, NetworkEndpoint to, Aspect a, int amount) {
		EssentiaPathCache pt = this.getPath(world, from, to);
		if (pt == null || pt.isEmpty())
			return null;
		int has = from.getContents(a);
		int amt = Math.min(amount, has);
		int put = to.addAspect(a, amt);
		int put2 = from.takeAspect(a, put);
		if (put2 < put) {
			to.takeAspect(a, put-put2);
		}
		if (put2 <= 0)
			return null;
		//ReikaJavaLibrary.pConsole("Transferred "+put+" & "+put2+" / "+amount+" of "+a.getTag()+" from "+from+" to "+to);
		//ReikaJavaLibrary.pConsole(pt+" of "+from+" , "+to);
		return new EssentiaPath(a, put2, pt);
	}

	public EssentiaMovement addEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount) {
		return this.addEssentia(caller, aspect, amount, new Coordinate(caller).offset(callDir, 1));
	}

	public EssentiaMovement addEssentia(TileEntityEssentiaRelay caller, Aspect aspect, int amount, Coordinate src) {
		this.addEndpoint(caller, (IEssentiaTransport)src.getTileEntity(caller.worldObj));
		ArrayList<EssentiaPath> li = new ArrayList();
		ArrayList<NetworkEndpoint> list = this.getTerminusList(caller.worldObj);
		Collections.sort(list, pushComparator);
		for (NetworkEndpoint p : list) {
			EssentiaPathCache pt = this.getPath(caller.worldObj, endpoints.get(src), p);
			if (pt != null && !pt.isEmpty()) {
				int added = p.addAspect(aspect, amount);
				if (added > 0) {
					amount -= added;
					li.add(new EssentiaPath(aspect, added, pt)); //ReikaJavaLibrary.makeListFrom(new Coordinate(caller), node, loc)
					if (amount <= 0) {
						break;
					}
				}
			}
		}
		return li.isEmpty() ? null : new EssentiaMovement(li);
	}

	public EssentiaMovement removeEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount) {
		return this.removeEssentia(caller, callDir, aspect, amount, new Coordinate(caller).offset(callDir, 1));
	}

	public EssentiaMovement removeEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount, Coordinate tgt) {
		TileEntity target = caller.getAdjacentTileEntity(callDir);
		this.addEndpoint(caller, (IEssentiaTransport)target);
		ArrayList<EssentiaPath> li = new ArrayList();
		ArrayList<NetworkEndpoint> list = this.getTerminusList(caller.worldObj);
		Collections.sort(list, pullComparator);
		for (NetworkEndpoint p : list) {
			EssentiaPathCache pt = this.getPath(caller.worldObj, p, endpoints.get(new Coordinate(target)));
			if (pt != null && !pt.isEmpty()) {
				int rem = p.takeAspect(aspect, amount);
				if (rem > 0) {
					amount -= rem;
					li.add(new EssentiaPath(aspect, rem, pt)); //ReikaJavaLibrary.makeListFrom(loc, node, new Coordinate(caller))
					if (amount <= 0) {
						break;
					}
				}
			}
		}
		return li.isEmpty() ? null : new EssentiaMovement(li);
	}
	/*
	private ArrayList<Coordinate> getPath(Coordinate loc, Coordinate loc2) {
		ImmutablePair<Coordinate, Coordinate> locs = new ImmutablePair(loc, loc2);
		ArrayList<Coordinate> li = pathList.get(locs);
		return li != null ? li : new ArrayList();
	}
	 */

	private ArrayList<NetworkEndpoint> getTerminusList(World world) {
		ArrayList<NetworkEndpoint> li = new ArrayList();
		Iterator<NetworkEndpoint> it = endpoints.values().iterator();
		while (it.hasNext()) {
			NetworkEndpoint n = it.next();
			if (n.isValid(world))
				li.add(n);
			else
				it.remove();
		}
		return li;
	}

	private EssentiaPathCache getPath(World world, NetworkEndpoint loc, NetworkEndpoint loc2) {
		pathList.clear();
		ImmutablePair<Coordinate, Coordinate> key = new ImmutablePair(loc.point, loc2.point);
		EssentiaPathCache path = pathList.get(key);
		if (path != null) {
			if (!path.validate(world)) {
				pathList.remove(key);
			}
			else {
				return path;
			}
		}
		ArrayList<Coordinate> li = this.calculatePath(world, loc, loc2);
		if (li != null && !li.isEmpty() && li.get(0).equals(loc.point) && li.get(li.size()-1).equals(loc2.point)) {
			path = new EssentiaPathCache(li);
			pathList.put(key, path);
		}
		return path;
	}

	private ArrayList<Coordinate> calculatePath(World world, NetworkEndpoint loc, NetworkEndpoint loc2) {
		if (loc.point.isWithinDistOnAllCoords(loc2.point, TileEntityEssentiaRelay.SEARCH_RANGE)) {
			ArrayList<Coordinate> li = new ArrayList();
			li.add(loc.point);
			if (!loc.nodeAccesses.isEmpty()) {
				li.add(loc.nodeAccesses.iterator().next());
			}
			if (!loc2.nodeAccesses.isEmpty()) {
				Coordinate loc3 = loc2.nodeAccesses.iterator().next();
				if (!li.contains(loc3))
					li.add(loc3);
			}
			li.add(loc2.point);
			return li;
		}
		EssentiaPathSearch s = new EssentiaPathSearch();
		HashSet<Coordinate> set = new HashSet();
		s.path.add(loc.point);
		s.looked.add(loc.point);
		for (Coordinate n : loc.nodeAccesses) {
			if (LOS(world, loc.point, n))
				this.recurseTo(world, n, loc2, s);
		}
		//ReikaJavaLibrary.pConsole(s.path, s.isComplete);
		return s.path;
	}

	private void recurseTo(World world, Coordinate loc, NetworkEndpoint seek, EssentiaPathSearch s) {
		if (s.isComplete)
			return;
		s.path.add(loc);
		s.looked.add(loc);
		ArrayList<Coordinate> li = this.getNextLocations(world, loc, s);
		//ReikaJavaLibrary.pConsole(li+" from "+loc+" S="+s.looked);
		for (Coordinate n2 : li) {
			if (seek.nodeAccesses.contains(n2)) {
				s.path.add(n2);
				s.path.add(seek.point);
				s.isComplete = true;
				return;
			}
		}
		for (Coordinate n2 : li) {
			this.recurseTo(world, n2, seek, s);
		}
		if (s.isComplete)
			return;
		s.path.remove(s.path.size()-1);
	}

	private static boolean LOS(World world, Coordinate c1, Coordinate c2) {
		tracer.setOrigins(c1.xCoord, c1.yCoord, c1.zCoord, c2.xCoord, c2.yCoord, c2.zCoord);
		//ReikaJavaLibrary.pConsole(c1+" > "+c2+" = "+tracer.isClearLineOfSight(world));
		return tracer.isClearLineOfSight(world);
	}

	private ArrayList<Coordinate> getNextLocations(World world, Coordinate loc, EssentiaPathSearch s) {
		ArrayList<Coordinate> li = new ArrayList();
		for (Coordinate loc2 : nodes) {
			//ReikaJavaLibrary.pConsole(loc2, loc.equals(new Coordinate(499, 4, 549)));
			if (loc2.getTileEntity(world) instanceof TileEntityEssentiaRelay && !s.looked.contains(loc2)) {
				if (loc.isWithinDistOnAllCoords(loc2, TileEntityEssentiaRelay.SEARCH_RANGE)) {
					tracer.setOrigins(loc.xCoord, loc.yCoord, loc.zCoord, loc2.xCoord, loc2.yCoord, loc2.zCoord);
					//ReikaJavaLibrary.pConsole(loc+" > "+loc2+" = "+tracer.isClearLineOfSight(world), loc2.equals(507, 4, 560));
					if (this.LOS(world, loc, loc2))
						li.add(loc2);
				}
			}
		}
		return li;
	}

	public int countEssentia(World world, Aspect aspect) {
		int sum = 0;
		Iterator<Entry<Coordinate, NetworkEndpoint>> it = endpoints.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Coordinate, NetworkEndpoint> e = it.next();
			TileEntity te = e.getKey().getTileEntity(world);
			if (te instanceof IEssentiaTransport) {
				IEssentiaTransport p = (IEssentiaTransport)te;
				for (int i = 0; i < 6; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					if (p.canOutputTo(dir)) {
						if (p instanceof IAspectContainer) {
							AspectList al = ((IAspectContainer)p).getAspects();
							if (al != null) {
								for (Aspect a : al.aspects.keySet()) {
									if (a == aspect) {
										sum += p.getEssentiaAmount(dir);
									}
								}
							}
						}
						else {
							Aspect a = p.getEssentiaType(dir);
							if (a == aspect) {
								sum += p.getEssentiaAmount(dir);
							}
						}
					}
				}
			}
			else {
				it.remove();
			}
		}
		//ReikaJavaLibrary.pConsole(aspect.getName()+":"+sum);
		return sum;
	}

	@Override
	public String toString() {
		return System.identityHashCode(this)+" "+nodes;
	}

	private void reset(World world) {
		/*
		for (Coordinate loc : nodes) {
			TileEntity te = loc.getTileEntity(world);
			if (te instanceof TileEntityEssentiaRelay)
				((TileEntityEssentiaRelay)te).network = null;
		}*/
		endpoints.clear();
		activeLocations.clear();
		nodes.clear();
		pathList.clear();
	}

	public void removeNode(TileEntityEssentiaRelay te) {
		nodes.remove(new Coordinate(te));
		pathList.clear();
	}

	public Collection<Coordinate> getAllFilteredJars() {
		Collection<Coordinate> ret = new HashSet();
		for (NetworkEndpoint n : endpoints.values()) {
			if (n instanceof LabelledJarEndpoint)
				ret.add(n.point);
		}
		return ret;
	}

	public HashSet<Coordinate> getDirectlyAccessibleEndpoints(TileEntityEssentiaRelay te) {
		return this.getDirectlyAccessibleEndpoints(new Coordinate(te));
	}

	private HashSet<Coordinate> getDirectlyAccessibleEndpoints(Coordinate c) {
		HashSet<Coordinate> ret = new HashSet();
		for (NetworkEndpoint n : endpoints.values()) {
			if (n.nodeAccesses.contains(c)) {
				ret.add(n.point);
			}
		}
		return ret;
	}

	public Collection<Coordinate> getAllAccessibleEndpoints(TileEntityEssentiaRelay te, boolean los) {
		Coordinate loc = new Coordinate(te);
		HashSet<Coordinate> ret = this.getDirectlyAccessibleEndpoints(loc);
		/*
		for (Coordinate c : ret) {
			NetworkEndpoint n = endpoints.get(c);
			for (NetworkEndpoint n2 : endpoints.values()) {
				if (!ret.contains(n2)) {
					if (this.getPath(te.worldObj, n, n2) != null) {
						ret.add(n2.point);
					}
				}
			}
		}*/
		HashSet<Coordinate> path = new HashSet();
		this.recursiveFind(te.worldObj, loc, ret, path, los);

		return ret;
	}

	private void recursiveFind(World world, Coordinate from, HashSet<Coordinate> ret, HashSet<Coordinate> path, boolean los) {
		path.add(from);
		for (Coordinate c : nodes) {
			if (!path.contains(c)) {
				if (c.isWithinDistOnAllCoords(from, TileEntityEssentiaRelay.SEARCH_RANGE)) {
					if (!los || LOS(world, from, c)) {
						ret.addAll(this.getDirectlyAccessibleEndpoints(c));
						//ReikaJavaLibrary.pConsole(c);
						this.recursiveFind(world, c, ret, path, los);
					}
				}
			}
		}
	}

	public boolean isFilteredJar(Coordinate c) {
		return endpoints.get(c) instanceof LabelledJarEndpoint;
	}

	private static Aspect isFilteredJar(IEssentiaTransport te) {
		if (isJar(te)) {
			Aspect a;
			try {
				a = (Aspect)filterField.get(te);
				return a;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static boolean isJar(IEssentiaTransport te) {
		return jarClass != null && jarClass.isAssignableFrom(te.getClass());
	}

	public static class EssentiaMovement {

		public final int totalAmount;
		private final ArrayList<EssentiaPath> paths;

		private EssentiaMovement(ArrayList<EssentiaPath> li) {
			paths = li;
			int sum = 0;
			for (EssentiaPath p : paths) {
				sum += p.amount;
			}
			totalAmount = sum;
		}

		public Collection<EssentiaPath> paths() {
			return Collections.unmodifiableList(paths);
		}

	}

	private static class EssentiaPathSearch {

		private final ArrayList<Coordinate> path = new ArrayList();
		private final HashSet<Coordinate> looked = new HashSet();

		private boolean isComplete;

	}

	private static class EssentiaPathCache {

		private final ArrayList<Coordinate> path;

		private EssentiaPathCache(ArrayList<Coordinate> li) {
			path = li;
		}

		public boolean validate(World world) {
			for (int i = 0; i < path.size()-1; i++) {
				Coordinate loc1 = path.get(i);
				Coordinate loc2 = path.get(i+1);
				if (!LOS(world, loc1, loc2))
					return false;
			}
			return true;
		}

		public boolean isEmpty() {
			return path.isEmpty();
		}

	}

	public static class EssentiaPath {

		private final ArrayList<Coordinate> path;

		public final Coordinate target;
		public final Coordinate source;

		public final Aspect aspect;
		public final int amount;

		private EssentiaPath(Aspect a, int amt, EssentiaPathCache p) {
			aspect = a;
			amount = amt;
			path = p.path;

			target = path.isEmpty() ? null : path.get(0);
			source = path.isEmpty() ? null : path.get(path.size()-1);
		}

		public void update(World world, int x, int y, int z) {
			this.doParticles(world, x, y, z);
		}

		private void doParticles(World world, int x, int y, int z) {
			for (int i = 0; i < path.size()-1; i++) {
				Coordinate loc1 = path.get(i);
				Coordinate loc2 = path.get(i+1);
				//this.sendParticle(world, loc1.xCoord, loc2.xCoord, loc1.yCoord, loc2.yCoord, loc1.zCoord, loc2.zCoord);
				ReikaPacketHelper.sendStringIntPacket(ChromatiCraft.packetChannel, ChromaPackets.ESSENTIAPARTICLE.ordinal(), new PacketTarget.RadiusTarget(world, x, y, z, 32), aspect.getTag(), loc1.xCoord, loc2.xCoord, loc1.yCoord, loc2.yCoord, loc1.zCoord, loc2.zCoord, amount);
			}
		}

		@SideOnly(Side.CLIENT)
		public static void sendParticle(World world, int x1, int x2, int y1, int y2, int z1, int z2, String asp, int amt) {
			Aspect a = Aspect.getAspect(asp);
			int l = 100;
			double dx = x2-x1;
			double dy = y2-y1;
			double dz = z2-z1;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			double v = 0.25;
			double vx = v*dx/dd;
			double vy = v*dy/dd;
			double vz = v*dz/dd;

			/*

			double r = 0.3125*2;
			double dr = 0.0625;
			for (double d = -r; d <= r; d += dr) {
				double px = x1+0.5+dx/dd*d;
				double py = y1+0.5+dy/dd*d;
				double pz = z1+0.5+dz/dd*d;
				float s = (float)(1.5+amt*(2D-d*d*8));
				EntityFX fx = new EntityBlurFX(world, px, py, pz, vx, vy, vz).setColor(a.getColor()).setLife(l).setScale(s).setRapidExpand().markDestination(x2, y2, z2);//.setIcon(ChromaIcons.TRANSFADE).setBasicBlend();
				EntityFX fx2 = new EntityBlurFX(world, px, py, pz, vx, vy, vz).setColor(0xffffff).setLife(l).setScale(s/2).setRapidExpand().markDestination(x2, y2, z2);//.setIcon(ChromaIcons.TRANSFADE).setBasicBlend();
				fx.noClip = true;
				fx2.noClip = true;
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
			}

			 */

			int i = 0;
			for (double d = 0; d <= dd; d += 0.125) {
				double px = x1+0.5+dx*d/dd;
				double py = y1+0.5+dy*d/dd;
				double pz = z1+0.5+dz*d/dd;
				double ds = Math.min(dd-d, d);
				float s = (float)(1.5+(amt/4D)*ds/1D);
				EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(a.getColor()).setLife(l).setScale(s).setRapidExpand();//.setIcon(ChromaIcons.TRANSFADE).setBasicBlend();
				EntityFX fx2 = new EntityBlurFX(world, px, py, pz).setColor(0xffffff).setLife(l).setScale(s/2).setRapidExpand();//.setIcon(ChromaIcons.TRANSFADE).setBasicBlend();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
				i++;
			}
		}

		@Override
		public String toString() {
			return amount+" of "+aspect.getName()+" along "+path;
		}

	}

	private static class AlembicEndpoint extends ActiveEndpoint {

		private AlembicEndpoint(Coordinate loc, IEssentiaTransport te) {
			super(loc, te);
		}

		@Override
		public AspectList getPull() {
			return null;
		}

		@Override
		public AspectList getPush() {
			try {
				Aspect a = (Aspect)alembicAspectField.get(tile);
				return a != null ? new AspectList().add(a, alembicAmountField.getInt(tile)) : null;
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public int addAspect(Aspect a, int amount) { //never allow fill
			return 0;
		}

		@Override
		public boolean canReceive() {
			return false;
		}

	}

	private static class LabelledJarEndpoint extends ActiveEndpoint {

		private final Aspect filter;

		private LabelledJarEndpoint(Coordinate loc, IEssentiaTransport te, Aspect a) {
			super(loc, te);
			filter = a;
		}

		@Override
		public AspectList getPull() {
			try {
				int amt = 64-amountField.getInt(tile);
				return amt > 0 ? new AspectList().add(filter, amt) : null;
			}
			catch (Exception e) {
				e.printStackTrace();
				return new AspectList().add(filter, 1);
			}
		}

		@Override
		public AspectList getPush() {
			return null;
		}

		@Override
		public boolean isValid(World world) {
			try {
				return super.isValid(world) && filterField.get(tile) == filter;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

	}

	private static abstract class ActiveEndpoint extends NetworkEndpoint {

		private ActiveEndpoint(Coordinate loc, IEssentiaTransport te) {
			super(loc, te);
		}

		public abstract AspectList getPull();

		public abstract AspectList getPush();

		@Override
		public boolean canReceive() {
			return this.getPush() == null && this.getPull() != null;
		}

		@Override
		public boolean canEmit() {
			return this.getPull() == null && this.getPush() != null;
		}

	}

	private static class NetworkEndpoint {

		public final HashSet<Coordinate> nodeAccesses = new HashSet();
		public final Coordinate point;
		protected final IEssentiaTransport tile;

		public final int suction;

		private NetworkEndpoint(Coordinate loc, IEssentiaTransport te) {
			point = loc;
			tile = te;

			int maxsuc = 0;
			for (int i = 0; i < 6; i++) {
				maxsuc = Math.max(maxsuc, te.getSuctionAmount(ForgeDirection.VALID_DIRECTIONS[i]));
			}
			if (isFilteredJar(te) != null)
				maxsuc += 100;
			suction = maxsuc;
		}

		public boolean isValid(World world) {
			return point.getTileEntity(world) == tile;
		}

		public final int getContents(Aspect a) {
			if (tile instanceof TileEntityAspectJar) {
				return ((TileEntityAspectJar)tile).getAmount(a);
			}
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				if (a == tile.getEssentiaType(dir)) {
					int ret = tile.getEssentiaAmount(dir);
					if (ret > 0)
						return ret;
				}
			}
			return 0;
		}

		public int addAspect(Aspect a, int amount) {
			int ret = 0;
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				if (tile.canInputFrom(dir)) {
					int added = tile.addEssentia(a, amount, dir);
					ret += added;
					amount -= added;
					if (amount <= 0)
						break;
				}
			}
			//ReikaJavaLibrary.pConsole("Added "+ret+" of "+a.getTag()+" to "+tile+" @ "+point, ret > 0);
			return ret;
		}

		public int takeAspect(Aspect a, int amount) {
			int ret = 0;
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				if (tile.canOutputTo(dir)) {
					int rem = tile.takeEssentia(a, amount, dir);
					ret += rem;
					amount -= rem;
					if (amount <= 0)
						break;
				}
			}
			//ReikaJavaLibrary.pConsole("Took "+ret+" of "+a.getTag()+" from "+tile+" @ "+point, ret > 0);
			return ret;
		}

		public boolean canReceive() {
			return true;
		}

		public boolean canEmit() {
			return true;
		}

		@Override
		public final int hashCode() {
			return point.hashCode();
		}

		@Override
		public final boolean equals(Object o) {
			return o != null && o.getClass() == this.getClass() && ((NetworkEndpoint)o).point.equals(point);
		}

		@Override
		public final String toString() {
			return tile+" @ "+point+" suc="+suction;
		}

	}

	private static class SuctionComparator implements Comparator<NetworkEndpoint> {

		private final boolean suction;

		private SuctionComparator(boolean suck) {
			suction = suck;
		}

		public int compare(NetworkEndpoint o1, NetworkEndpoint o2) {
			if (o1 instanceof ActiveEndpoint && o2 instanceof ActiveEndpoint) {
				ActiveEndpoint a1 = (ActiveEndpoint)o1;
				ActiveEndpoint a2 = (ActiveEndpoint)o2;
				AspectList p1 = suction ? a1.getPull() : a1.getPush();
				AspectList p2 = suction ? a2.getPull() : a2.getPush();
				if (p1 == null && p2 == null) {
					return 0;
				}
				else if (p1 == null) {
					return 1;
				}
				else if (p2 == null) {
					return -1;
				}
				else {
					return 0;
				}
			}
			if (o1 instanceof ActiveEndpoint) {
				return -1;
			}
			if (o2 instanceof ActiveEndpoint) {
				return 1;
			}
			else {
				int ret = Integer.compare(o1.suction, o2.suction);
				if (!suction)
					ret = -ret;
				return ret;
			}
		}
	}
}
