/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.API.CrystalElementProxy;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayMap;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public final class AbilityRituals {

	private final OneWayMap<Ability, AbilityRitual> data = new OneWayMap();

	public static final AbilityRituals instance = new AbilityRituals();

	private static Collection<WorldLocation> tables = new ArrayList();

	private int maxCost = 0;
	private int maxTotalCost = 0;

	private AbilityRituals() {
		this.addRituals();
	}

	private void addRituals() {
		AbilityRitual rit = new AbilityRitual(Chromabilities.REACH);
		rit.addAura(CrystalElement.LIME, 20000);
		rit.addAura(CrystalElement.GRAY, 10000);
		rit.addAura(CrystalElement.PURPLE, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.MAGNET);
		rit.addAura(CrystalElement.LIME, 5000);
		rit.addAura(CrystalElement.WHITE, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.SONIC);
		rit.addAura(CrystalElement.BLACK, 5000);
		rit.addAura(CrystalElement.YELLOW, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.SHIFT);
		rit.addAura(CrystalElement.LIME, 25000);
		rit.addAura(CrystalElement.YELLOW, 50000);
		rit.addAura(CrystalElement.BROWN, 10000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.HEAL);
		rit.addAura(CrystalElement.MAGENTA, 50000);
		rit.addAura(CrystalElement.LIGHTBLUE, 10000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.SHIELD);
		rit.addAura(CrystalElement.RED, 20000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.FIREBALL);
		rit.addAura(CrystalElement.ORANGE, 10000);
		rit.addAura(CrystalElement.PINK, 4000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.COMMUNICATE);
		rit.addAura(CrystalElement.BLACK, 40000);
		rit.addAura(CrystalElement.RED, 10000);
		rit.addAura(CrystalElement.PINK	, 12000);
		rit.addAura(CrystalElement.LIGHTGRAY, 8000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.HEALTH);
		rit.addAura(CrystalElement.MAGENTA, 50000);
		rit.addAura(CrystalElement.PURPLE, 25000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.PYLON);
		rit.addAura(CrystalElement.BLACK, 2000);
		rit.addAura(CrystalElement.YELLOW, 5000);
		rit.addAura(CrystalElement.RED, 25000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.LIGHTNING);
		rit.addAura(CrystalElement.BLACK, 5000);
		rit.addAura(CrystalElement.YELLOW, 40000);
		rit.addAura(CrystalElement.PINK, 10000);
		rit.addAura(CrystalElement.ORANGE, 2000);
		this.addRitual(rit);

		if (ModList.BLOODMAGIC.isLoaded()) {
			rit = new AbilityRitual(Chromabilities.LIFEPOINT);
			rit.addAura(CrystalElement.MAGENTA, 25000);
			rit.addAura(CrystalElement.BLACK, 5000);
			rit.addAura(CrystalElement.RED, 5000);
			this.addRitual(rit);
		}

		rit = new AbilityRitual(Chromabilities.DEATHPROOF);
		rit.addAura(CrystalElement.BLACK, 10000);
		rit.addAura(CrystalElement.WHITE, 10000);
		rit.addAura(CrystalElement.PINK, 5000);
		rit.addAura(CrystalElement.PURPLE, 15000);
		rit.addAura(CrystalElement.RED, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.HOTBAR);
		rit.addAura(CrystalElement.LIME, 20000);
		rit.addAura(CrystalElement.GRAY, 5000);
		rit.addAura(CrystalElement.PURPLE, 25000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.SHOCKWAVE);
		rit.addAura(CrystalElement.RED, 20000);
		rit.addAura(CrystalElement.PINK, 25000);
		rit.addAura(CrystalElement.YELLOW, 10000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.LEECH);
		rit.addAura(CrystalElement.PINK, 10000);
		rit.addAura(CrystalElement.MAGENTA, 15000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.TELEPORT);
		rit.addAura(CrystalElement.LIME, 100000);
		rit.addAura(CrystalElement.BLACK, 75000);
		rit.addAura(CrystalElement.PURPLE, 25000);
		rit.addAura(CrystalElement.GRAY, 40000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.FLOAT);
		rit.addAura(CrystalElement.LIME, 5000);
		rit.addAura(CrystalElement.BLACK, 2000);
		rit.addAura(CrystalElement.CYAN, 25000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.SPAWNERSEE);
		rit.addAura(CrystalElement.BLUE, 100000);
		rit.addAura(CrystalElement.BLACK, 20000);
		rit.addAura(CrystalElement.PINK, 25000);
		rit.addAura(CrystalElement.LIGHTGRAY, 25000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.BREADCRUMB);
		rit.addAura(CrystalElement.BLUE, 40000);
		rit.addAura(CrystalElement.LIGHTBLUE, 10000);
		rit.addAura(CrystalElement.LIME, 25000);
		rit.addAura(CrystalElement.LIGHTGRAY, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.DIMPING);
		rit.addAura(CrystalElement.BLUE, 40000);
		rit.addAura(CrystalElement.BLACK, 100000);
		rit.addAura(CrystalElement.LIME, 75000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.RANGEDBOOST);
		rit.addAura(CrystalElement.PINK, 25000);
		rit.addAura(CrystalElement.PURPLE, 20000);
		rit.addAura(CrystalElement.BLACK, 10000);
		rit.addAura(CrystalElement.LIME, 15000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.DASH);
		rit.addAura(CrystalElement.LIGHTBLUE, 5000);
		rit.addAura(CrystalElement.BLACK, 2000);
		rit.addAura(CrystalElement.LIME, 10000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.LASER);
		rit.addAura(CrystalElement.PINK, 360000);
		rit.addAura(CrystalElement.BLUE, 270000);
		rit.addAura(CrystalElement.BLACK, 90000);
		rit.addAura(CrystalElement.YELLOW, 720000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.FIRERAIN);
		rit.addAura(CrystalElement.PINK, 270000);
		rit.addAura(CrystalElement.BLACK, 120000);
		rit.addAura(CrystalElement.ORANGE, 480000);
		rit.addAura(CrystalElement.CYAN, 60000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.KEEPINV);
		rit.addAura(CrystalElement.LIGHTGRAY, 30000);
		rit.addAura(CrystalElement.BLACK, 20000);
		rit.addAura(CrystalElement.PINK, 60000);
		rit.addAura(CrystalElement.RED, 120000);
		rit.addAura(CrystalElement.WHITE, 40000);
		rit.addAura(CrystalElement.MAGENTA, 80000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.ORECLIP);
		rit.addAura(CrystalElement.BLACK, 90000);
		rit.addAura(CrystalElement.BLUE, 90000);
		rit.addAura(CrystalElement.LIME, 600000);
		rit.addAura(CrystalElement.LIGHTGRAY, 90000);
		rit.addAura(CrystalElement.PURPLE, 270000);
		rit.addAura(CrystalElement.BROWN, 360000);
		rit.addAura(CrystalElement.CYAN, 90000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.DOUBLECRAFT);
		rit.addAura(CrystalElement.BLACK, 180000);
		rit.addAura(CrystalElement.GRAY, 450000);
		rit.addAura(CrystalElement.PURPLE, 900000);
		rit.addAura(CrystalElement.BROWN, 240000);
		rit.addAura(CrystalElement.LIGHTBLUE, 60000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.RECHARGE);
		rit.addAura(CrystalElement.BLACK, 90000);
		rit.addAura(CrystalElement.YELLOW, 120000);
		rit.addAura(CrystalElement.LIME, 120000);
		rit.addAura(CrystalElement.PURPLE, 90000);
		rit.addAura(CrystalElement.WHITE, 40000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.GROWAURA);
		rit.addAura(CrystalElement.BLACK, 120000);
		rit.addAura(CrystalElement.MAGENTA, 270000);
		rit.addAura(CrystalElement.GREEN, 180000);
		rit.addAura(CrystalElement.RED, 120000);
		rit.addAura(CrystalElement.GRAY, 60000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.MEINV);
		rit.addAura(CrystalElement.BLACK, 120000);
		rit.addAura(CrystalElement.LIGHTGRAY, 180000);
		rit.addAura(CrystalElement.LIME, 180000);
		rit.addAura(CrystalElement.BROWN, 120000);
		rit.addAura(CrystalElement.WHITE, 240000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.MOBSEEK);
		rit.addAura(CrystalElement.BLACK, 90000);
		rit.addAura(CrystalElement.BLUE, 60000);
		rit.addAura(CrystalElement.LIME, 400000);
		rit.addAura(CrystalElement.LIGHTGRAY, 60000);
		rit.addAura(CrystalElement.PURPLE, 120000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.BEEALYZE);
		rit.addAura(CrystalElement.GREEN, 60000);
		rit.addAura(CrystalElement.BLUE, 20000);
		rit.addAura(CrystalElement.MAGENTA, 60000);
		rit.addAura(CrystalElement.PURPLE, 10000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.LIGHTCAST);
		rit.addAura(CrystalElement.BLUE, 100000);
		rit.addAura(CrystalElement.BROWN, 20000);
		rit.addAura(CrystalElement.GRAY, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.NUKER);
		rit.addAura(CrystalElement.BROWN, 200000);
		rit.addAura(CrystalElement.LIGHTBLUE, 60000);
		rit.addAura(CrystalElement.LIME, 60000);
		rit.addAura(CrystalElement.GRAY, 60000);
		rit.addAura(CrystalElement.YELLOW, 120000);
		rit.addAura(CrystalElement.BLACK, 40000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.JUMP);
		rit.addAura(CrystalElement.LIME, 20000);
		rit.addAura(CrystalElement.LIGHTBLUE, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.SUPERBUILD);
		rit.addAura(CrystalElement.LIGHTBLUE, 24000);
		rit.addAura(CrystalElement.GRAY, 6000);
		rit.addAura(CrystalElement.PURPLE, 6000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.CHESTCLEAR);
		rit.addAura(CrystalElement.LIME, 9000);
		rit.addAura(CrystalElement.LIGHTGRAY, 1000);
		rit.addAura(CrystalElement.WHITE, 3000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.MOBBAIT);
		rit.addAura(CrystalElement.LIGHTGRAY, 12000);
		rit.addAura(CrystalElement.RED, 9000);
		rit.addAura(CrystalElement.PINK, 3000);
		this.addRitual(rit);
	}

	private void addRitual(AbilityRitual ar) {
		data.put(ar.ability, ar);
		maxCost = Math.max(maxCost, ar.maxCost);
		maxTotalCost = Math.max(maxTotalCost, ar.totalCost);
	}

	public void addRitual(Ability a, HashMap<CrystalElementProxy, Integer> elements) {
		AbilityRitual rit = new AbilityRitual(a);
		for (CrystalElementProxy e : elements.keySet()) {
			rit.addAura(CrystalElement.getFromAPI(e), elements.get(e));
		}
		this.addRitual(rit);
	}

	public boolean hasRitual(Ability c) {
		return data.containsKey(c);
	}

	public ElementTagCompound getAura(Ability c) {
		return this.hasRitual(c) ? data.get(c).getRequiredAura() : new ElementTagCompound();
	}

	public int getDuration(Ability c) {
		return this.hasRitual(c) ? data.get(c).duration : 0;
	}

	public static boolean isPlayerUndergoingRitual(EntityPlayer ep) {
		for (WorldLocation loc : tables) {
			TileEntity te = loc.getTileEntity(ep.worldObj);
			if (te instanceof TileEntityRitualTable) {
				TileEntityRitualTable rit = (TileEntityRitualTable)te;
				if (rit.isActive() && rit.isPlayerUsing(ep))
					return true;
			}
		}
		return false;
	}

	public static void addTable(TileEntityRitualTable te) {
		WorldLocation loc = new WorldLocation(te);
		if (!tables.contains(loc))
			tables.add(loc);
	}

	public static void removeTable(TileEntityRitualTable te) {
		tables.remove(new WorldLocation(te));
	}

	public int getMaxAbilityCost() {
		return maxCost;
	}

	public int getMaxAbilityTotalCost() {
		return maxTotalCost;
	}
	/*
	public void reload() {
		maxCost = 0;
		maxTotalCost = 0;
		data.clear();
		this.addRituals();
	}
	 */
	private static class AbilityRitual {

		private final ElementTagCompound energy = new ElementTagCompound();
		public final int duration;
		public final Ability ability;
		private int maxCost;
		private int totalCost;

		private AbilityRitual(Ability c) {
			ability = c;
			duration = 980;
		}

		private void addAura(CrystalElement e, int amt) {
			energy.addTag(e, amt);

			maxCost = Math.max(maxCost, amt);
			totalCost += amt;
		}

		public ElementTagCompound getRequiredAura() {
			return energy.copy();
		}

	}
}
