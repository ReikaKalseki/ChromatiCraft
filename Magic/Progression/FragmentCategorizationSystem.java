package Reika.ChromatiCraft.Magic.Progression;

import java.util.EnumMap;
import java.util.Map.Entry;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FocusAcceleratable;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class FragmentCategorizationSystem {

	public static final FragmentCategorizationSystem instance = new FragmentCategorizationSystem();

	private final EnumMap<ChromaResearch, CountMap<FragmentCategory>> mappings = new EnumMap(ChromaResearch.class);
	private final EnumMap<ChromaResearch, FragmentCategorization> categories = new EnumMap(ChromaResearch.class);
	private final EnumMap<FragmentCategory, FragmentTable> tables = new EnumMap(FragmentCategory.class);

	private FragmentCategorizationSystem() {
		this.addMapping(ChromaTiles.COLLECTOR, FragmentCategory.CRAFTING, 20);
		this.addMapping(ChromaTiles.FABRICATOR, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.MINER, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.TELEPUMP, FragmentCategory.COLLECTION, 40);

		this.addMapping(ChromaTiles.AVOLASER, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaTiles.LAMP, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaTiles.CLOAKING, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaTiles.BEACON, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaTiles.FENCE, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaTiles.EXPLOSIONSHIELD, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaTiles.GUARDIAN, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaTiles.TURRET, FragmentCategory.PROTECTION, 30);
		this.addMapping(ChromaTiles.METEOR, FragmentCategory.PROTECTION, 40);

		this.addMapping(ChromaTiles.AREABREAKER, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaTiles.MULTIBUILDER, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaTiles.DEATHFOG, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.AURAPOINT, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaTiles.HOVERPAD, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaTiles.ITEMCOLLECTOR, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.INSERTER, FragmentCategory.AUTOMATION, 40);

		this.addMapping(ChromaTiles.CRYSTAL, FragmentCategory.LUMENS, 20);
		this.addMapping(ChromaTiles.FOCUSCRYSTAL, FragmentCategory.CRAFTING, 20);
		this.addMapping(ChromaTiles.PYLONTURBO, FragmentCategory.LUMENS, 30);

		this.addMapping(ChromaTiles.PARTICLES, FragmentCategory.BUILDING, 30);

		this.addMapping(ChromaTiles.OPTIMIZER, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaTiles.OPTIMIZER, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaResearch.RELAY, FragmentCategory.LUMENS, 40);

		this.addMapping(ChromaTiles.REVERTER, FragmentCategory.PROTECTION, 30);
		this.addMapping(ChromaTiles.COBBLEGEN, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.HARVESTPLANT, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.PLANTACCEL, FragmentCategory.IMPROVEMENT, 40);

		this.addMapping(ChromaTiles.ENCHANTER, FragmentCategory.AUTOMATION, 30);
		this.addMapping(ChromaTiles.ENCHANTER, FragmentCategory.TOOLARMOR, 30);
		this.addMapping(ChromaTiles.FURNACE, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.GLOWFIRE, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.REPROGRAMMER, FragmentCategory.AUTOMATION, 30);

		this.addMapping(ChromaTiles.AUTOMATOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.AUTOMATOR, FragmentCategory.IMPROVEMENT, 30);
		this.addMapping(ChromaTiles.PLAYERINFUSER, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaTiles.PLAYERINFUSER, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaTiles.RITUAL, FragmentCategory.ABILITY, 30);
		this.addMapping(ChromaTiles.STAND, FragmentCategory.CRAFTING, 20);

		this.addMapping(ChromaTiles.POWERTREE, FragmentCategory.LUMENS, 50);

		this.addMapping(ChromaTiles.FLUIDDISTRIBUTOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.FLUIDRELAY, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.ITEMRIFT, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.LAUNCHPAD, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaTiles.NETWORKITEM, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.RFDISTRIBUTOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.RIFT, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.ROUTERHUB, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.TELEPORT, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaTiles.WINDOW, FragmentCategory.TRAVEL, 40);

		//this.addMapping(ChromaTiles.DATANODE, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaTiles.FARMER, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.PERSONAL, FragmentCategory.LUMENS, 40);

		this.addMapping(ChromaTiles.VOIDTRAP, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaTiles.ALVEARY, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.ALVEARY, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaTiles.ALVEARY, FragmentCategory.LUMENS, 20);
		this.addMapping(ChromaTiles.LANDMARK, FragmentCategory.BUILDING, 30);
		this.addMapping(ChromaTiles.MANABOOSTER, FragmentCategory.AUTOMATION, 30);
		this.addMapping(ChromaTiles.MANABOOSTER, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaTiles.MANABOOSTER, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.BOOKDECOMP, FragmentCategory.COLLECTION, 30);
		this.addMapping(ChromaTiles.SMELTERYDISTRIBUTOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.SMELTERYDISTRIBUTOR, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.ASPECT, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaTiles.FLUXMAKER, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaTiles.ESSENTIARELAY, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.MEDISTRIBUTOR, FragmentCategory.AUTOMATION, 40);

		this.addMapping(ChromaItems.PROBE, FragmentCategory.STRUCTURE, 30);
		this.addMapping(ChromaItems.NETHERKEY, FragmentCategory.PROTECTION, 30);
		this.addMapping(ChromaItems.OREPICK, FragmentCategory.COLLECTION, 30);
		this.addMapping(ChromaItems.SPAWNERBYPASS, FragmentCategory.PROTECTION, 30);
		this.addMapping(ChromaItems.PURIFY, FragmentCategory.PROTECTION, 30);

		this.addMapping(ChromaItems.BUILDER, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaItems.TRANSITION, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaItems.DUPLICATOR, FragmentCategory.BUILDING, 40);
		this.addMapping(ChromaItems.EXCAVATOR, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaItems.HOVERWAND, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.TELEPORT, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.MOBSONAR, FragmentCategory.PROTECTION, 40);

		this.addMapping(ChromaItems.BOTTLENECK, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaItems.CAVEPATHER, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.BULKMOVER, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaItems.CHAINGUN, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaItems.VACUUMGUN, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaItems.SPLASHGUN, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaItems.SPLINEATTACK, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaItems.DATACRYSTAL, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaItems.EFFICIENCY, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaItems.ENDERBUCKET, FragmentCategory.BUILDING, 30);
		this.addMapping(ChromaItems.FLOATBOOTS, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.LINK, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaItems.KILLAURAGUN, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaItems.MULTITOOL, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaItems.ORESILK, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaItems.FINDER, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaItems.RECIPECACHE, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaItems.STRUCTMAP, FragmentCategory.ARTIFACT, 30);
		this.addMapping(ChromaItems.TELEGATELOCK, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.WARPCAPSULE, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaItems.WIDECOLLECTOR, FragmentCategory.COLLECTION, 40);

		this.addMapping(ChromaItems.SHIELDEDCELL, FragmentCategory.IMPROVEMENT, 20);
		this.addMapping(ChromaItems.BEEFRAME, FragmentCategory.AUTOMATION, 20);
		this.addMapping(ChromaItems.WARP, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaItems.WARP, FragmentCategory.IMPROVEMENT, 20);

		this.addMapping(Chromabilities.MAGNET, FragmentCategory.COLLECTION, 40);
		this.addMapping(Chromabilities.SHIFT, FragmentCategory.BUILDING, 40);
		this.addMapping(Chromabilities.SHIELD, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.SHOCKWAVE, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.HEAL, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.FIREBALL, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.COMMUNICATE, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.PYLON, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.PYLON, FragmentCategory.LUMENS, 20);
		this.addMapping(Chromabilities.HEALTH, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.HEALTH, FragmentCategory.IMPROVEMENT, 30);
		this.addMapping(Chromabilities.MEINV, FragmentCategory.AUTOMATION, 40);
		this.addMapping(Chromabilities.MEINV, FragmentCategory.BUILDING, 40);
		this.addMapping(Chromabilities.TELEPORT, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.DEATHPROOF, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.LEECH, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.FLOAT, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.JUMP, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.BREADCRUMB, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.DASH, FragmentCategory.TRAVEL, 40);
		this.addMapping(Chromabilities.KEEPINV, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.ORECLIP, FragmentCategory.COLLECTION, 40);
		this.addMapping(Chromabilities.DOUBLECRAFT, FragmentCategory.CRAFTING, 40);
		this.addMapping(Chromabilities.RECHARGE, FragmentCategory.LUMENS, 40);
		this.addMapping(Chromabilities.MOBSEEK, FragmentCategory.PROTECTION, 40);
		this.addMapping(Chromabilities.BEEALYZE, FragmentCategory.MODINTERFACE, 30);
		this.addMapping(Chromabilities.SUPERBUILD, FragmentCategory.BUILDING, 40);
		this.addMapping(Chromabilities.CHESTCLEAR, FragmentCategory.COLLECTION, 40);
		this.addMapping(Chromabilities.MOBBAIT, FragmentCategory.PROTECTION, 40);

		this.addMapping(ChromaStructures.PYLON, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaStructures.PYLON, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.CASTING1, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaStructures.CASTING2, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaStructures.CASTING3, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaStructures.CASTING3, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.RITUAL, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaStructures.RITUAL2, FragmentCategory.CRAFTING, 40);
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
		this.addMapping(ChromaStructures.CLOAKTOWER, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaStructures.PROTECT, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaStructures.RELAY, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaStructures.RELAY, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.TELEGATE, FragmentCategory.TRAVEL, 40);
		this.addMapping(ChromaStructures.METEOR1, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaStructures.METEOR2, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaStructures.METEOR2, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaStructures.METEOR3, FragmentCategory.PROTECTION, 40);
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
		this.addMapping(ChromaStructures.VOIDRITUAL, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaStructures.NETHERTRAP, FragmentCategory.PROTECTION, 40);
		this.addMapping(ChromaStructures.LAUNCHPAD, FragmentCategory.TRAVEL, 40);

		this.addMapping(ChromaResearch.BERRIES, FragmentCategory.LUMENS, 20);
		this.addMapping(ChromaResearch.GROUPS, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.CORES, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.HICORES, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.ALLOYS, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.FRAGMENT, FragmentCategory.ARTIFACT, 20);
		this.addMapping(ChromaResearch.ARTEFACT, FragmentCategory.ARTIFACT, 40);

		this.addMapping(ChromaResearch.ENERGY, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.CRYSTALS, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaResearch.PYLONS, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaResearch.PYLONS, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.ELEMENTS, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.STRUCTURES, FragmentCategory.ARTIFACT, 40);
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
		this.addMapping(ChromaResearch.DIMENSION3, FragmentCategory.ARTIFACT, 40);
		//this.addMapping(ChromaResearch.NODENET, FragmentCategory.MODINTERFACE, 40);
		this.addMapping(ChromaResearch.NODENET, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaResearch.NODENET, FragmentCategory.IMPROVEMENT, 30);
		this.addMapping(ChromaResearch.DIMTUNING, FragmentCategory.IMPROVEMENT, 30);
		this.addMapping(ChromaResearch.SELFCHARGE, FragmentCategory.LUMENS, 40);
		//this.addMapping(ChromaResearch.MYSTPAGE, FragmentCategory.MODINTERFACE, 40);
		this.addMapping(ChromaResearch.ENCHANTING, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaResearch.ENCHANTING, FragmentCategory.TOOLARMOR, 30);
		this.addMapping(ChromaResearch.ENCHANTING, FragmentCategory.IMPROVEMENT, 40);
		this.addMapping(ChromaResearch.ABILITIES, FragmentCategory.ABILITY, 40);
		this.addMapping(ChromaResearch.CASTTUNING, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.CASTTUNING, FragmentCategory.IMPROVEMENT, 30);
		this.addMapping(ChromaResearch.MULTIBLOCKS, FragmentCategory.CRAFTING, 30);
		this.addMapping(ChromaResearch.MULTIBLOCKS, FragmentCategory.STRUCTURE, 30);
		this.addMapping(ChromaResearch.PYLONLINK, FragmentCategory.LUMENS, 40);
		this.addMapping(ChromaResearch.PYLONLINK, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaResearch.ITEMBURNER, FragmentCategory.LUMENS, 40);
	}

	private void addMapping(ChromaTiles r, FragmentCategory fc, int amt) {
		ChromaResearch f = r.getFragment();
		if (f != null && f.getMachine() != null)
			this.addMapping(r.getFragment(), fc, amt);
	}

	private void addMapping(ChromaItems r, FragmentCategory fc, int amt) {
		ChromaResearch f = r.getFragment();
		if (f != null && f.getItem() != null)
			this.addMapping(r.getFragment(), fc, amt);
	}

	private void addMapping(Chromabilities r, FragmentCategory fc, int amt) {
		ChromaResearch f = r.getFragment();
		if (f != null && f.getAbility() != null)
			this.addMapping(r.getFragment(), fc, amt);
	}

	private void addMapping(ChromaStructures r, FragmentCategory fc, int amt) {
		ChromaResearch f = r.getFragment();
		if (f != null && f.getStructure() != null)
			this.addMapping(f, fc, amt);
	}

	private void addMapping(ChromaResearch r, FragmentCategory fc, int amt) {
		if (r == null)
			throw new RegistrationException(ChromatiCraft.instance, "Tried to add a mapping for a null fragment!");
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
		for (ChromaResearch r : ChromaResearch.getAllObtainableFragments()) {
			FragmentCategorization fc = this.calculateCategoriesFor(r);
			if (fc.weights.isEmpty())
				throw new RegistrationException(ChromatiCraft.instance, "Fragment "+r+" has no categories!");
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
		}
		for (FragmentCategory c : FragmentCategory.values()) {
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
			get += amt;
			weights.put(c, get);
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

	}

	public static enum FragmentCategory {
		AUTOMATION,
		PROTECTION,
		BUILDING,
		COLLECTION,
		TRAVEL,
		CRAFTING,
		LUMENS,
		ARTIFACT,

		IMPROVEMENT,

		INFO,
		BLOCK,
		TOOLARMOR,
		ABILITY,
		RESOURCE,
		STRUCTURE,

		MODINTERFACE,
	}

}
