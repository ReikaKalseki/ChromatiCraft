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

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;


public class EssentiaNetwork {

	private static Class jarClass;
	private static Field filterField;

	static {
		if (ModList.THAUMCRAFT.isLoaded()) {
			try {
				jarClass = Class.forName("thaumcraft.common.tiles.TileJarFillable");
				filterField = jarClass.getField("aspectFilter");
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not fetch Warded Jar class");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
			}
		}
	}

	private static final Comparator<NetworkEndpoint> pullComparator = new SuctionComparator(true);
	private static final Comparator<NetworkEndpoint> pushComparator = new SuctionComparator(false);

	private final HashMap<WorldLocation, NetworkEndpoint> endpoints = new HashMap();
	private final HashSet<ActiveEndpoint> activeLocations = new HashSet();
	private final HashSet<WorldLocation> nodes = new HashSet();
	private final HashMap<ImmutablePair<WorldLocation, WorldLocation>, ArrayList<WorldLocation>> pathList = new HashMap();

	private long lastTick;

	public void addNode(TileEntityEssentiaRelay te) {
		nodes.add(new WorldLocation(te));
	}

	public void addEndpoint(TileEntityEssentiaRelay caller, IEssentiaTransport te) {
		WorldLocation loc = new WorldLocation((TileEntity)te);
		NetworkEndpoint n = endpoints.get(loc);
		if (n == null) {
			n = this.createEndpoint(loc, te);
			if (n instanceof ActiveEndpoint) {
				activeLocations.add((ActiveEndpoint)n);
			}
			endpoints.put(loc, n);
		}
		n.nodeAccesses.add(new WorldLocation(caller));
	}

	private NetworkEndpoint createEndpoint(WorldLocation loc, IEssentiaTransport te) {
		Aspect a = isFilteredJar(te);
		if (a != null)
			return new LabelledJarEndpoint(loc, te, a);
		return new NetworkEndpoint(loc, te);
	}

	public void merge(EssentiaNetwork m) {
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
		activeLocations.addAll(m.activeLocations);

		for (WorldLocation loc : nodes) {
			TileEntity te = loc.getTileEntity();
			if (te instanceof TileEntityEssentiaRelay) {
				((TileEntityEssentiaRelay)te).network = this;
			}
		}

		//this.recalculatePaths();

		pathList.clear();

		m.endpoints.clear();
		m.nodes.clear();
		m.pathList.clear();
	}
	/*
	private void recalculatePaths() {
		pathList.clear();
		for (WorldLocation loc : tiles.keySet()) {
			for (WorldLocation loc2 : tiles.keySet()) {
				if (!loc.equals(loc2)) {
					ImmutablePair<WorldLocation, WorldLocation> locs = new ImmutablePair(loc, loc2);
					HashSet<WorldLocation> seen = new HashSet();
					seen.add(loc);
					seen.add(loc2);
					ArrayList<WorldLocation> li = this.findPathFrom(loc, loc2, seen);
					if (li != null) {
						ReikaJavaLibrary.pConsole(locs+": "+li);
						pathList.put(locs, li);
					}
				}
			}
		}
	}

	private ArrayList<WorldLocation> findPathFrom(WorldLocation loc, WorldLocation loc2, HashSet<WorldLocation> seen) {
		int r = TileEntityEssentiaRelay.SEARCH_RANGE;
		if (loc.isWithinDistOnAllCoords(loc2, r)) {
			return ReikaJavaLibrary.makeListFrom(loc, loc2);
		}
		else {
			ArrayList<WorldLocation> li = (ArrayList<WorldLocation>)nodes.get(loc);
			for (WorldLocation loc3 : li) {
				if (!seen.contains(loc3)) {
					seen.add(loc3);
					ArrayList<WorldLocation> li2 = this.findPathFrom(loc3, loc2, seen);
					if (li2 != null) {
						ArrayList<WorldLocation> ret = new ArrayList();
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

	public void tick(World world) {
		if (lastTick == world.getTotalWorldTime())
			return;
		lastTick = world.getTotalWorldTime();
		if (!activeLocations.isEmpty()) {
			HashSet<WorldLocation> locs = new HashSet();
			AspectList pushing = new AspectList();
			for (ActiveEndpoint n : activeLocations) {
				AspectList al = n.getPush();
				if (al != null && !al.aspects.isEmpty()) {
					for (Aspect a : al.aspects.keySet()) {
						pushing.add(a, al.getAmount(a));
						locs.add(n.point);
					}
				}
			}
			if (!pushing.aspects.isEmpty()) {
				for (Aspect a : pushing.aspects.keySet()) {
					for (NetworkEndpoint n : endpoints.values()) {
						if (!locs.contains(n.point)) {
							int rem = n.addAspect(a, pushing.getAmount(a));
							pushing.reduce(a, rem);
							if (pushing.getAmount(a) <= 0)
								break;
						}
					}
				}
			}
		}
	}

	public EssentiaMovement addEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount) {
		return this.addEssentia(caller, aspect, amount, new WorldLocation(caller).move(callDir, 1));
	}

	public EssentiaMovement addEssentia(TileEntityEssentiaRelay caller, Aspect aspect, int amount, WorldLocation src) {
		ArrayList<EssentiaPath> li = new ArrayList();

		ArrayList<NetworkEndpoint> list = new ArrayList(endpoints.values());
		Collections.sort(list, pushComparator);
		for (NetworkEndpoint p : list) {
			int added = p.addAspect(aspect, amount);
			if (added > 0) {
				amount -= added;
				ArrayList<WorldLocation> pt = this.getPath(src, p.point);
				li.add(new EssentiaPath(aspect, added, pt)); //ReikaJavaLibrary.makeListFrom(new WorldLocation(caller), node, loc)
				if (amount <= 0) {
					break;
				}
			}
		}
		return li.isEmpty() ? null : new EssentiaMovement(li);
	}

	public EssentiaMovement removeEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount) {
		return this.removeEssentia(caller, callDir, aspect, amount, new WorldLocation(caller).move(callDir, 1));
	}

	public EssentiaMovement removeEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount, WorldLocation tgt) {
		TileEntity target = caller.getAdjacentTileEntity(callDir);
		ArrayList<EssentiaPath> li = new ArrayList();
		ArrayList<NetworkEndpoint> list = new ArrayList(endpoints.values());
		Collections.sort(list, pullComparator);
		for (NetworkEndpoint p : list) {
			int rem = p.takeAspect(aspect, amount);
			if (rem > 0) {
				amount -= rem;
				ArrayList<WorldLocation> pt = this.getPath(p.point, new WorldLocation(target));
				li.add(new EssentiaPath(aspect, rem, pt)); //ReikaJavaLibrary.makeListFrom(loc, node, new WorldLocation(caller))
				if (amount <= 0) {
					break;
				}
			}
		}
		return li.isEmpty() ? null : new EssentiaMovement(li);
	}
	/*
	private ArrayList<WorldLocation> getPath(WorldLocation loc, WorldLocation loc2) {
		ImmutablePair<WorldLocation, WorldLocation> locs = new ImmutablePair(loc, loc2);
		ArrayList<WorldLocation> li = pathList.get(locs);
		return li != null ? li : new ArrayList();
	}
	 */

	private ArrayList<WorldLocation> getPath(WorldLocation loc, WorldLocation loc2) {
		ArrayList<WorldLocation> path = new ArrayList();
		path.add(loc);
		HashSet<WorldLocation> set = new HashSet();
		set.add(loc);
		WorldLocation tg = this.getNearestLocationExcept(loc, set);
		//ReikaJavaLibrary.pConsole(loc+"  &  "+loc2+"  =  "+tg);
		if (tg == null) {
			return new ArrayList();
		}
		if (tg.equals(loc2)) {
			path.add(loc2);
		}
		else {
			this.recursePathTo(tg, loc2, path, set);
		}
		//ReikaJavaLibrary.pConsole(loc+"  >  "+loc2+"  =  "+path);
		return path;
	}

	private void recursePathTo(WorldLocation loc, WorldLocation loc2, ArrayList<WorldLocation> path, HashSet<WorldLocation> set) {
		path.add(loc);
		set.add(loc);
		WorldLocation tg = this.getNearestLocationExcept(loc, set);
		if (tg == null) {
			path.clear();
			return;
		}
		if (tg.equals(loc2)) {
			path.add(loc2);
		}
		else {
			this.recursePathTo(tg, loc2, path, set);
		}
	}

	private WorldLocation getNearestLocationExcept(WorldLocation loc, HashSet<WorldLocation> set) {
		//ArrayList<WorldLocation> li = (ArrayList<WorldLocation>)nodes.get(loc);
		WorldLocation ret = null;
		double d = Double.POSITIVE_INFINITY;
		for (WorldLocation loc2 : /*li*/endpoints.keySet()) {
			if (!set.contains(loc2)) {
				if (loc.isWithinDistOnAllCoords(loc2, TileEntityEssentiaRelay.SEARCH_RANGE)) {
					double dist = loc2.getDistanceTo(loc);
					if (dist < d) {
						d = dist;
						ret = loc2;
					}
				}
			}
		}
		//ReikaJavaLibrary.pConsole("Found "+ret+" for "+loc);
		return ret;
	}

	public int countEssentia(Aspect aspect) {
		int sum = 0;
		Iterator<Entry<WorldLocation, NetworkEndpoint>> it = endpoints.entrySet().iterator();
		while (it.hasNext()) {
			Entry<WorldLocation, NetworkEndpoint> e = it.next();
			TileEntity te = e.getKey().getTileEntity();
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

	public void reset() {
		for (WorldLocation loc : nodes) {
			TileEntity te = loc.getTileEntity();
			if (te instanceof TileEntityEssentiaRelay)
				((TileEntityEssentiaRelay)te).network = null;
		}
		endpoints.clear();
		activeLocations.clear();
		nodes.clear();
	}

	private static Aspect isFilteredJar(IEssentiaTransport te) {
		if (jarClass != null && jarClass.isAssignableFrom(te.getClass())) {
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

	public static class EssentiaPath {

		private final ArrayList<WorldLocation> path;

		public final WorldLocation target;
		public final WorldLocation source;

		public final Aspect aspect;
		public final int amount;

		private EssentiaPath(Aspect a, int amt, ArrayList<WorldLocation> li) {
			aspect = a;
			amount = amt;
			path = li;

			target = li.isEmpty() ? null : li.get(0);
			source = li.isEmpty() ? null : li.get(li.size()-1);
		}

		public void update(World world, int x, int y, int z) {
			this.doParticles(world, x, y, z);
		}

		private void doParticles(World world, int x, int y, int z) {
			for (int i = 0; i < path.size()-1; i++) {
				WorldLocation loc1 = path.get(i);
				WorldLocation loc2 = path.get(i+1);
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

	private static class LabelledJarEndpoint extends ActiveEndpoint {

		private final Aspect filter;

		private LabelledJarEndpoint(WorldLocation loc, IEssentiaTransport te, Aspect a) {
			super(loc, te);
			filter = a;
		}

		@Override
		public AspectList getPull() {
			return new AspectList().add(filter, 1);
		}

		@Override
		public AspectList getPush() {
			return null;
		}

	}

	private static abstract class ActiveEndpoint extends NetworkEndpoint {

		private ActiveEndpoint(WorldLocation loc, IEssentiaTransport te) {
			super(loc, te);
		}

		public abstract AspectList getPull();

		public abstract AspectList getPush();

	}

	private static class NetworkEndpoint {

		public final HashSet<WorldLocation> nodeAccesses = new HashSet();
		public final WorldLocation point;
		private final IEssentiaTransport tile;

		public final int suction;

		private NetworkEndpoint(WorldLocation loc, IEssentiaTransport te) {
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
			return ret;
		}

		@Override
		public int hashCode() {
			return point.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof NetworkEndpoint && ((NetworkEndpoint)o).point.equals(point);
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
