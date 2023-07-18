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

import java.util.Locale;

import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.CustomCategoryConfig;
import Reika.DragonAPI.Interfaces.Configuration.DecimalConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;
import Reika.DragonAPI.Interfaces.Configuration.MatchingConfig;
import Reika.DragonAPI.Interfaces.Configuration.SegmentedConfigList;
import Reika.DragonAPI.Interfaces.Configuration.SelectiveConfig;
import Reika.DragonAPI.Interfaces.Configuration.StringConfig;
import Reika.DragonAPI.Interfaces.Configuration.UserSpecificConfig;
import Reika.DragonAPI.Interfaces.Registry.Dependency;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public enum ChromaOptions implements SegmentedConfigList, SelectiveConfig, IntegerConfig, BooleanConfig, DecimalConfig, StringConfig, MatchingConfig, CustomCategoryConfig, UserSpecificConfig, Dependency {

	NOISE("Lamp Noises", true),
	//NETHER("Nether Crystals", true),
	NOPARTICLES("Disable Pendant Particles", true), //Whether to disable potion spirals from pendant-given effects
	CRYSTALFARM("Crystal Plants May Drop Shards", true),
	TILELAG("Max Accelerator Lag in NanoSeconds", 1000000), //How many ns can be spent before an accelerator will stop ticking a TileEntity that tick (set negative to disable throttling)
	GUARDIAN("Guardian Stone Range", 16),
	GUARDCHUNK("Guardian Stone is Full Chunk Height", false),
	BLOCKPARTICLES("Dye Block Particles", true),
	ETHEREAL("Generate Anti-Taint plants in Rainbow Forest", true),
	//DYEFRAC("Vanilla Dye Drop Percentage", 100),
	ANIMALSPAWN("Rainbow Forest Animal Density", 6), //Note that when reducing this, animals in rainbow forests drop more, to keep total yields the same
	RAINBOWSPREAD("Rainbow Trees Spread Rainbow Forests", true),
	ENDERCOLORING("Ender Forest Coloring", false), //Whether to use the purplish coloring of foliage for Ender Forests.
	ENDERPOOLS("Ender Pool Density from 1 to 3", 2), //Higher is more pools
	ENDEREFFECT("Liquid Ender Effect", true), //Whether CC liquid ender should propel entities around (meaningless if TE is installed as Resonant Ender is used instead)
	GOLDAPPLES("Rainbow Leaf Gold Apple Drop Percentage", 0.25F),
	BROKENPYLON("Generate Some Pylons as Broken", false),
	DYNAMICHANDBOOK("Reload Handbook Data on Open", false),
	FLATGEN("Run Worldgen in Superflat Worlds", false),
	NONWORLDGEN("Generate Pylons in Non-Overworld", false),
	HANDBOOK("Spawn with CC Lexicon", true),
	CHESTGEN("Chest Generation Tier", 4), //How advanced/expensive items generating in worldgen chests can be. Higher is more
	ENDERTNT("Enable Ender TNT", true),
	KEYBINDABILITY("Use vanilla keybind system for ability selection GUI", false), //Instead of forced middle mouse button
	COPYTILE("Allow duplication wand to copy TileEntities", false),
	HARDTHAUM("More difficult ThaumCraft integration", true), //Whether CC/TC integration requires more progress in both mods
	PIELOC("Energy Buffer Overlay Location", 0), //Where to place the lumen buffer HUD element on screen, 0-3 for the corners, 0 being top-left, 1 bottom-left, 2 top-right, 3 bottom-right
	//RETROGEN("Retrogeneration", false),
	//BIOMEPAINTER("Enable Biome Painter", true),
	RAINBOWWEIGHT("Rainbow Forest Biome Weight", 10),
	ENDERWEIGHT("Ender Forest Biome Weight", 10),
	CLIFFWEIGHT("Luminous Cliffs Biome Weight", 4),
	HOSTILEFOREST("Allow Danger in Rainbow Forests", false), //Whether ball lightnings can do damage, becoming the only potentially-dangerous (if not aggressive) mobs in the biome
	RELAYRANGE("Lumen Relay Range", 16),
	REDRAGON("Always Respawn EnderDragon", false), //Respawn the EnderDragon when it is killed and the end is reloaded
	DELEND("Delete End on Unload", false),
	EASYFRAG("Auxiliary Fragment Acquisition", false), //Whether certain progress flags will also give an associated fragment, instead of you needing to unlock it like the others
	COPYSIZE("Duplication Wand Max Volume", 1000),
	SHIFTTILES("World Shift Can Move TileEntities", false),
	//SMALLAURA("Use reduced-size Pylon Aura image; only enable this if you get a full-screen color washout", false),
	STRUCTDIFFICULTY("Dimension Structure Difficulty", 3), //1-3, with 1 being easy and 3 hard
	BALLLIGHTNING("Enable Ball Lightning", true),
	PYLONLOAD("Pylons Chunkload Selves Once Used", true),
	RIFTLOAD("World Rifts Chunkload", false),
	SHORTPATH("Make Pylon pathfinding attempt shortest path - can be intensive", true),
	PROGRESSNOTIFY("Notify players of progress gains", true),
	PROGRESSNOTIFY_SELF("Notify players via chat of their own progress gains", false),
	CAVELIGHTERRANGE("Cave Lighter Range", 128),
	CAVELIGHTERSIZE("Cave Lighter Zone Size", 8), //The subterranean illuminator works by dividing the world into NxNxN blocks and attempting to place a light in each one. Smaller zones take longer to process the volume but have more thorough lighting.
	//BIOMEFX("Biome FX", true),
	//CONNECTEDCRYSTALSTONE("Connected Crystalline Stone Textures", false),
	POWEREDACCEL("Adjacency Upgrades Require Energy", true),
	PROGRESSDURATION("Progression Notification Duration", 800),
	RECEIVEDIMSOUND("Play Dimension Join Sound For Others", true),
	BIOMEBLEND("Blend CC Biome Edges", true), //Whether to attempt to blend the edges of the Luminous Cliffs to the surroundings. Expensive and imperfect
	MIDISIZE("Orchestra MIDI Size Limit (KB)", 80), //How large a MIDI can be loaded into the crystal orchestra, as superlarge MIDIs will overflow packet limits
	//STRUCTPASSLEVEL("Structure Bypass Usability (0 = None, 1 = Admins Only, 2 = All)", 2), //Who can use the bypass dimension structures
	SUPERBUILDKEYBIND("Superbuild Ability Activation", Key.LCTRL.name()), //TypeHelper to Website Generator: String; What key to hold to build in superbuild
	VILLAGERATE("Village Structure Frequency", 1F),
	NODECHARGESPEED("Crystal-Network-Integrated ThaumCraft Node Improvement Speed", 1F),
	METEORFIRE("Meteor Tower Projectiles Start Fires On Impact", true),
	PANELLAMPCOLLISION("Panel-Form Lumen Lamps Have No Hitbox", false),
	EPILEPSY("Epilepsy Mode", false), //Whether to reduce the dynamism of some visuals, such as by making them only show when holding a manipulator
	//PYLONOVERWORLD("Spawn Pylons in Overworld", true),
	RFEFFICIENCY("Wireless RF Transmission Efficiency (%)", 100F),
	LAUNCHPOWER("Launch Pad Power", 1F),
	FENCEHOPS("Crystal Fence segment count limit", 64),
	STRUCTTRIES("Structure Generation Attempts Per Flagged Chunk - affects worldgen CPU use and structure rarity", 20), //How forcefully should CC attempt to generate structures in the planned locations. More attempts means more computation time, but fewer means a lower success rate and thus lower total structure frequency
	PROGSHADER("Use Alternate Progress Shader", false), //Whether to replace the pixelation progress shader with a colorful overlay
	POWEREDPENDANTS("Enhanced Pendants Require Charging", false), //Whether enhanced pendants are "charged tools" like the aura tracker is
	ABILITYSHOWONLY("Ability Selection GUI only shows abilities you have", false),
	//RECURSIVEPOUCH("Allow Recursive Aura Pouches", true),
	LUMCLIFFSEEDSHIFT("Luminous Cliffs Terrain Shape Seed Shift", 0), //A modifier for the seed used to generate luminous cliffs terrain. Allows you to try a new terrain layout for a biome while keeping the overall world seed
	;

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
	private String defaultString;
	private Class type;
	private boolean enforcing = false;

	private static boolean t2ConfigModel = false;

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

	private ChromaOptions(String l, String d) {
		label = l;
		defaultString = d;
		type = String.class;
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

	public boolean isString() {
		return type == String.class;
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

	@Override
	public String getString() {
		return (String)ChromatiCraft.config.getControl(this.ordinal());
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
	public String getDefaultString() {
		return defaultString;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return enforcing;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

	public static int getVillageStructureRarity(int defaultValue, int minValue) {
		float f = Math.min(2.5F, VILLAGERATE.getFloat());
		return Math.max(minValue, (int)(defaultValue/f));
	}

	public static boolean doesVanillaDyeDrop(CrystalElement e) {
		return ChromatiCraft.config.getVanillaDyeChance(e) > 0;
	}

	public static boolean doesTreeDyeDrop(CrystalElement e) {
		return ChromatiCraft.config.getVanillaDyeChance(e) < 100;
	}

	public static boolean isVanillaDyeMoreCommon(int i) {
		return ChromatiCraft.config.getVanillaDyeChance(i) > 50;
	}

	public static boolean isVanillaDyeMoreCommon(CrystalElement e) {
		return ChromatiCraft.config.getVanillaDyeChance(e) > 50;
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
	/*
	public static boolean structureBypassEnabled() {
		return STRUCTPASSLEVEL.getValue() > 0;
	}

	public static boolean canPlayerUseStructureBypass(EntityPlayer ep) {
		if (ReikaPlayerAPI.isReika(ep))
			return true;
		int l = STRUCTPASSLEVEL.getValue();
		switch(l) {
			case 0:
			default:
				return false;
			case 1:
				return ep instanceof EntityPlayerMP && ReikaPlayerAPI.isAdmin((EntityPlayerMP)ep);
			case 2:
				return true;
		}
	}
	 */
	public static float getNodeGrowthSpeed() {
		float base = NODECHARGESPEED.getFloat();
		return Math.min(6F, Math.max(0.2F, base));
	}

	public static float getRFEfficiency() {
		float base = RFEFFICIENCY.getFloat()/100F;
		return Math.min(1F, Math.max(0.5F, base));
	}

	@Override
	public boolean enforceMatch() {
		switch(this) {
			case GUARDIAN:
			case RAINBOWSPREAD:
			case EASYFRAG:
				//case BIOMEPAINTER:
			case ENDERTNT:
			case HARDTHAUM:
			case MIDISIZE:
			case PANELLAMPCOLLISION:
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
				//case CONNECTEDCRYSTALSTONE:
			case RECEIVEDIMSOUND:
			case PROGRESSDURATION:
			case PROGRESSNOTIFY_SELF:
			case EPILEPSY:
			case PROGSHADER:
			case ABILITYSHOWONLY:
				return true;
			default:
				return false;
		}
	}

	@Override
	public String getCategory() {
		if (this.isString() && this.name().toLowerCase(Locale.ENGLISH).contains("keybind"))
			return "Keybinds";
		return null;
	}

	@Override
	public boolean saveIfUnspecified() {
		switch(this) {
			//case PYLONOVERWORLD:
			//	return false;
			default:
				return true;
		}
	}

	@Override
	public String getCustomConfigFile() {
		switch(this) {
			//case PYLONOVERWORLD:
			//	return "*_ExtraOptions";
			default:
				return null;
		}
	}

	@Override
	public boolean isAccessible() {
		switch(this) {
			//case PYLONOVERWORLD:
			//	return t2ConfigModel;
			default:
				return true;
		}
	}

	public static int getMaxFenceSections() {
		return ReikaMathLibrary.ceilPseudo2Exp(MathHelper.clamp_int(FENCEHOPS.getValue(), 16, 256));
	}

	public static int getStructureTriesPerChunk() {
		return MathHelper.clamp_int(STRUCTTRIES.getValue(), 5, 100);
	}

}
