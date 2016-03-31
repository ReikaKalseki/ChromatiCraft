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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.RepeaterTurboRecipe;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystal;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystalColors;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockDyeTypes;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Registry.Dependency;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModRegistry.PowerTypes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeType;

public enum ChromaResearch implements ProgressElement {

	//---------------------INFO--------------------//
	INTRO("Introduction", ""),
	START("Getting Started",			new ItemStack(Blocks.dirt),								ResearchLevel.ENTRY),
	LEXICON("The Lexicon",				ChromaItems.HELP.getStackOf(),							ResearchLevel.ENTRY),
	ELEMENTS("Crystal Energy", 			ChromaItems.ELEMENTAL.getStackOf(CrystalElement.BLUE),	ResearchLevel.BASICCRAFT,	ProgressStage.ALLCOLORS),
	CRYSTALS("Crystals", 				ChromaBlocks.CRYSTAL.getStackOfMetadata(4), 			ResearchLevel.ENTRY, 		ProgressStage.CRYSTALS),
	PYLONS("Pylons", 					ChromaTiles.PYLON.getCraftedProduct(), 					ResearchLevel.ENTRY, 		ProgressStage.PYLON),
	STRUCTURES("Structures",			ChromaBlocks.PYLONSTRUCT.getStackOf(),					ResearchLevel.RAWEXPLORE),
	TRANSMISSION("Signal Transmission", ChromaStacks.beaconDust, 								ResearchLevel.ENERGYEXPLORE),
	CRAFTING("Casting",					ChromaTiles.TABLE.getCraftedProduct(),					ResearchLevel.BASICCRAFT),
	BALLLIGHTNING("Ball Lightning",		ChromaStacks.auraDust,									ResearchLevel.ENERGYEXPLORE, ProgressStage.BALLLIGHTNING),
	APIRECIPES("Other Recipes",			new ItemStack(Blocks.dirt),								ResearchLevel.BASICCRAFT),
	LEYLINES("Ley Lines",				ChromaTiles.REPEATER.getCraftedProduct(),				ResearchLevel.NETWORKING,	ProgressStage.REPEATER),
	USINGRUNES("Crafting With Runes",	ChromaBlocks.RUNE.getStackOfMetadata(1),				ResearchLevel.RUNECRAFT, 	ProgressStage.RUNEUSE),
	DIMENSION("Another World",			ChromaBlocks.PORTAL.getStackOf(),						ResearchLevel.ENDGAME,		ProgressionManager.instance.getPrereqsArray(ProgressStage.DIMENSION)),
	DIMENSION2("A Volatile World",		ChromaBlocks.GLOWSAPLING.getStackOf(),					ResearchLevel.ENDGAME,		ProgressStage.DIMENSION),
	TURBO("Turbocharging",				ChromaStacks.elementUnit,								ResearchLevel.ENDGAME, 		ProgressionManager.instance.getPrereqsArray(ProgressStage.TURBOCHARGE)),
	TURBOREPEATER("Repeater Turbocharging", ChromaStacks.turboRepeater,							ResearchLevel.ENDGAME,		ProgressStage.TURBOCHARGE),
	PACKCHANGES("Modpack Changes",		new ItemStack(Blocks.command_block),					ResearchLevel.ENTRY),
	NODENET("Networking Aura Nodes",	new ItemStack(Blocks.command_block),					ResearchLevel.CTM,		ProgressStage.CTM),
	SELFCHARGE("Energy Internalization",ChromaItems.TOOL.getStackOf(),							ResearchLevel.CHARGESELF,	ProgressStage.CHARGE),
	MYSTPAGE("World Authoring",			new ItemStack(Items.map),								ResearchLevel.RAWEXPLORE),
	ENCHANTING("Crystal Enchanting",	new ItemStack(Items.enchanted_book),					ResearchLevel.MULTICRAFT,	ProgressStage.MULTIBLOCK),

	MACHINEDESC("Constructs", ""),
	REPEATER(		ChromaTiles.REPEATER,		ResearchLevel.NETWORKING),
	GUARDIAN(		ChromaTiles.GUARDIAN, 		ResearchLevel.PYLONCRAFT),
	//LIQUIFIER(		ChromaTiles.LIQUIFIER, 		ResearchLevel.RUNECRAFT),
	REPROGRAMMER(	ChromaTiles.REPROGRAMMER, 	ResearchLevel.PYLONCRAFT),
	ACCEL(			ChromaTiles.ACCELERATOR, 	ResearchLevel.ENDGAME),
	RIFT(			ChromaTiles.RIFT, 			ResearchLevel.PYLONCRAFT),
	TANK(			ChromaTiles.TANK, 			ResearchLevel.PYLONCRAFT),
	COMPOUND(		ChromaTiles.COMPOUND, 		ResearchLevel.NETWORKING,	ProgressStage.REPEATER),
	CHARGER(		ChromaTiles.CHARGER, 		ResearchLevel.PYLONCRAFT),
	LILY(			ChromaTiles.HEATLILY, 		ResearchLevel.RUNECRAFT),
	TICKER(			ChromaTiles.TICKER, 		ResearchLevel.MULTICRAFT),
	FENCE(			ChromaTiles.FENCE, 			ResearchLevel.MULTICRAFT),
	FURNACE(		ChromaTiles.FURNACE, 		ResearchLevel.PYLONCRAFT),
	TELEPUMP(		ChromaTiles.TELEPUMP, 		ResearchLevel.PYLONCRAFT),
	MINER(			ChromaTiles.MINER, 			ResearchLevel.CTM,			ProgressStage.CTM),
	ITEMSTAND(		ChromaTiles.STAND,			ResearchLevel.RUNECRAFT),
	LASER(			ChromaTiles.LASER, 			ResearchLevel.PYLONCRAFT),
	ITEMRIFT(		ChromaTiles.ITEMRIFT, 		ResearchLevel.MULTICRAFT),
	CRYSTAL(		ChromaTiles.CRYSTAL, 		ResearchLevel.ENDGAME),
	INFUSER(		ChromaTiles.INFUSER, 		ResearchLevel.MULTICRAFT),
	FABRICATOR(		ChromaTiles.FABRICATOR, 	ResearchLevel.PYLONCRAFT),
	ENCHANTER(		ChromaTiles.ENCHANTER, 		ResearchLevel.RUNECRAFT),
	CHROMAFLOWER(	ChromaTiles.CHROMAFLOWER, 	ResearchLevel.BASICCRAFT),
	COLLECTOR(		ChromaTiles.COLLECTOR, 		ResearchLevel.BASICCRAFT),
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
	BIOMEPAINT(		ChromaTiles.BIOMEPAINTER,	ResearchLevel.ENDGAME,			ProgressStage.RAINBOWFOREST),
	ASPECTJAR(		ChromaTiles.ASPECTJAR,		ResearchLevel.PYLONCRAFT),
	FARMER(			ChromaTiles.FARMER,			ResearchLevel.PYLONCRAFT),
	AUTO(			ChromaTiles.AUTOMATOR,		ResearchLevel.ENDGAME),
	MEDISTRIB(		ChromaTiles.MEDISTRIBUTOR,	ResearchLevel.MULTICRAFT),
	WINDOW(			ChromaTiles.WINDOW,			ResearchLevel.MULTICRAFT),
	RFDISTRIB(		ChromaTiles.RFDISTRIBUTOR,	ResearchLevel.RUNECRAFT),
	PERSONAL(		ChromaTiles.PERSONAL,		ResearchLevel.CHARGESELF,		ProgressStage.CHARGE),
	MUSIC(			ChromaTiles.MUSIC,			ResearchLevel.BASICCRAFT),
	PYLONTURBO(		ChromaTiles.PYLONTURBO,		ResearchLevel.ENDGAME,			ProgressStage.DIMENSION),
	TURRET(			ChromaTiles.TURRET,			ResearchLevel.BASICCRAFT,		ProgressStage.KILLMOB),
	BROADCAST(		ChromaTiles.BROADCAST,		ResearchLevel.NETWORKING),
	CLOAKING(		ChromaTiles.CLOAKING,		ResearchLevel.MULTICRAFT,		ProgressStage.KILLMOB),
	CAVELIGHTER(	ChromaTiles.LIGHTER,		ResearchLevel.RUNECRAFT,		ProgressStage.BEDROCK),
	GLOWFIRE(		ChromaTiles.GLOWFIRE,		ResearchLevel.ENERGYEXPLORE,	ProgressStage.SHARDCHARGE),
	ESSENTIA(		ChromaTiles.ESSENTIARELAY,	ResearchLevel.RUNECRAFT),
	INSERTER(		ChromaTiles.INSERTER,		ResearchLevel.RUNECRAFT),
	LOTUS(			ChromaTiles.REVERTER, 		ResearchLevel.RUNECRAFT),
	COBBLEGEN(		ChromaTiles.COBBLEGEN,		ResearchLevel.RUNECRAFT),
	PLANTACCEL(		ChromaTiles.PLANTACCEL,		ResearchLevel.RUNECRAFT),
	CROPSPEED(		ChromaTiles.CROPSPEED,		ResearchLevel.RUNECRAFT),
	WEAKREPEATER(	ChromaTiles.WEAKREPEATER,	ResearchLevel.ENERGYEXPLORE,	ProgressStage.PYLON, ProgressStage.MAKECHROMA),
	ENCHANTDECOMP(	ChromaTiles.ENCHANTDECOMP,	ResearchLevel.BASICCRAFT,		ProgressStage.MAKECHROMA),
	LUMENWIRE(		ChromaTiles.LUMENWIRE,		ResearchLevel.BASICCRAFT),
	PARTICLES(		ChromaTiles.PARTICLES,		ResearchLevel.BASICCRAFT),

	BLOCKS("Other Blocks", ""),
	RUNES(			ChromaBlocks.RUNE,			CrystalElement.LIGHTBLUE.ordinal(),	ResearchLevel.BASICCRAFT),
	CHROMA(			ChromaBlocks.CHROMA,											ResearchLevel.RAWEXPLORE),
	HEATLAMP(		ChromaBlocks.HEATLAMP,											ResearchLevel.RUNECRAFT,	ProgressStage.NETHER),
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
	PORTAL(			ChromaBlocks.PORTAL,											ResearchLevel.ENDGAME,		ProgressionManager.instance.getPrereqsArray(ProgressStage.DIMENSION)),
	COLORALTAR(		ChromaBlocks.COLORALTAR,	CrystalElement.WHITE.ordinal(),		ResearchLevel.ENERGYEXPLORE),
	DOOR(			ChromaBlocks.DOOR,												ResearchLevel.BASICCRAFT),
	GLASS(			ChromaBlocks.GLASS,			CrystalElement.BLUE.ordinal(),		ResearchLevel.BASICCRAFT),
	MUSICTRIGGER(	ChromaBlocks.MUSICTRIGGER,										ResearchLevel.BASICCRAFT),
	SELECTIVEGLASS(	ChromaBlocks.SELECTIVEGLASS,									ResearchLevel.BASICCRAFT),

	TOOLDESC("Tools", ""),
	WAND(				ChromaItems.TOOL,			ResearchLevel.ENTRY),
	FINDER(				ChromaItems.FINDER, 		ResearchLevel.BASICCRAFT,	ProgressStage.PYLON),
	EXCAVATOR(			ChromaItems.EXCAVATOR, 		ResearchLevel.CHARGESELF),
	TRANSITION(			ChromaItems.TRANSITION, 	ResearchLevel.CHARGESELF),
	INVLINK(			ChromaItems.LINK, 			ResearchLevel.ENERGYEXPLORE),
	PENDANT(			ChromaItems.PENDANT, 		ResearchLevel.ENERGYEXPLORE),
	LENS(				ChromaItems.LENS, 			ResearchLevel.BASICCRAFT),
	STORAGE(			ChromaItems.STORAGE, 		ResearchLevel.ENERGYEXPLORE),
	LINKTOOL(			ChromaItems.LINKTOOL, 		ResearchLevel.RUNECRAFT),
	WARP(				ChromaItems.WARP, 			ResearchLevel.PYLONCRAFT),
	TELEPORT(			ChromaItems.TELEPORT, 		ResearchLevel.MULTICRAFT),
	DUPLICATOR(			ChromaItems.DUPLICATOR, 	ResearchLevel.MULTICRAFT),
	BUILDER(			ChromaItems.BUILDER, 		ResearchLevel.MULTICRAFT),
	CAPTURE(			ChromaItems.CAPTURE, 		ResearchLevel.MULTICRAFT,	ProgressStage.KILLMOB),
	VOIDCELL(			ChromaItems.VOIDCELL, 		ResearchLevel.ENDGAME),
	AURAPOUCH(			ChromaItems.AURAPOUCH,		ResearchLevel.MULTICRAFT),
	MULTITOOL(			ChromaItems.MULTITOOL,		ResearchLevel.RUNECRAFT),
	OREPICK(			ChromaItems.OREPICK,		ResearchLevel.RUNECRAFT),
	ORESILK(			ChromaItems.ORESILK,		ResearchLevel.RUNECRAFT),
	GROWTH(				ChromaItems.GROWTH,			ResearchLevel.MULTICRAFT),
	ENDERCRYS(			ChromaItems.ENDERCRYSTAL,	ResearchLevel.ENDGAME,		ProgressStage.END),
	BULKMOVER(			ChromaItems.BULKMOVER,		ResearchLevel.RUNECRAFT),
	CHAINGUN(			ChromaItems.CHAINGUN,		ResearchLevel.MULTICRAFT),
	HOVER(				ChromaItems.HOVERWAND,		ResearchLevel.PYLONCRAFT),
	SPLASH(				ChromaItems.SPLASHGUN,		ResearchLevel.MULTICRAFT),
	VACUUMGUN(			ChromaItems.VACUUMGUN,		ResearchLevel.ENDGAME,		ProgressStage.DIMENSION),
	DOORKEY(			ChromaItems.KEY,			ResearchLevel.BASICCRAFT),
	OWNERKEY(			ChromaItems.SHARE,			ResearchLevel.BASICCRAFT),
	//FLUIDWAND(			ChromaItems.FLUIDWAND,		ResearchLevel.RUNECRAFT,	ProgressStage.OCEAN),
	CRYSTALCELL(		ChromaItems.CRYSTALCELL,	ResearchLevel.MULTICRAFT,	ProgressStage.CHARGE),
	PURIFY(				ChromaItems.PURIFY,			ResearchLevel.ENDGAME,		ProgressStage.RAINBOWLEAF, ProgressStage.ALLOY),
	EFFICIENCY(			ChromaItems.EFFICIENCY,			ResearchLevel.CTM),
	KILLAURA(			ChromaItems.KILLAURAGUN,	ResearchLevel.ENDGAME),

	RESOURCEDESC("Resources", ""),
	BERRIES("Berries",				ChromaItems.BERRY.getStackOf(CrystalElement.ORANGE),	ResearchLevel.RAWEXPLORE,	ProgressStage.DYETREE),
	SHARDS("Shards",				ChromaStacks.redShard, 									ResearchLevel.RAWEXPLORE,	ProgressStage.CRYSTALS),
	DUSTS("Plant Dusts",			ChromaStacks.auraDust, 									ResearchLevel.ENERGYEXPLORE),
	GROUPS("Groups",				ChromaStacks.crystalCore, 								ResearchLevel.BASICCRAFT),
	CORES("Cores",					ChromaStacks.energyCore,								ResearchLevel.MULTICRAFT),
	HICORES("Energized Cores",		ChromaStacks.energyCoreHigh,							ResearchLevel.PYLONCRAFT),
	IRID("Iridescent Crystal",		ChromaStacks.iridCrystal,								ResearchLevel.MULTICRAFT,	ProgressStage.ALLOY),
	ORES("Buried Secrets",			ChromaStacks.bindingCrystal,							ResearchLevel.RUNECRAFT),
	CRYSTALSTONE("Crystal Stone",	ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 			ResearchLevel.BASICCRAFT),
	SEED("Crystal Seeds",			ChromaItems.SEED.getStackOf(CrystalElement.MAGENTA),	ResearchLevel.RAWEXPLORE,	ProgressStage.CRYSTALS),
	FRAGMENT("Fragments",			ChromaItems.FRAGMENT, 									ResearchLevel.ENTRY),
	AUGMENT("Upgrades",				ChromaStacks.speedUpgrade,								ResearchLevel.PYLONCRAFT,	ProgressStage.STORAGE),
	ALLOYS("Alloying",				ChromaStacks.chromaIngot,								ResearchLevel.RUNECRAFT,	ProgressionManager.instance.getPrereqsArray(ProgressStage.ALLOY)),
	BEES("Crystal Bees",			new ItemStack(Blocks.dirt),								ResearchLevel.RAWEXPLORE,	ProgressStage.HIVE),

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
	WARPLOC(		Chromabilities.TELEPORT,					ResearchLevel.CTM),
	LEECH(			Chromabilities.LEECH),
	FLOAT(			Chromabilities.FLOAT),
	SPAWNERSEE(		Chromabilities.SPAWNERSEE,					ResearchLevel.ENDGAME),
	BREADCRUMB(		Chromabilities.BREADCRUMB),
	RANGEBOOST(		Chromabilities.RANGEDBOOST),
	DIMPING(		Chromabilities.DIMPING,						ResearchLevel.ENDGAME),
	DASH(			Chromabilities.DASH),
	LASERBILITY(	Chromabilities.LASER,						ResearchLevel.CTM),
	FIRERAIN(		Chromabilities.FIRERAIN,					ResearchLevel.CTM),
	KEEPINV(		Chromabilities.KEEPINV,						ResearchLevel.ENDGAME),
	ORECLIP(		Chromabilities.ORECLIP,						ResearchLevel.CTM),
	DOUBLECRAFT(	Chromabilities.DOUBLECRAFT,					ResearchLevel.CTM),
	GROWAURA(		Chromabilities.GROWAURA,					ResearchLevel.CTM),
	RECHARGE(		Chromabilities.RECHARGE,					ResearchLevel.CTM),
	MEINV(			Chromabilities.MEINV,						ResearchLevel.ENDGAME),
	MOBSEEK(		Chromabilities.MOBSEEK,						ResearchLevel.CTM),

	STRUCTUREDESC("Structures", ""),
	PYLON(			Structures.PYLON,		5,	ResearchLevel.ENERGYEXPLORE,	ProgressStage.PYLON),
	CASTING1(		Structures.CASTING1,	0,	ResearchLevel.BASICCRAFT,		ProgressStage.CRYSTALS),
	CASTING2(		Structures.CASTING2,	1,	ResearchLevel.RUNECRAFT,		ProgressStage.RUNEUSE),
	CASTING3(		Structures.CASTING3,	2,	ResearchLevel.NETWORKING,		ProgressStage.MULTIBLOCK),
	RITUAL	(		Structures.RITUAL,		7,	ResearchLevel.CHARGESELF,		ProgressStage.CHARGE),
	INFUSION(		Structures.INFUSION,	12,	ResearchLevel.MULTICRAFT,		ProgressStage.CHROMA),
	TREE(			Structures.TREE,		14,	ResearchLevel.PYLONCRAFT,		ProgressStage.LINK),
	REPEATERSTRUCT(	Structures.REPEATER,	0,	ResearchLevel.ENERGYEXPLORE,	ProgressStage.RUNEUSE),
	COMPOUNDSTRUCT(	Structures.COMPOUND,	13,	ResearchLevel.NETWORKING,		ProgressStage.MULTIBLOCK),
	CAVERN(			Structures.CAVERN,		0,	ResearchLevel.RAWEXPLORE,		ProgressStage.CAVERN),
	BURROW(			Structures.BURROW,		0,	ResearchLevel.RAWEXPLORE,		ProgressStage.BURROW),
	OCEAN(			Structures.OCEAN,		0,	ResearchLevel.RAWEXPLORE,		ProgressStage.OCEAN),
	DESERT(			Structures.DESERT,		0,	ResearchLevel.RAWEXPLORE,		ProgressStage.DESERTSTRUCT),
	PORTALSTRUCT(	Structures.PORTAL,		0,	ResearchLevel.ENDGAME,			ProgressionManager.instance.getPrereqsArray(ProgressStage.DIMENSION)),
	MINIPYLON(		Structures.PERSONAL,	9,	ResearchLevel.CHARGESELF,		ProgressStage.CHARGE),
	BROADCASTER(	Structures.BROADCAST,	15,	ResearchLevel.NETWORKING,		ProgressStage.MULTIBLOCK),
	CLOAKTOWER(		Structures.CLOAKTOWER,	3,	ResearchLevel.MULTICRAFT,		ProgressStage.KILLMOB),
	BOOSTTREE(		Structures.TREE_BOOSTED,14,	ResearchLevel.CTM,				ProgressStage.TURBOCHARGE),
	BEACONSTRUCT(	Structures.PROTECT,		6,	ResearchLevel.ENDGAME),
	MINIREPEATER(	Structures.WEAKREPEATER,Blocks.log, 0, ResearchLevel.ENERGYEXPLORE, ProgressStage.PYLON),
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

	private int sectionIndex = 0;

	public static final ChromaResearch[] researchList = values();
	static final MultiMap<ResearchLevel, ChromaResearch> levelMap = new MultiMap();
	private static final ItemHashMap<ChromaResearch> itemMap = new ItemHashMap();
	private static final List<ChromaResearch> parents = new ArrayList();
	private static final List<ChromaResearch> nonParents = new ArrayList();
	private static final List<ChromaResearch> obtainable = new ArrayList();
	private static final HashMap<String, ChromaResearch> byName = new HashMap();
	private static final IdentityHashMap<Object, ChromaResearch> duplicateChecker = new IdentityHashMap();

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
		if (rl == ResearchLevel.PYLONCRAFT) {
			for (ProgressStage ps : p) {
				if (ps.isGatedAfter(ProgressStage.DIMENSION)) {
					throw new RegistrationException(ChromatiCraft.instance, "Ability fragment "+c+" gated behind "+ps+" but is only level "+rl+"!");
				}
			}
		}
		progress = p.toArray(new ProgressStage[p.size()]);
		level = rl;
		ability = c;
	}

	private ChromaResearch(Structures s, int meta, ResearchLevel r, ProgressStage... p) {
		this(s, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), meta, r, p);
	}

	private ChromaResearch(Structures s, Block b, int meta, ResearchLevel r, ProgressStage... p) {
		iconItem = new ItemStack(b, 1, meta);
		pageTitle = s.getDisplayName();
		progress = p;
		level = r;
		struct = s;
	}

	public int sectionIndex() {
		return sectionIndex;
	}

	public boolean isAlwaysPresent() {
		return this == START || this == LEXICON;
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

	@SideOnly(Side.CLIENT)
	private ItemStack getTabIcon() {
		if (this == BEES) {
			return CrystalBees.getCrystalBee().getBeeItem(Minecraft.getMinecraft().theWorld, EnumBeeType.QUEEN);
		}
		if (this == ENDERCRYS) {
			return item.getStackOfMetadata(1);
		}
		if (iconItem.stackTagCompound == null)
			iconItem.stackTagCompound = new NBTTagCompound();
		iconItem.stackTagCompound.setBoolean("tooltip", true);
		return iconItem;
	}

	@SideOnly(Side.CLIENT)
	public void renderIcon(RenderItem ri, FontRenderer fr, int x, int y) {
		this.drawTabIcon(ri, x, y);
	}

	@SideOnly(Side.CLIENT)
	public void drawTabIcon(RenderItem ri, int x, int y) {
		if (this == START) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glColor4f(1, 1, 1, 1);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y+1, ChromaIcons.QUESTION.getIcon(), 16, 14);
			GL11.glPopAttrib();
			return;
		}
		else if (this == BALLLIGHTNING) {
			EntityBallLightning eb = new EntityBallLightning(Minecraft.getMinecraft().theWorld);
			eb.isDead = true;
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glPushMatrix();
			double d = 8;
			GL11.glTranslated(x+d, y+d, 0);
			double s = 18;
			GL11.glScaled(-s, s, 1);
			ReikaEntityHelper.getEntityRenderer(EntityBallLightning.class).doRender(eb, 0, 0, 0, 0, 0);
			GL11.glPopMatrix();
			return;
		}
		else if (this == PACKCHANGES) {
			ReikaTextureHelper.bindTerrainTexture();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y+1, ChromaIcons.QUESTION.getIcon(), 16, 14);
			return;
		}
		else if (this == NODENET) {
			ItemStack is = ThaumItemHelper.BlockEntry.NODE.getItem();
			GL11.glPushMatrix();
			double s = 2;
			GL11.glTranslated(x-8, y-6, 0);
			GL11.glScaled(s, s, 1);
			ReikaGuiAPI.instance.drawItemStack(ri, is, 0, 0);
			GL11.glPopMatrix();
			return;
		}
		else if (this == TURBO) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			ReikaTextureHelper.bindTerrainTexture();
			BlendMode.ADDITIVEDARK.apply();
			GL11.glRotated(0, 0, 0, 1);
			int ds = 4;
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x-ds, y-ds, ChromaIcons.TURBO.getIcon(), 16+ds*2, 16+ds*2);

			ds = -1;
			float f = 0.5F;
			GL11.glColor4f(f, f, f, f);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x-ds, y-ds, ChromaIcons.RADIATE.getIcon(), 16+ds*2, 16+ds*2);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glPopMatrix();
			GL11.glPopAttrib();
			return;
		}
		else if (this == APIRECIPES) {
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
			for (CastingRecipe cr : RecipesCastingTable.instance.getAllModdedItemRecipes()) {
				if (!ReikaItemHelper.collectionContainsItemStack(ico, cr.getOutput()))
					ico.add(cr.getOutput());
			}
			if (!ico.isEmpty()) {
				int idx = (int)((System.currentTimeMillis()/400)%ico.size());
				ReikaGuiAPI.instance.drawItemStack(ri, ReikaItemHelper.getSizedItemStack(ico.get(idx), 1), x, y);
			}
			else {
				ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, ChromaIcons.NOENTER.getIcon(), 16, 16);
			}
			return;
		}
		float zp = ri.zLevel;
		if (this.isUnloadable()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glColor4f(1, 1, 1, 0.75F);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, ChromaIcons.NOENTER.getIcon(), 16, 16);
			GL11.glPopAttrib();
			ri.zLevel = 0;
		}
		ReikaGuiAPI.instance.drawItemStack(ri, this.getTabIcon(), x, y);
		ri.zLevel = zp;
	}

	public boolean isUnloadable() {
		if (!ChromatiCraft.instance.isDimensionLoadable()) {
			if (this == DIMENSION || this == DIMENSION2 || this == PORTAL || this == PORTALSTRUCT) {
				return true;
			}
		}
		return false;
	}

	public String getData() {
		if (this == PACKCHANGES) {
			return "These are changes made to the way the mod works by the creator of the pack. None of these are normal " +
					"behavior of the mod, and any negative effects of these changes should be discussed with the pack creator, not " +
					"the mod developer.";
		}
		return ChromaDescriptions.getData(this);
	}

	public String getNotes(int subpage) {
		if (this == PACKCHANGES) {
			return PackModificationTracker.instance.getModifications(ChromatiCraft.instance).get(subpage-1).toString();
		}
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
		if (this == BEES)
			return false;
		if (this == BALLLIGHTNING)
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
		if (this == TURBOREPEATER)
			return true;
		if (this.isMachine() || this.isTool())
			return true;
		if (this == GROUPS)
			return true;
		if (this == CORES)
			return true;
		if (this == HICORES)
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
		if (this == PORTAL)
			return true;
		if (this == HEATLAMP)
			return true;
		if (this == COLORALTAR)
			return true;
		if (this == DOOR)
			return true;
		if (this == GLASS)
			return true;
		if (this == MUSICTRIGGER)
			return true;
		if (this == SELECTIVEGLASS)
			return true;
		return false;
	}

	public boolean isTool() {
		return this.getParent() == TOOLDESC;
	}

	public ArrayList<ItemStack> getItemStacks() {
		if (this.isMachine())
			return ReikaJavaLibrary.makeListFrom(machine.getCraftedProduct());
		if (this == TURBOREPEATER) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaStacks.turboRepeater);
			li.add(ChromaStacks.turboMultiRepeater);
			li.add(ChromaStacks.turboBroadcastRepeater);
			return li;
		}
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
		if (this == ENDERCRYS) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaItems.ENDERCRYSTAL.getStackOfMetadata(0));
			li.add(ChromaItems.ENDERCRYSTAL.getStackOfMetadata(1));
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
			if (item.getItemInstance() instanceof ItemCrystalBasic) {
				ArrayList<ItemStack> li = new ArrayList();
				for (int i = 0; i < 16; i++) {
					li.add(item.getStackOfMetadata(i));
				}
				return li;
			}
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
			li.add(ChromaStacks.spaceDust);
			li.add(ChromaStacks.resocrystal);
			li.add(ChromaStacks.lumaDust);
			li.add(ChromaStacks.echoCrystal);
			li.add(ChromaStacks.fireEssence);
			li.add(ChromaStacks.thermiticCrystal);
			return li;
		}
		if (this == DUSTS) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaStacks.auraDust);
			li.add(ChromaStacks.purityDust);
			li.add(ChromaStacks.elementDust);
			li.add(ChromaStacks.beaconDust);
			li.add(ChromaStacks.resonanceDust);
			li.add(ChromaStacks.teleDust);
			li.add(ChromaStacks.icyDust);
			li.add(ChromaStacks.etherBerries);
			li.add(ChromaStacks.energyPowder);
			li.add(ChromaStacks.livingEssence);
			li.add(ChromaStacks.voidDust);
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
		if (this == HICORES) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaStacks.energyCoreHigh);
			li.add(ChromaStacks.transformCoreHigh);
			li.add(ChromaStacks.voidCoreHigh);
			return li;
		}
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
		if (this == BEES) {
			ArrayList<ItemStack> li = new ArrayList();
			World world = ReikaWorldHelper.getBasicReferenceWorld();

			li.add(CrystalBees.getCrystalBee().getBeeItem(world, EnumBeeType.DRONE));
			li.add(CrystalBees.getCrystalBee().getBeeItem(world, EnumBeeType.PRINCESS));
			li.add(CrystalBees.getCrystalBee().getBeeItem(world, EnumBeeType.QUEEN));

			li.add(CrystalBees.getPureBee().getBeeItem(world, EnumBeeType.DRONE));
			li.add(CrystalBees.getPureBee().getBeeItem(world, EnumBeeType.PRINCESS));
			li.add(CrystalBees.getPureBee().getBeeItem(world, EnumBeeType.QUEEN));

			for (int i = 0; i < 16; i++) {
				CrystalElement e = CrystalElement.elements[i];
				li.add(CrystalBees.getElementalBee(e).getBeeItem(world, EnumBeeType.DRONE));
				li.add(CrystalBees.getElementalBee(e).getBeeItem(world, EnumBeeType.PRINCESS));
				li.add(CrystalBees.getElementalBee(e).getBeeItem(world, EnumBeeType.QUEEN));
			}

			return li;
		}
		if (block != null) {
			Item item = Item.getItemFromBlock(block.getBlockInstance());
			ArrayList<ItemStack> li = new ArrayList();
			if (item instanceof ItemBlockDyeTypes || item instanceof ItemBlockCrystalColors || item instanceof ItemBlockCrystal) {
				for (int i = 0; i < 16; i++) {
					li.add(block.getStackOfMetadata(i));
				}
			}
			else {
				li.add(block.getStackOf());
			}
			return li;
		}
		if (this == FENCEAUX || this == TNT || this == TANKAUX)
			return ReikaJavaLibrary.makeListFrom(iconItem);
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
			HashSet<CastingRecipe> li = new HashSet();
			li.addAll(RecipesCastingTable.instance.getAllAPIRecipes());
			li.addAll(RecipesCastingTable.instance.getAllModdedItemRecipes());
			return new ArrayList(li);
		}
		if (this == TURBOREPEATER) {
			ArrayList<CastingRecipe> li = new ArrayList();
			for (CastingRecipe cr : RecipesCastingTable.instance.getAllRecipes()) {
				if (cr instanceof RepeaterTurboRecipe) {
					li.add(cr);
				}
			}
			return li;
		}
		if (!this.isCrafting())
			return new ArrayList();
		ArrayList<ItemStack> li = this.getItemStacks();
		if (li == null || li.isEmpty())
			return new ArrayList();
		ArrayList<CastingRecipe> rec = new ArrayList();
		for (ItemStack is : li) {
			Collection<CastingRecipe> cr = RecipesCastingTable.instance.getAllRecipesMaking(is);
			for (CastingRecipe c : cr) {
				if (c.isIndexed())
					rec.add(c);
			}
		}
		return rec;
	}

	public int getRecipeIndex(ItemStack is) {
		if (this == ALLOYS) {
			return new ArrayList(PoolRecipes.instance.getAllPoolRecipes()).indexOf(PoolRecipes.instance.getPoolRecipeByOutput(is));
		}
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
		if (this == APIRECIPES && DragonAPICore.hasGameLoaded()) //only hide display
			return RecipesCastingTable.instance.getAllAPIRecipes().isEmpty() && RecipesCastingTable.instance.getAllModdedItemRecipes().isEmpty();
		if (this == PACKCHANGES && !PackModificationTracker.instance.modificationsExist(ChromatiCraft.instance))
			return true;
		Dependency dep = this.getDependency();
		if (dep != null && !dep.isLoaded())
			return true;
		return false;
	}

	public Dependency getDependency() {
		switch(this) {
			case BEES:
				return ModList.FORESTRY;
			case RFDISTRIB:
				return PowerTypes.RF;
			case BALLLIGHTNING:
				return ChromaOptions.BALLLIGHTNING;
			case NODENET:
				return ModList.THAUMCRAFT;
			case MYSTPAGE:
				return ModList.MYSTCRAFT;
			default:
				return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getShortDesc() {
		return "Something new to investigate";//"A new item to investigate";
	}

	@Override
	public String getFormatting() {
		return EnumChatFormatting.ITALIC.toString();
	}

	private Object getIDObject() {
		if (machine != null)
			return machine;
		else if (block != null)
			return block;
		else if (item != null)
			return item;
		else if (ability != null)
			return ability;
		else if (struct != null)
			return struct;
		else
			return this.ordinal(); //will never conflict
	}

	static {
		int index = 0;
		for (int i = 0; i < researchList.length; i++) {
			ChromaResearch r = researchList[i];
			if (!r.isDummiedOut()) {
				if (r.level != null)
					levelMap.addValue(r.level, r);
				byName.put(r.name(), r);
				if (r.isParent) {
					parents.add(r);
					index++;
				}
				else {
					nonParents.add(r);
					if (!r.isAlwaysPresent()) {
						obtainable.add(r);
					}
					r.sectionIndex = index;
				}
			}
			ChromaResearch pre = duplicateChecker.get(r.getIDObject());
			if (pre != null)
				throw new RegistrationException(ChromatiCraft.instance, "Two research fragments have the same block/item/ability/etc: "+r+" & "+pre);
			duplicateChecker.put(r.getIDObject(), r);
			ChromaResearchManager.instance.register(r);
		}
	}

	public static void loadPostCache() {
		for (int i = 0; i < researchList.length; i++) {
			ChromaResearch r = researchList[i];
			if (!r.isDummiedOut()) {
				try {
					Collection<ItemStack> c = r.getItemStacks();
					if (c != null) {
						for (ItemStack is : c) {
							if (is != null && is.getItem() != null)
								itemMap.put(is, r);
						}
					}
					Collection<CastingRecipe> crc = r.getCraftingRecipes();
					for (CastingRecipe cr : crc) {
						cr.setFragment(r);
					}
				}
				catch (Exception e) {
					Dependency dep = r.getDependency();
					if (dep != null && !(dep instanceof ConfigList)) {
						throw new InstallationException(ChromatiCraft.instance, "Another mod/API, '"+dep.getDisplayName()+"' is an incompatible version. Update both mods if possible, or if updating "+dep.getDisplayName()+" caused this, revert to the previous version.");
					}
					else {
						throw new RegistrationException(ChromatiCraft.instance, "Could not initialize Info Fragment '"+r+"'!", e);
					}
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

	/** All nonparent, minus the ones always present */
	public static List<ChromaResearch> getAllObtainableFragments() {
		return Collections.unmodifiableList(obtainable);
	}

	public static ChromaResearch getByName(String s) {
		return byName.get(s);
	}

}
