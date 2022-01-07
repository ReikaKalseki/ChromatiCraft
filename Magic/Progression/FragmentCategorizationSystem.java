package Reika.ChromatiCraft.Magic.Progression;

import java.util.EnumMap;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FocusAcceleratable;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;

public class FragmentCategorizationSystem {

	public static final FragmentCategorizationSystem instance = new FragmentCategorizationSystem();

	private final EnumMap<ChromaResearch, CountMap<FragmentCategory>> mappings = new EnumMap(ChromaResearch.class);
	private final EnumMap<ChromaResearch, FragmentCategorization> data = new EnumMap(ChromaResearch.class);

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
		this.addMapping(ChromaTiles.RELAYSOURCE, FragmentCategory.LUMENS, 40);

		this.addMapping(ChromaTiles.REVERTER, FragmentCategory.PROTECTION, 30);
		this.addMapping(ChromaTiles.COBBLEGEN, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.HARVESTPLANT, FragmentCategory.COLLECTION, 40);

		this.addMapping(ChromaTiles.ENCHANTER, FragmentCategory.AUTOMATION, 30);
		this.addMapping(ChromaTiles.ENCHANTER, FragmentCategory.TOOLARMOR, 30);
		this.addMapping(ChromaTiles.FURNACE, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.GLOWFIRE, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaTiles.REPROGRAMMER, FragmentCategory.AUTOMATION, 30);

		this.addMapping(ChromaTiles.REPROGRAMMER, FragmentCategory.AUTOMATION, 30);

		this.addMapping(ChromaTiles.AUTOMATOR, FragmentCategory.AUTOMATION, 40);
		this.addMapping(ChromaTiles.PLAYERINFUSER, FragmentCategory.LUMENS, 30);
		this.addMapping(ChromaTiles.RITUAL, FragmentCategory.ABILITY, 30);

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

		this.addMapping(ChromaTiles.DATANODE, FragmentCategory.ARTIFACT, 40);
		this.addMapping(ChromaTiles.FARMER, FragmentCategory.COLLECTION, 40);
		this.addMapping(ChromaTiles.PERSONAL, FragmentCategory.LUMENS, 40);

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

		this.addMapping(ChromaResearch.BERRIES, FragmentCategory.LUMENS, 20);
		this.addMapping(ChromaResearch.GROUPS, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.CORES, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.HICORES, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.ALLOYS, FragmentCategory.CRAFTING, 40);
		this.addMapping(ChromaResearch.FRAGMENT, FragmentCategory.ARTIFACT, 20);
		this.addMapping(ChromaResearch.ARTEFACT, FragmentCategory.ARTIFACT, 40);
	}

	private void addMapping(ChromaTiles r, FragmentCategory fc, int amt) {
		this.addMapping(r.getFragment(), fc, amt);
	}

	private void addMapping(ChromaItems r, FragmentCategory fc, int amt) {
		this.addMapping(r.getFragment(), fc, amt);
	}

	private void addMapping(Chromabilities r, FragmentCategory fc, int amt) {
		this.addMapping(r.getFragment(), fc, amt);
	}

	private void addMapping(ChromaResearch r, FragmentCategory fc, int amt) {
		CountMap<FragmentCategory> map = mappings.get(r);
		if (map == null) {
			map = new CountMap();
			mappings.put(r, map);
		}
		map.increment(fc, amt);
	}

	public FragmentCategorization getCategories(ChromaResearch r) {
		FragmentCategorization fc = data.get(r);
		if (fc == null) {
			fc = this.calculateCategoriesFor(r);
			if (fc.weights.isEmpty())
				throw new RegistrationException(ChromatiCraft.instance, "Fragment "+r+" has no categories!");
			data.put(r, fc);
		}
		return fc;
	}

	private FragmentCategorization calculateCategoriesFor(ChromaResearch r) {
		FragmentCategorization ret = new FragmentCategorization();
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
		else if (r.getDependency() != null)
			ret.add(FragmentCategory.MODINTERFACE, 30);
		return ret;
	}

	public static class FragmentCategorization {

		private final EnumMap<FragmentCategory, Double> weights = new EnumMap(FragmentCategory.class);

		private void add(FragmentCategory c, double amt) {
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

		INFO,
		BLOCK,
		TOOLARMOR,
		ABILITY,
		RESOURCE,
		STRUCTURE,

		MODINTERFACE,
	}

}
