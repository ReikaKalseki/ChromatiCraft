/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutablePair;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EssentiaNetwork {

	private final MultiMap<WorldLocation, WorldLocation> tiles = new MultiMap(new HashSetFactory());
	private final MultiMap<WorldLocation, WorldLocation> nodes = new MultiMap(new MultiMap.ListFactory());
	private final HashMap<ImmutablePair<WorldLocation, WorldLocation>, ArrayList<WorldLocation>> pathList = new HashMap();

	private static final Comparator<NetworkEndpoint> pullComparator = new SuctionComparator(true);
	private static final Comparator<NetworkEndpoint> pushComparator = new SuctionComparator(false);

	public void addTile(TileEntityEssentiaRelay caller, TileEntity te) {
		if (te instanceof TileEntityEssentiaRelay) {
			nodes.addValue(new WorldLocation(caller), new WorldLocation(te));
		}
		else {
			tiles.addValue(new WorldLocation(caller), new WorldLocation(te));
		}
		//ReikaJavaLibrary.pConsole("Adding "+te+" @ "+new WorldLocation(te));
		//this.recalculatePaths();
	}

	public void merge(EssentiaNetwork m) {
		tiles.putAll(m.tiles);
		nodes.putAll(m.nodes);

		for (WorldLocation loc : tiles.keySet()) {
			TileEntity te = loc.getTileEntity();
			if (te instanceof TileEntityEssentiaRelay) {
				((TileEntityEssentiaRelay)te).network = this;
			}
		}

		//this.recalculatePaths();

		m.tiles.clear();
		m.reset();
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
	public void reset() {
		Collection<WorldLocation> set = new ArrayList(tiles.keySet());
		for (WorldLocation loc : set) {
			TileEntity te = loc.getTileEntity();
			if (te instanceof TileEntityEssentiaRelay) {
				((TileEntityEssentiaRelay)te).scan(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
			}
		}
		tiles.clear();
		nodes.clear();
		pathList.clear();
	}

	public EssentiaMovement addEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount) {
		return this.addEssentia(caller, aspect, amount, new WorldLocation(caller).move(callDir, 1));
	}

	public EssentiaMovement addEssentia(TileEntityEssentiaRelay caller, Aspect aspect, int amount, WorldLocation src) {
		ArrayList<EssentiaPath> li = new ArrayList();

		ArrayList<NetworkEndpoint> list = this.collectAllTiles(src);
		Collections.sort(list, pushComparator);
		for (NetworkEndpoint p : list) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				if (p.tile.canInputFrom(dir)) {
					int added = p.tile.addEssentia(aspect, amount, dir);
					if (added > 0) {
						amount -= added;
						ArrayList<WorldLocation> pt = this.getPath(new WorldLocation(caller), p.relayNode);
						if (pt.isEmpty()) {
							//ChromatiCraft.logger.logError("Unable to find path for "+aspect.getName()+" from "+caller+" to "+loc);
						}
						else {

						}
						pt.add(p.point);
						pt.add(0, src);
						li.add(new EssentiaPath(aspect, added, pt)); //ReikaJavaLibrary.makeListFrom(new WorldLocation(caller), node, loc)
						if (amount <= 0) {
							break;
						}
					}
				}
			}
			if (amount <= 0) {
				break;
			}
		}
		return li.isEmpty() ? null : new EssentiaMovement(li);
	}

	private ArrayList<NetworkEndpoint> collectAllTiles(WorldLocation exclude) {
		ArrayList<NetworkEndpoint> li = new ArrayList();
		for (WorldLocation node : tiles.keySet()) {
			Iterator<WorldLocation> it = tiles.get(node).iterator();
			while (it.hasNext()) {
				WorldLocation loc = it.next();
				if (!loc.equals(exclude)) {
					TileEntity te = loc.getTileEntity();
					if (te instanceof IEssentiaTransport) {
						li.add(new NetworkEndpoint(node, loc, (IEssentiaTransport)te));
					}
					else {
						it.remove();
					}
				}
			}
		}
		return li;
	}

	public EssentiaMovement removeEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount) {
		return this.removeEssentia(caller, callDir, aspect, amount, new WorldLocation(caller).move(callDir, 1));
	}

	public EssentiaMovement removeEssentia(TileEntityEssentiaRelay caller, ForgeDirection callDir, Aspect aspect, int amount, WorldLocation tgt) {
		TileEntity target = caller.getAdjacentTileEntity(callDir);
		ArrayList<EssentiaPath> li = new ArrayList();
		ArrayList<NetworkEndpoint> list = this.collectAllTiles(tgt);
		Collections.sort(list, pullComparator);
		for (NetworkEndpoint p : list) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				if (p.tile.canOutputTo(dir)) {
					int rem = p.tile.takeEssentia(aspect, amount, dir);
					if (rem > 0) {
						amount -= rem;
						ArrayList<WorldLocation> pt = this.getPath(p.relayNode, new WorldLocation(caller));
						if (pt.isEmpty()) {
							//ChromatiCraft.logger.logError("Unable to find path for "+aspect.getName()+" from "+loc+" to "+caller);
						}
						else {

						}
						pt.add(0, p.point);
						pt.add(tgt);
						li.add(new EssentiaPath(aspect, rem, pt)); //ReikaJavaLibrary.makeListFrom(loc, node, new WorldLocation(caller))
						if (amount <= 0) {
							break;
						}
					}
				}
			}
			if (amount <= 0) {
				break;
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
		for (WorldLocation loc2 : /*li*/tiles.keySet()) {
			if (!set.contains(loc2)) {
				if (true || loc.isWithinDistOnAllCoords(loc2, TileEntityEssentiaRelay.SEARCH_RANGE)) {
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
		for (WorldLocation node : tiles.keySet()) {
			Iterator<WorldLocation> it = tiles.get(node).iterator();
			while (it.hasNext()) {
				WorldLocation loc = it.next();
				TileEntity te = loc.getTileEntity();
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
		}
		//ReikaJavaLibrary.pConsole(aspect.getName()+":"+sum);
		return sum;
	}

	@Override
	public String toString() {
		return System.identityHashCode(this)+" "+tiles;
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

	private static class NetworkEndpoint {

		public final WorldLocation relayNode;
		public final WorldLocation point;
		private final IEssentiaTransport tile;

		public final int suction;

		private NetworkEndpoint(WorldLocation node, WorldLocation loc, IEssentiaTransport te) {
			relayNode = node;
			point = loc;
			tile = te;

			int maxsuc = 0;
			for (int i = 0; i < 6; i++) {
				maxsuc = Math.max(maxsuc, te.getSuctionAmount(ForgeDirection.VALID_DIRECTIONS[i]));
			}
			suction = maxsuc;
		}

	}

	private static class SuctionComparator implements Comparator<NetworkEndpoint> {

		private final boolean suction;

		private SuctionComparator(boolean suck) {
			suction = suck;
		}

		@Override
		public int compare(NetworkEndpoint o1, NetworkEndpoint o2) {
			int ret = Integer.compare(o1.suction, o2.suction);
			if (!suction)
				ret = -ret;
			return ret;
		}

	}
}
