/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
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

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.Interfaces.DynamicallyGeneratedSubpage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.ChargedItemPlayerBufferConnectionRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.RepeaterTurboRecipe;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Block.Crystal.BlockCrystalGlow.Bases;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.GUI.Book.GuiMachineDescription;
import Reika.ChromatiCraft.Items.ItemMagicBranch;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystal;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystalColors;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockDyeTypes;
import Reika.ChromatiCraft.Magic.ToolChargingSystem;
import Reika.ChromatiCraft.Magic.Interfaces.PoweredItem;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem;
import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem.FragmentCategory;
import Reika.ChromatiCraft.Magic.Progression.ProgressAccess;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.ChromatiCraft.Render.TESR.RenderDataNode;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal.CrystalTier;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Registry.Dependency;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.ToolParts;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.WeaponParts;
import Reika.DragonAPI.ModRegistry.PowerTypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeType;

public enum ChromaResearch implements ProgressElement, ProgressAccess {

	//---------------------INFO--------------------//
	INTRO("Introduction", ""),
	START("Getting Started",				new ItemStack(Blocks.dirt),								ResearchLevel.ENTRY),
	LEXICON("The Lexicon",					ChromaItems.HELP.getStackOf(),							ResearchLevel.ENTRY),
	ENERGY("Crystal Energy", 				new ItemStack(Blocks.dirt),								ResearchLevel.ENERGY,		ProgressStage.ALLCOLORS, ProgressStage.CHARGE),
	ELEMENTS("Crystal Elements", 			ChromaItems.ELEMENTAL.getStackOf(CrystalElement.BLUE),	ResearchLevel.BASICCRAFT,	ProgressStage.ALLCOLORS),
	CRYSTALS("Crystals", 					ChromaBlocks.CRYSTAL.getStackOfMetadata(4), 			ResearchLevel.ENTRY, 		ProgressStage.CRYSTALS),
	PYLONS("Pylons", 						ChromaTiles.PYLON.getCraftedProduct(), 					ResearchLevel.ENTRY, 		ProgressStage.PYLON),
	STRUCTURES("Scattered Structures",		ChromaBlocks.PYLONSTRUCT.getStackOf(),					ResearchLevel.RAWEXPLORE),
	TRANSMISSION("Signal Transmission", 	ChromaStacks.beaconDust, 								ResearchLevel.ENERGY),
	CRAFTING("Casting",						ChromaTiles.TABLE.getCraftedProduct(),					ResearchLevel.BASICCRAFT),
	ENCHANTS("Enchantments",				ChromaTiles.ENCHANTER.getCraftedProduct(),				ResearchLevel.BASICCRAFT),
	CRAFTING2("Casting II",					ChromaTiles.TABLE.getCraftedProduct(),					ResearchLevel.RUNECRAFT,	ProgressStage.RUNEUSE),
	BALLLIGHTNING("Ball Lightning",			ChromaStacks.auraDust,									ResearchLevel.ENERGY,		ProgressStage.BALLLIGHTNING),
	APIRECIPES("Other Recipes",				new ItemStack(Blocks.dirt),								ResearchLevel.BASICCRAFT),
	LEYLINES("Ley Lines",					ChromaTiles.REPEATER.getCraftedProduct(),				ResearchLevel.NETWORKING,	ProgressStage.REPEATER),
	USINGRUNES("Crafting With Runes",		ChromaBlocks.RUNE.getStackOfMetadata(1),				ResearchLevel.RUNECRAFT, 	ProgressStage.RUNEUSE),
	DIMENSION("Another World",				ChromaBlocks.PORTAL.getStackOf(),						ResearchLevel.ENDGAME,		ProgressionManager.instance.getPrereqsArray(ProgressStage.DIMENSION)),
	DIMENSION2("A Volatile World",			ChromaBlocks.GLOWSAPLING.getStackOf(),					ResearchLevel.ENDGAME,		ProgressStage.DIMENSION),
	FARLANDS("The Far Regions",				DimDecoTypes.FLOATSTONE.getItem(),						ResearchLevel.ENDGAME,		ProgressStage.FARLANDS),
	TURBO("Turbocharging",					ChromaStacks.elementUnit,								ResearchLevel.ENDGAME, 		ProgressionManager.instance.getPrereqsArray(ProgressStage.TURBOCHARGE)),
	TURBOREPEATER("Repeater Turbocharging", ChromaStacks.turboRepeater,								ResearchLevel.ENDGAME,		ProgressStage.TURBOCHARGE),
	TURBOREPEATER2("Maximized Networking",	StoneTypes.RESORING.getItem(),							ResearchLevel.CTM,			ProgressStage.TURBOCHARGE),
	PACKCHANGES("Modpack Changes",			new ItemStack(Blocks.dirt),								ResearchLevel.ENTRY),
	NODENET("Networking Aura Nodes",		new ItemStack(Blocks.dirt),								ResearchLevel.CTM,			ProgressStage.CTM),
	SELFCHARGE("Energy Internalization",	ChromaItems.TOOL.getStackOf(),							ResearchLevel.ENERGY,		ProgressStage.CHARGE),
	MYSTPAGE("World Authoring",				new ItemStack(Items.map),								ResearchLevel.RAWEXPLORE),
	ENCHANTING("Crystal Enchanting",		new ItemStack(Items.enchanted_book),					ResearchLevel.MULTICRAFT,	ProgressStage.MULTIBLOCK),
	STRUCTUREPASSWORDS("Structure Keys",	ChromaBlocks.DIMDATA.getStackOfMetadata(1),				ResearchLevel.ENDGAME,		ProgressStage.STRUCTCOMPLETE),
	DIMTUNING("Portal Tuning",				ChromaStacks.bedrockloot2,								ResearchLevel.ENDGAME,		ProgressStage.DIMENSION),
	ABILITIES("Abilities",					ChromaTiles.RITUAL.getCraftedProduct(),					ResearchLevel.ENERGY),
	CASTTUNING("Personalized Casting",		new ItemStack(Blocks.skull, 1, 3),						ResearchLevel.NETWORKING,	ProgressionManager.instance.getPrereqsArray(ProgressStage.TUNECAST)),
	MULTIBLOCKS("Functional Construction",	StoneTypes.COLUMN.getItem(),							ResearchLevel.BASICCRAFT),
	PYLONLINK("Interconnected Energy",		ChromaTiles.PYLONLINK.getCraftedProduct(),				ResearchLevel.ENERGY,		ProgressStage.PYLONLINK),
	ITEMBURNER("Item Decomposition",		new ItemStack(Blocks.dirt),								ResearchLevel.ENERGY,		ProgressStage.CHARGE, ProgressStage.ALLCOLORS),
	MONUMENT("Construction Restoration",	ChromaTiles.AURAPOINT.getCraftedProduct(),				ResearchLevel.CTM,			ProgressStage.CTM),
	MUD("Magical Residue",					ChromaBlocks.MUD.getStackOf(),							ResearchLevel.RAWEXPLORE,	ProgressStage.MUDHINT),
	ITEMCHARGE("Passive Charging",			new ItemStack(Blocks.dirt),								ResearchLevel.BASICCRAFT,	ProgressStage.CHARGE),
	ITEMBUFFERLINK("Lumen Buffer Connection",new ItemStack(Blocks.dirt),							ResearchLevel.PYLONCRAFT,	ProgressStage.CHARGE),
	SKYPEATER(ChromaTiles.SKYPEATER.getName(),ChromaTiles.SKYPEATER.getCraftedProduct(),			ResearchLevel.ENERGY,		ProgressStage.GLOWCLIFFS),
	FRAGMENTSELECT("Fragment Decoding",		new ItemStack(Blocks.dirt),								ResearchLevel.BASICCRAFT,	ProgressStage.CHROMA),

	MACHINEDESC("Constructs", ""),
	REPEATER(		ChromaTiles.REPEATER,		ResearchLevel.NETWORKING,		ProgressStage.BLOWREPEATER),
	GUARDIAN(		ChromaTiles.GUARDIAN, 		ResearchLevel.PYLONCRAFT),
	REPROGRAMMER(	ChromaTiles.REPROGRAMMER, 	ResearchLevel.PYLONCRAFT),
	ACCEL(			ChromaTiles.ADJACENCY, 		ResearchLevel.ENDGAME),
	RIFT(			ChromaTiles.RIFT, 			ResearchLevel.PYLONCRAFT),
	TANK(			ChromaTiles.TANK, 			ResearchLevel.PYLONCRAFT,		ProgressStage.OCEAN),
	COMPOUND(		ChromaTiles.COMPOUND, 		ResearchLevel.NETWORKING,		ProgressStage.REPEATER),
	CHARGER(		ChromaTiles.CHARGER, 		ResearchLevel.MULTICRAFT,		ProgressStage.STORAGE),
	LILY(			ChromaTiles.HEATLILY, 		ResearchLevel.RUNECRAFT),
	TICKER(			ChromaTiles.TICKER, 		ResearchLevel.MULTICRAFT),
	FENCE(			ChromaTiles.FENCE, 			ResearchLevel.MULTICRAFT),
	FURNACE(		ChromaTiles.FURNACE, 		ResearchLevel.PYLONCRAFT,		ProgressStage.MINE),
	TELEPUMP(		ChromaTiles.TELEPUMP, 		ResearchLevel.PYLONCRAFT),
	MINER(			ChromaTiles.MINER, 			ResearchLevel.CTM,				ProgressStage.MINE, ProgressStage.FARLANDS),
	ITEMSTAND(		ChromaTiles.STAND,			ResearchLevel.RUNECRAFT),
	LASER(			ChromaTiles.LASER, 			ResearchLevel.PYLONCRAFT),
	ITEMRIFT(		ChromaTiles.ITEMRIFT, 		ResearchLevel.ENERGY),
	CRYSTAL(		ChromaTiles.CRYSTAL, 		ResearchLevel.ENDGAME),
	INFUSER(		ChromaTiles.INFUSER, 		ResearchLevel.MULTICRAFT),
	FABRICATOR(		ChromaTiles.FABRICATOR, 	ResearchLevel.PYLONCRAFT),
	ENCHANTER(		ChromaTiles.ENCHANTER, 		ResearchLevel.RUNECRAFT),
	CHROMAFLOWER(	ChromaTiles.CHROMAFLOWER, 	ResearchLevel.BASICCRAFT),
	COLLECTOR(		ChromaTiles.COLLECTOR, 		ResearchLevel.BASICCRAFT),
	BREWER(			ChromaTiles.BREWER, 		ResearchLevel.BASICCRAFT,		ProgressStage.POTION),
	RITUALTABLE(	ChromaTiles.RITUAL, 		ResearchLevel.ENERGY),
	CASTTABLE(		ChromaTiles.TABLE, 			ResearchLevel.ENTRY),
	BEACON(			ChromaTiles.BEACON, 		ResearchLevel.ENDGAME),
	ITEMCOLLECTOR(	ChromaTiles.ITEMCOLLECTOR, 	ResearchLevel.PYLONCRAFT),
	AISHUTDOWN(		ChromaTiles.AISHUTDOWN, 	ResearchLevel.MULTICRAFT),
	ASPECT(			ChromaTiles.ASPECT, 		ResearchLevel.ENERGY,			ProgressStage.NODE),
	LAMP(			ChromaTiles.LAMP, 			ResearchLevel.ENERGY),
	POWERTREE(		ChromaTiles.POWERTREE, 		ResearchLevel.ENDGAME,			ProgressStage.POWERCRYSTAL),
	LAMPCONTROL(	ChromaTiles.LAMPCONTROL, 	ResearchLevel.RUNECRAFT),
	BIOMEPAINT(		ChromaTiles.BIOMEPAINTER,	ResearchLevel.ENDGAME,			ProgressStage.RAINBOWFOREST),
	ASPECTJAR(		ChromaTiles.ASPECTJAR,		ResearchLevel.PYLONCRAFT,		ProgressStage.NODE),
	FARMER(			ChromaTiles.FARMER,			ResearchLevel.PYLONCRAFT,		ProgressStage.HARVEST),
	AUTO(			ChromaTiles.AUTOMATOR,		ResearchLevel.ENDGAME),
	MEDISTRIB(		ChromaTiles.MEDISTRIBUTOR,	ResearchLevel.MULTICRAFT),
	WINDOW(			ChromaTiles.WINDOW,			ResearchLevel.MULTICRAFT),
	RFDISTRIB(		ChromaTiles.RFDISTRIBUTOR,	ResearchLevel.RUNECRAFT),
	PERSONAL(		ChromaTiles.PERSONAL,		ResearchLevel.ENERGY,			ProgressStage.CHARGE),
	MUSIC(			ChromaTiles.MUSIC,			ResearchLevel.BASICCRAFT),
	PYLONTURBO(		ChromaTiles.PYLONTURBO,		ResearchLevel.ENDGAME,			ProgressStage.DIMENSION),
	TURRET(			ChromaTiles.TURRET,			ResearchLevel.BASICCRAFT,		ProgressStage.KILLMOB),
	BROADCAST(		ChromaTiles.BROADCAST,		ResearchLevel.NETWORKING,		ProgressStage.REPEATER),
	CLOAKING(		ChromaTiles.CLOAKING,		ResearchLevel.MULTICRAFT,		ProgressStage.KILLMOB),
	CAVELIGHTER(	ChromaTiles.LIGHTER,		ResearchLevel.RUNECRAFT,		ProgressStage.DEEPCAVE),
	GLOWFIRE(		ChromaTiles.GLOWFIRE,		ResearchLevel.ENERGY,			ProgressStage.SHARDCHARGE),
	ESSENTIA(		ChromaTiles.ESSENTIARELAY,	ResearchLevel.RUNECRAFT,		ProgressStage.NODE),
	INSERTER(		ChromaTiles.INSERTER,		ResearchLevel.RUNECRAFT),
	LOTUS(			ChromaTiles.REVERTER, 		ResearchLevel.RUNECRAFT),
	COBBLEGEN(		ChromaTiles.COBBLEGEN,		ResearchLevel.RUNECRAFT),
	PLANTACCEL(		ChromaTiles.PLANTACCEL,		ResearchLevel.RUNECRAFT),
	CROPSPEED(		ChromaTiles.CROPSPEED,		ResearchLevel.RUNECRAFT,		ProgressStage.HARVEST),
	WEAKREPEATER(	ChromaTiles.WEAKREPEATER,	ResearchLevel.ENERGY,			ProgressStage.PYLON, ProgressStage.MAKECHROMA),
	ENCHANTDECOMP(	ChromaTiles.ENCHANTDECOMP,	ResearchLevel.ENERGY,			ProgressStage.MAKECHROMA),
	LUMENWIRE(		ChromaTiles.LUMENWIRE,		ResearchLevel.BASICCRAFT),
	PARTICLES(		ChromaTiles.PARTICLES,		ResearchLevel.BASICCRAFT),
	METEOR(			ChromaTiles.METEOR,			ResearchLevel.PYLONCRAFT),
	FLUIDDISTRIB(	ChromaTiles.FLUIDDISTRIBUTOR,ResearchLevel.RUNECRAFT),
	VILLAGEREPAIR(	ChromaTiles.VILLAGEREPAIR,	ResearchLevel.MULTICRAFT,		ProgressStage.VILLAGECASTING),
	AREABREAKER(	ChromaTiles.AREABREAKER,	ResearchLevel.MULTICRAFT),
	WIRELESS(		ChromaTiles.WIRELESS,		ResearchLevel.MULTICRAFT,		ProgressStage.USEENERGY),
	GATE(			ChromaTiles.TELEPORT,		ResearchLevel.ENDGAME,			ProgressStage.END),
	FLUIDRELAY(		ChromaTiles.FLUIDRELAY,		ResearchLevel.RUNECRAFT),
	BOOKDECOMP(		ChromaTiles.BOOKDECOMP,		ResearchLevel.ENERGY,			ProgressStage.MYST),
	PLANTHARVEST(	ChromaTiles.HARVESTPLANT,	ResearchLevel.RUNECRAFT,		ProgressStage.HARVEST),
	AVOLASER(		ChromaTiles.AVOLASER,		ResearchLevel.ENDGAME, 			TieredOres.AVOLITE.level),
	ALVEARY(		ChromaTiles.ALVEARY,		ResearchLevel.PYLONCRAFT,		ProgressStage.HIVE),
	ROUTER(			ChromaTiles.ROUTERHUB,		ResearchLevel.MULTICRAFT),
	FOCUSCRYSTALS(	ChromaTiles.FOCUSCRYSTAL,	ResearchLevel.ENERGY,			ProgressStage.FOCUSCRYSTAL),
	FLUXMAKER(		ChromaTiles.FLUXMAKER,		ResearchLevel.ENERGY),
	FUNCRELAY(		ChromaTiles.FUNCTIONRELAY,	ResearchLevel.RUNECRAFT),
	CHROMACRAFTER(	ChromaTiles.CHROMACRAFTER,	ResearchLevel.PYLONCRAFT,		ProgressStage.ALLOY),
	MULTIBUILDER(	ChromaTiles.MULTIBUILDER,	ResearchLevel.MULTICRAFT),
	EXPLOSIONSHIELD(ChromaTiles.EXPLOSIONSHIELD,ResearchLevel.PYLONCRAFT),
	AURALOCUS(		ChromaTiles.AURAPOINT,		ResearchLevel.CTM),
	PROGRESSLINK(	ChromaTiles.PROGRESSLINK,	ResearchLevel.BASICCRAFT),
	MANABOOSTER(	ChromaTiles.MANABOOSTER,	ResearchLevel.PYLONCRAFT),
	NETWORKOPT(		ChromaTiles.OPTIMIZER,		ResearchLevel.ENDGAME,			TieredOres.LUMA.level),
	LANDMARK(		ChromaTiles.LANDMARK,		ResearchLevel.BASICCRAFT),
	INJECTOR(		ChromaTiles.INJECTOR,		ResearchLevel.MULTICRAFT),
	HOVERPAD(		ChromaTiles.HOVERPAD,		ResearchLevel.RUNECRAFT/*,		TieredOres.TELEPORT.level*/),
	DEATHFOG(		ChromaTiles.DEATHFOG,		ResearchLevel.RUNECRAFT,		ProgressStage.VOIDMONSTER),
	VOIDTRAP(		ChromaTiles.VOIDTRAP,		ResearchLevel.PYLONCRAFT,		ProgressStage.VOIDMONSTERDIE, ProgressStage.CHARGECRYSTAL),
	PLAYERINFUSER(	ChromaTiles.PLAYERINFUSER,	ResearchLevel.MULTICRAFT,		ProgressStage.INFUSE),
	SMELTERY(		ChromaTiles.SMELTERYDISTRIBUTOR,ResearchLevel.RUNECRAFT),
	LAUNCHPAD(		ChromaTiles.LAUNCHPAD,		ResearchLevel.BASICCRAFT),
	TOOLSTORAGE(	ChromaTiles.TOOLSTORAGE,	ResearchLevel.BASICCRAFT),
	BEESTORAGE(		ChromaTiles.BEESTORAGE,		ResearchLevel.RUNECRAFT,		ProgressStage.HIVE),
	NETWORKTRANSPORT(ChromaTiles.NETWORKITEM,	ResearchLevel.PYLONCRAFT),

	BLOCKS("Other Blocks", ""),
	RUNES(			ChromaBlocks.RUNE,			CrystalElement.LIGHTBLUE.ordinal(),	ResearchLevel.BASICCRAFT,	ProgressStage.ALLCOLORS),
	CHROMA(			ChromaBlocks.CHROMA,											ResearchLevel.RAWEXPLORE),
	HEATLAMP(		ChromaBlocks.HEATLAMP,											ResearchLevel.RUNECRAFT,	ProgressStage.NETHER),
	TNT(			ChromaBlocks.TNT,												ResearchLevel.PYLONCRAFT),
	TANKAUX(		ChromaBlocks.TANK,												ResearchLevel.MULTICRAFT),
	FENCEAUX(		ChromaBlocks.FENCE,												ResearchLevel.MULTICRAFT),
	LUMENLEAVES(	ChromaBlocks.POWERTREE,		CrystalElement.LIME.ordinal(),		ResearchLevel.ENDGAME,		ProgressStage.POWERCRYSTAL),
	DYELEAVES(		ChromaBlocks.DYELEAF,		CrystalElement.BROWN.ordinal(),		ResearchLevel.ENTRY,		ProgressStage.DYETREE),
	RAINBOWLEAVES(	ChromaBlocks.RAINBOWLEAF,	3,									ResearchLevel.RAWEXPLORE,	ProgressStage.RAINBOWLEAF),
	LAMPAUX(		ChromaBlocks.LAMPBLOCK,		CrystalElement.WHITE.ordinal(),		ResearchLevel.RUNECRAFT),
	CRYSTALLAMP(	ChromaBlocks.LAMP,			CrystalElement.YELLOW.ordinal(),	ResearchLevel.RAWEXPLORE),
	SUPERLAMP(		ChromaBlocks.SUPER,			CrystalElement.MAGENTA.ordinal(),	ResearchLevel.PYLONCRAFT,	ProgressStage.POTION),
	PATH(			ChromaBlocks.PATH,												ResearchLevel.RUNECRAFT),
	GLOW(			ChromaBlocks.GLOW,			CrystalElement.RED.ordinal(),		ResearchLevel.BASICCRAFT),
	RELAY(			ChromaBlocks.RELAY,												ResearchLevel.ENERGY),
	PORTAL(			ChromaBlocks.PORTAL,											ResearchLevel.ENDGAME,		ProgressionManager.instance.getPrereqsArray(ProgressStage.DIMENSION)),
	COLORALTAR(		ChromaBlocks.COLORALTAR,	CrystalElement.WHITE.ordinal(),		ResearchLevel.ENERGY),
	DOOR(			ChromaBlocks.DOOR,												ResearchLevel.BASICCRAFT),
	GLASS(			ChromaBlocks.GLASS,			CrystalElement.BLUE.ordinal(),		ResearchLevel.BASICCRAFT),
	MUSICTRIGGER(	ChromaBlocks.MUSICTRIGGER,										ResearchLevel.BASICCRAFT,	ProgressStage.ANYSTRUCT),
	SELECTIVEGLASS(	ChromaBlocks.SELECTIVEGLASS,									ResearchLevel.BASICCRAFT),
	AVOLAMP(		ChromaBlocks.AVOLAMP,											ResearchLevel.ENDGAME,		TieredOres.AVOLITE.level),
	REPEATERLAMP(	ChromaBlocks.REPEATERLAMP,										ResearchLevel.NETWORKING,	ProgressStage.REPEATER),
	REDSTONEPOD(	ChromaBlocks.REDSTONEPOD,										ResearchLevel.RUNECRAFT),
	FAKESKY(		ChromaBlocks.FAKESKY,											ResearchLevel.RUNECRAFT),
	CHUNKLOADER(	ChromaBlocks.CHUNKLOADER,										ResearchLevel.ENDGAME,		ProgressStage.DIMENSION),
	LUMA(			ChromaBlocks.LUMA,												ResearchLevel.RAWEXPLORE,	ProgressStage.LUMA),
	ENDER(			ChromaBlocks.ENDER,												ResearchLevel.RAWEXPLORE),
	//SPAWNERCONTROL(	ChromaBlocks.SPAWNERCONTROL,									ResearchLevel.RAWEXPLORE,	ProgressStage.BREAKSPAWNER),
	TRAPFLOOR(		ChromaBlocks.TRAPFLOOR,											ResearchLevel.RAWEXPLORE,	ProgressStage.SNOWSTRUCT),
	WARPNODE(		ChromaBlocks.WARPNODE,											ResearchLevel.RAWEXPLORE,	ProgressStage.WARPNODE),
	RFPOD(			ChromaBlocks.RFPOD,												ResearchLevel.RUNECRAFT),
	ENCRUSTED(		ChromaBlocks.ENCRUSTED,		CrystalElement.RED.ordinal(),		ResearchLevel.RAWEXPLORE,	ProgressStage.PYLON),
	INJECTORAUX(	ChromaBlocks.INJECTORAUX,										ResearchLevel.MULTICRAFT),
	HOVERPADAUX(	ChromaBlocks.PAD,												ResearchLevel.RUNECRAFT/*,	TieredOres.TELEPORT.level*/),

	TOOLDESC("Tools", ""),
	WAND(				ChromaItems.TOOL,			ResearchLevel.ENTRY),
	FINDER(				ChromaItems.FINDER, 		ResearchLevel.BASICCRAFT,		ProgressStage.PYLON),
	EXCAVATOR(			ChromaItems.EXCAVATOR, 		ResearchLevel.ENERGY,			ProgressStage.MINE),
	TRANSITION(			ChromaItems.TRANSITION, 	ResearchLevel.ENERGY),
	INVLINK(			ChromaItems.LINK, 			ResearchLevel.ENERGY),
	PENDANT(			ChromaItems.PENDANT, 		ResearchLevel.ENERGY,			ProgressStage.POTION),
	LENS(				ChromaItems.LENS, 			ResearchLevel.ENERGY),
	STORAGE(			ChromaItems.STORAGE, 		ResearchLevel.ENERGY,			ProgressStage.USEENERGY),
	LINKTOOL(			ChromaItems.LINKTOOL, 		ResearchLevel.RUNECRAFT),
	WARP(				ChromaItems.WARP, 			ResearchLevel.PYLONCRAFT),
	TELEPORT(			ChromaItems.TELEPORT, 		ResearchLevel.MULTICRAFT),
	DUPLICATOR(			ChromaItems.DUPLICATOR, 	ResearchLevel.PYLONCRAFT),
	BUILDER(			ChromaItems.BUILDER, 		ResearchLevel.MULTICRAFT),
	CAPTURE(			ChromaItems.CAPTURE, 		ResearchLevel.MULTICRAFT,		ProgressStage.KILLMOB),
	VOIDCELL(			ChromaItems.VOIDCELL, 		ResearchLevel.ENDGAME),
	AURAPOUCH(			ChromaItems.AURAPOUCH,		ResearchLevel.MULTICRAFT),
	MULTITOOL(			ChromaItems.MULTITOOL,		ResearchLevel.RUNECRAFT,		ProgressStage.MINE),
	OREPICK(			ChromaItems.OREPICK,		ResearchLevel.RUNECRAFT,		ProgressStage.MINE),
	ORESILK(			ChromaItems.ORESILK,		ResearchLevel.RUNECRAFT,		ProgressStage.MINE),
	GROWTH(				ChromaItems.GROWTH,			ResearchLevel.MULTICRAFT,		ProgressStage.HARVEST),
	ENDERCRYS(			ChromaItems.ENDERCRYSTAL,	ResearchLevel.ENDGAME,			ProgressStage.END),
	BULKMOVER(			ChromaItems.BULKMOVER,		ResearchLevel.RUNECRAFT),
	CHAINGUN(			ChromaItems.CHAINGUN,		ResearchLevel.MULTICRAFT,		ProgressStage.KILLMOB),
	HOVER(				ChromaItems.HOVERWAND,		ResearchLevel.PYLONCRAFT,		ProgressStage.KILLMOB),
	SPLASH(				ChromaItems.SPLASHGUN,		ResearchLevel.MULTICRAFT,		ProgressStage.KILLMOB),
	VACUUMGUN(			ChromaItems.VACUUMGUN,		ResearchLevel.ENDGAME,			ProgressStage.DIMENSION),
	DOORKEY(			ChromaItems.KEY,			ResearchLevel.BASICCRAFT),
	OWNERKEY(			ChromaItems.SHARE,			ResearchLevel.BASICCRAFT),
	//FLUIDWAND(			ChromaItems.FLUIDWAND,		ResearchLevel.RUNECRAFT,	ProgressStage.OCEAN),
	CRYSTALCELL(		ChromaItems.CRYSTALCELL,	ResearchLevel.MULTICRAFT,		ProgressStage.CHARGE),
	PURIFY(				ChromaItems.PURIFY,			ResearchLevel.ENDGAME,			ProgressStage.RAINBOWLEAF, ProgressStage.ALLOY),
	EFFICIENCY(			ChromaItems.EFFICIENCY,			ResearchLevel.CTM),
	KILLAURA(			ChromaItems.KILLAURAGUN,	ResearchLevel.ENDGAME,			ProgressStage.KILLMOB),
	FLOATBOOTS(			ChromaItems.FLOATBOOTS,		ResearchLevel.ENDGAME,			ProgressStage.DIMENSION),
	TELECAPSULE(		ChromaItems.WARPCAPSULE,	ResearchLevel.RUNECRAFT,		ProgressStage.DEEPCAVE),
	BEEFRAME(			ChromaItems.BEEFRAME,		ResearchLevel.RUNECRAFT,		ProgressStage.HIVE),
	STRUCTFIND(			ChromaItems.STRUCTUREFINDER,ResearchLevel.RUNECRAFT,		ProgressStage.RUNEUSE, ProgressStage.ANYSTRUCT),
	MOBSONAR(			ChromaItems.MOBSONAR,		ResearchLevel.RUNECRAFT,		ProgressStage.KILLMOB, ProgressStage.CHARGE),
	CAVEEXIT(			ChromaItems.CAVEPATHER,		ResearchLevel.BASICCRAFT,		ProgressStage.DEEPCAVE),
	SPLINEATTACK(		ChromaItems.SPLINEATTACK,	ResearchLevel.RUNECRAFT,		ProgressStage.KILLMOB),
	SHIELDEDCELL(		ChromaItems.SHIELDEDCELL,	ResearchLevel.BASICCRAFT,		ProgressStage.ARTEFACT),
	BOTTLENECK(			ChromaItems.BOTTLENECK,		ResearchLevel.NETWORKING,		ProgressStage.REPEATER),
	SPAWNERBYPASS(		ChromaItems.SPAWNERBYPASS,	ResearchLevel.ENERGY,			ProgressStage.FINDSPAWNER),
	ENDEREYE(			ChromaItems.ENDEREYE,		ResearchLevel.ENERGY,			ProgressStage.NETHERROOF),
	LIGHTGUN(			ChromaItems.LIGHTGUN,		ResearchLevel.BASICCRAFT,		ProgressStage.DEEPCAVE),
	PROBE(				ChromaItems.PROBE,			ResearchLevel.BASICCRAFT),
	TELEGATELOCK(		ChromaItems.TELEGATELOCK,	ResearchLevel.ENDGAME),
	ENDERBUCKET(		ChromaItems.ENDERBUCKET,	ResearchLevel.RUNECRAFT,		ProgressStage.END),
	WIDECOLLECTOR(		ChromaItems.WIDECOLLECTOR,	ResearchLevel.MULTICRAFT),
	NETHERKEY(			ChromaItems.NETHERKEY,		ResearchLevel.RUNECRAFT,		ProgressStage.NETHER, ProgressStage.ANYSTRUCT),
	RECIPECACHE(		ChromaItems.RECIPECACHE,	ResearchLevel.MULTICRAFT,		ProgressStage.MULTIBLOCK),
	ETHERPENDANT(		ChromaItems.ETHERPENDANT,	ResearchLevel.MULTICRAFT,		ProgressStage.LUMA, ProgressStage.END),
	STRUCTMAP(			ChromaItems.STRUCTMAP,		ResearchLevel.RAWEXPLORE,		ProgressStage.ANYSTRUCT, ProgressStage.TOWER, ProgressStage.ARTEFACT),

	RESOURCEDESC("Resources", ""),
	BERRIES("Berries",				ChromaItems.BERRY.getStackOf(CrystalElement.ORANGE),	ResearchLevel.RAWEXPLORE,	ProgressStage.DYETREE),
	SHARDS("Shards",				ChromaStacks.redShard, 									ResearchLevel.RAWEXPLORE,	ProgressStage.CRYSTALS),
	DUSTS("Plants",					ChromaStacks.auraDust, 									ResearchLevel.ENERGY),
	GROUPS("Groups",				ChromaStacks.crystalCore, 								ResearchLevel.RUNECRAFT),
	CORES("Cores",					ChromaStacks.energyCore,								ResearchLevel.ENERGY),
	HICORES("Energized Cores",		ChromaStacks.energyCoreHigh,							ResearchLevel.PYLONCRAFT),
	IRID("Iridescent Crystal",		ChromaStacks.iridCrystal,								ResearchLevel.MULTICRAFT,	ProgressStage.ALLOY),
	ORES("Buried Secrets",			ChromaStacks.bindingCrystal,							ResearchLevel.RAWEXPLORE,	ProgressStage.CRYSTALS),
	CRYSTALSTONE("Crystal Stone",	ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 			ResearchLevel.BASICCRAFT),
	SEED("Crystal Seeds",			ChromaItems.SEED.getStackOf(CrystalElement.MAGENTA),	ResearchLevel.RUNECRAFT),
	FRAGMENT("Fragments",			ChromaItems.FRAGMENT, 									ResearchLevel.ENTRY),
	AUGMENT("Upgrades",				ChromaStacks.speedUpgrade,								ResearchLevel.PYLONCRAFT,	ProgressStage.STORAGE),
	ALLOYS("Alloying",				ChromaStacks.chromaIngot,								ResearchLevel.ENERGY,ProgressionManager.instance.getPrereqsArray(ProgressStage.ALLOY)),
	INSCRIPTION("Inscription",		ChromaItems.DATACRYSTAL.getStackOf(),					ResearchLevel.RAWEXPLORE,	ProgressStage.TOWER),
	BEES("Crystal Bees",			new ItemStack(Blocks.dirt),								ResearchLevel.RAWEXPLORE,	ProgressStage.HIVE),
	TINKERTOOLS("Mix-And-Magic Tools",	new ItemStack(Blocks.dirt),							ResearchLevel.MULTICRAFT),
	BRANCHES("Magic Branches",		ItemMagicBranch.BranchTypes.CRYSTAL.getStack(),			ResearchLevel.ENERGY),
	FERTILITYSEED("Fertility Seeds",ChromaItems.FERTILITYSEED.getStackOf(),					ResearchLevel.RAWEXPLORE,	ProgressStage.GLOWCLIFFS),
	ARTEFACT("Mysterious Artifacts",ChromaItems.ARTEFACT.getStackOf(),						ResearchLevel.RAWEXPLORE,	ProgressStage.ARTEFACT),
	PROXESSENCE("Proximal Essence",	ChromaStacks.bedrockloot,								ResearchLevel.ENDGAME,		ProgressStage.DIMENSION),
	VOIDESSENCE("Corrupted Essence",ChromaStacks.voidmonsterEssence, 						ResearchLevel.RAWEXPLORE,	ProgressStage.VOIDMONSTER),

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
	PYLONPROTECT(	Chromabilities.PYLON, 						ResearchLevel.ENERGY),
	LIGHTNING(		Chromabilities.LIGHTNING),
	LIFEPOINT(		Chromabilities.LIFEPOINT),
	DEATHPROOF(		Chromabilities.DEATHPROOF),
	SHOCKWAVE(		Chromabilities.SHOCKWAVE),
	WARPLOC(		Chromabilities.TELEPORT,					ResearchLevel.ENDGAME),
	LEECH(			Chromabilities.LEECH, 						ResearchLevel.ENERGY),
	FLOAT(			Chromabilities.FLOAT, 						ResearchLevel.ENERGY),
	SPAWNERSEE(		Chromabilities.SPAWNERSEE,					ResearchLevel.ENDGAME),
	BREADCRUMB(		Chromabilities.BREADCRUMB),
	RANGEBOOST(		Chromabilities.RANGEDBOOST),
	DIMPING(		Chromabilities.DIMPING,						ResearchLevel.ENDGAME),
	DASH(			Chromabilities.DASH),
	LASERBILITY(	Chromabilities.LASER,						ResearchLevel.ENDGAME),
	FIRERAIN(		Chromabilities.FIRERAIN,					ResearchLevel.CTM),
	KEEPINV(		Chromabilities.KEEPINV,						ResearchLevel.ENDGAME),
	ORECLIP(		Chromabilities.ORECLIP,						ResearchLevel.CTM),
	DOUBLECRAFT(	Chromabilities.DOUBLECRAFT,					ResearchLevel.CTM),
	GROWAURA(		Chromabilities.GROWAURA,					ResearchLevel.ENDGAME),
	RECHARGE(		Chromabilities.RECHARGE,					ResearchLevel.ENDGAME),
	MEINV(			Chromabilities.MEINV,						ResearchLevel.ENDGAME),
	MOBSEEK(		Chromabilities.MOBSEEK,						ResearchLevel.ENDGAME),
	BEEALYZE(		Chromabilities.BEEALYZE),
	NUKER(			Chromabilities.NUKER,						ResearchLevel.ENDGAME),
	LIGHTCAST(		Chromabilities.LIGHTCAST, 					ResearchLevel.ENERGY),
	JUMPABILITY(	Chromabilities.JUMP, 						ResearchLevel.ENERGY),
	SUPERBUILD(		Chromabilities.SUPERBUILD, 					ResearchLevel.ENERGY),
	CHESTCLEAR(		Chromabilities.CHESTCLEAR, 					ResearchLevel.ENERGY),
	MOBBAIT(		Chromabilities.MOBBAIT, 					ResearchLevel.ENERGY),

	STRUCTUREDESC("Structures", ""),
	PYLON(			ChromaStructures.PYLON,			StoneTypes.FOCUS.ordinal(),				ResearchLevel.ENERGY,	ProgressStage.PYLON),
	CASTING1(		ChromaStructures.CASTING1,		StoneTypes.SMOOTH.ordinal(),			ResearchLevel.BASICCRAFT,		ProgressStage.CRYSTALS),
	CASTING2(		ChromaStructures.CASTING2,		StoneTypes.BEAM.ordinal(),				ResearchLevel.RUNECRAFT,		ProgressStage.RUNEUSE),
	CASTING3(		ChromaStructures.CASTING3,		StoneTypes.COLUMN.ordinal(),			ResearchLevel.NETWORKING,		ProgressStage.MULTIBLOCK, ProgressStage.BLOWREPEATER),
	RITUAL	(		ChromaStructures.RITUAL,		StoneTypes.ENGRAVED.ordinal(),			ResearchLevel.ENERGY,		ProgressStage.CHARGE),
	INFUSION(		ChromaStructures.INFUSION,		StoneTypes.BRICKS.ordinal(),			ResearchLevel.MULTICRAFT,		ProgressStage.CHROMA),
	PLAYERINFUSION(	ChromaStructures.PLAYERINFUSION,StoneTypes.BEAM.ordinal(),				ResearchLevel.MULTICRAFT,		ProgressStage.INFUSE),
	TREE(			ChromaStructures.TREE,			StoneTypes.STABILIZER.ordinal(),		ResearchLevel.ENDGAME,			ProgressStage.POWERCRYSTAL),
	TREESEND(		ChromaStructures.TREE_SENDER,	StoneTypes.FOCUSFRAME.ordinal(),		ResearchLevel.ENDGAME,			ProgressStage.POWERTREE),
	REPEATERSTRUCT(	ChromaStructures.REPEATER,		StoneTypes.SMOOTH.ordinal(),			ResearchLevel.NETWORKING,		ProgressStage.RUNEUSE, ProgressStage.BLOWREPEATER),
	COMPOUNDSTRUCT(	ChromaStructures.COMPOUND,		StoneTypes.MULTICHROMIC.ordinal(),		ResearchLevel.NETWORKING,		ProgressStage.REPEATER),
	CAVERN(			ChromaStructures.CAVERN,		ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata,	ResearchLevel.RAWEXPLORE,		ProgressStage.CAVERN),
	BURROW(			ChromaStructures.BURROW,		ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata,	ResearchLevel.RAWEXPLORE,		ProgressStage.BURROW),
	OCEAN(			ChromaStructures.OCEAN,			ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.MOSS.metadata,	ResearchLevel.RAWEXPLORE,		ProgressStage.OCEAN),
	DESERT(			ChromaStructures.DESERT,		ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata,ResearchLevel.RAWEXPLORE,		ProgressStage.DESERTSTRUCT),
	SNOW(			ChromaStructures.SNOWSTRUCT,	ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata,	ResearchLevel.RAWEXPLORE,		ProgressStage.SNOWSTRUCT),
	PORTALSTRUCT(	ChromaStructures.PORTAL,		StoneTypes.SMOOTH.ordinal(),			ResearchLevel.ENDGAME,			ProgressionManager.instance.getPrereqsArray(ProgressStage.DIMENSION)),
	MINIPYLON(		ChromaStructures.PERSONAL,		StoneTypes.FOCUSFRAME.ordinal(),		ResearchLevel.ENERGY,		ProgressStage.CHARGE),
	BROADCASTER(	ChromaStructures.BROADCAST,		StoneTypes.RESORING.ordinal(),			ResearchLevel.NETWORKING,		ProgressStage.MULTIBLOCK, ProgressStage.REPEATER),
	CLOAKTOWER(		ChromaStructures.CLOAKTOWER,	StoneTypes.GLOWCOL.ordinal(),			ResearchLevel.MULTICRAFT,		ProgressStage.KILLMOB),
	BOOSTTREE(		ChromaStructures.TREE_BOOSTED,	StoneTypes.STABILIZER.ordinal(),		ResearchLevel.CTM,				ProgressStage.TURBOCHARGE),
	BEACONSTRUCT(	ChromaStructures.PROTECT,		StoneTypes.CORNER.ordinal(),			ResearchLevel.ENDGAME),
	MINIREPEATER(	ChromaStructures.WEAKREPEATER,	Blocks.log, 0, 							ResearchLevel.ENERGY,	ProgressStage.PYLON),
	METEOR1(		ChromaStructures.METEOR1,		StoneTypes.BRICKS.ordinal(),			ResearchLevel.PYLONCRAFT),
	METEOR2(		ChromaStructures.METEOR2,		StoneTypes.BRICKS.ordinal(),			ResearchLevel.ENDGAME),
	METEOR3(		ChromaStructures.METEOR3,		StoneTypes.BRICKS.ordinal(),			ResearchLevel.ENDGAME,			ProgressStage.DIMENSION),
	RITUAL2	(		ChromaStructures.RITUAL2,		StoneTypes.ENGRAVED.ordinal(),			ResearchLevel.ENDGAME,			ProgressStage.DIMENSION),
	GATESTRUCT(		ChromaStructures.TELEGATE,		StoneTypes.COLUMN.ordinal(),			ResearchLevel.ENDGAME,			ProgressStage.END),
	RELAYSTRUCT(	ChromaStructures.RELAY,			StoneTypes.FOCUSFRAME.ordinal(),		ResearchLevel.ENDGAME,			ProgressStage.POWERCRYSTAL),
	PYLONBROADCAST(	ChromaStructures.PYLONBROADCAST,StoneTypes.FOCUS.ordinal(),				ResearchLevel.ENDGAME,			ProgressStage.DIMENSION),
	PYLONTURBORING(	ChromaStructures.PYLONTURBO,	StoneTypes.FOCUS.ordinal(),				ResearchLevel.ENDGAME,			ProgressionManager.instance.getPrereqsArray(ProgressStage.TURBOCHARGE)),
	WIRELESSPED(	ChromaStructures.WIRELESSPEDESTAL, StoneTypes.MULTICHROMIC.ordinal(), 	ResearchLevel.ENDGAME),
	WIRELESSPED2(	ChromaStructures.WIRELESSPEDESTAL2, StoneTypes.MULTICHROMIC.ordinal(),	ResearchLevel.CTM),
	DATATOWER(		ChromaStructures.DATANODE,		StoneTypes.SMOOTH.ordinal(),			ResearchLevel.RAWEXPLORE,		ProgressStage.TOWER),
	PROGLINKSTRUCT(	ChromaStructures.PROGRESSLINK,	StoneTypes.CORNER.ordinal(),			ResearchLevel.BASICCRAFT),
	OPTIMISTRUCT(	ChromaStructures.OPTIMIZER,		StoneTypes.RESORING.ordinal(),			ResearchLevel.ENDGAME,			TieredOres.LUMA.level),
	VOIDTRAPSTRUCT(	ChromaStructures.VOIDRITUAL,	Blocks.redstone_torch, 5,				ResearchLevel.PYLONCRAFT,		ProgressStage.VOIDMONSTERDIE, ProgressStage.CHARGECRYSTAL),
	VOIDTRAPSTRUCTN(ChromaStructures.NETHERTRAP,	Blocks.tnt, 0,							ResearchLevel.PYLONCRAFT,		ProgressStage.VOIDMONSTERDIE, ProgressStage.CHARGECRYSTAL),
	LAUNCHPADSTRUCT(ChromaStructures.LAUNCHPAD,		StoneTypes.ENGRAVED.ordinal(), 			ResearchLevel.BASICCRAFT),
	BIOMESTRUCT(	ChromaStructures.BIOMEFRAG,		ChromaBlocks.COLORLOCK.getBlockInstance(), 0, ResearchLevel.MULTICRAFT,	ProgressStage.BIOMESTRUCT),
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
	private ChromaStructures struct;

	private int sectionIndex = 0;

	public static final ChromaResearch[] researchList = values();
	static final MultiMap<ResearchLevel, ChromaResearch> levelMap = new MultiMap(CollectionType.HASHSET);
	private static final ItemHashMap<ChromaResearch> itemMap = new ItemHashMap();
	private static final EnumMap<ChromaTiles, ChromaResearch> tileMap = new EnumMap(ChromaTiles.class);
	private static final EnumMap<ChromaStructures, ChromaResearch> structureMap = new EnumMap(ChromaStructures.class);
	private static final HashMap<Ability, ChromaResearch> abilityMap = new HashMap();
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
		if (rl.ordinal() < ResearchLevel.ENERGY.ordinal() && r.isRelayPowered()) {
			throw new RegistrationException(ChromatiCraft.instance, "Machine fragment "+r+" relay powered but available before relays!");
		}
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
		iconItem = icon != null ? icon.copy() : null;
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

	private ChromaResearch(ChromaStructures s, int meta, ResearchLevel r, ProgressStage... p) {
		this(s, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), meta, r, p);
	}

	private ChromaResearch(ChromaStructures s, Block b, int meta, ResearchLevel r, ProgressStage... p) {
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

	public boolean canPlayerProgressTo(EntityPlayer ep) {
		if (progress != null) {
			for (int i = 0; i < progress.length; i++) {
				ProgressStage p = progress[i];
				if (!p.isPlayerAtStage(ep))
					return false;
			}
		}
		if (ability != null && !ability.isAvailableToPlayer(ep))
			return false;
		return true;
	}

	public ProgressStage[] getRequiredProgress() {
		return Arrays.copyOf(progress, progress.length);
	}

	public boolean isMachine() {
		return machine != null;
	}

	public Chromabilities getAbility() {
		return ability;
	}

	public ChromaStructures getStructure() {
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
		if (this == TINKERTOOLS) {
			int mat = ExtraChromaIDs.CHROMAMATID.getValue();
			return TinkerToolHandler.Tools.HAMMER.getToolOfMaterials(mat, mat, mat, mat);
		}
		if (this == ENDERCRYS) {
			return item.getStackOfMetadata(1);
		}
		if (this == FOCUSCRYSTALS) {
			return CrystalTier.REFINED.getCraftedItem();
		}
		if (this == ACCEL) {
			return ChromaItems.ADJACENCY.getStackOfMetadata(CrystalElement.LIGHTBLUE.ordinal());
		}
		if (item != null && item.getItemInstance() instanceof PoweredItem) {
			PoweredItem i = (PoweredItem)item.getItemInstance();
			return ToolChargingSystem.instance.getChargedItem((Item & PoweredItem)i, i.getMaxCharge());
		}
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
		else if (this == ITEMBURNER) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			String var4 = "Textures/GUIs/itemburner.png";
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
			ReikaGuiAPI.instance.drawTexturedModalRect(x, y, 202, 58, 16, 16);
			GL11.glPopAttrib();
			return;
		}
		else if (this == MONUMENT) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			BlendMode.ADDITIVEDARK.apply();
			String var4 = "Textures/monument_lines_big.png";
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
			int c1 = CrystalElement.getBlendedColor(ReikaRenderHelper.getSystemTimeAsInt()/50, 20);
			int c2 = CrystalElement.getBlendedColor(ReikaRenderHelper.getSystemTimeAsInt()/32, 20);
			int c3 = CrystalElement.getBlendedColor(ReikaRenderHelper.getSystemTimeAsInt()/71, 20);
			int c4 = CrystalElement.getBlendedColor(ReikaRenderHelper.getSystemTimeAsInt()/44, 20);
			int w = 16;
			int h = 16;
			int d = 0;
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(c1);
			tessellator.addVertexWithUV((x + 0 - d), (y + h + d), 0, 0, 1);
			tessellator.setColorOpaque_I(c2);
			tessellator.addVertexWithUV((x + w + d), (y + h + d), 0, 1, 1);
			tessellator.setColorOpaque_I(c3);
			tessellator.addVertexWithUV((x + w + d), (y + 0 - d), 0, 1, 0);
			tessellator.setColorOpaque_I(c4);
			tessellator.addVertexWithUV((x + 0 - d), (y + 0 - d), 0, 0, 0);
			tessellator.draw();
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
		}/*
		else if (this.getDependency() == ModList.VOIDMONSTER) {
			ReikaGuiAPI.instance.renderStatic(x, y, x+15, y+15);
			return;
		}*/
		else if (this == CASTTUNING) {
			GL11.glPushMatrix();
			/*
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glColor4f(1, 1, 1, 1);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, 50);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x-1, y-1, ChromaIcons.BIGFLARE.getIcon(), 18, 18);
			GL11.glPopMatrix();
			GL11.glPopAttrib();
			 */
			double s = 18;
			GL11.glTranslated(x+2.5, y+13.5, 0);
			GL11.glScaled(s, s, s);
			GL11.glColor4f(1, 1, 1, 1);
			try {
				GL11.glRotated(180, 0, 0, 1);
				GL11.glRotated(20, 1, 0, 0);
				GL11.glRotated(-60, 0, 1, 0);
				TileEntityRendererDispatcher.instance.renderTileEntityAt(ReikaPlayerAPI.getPlayerHead(Minecraft.getMinecraft().theWorld, ReikaPlayerAPI.getClientProfile()), 0, 0, 0, ReikaRenderHelper.getPartialTickTime());
			}
			catch (Exception e) {
				ReikaChatHelper.write("Threw exception rendering fragment "+this+"! Check your console!");
				e.printStackTrace();
			}
			GL11.glPopMatrix();
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
		else if (this == ITEMBUFFERLINK) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/infoicons.png");
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			int[] idxs = {24, 7};
			for (int idx : idxs) {
				double u = idx%16/16D;
				double v = idx/16/16D;
				double du = u+1/16D;
				double dv = v+1/16D;
				int d = idx == 7 ? 2 : 0;
				int w = 16;
				int h = 16;
				tessellator.addVertexWithUV((x + 0 - d), (y + h + d), 0, u, dv);
				tessellator.addVertexWithUV((x + w + d), (y + h + d), 0, du, dv);
				tessellator.addVertexWithUV((x + w + d), (y + 0 - d), 0, du, v);
				tessellator.addVertexWithUV((x + 0 - d), (y + 0 - d), 0, u, v);
			}
			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
			return;
		}
		else if (this == FRAGMENTSELECT) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glShadeModel(GL11.GL_SMOOTH);
			ReikaTextureHelper.bindTerrainTexture();
			int d = 0;
			int hash = System.identityHashCode(this);
			int c = CrystalElement.getBlendedColor(ReikaRenderHelper.getSystemTimeAsInt()/48+17*hash, 30);
			int br = ReikaColorAPI.GStoHex((int)(192+64*Math.sin(System.currentTimeMillis()/271D+hash)));
			float mix = (float)(0.5+0.5*Math.sin(System.currentTimeMillis()/443D-13*hash));
			c = ReikaColorAPI.mixColors(c, br, mix);
			c = ReikaColorAPI.getModifiedSat(c, 1.5F);
			GL11.glColor4f(ReikaColorAPI.getRed(c)/255F, ReikaColorAPI.getGreen(c)/255F, ReikaColorAPI.getBlue(c)/255F, 1);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x-d, y-d, ChromaIcons.LATTICE.getIcon(), 16+d*2, 16+d*2);
			BlendMode.DEFAULT.apply();
			ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/fragmentcategories.png");
			Tessellator tessellator = Tessellator.instance;
			//GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1, 1, 1, 1);
			c = ReikaColorAPI.mixColors(c, 0xffffff, 0.5F);
			float t = ReikaJavaLibrary.getSystemTimeAsInt()/2000F;
			int c1 = ReikaColorAPI.mixColors(0xffffff, ReikaColorAPI.getShiftedHue(c, 45F*(float)Math.sin(2*t)), 0.5F);
			int c2 = ReikaColorAPI.mixColors(0xffffff, ReikaColorAPI.getShiftedHue(c, 45F*(float)Math.sin(5*t)), 0.5F);
			int c3 = ReikaColorAPI.mixColors(0xffffff, ReikaColorAPI.getShiftedHue(c, 45F*(float)Math.sin(9*t)), 0.5F);
			int c4 = ReikaColorAPI.mixColors(0xffffff, ReikaColorAPI.getShiftedHue(c, 45F*(float)Math.sin(13*t)), 0.5F);
			tessellator.startDrawingQuads();
			int idx = (int)((System.currentTimeMillis()/800)%FragmentCategory.list.length);
			double u = idx%8/8D;
			double v = idx/8/8D;
			double du = u+1/8D;
			double dv = v+1/8D;
			int w = 16;
			int h = 16;
			d = -2;
			tessellator.setColorOpaque_I(c1);
			tessellator.addVertexWithUV((x + 0 - d), (y + h + d), 0, u, dv);
			tessellator.setColorOpaque_I(c2);
			tessellator.addVertexWithUV((x + w + d), (y + h + d), 0, du, dv);
			tessellator.setColorOpaque_I(c3);
			tessellator.addVertexWithUV((x + w + d), (y + 0 - d), 0, du, v);
			tessellator.setColorOpaque_I(c4);
			tessellator.addVertexWithUV((x + 0 - d), (y + 0 - d), 0, u, v);
			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
			return;
		}
		else if (this == TURBO) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glEnable(GL11.GL_BLEND);
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
		else if (this == WARPNODE) {
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/warpnode-small.png");
			int idx = (int)(System.currentTimeMillis()/20%64);
			double u = idx%8/8D;
			double v = idx/8/8D;
			double du = u+1/8D;
			double dv = v+1/8D;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			int d = 2;
			int w = 16;
			int h = 16;
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV((x + 0 - d), (y + h + d), 0, u, dv);
			tessellator.addVertexWithUV((x + w + d), (y + h + d), 0, du, dv);
			tessellator.addVertexWithUV((x + w + d), (y + 0 - d), 0, du, v);
			tessellator.addVertexWithUV((x + 0 - d), (y + 0 - d), 0, u, v);
			tessellator.draw();
			GL11.glPopAttrib();
			return;
		}
		else if (this == ENERGY) {
			ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/infoicons.png");
			int idx = 24;
			double u = idx%8/8D;
			double v = idx/8/8D;
			double du = u+1/8D;
			double dv = v+1/8D;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glShadeModel(GL11.GL_SMOOTH);
			int d = 2;
			int w = 16;
			int h = 16;
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(CrystalElement.getBlendedColor(Minecraft.getMinecraft().thePlayer.ticksExisted, 10));
			tessellator.addVertexWithUV((x + 0 - d), (y + h + d), 0, u, dv);
			tessellator.setColorOpaque_I(CrystalElement.getBlendedColor(Minecraft.getMinecraft().thePlayer.ticksExisted+5, 10));
			tessellator.addVertexWithUV((x + w + d), (y + h + d), 0, du, dv);
			tessellator.setColorOpaque_I(CrystalElement.getBlendedColor(Minecraft.getMinecraft().thePlayer.ticksExisted+15, 10));
			tessellator.addVertexWithUV((x + w + d), (y + 0 - d), 0, du, v);
			tessellator.setColorOpaque_I(CrystalElement.getBlendedColor(Minecraft.getMinecraft().thePlayer.ticksExisted+10, 10));
			tessellator.addVertexWithUV((x + 0 - d), (y + 0 - d), 0, u, v);
			tessellator.draw();
			GL11.glPopAttrib();
			return;
		}
		else if (this == DATATOWER) {
			ReikaGuiAPI.instance.drawItemStack(ri, ChromaItems.DATACRYSTAL.getStackOf(), x, y);
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			double s = 0.75;
			double d = 16;
			GL11.glTranslated(x-d, y-d, 0);
			GL11.glScaled(s, s, 0);
			RenderDataNode.renderFlare(Tessellator.instance, (float)(0.75+0.25*Math.sin(System.currentTimeMillis()/500D)), false);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
			return;
		}
		else if (this == ITEMCHARGE) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			ReikaTextureHelper.bindTerrainTexture();
			int c = CrystalElement.getBlendedColor(Minecraft.getMinecraft().thePlayer.ticksExisted, 30);
			GL11.glColor4f(ReikaColorAPI.getRed(c)/255F, ReikaColorAPI.getGreen(c)/255F, ReikaColorAPI.getBlue(c)/255F, 1);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, ChromaIcons.RADIATE.getIcon(), 16, 16);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, ChromaIcons.RADIATE.getIcon(), 16, 16);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x-8, y-8, ChromaIcons.ROUNDFLARE.getIcon(), 32, 32);
			GL11.glColor4f(1, 1, 1, 1);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, ChromaIcons.ROUNDFLARE.getIcon(), 16, 16);
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
		GL11.glPushMatrix();
		//if (this == DIMENSION3) {
		GL11.glTranslated(0, 0, -50);
		//}

		if (this.getTabIcon() == null) {
			ReikaChatHelper.write("Error rendering fragment "+this+": no icon to draw");
			return;
		}

		GuiMachineDescription.runningRender = true;
		ItemStack ico = this.getTabIcon().copy();
		if (ico.stackTagCompound == null)
			ico.stackTagCompound = new NBTTagCompound();
		ico.stackTagCompound.setBoolean("tooltip", true);
		ReikaGuiAPI.instance.drawItemStack(ri, ico, x, y);
		GuiMachineDescription.runningRender = false;

		ri.zLevel = zp;
		if (this == FARLANDS) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glTranslated(0, 0, -240);
			int s = 32;
			int ds = (s-16)/2;
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x-ds, y-ds, ChromaIcons.PURPLESPIN.getIcon(), s, s);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
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
		if (this == ACCEL)
			return ChromaDescriptions.getNotes(this, subpage-1);
		else if (this == ALVEARY && subpage > 1) {
			StringBuilder sb = new StringBuilder();
			TileEntityLumenAlveary.getSortedEffectList().get(subpage-2).getLexiconString(sb);
			return sb.toString();
		}
		String ret = ChromaDescriptions.getNotes(this, 0);
		if (item != null && item.getItemInstance() instanceof DynamicallyGeneratedSubpage) {
			DynamicallyGeneratedSubpage iw = (DynamicallyGeneratedSubpage)this.getItem().getItemInstance();
			if (!iw.replaceOriginal()) {
				ret += (ret.isEmpty() ? "" : "\n")+iw.getNotes(subpage);
			}
		}
		return ret;
	}

	public boolean sameTextAllSubpages() {
		return false;
	}

	public boolean isGating(ResearchLevel at) {
		if (this.isDummiedOut())
			return false;
		if (this.isMachine() && this.getMachine().isIncomplete())
			return false;
		if (this == TINKERTOOLS)
			return false;
		if (this == TRAPFLOOR)
			return false;
		if (this == VOIDESSENCE)
			return false;
		for (ProgressStage p : progress) {
			if (!p.isGating(at))
				return false;
		}
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
		if (this == ITEMBUFFERLINK)
			return true;
		if (this == TURBOREPEATER)
			return true;
		if (this == AURALOCUS)
			return false;
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
		if (this == TINKERTOOLS)
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
		if (this == REDSTONEPOD)
			return true;
		if (this == RFPOD)
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
		if (this == AVOLAMP)
			return true;
		if (this == REPEATERLAMP)
			return true;
		if (this == FAKESKY)
			return true;
		//if (this == SPAWNERCONTROL)
		//	return true;
		if (this == TRAPFLOOR)
			return true;
		if (this == INJECTORAUX)
			return true;
		if (this == HOVERPADAUX)
			return true;
		return false;
	}

	public boolean isTool() {
		return this.getParent() == TOOLDESC;
	}

	public boolean requiresProgress(ProgressStage p) {
		for (int i = 0; i < progress.length; i++) {
			if (progress[i] == p)
				return true;
		}
		return false;
	}

	public ArrayList<ItemStack> getItemStacks() {
		if (this == ACCEL) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < 16; i++) {
				if (AdjacencyUpgrades.upgrades[i].isImplemented()) {
					for (int k = 0; k < TileEntityAdjacencyUpgrade.MAX_TIER; k++) {
						ItemStack is = ChromaItems.ADJACENCY.getStackOfMetadata(i);
						is.stackTagCompound = new NBTTagCompound();
						is.stackTagCompound.setInteger("tier", k);
						li.add(is);
					}
				}
			}
			return li;
		}
		if (this == ROUTER) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(machine.getCraftedProduct());
			li.add(ChromaBlocks.ROUTERNODE.getStackOfMetadata(0));
			li.add(ChromaBlocks.ROUTERNODE.getStackOfMetadata(1));
			return li;
		}
		if (this == HEATLAMP) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < ChromaBlocks.HEATLAMP.getNumberMetadatas(); i++) {
				if (ChromaBlocks.HEATLAMP.isMetaInCreative(i))
					li.add(new ItemStack(ChromaBlocks.HEATLAMP.getBlockInstance(), 1, i));
			}
			return li;
		}
		if (this == FOCUSCRYSTALS) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < CrystalTier.tierList.length; i++) {
				li.add(CrystalTier.tierList[i].getCraftedItem());
			}
			return li;
		}
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
			for (int i = 0; i < 16*Bases.baseList.length; i++) {
				li.add(ChromaBlocks.GLOW.getStackOfMetadata(i));
			}
			return li;
		}
		if (this == RELAY) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaTiles.RELAYSOURCE.getCraftedProduct());
			li.add(ChromaBlocks.RELAYFILTER.getStackOf());
			for (int i = 0; i < 16; i++) {
				li.add(ChromaBlocks.RELAY.getStackOfMetadata(i));
			}
			li.add(ChromaBlocks.RELAY.getStackOfMetadata(16));
			li.add(ChromaBlocks.FLOATINGRELAY.getStackOf());
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
			if (item.getItemInstance() instanceof ItemPoweredChromaTool) {
				ItemPoweredChromaTool it = (ItemPoweredChromaTool)item.getItemInstance();
				ArrayList<ItemStack> li = new ArrayList();
				for (int i = 0; i < it.getChargeStates(); i++)
					li.add(item.getStackOfMetadata(i));
				return li;
			}
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
			li.add(ChromaStacks.elementUnit);
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
			li.add(ChromaStacks.lumenGem);
			li.add(ChromaStacks.avolite);
			li.add(ChromaStacks.echoCrystal);
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
			li.add(ChromaStacks.crystalLens);
			return li;
		}
		if (this == HICORES) {
			ArrayList<ItemStack> li = new ArrayList();
			li.add(ChromaStacks.energyCoreHigh);
			li.add(ChromaStacks.transformCoreHigh);
			li.add(ChromaStacks.voidCoreHigh);
			li.add(ChromaStacks.glowChunk);
			li.add(ChromaStacks.lumenCore);
			return li;
		}
		if (this == PATH) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < ChromaBlocks.PATH.getNumberMetadatas(); i++) {
				li.add(new ItemStack(ChromaBlocks.PATH.getBlockInstance(), 1, i));
			}
			return li;
		}
		if (this == HOVERPADAUX) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < ChromaBlocks.PAD.getNumberMetadatas(); i++) {
				li.add(new ItemStack(ChromaBlocks.PAD.getBlockInstance(), 1, i));
			}
			return li;
		}
		if (this == REPEATERLAMP) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < ChromaBlocks.REPEATERLAMP.getNumberMetadatas(); i++) {
				li.add(new ItemStack(ChromaBlocks.REPEATERLAMP.getBlockInstance(), 1, i));
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

			Collection<BeeSpecies> c = CrystalBees.getBasicBees();

			for (BeeSpecies b : c) {
				li.add(b.getBeeItem(world, EnumBeeType.DRONE));
				li.add(b.getBeeItem(world, EnumBeeType.PRINCESS));
				li.add(b.getBeeItem(world, EnumBeeType.QUEEN));
			}

			for (int i = 0; i < 16; i++) {
				CrystalElement e = CrystalElement.elements[i];
				li.add(CrystalBees.getElementalBee(e).getBeeItem(world, EnumBeeType.DRONE));
				li.add(CrystalBees.getElementalBee(e).getBeeItem(world, EnumBeeType.PRINCESS));
				li.add(CrystalBees.getElementalBee(e).getBeeItem(world, EnumBeeType.QUEEN));
			}

			c = CrystalBees.getAdvancedBees();

			for (BeeSpecies b : c) {
				li.add(b.getBeeItem(world, EnumBeeType.DRONE));
				li.add(b.getBeeItem(world, EnumBeeType.PRINCESS));
				li.add(b.getBeeItem(world, EnumBeeType.QUEEN));
			}

			return li;
		}
		if (this == TINKERTOOLS) {
			ArrayList<ItemStack> li = new ArrayList();

			for (int i = 0; i < ToolParts.partList.length; i++) {
				li.add(ToolParts.partList[i].getItem(ExtraChromaIDs.CHROMAMATID.getValue()));
			}
			for (int i = 0; i < WeaponParts.partList.length; i++) {
				li.add(ToolParts.partList[i].getItem(ExtraChromaIDs.CHROMAMATID.getValue()));
			}

			return li;
		}
		if (this == TANKAUX)
			return ReikaJavaLibrary.makeListFrom(ChromaBlocks.TANK.getStackOf(), ChromaBlocks.TANK.getStackOfMetadata(2));
		if (this == FENCEAUX || this == TNT || this == VOIDESSENCE)
			return ReikaJavaLibrary.makeListFrom(iconItem);
		if (this == PROXESSENCE)
			return ReikaJavaLibrary.makeListFrom(ChromaStacks.bedrockloot, ChromaStacks.bedrockloot2);
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
		if (this == APIRECIPES) {
			ArrayList<ItemStack> li = new ArrayList();
			for (CastingRecipe c : RecipesCastingTable.instance.getAllAPIRecipes()) {
				li.add(c.getOutput());
			}
			for (CastingRecipe c : RecipesCastingTable.instance.getAllModdedItemRecipes()) {
				li.add(c.getOutput());
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

	public boolean crafts(ItemStack is) {
		if (!this.isCrafting())
			return false;
		if (this == ALLOYS) {
			return ReikaItemHelper.listContainsItemStack(this.getItemStacks(), is, false);
		}
		else if (this.isVanillaRecipe()) {
			for (IRecipe ir : this.getVanillaRecipes()) {
				if (ReikaItemHelper.matchStacks(is, ir.getRecipeOutput())) {
					return true;
				}
			}
		}
		else {
			for (CastingRecipe c : this.getCraftingRecipes()) {
				if (ReikaItemHelper.matchStacks(is, c.getOutput())) {
					return true;
				}
			}
		}
		return false;
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
		if (this == ITEMBUFFERLINK) {
			ArrayList<CastingRecipe> li = new ArrayList();
			for (CastingRecipe cr : RecipesCastingTable.instance.getAllRecipes()) {
				if (cr instanceof ChargedItemPlayerBufferConnectionRecipe) {
					li.add(cr);
				}
			}
			return li;
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

	public int getRecipeIndex(ItemStack is, EntityPlayer ep) {
		if (this == ALLOYS) {
			return new ArrayList(PoolRecipes.instance.getAllPoolRecipesForPlayer(ep)).indexOf(PoolRecipes.instance.getPoolRecipeByOutput(is));
		}
		if (this == METEOR && is.stackTagCompound != null) {
			return is.stackTagCompound.getInteger("tier");
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
		if (this == ACCEL)
			return "Adjacency Cores";
		return pageTitle;
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
		//if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())
		//	return false;
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
		if (this == AUGMENT)
			return true;
		if (this == VILLAGEREPAIR)
			return true;
		if (this == AISHUTDOWN)
			return true;
		Dependency dep = this.getDependency();
		if (dep != null && !dep.isLoaded())
			return true;
		for (int i = 0; i < progress.length; i++) {
			if (!progress[i].active) {
				return true;
			}
		}
		return false;
	}

	public Dependency getDependency() {
		switch(this) {
			case BEES:
				return ModList.FORESTRY;
			case TINKERTOOLS:
				return ModList.TINKERER;
			case RFDISTRIB:
				return PowerTypes.RF;
			case BALLLIGHTNING:
				return ChromaOptions.BALLLIGHTNING;
			case NODENET:
				return ModList.THAUMCRAFT;
			case MYSTPAGE:
				return ModList.MYSTCRAFT;
			case DEATHFOG:
			case VOIDTRAP:
			case VOIDTRAPSTRUCT:
			case VOIDESSENCE:
				return ModList.VOIDMONSTER;
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

	@Override
	public boolean giveToPlayer(EntityPlayer ep, boolean notify) {
		return ChromaResearchManager.instance.givePlayerFragment(ep, this, notify);
	}

	@Override
	public boolean playerHas(EntityPlayer ep) {
		return ChromaResearchManager.instance.playerHasFragment(ep, this);
	}

	public boolean playerCanSeeRecipe(ItemStack is, EntityPlayer ep) {
		if (this == ALLOYS)
			return PoolRecipes.instance.getPoolRecipeByOutput(is).playerHasProgress(ep);
		return true;
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
			if (r.ability != null)
				abilityMap.put(r.ability, r);
			if (r.machine != null)
				tileMap.put(r.machine, r);
			if (r.struct != null)
				structureMap.put(r.struct, r);
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
						throw new InstallationException(ChromatiCraft.instance, "Another mod/API, '"+dep.getDisplayName()+"' is an incompatible version. Update both mods if possible, or if updating "+dep.getDisplayName()+" caused this, revert to the previous version.", e);
					}
					else {
						throw new RegistrationException(ChromatiCraft.instance, "Could not initialize Info Fragment '"+r+"'!", e);
					}
				}
			}
		}

		FragmentCategorizationSystem.instance.calculate();
		//FragmentCategorizationSystem.instance.dumpData();
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

	public static ChromaResearch getPageFor(Chromabilities a) {
		return abilityMap.get(a);
	}

	public static ChromaResearch getPageFor(ChromaTiles c) {
		return tileMap.get(c);
	}

	public static ChromaResearch getPageFor(ChromaStructures s) {
		return structureMap.get(s);
	}

	public static Collection<ChromaResearch> getPagesFor(ResearchLevel l) {
		return levelMap.get(l);
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
