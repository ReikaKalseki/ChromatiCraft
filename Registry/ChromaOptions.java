/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.DecimalConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;
import Reika.DragonAPI.Interfaces.Configuration.MatchingConfig;
import Reika.DragonAPI.Interfaces.Configuration.UserSpecificConfig;
import Reika.DragonAPI.Interfaces.Registry.Dependency;


public enum ChromaOptions implements IntegerConfig, BooleanConfig, DecimalConfig, MatchingConfig, UserSpecificConfig, Dependency {

	NOISE("Lamp Noises", true),
	NETHER("Nether Crystals", true),
	NOPARTICLES("Disable Pendant Particles", true),
	CRYSTALFARM("Crystal Plants May Drop Shards", true),
	TILELAG("Max Accelerator Lag in NanoSeconds", 1000000),
	GUARDIAN("Guardian Stone Range", 16),
	GUARDCHUNK("Guardian Stone is Full Chunk Height", false),
	BLOCKPARTICLES("Dye Block Particles", true),
	ETHEREAL("Generate Anti-Taint plants in Rainbow Forest", true),
	DYEFRAC("Vanilla Dye Drop Percentage", 100),
	ANIMALSPAWN("Rainbow Forest Animal Density", 6),
	RAINBOWSPREAD("Rainbow Trees Spread Rainbow Forests", true),
	ENDERCOLORING("Ender Forest Coloring", false),
	ENDERPOOLS("Ender Pool Density from 1 to 3", 2),
	ENDEREFFECT("Liquid Ender Effect", true),
	GOLDAPPLES("Rainbow Leaf Gold Apple Drop Percentage", 0.25F),
	BROKENPYLON("Generate Some Pylons as Broken", false),
	DYNAMICHANDBOOK("Reload Handbook Data on Open", false),
	FLATGEN("Run Worldgen in Superflat Worlds", false),
	NONWORLDGEN("Generate Pylons in Non-Overworld", false),
	HANDBOOK("Spawn with CC Lexicon", true),
	CHESTGEN("Chest Generation Tier", 4),
	ENDERTNT("Enable Ender TNT", true),
	KEYBINDABILITY("Use vanilla keybind system for ability selection GUI", false),
	COPYTILE("Allow duplication wand to copy TileEntities", false),
	HARDTHAUM("Make ThaumCraft integration require ThaumCraft progression", false),
	PIELOC("Energy Buffer Overlay Location", 0),
	RETROGEN("Retrogeneration", false),
	BIOMEPAINTER("Enable Biome Painter", true),
	RAINBOWWEIGHT("Rainbow Forest Biome Weight", 10),
	ENDERWEIGHT("Ender Forest Biome Weight", 10),
	CLIFFWEIGHT("Luminous Cliffs Biome Weight", 4),
	HOSTILEFOREST("Allow Danger in Rainbow Forests", false),
	RELAYRANGE("Lumen Relay Range", 16),
	REDRAGON("Always Respawn EnderDragon", false),
	DELEND("Delete End on Unload", false),
	EASYFRAG("Auxiliary Fragment Acquisition", false),
	COPYSIZE("Duplication Wand Max Volume", 1000),
	SHIFTTILES("World Shift Can Move TileEntities", false),
	//SMALLAURA("Use reduced-size Pylon Aura image; only enable this if you get a full-screen color washout", false),
	STRUCTDIFFICULTY("Dimension Structure Difficulty", 3),
	BALLLIGHTNING("Enable Ball Lightning", true),
	PYLONLOAD("Pylons Chunkload Selves Once Used", true),
	RIFTLOAD("World Rifts Chunkload", false),
	SHORTPATH("Make Pylon pathfinding attempt shortest path - can be intensive", true),
	PROGRESSNOTIFY("Notify players of progress gains", true),
	PROGRESSNOTIFY_SELF("Notify players via chat of their own progress gains", false),
	CAVELIGHTERRANGE("Cave Lighter Range", 128),
	CAVELIGHTERSIZE("Cave Lighter Zone Size", 8),
	//BIOMEFX("Biome FX", true),
	CONNECTEDCRYSTALSTONE("Connected Crystalline Stone Textures", false),
	POWEREDACCEL("Adjacency Upgrades Require Energy", false),
	PROGRESSDURATION("Progression Notification Duration", 800),
	RECEIVEDIMSOUND("Play Dimension Join Sound For Others", true),
	BIOMEBLEND("Blend CC Biome Edges", true),
	MIDISIZE("Orchestra MIDI Size Limit (KB)", 80),
	ALLOWSTRUCTPASS("Allow Structure Bypass Passwords", true);

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

	public static float getRainbowLeafGoldAppleDropChance() {
		float base = GOLDAPPLES.getFloat()/100F;
		return Math.min(1, Math.max(0.0001F, base));
	}

	public static int getRainbowForestWeight() {
		int base = RAINBOWWEIGHT.getValue();
		return Math.max(2, base);
	}

	public static int getEnderForestWeight() {
		int base = ENDERWEIGHT.getValue();
		return Math.max(2, base);
	}

	public static int getGlowingCliffsWeight() {
		int base = CLIFFWEIGHT.getValue();
		return Math.max(1, Math.min(getRainbowForestWeight()/2, base));
	}

	public static int getStructureDifficulty() {
		int base = STRUCTDIFFICULTY.getValue();
		return Math.min(3, Math.max(1, base));
	}

	@Override
	public boolean enforceMatch() {
		switch(this) {
			case GUARDIAN:
			case RAINBOWSPREAD:
			case EASYFRAG:
			case BIOMEPAINTER:
			case ENDERTNT:
			case HARDTHAUM:
			case MIDISIZE:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean isLoaded() {
		return this.getState();
	}

	@Override
	public String getDisplayName() {
		return label;
	}

	@Override
	public boolean isUserSpecific() {
		switch(this) {
			case NOISE:
			case NOPARTICLES:
			case BLOCKPARTICLES:
			case ENDERCOLORING:
			case DYNAMICHANDBOOK:
			case KEYBINDABILITY:
			case PIELOC:
				//case BIOMEFX:
			case CONNECTEDCRYSTALSTONE:
			case RECEIVEDIMSOUND:
			case PROGRESSDURATION:
			case PROGRESSNOTIFY_SELF:
				return true;
			default:
				return false;
		}
	}

}
