/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import mekanism.api.MekanismAPI;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import ttftcuts.atg.api.ATGBiomes;
import vazkii.botania.api.BotaniaAPI;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper.LoginApplier;
import Reika.ChromatiCraft.Auxiliary.ChromaBookSpawner;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.ChromaHelpHUD;
import Reika.ChromatiCraft.Auxiliary.ChromaLock;
import Reika.ChromatiCraft.Auxiliary.ChromaOverlays;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.CrystalMaterial;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.NetworkLoggerCommand;
import Reika.ChromatiCraft.Auxiliary.CrystalPlantHandler;
import Reika.ChromatiCraft.Auxiliary.ExplorationMonitor;
import Reika.ChromatiCraft.Auxiliary.FragmentTab;
import Reika.ChromatiCraft.Auxiliary.GuardianStoneManager;
import Reika.ChromatiCraft.Auxiliary.ManipulatorDispenserAction;
import Reika.ChromatiCraft.Auxiliary.MusicLoader;
import Reika.ChromatiCraft.Auxiliary.PylonCacheLoader;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Auxiliary.PylonFinderOverlay;
import Reika.ChromatiCraft.Auxiliary.TabChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Command.CrystalNetCommand;
import Reika.ChromatiCraft.Auxiliary.Command.DimensionGeneratorCommand;
import Reika.ChromatiCraft.Auxiliary.Command.GuardianCommand;
import Reika.ChromatiCraft.Auxiliary.Command.ProgressModifyCommand;
import Reika.ChromatiCraft.Auxiliary.Command.PylonCacheCommand;
import Reika.ChromatiCraft.Auxiliary.Command.RecipeReloadCommand;
import Reika.ChromatiCraft.Auxiliary.Command.RedecorateCommand;
import Reika.ChromatiCraft.Auxiliary.Command.ReshufflePylonCommand;
import Reika.ChromatiCraft.Auxiliary.Command.StructureGenCommand;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaDecorator;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LoadRegistry;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionBetterSaturation;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionCustomRegen;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionGrowthHormone;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.TransmutationRecipes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemDuplicationWand;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer.PlayerEnergyCommand;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.ModInterface.ChromaAspectManager;
import Reika.ChromatiCraft.ModInterface.ChromaAspectMapper;
import Reika.ChromatiCraft.ModInterface.CrystalWand;
import Reika.ChromatiCraft.ModInterface.MystPages;
import Reika.ChromatiCraft.ModInterface.NodeRecharger;
import Reika.ChromatiCraft.ModInterface.ReservoirHandlers.ChromaPrepHandler;
import Reika.ChromatiCraft.ModInterface.ReservoirHandlers.PoolRecipeHandler;
import Reika.ChromatiCraft.ModInterface.ReservoirHandlers.ShardBoostingHandler;
import Reika.ChromatiCraft.ModInterface.TieredOreCap;
import Reika.ChromatiCraft.ModInterface.TreeCapitatorHandler;
import Reika.ChromatiCraft.ModInterface.Bees.ApiaryAcceleration;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.ModInterface.Lua.ChromaLuaMethods;
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
import Reika.ChromatiCraft.Render.ParticleEngine;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.World.BiomeEnderForest;
import Reika.ChromatiCraft.World.BiomeRainbowForest;
import Reika.ChromatiCraft.World.ColorTreeGenerator;
import Reika.ChromatiCraft.World.CrystalGenerator;
import Reika.ChromatiCraft.World.DecoFlowerGenerator;
import Reika.ChromatiCraft.World.DungeonGenerator;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.ChromatiCraft.World.TieredWorldGenerator;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionTicker;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.DimensionJoinHandler;
import Reika.ChromatiCraft.World.Nether.NetherStructureGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.CreativeTabSorter;
import Reika.DragonAPI.Auxiliary.Trackers.BiomeCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.ConfigMatcher;
import Reika.DragonAPI.Auxiliary.Trackers.FurnaceFuelRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerFirstTimeTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.PotionCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Auxiliary.Trackers.SuggestedModsTracker;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Extras.PseudoAirMaterial;
import Reika.DragonAPI.Instantiable.EnhancedFluid;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaDispenserHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.ModInteract.BannedItemReader;
import Reika.DragonAPI.ModInteract.ItemStackRepository;
import Reika.DragonAPI.ModInteract.ReikaEEHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MTInteractionManager;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.SensitiveFluidRegistry;
import Reika.DragonAPI.ModInteract.DeepInteract.SensitiveItemRegistry;
import Reika.DragonAPI.ModInteract.DeepInteract.TimeTorchHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.TwilightForestLootHooks;
import Reika.DragonAPI.ModInteract.DeepInteract.TwilightForestLootHooks.LootLevels;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler.Potions;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThermalHandler;
import Reika.DragonAPI.ModRegistry.ModCropList;
import Reika.MeteorCraft.API.MeteorSpawnAPI;
import Reika.RotaryCraft.API.BlockColorInterface;
import Reika.RotaryCraft.API.ReservoirAPI;
import Reika.VoidMonster.API.DimensionAPI;

import com.chocolate.chocolateQuest.API.RegisterChestItem;
import com.chocolate.chocolateQuest.API.WeightedItemStack;

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
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



@Mod( modid = "ChromatiCraft", name="ChromatiCraft", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")

public class ChromatiCraft extends DragonAPIMod {
	public static final String packetChannel = "ChromaData";

	public static final TabChromatiCraft tabChroma = new TabChromatiCraft("ChromatiCraft");
	public static final TabChromatiCraft tabChromaDeco = new TabChromatiCraft("ChromatiCraft Deco");
	public static final TabChromatiCraft tabChromaGen = new TabChromatiCraft("ChromatiCraft Worldgen");
	public static final TabChromatiCraft tabChromaTools = new TabChromatiCraft("ChromatiCraft Tools");
	public static final TabChromatiCraft tabChromaItems = new TabChromatiCraft("ChromatiCraft Items");
	public static final FragmentTab tabChromaFragments = new FragmentTab("CC Info Fragments");

	static final Random rand = new Random();

	private boolean isLocked = false;

	public static final EnhancedFluid chroma = (EnhancedFluid)new EnhancedFluid("chroma").setColor(0x00aaff).setViscosity(300).setTemperature(288).setDensity(300).setLuminosity(15);
	//public static final EnhancedFluid activechroma = (EnhancedFluid)new EnhancedFluid("activechroma").setColor(0x00aaff).setViscosity(300).setTemperature(500).setDensity(300);
	public static EnhancedFluid crystal = (EnhancedFluid)new EnhancedFluid("potion crystal").setColor(0x66aaff).setLuminosity(15).setTemperature(500).setUnlocalizedName("potioncrystal");
	public static final Fluid ender = new Fluid("ender").setViscosity(2000).setDensity(1500).setTemperature(270).setUnlocalizedName("endere").setLuminosity(4);

	public static final Block[] blocks = new Block[ChromaBlocks.blockList.length];
	public static final Item[] items = new Item[ChromaItems.itemList.length];
	public static final Enchantment[] enchants = new Enchantment[ChromaEnchants.enchantmentList.length];

	public static Achievement[] achievements;

	public static final Material crystalMat = new CrystalMaterial();
	public static final Material airMat = PseudoAirMaterial.instance;

	private static final HashMap<String, ChromaDecorator> decorators = new HashMap();

	public static PotionGrowthHormone growth;
	public static PotionBetterSaturation betterSat;
	public static PotionCustomRegen betterRegen;

	public static final PylonDamage[] pylonDamage = new PylonDamage[16];

	@Instance("ChromatiCraft")
	public static ChromatiCraft instance = new ChromatiCraft();

	public static final ChromaConfig config = new ChromaConfig(instance, ChromaOptions.optionList, ExtraChromaIDs.idList);

	public static ModLogger logger;

	public static BiomeRainbowForest rainbowforest;
	public static BiomeEnderForest enderforest;

	@SidedProxy(clientSide="Reika.ChromatiCraft.ChromaClient", serverSide="Reika.ChromatiCraft.ChromaCommon")
	public static ChromaCommon proxy;

	private boolean dimensionLoadable = true;

	public boolean isDimensionLoadable() {
		return dimensionLoadable;
	}

	public final boolean isLocked() {
		return isLocked;
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

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.startTiming(LoadPhase.PRELOAD);
		this.verifyInstallation();

		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);

		logger = new ModLogger(instance, false);
		if (DragonOptions.FILELOG.getState())
			logger.setOutput("**_Loading_Log.log");

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MusicLoader.instance.registerAssets();
		}

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
			MinecraftForge.EVENT_BUS.register(ChromaHelpHUD.instance);
			MinecraftForge.EVENT_BUS.register(PylonFinderOverlay.instance);
		}

		isLocked = this.checkForLock();
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

		int id = ExtraChromaIDs.GROWTHID.getValue();
		PotionCollisionTracker.instance.addPotionID(instance, id, PotionGrowthHormone.class);
		growth = new PotionGrowthHormone(id);

		id = ExtraChromaIDs.SATID.getValue();
		PotionCollisionTracker.instance.addPotionID(instance, id, PotionBetterSaturation.class);
		betterSat = new PotionBetterSaturation(id);

		id = ExtraChromaIDs.REGENID.getValue();
		PotionCollisionTracker.instance.addPotionID(instance, id, PotionCustomRegen.class);
		betterRegen = new PotionCustomRegen(id);

		BiomeCollisionTracker.instance.addBiomeID(instance, ExtraChromaIDs.RAINBOWFOREST.getValue(), BiomeRainbowForest.class);
		BiomeCollisionTracker.instance.addBiomeID(instance, ExtraChromaIDs.ENDERFOREST.getValue(), BiomeEnderForest.class);

		this.setupClassFiles();
		//ChromaResearch.loadCache();

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ChromatiPackets());

		proxy.registerKeys();

		tabChroma.setIcon(ChromaItems.RIFT.getStackOf());
		tabChromaDeco.setIcon(ChromaTiles.CHROMAFLOWER.getCraftedProduct());
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

		FMLInterModComms.sendMessage("zzzzzcustomconfigs", "blacklist-mod-as-output", this.getModContainer().getModId());

		ConfigMatcher.instance.addConfigList(this, ChromaOptions.optionList);

		this.basicSetup(evt);
		this.finishTiming();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.startTiming(LoadPhase.LOAD);
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
			ParticleEngine.instance.register();
		}

		rainbowforest = new BiomeRainbowForest(ExtraChromaIDs.RAINBOWFOREST.getValue());
		BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(rainbowforest, ChromaOptions.getRainbowForestWeight()));
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(rainbowforest, ChromaOptions.getRainbowForestWeight()));
		BiomeManager.addSpawnBiome(rainbowforest);
		BiomeDictionary.registerBiomeType(rainbowforest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.HILLS);

		enderforest = new BiomeEnderForest(ExtraChromaIDs.ENDERFOREST.getValue());
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(enderforest, ChromaOptions.getEnderForestWeight()));
		BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(enderforest, ChromaOptions.getEnderForestWeight()));
		BiomeManager.addSpawnBiome(enderforest);
		BiomeDictionary.registerBiomeType(enderforest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MAGICAL);

		ChromaDimensionManager.initialize();

		RetroGenController.instance.addHybridGenerator(PylonGenerator.instance, Integer.MIN_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(DungeonGenerator.instance, Integer.MAX_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(NetherStructureGenerator.instance, Integer.MAX_VALUE, ChromaOptions.RETROGEN.getState());

		this.addRerunnableDecorator(CrystalGenerator.instance, 0);
		this.addRerunnableDecorator(ColorTreeGenerator.instance, -10);
		this.addRerunnableDecorator(TieredWorldGenerator.instance, Integer.MIN_VALUE);
		this.addRerunnableDecorator(DecoFlowerGenerator.instance, Integer.MIN_VALUE);

		//ReikaEntityHelper.overrideEntity(EntityChromaEnderCrystal.class, "EnderCrystal", 0);

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
			if (ModList.THAUMCRAFT.isLoaded())
				TickRegistry.instance.registerTickHandler(NodeRecharger.instance);
			MinecraftForge.EVENT_BUS.register(AbilityHelper.instance);
			FMLCommonHandler.instance().bus().register(AbilityHelper.instance);
			PlayerHandler.instance.registerTracker(LoginApplier.instance);
			PlayerHandler.instance.registerTracker(PylonCacheLoader.instance);
			PlayerHandler.instance.registerTracker(DimensionJoinHandler.instance);
		}

		if (ChromaOptions.HANDBOOK.getState())
			PlayerFirstTimeTracker.addTracker(new ChromaBookSpawner());

		for (int i = 0; i < 16; i++) {
			ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
			ItemStack used = ChromaOptions.isVanillaDyeMoreCommon() ? dye.getStackOf() : ChromaItems.DYE.getStackOfMetadata(i);
			ItemStack sapling = new ItemStack(ChromaBlocks.DYESAPLING.getBlockInstance(), 1, i);
			ItemStack flower = new ItemStack(ChromaBlocks.DYEFLOWER.getBlockInstance(), 1, i);
			ItemStack leaf = new ItemStack(ChromaBlocks.DYELEAF.getBlockInstance(), 1, i);
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

		if (ChromaOptions.doesVanillaDyeDrop()) {

		}
		else {/*
			logger.log("Configs were set such that trees do not drop vanilla dyes! Loading interface recipes to ensure farmability!");
			for (int i = 0; i < 16; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				Object[] in = this.getIntercraft(dye);
				Object[] sub = new Object[in.length-1];
				System.arraycopy(in, 1, sub, 0, sub.length);
				boolean shaped = (Boolean)in[0];
				if (shaped) {
					GameRegistry.addRecipe(dye.getStackOf(), sub);
				}
				else {
					GameRegistry.addShapelessRecipe(dye.getStackOf(), sub);
				}
			}*/
		}

		this.addDyeCompat();

		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.leaves);

		if (ModList.ATG.isLoaded()) {
			ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "Forest", rainbowforest, 1.0);
			ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "Forest", enderforest, 1.0);
		}

		if (ModList.BLUEPOWER.isLoaded()) { //prevent what is nearly an exploit by uncrafting gold apples
			try {
				Class c = Class.forName("com.bluepowermod.recipe.AlloyFurnaceRegistry");
				Field inst = c.getDeclaredField("INSTANCE");
				inst.setAccessible(true);
				Object instance = inst.get(null);
				Field list = c.getDeclaredField("blacklist");
				list.setAccessible(true);
				List li = (List)list.get(instance);
				li.add("minecraft:golden_apple");
			}
			catch (Exception e) {
				logger.logError("Unable to blacklist golden apple recycling");
				e.printStackTrace();
			}
		}

		ReikaJavaLibrary.initClass(ChromaLuaMethods.class);

		//ReikaEEHelper.blacklistRegistry(ChromaBlocks.blockList);
		//ReikaEEHelper.blacklistRegistry(ChromaItems.itemList);

		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.FORESTRY, "Access to crystal bees which have valuable genetics");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.TWILIGHT, "Dense crystal generation and other worldgen hooks");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.THAUMCRAFT, "High crystal aspect values and extensive mod interaction");

		FMLInterModComms.sendMessage("aura", "lootblacklist", ChromaItems.FRAGMENT.getStackOf());
		FMLInterModComms.sendMessage("aura", "lootblacklist", ChromaItems.SHARD.getStackOf());

		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(0));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(1));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(2));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(7));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(8));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(10));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(11));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(12));

		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.CLOAK.ordinal()));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.STONE.ordinal()));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.MOSS.ordinal()));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.CRACKS.ordinal()));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.COBBLE.ordinal()));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.LIGHT.ordinal()));


		if (ModList.MYSTCRAFT.isLoaded()) {
			ReikaMystcraftHelper.registerPageRegistry(MystPages.instance);
			for (int i = 0; i < Biomes.biomeList.length; i++) {
				ReikaMystcraftHelper.disableBiomePage(Biomes.biomeList[i].getBiome());
			}
			for (int i = 0; i < SubBiomes.biomeList.length; i++) {
				ReikaMystcraftHelper.disableBiomePage(SubBiomes.biomeList[i].getBiome());
			}
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

		ReikaEEHelper.blacklistEntry(ChromaItems.TIERED);
		ReikaEEHelper.blacklistEntry(ChromaItems.SHARD);
		ReikaEEHelper.blacklistEntry(ChromaBlocks.TIEREDORE);
		ReikaEEHelper.blacklistEntry(ChromaBlocks.TIEREDPLANT);
		ReikaEEHelper.blacklistEntry(ChromaBlocks.CRYSTAL);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			ReikaJavaLibrary.initClassWithSubs(ChromaFontRenderer.class);

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
			for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.DECAY.getBlockInstance(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.DYELEAF.getBlockInstance(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.DYE.getBlockInstance(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.DYESAPLING.getBlockInstance(), i, dye.color);
			}
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
		ItemDuplicationWand.loadMappings();
		ChunkProviderChroma.regenerateGenerators();

		ReikaDispenserHelper.addDispenserAction(ChromaItems.TOOL.getStackOf(), new ManipulatorDispenserAction());

		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i] instanceof LoadRegistry) {
				((LoadRegistry)blocks[i]).onLoad();
			}
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			ChromaAspectManager.instance.register();
			ChromaAspectMapper.instance.register();

			new CrystalWand().setGlowing(true);
			TieredOreCap.registerAll();

			ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.STRUCTSHIELD.getBlockInstance());
			ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.SPECIALSHIELD.getBlockInstance());
			ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.RUNE.getBlockInstance());
			ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.PYLONSTRUCT.getBlockInstance());
			ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.PYLON.getBlockInstance());

			for (int i = 0; i < ThaumIDHandler.Potions.list.length; i++) {
				Potions p = ThaumIDHandler.Potions.list[i];
				if (p.isWarpRelated() && p.getID() != -1)
					CrystalPotionController.addIgnoredPotion(Potion.potionTypes[p.getID()]);
			}
		}

		if (ModList.BLOODMAGIC.isLoaded() && BloodMagicHandler.getInstance().soulFrayID != -1) {
			CrystalPotionController.addIgnoredPotion(Potion.potionTypes[BloodMagicHandler.getInstance().soulFrayID]);
		}

		if (ModList.MEKANISM.isLoaded()) {
			for (int i = 0; i < 16; i++) {
				try {
					MekanismAPI.addBoxBlacklist(ChromaBlocks.PYLON.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.SPECIALSHIELD.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.POWERTREE.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.PORTAL.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.CONSOLE.getBlockInstance(), i);

					MekanismAPI.addBoxBlacklist(ChromaBlocks.COLORLOCK.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.DIMDATA.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.LOCKFREEZE.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.LOCKKEY.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.GOL.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.GOLCONTROL.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.MUSICMEMORY.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.COLORLOCK.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.SHIFTKEY.getBlockInstance(), i);
					MekanismAPI.addBoxBlacklist(ChromaBlocks.SHIFTLOCK.getBlockInstance(), i);
				}
				catch (Exception e) {
					logger.logError("Unable to blacklist tiles from Mekanism box");
					e.printStackTrace();
				}
				catch (LinkageError e) {
					logger.logError("Unable to blacklist tiles from Mekanism box");
					e.printStackTrace();
				}
			}
		}

		if (ModList.FORESTRY.isLoaded()) {
			try {
				CrystalBees.register();
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.logError("Could not add Forestry integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
			}
			catch (LinkageError e) {
				e.printStackTrace();
				logger.logError("Could not add Forestry integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
			}
			ApiaryAcceleration.instance.register();
		}

		if (ModList.TWILIGHT.isLoaded()) {
			for (int i = 0; i < 16; i++) {
				ItemStack is = ChromaItems.SHARD.getStackOfMetadata(i);
				int rarity = CrystalElement.elements[i].isPrimary() ? 20 : 10;
				TwilightForestLootHooks.DungeonTypes.TREE_DUNGEON.addItem(is, LootLevels.COMMON, rarity);
				TwilightForestLootHooks.DungeonTypes.RUINS_BASEMENT.addItem(is, LootLevels.COMMON, rarity*6/5);
			}

			TwilightForestLootHooks.DungeonTypes.TREE_DUNGEON.addItem(ChromaItems.FRAGMENT.getStackOf(), LootLevels.COMMON, 8);
			TwilightForestLootHooks.DungeonTypes.RUINS_BASEMENT.addItem(ChromaItems.FRAGMENT.getStackOf(), LootLevels.COMMON, 12);
			TwilightForestLootHooks.DungeonTypes.HEDGE_MAZE.addItem(ChromaItems.FRAGMENT.getStackOf(), LootLevels.COMMON, 4);
			TwilightForestLootHooks.DungeonTypes.LICH_LIBRARY.addItem(ChromaItems.FRAGMENT.getStackOf(), LootLevels.COMMON, 15);
			TwilightForestLootHooks.DungeonTypes.LABYRINTH_VAULT.addItem(ChromaItems.FRAGMENT.getStackOf(), LootLevels.COMMON, 15);
			TwilightForestLootHooks.DungeonTypes.LABYRINTH_END.addItem(ChromaItems.FRAGMENT.getStackOf(), LootLevels.COMMON, 8);
			TwilightForestLootHooks.DungeonTypes.SMALL_HOLLOW.addItem(ChromaItems.FRAGMENT.getStackOf(), LootLevels.COMMON, 6);
			TwilightForestLootHooks.DungeonTypes.MEDIUM_HOLLOW.addItem(ChromaItems.FRAGMENT.getStackOf(), LootLevels.COMMON, 8);
			TwilightForestLootHooks.DungeonTypes.LARGE_HOLLOW.addItem(ChromaItems.FRAGMENT.getStackOf(), LootLevels.COMMON, 12);

			TwilightForestLootHooks.DungeonTypes.HEDGE_MAZE.addItem(ChromaBlocks.GLOWSAPLING.getStackOf(), LootLevels.RARE, 6);
			TwilightForestLootHooks.DungeonTypes.TREE_DUNGEON.addItem(ChromaBlocks.GLOWSAPLING.getStackOf(), LootLevels.ULTRARARE, 2);
		}

		if (Loader.isModLoaded("chocolateQuest")) {
			try {
				RegisterChestItem.treasureList.add(new WeightedItemStack(ChromaItems.FRAGMENT.getStackOf(), 40));

				for (int i = 0; i < 16; i++) {
					ItemStack is = ChromaItems.SHARD.getStackOfMetadata(i);
					int wt = CrystalElement.elements[i].isPrimary() ? 50 : 25;
					RegisterChestItem.mineralList.add(new WeightedItemStack(is, wt));
				}
			}
			catch (IncompatibleClassChangeError e) {
				e.printStackTrace();
				logger.logError("Could not add ChocolateQuest integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.logError("Could not add ChocolateQuest integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
			}
		}

		if (ModList.BOTANIA.isLoaded()) {
			try {
				BotaniaAPI.blackListItemFromLoonium(ChromaItems.FRAGMENT.getItemInstance());
				BotaniaAPI.blackListItemFromLoonium(ChromaItems.SHARD.getItemInstance());
			}
			catch (LinkageError e) {
				e.printStackTrace();
				logger.logError("Could not add Botania integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.logError("Could not add Botania integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
			}
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
			DimensionAPI.blacklistDimensionForSpawning(ExtraChromaIDs.DIMID.getValue());
		}

		if (ModList.MINEFACTORY.isLoaded()) {
			try {
				Class c = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
				Method m = c.getMethod("registerSafariNetBlacklist", Class.class);
				m.invoke(null, EntityBallLightning.class);
				m = c.getMethod("registerAutoSpawnerBlacklistClass", Class.class);
				m.invoke(null, EntityBallLightning.class);
			}
			catch (Exception e) {
				logger.logError("Could not blacklist Ball Lightning from MFR Safari Net!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.MINEFACTORY, e);
				e.printStackTrace();
			}
		}

		if (ModList.CARPENTER.isLoaded()) {
			try {
				Class c = Class.forName("com.carpentersblocks.util.registry.FeatureRegistry");
				Field f = c.getDeclaredField("coverExceptions");
				f.setAccessible(true);
				List<String> li = (List<String>)f.get(null);
				for (int i = 0; i < 16; i++)
					li.add(ChromaBlocks.GLASS.getStackOfMetadata(i).getDisplayName());
			}
			catch (Exception e) {
				logger.logError("Could not add compatibility with Carpenter's blocks!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.CARPENTER, e);
				e.printStackTrace();
			}
		}

		if (ModList.ENDERIO.isLoaded()) {
			try {
				Class c = Class.forName("crazypants.enderio.config.Config");
				Field f = c.getField("travelStaffBlinkBlackList");
				String[] arr = (String[])f.get(null);
				String[] next = new String[arr.length+3];
				System.arraycopy(arr, 0, next, 0, arr.length);
				next[next.length-3] = ReikaRegistryHelper.getGameRegistryName(ChromaBlocks.STRUCTSHIELD);
				next[next.length-2] = ReikaRegistryHelper.getGameRegistryName(ChromaBlocks.SPECIALSHIELD);
				next[next.length-1] = ReikaRegistryHelper.getGameRegistryName(ChromaBlocks.DOOR);
				f.set(null, next);
			}
			catch (Exception e) {
				logger.logError("Could not add EnderIO travelling staff blacklisting!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.ENDERIO, e);
				e.printStackTrace();
			}
		}

		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles m = ChromaTiles.TEList[i];
			TimeTorchHelper.blacklistTileEntity(m.getTEClass());
		}

		ChromaResearch.loadPostCache();

		this.finishTiming();
	}

	public static boolean isRainbowForest(BiomeGenBase b) {
		return b instanceof BiomeRainbowForest || b.biomeName.equals("Rainbow Forest");
	}

	@EventHandler
	public void registerCommands(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new GuardianCommand());
		evt.registerServerCommand(new ProgressModifyCommand());
		evt.registerServerCommand(new NetworkLoggerCommand());
		evt.registerServerCommand(new PlayerEnergyCommand());
		evt.registerServerCommand(new StructureGenCommand());
		evt.registerServerCommand(new DimensionGeneratorCommand());
		evt.registerServerCommand(new RecipeReloadCommand());
		evt.registerServerCommand(new PylonCacheCommand());
		evt.registerServerCommand(new CrystalNetCommand());
		evt.registerServerCommand(new ReshufflePylonCommand());
		evt.registerServerCommand(new RedecorateCommand());
	}

	@EventHandler
	public void overrideRecipes(FMLServerStartedEvent evt) {
		if (!this.isLocked()) {
			RecipesCastingTable.instance.reload();
		}
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
	}

	private static void setupLiquidContainers() {
		logger.log("Loading And Registering Liquid Containers");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(chroma, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOf(), new ItemStack(Items.bucket));
		if (!ModList.THERMALFOUNDATION.isLoaded())
			FluidContainerRegistry.registerFluidContainer(new FluidStack(ender, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOfMetadata(1), new ItemStack(Items.bucket));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(crystal, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOfMetadata(2), new ItemStack(Items.bucket));
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

				IIconRegister ico = event.map;
				ender.setStillIcon(ico.registerIcon("ChromatiCraft:fluid/ender"));
				ender.setFlowingIcon(ico.registerIcon("ChromatiCraft:fluid/flowingender"));
				ender.setBlock(ChromaBlocks.ENDER.getBlockInstance());

				for (int i = 0; i < CrystalElement.elements.length; i++) {
					CrystalElement.elements[i].setIcons(event.map);
				}
				ChromaIcons.registerAll(event.map);
			}
		}
	}

	public Block getEnderBlockToGenerate() {
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
					if (TileEntity.class.isAssignableFrom(in)) {
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
}
