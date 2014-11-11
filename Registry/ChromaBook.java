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
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Interfaces.HandbookEntry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public enum ChromaBook implements HandbookEntry {

	//---------------------TOC--------------------//
	TOC("Table Of Contents", ""),
	INFO("Info", ChromaItems.HELP.getStackOf()),
	CRAFTING("Crafting", ChromaTiles.TABLE.getCraftedProduct()),
	MACHINES("Constructs", ChromaTiles.RIFT.getCraftedProduct()),
	TOOLS("Tools", ChromaItems.FINDER.getStackOf()),
	ABILITY("Rituals", ChromaTiles.RITUAL.getCraftedProduct()),
	RESOURCE("Resources", ChromaItems.BERRY.getStackOf()),
	//---------------------INFO--------------------//
	INTRO("Introduction", ""),
	ELEMENTS("Crystal Energy", ChromaItems.ELEMENTAL.getStackOfMetadata(CrystalElement.BLUE.ordinal())),
	PYLONS("Pylons", ChromaTiles.PYLON.getCraftedProduct(), ProgressStage.PYLON),
	CRYSTALS("Crystals", ChromaBlocks.CRYSTAL.getBlockInstance(), ProgressStage.CRYSTALS),
	TRANSMISSION("Signal Transmission", ChromaStacks.beaconDust),

	MACHINESDESC("Machines", ""),
	REPEATER(ChromaTiles.REPEATER),
	GUARDIAN(ChromaTiles.GUARDIAN),
	REPROGRAMMER(ChromaTiles.REPROGRAMMER),
	ACCEL(ChromaTiles.ACCELERATOR),
	RIFT(ChromaTiles.RIFT),
	TANK(ChromaTiles.TANK),
	COMPOUND(ChromaTiles.COMPOUND),
	CHARGER(ChromaTiles.CHARGER),
	LILY(ChromaTiles.HEATLILY),
	TICKER(ChromaTiles.TICKER),
	FENCE(ChromaTiles.FENCE),
	FURNACE(ChromaTiles.FURNACE),
	TELEPUMP(ChromaTiles.TELEPUMP),
	MINER(ChromaTiles.MINER),
	LASER(ChromaTiles.LASER),
	ITEMRIFT(ChromaTiles.ITEMRIFT),
	CRYSTAL(ChromaTiles.CRYSTAL),
	INFUSER(ChromaTiles.INFUSER),
	FABRICATOR(ChromaTiles.FABRICATOR),

	TOOLDESC("Tools", ""),
	FINDER(ChromaItems.FINDER),
	EXCAVATOR(ChromaItems.EXCAVATOR),
	TRANSITION(ChromaItems.TRANSITION),

	RESOURCEDESC("Resources", ""),
	SHARDS(ChromaStacks.redShard, ProgressStage.CRYSTALS),
	DUSTS(ChromaStacks.auraDust),
	GROUPS(ChromaStacks.crystalCore),
	BINDING(ChromaStacks.bindingCrystal), //**
	;

	private final ItemStack iconItem;
	private final String pageTitle;
	private boolean isParent = false;
	private ChromaTiles machine;
	private ChromaItems item;
	private final ProgressStage[] progress;

	public static final ChromaBook[] tabList = values();

	private ChromaBook() {
		this("");
	}

	private ChromaBook(ChromaTiles r, ProgressStage... p) {
		this(r.getName(), r.getCraftedProduct(), p);
		machine = r;
	}

	private ChromaBook(ChromaItems i, ProgressStage... p) {
		this(i.getBasicName(), i.getStackOf(), p);
		item = i;
	}


	private ChromaBook(ItemStack item, ProgressStage... p) {
		this("", item, p);
	}

	private ChromaBook(String name, String s) {
		this(name);
		isParent = true;
	}

	private ChromaBook(String name, ProgressStage... p) {
		this(name, (ItemStack)null, p);
	}

	private ChromaBook(String name, ChromaItems i, ProgressStage... p) {
		this(name, i.getStackOf(), p);
	}

	private ChromaBook(String name, ChromaTiles r, ProgressStage... p) {
		this(name, r.getCraftedProduct(), p);
	}

	private ChromaBook(String name, Item icon, ProgressStage... p) {
		this(name, new ItemStack(icon), p);
	}

	private ChromaBook(String name, Block icon, ProgressStage... p) {
		this(name, new ItemStack(icon), p);
	}

	private ChromaBook(String name, ItemStack icon, ProgressStage... p) {
		iconItem = icon;
		pageTitle = name;
		progress = p;
	}

	public static ChromaBook getEntry(int screen, int page) {
		//ReikaJavaLibrary.pConsole(screen+"   "+page);
		if (screen < INTRO.getScreen())
			return TOC;
		ChromaBook h = ChromaBookData.getMapping(screen, page);
		return h != null ? h : TOC;
	}

	public boolean playerCanSee(EntityPlayer ep) {
		if (progress != null) {
			for (int i = 0; i < progress.length; i++) {
				ProgressStage p = progress[i];
				if (!ProgressionManager.instance.isPlayerAtStage(ep, p))
					return false;
			}
		}
		return true;
	}

	public static void addRelevantButtons(int j, int k, int screen, List<GuiButton> li) {
		int id = 0;
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].getScreen() == screen && tabList[i].playerCanSee(Minecraft.getMinecraft().thePlayer)) {
				li.add(new ImagedGuiButton(id, j-20, k+id*20-8, 20, 20, 0, 0, tabList[i].getTabImageFile(), ChromatiCraft.class));
				//ReikaJavaLibrary.pConsole("Adding "+tabList[i]+" with ID "+id+" to screen "+screen);
				id++;
			}
		}
	}

	public static List<ChromaBook> getEntriesForScreen(int screen) {
		//ReikaJavaLibrary.pConsole(screen);
		List<ChromaBook> li = new ArrayList<ChromaBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].getScreen() == screen) {
				li.add(tabList[i]);
			}
		}
		return li;
	}

	public static List<ChromaBook> getTOCTabs() {
		ArrayList<ChromaBook> li = new ArrayList<ChromaBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent && tabList[i] != TOC)
				li.add(tabList[i]);
		}
		return li;
	}

	public static List<ChromaBook> getMachineTabs() {
		List<ChromaBook> tabs = new ArrayList<ChromaBook>();
		for (int i = 0; i < tabList.length; i++) {
			ChromaBook h = tabList[i];
			if (h.isMachine() && !h.isParent)
				tabs.add(h);
		}
		return tabs;
	}

	public static ChromaBook[] getResourceTabs() {
		int size = tabList.length-RESOURCEDESC.ordinal()-1;
		ChromaBook[] tabs = new ChromaBook[size];
		System.arraycopy(tabList, RESOURCEDESC.ordinal()+1, tabs, 0, size);
		return tabs;
	}

	public static List<ChromaBook> getCategoryTabs() {
		ArrayList<ChromaBook> li = new ArrayList<ChromaBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent && tabList[i] != TOC)
				li.add(tabList[i]);
		}
		return li;
	}

	public boolean isMachine() {
		return machine != null;
	}

	public ChromaTiles getMachine() {
		return machine;
	}

	public ChromaItems getItem() {
		return item;
	}

	@Override
	public ItemStack getTabIcon() {
		return iconItem;
	}

	public String getData() {
		if (this == TOC)
			return ChromaDescriptions.getTOC();
		return ChromaDescriptions.getData(this);
	}

	public String getNotes() {
		return ChromaDescriptions.getNotes(this);
	}

	@Override
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

	@Override
	public String getTitle() {
		return pageTitle;
	}

	@Override
	public boolean hasMachineRender() {
		return this.isMachine();
	}

	@Override
	public boolean hasSubpages() {
		return this.isMachine() || this.isTool();
	}

	public String getTabImageFile() {
		return "Textures/GUIs/Handbook/tabs.png";
	}

	public int getRelativeScreen() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset/8;
	}

	public ChromaBook getParent() {
		ChromaBook parent = null;
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent) {
				if (this.ordinal() >= tabList[i].ordinal()) {
					parent = tabList[i];
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
			ChromaBook h = tabList[i];
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
		for (int i = this.ordinal()+1; i < tabList.length; i++) {
			ChromaBook h = tabList[i];
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

	@Override
	public boolean isConfigDisabled() {
		return false;
	}

}
