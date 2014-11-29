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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaBookData;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.DragonAPI.Instantiable.Data.MultiMap;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public enum ChromaResearch {

	//---------------------TOC--------------------//
	/*
	TOC("Table Of Contents", ""),
	INFO("Info", ChromaItems.HELP.getStackOf()),
	CRAFTING("Crafting", ChromaTiles.TABLE.getCraftedProduct()),
	MACHINES("Constructs", ChromaTiles.RIFT.getCraftedProduct()),
	TOOLS("Tools", ChromaItems.FINDER.getStackOf()),
	ABILITY("Rituals", ChromaTiles.RITUAL.getCraftedProduct()),
	RESOURCE("Resources", ChromaItems.BERRY.getStackOf()),*/
	//---------------------INFO--------------------//
	INTRO("Introduction", ""),
	ELEMENTS("Crystal Energy", ChromaItems.ELEMENTAL.getStackOfElement(CrystalElement.BLUE),ResearchLevel.BASICCRAFT),
	CRYSTALS("Crystals", new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, 4), 		ResearchLevel.ENTRY, 		ProgressStage.CRYSTALS),
	PYLONS("Pylons", ChromaTiles.PYLON.getCraftedProduct(), 								ResearchLevel.ENTRY, 		ProgressStage.PYLON),
	TRANSMISSION("Signal Transmission", ChromaStacks.beaconDust, 							ResearchLevel.ENERGYEXPLORE),

	MACHINESDESC("Constructs", ""),
	REPEATER(		ChromaTiles.REPEATER,		ResearchLevel.NETWORKING),
	GUARDIAN(		ChromaTiles.GUARDIAN, 		ResearchLevel.MULTICRAFT),
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

	TOOLDESC("Tools", ""),
	FINDER(			ChromaItems.FINDER, 		ResearchLevel.RAWEXPLORE,	ProgressStage.PYLON),
	EXCAVATOR(		ChromaItems.EXCAVATOR, 		ResearchLevel.CHARGESELF),
	TRANSITION(		ChromaItems.TRANSITION, 	ResearchLevel.CHARGESELF),

	RESOURCEDESC("Resources", ""),
	SHARDS("Shards",				ChromaStacks.redShard, 							ResearchLevel.RAWEXPLORE,	ProgressStage.CRYSTALS),
	DUSTS("Dusts",					ChromaStacks.auraDust, 							ResearchLevel.ENERGYEXPLORE),
	GROUPS("Groups",				ChromaStacks.crystalCore, 						ResearchLevel.BASICCRAFT),
	BINDING("Ores",					ChromaStacks.bindingCrystal,					ResearchLevel.RUNECRAFT),
	CRYSTALSTONE("Crystal Stone",	ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 	ResearchLevel.BASICCRAFT),//**
	;

	private final ItemStack iconItem;
	private final String pageTitle;
	private boolean isParent = false;
	private ChromaTiles machine;
	private ChromaItems item;
	private final ProgressStage[] progress;
	public final ResearchLevel level;

	public static final ChromaResearch[] researchList = values();
	static final MultiMap<ResearchLevel, ChromaResearch> levelMap = new MultiMap();

	private ChromaResearch() {
		this("");
	}

	private ChromaResearch(ChromaTiles r, ResearchLevel rl, ProgressStage... p) {
		this(r.getName(), r.getCraftedProduct(), rl, p);
		machine = r;
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

	public static ChromaResearch getEntry(int screen, int page) {
		//ReikaJavaLibrary.pConsole(screen+"   "+page);
		//if (screen < INTRO.getScreen())
		//	return TOC;
		ChromaResearch h = ChromaBookData.getMapping(screen, page);
		return h != null ? h : null;//TOC;
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

	public static void addRelevantButtons(int j, int k, int screen, List<GuiButton> li) {
		int id = 0;
		for (int i = 0; i < researchList.length; i++) {
			if (researchList[i].getScreen() == screen && researchList[i].playerCanSee(Minecraft.getMinecraft().thePlayer)) {
				li.add(new ImagedGuiButton(id, j-20, k+id*20-8, 20, 20, 0, 0, researchList[i].getTabImageFile(), ChromatiCraft.class));
				//ReikaJavaLibrary.pConsole("Adding "+tabList[i]+" with ID "+id+" to screen "+screen);
				id++;
			}
		}
	}

	public static List<ChromaResearch> getEntriesForScreen(int screen) {
		//ReikaJavaLibrary.pConsole(screen);
		List<ChromaResearch> li = new ArrayList<ChromaResearch>();
		for (int i = 0; i < researchList.length; i++) {
			if (researchList[i].getScreen() == screen) {
				li.add(researchList[i]);
			}
		}
		return li;
	}
	/*
	public static List<ChromaBook> getTOCTabs() {
		ArrayList<ChromaBook> li = new ArrayList<ChromaBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent && tabList[i] != TOC)
				li.add(tabList[i]);
		}
		return li;
	}
	 */
	public static List<ChromaResearch> getMachineTabs() {
		List<ChromaResearch> tabs = new ArrayList<ChromaResearch>();
		for (int i = 0; i < researchList.length; i++) {
			ChromaResearch h = researchList[i];
			if (h.isMachine() && !h.isParent)
				tabs.add(h);
		}
		return tabs;
	}

	public static ChromaResearch[] getResourceTabs() {
		int size = researchList.length-RESOURCEDESC.ordinal()-1;
		ChromaResearch[] tabs = new ChromaResearch[size];
		System.arraycopy(researchList, RESOURCEDESC.ordinal()+1, tabs, 0, size);
		return tabs;
	}
	/*
	public static List<ChromaBook> getCategoryTabs() {
		ArrayList<ChromaBook> li = new ArrayList<ChromaBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent && tabList[i] != TOC)
				li.add(tabList[i]);
		}
		return li;
	}
	 */
	public boolean isMachine() {
		return machine != null;
	}

	public ChromaTiles getMachine() {
		return machine;
	}

	public ChromaItems getItem() {
		return item;
	}

	public ItemStack getTabIcon() {
		return iconItem;
	}

	public String getData() {
		//if (this == TOC)
		//	return ChromaDescriptions.getTOC();
		return ChromaDescriptions.getData(this);
	}

	public String getNotes() {
		return ChromaDescriptions.getNotes(this);
	}

	public boolean sameTextAllSubpages() {
		return false;
	}

	public boolean isCrafting() {
		if (isParent)
			return false;
		if (this.isSmelting())
			return false;
		if (this.isMachine() || this.isTool())
			return true;
		if (this == GROUPS)
			return true;
		//if (this.getParent() == TOC || this.getParent() == TERMS)
		//	return false;
		//if (this == MODINTERFACE)
		//	return false;
		return false;
	}

	public boolean isSmelting() {
		return false;
	}

	public boolean isTool() {
		return this.getParent() == TOOLDESC;
	}

	private ArrayList<ItemStack> getItemStacks() {
		if (this.isMachine())
			return ReikaJavaLibrary.makeListFrom(machine.getCraftedProduct());
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
		return null;
	}

	public int getRecipeCount() {
		return this.getCrafting().size();
	}

	public ArrayList<CastingRecipe> getCrafting() {
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
		ArrayList<CastingRecipe> li = this.getCrafting();
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

	public static String getTabImageFile() {
		return "Textures/GUIs/Handbook/tabs.png";
	}

	public int getRelativeScreen() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset/8;
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

	public int getBaseScreen() {
		int sc = 0;
		for (int i = 0; i < this.ordinal(); i++) {
			ChromaResearch h = researchList[i];
			if (h.isParent) {
				sc += h.getNumberChildren()/8+1;
			}
		}
		return sc;
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

	public int getRelativePage() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset;
	}

	public int getRelativeTabPosn() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset-this.getRelativeScreen()*8;
	}

	public int getScreen() {
		return this.getParent().getBaseScreen()+this.getRelativeScreen();
	}

	public int getPage() {
		return (this.ordinal()-this.getParent().ordinal())%8;
	}

	public boolean isConfigDisabled() {
		return false;
	}

	static {
		for (int i = 0; i < ChromaResearch.researchList.length; i++) {
			ChromaResearch r = ChromaResearch.researchList[i];
			if (r.level != null)
				levelMap.addValue(r.level, r);
		}
	}

}
