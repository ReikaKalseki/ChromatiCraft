/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.mojang.authlib.GameProfile;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.ProductCondition;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary.AlvearyEffect;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.GUI.StatusLogger;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.EnumTolerance;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.api.multiblock.IAlvearyController;
import forestry.api.multiblock.IMultiblockComponent;


public class ChromaBeeHelpers {

	private static HashMap<SelectionKey, EntitySelection> entityLists = new HashMap();
	private static HashMap<ChunkCoordinates, Coordinate> alvearyControllerLocations = new HashMap();
	private static HashMap<ChunkCoordinates, CachedTerritory> territoryCache = new HashMap();

	static List<WeakReference<EntityLivingBase>> getEntityList(AxisAlignedBB box, long time, World world, ChunkCoordinates c, Class ce, IEntitySelector s) {
		if (EntityPlayer.class.isAssignableFrom(ce) || s == ReikaEntityHelper.playerSelector) {
			List<WeakReference<EntityLivingBase>> li = new ArrayList();
			for (EntityPlayer ep : ((List<EntityPlayer>)world.playerEntities)) {
				if (ep.boundingBox.intersectsWith(box))
					li.add(new WeakReference(ep));
			}
			return li;
		}

		SelectionKey sk = new SelectionKey(world, c, ce, s);
		EntitySelection e = entityLists.get(sk);
		if (e == null || e.age > 5) {
			if (e == null) {
				e = new EntitySelection();
				entityLists.put(sk, e);
			}
			e.updateList(box, world, ce, s);
		}
		else if (time > e.lastTick) {
			e.lastTick = time;
			e.age++;
		}
		return e.entityList;
	}

	public static interface ConditionalProductProvider {

		public ItemHashMap<ProductCondition> getConditions();

		public ArrayList<String> getGeneralRequirements();

		public void sendStatusInfo(World world, int x, int y, int z, StatusLogger log, IBeeGenome ibg, IBeeHousing ibh);

	}

	public static interface ConditionalProductBee {

		public ConditionalProductProvider getProductProvider();

	}

	public static class CompoundConditionalProductProvider implements ConditionalProductProvider {

		private Collection<ConditionalProductProvider> list = new ArrayList();
		private Collection<ProductCondition> extras = new ArrayList();

		public CompoundConditionalProductProvider() {

		}

		public void add(ConditionalProductProvider p) {
			list.add(p);
		}

		public void addGeneral(ProductCondition c) {
			extras.add(c);
		}

		@Override
		public ItemHashMap<ProductCondition> getConditions() {
			ItemHashMap<ProductCondition> map = new ItemHashMap();
			for (ConditionalProductProvider p : list) {
				map.putAll(p.getConditions());
			}
			return map;
		}

		@Override
		public ArrayList<String> getGeneralRequirements() {
			ArrayList<String> li = new ArrayList();
			for (ConditionalProductProvider p : list) {
				li.addAll(p.getGeneralRequirements());
			}
			for (ProductCondition c : extras) {
				li.add(c.getDescription());
			}
			return li;
		}

		@Override
		public void sendStatusInfo(World world, int x, int y, int z, StatusLogger log, IBeeGenome ibg, IBeeHousing ibh) {
			for (ConditionalProductProvider p : list) {
				p.sendStatusInfo(world, x, y, z, log, ibg, ibh);
			}
			for (ProductCondition p : extras) {
				log.addStatus(p.getDescription(), p.check(world, x, y, z, ibg, ibh));
			}
		}

		public boolean isEmpty() {
			return list.isEmpty() && extras.isEmpty();
		}

		public boolean checkGeneral(IBeeGenome ibg, IBeeHousing ibh) {
			ChunkCoordinates cc = ibh.getCoordinates();
			for (ProductCondition p : extras) {
				if (!p.check(ibh.getWorld(), cc.posX, cc.posY, cc.posZ, ibg, ibh))
					return false;
			}
			return true;
		}

	}

	public static boolean isBestPossibleBee(IBeeGenome ibg) {
		if (!ibg.getNocturnal() || !ibg.getCaveDwelling() || !ibg.getTolerantFlyer())
			return false;
		if (ibg.getToleranceHumid() != EnumTolerance.BOTH_5 || ibg.getToleranceTemp() != EnumTolerance.BOTH_5)
			return false;
		if (ibg.getFertility() < CrystalBees.superFertility.getAllele().getValue())
			return false;
		if (ibg.getFlowering() < CrystalBees.superFlowering.getAllele().getValue())
			return false;
		if (ibg.getLifespan() < CrystalBees.superLife.getAllele().getValue())
			return false;
		if (ibg.getSpeed() < CrystalBees.superSpeed.getAllele().getValue())
			return false;
		return Arrays.equals(ibg.getTerritory(), CrystalBees.superTerritory.getAllele().getValue());
	}

	static int[] getSearchRange(IBeeGenome ibg, IBeeHousing ibh) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
		int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
		int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
		int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;
		return new int[]{r, r2};
	}

	public static WorldLocation getLocation(IBeeHousing ibh) {
		ChunkCoordinates c = ibh.getCoordinates();
		return new WorldLocation(ibh.getWorld(), c.posX, c.posY, c.posZ);
	}

	public static EntityPlayer getOwner(IBeeHousing ibh) {
		GameProfile p = ibh.getOwner();
		return p != null && p.getId() != null ? ibh.getWorld().func_152378_a(p.getId()) : null;
	}

	public static boolean isLumenAlvearyEffectSelectedAndActive(IBeeHousing ibh, AlvearyEffect e) {
		TileEntityLumenAlveary te = getLumenAlvearyController(ibh, ibh.getWorld(), ibh.getCoordinates());
		return te != null && te.isEffectSelectedAndActive(e);
	}

	public static boolean isLumenAlvearyInfSight(IBeeHousing ibh) {
		TileEntityLumenAlveary te = getLumenAlvearyController(ibh, ibh.getWorld(), ibh.getCoordinates());
		return te != null && te.hasInfiniteAwareness();
	}

	private static class SelectionKey {

		private final int dimension;
		private final ChunkCoordinates location;
		private final Class classType;
		private final IEntitySelector selector;

		private SelectionKey(World world, ChunkCoordinates c, Class ce, IEntitySelector s) {
			dimension = world.provider.dimensionId;
			location = c;
			classType = ce;
			selector = s;
		}

		@Override
		public int hashCode() {
			return dimension ^ location.hashCode() - classType.hashCode() ^ (selector != null ? selector.hashCode() : 0);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof SelectionKey) {
				SelectionKey sk = (SelectionKey)o;
				return sk.dimension == dimension && classType == sk.classType && (selector == sk.selector || (selector != null && sk.selector != null && selector.getClass() == sk.selector.getClass())) && location.equals(sk.location);
			}
			else {
				return false;
			}
		}

	}

	private static class EntitySelection {

		private int age;
		private long lastTick;
		private ArrayList<WeakReference<EntityLivingBase>> entityList = new ArrayList();

		private void updateList(AxisAlignedBB box, World world, Class ce, IEntitySelector s) {
			entityList = new ArrayList();
			for (EntityLivingBase e : ((List<EntityLivingBase>)world.selectEntitiesWithinAABB(ce, box, s))) {
				entityList.add(new WeakReference(e));
			}
			age = 0;
		}

	}

	public static TileEntityLumenAlveary getLumenAlvearyController(IBeeHousing ibh, World world, ChunkCoordinates cc) {
		if (ibh instanceof IAlvearyComponent) {
			ibh = ((IAlvearyComponent)ibh).getMultiblockLogic().getController();
		}
		if (ibh instanceof IAlvearyController) {
			Coordinate loc = alvearyControllerLocations.get(cc);
			if (loc != null) {
				TileEntity te = loc.getTileEntity(world);
				if (te instanceof TileEntityLumenAlveary) {
					return (TileEntityLumenAlveary)te;
				}
				else {
					alvearyControllerLocations.remove(cc);
				}
			}
			for (IMultiblockComponent ib : ((IAlvearyController)ibh).getComponents()) {
				if (ib instanceof TileEntityLumenAlveary) {
					TileEntityLumenAlveary te = (TileEntityLumenAlveary)ib;
					alvearyControllerLocations.put(cc, new Coordinate(te));
					return te;
				}
			}
		}
		return null;
	}

	public static boolean checkProgression(World world, IBeeHousing ibh, ProgressStage p) {
		TileEntityLumenAlveary te = getLumenAlvearyController(ibh, world, ibh.getCoordinates());
		if (te != null) {
			return p.isPlayerAtStage(te.getPlacer());
		}
		GameProfile gp = ibh.getOwner();
		return gp != null && p.isPlayerAtStage(world, gp.getId());
	}

	public static int[] getEffectiveTerritory(IBeeHousing ibh, ChunkCoordinates c, IBeeGenome ibg, long time) {
		CachedTerritory t = territoryCache.get(c);
		if (t == null || t.territory == null || t.age >= 20) {
			if (t == null) {
				t = new CachedTerritory();
				territoryCache.put(c, t);
			}
			t.recalculate(ibg, ibh);
		}
		else if (time > t.lastTick) {
			t.lastTick = time;
			t.age++;
		}
		return t.territory;
	}

	private static class CachedTerritory {

		private int[] territory;
		private int age;
		private long lastTick;

		private void recalculate(IBeeGenome ibg, IBeeHousing ibh) {
			age = 0;
			territory = ReikaBeeHelper.getFinalTerritory(ibg, ibh);
		}

	}

}
