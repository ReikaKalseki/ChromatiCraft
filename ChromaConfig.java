/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.ArrayList;

import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Registry.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;

public class ChromaConfig extends ControlledConfig {

	private static final ArrayList<String> modTrees = getModTrees();
	private static final int treeLength = modTrees.size();
	private static final int vanillaTreeCount = ReikaTreeHelper.treeList.length;
	private final DataElement<Boolean>[] trees = new DataElement[treeLength+vanillaTreeCount];
	private DataElement<String[]> guardianExceptions;
	private Key superbuildKey;

	public ChromaConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id) {
		super(mod, option, id);

		for (int i = 0; i < vanillaTreeCount; i++) {
			String name = ReikaTreeHelper.treeList[i].getName();
			trees[i] = this.registerAdditionalOption("Generate Vanilla Logs", name, true);
		}
		for (int i = 0; i < treeLength; i++) {
			String name = modTrees.get(i);
			trees[i+vanillaTreeCount] = this.registerAdditionalOption("Generate Mod Logs", name, true);
		}

		guardianExceptions = this.registerAdditionalOption("Other Options", "Guardian Stone Exceptions", this.getDefaultGuardstoneExceptions());
	}

	@Override
	protected void afterInit() {
		superbuildKey = Key.readFromConfig(configMod, ChromaOptions.SUPERBUILDKEYBIND);
	}

	public Key getSuperbuildKey() {
		return superbuildKey;
	}

	private String[] getDefaultGuardstoneExceptions() {
		ArrayList<String> li = new ArrayList();
		for (Action a : Action.values())
			li.add("none#"+a.name());
		li.add(ModList.EXTRAUTILS.modLabel+":dark_portal:2#"+Action.RIGHT_CLICK_BLOCK.name()); //last millenium portal
		return li.toArray(new String[li.size()]);
	}

	public ArrayList<String> getGuardianExceptions() {
		return ReikaJavaLibrary.makeListFromArray(guardianExceptions.getData());
	}

	private static ArrayList<String> getModTrees() {
		ArrayList<String> base = ReikaJavaLibrary.getEnumEntriesWithoutInitializing(ModWoodList.class);
		ArrayList<String> li = new ArrayList();
		for (int i = 0; i < base.size(); i++) {
			StringBuilder sb = new StringBuilder();
			String sg = base.get(i);
			if (sg.startsWith("BOP")) {
				sg = sg.substring(3);
				sb.append("Biomes O Plenty ");
				sb.append(ReikaStringParser.capFirstChar(sg));
			}
			else if (sg.startsWith("BXL")) {
				sg = sg.substring(3);
				sb.append("ExtraBiomes XL ");
				sb.append(ReikaStringParser.capFirstChar(sg));
			}
			else if (sg.startsWith("MFR")) {
				sg = sg.substring(3);
				sb.append("MineFactory Reloaded ");
				sb.append(ReikaStringParser.capFirstChar(sg));
			}
			else if (sg.startsWith("IC2")) {
				sg = sg.substring(3);
				sb.append("IndustrialCraft ");
				sb.append(ReikaStringParser.capFirstChar(sg));
			}
			else if (sg.startsWith("NATURA")) {
				sg = sg.substring(6);
				sb.append("Natura ");
				sb.append(ReikaStringParser.capFirstChar(sg));
			}
			else {
				sb.append(ReikaStringParser.capFirstChar(sg));
			}
			li.add(sb.toString());
		}
		return li;
	}

	public boolean shouldGenerateLogType(ModWoodList tree) {
		return trees[tree.ordinal()+ReikaTreeHelper.treeList.length].getData();
	}

	public boolean shouldGenerateLogType(ReikaTreeHelper tree) {
		return trees[tree.ordinal()].getData();
	}

}
