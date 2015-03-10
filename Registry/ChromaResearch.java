/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystal;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystalColors;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockDyeTypes;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ChromaResearch {

	//---------------------INFO--------------------//
	INTRO("Introduction", ""),
	START("Getting Started",			ChromaItems.HELP.getStackOf(),							ResearchLevel.ENTRY),
	ELEMENTS("Crystal Energy", 			ChromaItems.ELEMENTAL.getStackOf(CrystalElement.BLUE),	ResearchLevel.BASICCRAFT,	ProgressStage.ALLCOLORS),
	CRYSTALS("Crystals", 				ChromaBlocks.CRYSTAL.getStackOfMetadata(4), 			ResearchLevel.ENTRY, 		ProgressStage.CRYSTALS),
	PYLONS("Pylons", 					ChromaTiles.PYLON.getCraftedProduct(), 					ResearchLevel.ENTRY, 		ProgressStage.PYLON),
	TRANSMISSION("Signal Transmission", ChromaStacks.beaconDust, 								ResearchLevel.ENERGYEXPLORE),
	CRAFTING("Casting",					ChromaTiles.TABLE.getCraftedProduct(),					ResearchLevel.BASICCRAFT),
	BALLLIGHTNING("Ball Lightning",		ChromaStacks.auraDust,									ResearchLevel.ENERGYEXPLORE, ProgressStage.BALLLIGHTNING),
	APIRECIPES("Other Recipes",			new ItemStack(Blocks.dirt),								ResearchLevel.BASICCRAFT),

	MACHINEDESC("Constructs", ""),
	REPEATER(		ChromaTiles.REPEATER,		ResearchLevel.NETWORKING),
	GUARDIAN(		ChromaTiles.GUARDIAN, 		ResearchLevel.PYLONCRAFT),
	//LIQUIFIER(		ChromaTiles.LIQUIFIER, 		ResearchLevel.RUNECRAFT),
	REPROGRAMMER(	ChromaTiles.REPROGRAMMER, 	ResearchLevel.MULTICRAFT),
	ACCEL(			ChromaTiles.ACCELERATOR, 	ResearchLevel.ENDGAME),
	RIFT(			ChromaTiles.RIFT, 			ResearchLevel.PYLONCRAFT),
	TANK(			ChromaTiles.TANK, 			ResearchLevel.PYLONCRAFT),
	COMPOUND(		ChromaTiles.COMPOUND, 		ResearchLevel.NETWORKING),
	CHARGER(		ChromaTiles.CHARGER, 		ResearchLevel.PYLONCRAFT),
	LILY(			ChromaTiles.HEATLILY, 		ResearchLevel.RUNECRAFT),
	TICKER(			ChromaTiles.TICKER, 		ResearchLevel.MULTICRAFT),
	FENCE(			ChromaTiles.FENCE, 			ResearchLevel.MULTICRAFT),
	FURNACE(		ChromaTiles.FURNACE, 		ResearchLevel.PYLONCRAFT),
	TELEPUMP(		ChromaTiles.TELEPUMP, 		ResearchLevel.PYLONCRAFT),
	MINER(			ChromaTiles.MINER, 			ResearchLevel.PYLONCRAFT),
	ITEMSTAND(		ChromaTiles.STAND,			ResearchLevel.RUNECRAFT),
	LASER(			ChromaTiles.LASER, 			ResearchLevel.PYLONCRAFT),
	ITEMRIFT(		ChromaTiles.ITEMRIFT, 		ResearchLevel.MULTICRAFT),
	CRYSTAL(		ChromaTiles.CRYSTAL, 		ResearchLevel.ENDGAME),
	INFUSER(		ChromaTiles.INFUSER, 		ResearchLevel.MULTICRAFT),
	FABRICATOR(		ChromaTiles.FABRICATOR, 	ResearchLevel.PYLONCRAFT),
	ENCHANTER(		ChromaTiles.ENCHANTER, 		ResearchLevel.RUNECRAFT),
	CHROMAFLOWER(	ChromaTiles.CHROMAFLOWER, 	ResearchLevel.BASICCRAFT),
	COLLECTOR(		ChromaTiles.COLLECTOR, 		ResearchLevel.ENERGYEXPLORE),
	BREWER(			ChromaTiles.BREWER, 		ResearchLevel.BASICCRAFT),
	RITUALTABLE(	ChromaTiles.RITUAL, 		ResearchLevel.ENERGYEXPLORE),
	CASTTABLE(		ChromaTiles.TABLE, 			ResearchLevel.ENTRY),
	BEACON(			ChromaTiles.BEACON, 		ResearchLevel.ENDGAME),
	ITEMCOLLECTOR(	ChromaTiles.ITEMCOLLECTOR, 	ResearchLevel.PYLONCRAFT),
	AISHUTDOWN(		ChromaTiles.AISHUTDOWN, 	ResearchLevel.MULTICRAFT),
	ASPECT(			ChromaTiles.ASPECT, 		ResearchLevel.ENERGYEXPLORE),
	LAMP(			ChromaTiles.LAMP, 			ResearchLevel.ENERGYEXPLORE),
	POWERTREE(		ChromaTiles.POWERTREE, 		ResearchLevel.ENDGAME,			ProgressStage.POWERCRYSTAL),
	LAMPCONTROL(	ChromaTiles.LAMPCONTROL, 	ResearchLevel.RUNECRAFT),
	BIOMEPAINT(		ChromaTiles.BIOMEPAINTER,	ResearchLevel.ENDGAME),
	ASPECTJAR(		ChromaTiles.ASPECTJAR,		ResearchLevel.PYLONCRAFT),
	FARMER(			ChromaTiles.FARMER,			ResearchLevel.PYLONCRAFT),

	BLOCKS("Other Blocks", ""),
	RUNES(			ChromaBlocks.RUNE,			CrystalElement.LIGHTBLUE.ordinal(),	ResearchLevel.BASICCRAFT),
	CHROMA(			ChromaBlocks.CHROMA,											ResearchLevel.RAWEXPLORE),
	TNT(			ChromaBlocks.TNT,												ResearchLevel.PYLONCRAFT),
	TANKAUX(		ChromaBlocks.TANK,												ResearchLevel.MULTICRAFT),
	FENCEAUX(		ChromaBlocks.FENCE,												ResearchLevel.MULTICRAFT),
	LUMENLEAVES(	ChromaBlocks.POWERTREE,		CrystalElement.LIME.ordinal(),		ResearchLevel.PYLONCRAFT),
	DYELEAVES(		ChromaBlocks.DYELEAF,		CrystalElement.BROWN.ordinal(),		ResearchLevel.ENTRY,		ProgressStage.DYETREE),
	RAINBOWLEAVES(	ChromaBlocks.RAINBOWLEAF,	3,									ResearchLevel.RAWEXPLORE,	ProgressStage.RAINBOWLEAF),
	LAMPAUX(		ChromaBlocks.LAMPBLOCK,		CrystalElement.WHITE.ordinal(),		ResearchLevel.BASICCRAFT),
	CRYSTALLAMP(	ChromaBlocks.LAMP,			CrystalElement.YELLOW.ordinal(),	ResearchLevel.RAWEXPLORE),
	SUPERLAMP(		ChromaBlocks.SUPER,			CrystalElement.MAGENTA.ordinal(),	ResearchLevel.PYLONCRAFT),
	PATH(			ChromaBlocks.PATH,												ResearchLevel.RUNECRAFT),
	GLOW(			ChromaBlocks.GLOW,			CrystalElement.RED.ordinal(),		ResearchLevel.BASICCRAFT),
	RELAY(			ChromaBlocks.RELAY,												ResearchLevel.NETWORKING),

	TOOLDESC("Tools", ""),
	WAND(				ChromaItems.TOOL,			ResearchLevel.ENTRY),
	FINDER(				ChromaItems.FINDER, 		ResearchLevel.RAWEXPLORE,	ProgressStage.PYLON),
	EXCAVATOR(			ChromaItems.EXCAVATOR, 		ResearchLevel.CHARGESELF),
	TRANSITION(			ChromaItems.TRANSITION, 	ResearchLevel.CHARGESELF),
	INVLINK(			ChromaItems.LINK, 			ResearchLevel.ENERGYEXPLORE),
	PENDANT(			ChromaItems.PENDANT, 		ResearchLevel.ENERGYEXPLORE),
	VACUUMGUN(			ChromaItems.VACUUMGUN, 		ResearchLevel.MULTICRAFT),
	LENS(				ChromaItems.LENS, 			ResearchLevel.BASICCRAFT),
	STORAGE(			ChromaItems.STORAGE, 		ResearchLevel.ENERGYEXPLORE),
	LINKTOOL(			ChromaItems.LINKTOOL, 		ResearchLevel.RUNECRAFT),
	WARP(				ChromaItems.WARP, 			ResearchLevel.PYLONCRAFT),
	TELEPORT(			ChromaItems.TELEPORT, 		ResearchLevel.MULTICRAFT),
	DUPLICATOR(			ChromaItems.DUPLICATOR, 	ResearchLevel.MULTICRAFT),
	BUILDER(			ChromaItems.BUILDER, 		ResearchLevel.MULTICRAFT),
	CAPTURE(			ChromaItems.CAPTURE, 		ResearchLevel.MULTICRAFT),
	VOIDCELL(			ChromaItems.VOIDCELL, 		ResearchLevel.ENDGAME),
	AURAPOUCH(			ChromaItems.AURAPOUCH,		ResearchLevel.MULTICRAFT),
	MULTITOOL(			ChromaItems.MULTITOOL,		ResearchLevel.RUNECRAFT),
	OREPICK(			ChromaItems.OREPICK,		ResearchLevel.RUNECRAFT),
	ORESILK(			ChromaItems.ORESILK,		ResearchLevel.RUNECRAFT),

	RESOURCEDESC("Resources", ""),
	BERRIES("Berries",				ChromaItems.BERRY.getStackOf(CrystalElement.ORANGE),	ResearchLevel.RAWEXPLORE,	ProgressStage.DYETREE),
	SHARDS("Shards",				ChromaStacks.redShard, 									ResearchLevel.RAWEXPLORE,	ProgressStage.CRYSTALS),
	DUSTS("Plant Dusts",			ChromaStacks.auraDust, 									ResearchLevel.ENERGYEXPLORE),
	GROUPS("Groups",				ChromaStacks.crystalCore, 								ResearchLevel.BASICCRAFT),
	CORES("Cores",					ChromaStacks.energyCore,								ResearchLevel.MULTICRAFT),
	IRID("Iridescent Crystal",		ChromaStacks.iridCrystal,								ResearchLevel.MULTICRAFT,	ProgressStage.CHROMA),
	ORES("Buried Secrets",			ChromaStacks.bindingCrystal,							ResearchLevel.RUNECRAFT),
	CRYSTALSTONE("Crystal Stone",	ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 			ResearchLevel.BASICCRAFT),
	SEED("Crystal Seeds",			ChromaItems.SEED.getStackOf(CrystalElement.MAGENTA),	ResearchLevel.RAWEXPLORE,	ProgressStage.CRYSTALS),
	FRAGMENT("Fragments",			ChromaItems.FRAGMENT, 									ResearchLevel.ENTRY),
	AUGMENT("Upgrades",				ChromaStacks.speedUpgrade,								ResearchLevel.PYLONCRAFT,	ProgressStage.STORAGE),
	ALLOYS("Alloying",				ChromaStacks.magicIngot,								ResearchLevel.BASICCRAFT,	ProgressStage.CHROMA),

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
	PYLONPROTECT(	Chromabilities.PYLON, 						ResearchLevel.ENERGYEXPLORE),
	LIGHTNING(		Chromabilities.LIGHTNING),
	LIFEPOINT(		Chromabilities.LIFEPOINT),
	DEATHPROOF(		Chromabilities.DEATHPROOF),
	SHOCKWAVE(		Chromabilities.SHOCKWAVE),
	WARPLOC(		Chromabilities.TELEPORT,					ResearchLevel.ENDGAME),
	LEECH(			Chromabilities.LEECH),

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
	CAVERN(			ChromaStructures.Structures.CAVERN,		0,	ResearchLevel.RAWEXPLORE,		ProgressStage.CAVERN),
	BURROW(			ChromaStructures.Structures.BURROW,		0,	ResearchLevel.RAWEXPLORE,		ProgressStage.BURROW),
	OCEAN(			ChromaStructures.Structures.OCEAN,		0,	ResearchLevel.RAWEXPLORE,		ProgressStage.OCEAN),
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
	private static final ItemHashMap<ChromaResearch> itemMap = new ItemHashMap();
	private static final List<ChromaResearch> parents = new ArrayList();
	private static final List<ChromaResearch> nonParents = new ArrayList();
	private static final HashMap<String, ChromaResearch> byName = new HashMap();

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
		this(r.getBasicName(), r.getStackOfMetadata(meta), rl, p);
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

	private ChromaResearch(Chromabilities c) {
		this(c, ResearchLevel.PYLONCRAFT);
	}

	private ChromaResearch(Chromabilities c, ResearchLevel rl) {
		iconItem = ChromaTiles.RITUAL.getCraftedProduct();
		pageTitle = c.getDisplayName();
		Collection<ProgressStage> p = AbilityHelper.instance.getProgressFor(c);
		progress = p.toArray(new ProgressStage[p.size()]);
		level = rl;
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
		if (this.isDummiedOut())
			return DragonAPICore.isReikasComputer();
		if (progress != null) {
			for (int i = 0; i < progress.length; i++) {
				ProgressStage p = progress[i];
				if (!p.isPlayerAtStage(ep))
					return false;
			}
		}
		return ChromaResearchManager.instance.playerHasFragment(ep, this);
	}

	public boolean playerCanRead(EntityPlayer ep) {
		return this.playerCanSee(ep) || ChromaResearchManager.instance.canPlayerStepTo(ep, this);
	}

	public boolean playerHasProgress(EntityPlayer ep) {
		if (progress != null) {
			for (int i = 0; i < progress.length; i++) {
				ProgressStage p = progress[i];
				if (!p.isPlayerAtStage(ep))
					return false;
			}
		}
		return true;
	}

	ProgressStage[] getRequiredProgress() {
		return Arrays.copyOf(progress, progress.length);
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

	private ItemStack getTabIcon() {
		return iconItem;
	}

	@SideOnly(Side.CLIENT)
	public void drawTabIcon(RenderItem ri, int x, int y) {
		if (this == BALLLIGHTNING) {
			EntityBallLightning eb = new EntityBallLightning(Minecraft.getMinecraft().theWorld);
			eb.isDead = true;
			GL11.glPushMatrix();
			double d = 8;
			GL11.glTranslated(x+d, y+d, 0);
			double s = 18;
			GL11.glScaled(-s, s, 1);
			ReikaEntityHelper.getEntityRenderer(EntityBallLightning.class).doRender(eb, 0, 0, 0, 0, 0);
			GL11.glPopMatrix();
			return;
		}
		if (this == APIRECIPES) {
			ArrayList<ItemStack> ico = new ArrayList();
			/*
			if (ModList.THAUMCRAFT.isLoaded()) {
				ico.add(ThaumItemHelper.BlockEntry.ANCIENTROCK.getItem());
				ico.add(new ItemStack(ThaumItemHelper.BlockEntry.CRYSTAL.getBlock(), 1, 6));
				ico.add(ThaumItemHelper.BlockEntry.ETHEREAL.getItem());
				ico.add(ThaumItemHelper.ItemEntry.NITOR.getItem());
				ico.add(ThaumItemHelper.ItemEntry.THAUMIUM.getItem());
				ico.add(ThaumItemHelper.ItemEntry.FABRIC.getItem());
			}
			if (ModList.ROTARYCRAFT.isLoaded()) {
				ico.add(MachineRegistry.BLASTFURNACE.getCraftedProduct());
				ico.add(EngineType.AC.getCraftedProduct());
				ico.add(ItemRegistry.GRAVELGUN.getStackOf());
			}
			if (ModList.REACTORCRAFT.isLoaded()) {
				ico.add(ReactorTiles.INJECTOR.getCraftedProduct());
			}
			if (ModList.APPENG.isLoaded()) {
				ico.add(AEApi.instance().blocks().blockController.stack(1));
				ico.add(AEApi.instance().blocks().blockQuantumLink.stack(1));
				ico.add(AEApi.instance().blocks().blockQuartzGrowthAccelerator.stack(1));
			}
			if (ModList.FORESTRY.isLoaded()) {
				ico.add(new ItemStack(ForestryHandler.BlockEntry.HIVE.getBlock()));
				ico.add(new ItemStack(ForestryHandler.ItemEntry.COMB.getItem()));
				ico.add(new ItemStack(ForestryHandler.ItemEntry.HONEYDEW.getItem()));
				ico.add(new ItemStack(ForestryHandler.ItemEntry.QUEEN.getItem()));
				ico.add(new ItemStack(ForestryHandler.ItemEntry.POLLEN.getItem()));
			}
			if (ModList.FORESTRY.isLoaded()) {
				ico.add(new ItemStack(ForestryHandler.BlockEntry.HIVE.getBlock()));
				ico.add(new ItemStack(ForestryHandler.ItemEntry.COMB.getItem()));
				ico.add(new ItemStack(ForestryHandler.ItemEntry.HONEYDEW.getItem()));
				ico.add(new ItemStack(ForestryHandler.ItemEntry.QUEEN.getItem()));
				ico.add(new ItemStack(ForestryHandler.ItemEntry.POLLEN.getItem()));
			}
			 */
			for (CastingRecipe cr : RecipesCastingTable.instance.getAllAPIRecipes()) {
				if (!ReikaItemHelper.collectionContainsItemStack(ico, cr.getOutput()))
					ico.add(cr.getOutput());
			}
			if (!ico.isEmpty()) {
				int idx = (int)((System.currentTimeMillis()/400)%ico.size());
				ReikaGuiAPI.instance.drawItemStack(ri, ico.get(idx), x, y);
			}
			else {
				ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, ChromaIcons.NOENTER.getIcon(), 16, 16);
			}
			return;
		}
		ReikaGuiAPI.instance.drawItemStack(ri, this.getTabIcon(), x, y);
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

	public boolean isGating() {
		if (this.isDummiedOut())
			return false;
		if (this == DEATHPROOF)
			return false;
		return struct == null || !struct.isNatural();
	}

	public boolean isAbility() {
		if (isParent)
			return false;
		return this.getParent() == ABILITYDESC;
	}

	public boolean isCrafting() {
		if (isParent)
			return false;
		if (this == APIRECIPES)
			return true;
		if (this.isMachine() || this.isTool())
			return true;
		if (this == GROUPS)
			return true;
		if (this == CORES)
			return true;
		if (this == ALLOYS)
			return true;
		if (this == IRID)
			return true;
		if (this == SEED)
			return true;
		if (this == AUGMENT)
			return true;
		if (this == RELAY)
			return true;
		if (this == RUNES)
			return true;
		if (this == TANKAUX)
			return true;
		if (this == FENCEAUX)
			return true;
		if (this == TNT)
			return true;
		if (this == LAMPAUX)
			return true;
		if (this == CRYSTALLAMP || this == SUPERLAMP)
			return true;
		if (this == CRYSTALSTONE)
			return true;
		if (this == PATH)
			return true;
		if (this == GLOW)
			return true;
		return false;
	}

	public boolean isTool() {
		return this.getParent() == TOOLDESC;
	}

	public ArrayList<ItemStack> getItemStacks() {
		if (this.isMachine())
			return ReikaJavaLibrary.makeListFrom(machine.getCraftedProduct());
		if (this == STORAGE) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < ChromaItems.STORAGE.getNumberMetadatas(); i++) {
				li.add(ChromaItems.STORAGE.getStackOfMetadata(i));
			}
			return li;
		}
		if (this == GLOW) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < 48; i++) {
				li.add(ChromaBlocks.GLOW.getStackOfMetadata(i));
			}
			return li;
		}
		if (this == RELAY) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaTiles.RELAYSOURCE.getCraftedProduct());
			for (int i = 0; i < 16; i++) {
				li.add(ChromaBlocks.RELAY.getStackOfMetadata(i));
			}
			li.add(ChromaBlocks.RELAY.getStackOfMetadata(16));
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
		if (this == AUGMENT) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaStacks.speedUpgrade);
			li.add(ChromaStacks.efficiencyUpgrade);
			li.add(ChromaStacks.silkUpgrade);
			return li;
		}
		if (item != null) {
			return ReikaJavaLibrary.makeListFrom(item.getStackOf());
		}
		if (iconItem != null && iconItem.getItem() instanceof ItemCrystalBasic) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < 16; i++) {
				li.add(new ItemStack(iconItem.getItem(), 1, i));
			}
			return li;
		}
		if (this == GROUPS) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < 13; i++) {
				li.add(ChromaItems.CLUSTER.getStackOfMetadata(i));
			}
			return li;
		}
		if (this == ALLOYS) {
			return new ArrayList(PoolRecipes.instance.getAllOutputItems());
		}
		if (this == ORES) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaStacks.chromaDust);
			for (int i = 0; i < 16; i++)
				li.add(ChromaItems.ELEMENTAL.getStackOfMetadata(i));
			li.add(ChromaStacks.focusDust);
			li.add(ChromaStacks.bindingCrystal);
			li.add(ChromaStacks.enderDust);
			li.add(ChromaStacks.waterDust);
			li.add(ChromaStacks.firaxite);
			return li;
		}
		if (this == DUSTS) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaStacks.auraDust);
			li.add(ChromaStacks.purityDust);
			li.add(ChromaStacks.elementDust);
			li.add(ChromaStacks.beaconDust);
			li.add(ChromaStacks.resonanceDust);
			return li;
		}
		if (this == IRID) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaStacks.rawCrystal);
			li.add(ChromaStacks.iridCrystal);
			li.add(ChromaStacks.iridChunk);
			return li;
		}
		if (this == CORES) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaStacks.crystalFocus);
			li.add(ChromaStacks.energyCore);
			li.add(ChromaStacks.transformCore);
			li.add(ChromaStacks.voidCore);
			li.add(ChromaStacks.elementUnit);
			li.add(ChromaStacks.crystalLens);
			return li;
		}
		if (block != null) {
			Item item = Item.getItemFromBlock(block.getBlockInstance());
			if (item instanceof ItemBlockDyeTypes || item instanceof ItemBlockCrystalColors || item instanceof ItemBlockCrystal) {
				ArrayList<ItemStack> li = new ArrayList();
				for (int i = 0; i < 16; i++) {
					li.add(block.getStackOfMetadata(i));
				}
				return li;
			}
		}
		if (this == FENCEAUX || this == TNT || this == TANKAUX)
			return ReikaJavaLibrary.makeListFrom(this.getTabIcon());
		if (this == PATH) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < ChromaBlocks.PATH.getNumberMetadatas(); i++) {
				li.add(new ItemStack(ChromaBlocks.PATH.getBlockInstance(), 1, i));
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

	public int getVanillaRecipeCount() {
		return this.getVanillaRecipes().size();
	}

	public boolean isCraftable() {
		if (this == ALLOYS)
			return true;
		if (!this.isConfigDisabled() && this.isCrafting()) {
			return this.isVanillaRecipe() ? this.getVanillaRecipeCount() > 0 : this.getRecipeCount() > 0;
		}
		return false;
	}

	public boolean isVanillaRecipe() {
		switch(this) {
		case CASTTABLE:
		case WAND:
			return true;
		default:
			return false;
		}
	}

	public ChromaGuis getCraftingType() {
		if (this == ALLOYS)
			return ChromaGuis.ALLOYING;
		return this.isVanillaRecipe() ? ChromaGuis.CRAFTING : ChromaGuis.RECIPE;
	}

	public ArrayList<CastingRecipe> getCraftingRecipes() {
		if (this == APIRECIPES) {
			return new ArrayList(RecipesCastingTable.instance.getAllAPIRecipes());
		}
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

	public int getRecipeIndex(ItemStack is) {
		ArrayList<CastingRecipe> li = this.getCraftingRecipes();
		for (int i = 0; i < li.size(); i++) {
			if (ReikaItemHelper.matchStacks(is, li.get(i).getOutput()))
				return i;
		}
		return 0;
	}

	public RecipeType getRecipeLevel(int recipe) {
		ArrayList<CastingRecipe> li = this.getCraftingRecipes();
		if (li.isEmpty())
			return null;
		return li.get(recipe).type;
	}

	public ArrayList<IRecipe> getVanillaRecipes() {
		if (!this.isCrafting())
			return new ArrayList();
		ArrayList<ItemStack> li = this.getItemStacks();
		if (li == null || li.isEmpty())
			return new ArrayList();
		ArrayList<IRecipe> rec = new ArrayList();
		for (ItemStack is : li) {
			rec.addAll(ReikaRecipeHelper.getAllRecipesByOutput(CraftingManager.getInstance().getRecipeList(), is));
		}
		return rec;
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

	public boolean isConfigDisabled() {
		if (machine != null)
			return machine.isConfigDisabled();
		if (item != null)
			return item.isConfigDisabled();
		return false;
	}

	public boolean isDummiedOut() {
		if (machine != null)
			return machine.isDummiedOut();
		if (item != null)
			return item.isDummiedOut();
		if (ability != null)
			return ability.isDummiedOut();
		if (this == APIRECIPES && RecipesCastingTable.instance.getAllAPIRecipes().isEmpty() && DragonAPICore.hasGameLoaded()) //only hide display
			return true;
		return false;
	}

	static {
		for (int i = 0; i < researchList.length; i++) {
			ChromaResearch r = researchList[i];
			if (!r.isDummiedOut()) {
				if (r.level != null)
					levelMap.addValue(r.level, r);
				byName.put(r.name(), r);
				if (r.isParent)
					parents.add(r);
				else
					nonParents.add(r);
				Collection<ItemStack> c = r.getItemStacks();
				if (c != null) {
					for (ItemStack is : c) {
						itemMap.put(is, r);
					}
				}
				Collection<CastingRecipe> crc = r.getCraftingRecipes();
				for (CastingRecipe cr : crc) {
					cr.setFragment(r);
				}
			}
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

	public static ChromaResearch getPageFor(ItemStack is) {
		return itemMap.get(is);
	}

	public static List<ChromaResearch> getAllParents() {
		return Collections.unmodifiableList(parents);
	}

	public static List<ChromaResearch> getAllNonParents() {
		return Collections.unmodifiableList(nonParents);
	}

	public static ChromaResearch getByName(String s) {
		return byName.get(s);
	}

}
