/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityWirelessPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.ASM.InterfaceInjector.Injectable;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.OpenPathFinder;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.LocationTerminus;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.TerminationCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.PluralMap;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;

import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.subtile.ISubTileContainer;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.subtile.SubTileGenerating;

@Injectable(value = {"vazkii.botania.api.mana.IManaCollector"})
public class TileEntityManaBooster extends TileEntityWirelessPowered {

	private static final int FLOWER_RANGE = 8;
	private static final int POOL_RANGE = 12;

	public static final int BOOST_FACTOR = 6;

	private static final ElementTagCompound required = new ElementTagCompound();
	private static final ElementTagCompound requestable = new ElementTagCompound();

	private static Field manaField;
	private static Field spreaderBindField;

	static {
		if (ModList.BOTANIA.isLoaded()) {
			try {
				initFields();
			}
			catch (Exception e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.BOTANIA, e);
				ChromatiCraft.logger.logError("Could not access generating flower mana variable!");
				e.printStackTrace();
			}
		}

		required.addTag(CrystalElement.BLACK, 250);
		required.addTag(CrystalElement.GRAY, 100);
		required.addTag(CrystalElement.YELLOW, 100);
		required.addTag(CrystalElement.LIME, 50);

		requestable.addTag(required);
		requestable.addTag(CrystalElement.LIGHTBLUE, 400);
	}

	@ModDependent(ModList.BOTANIA)
	private static void initFields() throws Exception {
		manaField = SubTileGenerating.class.getDeclaredField("mana");
		manaField.setAccessible(true);

		spreaderBindField = SubTileGenerating.class.getDeclaredField("linkedCollector");
		spreaderBindField.setAccessible(true);
	}

	private final StepTimer flowerScan = new StepTimer(50);
	private final StepTimer poolScan = new StepTimer(300);

	private final ArrayList<Coordinate> poolCache = new ArrayList();
	private final ArrayList<Coordinate> flowerCache = new ArrayList();
	private final PluralMap<ManaPath> pathCache = new PluralMap(2);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			if (this.getTicksExisted()%8 == 0) {
				for (CrystalElement e : required.elementSet()) {
					if (energy.getValue(e) < this.getMaxStorage(e))
						this.requestEnergy(e, this.getMaxStorage(e)-energy.getValue(e));
				}
			}

			if (energy.containsAtLeast(required)) {
				if (this.getTicksExisted()%8 == 0)
					energy.subtract(required);
			}
			else
				return;

			boolean accel = energy.getValue(CrystalElement.LIGHTBLUE) >= requestable.getValue(CrystalElement.LIGHTBLUE);
			if (accel) {
				energy.subtract(CrystalElement.LIGHTBLUE, requestable.getValue(CrystalElement.LIGHTBLUE));
			}

			if (this.isTickingNaturally()) {
				flowerScan.update();
				poolScan.update();
			}
			if (flowerScan.checkCap()) {
				this.scanAndCache(world, x, y, z, false);
			}
			if (poolScan.checkCap()) {
				this.scanAndCache(world, x, y, z, true);
			}

			if (!flowerCache.isEmpty() && !poolCache.isEmpty() && this.getTicksExisted()%(accel ? 4 : 8) == 0) {
				int idx = rand.nextInt(flowerCache.size());
				Coordinate c = flowerCache.get(idx);
				int mana = this.receiveMana(world, c, Integer.MAX_VALUE, false);
				if (mana == -1) {
					flowerCache.remove(idx);
				}
				else if (mana > 0) {
					idx = rand.nextInt(poolCache.size());
					Coordinate c2 = poolCache.get(idx);
					int space = this.dumpMana(world, c2, Integer.MAX_VALUE, false);
					if (space == -1) {
						poolCache.remove(idx);
					}
					else if (space > 0) {
						int transfer = Math.min(mana, space/BOOST_FACTOR);
						if (transfer > 0) {
							this.doManaTransfer(world, c, c2, transfer, accel);
						}
					}
				}
			}
		}
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 1200;
	}

	@Override
	protected int getReceiveRange(CrystalElement e) {
		return 8;
	}

	private void doManaTransfer(World world, Coordinate from, Coordinate to, int transfer, boolean accelerated) {
		//for testing
		//this.receiveMana(world, from, transfer, true);
		//this.dumpMana(world, to, transfer*BOOST_FACTOR, true);

		ManaPath path = this.getPathForBurst(world, from, to);
		if (path != null) {
			EntityChromaManaBurst e = new EntityChromaManaBurst(world, transfer, path, accelerated);
			world.spawnEntityInWorld(e);
		}
	}

	private ManaPath getPathForBurst(World world, Coordinate from, Coordinate to) {
		ManaPath p = pathCache.get(from, to);
		if (p == null || !p.isValid(world)) {
			p = this.calculateManaPath(world, from, to);
			pathCache.put(p, from, to);
		}
		return p;
	}

	private ManaPath calculateManaPath(World world, Coordinate from, Coordinate to) {
		Coordinate mid = new Coordinate(this);
		PropagationCondition f = new OpenPathFinder(from, mid, 40);
		TerminationCondition t = new LocationTerminus(mid);
		//Search s = new Search(from.xCoord, from.yCoord, from.zCoord);
		LinkedList<Coordinate> li = Search.getPath(world, from.xCoord, from.yCoord, from.zCoord, t, f);
		f = new OpenPathFinder(mid, to, 40);
		t = new LocationTerminus(to);
		LinkedList<Coordinate> li2 = Search.getPath(world, mid.xCoord, mid.yCoord, mid.zCoord, t, f);
		if (li != null && li2 != null) {
			HashSet<Coordinate> set = new HashSet();

			Spline s1 = new Spline(SplineType.CENTRIPETAL);
			s1.addPoint(new BasicSplinePoint(new DecimalPosition(from)));
			int i = -1;
			for (Coordinate c : li) {
				if (i%4 == 0 && c != li.getLast()) {
					if (!c.equals(to) && !c.equals(from) && !c.equals(mid))
						set.add(c);
					s1.addPoint(new BasicSplinePoint(new DecimalPosition(c)));
				}
				i++;
			}
			s1.addPoint(new BasicSplinePoint(new DecimalPosition(mid)));

			Spline s2 = new Spline(SplineType.CENTRIPETAL);
			s2.addPoint(new BasicSplinePoint(new DecimalPosition(mid)));
			i = -1;
			for (Coordinate c : li2) {
				if (i%4 == 0 && c != li2.getLast()) {
					if (!c.equals(to) && !c.equals(from) && !c.equals(mid))
						set.add(c);
					s2.addPoint(new BasicSplinePoint(new DecimalPosition(c)));
				}
				i++;
			}
			s2.addPoint(new BasicSplinePoint(new DecimalPosition(to)));

			ManaPath p = new ManaPath(from, to, mid, s1, s2, set);
			return p;
		}
		return null;
	}

	@ModDependent(ModList.BOTANIA)
	public static int receiveMana(World world, Coordinate c, int amt, boolean doRemove) {
		TileEntity te = c.getTileEntity(world);
		if (!isGeneratingFlower(te))
			return -1;
		try {
			SubTileGenerating gen = (SubTileGenerating)((ISubTileContainer)te).getSubTile();
			int has = manaField.getInt(gen);
			if (has >= gen.getMaxMana()/4) {
				int rem = Math.min(amt, has);
				if (doRemove && rem > 0) {
					manaField.set(gen, has-rem);
					c.triggerBlockUpdate(world, false);
				}
				return rem;
			}
			else {
				return 0;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@ModDependent(ModList.BOTANIA)
	public static int dumpMana(World world, Coordinate c, int amt, boolean doAdd) {
		TileEntity te = c.getTileEntity(world);
		if (!isValidPool(te))
			return -1;
		int add = Math.min(amt, ((ISparkAttachable)te).getAvailableSpaceForMana());
		if (doAdd && add > 0) {
			((IManaPool)te).recieveMana(add);
			c.triggerBlockUpdate(world, false);
		}
		return add;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.scanAndCache(world, x, y, z, true);
		this.scanAndCache(world, x, y, z, false);
	}

	private void scanAndCache(World world, int x, int y, int z, boolean pools) {
		ArrayList<Coordinate> set = pools ? poolCache : flowerCache;
		set.clear();
		int r = pools ? POOL_RANGE : FLOWER_RANGE;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				if (Math.abs(i)+Math.abs(k) <= r) {
					for (int j = 1; j <= 3; j++) {
						int dx = x+i;
						int dy = y-j;
						int dz = z+k;
						boolean flag = !world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz);
						if (flag) {
							TileEntity te = world.getTileEntity(dx, dy, dz);
							if (pools ? this.isValidPool(te) : this.isGeneratingFlower(te)) {
								set.add(new Coordinate(dx, dy, dz));
								if (!pools) {
									this.bindFlowerToPool(te);
								}
							}
							break;
						}
					}
				}
			}
		}
	}

	@ModDependent(ModList.BOTANIA)
	private void bindFlowerToPool(TileEntity te) {
		if (te instanceof ISubTileContainer) {
			SubTileEntity st = ((ISubTileContainer)te).getSubTile();
			if (st instanceof SubTileGenerating) {
				try {
					spreaderBindField.set(st, this);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@ModDependent(ModList.BOTANIA)
	private static boolean isValidPool(TileEntity te) {
		return te instanceof IManaPool && te instanceof ISparkAttachable;
	}

	@ModDependent(ModList.BOTANIA)
	private static boolean isGeneratingFlower(TileEntity te) {
		if (te instanceof ISubTileContainer) {
			SubTileEntity st = ((ISubTileContainer)te).getSubTile();
			if (st instanceof SubTileGenerating) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.MANABOOSTER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	//@Override
	public boolean isFull() {
		return true;
	}

	//@Override
	public void recieveMana(int mana) {

	}

	//@Override
	public boolean canRecieveManaFromBursts() {
		return false;
	}

	//@Override
	public int getCurrentMana() {
		return 0;
	}

	//@Override
	public void onClientDisplayTick() {

	}

	//@Override
	@ModDependent(ModList.BOTANIA)
	public float getManaYieldMultiplier(IManaBurst burst) {
		return 1;
	}

	//@Override
	public int getMaxMana() {
		return Integer.MAX_VALUE;
	}

	public static class ManaPath {

		public final Coordinate manaSource;
		public final Coordinate manaTarget;
		public final DecimalPosition boosterCenter;
		public final List<DecimalPosition> pathToBooster;
		public final List<DecimalPosition> pathToPool;
		public final DecimalPosition boosterEntry;
		public final DecimalPosition boosterExit;
		private final HashSet<Coordinate> coords;

		private ManaPath(Coordinate c1, Coordinate c2, Coordinate booster, Spline s1, Spline s2, HashSet<Coordinate> set) {
			manaSource = c1;
			manaTarget = c2;
			boosterCenter = new DecimalPosition(booster);
			pathToBooster = Collections.unmodifiableList(s1.get(8, false));
			pathToPool = Collections.unmodifiableList(s2.get(8, false));
			boosterEntry = pathToBooster.get(pathToBooster.size()-1);
			boosterExit = pathToPool.get(0);
			coords = set;
			for (DecimalPosition p : pathToBooster) {
				coords.add(new Coordinate(p.xCoord, p.yCoord, p.zCoord));
			}
			for (DecimalPosition p : pathToPool) {
				coords.add(new Coordinate(p.xCoord, p.yCoord, p.zCoord));
			}
		}

		public boolean isValid(World world) {
			for (Coordinate c : coords) {
				if (!c.getBlock(world).isAir(world, c.xCoord, c.yCoord, c.zCoord))
					return false;
			}
			return true;
		}

	}

}
