/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Tolerance;
import Reika.DragonAPI.ModInteract.Bees.BeeTraits;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public enum CrystalBeeTypes {
	BLACK(Speeds.SLOW,					Life.NORMAL,			Fertility.LOW,				Flowering.SLOWER,			Territory.DEFAULT,			Tolerance.NONE,	Tolerance.NONE,	0,	0,	EnumTemperature.NORMAL,	EnumHumidity.NORMAL),
	RED(Speeds.NORMAL,					CrystalBees.superLife,	Fertility.NORMAL,			Flowering.AVERAGE,			Territory.DEFAULT,			Tolerance.DOWN,	Tolerance.DOWN,	1,	1,	EnumTemperature.HOT,	EnumHumidity.ARID),
	GREEN(Speeds.SLOWEST,				Life.NORMAL,			Fertility.LOW,				CrystalBees.superFlowering,	Territory.DEFAULT,			Tolerance.UP,	Tolerance.NONE,	1,	0,	EnumTemperature.WARM,	EnumHumidity.DAMP),
	BROWN(Speeds.SLOWER,				CrystalBees.blinkLife,	Fertility.NORMAL,			Flowering.SLOW,				Territory.DEFAULT,			Tolerance.NONE,	Tolerance.DOWN,	0,	1,	EnumTemperature.COLD,	EnumHumidity.NORMAL),
	BLUE(Speeds.FAST,					Life.LONG,				Fertility.NORMAL,			Flowering.SLOWEST,			Territory.LARGER,			Tolerance.UP,	Tolerance.NONE,	2,	0,	EnumTemperature.NORMAL,	EnumHumidity.NORMAL),
	PURPLE(Speeds.NORMAL,				Life.NORMAL,			Fertility.LOW,				Flowering.SLOWER,			Territory.DEFAULT,			Tolerance.NONE,	Tolerance.NONE,	0,	0,	EnumTemperature.NORMAL,	EnumHumidity.DAMP),
	CYAN(Speeds.SLOW,					Life.SHORT,				Fertility.NORMAL,			Flowering.FASTER,			Territory.LARGE,			Tolerance.BOTH,	Tolerance.NONE,	1,	0,	EnumTemperature.NORMAL,	EnumHumidity.DAMP),
	LIGHTGRAY(Speeds.SLOWEST,			Life.SHORTER,			Fertility.LOW,				Flowering.SLOWEST,			Territory.LARGER,			Tolerance.BOTH,	Tolerance.NONE,	1,	0,	EnumTemperature.NORMAL,	EnumHumidity.NORMAL),
	GRAY(Speeds.SLOWEST,				Life.LONG,				Fertility.LOW,				Flowering.SLOWEST,			Territory.DEFAULT,			Tolerance.NONE,	Tolerance.BOTH,	0,	5,	EnumTemperature.NORMAL,	EnumHumidity.NORMAL),
	PINK(Speeds.NORMAL,					Life.ELONGATED,			Fertility.NORMAL,			Flowering.FAST,				Territory.DEFAULT,			Tolerance.NONE,	Tolerance.NONE,	0,	0,	EnumTemperature.WARM,	EnumHumidity.NORMAL),
	LIME(Speeds.SLOW,					Life.SHORTER,			Fertility.NORMAL,			Flowering.AVERAGE,			CrystalBees.superTerritory,	Tolerance.DOWN,	Tolerance.NONE,	1,	0,	EnumTemperature.NORMAL,	EnumHumidity.NORMAL),
	YELLOW(Speeds.FAST,					Life.ELONGATED,			Fertility.HIGH,				Flowering.SLOW,				Territory.DEFAULT,			Tolerance.DOWN,	Tolerance.NONE,	1,	0,	EnumTemperature.HELLISH,EnumHumidity.ARID),
	LIGHTBLUE(CrystalBees.superSpeed,	Life.SHORTER,			Fertility.LOW,				Flowering.SLOWEST,			Territory.DEFAULT,			Tolerance.NONE,	Tolerance.UP,	0,	0,	EnumTemperature.NORMAL,	EnumHumidity.NORMAL),
	MAGENTA(Speeds.NORMAL,				Life.SHORT,				CrystalBees.superFertility,	Flowering.FASTER,			Territory.DEFAULT,			Tolerance.NONE,	Tolerance.BOTH,	0,	1,	EnumTemperature.NORMAL,	EnumHumidity.NORMAL),
	ORANGE(Speeds.FASTER,				Life.SHORTENED,			Fertility.NORMAL,			Flowering.SLOW,				Territory.DEFAULT,			Tolerance.BOTH,	Tolerance.NONE,	5,	0,	EnumTemperature.HOT,	EnumHumidity.ARID),
	WHITE(Speeds.SLOWER,				Life.LONGER,			Fertility.LOW,				Flowering.FAST,				Territory.DEFAULT,			Tolerance.NONE,	Tolerance.NONE,	0,	0,	EnumTemperature.ICY,	EnumHumidity.NORMAL);

	public static final CrystalBeeTypes[] list = values();

	protected final BeeTraits traits = new BeeTraits();

	private CrystalBeeTypes(Speeds s, Life l, Fertility f, Flowering f2, Territory a, Tolerance d1, Tolerance d2, int tt, int ht, EnumTemperature t, EnumHumidity h) {
		traits.speed = s;
		traits.lifespan = l;
		traits.fertility = f;
		traits.flowering = f2;
		traits.area = a;
		traits.tempDir = d1;
		traits.humidDir = d2;
		traits.tempTol = tt;
		traits.humidTol = ht;
		traits.temperature = t;
		traits.humidity = h;

		traits.isTolerant = this.ordinal() == CrystalElement.CYAN.ordinal();
		traits.isNocturnal = this.ordinal() == CrystalElement.BLUE.ordinal();
		traits.isCaveDwelling = this.ordinal() == CrystalElement.LIGHTGRAY.ordinal();
	}

	public BeeTraits getTraits() {
		return traits;
	}
}
