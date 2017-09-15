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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.ProductCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.GUI.StatusLogger;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.EnumTolerance;


public class ChromaBeeHelpers {

	public static interface ConditionalProductProvider {

		public ItemHashMap<ProductCondition> getConditions();

		public ArrayList<String> getGeneralRequirements();

		public void sendStatusInfo(World world, int x, int y, int z, StatusLogger log, IBeeGenome ibg, IBeeHousing ibh);

	}

	public static interface ConditionalProductBee {

		public ConditionalProductProvider getProductProvider();

	}

	static class CompoundConditionalProductProvider implements ConditionalProductProvider {

		private Collection<ConditionalProductProvider> list = new ArrayList();
		private Collection<ProductCondition> extras = new ArrayList();

		CompoundConditionalProductProvider() {

		}

		void add(ConditionalProductProvider p) {
			list.add(p);
		}

		void addGeneral(ProductCondition c) {
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

}
