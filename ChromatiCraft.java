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

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import Reika.ChromatiCraft.Auxiliary.ChromaASMHandler;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.ChromaBookSpawner;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaLock;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.CrystalMaterial;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.NetworkLoggerCommand;
import Reika.ChromatiCraft.Auxiliary.CrystalPlantHandler;
import Reika.ChromatiCraft.Auxiliary.ExplorationMonitor;
import Reika.ChromatiCraft.Auxiliary.GuardianStoneManager;
import Reika.ChromatiCraft.Auxiliary.MusicLoader;
import Reika.ChromatiCraft.Auxiliary.PylonCacheLoader;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Auxiliary.ToolDispenserHandlers.ManipulatorDispenserAction;
import Reika.ChromatiCraft.Auxiliary.ToolDispenserHandlers.ProjectileToolDispenserAction;
import Reika.ChromatiCraft.Auxiliary.VillageTradeHandler;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.Ability.ChromabilityHandler;
import Reika.ChromatiCraft.Auxiliary.Command.CrystalNetCommand;
import Reika.ChromatiCraft.Auxiliary.Command.DimensionGeneratorCommand;
import Reika.ChromatiCraft.Auxiliary.Command.GuardianCommand;
import Reika.ChromatiCraft.Auxiliary.Command.NodeWrapperInspectionCommand;
import Reika.ChromatiCraft.Auxiliary.Command.PlaceStructureCommand;
import Reika.ChromatiCraft.Auxiliary.Command.ProgressModifyCommand;
import Reika.ChromatiCraft.Auxiliary.Command.PylonCacheCommand;
import Reika.ChromatiCraft.Auxiliary.Command.RecipeReloadCommand;
import Reika.ChromatiCraft.Auxiliary.Command.RedecorateCommand;
import Reika.ChromatiCraft.Auxiliary.Command.ReshufflePylonCommand;
import Reika.ChromatiCraft.Auxiliary.Command.StructureCacheCommand;
import Reika.ChromatiCraft.Auxiliary.Command.StructureGenCommand;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaDecorator;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LoadRegistry;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionBetterSaturation;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionCustomRegen;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionGrowthHormone;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionLumarhea;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionLumenRegen;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionVoidGaze;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.TransmutationRecipes;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaHelpHUD;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaOverlays;
import Reika.ChromatiCraft.Auxiliary.Render.MobSonarRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.OreOverlayRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.PylonFinderOverlay;
import Reika.ChromatiCraft.Auxiliary.Render.StructureErrorOverlays;
import Reika.ChromatiCraft.Auxiliary.Tab.FragmentTab;
import Reika.ChromatiCraft.Auxiliary.Tab.TabChromatiCraft;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemDuplicationWand;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.Artefact.ArtefactSpawner;
import Reika.ChromatiCraft.Magic.Artefact.UABombingEffects;
import Reika.ChromatiCraft.Magic.Lore.RosettaStone;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Progression.ProgressionLoadHandler;
import Reika.ChromatiCraft.ModInterface.IC2ReactorAcceleration;
import Reika.ChromatiCraft.ModInterface.ModInteraction;
import Reika.ChromatiCraft.ModInterface.MultiblockAcceleration;
import Reika.ChromatiCraft.ModInterface.ReservoirHandlers.ChromaPrepHandler;
import Reika.ChromatiCraft.ModInterface.ReservoirHandlers.PoolRecipeHandler;
import Reika.ChromatiCraft.ModInterface.ReservoirHandlers.ShardBoostingHandler;
import Reika.ChromatiCraft.ModInterface.TreeCapitatorHandler;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.ChromaAspectManager;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.NodeRecharger;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TileEntityAspectFormer;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterRitualClientEffects;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaEnchants;
import Reika.ChromatiCraft.Registry.ChromaEntities;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Registry.ItemMagicRegistry;
import Reika.ChromatiCraft.Render.ParticleEngine;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityOreCreator;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.World.BiomeEnderForest;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.BiomeRainbowForest;
import Reika.ChromatiCraft.World.GlowingCliffsEdge;
import Reika.ChromatiCraft.World.VillagersFailChromatiCraft;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionTicker;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.DimensionJoinHandler;
import Reika.ChromatiCraft.World.IWG.CaveIndicatorGenerator;
import Reika.ChromatiCraft.World.IWG.ColorTreeGenerator;
import Reika.ChromatiCraft.World.IWG.CrystalGenerator;
import Reika.ChromatiCraft.World.IWG.DataTowerGenerator;
import Reika.ChromatiCraft.World.IWG.DecoFlowerGenerator;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.ChromatiCraft.World.IWG.GlowingCliffsAuxGenerator;
import Reika.ChromatiCraft.World.IWG.LumaGenerator;
import Reika.ChromatiCraft.World.IWG.PylonGenerator;
import Reika.ChromatiCraft.World.IWG.SkypeaterGenerator;
import Reika.ChromatiCraft.World.IWG.TieredWorldGenerator;
import Reika.ChromatiCraft.World.IWG.WarpNodeGenerator;
import Reika.ChromatiCraft.World.Nether.NetherStructureGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.CreativeTabSorter;
import Reika.DragonAPI.Auxiliary.IconLookupRegistry;
import Reika.DragonAPI.Auxiliary.PopupWriter;
import Reika.DragonAPI.Auxiliary.SpecialBiomePlacementRegistry;
import Reika.DragonAPI.Auxiliary.SpecialBiomePlacementRegistry.Category;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.ConfigMatcher;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController;
import Reika.DragonAPI.Auxiliary.Trackers.FurnaceFuelRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.IDCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.ModLockController;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerFirstTimeTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Auxiliary.Trackers.SuggestedModsTracker;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Extras.PseudoAirMaterial;
import Reika.DragonAPI.Instantiable.EnhancedFluid;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Event.Client.GameFinishedLoadingEvent;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaDispenserHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.ModInteract.BannedItemReader;
import Reika.DragonAPI.ModInteract.ItemStackRepository;
import Reika.DragonAPI.ModInteract.ReikaEEHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.MTInteractionManager;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.SensitiveFluidRegistry;
import Reika.DragonAPI.ModInteract.DeepInteract.SensitiveItemRegistry;
import Reika.DragonAPI.ModInteract.DeepInteract.TimeTorchHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThermalHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerBlockHandler.Pulses;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.DragonAPI.ModRegistry.ModCropList;
import Reika.MeteorCraft.API.MeteorSpawnAPI;
import Reika.RotaryCraft.API.ReservoirAPI;
import Reika.VoidMonster.API.DimensionAPI;
import Reika.VoidMonster.API.MonsterAPI;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ttftcuts.atg.api.ATGBiomes;



@Mod( modid = "ChromatiCraft", name="ChromatiCraft", version = "v@MAJOR_VERSION@@MINOR_VERSION@", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")

public class ChromatiCraft extends DragonAPIMod {
	public static final String packetChannel = "ChromaData";

	public static final TabChromatiCraft tabChroma = new TabChromatiCraft("ChromatiCraft");
	public static final TabChromatiCraft tabChromaDeco = new TabChromatiCraft("ChromatiCraft Deco");
	public static final TabChromatiCraft tabChromaGen = new TabChromatiCraft("ChromatiCraft Worldgen");
	public static final TabChromatiCraft tabChromaTools = new TabChromatiCraft("ChromatiCraft Tools");
	public static final TabChromatiCraft tabChromaItems = new TabChromatiCraft("ChromatiCraft Items");
	public static final FragmentTab tabChromaFragments = new FragmentTab("CC Info Fragments");

	public static final ArmorMaterial FLOATSTONE = EnumHelper.addArmorMaterial("Floatstone", 65536, new int[]{0, 0, 0, 0}, ArmorMaterial.GOLD.getEnchantability()*3/2);

	static final Random rand = new Random();

	private boolean isOfflineMode = false;

	public static final EnhancedFluid chroma = (EnhancedFluid)new EnhancedFluid("chroma").setColor(0x00aaff).setViscosity(300).setTemperature(288).setDensity(300).setLuminosity(15);
	//public static final EnhancedFluid activechroma = (EnhancedFluid)new EnhancedFluid("activechroma").setColor(0x00aaff).setViscosity(300).setTemperature(500).setDensity(300);
	public static EnhancedFluid crystal = (EnhancedFluid)new EnhancedFluid("potion crystal").setColor(0x66aaff).setLuminosity(15).setTemperature(500).setUnlocalizedName("potioncrystal").setViscosity(1500);
	public static final Fluid ender = new Fluid("ender").setViscosity(2000).setDensity(1500).setTemperature(270).setUnlocalizedName("endere").setLuminosity(4);
	public static final Fluid luma = new Fluid("luma").setViscosity(50).setDensity(1).setTemperature(250);
	public static final Fluid lumen = new Fluid("lumen").setViscosity(500).setDensity(300).setTemperature(300).setLuminosity(15);

	public static final Block[] blocks = new Block[ChromaBlocks.blockList.length];
	public static final Item[] items = new Item[ChromaItems.itemList.length];
	public static final Enchantment[] enchants = new Enchantment[ChromaEnchants.enchantmentList.length];

	public static final Material crystalMat = new CrystalMaterial();
	public static final Material airMat = PseudoAirMaterial.instance;

	private static final HashMap<String, ChromaDecorator> decorators = new HashMap();

	public static PotionGrowthHormone growth;
	public static PotionBetterSaturation betterSat;
	public static PotionCustomRegen betterRegen;
	public static PotionLumarhea lumarhea;
	public static PotionVoidGaze voidGaze;
	public static PotionLumenRegen lumenRegen;

	public static final PylonDamage[] pylonDamage = new PylonDamage[16];

	public static EnumCreatureType glowCloudType = EnumHelper.addCreatureType("glowcloud", EntityGlowCloud.class, 24, Material.air, true, false);

	@Instance("ChromatiCraft")
	public static ChromatiCraft instance = new ChromatiCraft();

	public static final ChromaConfig config = new ChromaConfig(instance, ChromaOptions.optionList, ExtraChromaIDs.idList);

	public static ModLogger logger;

	public static BiomeRainbowForest rainbowforest;
	public static BiomeEnderForest enderforest;
	public static BiomeGlowingCliffs glowingcliffs;
	public static GlowingCliffsEdge glowingcliffsEdge;

	@SidedProxy(clientSide="Reika.ChromatiCraft.ChromaClient", serverSide="Reika.ChromatiCraft.ChromaCommon")
	public static ChromaCommon proxy;

	private boolean dimensionLoadable = true;

	public boolean isDimensionLoadable() {
		return dimensionLoadable;
	}

	public final boolean isLocked() {
		return !ModLockController.instance.verify(this);
	}

	private final boolean checkForLock() {
		for (int i = 0; i < ChromaItems.itemList.length; i++) {
			ChromaItems r = ChromaItems.itemList[i];
			if (!r.isDummiedOut()) {
				Item id = r.getItemInstance();
				if (BannedItemReader.instance.containsID(id))
					return true;
			}
		}
		for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
			ChromaBlocks r = ChromaBlocks.blockList[i];
			if (!r.isDummiedOut()) {
				Block id = r.getBlockInstance();
				if (BannedItemReader.instance.containsID(id))
					return true;
			}
		}
		return false;
	}

	public boolean isOfflineMode() {
		return isOfflineMode;
	}

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.startTiming(LoadPhase.PRELOAD);
		this.verifyInstallation();

		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		ModLockController.instance.registerMod(this);

		logger = new ModLogger(instance, false);
		if (DragonOptions.FILELOG.getState())
			logger.setOutput("**_Loading_Log.log");

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MusicLoader.instance.registerAssets();
		}

		proxy.initAssetLoaders();
		proxy.registerSounds();

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			pylonDamage[i] = new PylonDamage(CrystalElement.elements[i]);
		}

		MinecraftForge.EVENT_BUS.register(GuardianStoneManager.instance);
		MinecraftForge.EVENT_BUS.register(ChromaticEventManager.instance);
		MinecraftForge.EVENT_BUS.register(ChromaDimensionTicker.instance);
		FMLCommonHandler.instance().bus().register(ChromaticEventManager.instance);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(ChromaClientEventController.instance);
			FMLCommonHandler.instance().bus().register(ChromaClientEventController.instance);
			MinecraftForge.EVENT_BUS.register(ChromaHelpHUD.instance);
			MinecraftForge.EVENT_BUS.register(PylonFinderOverlay.instance);
		}

		this.setupClassFiles();

		int id = ExtraChromaIDs.GROWTHID.getValue();
		IDCollisionTracker.instance.addPotionID(instance, id, PotionGrowthHormone.class);
		growth = new PotionGrowthHormone(id);

		id = ExtraChromaIDs.SATID.getValue();
		IDCollisionTracker.instance.addPotionID(instance, id, PotionBetterSaturation.class);
		betterSat = new PotionBetterSaturation(id);

		id = ExtraChromaIDs.REGENID.getValue();
		IDCollisionTracker.instance.addPotionID(instance, id, PotionCustomRegen.class);
		betterRegen = new PotionCustomRegen(id);

		id = ExtraChromaIDs.LUMARHEAID.getValue();
		IDCollisionTracker.instance.addPotionID(instance, id, PotionLumarhea.class);
		lumarhea = new PotionLumarhea(id);

		id = ExtraChromaIDs.VOIDGAZEID.getValue();
		IDCollisionTracker.instance.addPotionID(instance, id, PotionVoidGaze.class);
		voidGaze = new PotionVoidGaze(id);

		id = ExtraChromaIDs.LUMENREGENID.getValue();
		IDCollisionTracker.instance.addPotionID(instance, id, PotionLumenRegen.class);
		lumenRegen = new PotionLumenRegen(id);

		lumen.setBlock(ChromaBlocks.MOLTENLUMEN.getBlockInstance());
		ender.setBlock(ChromaBlocks.ENDER.getBlockInstance());
		luma.setBlock(ChromaBlocks.LUMA.getBlockInstance());

		IDCollisionTracker.instance.addBiomeID(instance, ExtraChromaIDs.RAINBOWFOREST.getValue(), BiomeRainbowForest.class);
		IDCollisionTracker.instance.addBiomeID(instance, ExtraChromaIDs.ENDERFOREST.getValue(), BiomeEnderForest.class);
		IDCollisionTracker.instance.addBiomeID(instance, ExtraChromaIDs.LUMINOUSCLIFFS.getValue(), BiomeGlowingCliffs.class);

		//ChromaResearch.loadCache();

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ChromatiPackets());

		proxy.registerKeys();

		tabChroma.setIcon(ChromaItems.RIFT.getStackOf());
		tabChromaDeco.setIcon(ChromaBlocks.COLORALTAR.getStackOfMetadata(CrystalElement.BLUE.ordinal()));
		tabChromaGen.setIcon(ChromaBlocks.RAINBOWSAPLING.getStackOf());
		tabChromaTools.setIcon(ChromaItems.TOOL.getStackOf());
		tabChromaItems.setIcon(ChromaStacks.getShard(CrystalElement.RED));
		CreativeTabSorter.instance.registerCreativeTabAfter(tabChromaDeco, tabChroma);
		CreativeTabSorter.instance.registerCreativeTabAfter(tabChromaGen, tabChroma);
		CreativeTabSorter.instance.registerCreativeTabAfter(tabChromaItems, tabChroma);
		CreativeTabSorter.instance.registerCreativeTabAfter(tabChromaTools, tabChroma);
		CreativeTabSorter.instance.registerCreativeTabAfter(tabChromaFragments, tabChroma);

		if (!this.isLocked()) {
			//if (ConfigRegistry.ACHIEVEMENTS.getState()) {
			//		achievements = new Achievement[RotaryAchievements.list.length];
			//		RotaryAchievements.registerAchievements();
			//	}
		}

		//CompatibilityTracker.instance.registerIncompatibility(ModList.CHROMATICRAFT, ModList.OPTIFINE, CompatibilityTracker.Severity.GLITCH, "Optifine is known to break some rendering and cause framerate drops.");

		if (ModList.FORESTRY.isLoaded()) {
			ModInteraction.addCrystalBackpack();
		}

		FMLInterModComms.sendMessage("zzzzzcustomconfigs", "blacklist-mod-as-output", this.getModContainer().getModId());

		ConfigMatcher.instance.addConfigList(this, ChromaOptions.optionList);
		ConfigMatcher.instance.addConfigList(this, ExtraChromaIDs.idList);

		RayTracer.addVisuallyTransparentBlock(ChromaBlocks.GLASS.getBlockInstance());
		RayTracer.addVisuallyTransparentBlock(ChromaBlocks.SELECTIVEGLASS.getBlockInstance());
		RayTracer.addVisuallyTransparentBlock(ChromaBlocks.DOOR.getBlockInstance());
		RayTracer.addVisuallyTransparentBlock(ChromaBlocks.PYLON.getBlockInstance());
		RayTracer.addVisuallyTransparentBlock(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.ordinal());
		RayTracer.addVisuallyTransparentBlock(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);

		this.basicSetup(evt);
		this.finishTiming();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.startTiming(LoadPhase.LOAD);

		if (this.checkForLock()) {
			ModLockController.instance.unverify(this);
		}
		if (this.isLocked()) {
			ReikaJavaLibrary.pConsole("");
			ReikaJavaLibrary.pConsole("\t========================================= ChromatiCraft ===============================================");
			ReikaJavaLibrary.pConsole("\tNOTICE: It has been detected that third-party plugins are being used to disable parts of ChromatiCraft.");
			ReikaJavaLibrary.pConsole("\tBecause this is frequently done to sell access to mod content, which is against the Terms of Use");
			ReikaJavaLibrary.pConsole("\tof both Mojang and the mod, the mod has been functionally disabled. No damage will occur to worlds,");
			ReikaJavaLibrary.pConsole("\tand all machines (including contents) and items already placed or in inventories will remain so,");
			ReikaJavaLibrary.pConsole("\tbut its machines will not function, recipes will not load, and no renders or textures will be present.");
			ReikaJavaLibrary.pConsole("\tAll other mods in your installation will remain fully functional.");
			ReikaJavaLibrary.pConsole("\tTo regain functionality, unban the ChromatiCraft content, and then reload the game. All functionality");
			ReikaJavaLibrary.pConsole("\twill be restored. You may contact Reika for further information on his forum thread.");
			ReikaJavaLibrary.pConsole("\t=====================================================================================================");
			ReikaJavaLibrary.pConsole("");
		}

		ChromaRecipes.loadDictionary();
		if (this.isLocked())
			PlayerHandler.instance.registerTracker(ChromaLock.instance);
		if (!this.isLocked()) {
			proxy.addArmorRenders();
			proxy.registerRenderers();
		}

		ItemStackRepository.instance.registerClass(this, ChromaStacks.class);

		TransmutationRecipes.instance.getClass();

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(ChromaOverlays.instance);
			MinecraftForge.EVENT_BUS.register(OreOverlayRenderer.instance);
			MinecraftForge.EVENT_BUS.register(StructureErrorOverlays.instance);
			FMLCommonHandler.instance().bus().register(OreOverlayRenderer.instance);
			FMLCommonHandler.instance().bus().register(MobSonarRenderer.instance);
			FMLCommonHandler.instance().bus().register(StructureErrorOverlays.instance);
			MinecraftForge.EVENT_BUS.register(MobSonarRenderer.instance);
			ParticleEngine.instance.register();
		}

		rainbowforest = new BiomeRainbowForest(ExtraChromaIDs.RAINBOWFOREST.getValue());
		BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(rainbowforest, ChromaOptions.getRainbowForestWeight()));
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(rainbowforest, ChromaOptions.getRainbowForestWeight()));
		BiomeManager.addSpawnBiome(rainbowforest);
		BiomeManager.addStrongholdBiome(rainbowforest);
		BiomeManager.addVillageBiome(rainbowforest, true);
		BiomeDictionary.registerBiomeType(rainbowforest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.HILLS);

		enderforest = new BiomeEnderForest(ExtraChromaIDs.ENDERFOREST.getValue());
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(enderforest, ChromaOptions.getEnderForestWeight()));
		BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(enderforest, ChromaOptions.getEnderForestWeight()));
		BiomeManager.addSpawnBiome(enderforest);
		BiomeManager.addStrongholdBiome(enderforest);
		BiomeManager.addVillageBiome(enderforest, true);
		BiomeDictionary.registerBiomeType(enderforest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MAGICAL);

		glowingcliffs = new BiomeGlowingCliffs(ExtraChromaIDs.LUMINOUSCLIFFS.getValue(), true);
		//BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(glowingcliffs, ChromaOptions.getGlowingCliffsWeight()));
		//BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(glowingcliffs, ChromaOptions.getGlowingCliffsWeight()));
		//BiomeManager.addSpawnBiome(glowingcliffs);
		BiomeManager.addStrongholdBiome(glowingcliffs);

		glowingcliffsEdge = new GlowingCliffsEdge(ExtraChromaIDs.LUMINOUSEDGE.getValue());

		ReikaBiomeHelper.addChildBiome(glowingcliffs, glowingcliffsEdge);

		//replace 1/8 of jungle and 1/8 of Mega Taiga, for a total of 4/(16-2) = 28% net spawn rate of either Jungle or MT
		//revised to 1/16th of each for 2/15 = 13% spawn rate
		SpecialBiomePlacementRegistry.instance.registerID(this, Category.WARM, 2, glowingcliffs.biomeID);
		//SpecialBiomePlacementRegistry.instance.registerID(this, Category.WARM, 3, glowingcliffs.biomeID);
		SpecialBiomePlacementRegistry.instance.registerID(this, Category.COOL, 2, glowingcliffs.biomeID);
		//SpecialBiomePlacementRegistry.instance.registerID(this, Category.COOL, 3, glowingcliffs.biomeID);
		BiomeDictionary.registerBiomeType(glowingcliffs, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.LUSH, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.WET);
		BiomeDictionary.registerBiomeType(glowingcliffsEdge, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.LUSH, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.WET);

		ChromaDimensionManager.initialize();

		RetroGenController.instance.addHybridGenerator(PylonGenerator.instance, Integer.MIN_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(DungeonGenerator.instance, Integer.MAX_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(DataTowerGenerator.instance, Integer.MAX_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(DataTowerGenerator.instance, Integer.MIN_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(NetherStructureGenerator.instance, Integer.MAX_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(GlowingCliffsAuxGenerator.instance, Integer.MIN_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(SkypeaterGenerator.instance, Integer.MAX_VALUE, ChromaOptions.RETROGEN.getState());
		//RetroGenController.instance.addHybridGenerator(UnknownArtefactGenerator.instance, Integer.MIN_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(WarpNodeGenerator.instance, Integer.MAX_VALUE, ChromaOptions.RETROGEN.getState());

		this.addRerunnableDecorator(CrystalGenerator.instance, 0);
		this.addRerunnableDecorator(ColorTreeGenerator.instance, -10);
		this.addRerunnableDecorator(TieredWorldGenerator.instance, Integer.MIN_VALUE);
		this.addRerunnableDecorator(DecoFlowerGenerator.instance, Integer.MIN_VALUE);
		this.addRerunnableDecorator(LumaGenerator.instance, Integer.MAX_VALUE);
		this.addRerunnableDecorator(CaveIndicatorGenerator.instance, Integer.MAX_VALUE);

		//ReikaEntityHelper.overrideEntity(EntityChromaEnderCrystal.class, "EnderCrystal", 0);

		VillagersFailChromatiCraft.register();
		for (int i = 0; i < 4; i++)
			VillagerRegistry.instance().registerVillageTradeHandler(i, VillageTradeHandler.instance);
		for (int i : VillagerRegistry.instance().getRegisteredVillagers())
			VillagerRegistry.instance().registerVillageTradeHandler(i, VillageTradeHandler.instance);

		ChromaChests.addToChests();

		if (!this.isLocked())
			;//RotaryNames.addNames();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ChromaGuiHandler());
		this.addTileEntities();
		this.addEntities();

		if (!this.isLocked()) {
			ChromaRecipes.addRecipes();
		}

		//if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
		ChromaDescriptions.loadData();
		//}

		PackModificationTracker.instance.addMod(this, config);

		if (!this.isLocked())
			IntegrityChecker.instance.addMod(instance, ChromaBlocks.blockList, ChromaItems.itemList);

		if (!this.isLocked()) {
			TickRegistry.instance.registerTickHandler(ChromabilityHandler.instance);
			TickRegistry.instance.registerTickHandler(CrystalNetworker.instance);
			TickRegistry.instance.registerTickHandler(ExplorationMonitor.instance);
			TickRegistry.instance.registerTickHandler(ChromaDimensionTicker.instance);
			//TickRegistry.instance.registerTickHandler(LightingRerenderer.instance);
			//TickRegistry.instance.registerTickHandler(ChunkResetter.instance);
			TickRegistry.instance.registerTickHandler(ArtefactSpawner.instance);
			//TickRegistry.instance.registerTickHandler(TunnelNukerSpawner.instance);
			if (ModList.THAUMCRAFT.isLoaded())
				TickRegistry.instance.registerTickHandler(NodeRecharger.instance);
			MinecraftForge.EVENT_BUS.register(AbilityHelper.instance);
			FMLCommonHandler.instance().bus().register(AbilityHelper.instance);
			AbilityHelper.instance.register();
			PlayerHandler.instance.registerTracker(PylonCacheLoader.instance);
			PlayerHandler.instance.registerTracker(DimensionJoinHandler.instance);
			PlayerHandler.instance.registerTracker(ProgressionLoadHandler.instance);
			if (ModList.VOIDMONSTER.isLoaded() && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				TickRegistry.instance.registerTickHandler(VoidMonsterRitualClientEffects.instance);
		}

		if (ChromaOptions.HANDBOOK.getState())
			PlayerFirstTimeTracker.addTracker(new ChromaBookSpawner());

		for (int i = 0; i < 16; i++) {
			ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
			ItemStack used = ChromaOptions.isVanillaDyeMoreCommon() ? dye.getStackOf() : ChromaItems.DYE.getStackOfMetadata(i);
			ItemStack sapling = new ItemStack(ChromaBlocks.DYESAPLING.getBlockInstance(), 1, i);
			ItemStack flower = new ItemStack(ChromaBlocks.DYEFLOWER.getBlockInstance(), 1, i);
			ItemStack leaf = new ItemStack(ChromaBlocks.DYELEAF.getBlockInstance(), 1, i);
			if (!ReikaItemHelper.matchStacks(used, ReikaItemHelper.lapisDye))
				GameRegistry.addRecipe(new ItemStack(ChromaBlocks.DYE.getBlockInstance(), 1, i), "ddd", "ddd", "ddd", 'd', used);
			GameRegistry.addShapelessRecipe(used, flower);
			OreDictionary.registerOre(dye.getOreDictName(), ChromaItems.DYE.getStackOfMetadata(i));
			OreDictionary.registerOre("treeSapling", sapling);
			OreDictionary.registerOre("treeLeaves", leaf);
			OreDictionary.registerOre("plant"+dye.colorNameNoSpaces, flower);
			OreDictionary.registerOre("flower"+dye.colorNameNoSpaces, flower);
			OreDictionary.registerOre("flower", flower);
			FurnaceFuelRegistry.instance.registerItemSimple(sapling, 0.5F);
		}

		FurnaceFuelRegistry.instance.registerItemSimple(ChromaStacks.firaxite, 24);

		this.addDyeCompat();

		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.leaves);

		if (ModList.ATG.isLoaded()) {
			ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "Forest", rainbowforest, 1.0);
			ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "Forest", enderforest, 1.0);
			ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "Cliffs", glowingcliffs, 0.125);
		}

		if (ModList.BLUEPOWER.isLoaded()) { //prevent what is nearly an exploit by uncrafting gold apples
			ModInteraction.blacklistGoldAppleUncrafting();
		}

		LuaMethod.registerMethods("Reika.ChromatiCraft.ModInterface.Lua");

		//ReikaEEHelper.blacklistRegistry(ChromaBlocks.blockList);
		//ReikaEEHelper.blacklistRegistry(ChromaItems.itemList);

		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.FORESTRY, "Access to crystal bees which have valuable genetics");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.TWILIGHT, "Dense crystal generation and other worldgen hooks");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.THAUMCRAFT, "High crystal aspect values and extensive mod interaction");

		FMLInterModComms.sendMessage("aura", "lootblacklist", ChromaItems.FRAGMENT.getStackOf());
		FMLInterModComms.sendMessage("aura", "lootblacklist", ChromaItems.SHARD.getStackOf());

		FMLInterModComms.sendMessage("Randomod", "blacklist", this.getModContainer().getModId());

		ModInteraction.addMicroblocks();

		DonatorController.instance.registerMod(this, DonatorController.reikaURL);

		if (ModList.MYSTCRAFT.isLoaded()) {
			ModInteraction.addMystCraft();
		}

		if (ModList.RFTOOLS.isLoaded()) {
			ModInteraction.modifyRFToolsPages();
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			ChromaAspectManager.instance.PUZZLE.getName(); //init to register the two aspects
		}

		for (int i = 0; i < ChromaItems.itemList.length; i++) {
			ChromaItems ir = ChromaItems.itemList[i];
			if (!ir.isDummiedOut() && ir != ChromaItems.TOOL) {
				SensitiveItemRegistry.instance.registerItem(this, ir.getItemInstance(), false);
			}
		}

		for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
			ChromaBlocks ir = ChromaBlocks.blockList[i];
			SensitiveItemRegistry.instance.registerItem(this, ir.getBlockInstance(), false);
		}

		if (MTInteractionManager.isMTLoaded()) {
			MTInteractionManager.instance.blacklistRecipeRemovalFor(ChromaTiles.TABLE.getCraftedProduct());

			MTInteractionManager.instance.blacklistOreDictTagsFor(ChromaItems.SHARD.getItemInstance());
			MTInteractionManager.instance.blacklistOreDictTagsFor(ChromaItems.TIERED.getItemInstance());
			MTInteractionManager.instance.blacklistOreDictTagsFor(ChromaBlocks.CRYSTAL.getBlockInstance());
		}

		SensitiveFluidRegistry.instance.registerFluid("chroma");
		SensitiveFluidRegistry.instance.registerFluid("ender");
		SensitiveFluidRegistry.instance.registerFluid("potion crystal");
		SensitiveFluidRegistry.instance.registerFluid("luma");
		SensitiveFluidRegistry.instance.registerFluid("lumen");

		ReikaEEHelper.blacklistEntry(ChromaItems.TIERED);
		ReikaEEHelper.blacklistEntry(ChromaItems.SHARD);
		ReikaEEHelper.blacklistEntry(ChromaBlocks.TIEREDORE);
		ReikaEEHelper.blacklistEntry(ChromaBlocks.TIEREDPLANT);
		ReikaEEHelper.blacklistEntry(ChromaBlocks.CRYSTAL);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			ReikaJavaLibrary.initClassWithSubs(ChromaFontRenderer.class);

		if (ModList.ENDERIO.isLoaded()) {
			ModInteraction.blacklistTravelStaff();
		}

		if (ModList.APPENG.isLoaded()) {
			MESystemReader.registerMESystemEffect(UABombingEffects.instance.createMESystemEffect());
		}

		ChunkProviderChroma.regenerateGenerators();
		RosettaStone.init.test();

		this.finishTiming();
	}

	private static void addRerunnableDecorator(ChromaDecorator d, int wt) {
		RetroGenController.instance.addHybridGenerator(d, wt, ChromaOptions.RETROGEN.getState());
		decorators.put(d.getCommandID().toLowerCase(Locale.ENGLISH), d);
	}

	private void addEntities() {
		ReikaRegistryHelper.registerModEntities(instance, ChromaEntities.entityList);
		//ReikaEntityHelper.removeEntityFromRegistry(EntityEnderCrystal.class);
		//EntityList.addMapping(EntityChromaEnderCrystal.class, "EnderCrystal", 200);
	}

	private void addDyeCompat() {
		if (ModList.TREECAPITATOR.isLoaded()) {
			TreeCapitatorHandler.register();
		}

		if (ModList.ROTARYCRAFT.isLoaded()) {
			ModInteraction.addRCGPRColors();
		}

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack is = new ItemStack(ChromaBlocks.DYE.getBlockInstance(), 1, i);
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", is);
		}
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);

		if (!this.isLocked()) {
			ChromaRecipes.addPostLoadRecipes();
		}

		proxy.addDonatorRender();

		ModCropList.addCustomCropType(new CrystalPlantHandler());

		TileEntityBiomePainter.buildBiomeList();
		TileEntityOreCreator.initOreMap();
		TileEntityTeleportationPump.buildProgressionMap();
		ItemDuplicationWand.loadMappings();

		ReikaDispenserHelper.addDispenserAction(ChromaItems.TOOL.getStackOf(), new ManipulatorDispenserAction());
		ProjectileToolDispenserAction proj = new ProjectileToolDispenserAction();
		ReikaDispenserHelper.addDispenserAction(ChromaItems.SPLASHGUN.getStackOf(), proj);
		ReikaDispenserHelper.addDispenserAction(ChromaItems.VACUUMGUN.getStackOf(), proj);
		ReikaDispenserHelper.addDispenserAction(ChromaItems.CHAINGUN.getStackOf(), proj);

		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i] instanceof LoadRegistry) {
				((LoadRegistry)blocks[i]).onLoad();
			}
		}

		//CliffFogRenderer.instance.initialize();
		GlowingCliffsAuxGenerator.instance.initialize();

		WorldGenInterceptionRegistry.instance.addWatcher(ChromaAux.populationWatcher);
		WorldGenInterceptionRegistry.instance.addIWGWatcher(ChromaAux.slimeIslandBlocker);
		WorldGenInterceptionRegistry.instance.addException(ChromaAux.dimensionException);

		IconLookupRegistry.instance.registerIcons(instance, ChromaIcons.class);
		IconLookupRegistry.instance.registerIcons(instance, CrystalElement.class);

		ItemMagicRegistry.instance.addPostload();

		if (ModList.THAUMCRAFT.isLoaded()) {
			ModInteraction.addThaumCraft();

			TileEntityAspectFormer.initCapacity();
		}

		if (ModList.BLOODMAGIC.isLoaded() && BloodMagicHandler.getInstance().soulFrayID != -1) {
			CrystalPotionController.addIgnoredPotion(Potion.potionTypes[BloodMagicHandler.getInstance().soulFrayID]);
		}

		if (Loader.isModLoaded("TardisMod")) {
			ModInteraction.blacklistTardisFromDimension();
		}

		if (Loader.isModLoaded("dsurround")) {
			ModInteraction.setDynSurroundSettings();
		}

		if (ModList.MEKANISM.isLoaded()) {
			ModInteraction.blacklistMekBoxes();
		}

		if (ModList.FORESTRY.isLoaded()) {
			ModInteraction.addForestry();
		}

		if (ModList.IC2.isLoaded()) {
			IC2ReactorAcceleration.instance.register();
		}
		MultiblockAcceleration.instance.register();

		if (ModList.TWILIGHT.isLoaded()) {
			ModInteraction.addTFLoot();
		}

		if (Loader.isModLoaded("chocolateQuest")) {
			ModInteraction.addChocoDungeonLoot();
		}

		if (ModList.BOTANIA.isLoaded()) {
			ModInteraction.blacklistLoonium();
		}

		if (ModList.ROTARYCRAFT.isLoaded()) {
			ReservoirAPI.registerHandler(new ChromaPrepHandler());
			ReservoirAPI.registerHandler(new ShardBoostingHandler());
			ReservoirAPI.registerHandler(new PoolRecipeHandler());
		}

		if (ModList.METEORCRAFT.isLoaded()) {
			MeteorSpawnAPI.blacklistDimension(ExtraChromaIDs.DIMID.getValue());
		}

		if (ModList.VOIDMONSTER.isLoaded()) {
			DimensionAPI.blacklistDimensionForSounds(ExtraChromaIDs.DIMID.getValue());
			DimensionAPI.blacklistBiomeForSounds(ExtraChromaIDs.RAINBOWFOREST.getValue());
			DimensionAPI.blacklistBiomeForSounds(ExtraChromaIDs.LUMINOUSCLIFFS.getValue());
			DimensionAPI.blacklistBiomeForSounds(ExtraChromaIDs.LUMINOUSEDGE.getValue());
			DimensionAPI.setDimensionRuleForSpawning(ExtraChromaIDs.DIMID.getValue(), false);
			MonsterAPI.addDrop(ChromaStacks.voidmonsterEssence, 24, 64);
		}

		if (ModList.MINEFACTORY.isLoaded()) {
			ModInteraction.blacklistMFRSafariNet();
		}

		if (ModList.CARPENTER.isLoaded()) {
			ModInteraction.addCarpenterCovers();
		}

		if (ModList.RFTOOLS.isLoaded()) {
			ModInteraction.blacklistRFToolsTeleport();
		}

		if (ModList.TINKERER.isLoaded() && (Pulses.TOOLS.isLoaded() || Pulses.WEAPONS.isLoaded())) {
			ModInteraction.addChromastoneTools();
		}

		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles m = ChromaTiles.TEList[i];
			TimeTorchHelper.blacklistTileEntity(m.getTEClass());
		}

		ChromaResearch.loadPostCache();

		this.finishTiming();
	}

	public static boolean isRainbowForest(BiomeGenBase b) {
		return b instanceof BiomeRainbowForest || (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.getMystParentBiome(b) instanceof BiomeRainbowForest);
	}

	public static boolean isEnderForest(BiomeGenBase b) {
		return b instanceof BiomeEnderForest || (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.getMystParentBiome(b) instanceof BiomeEnderForest);
	}

	@EventHandler
	public void registerCommands(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new GuardianCommand());
		evt.registerServerCommand(new ProgressModifyCommand());
		evt.registerServerCommand(new NetworkLoggerCommand());
		evt.registerServerCommand(new StructureGenCommand());
		evt.registerServerCommand(new DimensionGeneratorCommand());
		evt.registerServerCommand(new RecipeReloadCommand());
		evt.registerServerCommand(new PylonCacheCommand());
		evt.registerServerCommand(new StructureCacheCommand());
		evt.registerServerCommand(new CrystalNetCommand());
		evt.registerServerCommand(new ReshufflePylonCommand());
		evt.registerServerCommand(new RedecorateCommand());
		evt.registerServerCommand(new PlaceStructureCommand());
		evt.registerServerCommand(new NodeWrapperInspectionCommand());

		if (MinecraftServer.getServer() != null && !MinecraftServer.getServer().isServerInOnlineMode()) {
			isOfflineMode = true;
			PopupWriter.instance.addMessage("ChromatiCraft does not properly work in offline mode! Ownership data is not properly read/saved and some things will not work!");
		}
		else {
			isOfflineMode = false;
		}

		DungeonGenerator.instance.initLevelData(evt.getServer());
		ProgressionLoadHandler.instance.initLevelData(evt.getServer());
		ProgressionLoadHandler.instance.load();
		OreOverlayRenderer.instance.loadOres();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGameLoaded(GameFinishedLoadingEvent evt) {
		OreOverlayRenderer.instance.loadOres();
	}

	@EventHandler
	public void overrideRecipes(FMLServerStartedEvent evt) {
		if (!this.isLocked()) {
			RecipesCastingTable.instance.reload();
		}
	}

	@EventHandler
	public void serverShutdown(FMLServerStoppingEvent evt) {
		ProgressionLoadHandler.instance.saveAll();
		//if (MinecraftServer.getServer().isDedicatedServer())
		ChromaDimensionManager.serverStopping = true;
	}

	private void setupClassFiles() {
		setupLiquids();

		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, ChromaBlocks.blockList, blocks);
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, ChromaItems.itemList, items);
		ReikaRegistryHelper.instantiateAndRegisterEnchantments(instance, ChromaEnchants.enchantmentList, enchants);

		ChromaTiles.loadMappings();
		ChromaBlocks.loadMappings();
		ChromaItems.loadMappings();

		setupLiquidContainers();

		//Block b = Blocks.mob_spawner;
		//Items.itemsList[b.blockID] = new ItemNBTSpawner(b.blockID).setUnlocalizedName(Items.itemsList[b.blockID].getUnlocalizedName());
	}

	private static void setupLiquids() {
		logger.log("Loading And Registering Liquids");
		FluidRegistry.registerFluid(chroma);
		//FluidRegistry.registerFluid(activechroma);
		FluidRegistry.registerFluid(crystal);
		FluidRegistry.registerFluid(ender);
		FluidRegistry.registerFluid(luma);
		FluidRegistry.registerFluid(lumen);
	}

	private static void setupLiquidContainers() {
		logger.log("Loading And Registering Liquid Containers");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(chroma, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOf(), new ItemStack(Items.bucket));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(luma, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOfMetadata(3), new ItemStack(Items.bucket));
		if (!ModList.THERMALFOUNDATION.isLoaded())
			FluidContainerRegistry.registerFluidContainer(new FluidStack(ender, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOfMetadata(1), new ItemStack(Items.bucket));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(crystal, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOfMetadata(2), new ItemStack(Items.bucket));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(lumen, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOfMetadata(4), new ItemStack(Items.bucket));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void setupExtraIcons(TextureStitchEvent.Pre event) {
		if (!this.isLocked()) {
			logger.log("Loading Additional Icons");

			if (event.map.getTextureType() == 0) {
				IIcon sicon = event.map.registerIcon("ChromatiCraft:fluid/chroma");
				IIcon ficon = event.map.registerIcon("ChromatiCraft:fluid/chroma_flowing");
				chroma.setIcons(sicon, ficon);

				IIcon cry = event.map.registerIcon("ChromatiCraft:fluid/liqcrystal3");
				crystal.setIcons(cry);

				IIcon lum = event.map.registerIcon("ChromatiCraft:fluid/lumen");
				lumen.setIcons(lum);

				IIconRegister ico = event.map;
				ender.setStillIcon(ico.registerIcon("ChromatiCraft:fluid/ender"));
				ender.setFlowingIcon(ico.registerIcon("ChromatiCraft:fluid/flowingender"));

				luma.setIcons(ico.registerIcon("ChromatiCraft:fluid/aether/aether_full"), ico.registerIcon("ChromatiCraft:fluid/aether/aether_flow2"));
				//aether.setFlowingIcon(ico.registerIcon("ChromatiCraft:fluid/flowingender"));

				for (int i = 0; i < CrystalElement.elements.length; i++) {
					CrystalElement.elements[i].setIcons(event.map);
				}
			}
			ChromaIcons.registerAll(event.map);
		}
	}

	public static Block getEnderBlockToGenerate() {
		if (ModList.THERMALFOUNDATION.isLoaded() && ThermalHandler.getInstance().enderID != null) {
			return ThermalHandler.getInstance().enderID;
		}
		return ChromaBlocks.ENDER.getBlockInstance();
	}

	private void addTileEntities() {
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles c = ChromaTiles.TEList[i];
			if (c == ChromaTiles.ADJACENCY) {
				for (int k = 0; k < 16; k++) {
					if (AdjacencyUpgrades.upgrades[k].isImplemented()) {
						String label = "CC"+c.getUnlocalizedName().toLowerCase(Locale.ENGLISH).replaceAll("\\s","")+"_"+k;
						Class cl = AdjacencyUpgrades.upgrades[k].getTileClass();
						GameRegistry.registerTileEntity(cl, label);
						ReikaJavaLibrary.initClass(cl);
					}
				}
			}
			else {
				String label = "CC"+c.getUnlocalizedName().toLowerCase(Locale.ENGLISH).replaceAll("\\s","");
				GameRegistry.registerTileEntity(c.getTEClass(), label);
				ReikaJavaLibrary.initClass(c.getTEClass());
			}
		}
		for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
			ChromaBlocks b = ChromaBlocks.blockList[i];
			Class c = b.getObjectClass();
			Class[] cs = c.getClasses();
			if (cs != null) {
				for (int k = 0; k < cs.length; k++) {
					Class in = cs[k];
					if (TileEntity.class.isAssignableFrom(in) && (in.getModifiers() & Modifier.ABSTRACT) == 0) {
						String s = "CC"+in.getSimpleName();
						GameRegistry.registerTileEntity(in, s);
					}
				}
			}
		}
		GameRegistry.registerTileEntity(TileEntityCrystalPlant.class, "CCCrystalPlant");
	}

	@Override
	public String getDisplayName() {
		return "ChromatiCraft";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage();
	}

	@Override
	public String getWiki() {
		return "http://ChromatiCraft.wikia.com/wiki/ChromatiCraft_Wiki";
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

	@Override
	public String getUpdateCheckURL() {
		return CommandableUpdateChecker.reikaURL;
	}

	public static ChromaDecorator getDecorator(String id) {
		return decorators.get(id);
	}

	@Override
	public File getConfigFolder() {
		return config.getConfigFolder();
	}

	@Override
	protected Class<? extends IClassTransformer> getASMClass() {
		return ChromaASMHandler.ASMExecutor.class;
	}
}
