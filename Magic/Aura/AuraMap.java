/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Aura;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.AuraSource;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class AuraMap {

	public static final AuraMap instance = new AuraMap();

	private AuraMap() {

	}

	private final ArrayList<AuraLocation> auraSources = new ArrayList();

	public Collection<AuraLocation> getAuraSources() {
		return Collections.unmodifiableCollection(auraSources);
	}

	public ArrayList<AuraLocation> getAuraSourcesWithinDOfXYZ(World world, int x, int y, int z, double maxdist) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < auraSources.size(); i++) {
			AuraLocation loc = auraSources.get(i);
			double dist = loc.getDistanceFrom(x, y, z);
			if (dist <= maxdist) {
				li.add(loc);
			}
		}
		return li;
	}

	public void addAuraSource(AuraSource source, World world, int x, int y, int z) {
		AuraLocation loc = new AuraLocation(source, world, x, y, z);
		auraSources.add(loc);
	}

	public static class AuraLocation {

		public final AuraSource source;
		public final int xCoord;
		public final int yCoord;
		public final int zCoord;
		public final int dimensionID;

		private AuraLocation(AuraSource source, World world, int x, int y, int z) {
			this.source = source;
			xCoord = x;
			yCoord = y;
			zCoord = z;
			dimensionID = world.provider.dimensionId;
		}

		public double getDistanceFrom(int x, int y, int z) {
			return ReikaMathLibrary.py3d(xCoord-x, yCoord-y, zCoord-z);
		}

		public boolean isInWorld(World world) {
			return dimensionID == world.provider.dimensionId;
		}

		public int getAuraStrengthAt(CrystalElement e, World world, int x, int y, int z) {
			if (!this.isInWorld(world))
				return 0;
			int base = source.getAuras().getValue(e);
			if (base == 0)
				return 0;
			double d = this.getDistanceFrom(x, y, z);
			return this.calculateStrength(base, d);
		}

		public ElementTagCompound getAuraStrengthsAt(World world, int x, int y, int z) {
			if (!this.isInWorld(world))
				return null;
			ElementTagCompound base = source.getAuras();
			if (base == null || base.isEmpty())
				return null;
			double d = this.getDistanceFrom(x, y, z);
			ElementTagCompound e = new ElementTagCompound();
			for (int i = 0; i < CrystalElement.elements.length; i++) {
				CrystalElement el = CrystalElement.elements[i];
				e.addTag(el, this.calculateStrength(base.getValue(el), d));
			}
			return e;
		}

		private int calculateStrength(int base, double dist) {
			return (int)(base*source.getCoefficient()/Math.pow(dist, source.getDistancePower()));
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof AuraLocation) {
				AuraLocation a = (AuraLocation)o;
				return a.source == source && a.xCoord == xCoord && a.yCoord == yCoord && a.zCoord == zCoord && a.dimensionID == dimensionID;
			}
			return false;
		}

	}

}
