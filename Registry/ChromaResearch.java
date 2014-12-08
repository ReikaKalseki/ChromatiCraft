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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.DragonAPI.Instantiable.Data.MultiMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public enum ChromaResearch {

	//---------------------INFO--------------------//
	INTRO("Introduction", ""),
	ELEMENTS("Crystal Energy", 			ChromaItems.ELEMENTAL.getStackOf(CrystalElement.BLUE),	ResearchLevel.BASICCRAFT),
	CRYSTALS("Crystals", 				ChromaBlocks.CRYSTAL.getStackOf(4), 					ResearchLevel.ENTRY, 		ProgressStage.CRYSTALS),
	PYLONS("Pylons", 					ChromaTiles.PYLON.getCraftedProduct(), 					ResearchLevel.ENTRY, 		ProgressStage.PYLON),
	TRANSMISSION("Signal Transmission", ChromaStacks.beaconDust, 								ResearchLevel.ENERGYEXPLORE),

	MACHINEDESC("Constructs", ""),
	REPEATER(		ChromaTiles.REPEATER,		ResearchLevel.NETWORKING),
	GUARDIAN(		ChromaTiles.GUARDIAN, 		ResearchLevel.MULTICRAFT),
	//LIQUIFIER(		ChromaTiles.LIQUIFIER, 		ResearchLevel.RUNECRAFT),
	REPROGRAMMER(	ChromaTiles.REPROGRAMMER, 	ResearchLevel.MULTICRAFT),
	ACCEL(			ChromaTiles.ACCELERATOR, 	ResearchLevel.ENDGAME),
	RIFT(			ChromaTiles.RIFT, 			ResearchLevel.PYLONCRAFT),
	TANK(			ChromaTiles.TANK, 			ResearchLevel.MULTICRAFT),
	COMPOUND(		ChromaTiles.COMPOUND, 		ResearchLevel.NETWORKING),
	CHARGER(		ChromaTiles.CHARGER, 		ResearchLevel.PYLONCRAFT),
	LILY(			ChromaTiles.HEATLILY, 		ResearchLevel.RUNECRAFT),
	TICKER(			ChromaTiles.TICKER, 		ResearchLevel.MULTICRAFT),
	FENCE(			ChromaTiles.FENCE, 			ResearchLevel.MULTICRAFT),
	FURNACE(		ChromaTiles.FURNACE, 		ResearchLevel.MULTICRAFT),
	TELEPUMP(		ChromaTiles.TELEPUMP, 		ResearchLevel.MULTICRAFT),
	MINER(			ChromaTiles.MINER, 			ResearchLevel.PYLONCRAFT),
	LASER(			ChromaTiles.LASER, 			ResearchLevel.PYLONCRAFT),
	ITEMRIFT(		ChromaTiles.ITEMRIFT, 		ResearchLevel.MULTICRAFT),
	CRYSTAL(		ChromaTiles.CRYSTAL, 		ResearchLevel.PYLONCRAFT),
	INFUSER(		ChromaTiles.INFUSER, 		ResearchLevel.RUNECRAFT),
	FABRICATOR(		ChromaTiles.FABRICATOR, 	ResearchLevel.PYLONCRAFT),
	ENCHANTER(		ChromaTiles.ENCHANTER, 		ResearchLevel.MULTICRAFT),
	CHROMAFLOWER(	ChromaTiles.CHROMAFLOWER, 	ResearchLevel.RUNECRAFT),
	COLLECTOR(		ChromaTiles.COLLECTOR, 		ResearchLevel.ENERGYEXPLORE),
	BREWER(			ChromaTiles.BREWER, 		ResearchLevel.BASICCRAFT),
	RITUALTABLE(	ChromaTiles.RITUAL, 		ResearchLevel.ENERGYEXPLORE),
	CASTTABLE(		ChromaTiles.TABLE, 			ResearchLevel.ENTRY),
	BEACON(			ChromaTiles.BEACON, 		ResearchLevel.ENDGAME),
	ITEMCOLLECTOR(	ChromaTiles.ITEMCOLLECTOR, 	ResearchLevel.MULTICRAFT),
	AISHUTDOWN(		ChromaTiles.AISHUTDOWN, 	ResearchLevel.MULTICRAFT),
	ASPECT(			ChromaTiles.ASPECT, 		ResearchLevel.ENERGYEXPLORE),
	LAMP(			ChromaTiles.LAMP, 			ResearchLevel.ENERGYEXPLORE),
	POWERTREE(		ChromaTiles.POWERTREE, 		ResearchLevel.PYLONCRAFT),
	LAMPCONTROL(	ChromaTiles.LAMPCONTROL, 	ResearchLevel.BASICCRAFT),

	BLOCKS("Other Blocks", ""),
	TNT(			ChromaBlocks.TNT,			ResearchLevel.PYLONCRAFT),
	TANKAUX(		ChromaBlocks.TANK,			ResearchLevel.MULTICRAFT),
	FENCEAUX(		ChromaBlocks.FENCE,			ResearchLevel.MULTICRAFT),
	LUMENLEAVES(	ChromaBlocks.POWERTREE,		ResearchLevel.PYLONCRAFT),
	DYELEAVES(		ChromaBlocks.DYELEAF,		ResearchLevel.ENTRY),
	RAINBOWLEAVES(	ChromaBlocks.RAINBOWLEAF,	ResearchLevel.RAWEXPLORE),

	TOOLDESC("Tools", ""),
	FINDER(			ChromaItems.FINDER, 		ResearchLevel.RAWEXPLORE,	ProgressStage.PYLON),
	EXCAVATOR(		ChromaItems.EXCAVATOR, 		ResearchLevel.CHARGESELF),
	TRANSITION(		ChromaItems.TRANSITION, 	ResearchLevel.CHARGESELF),
	INVLINK(		ChromaItems.LINK, 			ResearchLevel.ENERGYEXPLORE),
	PENDANT(		ChromaItems.PENDANT, 		ResearchLevel.ENERGYEXPLORE),
	VACUUMGUN(		ChromaItems.VACUUMGUN, 		ResearchLevel.MULTICRAFT),
	LENS(			ChromaItems.LENS, 			ResearchLevel.BASICCRAFT),
	STORAGE(		ChromaItems.STORAGE, 		ResearchLevel.ENERGYEXPLORE),
	LINKTOOL(		ChromaItems.LINKTOOL, 		ResearchLevel.MULTICRAFT),
	WARP(			ChromaItems.WARP, 			ResearchLevel.ENERGYEXPLORE),
	FRAGMENT(		ChromaItems.FRAGMENT, 		ResearchLevel.ENTRY),

	RESOURCEDESC("Resources", ""),
	SHARDS("Shards",				ChromaStacks.redShard, 								ResearchLevel.RAWEXPLORE,	ProgressStage.CRYSTALS),
	DUSTS("Dusts",					ChromaStacks.auraDust, 								ResearchLevel.ENERGYEXPLORE),
	GROUPS("Groups",				ChromaStacks.crystalCore, 							ResearchLevel.BASICCRAFT),
	BINDING("Ores",					ChromaStacks.bindingCrystal,						ResearchLevel.RUNECRAFT),
	CRYSTALSTONE("Crystal Stone",	ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 		ResearchLevel.BASICCRAFT),
	SEED("Crystal Seeds",			ChromaItems.SEED.getStackOf(CrystalElement.RED),	ResearchLevel.RAWEXPLORE,	ProgressStage.CRYSTALS),

	ABILITYDESC("Abilities", ""),
	REACH(			Chromabilities.REACH),
	MAGNET(			Chromabilities.MAGNET),
	SONIC(			Chromabilities.SONIC),
	SHIFT(			Chromabilities.SHIFT),
	HEAL(			Chromabilities.HEAL),
	SHIELD(			Chromabilities.SHIELD),
	FIREBALL(		Chromabilities.FIREBALL),
	COMMUNICATE(	Chromabilities.COMMUNICATE),
	HEALTH(			Chromabilities.HEALTH),
	PYLONPROTECT(	Chromabilities.PYLON),

	STRUCTUREDESC("Structures", ""),
	PYLON(			ChromaStructures.Structures.PYLON,		5,	ResearchLevel.ENERGYEXPLORE,	ProgressStage.PYLON),
	CASTING1(		ChromaStructures.Structures.CASTING1,	0,	ResearchLevel.BASICCRAFT,		ProgressStage.CRYSTALS),
	CASTING2(		ChromaStructures.Structures.CASTING2,	1,	ResearchLevel.RUNECRAFT,		ProgressStage.RUNEUSE),
	CASTING3(		ChromaStructures.Structures.CASTING3,	2,	ResearchLevel.NETWORKING,		ProgressStage.MULTIBLOCK),
	RITUAL	(		ChromaStructures.Structures.RITUAL,		7,	ResearchLevel.CHARGESELF,		ProgressStage.CHARGE),
	INFUSION(		ChromaStructures.Structures.INFUSION,	12,	ResearchLevel.MULTICRAFT,		ProgressStage.CHROMA),
	TREE(			ChromaStructures.Structures.TREE,		14,	ResearchLevel.PYLONCRAFT,		ProgressStage.LINK),
	REPEATERSTRUCT(	ChromaStructures.Structures.REPEATER,	0,	ResearchLevel.ENERGYEXPLORE,	ProgressStage.RUNEUSE),
	COMPOUNDSTRUCT(	ChromaStructures.Structures.COMPOUND,	13,	ResearchLevel.NETWORKING,		ProgressStage.MULTIBLOCK),
	;

	private final ItemStack iconItem;
	private final String pageTitle;
	private boolean isParent = false;
	private ChromaTiles machine;
	private ChromaBlocks block;
	private ChromaItems item;
	private final ProgressStage[] progress;
	public final ResearchLevel level;
	private Chromabilities ability;
	private Structures struct;

	public static final ChromaResearch[] researchList = values();
	static final MultiMap<ResearchLevel, ChromaResearch> levelMap = new MultiMap();
	private static final List<ChromaResearch> nonParents = new ArrayList();

	private ChromaResearch() {
		this("");
	}

	private ChromaResearch(ChromaTiles r, ResearchLevel rl, ProgressStage... p) {
		this(r.getName(), r.getCraftedProduct(), rl, p);
		machine = r;
	}

	private ChromaResearch(ChromaBlocks r, ResearchLevel rl, ProgressStage... p) {
		this(r.getBasicName(), r.getStackOf(), rl, p);
		block = r;
	}

	private ChromaResearch(ChromaBlocks r, int meta, ResearchLevel rl, ProgressStage... p) {
		this(r.getBasicName(), r.getStackOf(meta), rl, p);
		block = r;
	}

	private ChromaResearch(ChromaItems i, ResearchLevel rl, ProgressStage... p) {
		this(i.getBasicName(), i.getStackOf(), rl, p);
		item = i;
	}

	private ChromaResearch(String name, String s) {
		this(name);
		isParent = true;
	}

	private ChromaResearch(String name) {
		this(name, (ItemStack)null, null);
	}

	private ChromaResearch(String name, ChromaItems i, ResearchLevel rl, ProgressStage... p) {
		this(name, i.getStackOf(), rl, p);
	}

	private ChromaResearch(String name, ChromaTiles r, ResearchLevel rl, ProgressStage... p) {
		this(name, r.getCraftedProduct(), rl, p);
	}

	private ChromaResearch(String name, Item icon, ResearchLevel rl, ProgressStage... p) {
		this(name, new ItemStack(icon), rl, p);
	}

	private ChromaResearch(String name, Block icon, ResearchLevel rl, ProgressStage... p) {
		this(name, new ItemStack(icon), rl, p);
	}

	private ChromaResearch(String name, ItemStack icon, ResearchLevel rl, ProgressStage... p) {
		iconItem = icon;
		pageTitle = name;
		progress = p;
		level = rl;
	}

	private ChromaResearch(Chromabilities c, ProgressStage... p) {
		iconItem = ChromaTiles.RITUAL.getCraftedProduct();
		pageTitle = c.getDisplayName();
		progress = p;
		level = ResearchLevel.PYLONCRAFT;
		ability = c;
	}

	private ChromaResearch(Structures s, int meta, ResearchLevel r, ProgressStage... p) {
		iconItem = new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 1, meta);
		pageTitle = s.getDisplayName();
		progress = p;
		level = r;
		struct = s;
	}

	public boolean playerCanSee(EntityPlayer ep) {
		if (progress != null) {
			for (int i = 0; i < progress.length; i++) {
				ProgressStage p = progress[i];
				if (!ProgressionManager.instance.isPlayerAtStage(ep, p))
					return false;
			}
		}
		return ChromaResearchManager.instance.playerHasFragment(ep, this);
	}

	public boolean playerCanRead(EntityPlayer ep) {
		return this.playerCanSee(ep) || ChromaResearchManager.instance.canPlayerStepTo(ep, this);
	}

	public boolean isMachine() {
		return machine != null;
	}

	public Chromabilities getAbility() {
		return ability;
	}

	public Structures getStructure() {
		return struct;
	}

	public ChromaTiles getMachine() {
		return machine;
	}

	public ChromaBlocks getBlock() {
		return block;
	}

	public ChromaItems getItem() {
		return item;
	}

	public ItemStack getTabIcon() {
		return iconItem;
	}

	public String getData() {
		return ChromaDescriptions.getData(this);
	}

	public String getNotes() {
		return ChromaDescriptions.getNotes(this);
	}

	public boolean sameTextAllSubpages() {
		return false;
	}

	public boolean isAbility() {
		if (isParent)
			return false;
		return this.getParent() == ABILITYDESC;
	}

	public boolean isCrafting() {
		if (isParent)
			return false;
		if (this.isMachine() || this.isTool())
			return true;
		if (this == GROUPS)
			return true;
		if (this == CRYSTALSTONE)
			return true;
		return false;
	}

	public boolean isTool() {
		return this.getParent() == TOOLDESC;
	}

	private ArrayList<ItemStack> getItemStacks() {
		if (this.isMachine())
			return ReikaJavaLibrary.makeListFrom(machine.getCraftedProduct());
		if (this == STORAGE) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < ChromaItems.STORAGE.getNumberMetadatas(); i++) {
				li.add(ChromaItems.STORAGE.getStackOfMetadata(i));
			}
			return li;
		}
		if (this == PENDANT) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < 16; i++) {
				li.add(ChromaItems.PENDANT.getStackOfMetadata(i));
				li.add(ChromaItems.PENDANT3.getStackOfMetadata(i));
			}
			return li;
		}
		if (item != null) {
			return ReikaJavaLibrary.makeListFrom(item.getStackOf());
		}
		if (this == GROUPS) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < 13; i++) {
				li.add(ChromaItems.CLUSTER.getStackOfMetadata(i));
			}
			return li;
		}
		if (this == CRYSTALSTONE) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < ChromaBlocks.PYLONSTRUCT.getNumberMetadatas(); i++) {
				li.add(new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 1, i));
			}
			return li;
		}
		return null;
	}

	public int getRecipeCount() {
		return this.getCraftingRecipes().size();
	}

	public boolean isCraftable() {
		return !this.isConfigDisabled() && this.isCrafting() ? this.getRecipeCount() > 0  : false;
	}

	public ArrayList<CastingRecipe> getCraftingRecipes() {
		if (!this.isCrafting())
			return new ArrayList();
		ArrayList<ItemStack> li = this.getItemStacks();
		if (li == null || li.isEmpty())
			return new ArrayList();
		ArrayList<CastingRecipe> rec = new ArrayList();
		for (ItemStack is : li) {
			rec.addAll(RecipesCastingTable.instance.getAllRecipesMaking(is));
		}
		return rec;
	}

	public RecipeType getRecipeLevel(int recipe) {
		ArrayList<CastingRecipe> li = this.getCraftingRecipes();
		if (li.isEmpty())
			return null;
		return li.get(recipe).type;
	}

	public String getTitle() {
		return pageTitle;
	}

	public boolean hasSubpages() {
		return this.isCrafting();
	}

	public ChromaResearch getParent() {
		ChromaResearch parent = null;
		for (int i = 0; i < researchList.length; i++) {
			if (researchList[i].isParent) {
				if (this.ordinal() >= researchList[i].ordinal()) {
					parent = researchList[i];
				}
			}
		}
		//ReikaJavaLibrary.pConsole("Setting parent for "+this+" to "+parent);
		return parent;
	}

	public boolean isParent() {
		return isParent;
	}

	public int getNumberChildren() {
		if (!isParent)
			return 0;
		int ch = 0;
		for (int i = this.ordinal()+1; i < researchList.length; i++) {
			ChromaResearch h = researchList[i];
			if (h.isParent) {
				return ch;
			}
			else {
				ch++;
			}
		}
		return ch;
	}

	public boolean isConfigDisabled() {
		return false;
	}

	static {
		for (int i = 0; i < ChromaResearch.researchList.length; i++) {
			ChromaResearch r = ChromaResearch.researchList[i];
			if (r.level != null)
				levelMap.addValue(r.level, r);
			if (!r.isParent)
				nonParents.add(r);
		}
	}

	public static ArrayList<ChromaResearch> getInfoTabs() {
		return getAllUnder(INTRO);
	}

	public static ArrayList<ChromaResearch> getMachineTabs() {
		return getAllUnder(MACHINEDESC);
	}

	public static ArrayList<ChromaResearch> getBlockTabs() {
		return getAllUnder(BLOCKS);
	}

	public static ArrayList<ChromaResearch> getAbilityTabs() {
		return getAllUnder(ABILITYDESC);
	}

	public static ArrayList<ChromaResearch> getToolTabs() {
		return getAllUnder(TOOLDESC);
	}

	public static ArrayList<ChromaResearch> getResourceTabs() {
		return getAllUnder(RESOURCEDESC);
	}

	public static ArrayList<ChromaResearch> getStructureTabs() {
		return getAllUnder(STRUCTUREDESC);
	}

	private static ArrayList<ChromaResearch> getAllUnder(ChromaResearch parent) {
		ArrayList<ChromaResearch> li = new ArrayList();
		for (int i = parent.ordinal()+1; i < researchList.length; i++) {
			ChromaResearch r = researchList[i];
			if (r.getParent() == parent)
				li.add(r);
			else
				break;
		}
		return li;
	}

	public static List<ChromaResearch> getAllNonParents() {
		return Collections.unmodifiableList(nonParents);
	}

}
