package Reika.ChromatiCraft.Magic.Progression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FocusAcceleratable;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class FragmentCategorizationSystem {

	public static final FragmentCategorizationSystem instance = new FragmentCategorizationSystem();

	private final EnumMap<ChromaResearch, CountMap<FragmentCategory>> mappings = new EnumMap(ChromaResearch.class);
	private final EnumMap<ChromaResearch, FragmentCategorization> categories = new EnumMap(ChromaResearch.class);
	private final EnumMap<FragmentCategory, FragmentTable> tables = new EnumMap(FragmentCategory.class);

	private FragmentCategorizationSystem() {
		this.addMapping(ChromaTiles.COLLECTOR, FragmentCategory.RESOURCE, 40);
		this.addMapping(ChromaTiles.COLLECTOR, FragmentCategory.CONVERSION, 40);
		this.addMapping(ChromaTiles.FABRICATOR, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.FABRICATOR, FragmentCategory.CONVERSION, 40);
		this.addMapping(ChromaTiles.MINER, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.MINER, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaTiles.TELEPUMP, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.TELEPUMP, FragmentCategory.WORLD, 40);

		this.addMapping(ChromaTiles.AVOLASER, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaTiles.LAMP, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaTiles.CLOAKING, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaTiles.BEACON, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaTiles.FENCE, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaTiles.EXPLOSIONSHIELD, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaTiles.GUARDIAN, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaTiles.TURRET, FragmentCategory.ATTACK, 30);
		this.addMapping(ChromaTiles.METEOR, FragmentCategory.ATTACK, 40);

		this.addMapping(ChromaTiles.AREABREAKER, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaTiles.MULTIBUILDER, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaTiles.LAMPCONTROL, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaTiles.DEATHFOG, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.AURAPOINT, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaTiles.AURAPOINT, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaTiles.HOVERPAD, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaTiles.ITEMCOLLECTOR, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.ITEMCOLLECTOR, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaTiles.INSERTER, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.LIGHTER, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaTiles.LASER, FragmentCategory.WORLD, 20);
		this.addMapping(ChromaTiles.PROGRESSLINK, FragmentCategory.CONVERSION, 20);
		this.addMapping(ChromaTiles.LUMENWIRE, FragmentCategory.AUTOMATION, 20);
		this.addMapping(ChromaTiles.CHROMACRAFTER, FragmentCategory.AUTOMATION, 20);
		this.addMapping(ChromaTiles.FUNCTIONRELAY, FragmentCategory.IMPROVEMENT, 20);

		this.addMapping(ChromaTiles.ADJACENCY, FragmentCategory.IMPROVEMENT, 40);

		this.addMapping(ChromaTiles.CRYSTAL, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaTiles.CHARGER, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaTiles.CHARGER, FragmentCategory.TOOLARMOR, 40);
		this.addMapping(ChromaTiles.CHARGER, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaTiles.FOCUSCRYSTAL, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.PYLONTURBO, FragmentCategory.LUMENS, 40);

		this.addMapping(ChromaTiles.PARTICLES, FragmentCategory.BUILDING, 30);

		this.addMapping(ChromaTiles.OPTIMIZER, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaTiles.OPTIMIZER, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaResearch.RELAY, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaTiles.WIRELESS, FragmentCategory.LUMENS, 40);

		this.addMapping(ChromaTiles.REVERTER, FragmentCategory.DEFENCE, 30);
		this.addMapping(ChromaTiles.REVERTER, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaTiles.REVERTER, FragmentCategory.CONVERSION, 40);
		this.addMapping(ChromaTiles.HEATLILY, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaTiles.COBBLEGEN, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.HARVESTPLANT, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.PLANTACCEL, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaTiles.CROPSPEED, FragmentCategory.IMPROVEMENT, 30);
		this.addMapping(ChromaTiles.CHROMAFLOWER, FragmentCategory.BUILDING, 40);

		this.addMapping(ChromaTiles.ENCHANTER, FragmentCategory.AUTOMATION, 30);
		this.addMapping(ChromaTiles.ENCHANTER, FragmentCategory.TOOLARMOR, 30);
		this.addMapping(ChromaTiles.FURNACE, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.GLOWFIRE, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.GLOWFIRE, FragmentCategory.CONVERSION, 40);
		//this.addMapping(ChromaTiles.REPROGRAMMER, FragmentCategory.AUTOMATION, 30);
		this.addMapping(ChromaTiles.REPROGRAMMER, FragmentCategory.CONVERSION, 40);
		this.addMapping(ChromaTiles.ENCHANTDECOMP, FragmentCategory.CONVERSION, 40);

		this.addMapping(ChromaTiles.AUTOMATOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.AUTOMATOR, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaTiles.AUTOMATOR, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.INJECTOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.INJECTOR, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaTiles.INJECTOR, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.INJECTORAUX, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaResearch.INJECTORAUX, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaResearch.INJECTORAUX, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.PLAYERINFUSER, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaTiles.PLAYERINFUSER, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaTiles.PLAYERINFUSER, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaTiles.RITUAL, FragmentCategory.ABILITY, 30);
		this.addMapping(ChromaTiles.STAND, FragmentCategory.CRAFTING, 20);
		this.addMapping(ChromaTiles.BREWER, FragmentCategory.CRAFTING, 40);

		this.addMapping(ChromaTiles.POWERTREE, FragmentCategory.LUMENS, 50);
		this.addMapping(ChromaTiles.POWERTREE, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaTiles.TOOLSTORAGE, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaTiles.TANK, FragmentCategory.STORAGE, 40);

		this.addMapping(ChromaTiles.FLUIDDISTRIBUTOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.FLUIDRELAY, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.ITEMRIFT, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.LAUNCHPAD, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaTiles.NETWORKITEM, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.RFDISTRIBUTOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.RIFT, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.RIFT, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaTiles.ROUTERHUB, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.TELEPORT, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaTiles.WINDOW, FragmentCategory.TRAVEL, 40);

		//this.addMapping(ChromaTiles.DATANODE, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaTiles.FARMER, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.PERSONAL, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaTiles.BIOMEPAINTER, FragmentCategory.WORLD, 40);

		this.addMapping(ChromaTiles.VOIDTRAP, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaTiles.ALVEARY, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.ALVEARY, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaTiles.ALVEARY, FragmentCategory.CONVERSION, 20);
		this.addMapping(ChromaTiles.LANDMARK, FragmentCategory.BUILDING, 30);
		this.addMapping(ChromaTiles.MANABOOSTER, FragmentCategory.AUTOMATION, 30);
		this.addMapping(ChromaTiles.MANABOOSTER, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaTiles.MANABOOSTER, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.MANABOOSTER, FragmentCategory.CONVERSION, 40);
		this.addMapping(ChromaTiles.BOOKDECOMP, FragmentCategory.COLLECTION, 30);
		this.addMapping(ChromaTiles.SMELTERYDISTRIBUTOR, FragmentCategory.AUTOMATION, 40);
		//this.addMapping(ChromaTiles.SMELTERYDISTRIBUTOR, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.ASPECT, FragmentCategory.CONVERSION, 40);
		this.addMapping(ChromaTiles.FLUXMAKER, FragmentCategory.CONVERSION, 30);
		this.addMapping(ChromaTiles.ESSENTIARELAY, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.MEDISTRIBUTOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.BEESTORAGE, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaTiles.ASPECTJAR, FragmentCategory.STORAGE, 40);

		this.addMapping(ChromaBlocks.GLOW, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.LAMPBLOCK, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.LAMP, FragmentCategory.BUILDING, 40);
		//this.addMapping(ChromaBlocks.RUNE, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaBlocks.CHROMA, FragmentCategory.RESOURCE, 40);
		this.addMapping(ChromaBlocks.HEATLAMP, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaBlocks.TNT, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaBlocks.TANK, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaBlocks.FENCE, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaBlocks.SUPER, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaBlocks.PATH, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.PATH, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaBlocks.PORTAL, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaBlocks.COLORALTAR, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.DOOR, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.GLASS, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.SELECTIVEGLASS, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.SELECTIVEGLASS, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaBlocks.AVOLAMP, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.REPEATERLAMP, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.REDSTONEPOD, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaBlocks.RFPOD, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaBlocks.FAKESKY, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.CHUNKLOADER, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaBlocks.CHUNKLOADER, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaBlocks.LUMA, FragmentCategory.RESOURCE, 40);
		this.addMapping(ChromaBlocks.LUMA, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaBlocks.ENDER, FragmentCategory.RESOURCE, 40);
		this.addMapping(ChromaBlocks.ENDER, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaBlocks.ENDER, FragmentCategory.TRAVEL, 20);
		this.addMapping(ChromaBlocks.TRAPFLOOR, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaBlocks.TRAPFLOOR, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaBlocks.WARPNODE, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaBlocks.WARPNODE, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaBlocks.ENCRUSTED, FragmentCategory.RESOURCE, 40);
		this.addMapping(ChromaBlocks.PAD, FragmentCategory.TRAVEL, 40);

		this.addMapping(ChromaItems.PROBE, FragmentCategory.STRUCTURE, 30);
		this.addMapping(ChromaItems.NETHERKEY, FragmentCategory.DEFENCE, 30);
		this.addMapping(ChromaItems.OREPICK, FragmentCategory.COLLECTION, 30);
		this.addMapping(ChromaItems.SPAWNERBYPASS, FragmentCategory.DEFENCE, 30);
		this.addMapping(ChromaItems.PURIFY, FragmentCategory.DEFENCE, 30);
		this.addMapping(ChromaItems.STRUCTUREFINDER, FragmentCategory.WORLD, 40);

		this.addMapping(ChromaItems.BUILDER, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaItems.TRANSITION, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaItems.TRANSITION, FragmentCategory.CONVERSION, 40);
		this.addMapping(ChromaItems.DUPLICATOR, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaItems.EXCAVATOR, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaItems.HOVERWAND, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.TELEPORT, FragmentCategory.TRAVEL, 40);
		//this.addMapping(ChromaItems.MOBSONAR, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaItems.CAPTURE, FragmentCategory.STORAGE, 40);

		this.addMapping(ChromaItems.ENDEREYE, FragmentCategory.WORLD, 40);

		this.addMapping(ChromaResearch.FERTILITYSEED, FragmentCategory.WORLD, 20);
		this.addMapping(ChromaResearch.SHARDS, FragmentCategory.LUMENS, 20);
		this.addMapping(ChromaResearch.SHARDS, FragmentCategory.CRAFTING, 20);

		this.addMapping(ChromaItems.BOTTLENECK, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaItems.CAVEPATHER, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.BULKMOVER, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaItems.AURAPOUCH, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaItems.CHAINGUN, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaItems.VACUUMGUN, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaItems.SPLASHGUN, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaItems.SPLINEATTACK, FragmentCategory.ATTACK, 40);
		//this.addMapping(ChromaItems.DATACRYSTAL, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaItems.ENDERCRYSTAL, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaItems.EFFICIENCY, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaItems.ENDERBUCKET, FragmentCategory.BUILDING, 30);
		this.addMapping(ChromaItems.ETHERPENDANT, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaItems.FLOATBOOTS, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.LINK, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaItems.KILLAURAGUN, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaItems.MULTITOOL, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaItems.ORESILK, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaItems.FINDER, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaItems.FINDER, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaItems.RECIPECACHE, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaItems.RECIPECACHE, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaResearch.STRUCTMAP, FragmentCategory.ARTIFACT, 30);
		this.addMapping(ChromaResearch.STRUCTMAP, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaItems.TELEGATELOCK, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.WARPCAPSULE, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.WIDECOLLECTOR, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaItems.LIGHTGUN, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaItems.STORAGE, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaItems.STORAGE, FragmentCategory.TOOLARMOR, 40);
		this.addMapping(ChromaItems.STORAGE, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaItems.LENS, FragmentCategory.LUMENS, 20);
		this.addMapping(ChromaItems.MOBSONAR, FragmentCategory.DEFENCE, 20);

		this.addMapping(ChromaItems.SHIELDEDCELL, FragmentCategory.IMPROVEMENT, 20);
		this.addMapping(ChromaItems.SHIELDEDCELL, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaItems.VOIDCELL, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaItems.CRYSTALCELL, FragmentCategory.STORAGE, 40);
		this.addMapping(ChromaItems.BEEFRAME, FragmentCategory.AUTOMATION, 20);
		this.addMapping(ChromaItems.WARP, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaItems.WARP, FragmentCategory.IMPROVEMENT, 20);

		this.addMapping(Chromabilities.REACH, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(Chromabilities.MAGNET, FragmentCategory.COLLECTION, 40);
		this.addMapping(Chromabilities.SHIFT, FragmentCategory.BUILDING, 40);
		this.addMapping(Chromabilities.SHIFT, FragmentCategory.WORLD, 40);
		this.addMapping(Chromabilities.SHIELD, FragmentCategory.DEFENCE, 40);
		this.addMapping(Chromabilities.SHOCKWAVE, FragmentCategory.ATTACK, 40);
		this.addMapping(Chromabilities.HEAL, FragmentCategory.DEFENCE, 40);
		this.addMapping(Chromabilities.FIREBALL, FragmentCategory.ATTACK, 40);
		this.addMapping(Chromabilities.LIGHTNING, FragmentCategory.ATTACK, 40);
		this.addMapping(Chromabilities.COMMUNICATE, FragmentCategory.DEFENCE, 40);
		this.addMapping(Chromabilities.PYLON, FragmentCategory.DEFENCE, 40);
		this.addMapping(Chromabilities.PYLON, FragmentCategory.LUMENS, 20);
		this.addMapping(Chromabilities.HEALTH, FragmentCategory.DEFENCE, 40);
		this.addMapping(Chromabilities.HEALTH, FragmentCategory.IMPROVEMENT, 30);
		this.addMapping(Chromabilities.MEINV, FragmentCategory.AUTOMATION, 40);
		this.addMapping(Chromabilities.MEINV, FragmentCategory.BUILDING, 40);
		this.addMapping(Chromabilities.TELEPORT, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.DEATHPROOF, FragmentCategory.DEFENCE, 40);
		this.addMapping(Chromabilities.DIMPING, FragmentCategory.WORLD, 40);
		this.addMapping(Chromabilities.LASER, FragmentCategory.ATTACK, 40);
		this.addMapping(Chromabilities.FIRERAIN, FragmentCategory.WORLD, 40);
		this.addMapping(Chromabilities.FIRERAIN, FragmentCategory.ATTACK, 40);
		this.addMapping(Chromabilities.GROWAURA, FragmentCategory.WORLD, 40);
		//this.addMapping(Chromabilities.GROWAURA, FragmentCategory.DEFENCE, 40);
		this.addMapping(Chromabilities.LEECH, FragmentCategory.ATTACK, 40);
		this.addMapping(Chromabilities.FLOAT, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.JUMP, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.NUKER, FragmentCategory.COLLECTION, 40);
		this.addMapping(Chromabilities.BREADCRUMB, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.DASH, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.KEEPINV, FragmentCategory.DEFENCE, 40);
		this.addMapping(Chromabilities.ORECLIP, FragmentCategory.COLLECTION, 40);
		this.addMapping(Chromabilities.ORECLIP, FragmentCategory.WORLD, 40);
		this.addMapping(Chromabilities.DOUBLECRAFT, FragmentCategory.CRAFTING, 40);
		this.addMapping(Chromabilities.RECHARGE, FragmentCategory.LUMENS, 40);
		this.addMapping(Chromabilities.MOBSEEK, FragmentCategory.ATTACK, 40);
		this.addMapping(Chromabilities.BEEALYZE, FragmentCategory.MODINTERFACE, 30);
		this.addMapping(Chromabilities.SUPERBUILD, FragmentCategory.BUILDING, 40);
		this.addMapping(Chromabilities.CHESTCLEAR, FragmentCategory.COLLECTION, 40);
		this.addMapping(Chromabilities.MOBBAIT, FragmentCategory.DEFENCE, 40);
		this.addMapping(Chromabilities.LIGHTCAST, FragmentCategory.WORLD, 40);
		this.addMapping(Chromabilities.SPAWNERSEE, FragmentCategory.ATTACK, 40);

		this.addMapping(ChromaStructures.PYLON, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.PYLON, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.CASTING1, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaStructures.CASTING2, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaStructures.CASTING3, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaStructures.CASTING3, FragmentCategory.IMPROVEMENT, 40);
		//this.addMapping(ChromaStructures.RITUAL, FragmentCategory.CRAFTING, 40);
		//this.addMapping(ChromaStructures.RITUAL2, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaStructures.RITUAL2, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.INFUSION, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaStructures.PLAYERINFUSION, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.PLAYERINFUSION, FragmentCategory.IMPROVEMENT, 40);/*
		this.addMapping(ChromaStructures.BURROW, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.CAVERN, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.OCEAN, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.DESERT, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.SNOWSTRUCT, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.BIOMEFRAG, FragmentCategory.ARTIFACT, 40);*/
		this.addMapping(ChromaStructures.OPTIMIZER, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.OPTIMIZER, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.TREE, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.TREE_BOOSTED, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.TREE_BOOSTED, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.TREE_SENDER, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.TREE_SENDER, FragmentCategory.IMPROVEMENT, 20);
		this.addMapping(ChromaStructures.REPEATER, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.COMPOUND, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.BROADCAST, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.WEAKREPEATER, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.PORTAL, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.PERSONAL, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.CLOAKTOWER, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaStructures.PROTECT, FragmentCategory.DEFENCE, 40);
		this.addMapping(ChromaStructures.RELAY, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.RELAY, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.TELEGATE, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaStructures.METEOR1, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaStructures.METEOR2, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaStructures.METEOR2, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.METEOR3, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaStructures.METEOR3, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.PYLONBROADCAST, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.PYLONBROADCAST, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.PYLONTURBO, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.PYLONTURBO, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.DATANODE, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.WIRELESSPEDESTAL, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.WIRELESSPEDESTAL, FragmentCategory.IMPROVEMENT, 20);
		this.addMapping(ChromaStructures.WIRELESSPEDESTAL2, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.WIRELESSPEDESTAL2, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.VOIDRITUAL, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaStructures.NETHERTRAP, FragmentCategory.ATTACK, 40);
		this.addMapping(ChromaStructures.LAUNCHPAD, FragmentCategory.TRAVEL, 40);

		this.addMapping(ChromaResearch.BERRIES, FragmentCategory.LUMENS, 20);
		this.addMapping(ChromaResearch.GROUPS, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.CORES, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.HICORES, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.ALLOYS, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.IRID, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.FRAGMENT, FragmentCategory.ARTIFACT, 20);
		this.addMapping(ChromaResearch.ARTEFACT, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.ORES, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaResearch.DUSTS, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaResearch.BEES, FragmentCategory.IMPROVEMENT, 20);
		this.addMapping(ChromaResearch.CRYSTALSTONE, FragmentCategory.STRUCTURE, 40);
		this.addMapping(ChromaResearch.PROXESSENCE, FragmentCategory.IMPROVEMENT, 20);
		this.addMapping(ChromaResearch.PROXESSENCE, FragmentCategory.WORLD, 20);
		this.addMapping(ChromaResearch.PROXESSENCE, FragmentCategory.ARTIFACT, 20);
		this.addMapping(ChromaResearch.INSCRIPTION, FragmentCategory.ARTIFACT, 40);

		this.addMapping(ChromaResearch.ENERGY, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.CRYSTALS, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaResearch.PYLONS, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaResearch.PYLONS, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.ELEMENTS, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.STRUCTURES, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.STRUCTURES, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaResearch.TRANSMISSION, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.CRAFTING, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.CRAFTING2, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.ENCHANTS, FragmentCategory.TOOLARMOR, 40);
		this.addMapping(ChromaResearch.APIRECIPES, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaResearch.APIRECIPES, FragmentCategory.MODINTERFACE, 30);
		this.addMapping(ChromaResearch.USINGRUNES, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaResearch.LEYLINES, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.TURBO, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.TURBO, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaResearch.TURBOREPEATER, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.TURBOREPEATER, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaResearch.TURBOREPEATER, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaResearch.TURBOREPEATER2, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.TURBOREPEATER2, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaResearch.DIMENSION, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.DIMENSION2, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.FARLANDS, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.DIMENSION, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaResearch.DIMENSION2, FragmentCategory.WORLD, 40);
		this.addMapping(ChromaResearch.FARLANDS, FragmentCategory.WORLD, 40);
		//this.addMapping(ChromaResearch.NODENET, FragmentCategory.MODINTERFACE, 40);
		this.addMapping(ChromaResearch.NODENET, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaResearch.NODENET, FragmentCategory.IMPROVEMENT, 30);
		this.addMapping(ChromaResearch.DIMTUNING, FragmentCategory.IMPROVEMENT, 30);
		this.addMapping(ChromaResearch.DIMTUNING, FragmentCategory.WORLD, 30);
		this.addMapping(ChromaResearch.SELFCHARGE, FragmentCategory.LUMENS, 40);
		//this.addMapping(ChromaResearch.MYSTPAGE, FragmentCategory.MODINTERFACE, 40);
		//this.addMapping(ChromaResearch.ENCHANTING, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaResearch.ENCHANTING, FragmentCategory.TOOLARMOR, 30);
		this.addMapping(ChromaResearch.ENCHANTING, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaResearch.ABILITIES, FragmentCategory.ABILITY, 40);
		this.addMapping(ChromaResearch.CASTTUNING, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.CASTTUNING, FragmentCategory.IMPROVEMENT, 30);
		//this.addMapping(ChromaResearch.MULTIBLOCKS, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaResearch.MULTIBLOCKS, FragmentCategory.STRUCTURE, 30);
		this.addMapping(ChromaResearch.PYLONLINK, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.PYLONLINK, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.ITEMBURNER, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.MONUMENT, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.MONUMENT, FragmentCategory.CONVERSION, 40);
		this.addMapping(ChromaResearch.MONUMENT, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaResearch.MONUMENT, FragmentCategory.STRUCTURE, 40);
		this.addMapping(ChromaResearch.MUD, FragmentCategory.WORLD, 20);
		this.addMapping(ChromaResearch.MUD, FragmentCategory.LUMENS, 20);
		this.addMapping(ChromaResearch.ITEMCHARGE, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaResearch.ITEMCHARGE, FragmentCategory.TOOLARMOR, 30);
		this.addMapping(ChromaResearch.BALLLIGHTNING, FragmentCategory.WORLD, 10);
		this.addMapping(ChromaResearch.STRUCTUREPASSWORDS, FragmentCategory.STRUCTURE, 30);
		this.addMapping(ChromaResearch.STRUCTUREPASSWORDS, FragmentCategory.ARTIFACT, 20);
	}

	private void addMapping(ChromaTiles r, FragmentCategory fc, int amt) {
		if (r.isDummiedOut())
			return;
		ChromaResearch f = r.getFragment();
		if (f != null && f.getMachine() == r)
			this.addMapping(f, fc, amt);
		else
			throw new RegistrationException(ChromatiCraft.instance, "Tried to add a mapping for "+r+"@"+r.getClass()+", which does not have its own fragment!");
	}

	private void addMapping(ChromaItems r, FragmentCategory fc, int amt) {
		if (r.isDummiedOut())
			return;
		ChromaResearch f = r.getFragment();
		if (f != null && f.getItem() == r)
			this.addMapping(f, fc, amt);
		else
			throw new RegistrationException(ChromatiCraft.instance, "Tried to add a mapping for "+r+"@"+r.getClass()+", which does not have its own fragment!");
	}

	private void addMapping(ChromaBlocks r, FragmentCategory fc, int amt) {
		if (r.isDummiedOut())
			return;
		ChromaResearch f = r.getFragment();
		if (f != null && f.getBlock() == r)
			this.addMapping(f, fc, amt);
		else
			throw new RegistrationException(ChromatiCraft.instance, "Tried to add a mapping for "+r+"@"+r.getClass()+", which does not have its own fragment!");
	}

	private void addMapping(Chromabilities r, FragmentCategory fc, int amt) {
		if (r.isDummiedOut())
			return;
		ChromaResearch f = r.getFragment();
		if (f != null && f.getAbility() == r)
			this.addMapping(f, fc, amt);
		else
			throw new RegistrationException(ChromatiCraft.instance, "Tried to add a mapping for "+r+"@"+r.getClass()+", which does not have its own fragment!");
	}

	private void addMapping(ChromaStructures r, FragmentCategory fc, int amt) {
		ChromaResearch f = r.getFragment();
		if (f != null && f.getStructure() == r)
			this.addMapping(f, fc, amt);
		else
			throw new RegistrationException(ChromatiCraft.instance, "Tried to add a mapping for "+r+"@"+r.getClass()+", which does not have its own fragment!");
	}

	private void addMapping(ChromaResearch r, FragmentCategory fc, int amt) {
		if (r == null)
			throw new RegistrationException(ChromatiCraft.instance, "Tried to add a mapping for a null fragment!");
		if (r.isDummiedOut())
			;//throw new RegistrationException(ChromatiCraft.instance, "Tried to add a mapping for a disabled fragment!");
		if (!r.isDummiedOut() && !ChromaResearch.getAllObtainableFragments().contains(r))
			throw new RegistrationException(ChromatiCraft.instance, "Tried to add a mapping for an unobtainable fragment!");
		CountMap<FragmentCategory> map = mappings.get(r);
		if (map == null) {
			map = new CountMap();
			mappings.put(r, map);
		}
		if (map.get(fc) > 0)
			throw new RegistrationException(ChromatiCraft.instance, "Duplicate category ("+fc+") mapping for "+r);
		map.increment(fc, amt);
	}

	public void calculate() {
		HashSet<ChromaResearch> empty = new HashSet();
		for (ChromaResearch r : ChromaResearch.getAllObtainableFragments()) {
			FragmentCategorization fc = this.calculateCategoriesFor(r);
			if (fc.weights.isEmpty())
				empty.add(r);
			categories.put(r, fc);
			for (Entry<FragmentCategory, Double> e : fc.weights.entrySet()) {
				FragmentCategory c = e.getKey();
				FragmentTable t = tables.get(c);
				if (t == null) {
					t = new FragmentTable(c);
					tables.put(c, t);
				}
				t.weights.put(r, e.getValue());
			}
		}
		if (!empty.isEmpty())
			throw new RegistrationException(ChromatiCraft.instance, "Fragments "+empty+" have no categories!");
	}

	public FragmentCategorization getCategories(ChromaResearch r) {
		return categories.get(r);
	}

	public FragmentTable getFragments(FragmentCategory r) {
		return tables.get(r);
	}

	private FragmentCategorization calculateCategoriesFor(ChromaResearch r) {
		FragmentCategorization ret = new FragmentCategorization(r);
		CountMap<FragmentCategory> map = mappings.get(r);

		if (map != null) {
			for (FragmentCategory fc : map.keySet()) {
				ret.add(fc, map.get(fc));
			}
		}

		if (r.getParent() == ChromaResearch.INTRO)
			ret.add(FragmentCategory.INFO, 20);
		if (r.isMachine() || r.getParent() == ChromaResearch.BLOCKS)
			ret.add(FragmentCategory.BLOCK, 20);
		if (r.isTool())
			ret.add(FragmentCategory.TOOLARMOR, 20);
		if (r.getParent() == ChromaResearch.RESOURCEDESC)
			ret.add(FragmentCategory.RESOURCE, 20);
		if (r.isAbility())
			ret.add(FragmentCategory.ABILITY, 20);

		ChromaStructures s = r.getStructure();
		if (s != null) {
			ret.add(FragmentCategory.STRUCTURE, 20);
			if (s.isNatural())
				ret.add(FragmentCategory.ARTIFACT, 20);
		}

		ChromaTiles t = r.getMachine();
		if (t != null && t.isRepeater())
			ret.add(FragmentCategory.LUMENS, 40);
		if (t != null && FocusAcceleratable.class.isAssignableFrom(t.getTEClass()))
			ret.add(FragmentCategory.CRAFTING, 40);

		if (t != null && t.getPrerequisite() != null)
			ret.add(FragmentCategory.MODINTERFACE, 30);
		else if (r.getItem() != null && r.getItem().hasPrerequisite())
			ret.add(FragmentCategory.MODINTERFACE, 30);
		else if (r.getAbility() != null && r.getAbility().getModDependency() != null)
			ret.add(FragmentCategory.MODINTERFACE, 30);
		else if (r.getDependency() != null && !(r.getDependency() instanceof ChromaOptions))
			ret.add(FragmentCategory.MODINTERFACE, 30);
		return ret;
	}

	public void dumpData() {
		for (ChromaResearch r : ChromaResearch.getAllObtainableFragments()) {
			FragmentCategorization c = this.getCategories(r);
			ReikaJavaLibrary.pConsole("Fragment "+r.getTitle()+" ("+r.name()+") = "+c.weights.size()+":"+c.weights);
			if (c.weights.size() == 1)
				ReikaJavaLibrary.pConsole("Fragment "+r.getTitle()+" has no special categories!");
		}
		for (FragmentCategory c : FragmentCategory.list) {
			FragmentTable t = this.getFragments(c);
			ReikaJavaLibrary.pConsole("Category "+c+" = "+t.weights.size()+": "+t.weights);
		}
	}

	public static class FragmentCategorization {

		private final ChromaResearch fragment;
		private final EnumMap<FragmentCategory, Double> weights = new EnumMap(FragmentCategory.class);

		private FragmentCategorization(ChromaResearch r) {
			fragment = r;
		}

		private void add(FragmentCategory c, double amt) {
			Double get = weights.get(c);
			if (get == null)
				get = 0D;
			weights.put(c, Math.max(amt, get.doubleValue()));
		}

		public WeightedRandom<FragmentCategory> getSelection() {
			WeightedRandom<FragmentCategory> ret = new WeightedRandom();
			for (Entry<FragmentCategory, Double> e : weights.entrySet()) {
				ret.addEntry(e.getKey(), e.getValue());
			}
			return ret;
		}

		public Set<FragmentCategory> set() {
			return weights.keySet();
		}

	}

	public static class FragmentTable {

		private final FragmentCategory category;
		private final EnumMap<ChromaResearch, Double> weights = new EnumMap(ChromaResearch.class);

		private FragmentTable(FragmentCategory r) {
			category = r;
		}

		private void add(ChromaResearch c, double amt) {
			Double get = weights.get(c);
			if (get == null)
				get = 0D;
			get += amt;
			weights.put(c, get);
		}

		public WeightedRandom<ChromaResearch> getSelectionForPlayer(EntityPlayer ep) {
			HashSet<ChromaResearch> set = new HashSet(ChromaResearchManager.instance.getNextResearchesFor(ep));
			WeightedRandom<ChromaResearch> ret = new WeightedRandom();
			for (Entry<ChromaResearch, Double> e : weights.entrySet()) {
				ChromaResearch r = e.getKey();
				if (!set.contains(r))
					continue;
				ret.addEntry(r, e.getValue());

			}
			return ret;
		}

	}

	public static enum FragmentCategory {
		AUTOMATION(true),
		DEFENCE,
		ATTACK,
		BUILDING,
		COLLECTION,
		TRAVEL,
		CRAFTING,
		LUMENS,
		WORLD,
		//PLAYER,
		CONVERSION,
		STORAGE,

		ARTIFACT,
		IMPROVEMENT,
		MODINTERFACE,

		INFO,
		BLOCK,
		TOOLARMOR,
		ABILITY,
		RESOURCE,
		STRUCTURE,
		;

		public final boolean isPrimary;
		private int sortIndex;

		public static final FragmentCategory[] list = values();

		private FragmentCategory() {
			this(false);
		}

		private FragmentCategory(boolean p) {
			isPrimary = p;
		}

		public double getAngle() {
			return 360D/list.length*sortIndex;
		}

		static {
			ArrayList<FragmentCategory> li = new ArrayList();
			for (FragmentCategory c : list) {
				li.add(c);
			}
			Collections.sort(li, new Comparator<FragmentCategory>(){
				@Override
				public int compare(FragmentCategory o1, FragmentCategory o2) {
					return String.CASE_INSENSITIVE_ORDER.compare(ControlledConfig.getUserHash(o1.name()), ControlledConfig.getUserHash(o2.name()));
				}
			});
			for (FragmentCategory c : list) {
				c.sortIndex = li.indexOf(c);
			}
		}
	}

}
