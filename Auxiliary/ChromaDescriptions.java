/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
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
import java.util.Locale;

import com.google.common.base.Strings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraftforge.common.MinecraftForge;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Lore.LoreScripts;
import Reika.ChromatiCraft.Magic.Lore.LoreScripts.ScriptLocations;
import Reika.ChromatiCraft.Magic.Network.RelayNetworker;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TileEntityAspectJar;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaEnchants;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalLaser;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityChromaLamp;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityCrystalBeacon;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityGuardianStone;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityCollector;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityCrystalFurnace;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityPowerTree;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Maps.PluralMap;
import Reika.DragonAPI.Instantiable.Event.Client.ResourceReloadEvent;
import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ChromaDescriptions {

	private static String PARENT = getParent(true);
	private static final String DESC_SUFFIX = ":desc";
	private static final String NOTE_SUFFIX = ":note";

	private static final HashMap<ChromaResearch, String> data = new HashMap<ChromaResearch, String>();
	private static final PluralMap<String> notes = new PluralMap(2);

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
	//private static final MultiMap<ScriptLocations, String> loreText = new MultiMap();

	private static final XMLInterface machines = loadData("machines");
	private static final XMLInterface elements = loadData("elements");
	private static final XMLInterface blocks = loadData("blocks");
	private static final XMLInterface abilities = loadData("abilities");
	private static final XMLInterface structures = loadData("structure");
	private static final XMLInterface tools = loadData("tools");
	private static final XMLInterface resources = loadData("resource");
	private static final XMLInterface infos = loadData("info");
	private static final XMLInterface hover = loadData("hover");
	private static final XMLInterface progress = loadData("progression");
	private static final XMLInterface enchants = loadData("enchants");
	private static final XMLInterface lore = LoreScripts.instance.hasReroutePath() ? new XMLInterface(LoreScripts.instance.getReroutedLoreFile(), true) : loadData("lore", true);

	private static XMLInterface loadData(String name) {
		return loadData(name, false);
	}

	private static XMLInterface loadData(String name, boolean decrypt) {
		XMLInterface xml = new XMLInterface(ChromatiCraft.class, PARENT+name+".xml", !ReikaObfuscationHelper.isDeObfEnvironment());
		xml.setFallback(getParent(false)+name+".xml");
		if (decrypt)
			xml.setEncrypted();
		xml.init();
		return xml;
	}

	private static String getParent(boolean locale) {
		return locale && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? getLocalizedParent() : "Resources/";
	}

	@SideOnly(Side.CLIENT)
	private static String getLocalizedParent() {
		Language language = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
		String lang = language.getLanguageCode();
		if (hasLocalizedFor(language) && !"en_US".equals(lang))
			return "Resources/"+lang+"/";
		return "Resources/";
	}

	@SideOnly(Side.CLIENT)
	private static boolean hasLocalizedFor(Language language) {
		String lang = language.getLanguageCode();
		Object o = ChromatiCraft.class.getResourceAsStream("Resources/"+lang+"/categories.xml");
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

	private static void addData(Chromabilities a, Object... data) {
		abilityData.put(a, data);
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
		PARENT = getParent(true);

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
		enchants.reread();
		lore.reread();

		loadRosetta();

		loadData();
	}

	@SideOnly(Side.CLIENT)
	private static void loadRosetta() {
		if (Minecraft.getMinecraft().thePlayer != null)
			LoreManager.instance.getOrCreateRosetta(Minecraft.getMinecraft().thePlayer).loadText();
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
			if (m == ChromaTiles.ADJACENCY) {
				ArrayList<String> pages = new ArrayList();
				pages.add(DESC_SUFFIX);
				pages.add(NOTE_SUFFIX);
				for (int i = 0; i < 16; i++) {
					pages.add(":"+CrystalElement.elements[i].name().toLowerCase());
				}
				int i = 0;
				for (String s : pages) {
					String text = machines.getValueAtNode("machines:"+m.name().toLowerCase(Locale.ENGLISH)+s);
					boolean desc = s.equals(DESC_SUFFIX);
					boolean note = s.equals(NOTE_SUFFIX);
					if (desc)
						text = String.format(text, machineData.get(m));
					else if (note)
						text = String.format(text, machineNotes.get(m));
					if (XMLInterface.NULL_VALUE.equals(text))
						text = "There is no lexicon data for this machine yet.";
					if (m.isIncomplete()) {
						text += "\nThis machine is incomplete. Use at your own risk.";
					}
					if (desc)
						addEntry(h, text);
					else
						notes.put(text, h, i-1);
					i++;
				}
			}
			else {
				String desc = machines.getValueAtNode("machines:"+m.name().toLowerCase(Locale.ENGLISH)+DESC_SUFFIX);
				String aux = machines.getValueAtNode("machines:"+m.name().toLowerCase(Locale.ENGLISH)+NOTE_SUFFIX);
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
				if (m.isCrystalNetworkTile()) {
					CrystalNetworkTile te = (CrystalNetworkTile)m.createTEInstanceForRender(0);
					if (te instanceof CrystalReceiver) {
						aux += String.format("Lumen Reception Range: %d m\n", ((CrystalReceiver)te).getReceiveRange());
					}
					if (te instanceof CrystalTransmitter) {
						aux += String.format("Lumen Transmission Range: %d m\n", ((CrystalTransmitter)te).getSendRange());
					}
				}
				if (m.hasPrerequisite()) {
					String sg = m.getPrerequisite().getModLabel().replaceAll("[|]", "");
					aux += "\nDependencies: "+ReikaStringParser.capitalizeWords(ReikaStringParser.splitCamelCase(sg).replaceAll(" Craft", "Craft"));
				}
				if (m.isIncomplete()) {
					desc += "\nThis machine is incomplete. Use at your own risk.";
				}
				while(aux.startsWith("\n"))
					aux = aux.substring("\n".length());

				addEntry(h, desc);
				notes.put(aux, h, 0);
			}
		}

		for (ChromaResearch h : blocktabs) {
			String desc = blocks.getValueAtNode("blocks:"+h.name().toLowerCase(Locale.ENGLISH));
			desc = String.format(desc, blockData.get(h.getBlock()));
			addEntry(h, desc);
		}

		for (ChromaResearch h : tooltabs) {
			String desc = tools.getValueAtNode("tools:"+h.name().toLowerCase(Locale.ENGLISH));
			desc = String.format(desc, itemData.get(h.getItem()));
			if (h.getItem().getItemInstance() instanceof ItemWandBase) {
				notes.put(((ItemWandBase)h.getItem().getItemInstance()).generateUsageData(), h, 0);
			}
			addEntry(h, desc);
		}

		for (ChromaResearch h : resourcetabs) {
			String desc = resources.getValueAtNode("resource:"+h.name().toLowerCase(Locale.ENGLISH));
			addEntry(h, desc);
		}

		for (ChromaResearch h : structuretabs) {
			String desc = structures.getValueAtNode("structure:"+h.name().toLowerCase(Locale.ENGLISH));
			addEntry(h, desc);
		}

		for (ChromaResearch h : infotabs) {
			String desc = infos.getValueAtNode("info:"+h.name().toLowerCase(Locale.ENGLISH));
			desc = String.format(desc, miscData.get(h));
			addEntry(h, desc);
		}

		for (ChromaResearch h : abilitytabs) {
			Chromabilities a = h.getAbility();
			String desc = abilities.getValueAtNode("ability:"+a.name().toLowerCase(Locale.ENGLISH));
			desc = String.format(desc, abilityData.get(a));
			abilityText.put(a, desc);
		}

		for (CrystalElement e : CrystalElement.elements) {
			String desc = elements.getValueAtNode("elements:"+e.name().toLowerCase(Locale.ENGLISH));
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
			String title = progress.getValueAtNode("progression:"+p.name().toLowerCase(Locale.ENGLISH)+":title");
			String hint = progress.getValueAtNode("progression:"+p.name().toLowerCase(Locale.ENGLISH)+":hint");
			String reveal = progress.getValueAtNode("progression:"+p.name().toLowerCase(Locale.ENGLISH)+":reveal");
			String desc = progress.getValueAtNode("progression:"+p.name().toLowerCase(Locale.ENGLISH)+":desc");
			if (Strings.isNullOrEmpty(desc))
				desc = reveal;
			progressText.put(p, new ProgressNote(title.replaceAll("\\n", ""), hint.replaceAll("\\n", ""), reveal.replaceAll("\\n", ""), desc.replaceAll("\\n", "")));
		}

		for (int i = 0; i < ChromaEnchants.enchantmentList.length; i++) {
			ChromaEnchants e = ChromaEnchants.enchantmentList[i];
			String desc = enchants.getValueAtNode("enchants:"+e.name().toLowerCase(Locale.ENGLISH));
			notes.put(desc, ChromaResearch.ENCHANTS, i+1);
		}
		String desc = enchants.getValueAtNode("enchants:boostedlevel");
		notes.put(desc, ChromaResearch.ENCHANTS, ChromaEnchants.enchantmentList.length+1);

		for (int i = 0; i < ScriptLocations.list.length; i++) {
			ScriptLocations l = ScriptLocations.list[i];
			l.reload();
			String pre = "lore:"+l.name().toLowerCase(Locale.ENGLISH);
			//String s = lore.getValueAtNode(pre);
			Collection<String> li = lore.getNodesWithin(pre);
			for (String s : li) {
				l.loadText(lore.getValueAtNode(s));
				//loreText.addValue(l, lore.getValueAtNode(s));
			}
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

	public static String getNotes(ChromaResearch h, int page) {
		if (!notes.containsKeyV(h, page))
			return "";
		return notes.get(h, page);
	}

	static {
		loadNumericalData();
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			registerReloadListener();
	}

	@SideOnly(Side.CLIENT)
	public static final class ReloadListener {

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void reload(ResourceReloadEvent evt) {
			ChromaDescriptions.reload();
		}

	}

	@SideOnly(Side.CLIENT)
	private static void registerReloadListener() {
		MinecraftForge.EVENT_BUS.register(new ReloadListener());
	}

	private static void loadNumericalData() {
		addData(ChromaTiles.WEAKREPEATER, TileEntityCrystalPylon.RANGE);
		addData(ChromaTiles.LASER, TileEntityCrystalLaser.MAX_RANGE);
		addData(ChromaTiles.BEACON, CrystalElement.RED.displayName);
		addData(ChromaTiles.LIGHTER, CrystalElement.BLUE.displayName);

		addNotes(ChromaTiles.ADJACENCY, TileEntityAdjacencyUpgrade.MAX_TIER);
		addNotes(ChromaTiles.ENCHANTER, TileEntityAutoEnchanter.CHROMA_PER_LEVEL_BASE);
		addNotes(ChromaTiles.GUARDIAN, TileEntityGuardianStone.RANGE);
		//addNotes(ChromaTiles.TELEPUMP, TileEntityTeleportationPump.getRequiredEnergy().toDisplay());
		//addNotes(ChromaTiles.MINER, TileEntityMiner.getRequiredEnergy().toDisplay());

		//addNotes(ChromaTiles.REPROGRAMMER, TileEntitySpawnerReprogrammer.getRequiredEnergy().toDisplay());
		addNotes(ChromaTiles.TANK, TileEntityCrystalTank.FACTOR/1000, TileEntityCrystalTank.MAXCAPACITY/1000);
		addNotes(ChromaTiles.CHARGER, TileEntityCrystalCharger.CAPACITY);
		//addNotes(ChromaTiles.TICKER, TileEntityInventoryTicker.getRequiredEnergy().toDisplay());
		addNotes(ChromaTiles.FURNACE, TileEntityCrystalFurnace.MULTIPLY);
		addNotes(ChromaTiles.FABRICATOR, FabricationRecipes.FACTOR, FabricationRecipes.INITFACTOR, FabricationRecipes.POWER);
		addNotes(ChromaTiles.BEACON, TileEntityCrystalBeacon.RATIO, TileEntityCrystalBeacon.POWER, TileEntityCrystalBeacon.MAXRANGE);
		addNotes(ChromaTiles.COLLECTOR, TileEntityCollector.XP_PER_CHROMA);
		addNotes(ChromaTiles.ITEMCOLLECTOR, TileEntityItemCollector.MAXRANGE);
		addNotes(ChromaTiles.LAMP, TileEntityChromaLamp.FACTOR);
		addNotes(ChromaTiles.POWERTREE, TileEntityPowerTree.BASE, TileEntityPowerTree.RATIO, TileEntityPowerTree.POWER);
		addNotes(ChromaTiles.LAMPCONTROL, TileEntityLampController.MAXRANGE, TileEntityLampController.MAXCHANNEL);
		addNotes(ChromaTiles.ASPECTJAR, TileEntityAspectJar.CAPACITY_PRIMAL, TileEntityAspectJar.CAPACITY);
		addNotes(ChromaTiles.WIRELESS, ChromaStructures.WIRELESSPEDESTAL.getDisplayName(), ChromaTiles.WIRELESS.getName());
		if (ModList.FORESTRY.isLoaded())
			addNotes(ChromaTiles.ALVEARY, TileEntityLumenAlveary.getEffectsAsString());

		addData(ChromaBlocks.RELAY, RelayNetworker.instance.maxRange);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (e == CrystalElement.LIGHTGRAY)
				addData(e, e.displayName, CrystalElement.WHITE.displayName);
			else
				addData(e, e.displayName);
		}

		addData(ChromaItems.SHARE, ChromaTiles.TABLE.getName(), ChromaTiles.RITUAL.getName());

		addData(Chromabilities.REACH, new Object[]{Chromabilities.MAX_REACH});
		addData(Chromabilities.LIFEPOINT, new Object[]{CrystalElement.MAGENTA.displayName});

		miscData.put(ChromaResearch.ENCHANTS, new Object[]{ChromaTiles.ENCHANTER.getName()});
	}

	public static String getParentPage() {
		return PARENT;
	}

	public static ProgressNote getProgressText(ProgressStage p) {
		return progressText.containsKey(p) ? progressText.get(p) : new ProgressNote("#NULL", "#NULL", "#NULL", "#NULL");
	}
	/*
	public static Collection<String> getScriptTexts(ScriptLocations s) {
		return Collections.unmodifiableCollection(loreText.get(s));
	}
	 */
	public static boolean isUnfilled(String s) {
		return s == null || s.isEmpty() || s.endsWith(XMLInterface.NULL_VALUE);
	}

	public static class ProgressNote {

		public final String title;
		public final String hint;
		public final String reveal;
		public final String desc;

		private ProgressNote(String t, String h, String rvl, String desc) {
			title = t;
			hint = h;
			reveal = rvl;
			this.desc = desc;
		}

	}
}
