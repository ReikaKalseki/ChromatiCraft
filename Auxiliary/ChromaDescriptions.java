/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public final class ChromaDescriptions {

	public static final String PARENT = getParent();
	public static final String DESC_SUFFIX = ":desc";
	public static final String NOTE_SUFFIX = ":note";

	private static HashMap<ChromaResearch, String> data = new HashMap<ChromaResearch, String>();
	private static HashMap<ChromaResearch, String> notes = new HashMap<ChromaResearch, String>();

	private static HashMap<ChromaTiles, Object[]> machineData = new HashMap<ChromaTiles, Object[]>();
	private static HashMap<ChromaTiles, Object[]> machineNotes = new HashMap<ChromaTiles, Object[]>();
	private static HashMap<ChromaResearch, Object[]> miscData = new HashMap<ChromaResearch, Object[]>();

	private static ArrayList<ChromaResearch> categories = new ArrayList<ChromaResearch>();

	private static final boolean mustLoad = !ReikaObfuscationHelper.isDeObfEnvironment();
	private static final XMLInterface parents = new XMLInterface(ChromatiCraft.class, PARENT+"categories.xml", mustLoad);
	private static final XMLInterface machines = new XMLInterface(ChromatiCraft.class, PARENT+"machines.xml", mustLoad);
	private static final XMLInterface abilities = new XMLInterface(ChromatiCraft.class, PARENT+"abilities.xml", mustLoad);
	private static final XMLInterface tools = new XMLInterface(ChromatiCraft.class, PARENT+"tools.xml", mustLoad);
	private static final XMLInterface resources = new XMLInterface(ChromatiCraft.class, PARENT+"resource.xml", mustLoad);
	private static final XMLInterface miscs = new XMLInterface(ChromatiCraft.class, PARENT+"misc.xml", mustLoad);
	private static final XMLInterface infos = new XMLInterface(ChromatiCraft.class, PARENT+"info.xml", mustLoad);
	private static final XMLInterface hover = new XMLInterface(ChromatiCraft.class, PARENT+"hover.xml", mustLoad);

	public static void addCategory(ChromaResearch h) {
		categories.add(h);
	}

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

	public static int getCategoryCount() {
		return categories.size();
	}
	/*
	public static String getTOC() {
		List<ChromaBook> toctabs = ChromaBook.getTOCTabs();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < toctabs.size(); i++) {
			ChromaBook h = toctabs.get(i);
			sb.append("Page ");
			sb.append(h.getScreen());
			sb.append(" - ");
			sb.append(h.getTitle());
			if (i < toctabs.size()-1)
				sb.append("\n");
		}
		return sb.toString();
	}
	 */
	public static String getHoverText(String key) {
		return hover.getValueAtNode(key);
	}

	private static void addData(ChromaTiles m, Object... data) {
		machineData.put(m, data);
	}

	private static void addData(ChromaResearch h, Object... data) {
		miscData.put(h, data);
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

	/** Call this from the SERVER side! */
	public static void reload() {
		loadNumericalData();

		machines.reread();
		abilities.reread();
		tools.reread();
		resources.reread();
		miscs.reread();
		infos.reread();
		hover.reread();

		parents.reread();

		loadData();
	}

	private static void addEntry(ChromaResearch h, String sg) {
		data.put(h, sg);
	}

	public static void loadData() {
		/*
		List<ChromaBook> parenttabs = ChromaBook.getCategoryTabs();

		List<ChromaBook> machinetabs = ChromaBook.getMachineTabs();
		ChromaBook[] abilitytabs = ChromaBook.getAbilityTabs();
		ChromaBook[] tooltabs = ChromaBook.getToolTabs();
		ChromaBook[] resourcetabs = ChromaBook.getResourceTabs();
		ChromaBook[] misctabs = ChromaBook.getMiscTabs();
		ChromaBook[] infotabs = ChromaBook.getInfoTabs();

		for (int i = 0; i < parenttabs.size(); i++) {
			ChromaBook h = parenttabs.get(i);
			String desc = parents.getValueAtNode("categories:"+h.name().toLowerCase().substring(0, h.name().length()-4));
			addEntry(h, desc);
		}

		for (int i = 0; i < machinetabs.size(); i++) {
			ChromaBook h = machinetabs.get(i);
			ChromaTiles m = h.getMachine();
			String desc = machines.getValueAtNode("machines:"+m.name().toLowerCase()+DESC_SUFFIX);
			String aux = machines.getValueAtNode("machines:"+m.name().toLowerCase()+NOTE_SUFFIX);
			desc = String.format(desc, machineData.get(m));
			aux = String.format(aux, machineNotes.get(m));

			if (XMLInterface.NULL_VALUE.equals(desc))
				desc = "There is no handbook data for this machine yet.";

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

		for (int i = 0; i < tooltabs.length; i++) {
			ChromaBook h = tooltabs[i];
			String desc = tools.getValueAtNode("tools:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (int i = 0; i < resourcetabs.length; i++) {
			ChromaBook h = resourcetabs[i];
			String desc = resources.getValueAtNode("resource:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (int i = 0; i < misctabs.length; i++) {
			ChromaBook h = misctabs[i];
			String desc = miscs.getValueAtNode("misc:"+h.name().toLowerCase());
			//ReikaJavaLibrary.pConsole(desc);
			desc = String.format(desc, miscData.get(h));
			addEntry(h, desc);
		}

		for (int i = 0; i < infotabs.length; i++) {
			ChromaBook h = infotabs[i];
			String desc = infos.getValueAtNode("info:"+h.name().toLowerCase());
			desc = String.format(desc, miscData.get(h));
			addEntry(h, desc);
		}*/

		for (int i = 0; i < Chromabilities.abilities.length; i++) {
			Chromabilities c = Chromabilities.abilities[i];
			String desc;
			String aux;
			desc = abilities.getValueAtNode("abilities:"+c.name().toLowerCase()+DESC_SUFFIX);
			aux = abilities.getValueAtNode("abilities:"+c.name().toLowerCase()+NOTE_SUFFIX);

			desc = String.format(desc);
			aux = String.format(aux);

			//data.put(h, desc);
			//notes.put(h, aux);
		}
	}

	public static String getAbilityDescription(Chromabilities c) {
		return abilities.getValueAtNode("abilities:"+c.name().toLowerCase());
	}

	public static String getData(ChromaResearch h) {
		if (!data.containsKey(h))
			return "This item has no handbook text yet.";
		return data.get(h);
	}

	public static String getNotes(ChromaResearch h) {
		if (!notes.containsKey(h))
			return "";
		return notes.get(h);
	}

	static {
		loadNumericalData();
	}

	private static void loadNumericalData() {

	}
}
