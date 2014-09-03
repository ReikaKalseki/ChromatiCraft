/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.net.URL;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.IIcon;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import ttftcuts.atg.api.ATGBiomes;
import ttftcuts.atg.api.ATGBiomes.BiomeType;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaLock;
import Reika.ChromatiCraft.Auxiliary.GuardianCommand;
import Reika.ChromatiCraft.Auxiliary.GuardianStoneManager;
import Reika.ChromatiCraft.Auxiliary.TabChromatiCraft;
import Reika.ChromatiCraft.Block.BlockCrystalTank.CrystalTankAuxTile;
import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.ModInterface.CrystalDyeAspectManager;
import Reika.ChromatiCraft.ModInterface.TreeCapitatorHandler;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPlant;
import Reika.ChromatiCraft.World.BiomeEnderForest;
import Reika.ChromatiCraft.World.BiomeRainbowForest;
import Reika.ChromatiCraft.World.ColorTreeGenerator;
import Reika.ChromatiCraft.World.CrystalGenerator;
import Reika.ChromatiCraft.World.CustomEndProvider;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.BiomeCollisionTracker;
import Reika.DragonAPI.Auxiliary.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.CompatibilityTracker;
import Reika.DragonAPI.Auxiliary.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.PlayerHandler;
import Reika.DragonAPI.Auxiliary.SuggestedModsTracker;
import Reika.DragonAPI.Auxiliary.TickRegistry;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.EnhancedFluid;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.BannedItemReader;
import Reika.DragonAPI.ModInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.ThermalHandler;
import Reika.RotaryCraft.API.BlockColorInterface;
import cpw.mods.fml.common.FMLCommonHandler;
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


@Mod( modid = "ChromatiCraft", name="ChromatiCraft", version="Alpha", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")

public class ChromatiCraft extends DragonAPIMod {
	public static final String packetChannel = "ChromaData";

	public static final TabChromatiCraft tabChroma = new TabChromatiCraft(CreativeTabs.getNextID(), "ChromatiCraft");
	public static final TabChromatiCraft tabChromaTools = new TabChromatiCraft(CreativeTabs.getNextID(), "ChromatiCraft Tools");
	public static final TabChromatiCraft tabChromaItems = new TabChromatiCraft(CreativeTabs.getNextID(), "ChromatiCraft Items");

	static final Random rand = new Random();

	private boolean isLocked = false;

	public static final EnhancedFluid chroma = (EnhancedFluid)new EnhancedFluid("chroma").setColor(0x00aaff).setViscosity(300).setTemperature(400).setDensity(300);
	public static EnhancedFluid crystal = (EnhancedFluid)new EnhancedFluid("potion crystal").setColor(0x66aaff).setGameName("Crystal").setLuminosity(15).setTemperature(500);
	public static final Fluid ender = new Fluid("ender").setViscosity(2000).setDensity(1500).setTemperature(270).setUnlocalizedName("endere");

	public static final Block[] blocks = new Block[ChromaBlocks.blockList.length];
	public static final Item[] items = new Item[ChromaItems.itemList.length];

	public static Achievement[] achievements;

	public static final Material enderMat = new Material(MapColor.ironColor);

	@Instance("ChromatiCraft")
	public static ChromatiCraft instance = new ChromatiCraft();

	public static final ChromaConfig config = new ChromaConfig(instance, ChromaOptions.optionList, null, 0);

	public static ModLogger logger;

	public static BiomeRainbowForest rainbowforest;
	public static BiomeEnderForest enderforest;

	//private String version;

	@SidedProxy(clientSide="Reika.ChromatiCraft.ChromaClient", serverSide="Reika.ChromatiCraft.ChromaCommon")
	public static ChromaCommon proxy;

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
		MinecraftForge.EVENT_BUS.register(GuardianStoneManager.instance);
		MinecraftForge.EVENT_BUS.register(ChromaticEventManager.instance);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			MinecraftForge.EVENT_BUS.register(ChromaClientEventController.instance);

		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		proxy.registerSounds();

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

		logger = new ModLogger(instance, false);

		BiomeCollisionTracker.instance.addBiomeID(instance, ChromaOptions.RAINBOWFORESTID.getValue(), BiomeRainbowForest.class);
		BiomeCollisionTracker.instance.addBiomeID(instance, ChromaOptions.ENDERFORESTID.getValue(), BiomeEnderForest.class);

		this.setupClassFiles();
		ChromaTiles.loadMappings();
		ChromaBlocks.loadMappings();
		ChromaItems.loadMappings();
		//DimensionManager.registerProviderType(1, CustomEndProvider.class, false); if ASM turns out to be impossible

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ChromatiPackets());

		tabChroma.setIcon(ChromaItems.RIFT.getStackOf());
		tabChromaTools.setIcon(ChromaItems.TOOL.getStackOf());
		tabChromaItems.setIcon(ReikaItemHelper.redDye);

		if (!this.isLocked()) {
			//if (ConfigRegistry.ACHIEVEMENTS.getState()) {
			//		achievements = new Achievement[RotaryAchievements.list.length];
			//		RotaryAchievements.registerAchievements();
			//	}
		}

		CompatibilityTracker.instance.registerIncompatibility(ModList.CHROMATICRAFT, ModList.OPTIFINE, CompatibilityTracker.Severity.GLITCH, "Optifine is known to break some rendering and cause framerate drops.");

		this.basicSetup(evt);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		ChromaRecipes.loadDictionary();
		if (this.isLocked())
			PlayerHandler.instance.registerTracker(ChromaLock.instance);
		if (!this.isLocked()) {
			proxy.addArmorRenders();
			proxy.registerRenderers();
		}

		rainbowforest = new BiomeRainbowForest(ChromaOptions.RAINBOWFORESTID.getValue());
		BiomeManager.addSpawnBiome(rainbowforest);
		BiomeDictionary.registerBiomeType(rainbowforest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.HILLS);

		enderforest = new BiomeEnderForest(ChromaOptions.ENDERFORESTID.getValue());
		BiomeManager.addSpawnBiome(enderforest);
		BiomeDictionary.registerBiomeType(rainbowforest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MAGICAL);

		GameRegistry.registerWorldGenerator(new CrystalGenerator(), 0);
		GameRegistry.registerWorldGenerator(new ColorTreeGenerator(), -10);
		GameRegistry.registerWorldGenerator(new PylonGenerator(), Integer.MIN_VALUE);

		DimensionManager.unregisterProviderType(1);
		DimensionManager.registerProviderType(1, CustomEndProvider.class, false);

		ReikaEntityHelper.overrideEntity(EntityChromaEnderCrystal.class, "EnderCrystal", 0);

		if (!this.isLocked())
			;//RotaryNames.addNames();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ChromaGuiHandler());
		this.addTileEntities();
		;//RotaryRegistration.addEntities();

		if (!this.isLocked()) {
			ChromaRecipes.addRecipes();
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			ChromaDescriptions.loadData();

		if (!this.isLocked())
			IntegrityChecker.instance.addMod(instance, ChromaBlocks.blockList, ChromaItems.itemList);

		if (!this.isLocked()) {
			TickRegistry.instance.registerTickHandler(ChromabilityHandler.instance, Side.SERVER);
			TickRegistry.instance.registerTickHandler(CrystalNetworker.instance, Side.SERVER);
			MinecraftForge.EVENT_BUS.register(AbilityHelper.instance);
		}

		//if (ConfigRegistry.HANDBOOK.getState())
		;//PlayerFirstTimeTracker.addTracker(new ChromatiBookTracker());

		ReikaMystcraftHelper.disableFluidPage("chroma");

		for (int i = 0; i < 16; i++) {
			ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
			ItemStack used = ChromaOptions.isVanillaDyeMoreCommon() ? dye.getStackOf() : ChromaItems.DYE.getStackOfMetadata(i);
			ItemStack sapling = new ItemStack(ChromaBlocks.DYESAPLING.getBlockInstance(), 1, i);
			ItemStack flower = new ItemStack(ChromaBlocks.DYEFLOWER.getBlockInstance(), 1, i);
			GameRegistry.addRecipe(new ItemStack(ChromaBlocks.DYE.getBlockInstance(), 1, i), "ddd", "ddd", "ddd", 'd', used);
			GameRegistry.addShapelessRecipe(used, flower);
			OreDictionary.registerOre(dye.getOreDictName(), ChromaItems.DYE.getStackOfMetadata(i));
			OreDictionary.registerOre("treeSapling", sapling);
			OreDictionary.registerOre("plant"+dye.colorNameNoSpaces, flower);
			OreDictionary.registerOre("flower"+dye.colorNameNoSpaces, flower);
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

		ATGBiomes.addBiome(BiomeType.LAND, "Forest", rainbowforest, 1.0);
		ATGBiomes.addBiome(BiomeType.LAND, "Forest", enderforest, 1.0);

		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.FORESTRY, "Access to crystal bees which have valuable genetics");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.TWILIGHT, "Dense crystal generation");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.THAUMCRAFT, "High crystal aspect values");
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

		if (!this.isLocked()) {
			ChromaRecipes.addPostLoadRecipes();
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			CrystalDyeAspectManager.register();
		}

		if (ModList.FORESTRY.isLoaded()) {
			CrystalBees.register();
		}
	}

	public static boolean isRainbowForest(BiomeGenBase b) {
		return b instanceof BiomeRainbowForest || b.biomeName.equals("Rainbow Forest");
	}

	@EventHandler
	public void registerCommands(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new GuardianCommand());
	}

	@EventHandler
	public void overrideRecipes(FMLServerStartedEvent evt) {
		if (!this.isLocked()) {

		}
	}

	private void setupClassFiles() {
		setupLiquids();
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, ChromaBlocks.blockList, blocks);
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, ChromaItems.itemList, items);
		setupLiquidContainers();

		//Block b = Blocks.mob_spawner;
		//Items.itemsList[b.blockID] = new ItemNBTSpawner(b.blockID).setUnlocalizedName(Items.itemsList[b.blockID].getUnlocalizedName());
	}

	private static void setupLiquids() {
		logger.log("Loading And Registering Liquids");
		FluidRegistry.registerFluid(chroma);
		FluidRegistry.registerFluid(crystal);
		FluidRegistry.registerFluid(ender);
	}

	private static void setupLiquidContainers() {
		logger.log("Loading And Registering Liquid Containers");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(chroma, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOf(), new ItemStack(Items.bucket));
		if (!ModList.THERMALEXPANSION.isLoaded())
			FluidContainerRegistry.registerFluidContainer(new FluidStack(ender, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOfMetadata(1), new ItemStack(Items.bucket));
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
				ender.setStillIcon(ico.registerIcon("chromaticraft:fluid/ender"));
				ender.setFlowingIcon(ico.registerIcon("chromaticraft:fluid/flowingender"));
				ender.setBlock(ChromaBlocks.ENDER.getBlockInstance());

				for (int i = 0; i < CrystalElement.elements.length; i++) {
					CrystalElement.elements[i].setIcons(event.map);
				}
				ChromaIcons.registerAll(event.map);
			}
		}
	}

	public Block getEnderBlockToGenerate() {
		if (ModList.THERMALEXPANSION.isLoaded() && ThermalHandler.getInstance().enderID != null) {
			return ThermalHandler.getInstance().enderID;
		}
		return ChromaBlocks.ENDER.getBlockInstance();
	}

	private void addTileEntities() {
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			String label = "CC"+ChromaTiles.TEList[i].getUnlocalizedName().toLowerCase().replaceAll("\\s","");
			GameRegistry.registerTileEntity(ChromaTiles.TEList[i].getTEClass(), label);
			ReikaJavaLibrary.initClass(ChromaTiles.TEList[i].getTEClass());
		}
		GameRegistry.registerTileEntity(TileEntityCrystalPlant.class, "CCCrystalPlant");
		GameRegistry.registerTileEntity(CrystalTankAuxTile.class, "CCTankAux");
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
}
