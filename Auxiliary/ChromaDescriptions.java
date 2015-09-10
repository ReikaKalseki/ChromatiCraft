/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraftforge.common.MinecraftForge;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Magic.Network.RelayNetworker;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectJar;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalTank;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityChromaLamp;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalBeacon;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalLaser;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityGuardianStone;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityCollector;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityCrystalFurnace;
import Reika.DragonAPI.Instantiable.Event.Client.ResourceReloadEvent;
import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class ChromaDescriptions {

	private static String PARENT = getParent();
	private static final String DESC_SUFFIX = ":desc";
	private static final String NOTE_SUFFIX = ":note";

	private static final HashMap<ChromaResearch, String> data = new HashMap<ChromaResearch, String>();
	private static final HashMap<ChromaResearch, String> notes = new HashMap<ChromaResearch, String>();

	private static final HashMap<ChromaTiles, Object[]> machineData = new HashMap<ChromaTiles, Object[]>();
	private static final HashMap<ChromaTiles, Object[]> machineNotes = new HashMap<ChromaTiles, Object[]>();
	private static final HashMap<ChromaBlocks, Object[]> blockData = new HashMap<ChromaBlocks, Object[]>();
	private static final HashMap<ChromaItems, Object[]> itemData = new HashMap<ChromaItems, Object[]>();
	private static final HashMap<ChromaResearch, Object[]> miscData = new HashMap<ChromaResearch, Object[]>();
	private static final EnumMap<Chromabilities, Object[]> abilityData = new EnumMap(Chromabilities.class);
	private static final HashMap<String, Object[]> hoverData = new HashMap<String, Object[]>();
	private static final HashMap<CrystalElement, Object[]> elementData = new HashMap<CrystalElement, Object[]>();

	private static final HashMap<String, String> hoverText = new HashMap<String, String>();
	private static final EnumMap<ProgressStage, ProgressNote> progressText = new EnumMap(ProgressStage.class);
	private static final EnumMap<Chromabilities, String> abilityText = new EnumMap(Chromabilities.class);
	private static final EnumMap<CrystalElement, String> elementText = new EnumMap(CrystalElement.class);

	private static final boolean mustLoad = !ReikaObfuscationHelper.isDeObfEnvironment();
	private static final XMLInterface machines = new XMLInterface(ChromatiCraft.class, PARENT+"machines.xml", mustLoad);
	private static final XMLInterface elements = new XMLInterface(ChromatiCraft.class, PARENT+"elements.xml", mustLoad);
	private static final XMLInterface blocks = new XMLInterface(ChromatiCraft.class, PARENT+"blocks.xml", mustLoad);
	private static final XMLInterface abilities = new XMLInterface(ChromatiCraft.class, PARENT+"abilities.xml", mustLoad);
	private static final XMLInterface structures = new XMLInterface(ChromatiCraft.class, PARENT+"structure.xml", mustLoad);
	private static final XMLInterface tools = new XMLInterface(ChromatiCraft.class, PARENT+"tools.xml", mustLoad);
	private static final XMLInterface resources = new XMLInterface(ChromatiCraft.class, PARENT+"resource.xml", mustLoad);
	private static final XMLInterface infos = new XMLInterface(ChromatiCraft.class, PARENT+"info.xml", mustLoad);
	private static final XMLInterface hover = new XMLInterface(ChromatiCraft.class, PARENT+"hover.xml", mustLoad);
	private static final XMLInterface progress = new XMLInterface(ChromatiCraft.class, PARENT+"progression.xml", mustLoad);

	private static String getParent() {
		Language language = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
		String lang = language.getLanguageCode();
		String sg = lang.toUpperCase().substring(0, 2)+"/";
		if (hasLocalizedFor(language) && !"EN".equals(sg))
			return "Resources/"+sg;
		return "Resources/";
	}

	private static boolean hasLocalizedFor(Language language) {
		String lang = language.getLanguageCode();
		String sg = lang.toUpperCase().substring(0, 2)+"/";
		Object o = ChromatiCraft.class.getResourceAsStream("Resources/"+sg+"categories.xml");
		return o != null;
	}

	public static String getHoverText(String key) {
		return hoverText.get(key);
	}

	private static void addData(ChromaTiles m, Object... data) {
		machineData.put(m, data);
	}

	private static void addData(ChromaBlocks m, Object... data) {
		blockData.put(m, data);
	}

	private static void addData(ChromaItems m, Object... data) {
		itemData.put(m, data);
	}

	private static void addData(ChromaResearch h, Object... data) {
		miscData.put(h, data);
	}

	private static void addData(CrystalElement e, Object... data) {
		elementData.put(e, data);
	}

	private static void addData(String s, Object... data) {
		hoverData.put(s, data);
	}

	private static void addData(ChromaResearch h, int[] data) {
		Object[] o = new Object[data.length];
		for (int i = 0; i < o.length; i++)
			o[i] = data[i];
		miscData.put(h, o);
	}

	private static void addNotes(ChromaTiles m, Object... data) {
		machineNotes.put(m, data);
	}

	public static void reload() {
		PARENT = getParent();

		loadNumericalData();

		machines.reread();
		elements.reread();
		blocks.reread();
		abilities.reread();
		tools.reread();
		resources.reread();
		infos.reread();
		structures.reread();
		hover.reread();
		progress.reread();

		loadData();
	}

	private static void addEntry(ChromaResearch h, String sg) {
		data.put(h, sg);
	}

	public static void loadData() {
		ArrayList<ChromaResearch> infotabs = ChromaResearch.getInfoTabs();
		ArrayList<ChromaResearch> machinetabs = ChromaResearch.getMachineTabs();
		ArrayList<ChromaResearch> blocktabs = ChromaResearch.getBlockTabs();
		ArrayList<ChromaResearch> abilitytabs = ChromaResearch.getAbilityTabs();
		ArrayList<ChromaResearch> tooltabs = ChromaResearch.getToolTabs();
		ArrayList<ChromaResearch> resourcetabs = ChromaResearch.getResourceTabs();
		ArrayList<ChromaResearch> structuretabs = ChromaResearch.getStructureTabs();

		for (ChromaResearch h : machinetabs) {
			ChromaTiles m = h.getMachine();
			String desc = machines.getValueAtNode("machines:"+m.name().toLowerCase()+DESC_SUFFIX);
			String aux = machines.getValueAtNode("machines:"+m.name().toLowerCase()+NOTE_SUFFIX);
			desc = String.format(desc, machineData.get(m));
			aux = String.format(aux, machineNotes.get(m));

			if (XMLInterface.NULL_VALUE.equals(desc))
				desc = "There is no lexicon data for this machine yet.";
			//ReikaJavaLibrary.pConsole(m.name().toLowerCase()+":"+desc);

			if (m.isDummiedOut()) {
				desc += "\nThis machine is currently unavailable.";
				if (m.hasPrerequisite() && !m.getPrerequisite().isLoaded())
					desc += "\nThis machine depends on another mod.";
				aux += "\nNote: Dummied Out";
			}
			if (m.hasPrerequisite()) {
				String sg = m.getPrerequisite().getModLabel().replaceAll("[|]", "");
				aux += "\nDependencies: "+ReikaStringParser.splitCamelCase(sg).replaceAll(" Craft", "Craft");
			}
			if (m.isIncomplete()) {
				desc += "\nThis machine is incomplete. Use at your own risk.";
			}

			addEntry(h, desc);
			notes.put(h, aux);
		}

		for (ChromaResearch h : blocktabs) {
			String desc = blocks.getValueAtNode("blocks:"+h.name().toLowerCase());
			desc = String.format(desc, blockData.get(h.getBlock()));
			addEntry(h, desc);
		}

		for (ChromaResearch h : tooltabs) {
			String desc = tools.getValueAtNode("tools:"+h.name().toLowerCase());
			desc = String.format(desc, itemData.get(h));
			if (h.getItem().getItemInstance() instanceof ItemWandBase) {
				notes.put(h, ((ItemWandBase)h.getItem().getItemInstance()).generateUsageData());
			}
			addEntry(h, desc);
		}

		for (ChromaResearch h : resourcetabs) {
			String desc = resources.getValueAtNode("resource:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (ChromaResearch h : structuretabs) {
			String desc = structures.getValueAtNode("structure:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (ChromaResearch h : infotabs) {
			String desc = infos.getValueAtNode("info:"+h.name().toLowerCase());
			desc = String.format(desc, miscData.get(h));
			addEntry(h, desc);
		}

		for (ChromaResearch h : abilitytabs) {
			Chromabilities a = h.getAbility();
			String desc = abilities.getValueAtNode("ability:"+a.name().toLowerCase());
			desc = String.format(desc, abilityData.get(a));
			abilityText.put(a, desc);
		}

		for (CrystalElement e : CrystalElement.elements) {
			String desc = elements.getValueAtNode("elements:"+e.name().toLowerCase());
			desc = String.format(desc, elementData.get(e));
			elementText.put(e, desc);
		}

		Collection<String> keys = ChromaHelpData.instance.getHelpKeys();
		for (String s : keys) {
			String desc = hover.getValueAtNode("hover:"+s);
			desc = String.format(desc, hoverData.get(s));
			hoverText.put(s, desc);
		}

		for (int i = 0; i < ProgressStage.list.length; i++) {
			ProgressStage p = ProgressStage.list[i];
			String title = progress.getValueAtNode("progression:"+p.name().toLowerCase()+":title");
			String hint = progress.getValueAtNode("progression:"+p.name().toLowerCase()+":hint");
			String desc = progress.getValueAtNode("progression:"+p.name().toLowerCase()+":reveal");
			progressText.put(p, new ProgressNote(title.replaceAll("\\n", ""), hint.replaceAll("\\n", ""), desc.replaceAll("\\n", "")));
		}
	}

	public static String getAbilityDescription(Chromabilities c) {
		String s = abilityText.get(c);
		return s != null ? s : "This ability has no lexicon info yet.";
	}

	public static String getElementDescription(CrystalElement e) {
		return elementText.get(e);
	}

	public static String getData(ChromaResearch h) {
		if (h.getAbility() != null)
			return abilityText.get(h.getAbility());
		if (!data.containsKey(h))
			return "This item has no lexicon info yet.";
		return data.get(h);
	}

	public static String getNotes(ChromaResearch h) {
		if (!notes.containsKey(h))
			return "";
		return notes.get(h);
	}

	static {
		loadNumericalData();
		MinecraftForge.EVENT_BUS.register(new ReloadListener());
	}

	public static final class ReloadListener {

		@SubscribeEvent
		public void reload(ResourceReloadEvent evt) {
			ChromaDescriptions.reload();
		}

	}

	private static void loadNumericalData() {
		addData(ChromaTiles.REPEATER, TileEntityCrystalPylon.RANGE);
		addData(ChromaTiles.LASER, TileEntityCrystalLaser.MAX_RANGE);
		addData(ChromaTiles.BEACON, CrystalElement.RED.displayName);

		addNotes(ChromaTiles.GUARDIAN, TileEntityGuardianStone.RANGE);
		addNotes(ChromaTiles.TELEPUMP, TileEntityTeleportationPump.getRequiredEnergy().toDisplay());
		addNotes(ChromaTiles.MINER, TileEntityMiner.getRequiredEnergy().toDisplay());
		//addNotes(ChromaTiles.REPROGRAMMER, TileEntitySpawnerReprogrammer.getRequiredEnergy().toDisplay());
		addNotes(ChromaTiles.REPEATER, TileEntityCrystalRepeater.RANGE);
		addNotes(ChromaTiles.TANK, TileEntityCrystalTank.FACTOR/1000, TileEntityCrystalTank.MAXCAPACITY/1000);
		addNotes(ChromaTiles.CHARGER, TileEntityCrystalCharger.CAPACITY);
		//addNotes(ChromaTiles.TICKER, TileEntityInventoryTicker.getRequiredEnergy().toDisplay());
		addNotes(ChromaTiles.FURNACE, TileEntityCrystalFurnace.MULTIPLY);
		addNotes(ChromaTiles.FABRICATOR, FabricationRecipes.FACTOR, FabricationRecipes.INITFACTOR, FabricationRecipes.POWER);
		addNotes(ChromaTiles.BEACON, TileEntityCrystalBeacon.RATIO, TileEntityCrystalBeacon.POWER, TileEntityCrystalBeacon.MAXRANGE);
		addNotes(ChromaTiles.COLLECTOR, TileEntityCollector.XP_PER_CHROMA);
		addNotes(ChromaTiles.ITEMCOLLECTOR, TileEntityItemCollector.MAXRANGE, TileEntityItemCollector.MAXYRANGE);
		addNotes(ChromaTiles.LAMP, TileEntityChromaLamp.FACTOR);
		addNotes(ChromaTiles.POWERTREE, TileEntityPowerTree.BASE, TileEntityPowerTree.RATIO, TileEntityPowerTree.POWER);
		addNotes(ChromaTiles.LAMPCONTROL, TileEntityLampController.MAXRANGE, TileEntityLampController.MAXCHANNEL);
		addNotes(ChromaTiles.ASPECTJAR, TileEntityAspectJar.CAPACITY_PRIMAL, TileEntityAspectJar.CAPACITY);

		addData(ChromaBlocks.RELAY, RelayNetworker.instance.maxRange);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (e == CrystalElement.LIGHTGRAY)
				addData(e, e.displayName, CrystalElement.WHITE.displayName);
			else
				addData(e, e.displayName);
		}

		abilityData.put(Chromabilities.REACH, new Object[]{Chromabilities.MAX_REACH});
		abilityData.put(Chromabilities.LIFEPOINT, new Object[]{CrystalElement.MAGENTA.displayName});
	}

	public static String getParentPage() {
		return PARENT;
	}

	static ProgressNote getProgressText(ProgressStage p) {
		return progressText.get(p);
	}

	static class ProgressNote {

		public final String title;
		public final String hint;
		public final String reveal;

		public ProgressNote(String t, String h, String desc) {
			title = t;
			hint = h;
			reveal = desc;
		}

	}
}
