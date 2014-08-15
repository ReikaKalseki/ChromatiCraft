/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Interfaces.ConfigList;


public enum ChromaOptions implements ConfigList {

	NOISE("Lamp Noises", true),
	EFFECTS("Lamp Effects", false),
	NETHER("Nether Crystals", true),
	NOPARTICLES("Disable Pendant Particles", true),
	CRYSTALFARM("Crystal Plants May Drop Shards", true),
	TILELAG("Max Accelerator Lag in NanoSeconds", 1000000),
	GUARDIAN("Guardian Stone Range", 16),
	NORMAL("Generate Dye Trees in Normal Biomes", true),
	RAINBOWFORESTID("Rainbow Forest Biome ID", 48),
	BLOCKPARTICLES("Dye Block Particles", true),
	ETHEREAL("Generate Anti-Taint plants in Rainbow Forest", true),
	DENSITY("Dye Tree Density in Normal Biomes", 2),
	GENRAINBOW("Generate Rainbow Trees", true),
	DYEFRAC("Vanilla Dye Drop Percentage", 100),
	ANIMALSPAWN("Rainbow Forest Animal Density", 6),
	RAINBOWSPREAD("Rainbow Trees Spead Rainbow Forests", true),
	ENDERCOLORING("Ender Forest Coloring", false),
	ENDERPOOLS("Ender Pool Density from 1 to 3", 2),
	ENDERFORESTID("Ender Forest ID", 47),
	ENDEREFFECT("Liquid Ender Effect", true);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
	private Class type;
	private boolean enforcing = false;

	public static final ChromaOptions[] optionList = values();

	private ChromaOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	private ChromaOptions(String l, boolean d, boolean tag) {
		this(l, d);
		enforcing = true;
	}

	private ChromaOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	private ChromaOptions(String l, float d) {
		label = l;
		defaultFloat = d;
		type = float.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public boolean isDecimal() {
		return type == float.class;
	}

	public Class getPropertyType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public boolean getState() {
		return (Boolean)ChromatiCraft.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)ChromatiCraft.config.getControl(this.ordinal());
	}

	public float getFloat() {
		return (Float)ChromatiCraft.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
	}

	@Override
	public int getDefaultValue() {
		return defaultValue;
	}

	@Override
	public float getDefaultFloat() {
		return defaultFloat;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return enforcing;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

	public static boolean doesVanillaDyeDrop() {
		return DYEFRAC.getValue() > 0;
	}

	public static boolean doesTreeDyeDrop() {
		return DYEFRAC.getValue() < 100;
	}

	public static boolean isVanillaDyeMoreCommon() {
		return DYEFRAC.getValue() > 50;
	}

}