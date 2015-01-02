/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public class AbilityRituals {

	private final HashMap<Ability, AbilityRitual> data = new HashMap();

	public static final AbilityRituals instance = new AbilityRituals();

	private static Collection<WorldLocation> tables = new ArrayList();

	private AbilityRituals() {

		AbilityRitual rit = new AbilityRitual(Chromabilities.REACH);
		rit.addAura(CrystalElement.LIME, 20000);
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
		rit.addAura(CrystalElement.MAGENTA, 25000);
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
	}

	private void addRitual(AbilityRitual ar) {
		data.put(ar.ability, ar);
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
			TileEntity te = ep.worldObj.getTileEntity(loc.xCoord, loc.yCoord, loc.zCoord);
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

	private static class AbilityRitual {

		private final ElementTagCompound energy = new ElementTagCompound();
		public final int duration;
		public final Ability ability;

		private AbilityRitual(Ability c) {
			ability = c;
			duration = 950;
		}

		private void addAura(CrystalElement e, int amt) {
			energy.addTag(e, amt);
		}

		public ElementTagCompound getRequiredAura() {
			return energy.copy();
		}

	}
}
