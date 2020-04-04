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

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Heights;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Maturation;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Saplings;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Sappiness;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Yield;
import Reika.DragonAPI.ModInteract.Bees.TreeTraits;

public enum DyeTreeTypes {

	BLACK(Yield.LOW, Yield.HIGH, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT),
	RED(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT),
	GREEN(Yield.LOWER, Yield.LOW, Saplings.LOW, Saplings.HIGHER, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.FAST, Territory.DEFAULT),
	BROWN(Yield.LOWER, Yield.HIGHER, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.LOWER, Maturation.SLOW, Maturation.SLOW, Territory.DEFAULT),
	BLUE(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.FASTER, Territory.DEFAULT),
	PURPLE(Yield.LOWER, Yield.HIGHEST, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT),
	CYAN(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.HIGHEST, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT),
	LIGHTGRAY(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.LOWER, Maturation.SLOWER, Maturation.SLOW, Territory.DEFAULT),
	GRAY(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.HIGH, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT),
	PINK(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT),
	LIME(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.AVERAGE, Territory.LARGE),
	YELLOW(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.HIGHER, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT),
	LIGHTBLUE(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.FASTEST, Territory.DEFAULT),
	MAGENTA(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.HIGHEST, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT),
	ORANGE(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.LOW, Sappiness.LOWEST, Sappiness.HIGH, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT),
	WHITE(Yield.LOWER, Yield.LOW, Saplings.LOWER, Saplings.HIGH, Sappiness.LOWEST, Sappiness.LOWER, Maturation.AVERAGE, Maturation.AVERAGE, Territory.DEFAULT);

	public static final DyeTreeTypes[] list = values();

	protected final TreeTraits traits = new TreeTraits();
	protected final TreeTraits traitsHybrid = new TreeTraits();

	private DyeTreeTypes(Yield y, Yield y2, Saplings f, Saplings f2, Sappiness s, Sappiness s2, Maturation m, Maturation m2, Territory a) {
		traits.sappiness = s;
		traits.yield = y;
		traits.fertility = f;
		traits.maturation = m;

		traitsHybrid.sappiness = s2;
		traitsHybrid.yield = y2;
		traitsHybrid.fertility = f2;
		traitsHybrid.maturation = m2;

		traits.height = Heights.AVERAGE;
		traits.area = a;
		traitsHybrid.height = Heights.AVERAGE;
		traitsHybrid.area = a;

		traitsHybrid.isFireproof = this.ordinal() == CrystalElement.ORANGE.ordinal();
	}

	public TreeTraits getTraits() {
		return traits;
	}
}
