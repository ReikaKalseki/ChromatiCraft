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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;


public class EssentiaNetwork {

	private static Class jarClass;
	private static Class alembicClass;
	private static Field filterField;
	private static Field amountField;
	private static Field alembicAspectField;
	private static Field alembicAmountField;

	private static final SuctionComparator endpointComparator = new SuctionComparator();

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

	private final HashSet<EssentiaSubnet> subnets = new HashSet();

	private EssentiaNetwork() {

	}
	/*
	private ArrayList<Coordinate> getPath(Coordinate loc, Coordinate loc2) {
		ImmutablePair<Coordinate, Coordinate> locs = new ImmutablePair(loc, loc2);
		ArrayList<Coordinate> li = pathList.get(locs);
		return li != null ? li : new ArrayList();
	}
	 */

	private static boolean LOS(World world, Coordinate c1, Coordinate c2) {
		tracer.setOrigins(c1.xCoord+0.5, c1.yCoord, c1.zCoord+0.5, c2.xCoord+0.5, c2.yCoord+0.5, c2.zCoord+0.5);
		//ReikaJavaLibrary.pConsole(c1+" > "+c2+" = "+tracer.isClearLineOfSight(world));
		return tracer.isClearLineOfSight(world);
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
		private boolean isDirty = false;

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

		public void markDirty() {
			isDirty = true;
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
		public AspectList getPull(World world) {
			return null;
		}

		@Override
		public AspectList getPush(World world) {
			IEssentiaTransport tile = this.getTile(world);
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
		public int addAspect(World world, Aspect a, int amount) { //never allow fill
			return 0;
		}

		@Override
		public boolean canReceive(World world) {
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
		public AspectList getPull(World world) {
			try {
				int amt = 64-amountField.getInt(this.getTile(world));
				return amt > 0 ? new AspectList().add(filter, amt) : null;
			}
			catch (Exception e) {
				e.printStackTrace();
				return new AspectList().add(filter, 1);
			}
		}

		@Override
		public AspectList getPush(World world) {
			return null;
		}

		@Override
		public boolean isValid(World world) {
			try {
				return super.isValid(world) && filterField.get(this.getTile(world)) == filter;
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

		public abstract AspectList getPull(World world);

		public abstract AspectList getPush(World world);

		@Override
		public boolean canReceive(World world) {
			return this.getPush(world) == null && this.getPull(world) != null;
		}

		@Override
		public boolean canEmit(World world) {
			return this.getPull(world) == null && this.getPush(world) != null;
		}

	}

	private static class NetworkEndpoint {

		public final Coordinate point;
		private final HashMap<Coordinate, EssentiaNode> relays = new HashMap();
		protected final int tileHash;

		public final int suction;

		private NetworkEndpoint(Coordinate loc, IEssentiaTransport te) {
			point = loc;
			tileHash = System.identityHashCode(te);

			int maxsuc = 0;
			for (int i = 0; i < 6; i++) {
				maxsuc = Math.max(maxsuc, te.getSuctionAmount(ForgeDirection.VALID_DIRECTIONS[i]));
			}
			if (isFilteredJar(te) != null)
				maxsuc += 100;
			suction = maxsuc;
		}

		public boolean isValid(World world) {
			TileEntity te = point.getTileEntity(world);
			return te != null && System.identityHashCode(te) == tileHash;
		}

		protected final IEssentiaTransport getTile(World world) {
			return (IEssentiaTransport)point.getTileEntity(world);
		}

		public final int getContents(World world, Aspect a) {
			IEssentiaTransport tile = this.getTile(world);
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

		public int addAspect(World world, Aspect a, int amount) {
			IEssentiaTransport tile = this.getTile(world);
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

		public int takeAspect(World world, Aspect a, int amount) {
			IEssentiaTransport tile = this.getTile(world);
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

		public boolean canReceive(World world) {
			return true;
		}

		public boolean canEmit(World world) {
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
			return tileHash+" @ "+point+" suc="+suction;
		}

	}

	private static class SuctionComparator implements Comparator<NetworkEndpoint> {

		private boolean suction;
		private World referenceWorld;

		private void setWorld(World world) {
			referenceWorld = world;
		}

		private void setForPull(boolean pull) {
			suction = pull;
		}

		private void reset() {
			referenceWorld = null;
		}

		public int compare(NetworkEndpoint o1, NetworkEndpoint o2) {
			if (o1 instanceof ActiveEndpoint && o2 instanceof ActiveEndpoint) {
				ActiveEndpoint a1 = (ActiveEndpoint)o1;
				ActiveEndpoint a2 = (ActiveEndpoint)o2;
				AspectList p1 = suction ? a1.getPull(referenceWorld) : a1.getPush(referenceWorld);
				AspectList p2 = suction ? a2.getPull(referenceWorld) : a2.getPush(referenceWorld);
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

	public static class NetworkBuilder {

		public static EssentiaSubnet buildFrom(TileEntityEssentiaRelay te) {
			HashSet<Coordinate> set = new HashSet();
			set.add(new Coordinate(te));
			boolean flag = true;
			while (flag) {
				flag = false;
				for (Coordinate c : set) {
					Collection<Coordinate> li = getNearNodesExcept(te.worldObj, c, set);
					if (li == null)
						return null;
					if (!li.isEmpty()) {
						flag = true;
						for (Coordinate c2 : li) {
							set.add(c2);
						}
					}
				}
			}
			EssentiaSubnet ret = buildNetworkWithNodes(te.worldObj, set);
			return ret;
		}

		private static Collection<Coordinate> getNearNodesExcept(World world, Coordinate c, HashSet<Coordinate> set) {
			Collection<Coordinate> ret = new ArrayList();
			int r = TileEntityEssentiaRelay.SEARCH_RANGE;
			for (int i = -r; i <= r; i++) {
				for (int j = -r; j <= r; j++) {
					for (int k = -r; k <= r; k++) {
						int dx = c.xCoord+i;
						int dy = c.yCoord+j;
						int dz = c.zCoord+k;
						Coordinate c2 = new Coordinate(dx, dy, dz);
						if (set.contains(c2))
							continue;
						if (ChromaTiles.getTile(world, dx, dy, dz) == ChromaTiles.ESSENTIARELAY) {
							EssentiaSubnet net = ((TileEntityEssentiaRelay)world.getTileEntity(dx, dy, dz)).getNetwork();
							if (net != null) {
								net.destroy(world, true);
								return null;
							}
							ret.add(c2);
						}
					}
				}
			}
			return ret;
		}

		private static EssentiaSubnet buildNetworkWithNodes(World world, HashSet<Coordinate> set) {
			EssentiaSubnet net = new EssentiaSubnet(EssentiaNetwork.getNetwork(world), world.provider.dimensionId);
			for (Coordinate c : set) {
				net.addRelay(world, c);
			}
			net.findAllEndpoints(world);
			return net;
		}
	}

	public static class EssentiaSubnet {

		public final int dimension;

		private final EssentiaNetwork parent;
		private final HashMap<Coordinate, EssentiaNode> relays = new HashMap();
		private final HashMap<Coordinate, NetworkEndpoint> endpoints = new HashMap();
		private final HashMap<ImmutablePair<Coordinate, Coordinate>, EssentiaPathCache> pathList = new HashMap();

		private final HashMap<Coordinate, Boolean> renderMap = new HashMap();

		private long lastTick;

		private EssentiaSubnet(EssentiaNetwork n, int dim) {
			dimension = dim;
			parent = n;
			parent.subnets.add(this);
		}

		public void destroy(World world, boolean doDrops) {
			parent.subnets.remove(this);
			for (EssentiaNode n : relays.values()) {
				n.destroy(world, doDrops);
			}
			relays.clear();
			endpoints.clear();
			pathList.clear();
			renderMap.clear();
		}

		private void addRelay(World world, Coordinate c) {
			EssentiaNode n = new EssentiaNode(this, c);
			this.linkNodesTo(world, n);
			relays.put(c, n);
			renderMap.put(c, false);
			((TileEntityEssentiaRelay)c.getTileEntity(world)).setNetwork(this);
		}

		private void linkNodesTo(World world, EssentiaNode n) {
			for (EssentiaNode c : relays.values()) {
				if (this.canConnect(world, n, c)) {
					n.connect(c);
				}
			}
		}

		private boolean canConnect(World world, EssentiaNode n, EssentiaNode c) {
			return n.getDistanceTo(c) <= TileEntityEssentiaRelay.SEARCH_RANGE && LOS(world, n.position, c.position);
		}

		private void findAllEndpoints(World world) {
			for (EssentiaNode c : relays.values()) {
				c.findValidEndpoints(world);
			}
		}

		public EssentiaNode getNode(TileEntityEssentiaRelay te) {
			return relays.get(new Coordinate(te));
		}

		public int countEssentia(World world, Aspect aspect) {
			int sum = 0;
			for (NetworkEndpoint n : endpoints.values()) {
				sum += n.getContents(world, aspect);
			}
			return sum;
		}

		public EssentiaMovement removeEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount) {
			return this.removeEssentia(caller, callDir, aspect, amount, new Coordinate(caller).offset(callDir, 1));
		}

		public EssentiaMovement removeEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount, Coordinate tgt) {
			TileEntity target = caller.getAdjacentTileEntity(callDir);
			ArrayList<EssentiaPath> li = new ArrayList();
			ArrayList<NetworkEndpoint> list = new ArrayList(endpoints.values());
			endpointComparator.setWorld(caller.worldObj);
			endpointComparator.setForPull(true);
			Collections.sort(list, endpointComparator);
			endpointComparator.reset();
			for (NetworkEndpoint p : list) {
				EssentiaPathCache pt = this.getPath(caller.worldObj, p, endpoints.get(new Coordinate(target)));
				if (pt != null && !pt.isEmpty()) {
					int rem = p.takeAspect(caller.worldObj, aspect, amount);
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

		public EssentiaMovement addEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount) {
			return this.addEssentia(caller, aspect, amount, new Coordinate(caller).offset(callDir, 1));
		}

		public EssentiaMovement addEssentia(TileEntityEssentiaRelay caller, Aspect aspect, int amount, Coordinate src) {
			ArrayList<EssentiaPath> li = new ArrayList();
			ArrayList<NetworkEndpoint> list = new ArrayList(endpoints.values());
			endpointComparator.setWorld(caller.worldObj);
			endpointComparator.setForPull(false);
			Collections.sort(list, endpointComparator);
			endpointComparator.reset();
			for (NetworkEndpoint p : list) {
				EssentiaPathCache pt = this.getPath(caller.worldObj, endpoints.get(src), p);
				if (pt != null && !pt.isEmpty()) {
					int added = p.addAspect(caller.worldObj, aspect, amount);
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

		public EssentiaMovement tick(World world) {
			if (lastTick == world.getTotalWorldTime())
				return null;
			lastTick = world.getTotalWorldTime();
			ArrayList<EssentiaPath> li = new ArrayList();
			for (NetworkEndpoint net : endpoints.values()) {
				if (net instanceof ActiveEndpoint) {
					ActiveEndpoint from = (ActiveEndpoint)net;
					AspectList al = from.getPush(world);
					if (al != null && !al.aspects.isEmpty()) {
						for (NetworkEndpoint to : endpoints.values()) {
							if (this.canTransfer(world, from, to)) {
								li.addAll(this.transferEssentia(world, from, to, al));
								if (al.aspects.isEmpty())
									break;
							}
						}
					}
					al = from.getPull(world);
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
			}
			return li.isEmpty() ? null : new EssentiaMovement(li);
		}

		private boolean canTransfer(World world, NetworkEndpoint from, NetworkEndpoint to) {
			if (from == to || from.point.equals(to.point))
				return false;
			if (from instanceof LabelledJarEndpoint)
				return !isJar(to.getTile(world));
			if (isJar(from.getTile(world)) && isJar(to.getTile(world)))
				return to instanceof LabelledJarEndpoint && ((LabelledJarEndpoint)to).getPull(world) != null;
			return from.canEmit(world) && to.canReceive(world) && from.isValid(world) && to.isValid(world);
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
			int has = from.getContents(world, a);
			int amt = Math.min(amount, has);
			int put = to.addAspect(world, a, amt);
			int put2 = from.takeAspect(world, a, put);
			if (put2 < put) {
				to.takeAspect(world, a, put-put2);
			}
			if (put2 <= 0)
				return null;
			//ReikaJavaLibrary.pConsole("Transferred "+put+" & "+put2+" / "+amount+" of "+a.getTag()+" from "+from+" to "+to);
			//ReikaJavaLibrary.pConsole(pt+" of "+from+" , "+to);
			return new EssentiaPath(a, put2, pt);
		}

		@Override
		public String toString() {
			return System.identityHashCode(this)+" "+relays;
		}

		public Collection<Coordinate> getAllFilteredJars() {
			Collection<Coordinate> ret = new HashSet();
			for (NetworkEndpoint n : endpoints.values()) {
				if (n instanceof LabelledJarEndpoint)
					ret.add(n.point);
			}
			return ret;
		}

		public boolean isFilteredJar(Coordinate c) {
			return endpoints.get(c) instanceof LabelledJarEndpoint;
		}

		private EssentiaPathCache getPath(World world, NetworkEndpoint from, NetworkEndpoint to) {
			ImmutablePair<Coordinate, Coordinate> key = new ImmutablePair(from.point, to.point);
			EssentiaPathCache path = pathList.get(key);
			if (path != null) {
				if (path.isDirty) {
					if (!path.validate(world))
						pathList.remove(key);
				}
				return path;
			}
			ArrayList<Coordinate> li = this.calculatePath(world, from, to);
			if (li != null && !li.isEmpty()) {
				path = new EssentiaPathCache(li);
				pathList.put(key, path);
			}
			return path;
		}

		private ArrayList<Coordinate> calculatePath(World world, NetworkEndpoint from, NetworkEndpoint to) {
			for (Coordinate c : from.relays.keySet()) {
				if (to.relays.containsKey(c)) {
					return this.buildPathBetween(from, to, c);
				}
			}
			for (EssentiaNode e : from.relays.values()) {
				for (EssentiaNode e2 : to.relays.values()) {
					ArrayList<Coordinate> li = this.getNodePath(world, e, e2, new HashSet());
					if (li != null) {
						return this.buildPathBetween(from, to, li.toArray(new Coordinate[li.size()]));
					}
				}
			}
			return null;
		}

		private ArrayList<Coordinate> getNodePath(World world, EssentiaNode from, EssentiaNode to, HashSet<Coordinate> visited) {
			if (to.otherNodes.contains(from.position)) {
				return ReikaJavaLibrary.makeListFrom(from.position, to.position);
			}
			for (Coordinate c : to.otherNodes) {
				if (visited.contains(c))
					continue;
				visited.add(c);
				EssentiaNode e = relays.get(c);
				ArrayList<Coordinate> li = this.getNodePath(world, from, e, visited);
				if (li != null) {
					li.add(to.position);
					return li;
				}
			}
			return null;
		}

		private ArrayList<Coordinate> buildPathBetween(NetworkEndpoint from, NetworkEndpoint to, Coordinate... hops) {
			ArrayList<Coordinate> li = new ArrayList();
			li.add(from.point);
			for (Coordinate c : hops) {
				li.add(c);
			}
			li.add(to.point);
			return li;
		}

		public Collection<Coordinate> getAllEndpoints() {
			return Collections.unmodifiableCollection(endpoints.keySet());
		}

		public Map<Coordinate, Boolean> getGeneralizedNetworkRenderer() {
			return Collections.unmodifiableMap(renderMap);
		}
	}

	public static class EssentiaNode {

		public final Coordinate position;

		private final EssentiaSubnet network;
		private final HashSet<Coordinate> otherNodes = new HashSet();
		private final HashMap<Coordinate, NetworkEndpoint> inertEndpoints = new HashMap();
		private final HashMap<Coordinate, ActiveEndpoint> activeEndpoints = new HashMap();

		private EssentiaNode(EssentiaSubnet n, Coordinate c) {
			network = n;
			position = c;
		}

		private void destroy(World world, boolean doDrops) {
			activeEndpoints.clear();
			inertEndpoints.clear();
			otherNodes.clear();
			TileEntity te = position.getTileEntity(world);
			if (te instanceof TileEntityEssentiaRelay) {
				((TileEntityEssentiaRelay)te).reset();
				if (doDrops)
					((TileEntityEssentiaRelay)te).drop();
			}
		}

		public double getDistanceTo(EssentiaNode c) {
			return c.position.getDistanceTo(position);
		}

		private void findValidEndpoints(World world) {
			int r = TileEntityEssentiaRelay.SEARCH_RANGE;
			for (int i = -r; i <= r; i++) {
				for (int j = -r; j <= r; j++) {
					for (int k = -r; k <= r; k++) {
						int dx = position.xCoord+i;
						int dy = position.yCoord+j;
						int dz = position.zCoord+k;
						this.addEndpointAt(world, dx, dy, dz);
					}
				}
			}
		}

		private void addEndpointAt(World world, int x, int y, int z) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof IEssentiaTransport && !(te instanceof TileEntityEssentiaRelay)) {
				Coordinate c = new Coordinate(x, y, z);
				NetworkEndpoint end = network.endpoints.get(c);
				if (end == null)
					end = this.createEndpoint(c, (IEssentiaTransport)te);
				if (end instanceof ActiveEndpoint) {
					activeEndpoints.put(c, (ActiveEndpoint)end);
				}
				else {
					inertEndpoints.put(c, end);
				}
				end.relays.put(position, this);
				network.endpoints.put(c, end);
				network.renderMap.put(c, true);
			}
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

		private void connect(EssentiaNode c) {
			otherNodes.add(c.position);
			c.otherNodes.add(position);
		}

		public Collection<Coordinate> getNeighbors() {
			return Collections.unmodifiableCollection(otherNodes);
		}

		public Collection<Coordinate> getVisibleEndpoints() {
			Collection<Coordinate> c = new HashSet();
			c.addAll(activeEndpoints.keySet());
			c.addAll(inertEndpoints.keySet());
			return c;
		}

	}
}
