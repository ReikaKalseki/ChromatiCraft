/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Event.CastingRecipesReloadEvent;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.AvoLampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.ChromaFlowerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CompoundRelayRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CompoundRuneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CrystalAltarRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CrystalGlassRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CrystalGlowRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CrystalLampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CrystalStoneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.DoorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.FakeSkyRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.FenceAuxRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.FloatingRelayRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.HeatLampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.LumenLampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.MusicTriggerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.PathRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.PortalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.PotionCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RecipeEnderTNT;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RecipeTankBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RedstonePodRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RelayFilterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RelayRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RepeaterLampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RouterNodeRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RuneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.SelectiveGlassRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalCellRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalClusterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalFocusRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalGroupRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalLensRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalMirrorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalSeedRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalStarRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.DoorKeyRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.ElementUnitRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.EnergyCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.HighEnergyCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.HighTransformationCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.HighVoidCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.IridescentChunkRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.LumenChunkRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.LumenCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.RawCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.ThrowableGemRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.TransformationCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.VoidCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.VoidStorageRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.ConfigRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.DoubleJumpRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.EnchantmentRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.RepeaterTurboRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.TinkerToolPartRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.AdjacencyRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.AspectFormerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.AspectJarRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.AutomatorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.AvoLaserRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.BatteryRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.BeaconRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.BiomePainterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CaveLighterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.ChromaCollectorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.ChromaCrafterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CloakTowerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CobbleGenRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CompoundRepeaterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CropSpeedPlantRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalBrewerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalChargerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalFenceRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalFurnaceRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalLaserRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalTankRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.EnchantDecompRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.EnchanterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.EssentiaRelayRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FabricatorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FarmerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FluidDistributorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FluidRelayRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FluxMakerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FocusCrystalRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FocusCrystalRecipes.DefaultFocusCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FunctionRelayRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.GlowFireRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.GuardianStoneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.HarvestPlantRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.HeatLilyRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.InfuserRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.InvTickerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.IridescentCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.ItemCollectorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.ItemInserterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.LampControlRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.LampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.LumenAlvearyRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.LumenBroadcastRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.LumenTurretRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.LumenWireRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.MEDistributorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.MeteorTowerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.MinerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.MultiBuilderRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.MusicRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.PageExtractorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.ParticleSpawnerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.PlantAccelerationRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.PylonTurboRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RFDistributorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RecipeAreaBreaker;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RecipeCrystalRepeater;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RecipePersonalCharger;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RelaySourceRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.ReversionLotusRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RiftRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RitualTableRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RouterHubRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.SpawnerReprogrammerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.StandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.TelePumpRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.TransportWindowRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.WarpGateRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.WeakRepeaterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.WirelessTransmitterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.AuraCleanerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.AuraPouchRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.BeeFrameRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.BuilderWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.CaptureWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.CavePatherRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.ChainGunRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.DuplicationWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.EfficiencyCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.EnderCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.EnhancedPendantRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.ExcavatorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.FloatstoneBootsRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.GrowthWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.InventoryLinkRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.KillAuraRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.LinkToolRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.MobSonarRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.MultiToolRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.OrePickRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.OreSilkerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.OwnerKeyRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.PendantRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.PurifyCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.PylonFinderRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.RecipeHoverWand;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.RecipeItemMover;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.SplashGunRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.SplineAttackRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.StorageCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.StructureFinderRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.TeleportWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.TintedLensRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.TransitionRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.VacuumGunRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.WarpCapsuleRecipe;
import Reika.ChromatiCraft.Base.ItemChromaBasic;
import Reika.ChromatiCraft.Block.BlockPath;
import Reika.ChromatiCraft.Block.BlockPath.PathType;
import Reika.ChromatiCraft.Block.Crystal.BlockCrystalGlow.Bases;
import Reika.ChromatiCraft.Block.Decoration.BlockRepeaterLight;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.ModInterface.CrystalBackpack;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.CustomRecipeList;
import Reika.DragonAPI.Instantiable.IO.LuaBlock;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.ToolParts;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.WeaponParts;
import Reika.DragonAPI.ModRegistry.PowerTypes;
import Reika.RotaryCraft.Registry.ItemRegistry;

import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.registry.GameRegistry;

public class RecipesCastingTable {

	public static final RecipesCastingTable instance = new RecipesCastingTable();

	private final HashMap<RecipeType, OneWayList<CastingRecipe>> recipes = new HashMap();
	private final OneWayList<CastingRecipe> APIrecipes = new OneWayList();
	private final ArrayList<CastingRecipe> moddedItemRecipes = new ArrayList();

	private int maxID = 0;
	private final HashBiMap<Integer, CastingRecipe> recipeIDs;

	private int maxEnergyCost = 0;
	private int maxTotalEnergyCost = 0;

	private RecipesCastingTable() {
		recipeIDs = HashBiMap.create();
		this.loadRecipes();
	}

	private void loadRecipes() {
		if (ChromatiCraft.instance.isLocked())
			return;

		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.redGroup, CrystalElement.RED, CrystalElement.BLUE, CrystalElement.PURPLE, CrystalElement.MAGENTA, ChromaStacks.auraDust, false));
		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.greenGroup, CrystalElement.YELLOW, CrystalElement.CYAN, CrystalElement.LIME, CrystalElement.GREEN, ChromaStacks.livingEssence, false));
		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.orangeGroup, CrystalElement.BROWN, CrystalElement.PINK, CrystalElement.ORANGE, CrystalElement.LIGHTBLUE, ChromaStacks.chromaDust, false));
		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.whiteGroup, CrystalElement.BLACK, CrystalElement.GRAY, CrystalElement.LIGHTGRAY, CrystalElement.WHITE, ChromaStacks.icyDust, false));

		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.redGroup, CrystalElement.RED, CrystalElement.BLUE, CrystalElement.PURPLE, CrystalElement.MAGENTA, ChromaStacks.auraDust, true));
		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.greenGroup, CrystalElement.YELLOW, CrystalElement.CYAN, CrystalElement.LIME, CrystalElement.GREEN, ChromaStacks.livingEssence, true));
		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.orangeGroup, CrystalElement.BROWN, CrystalElement.PINK, CrystalElement.ORANGE, CrystalElement.LIGHTBLUE, ChromaStacks.chromaDust, true));
		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.whiteGroup, CrystalElement.BLACK, CrystalElement.GRAY, CrystalElement.LIGHTGRAY, CrystalElement.WHITE, ChromaStacks.icyDust, true));

		this.addRecipe(new CrystalClusterRecipe(ChromaStacks.primaryCluster));
		this.addRecipe(new CrystalClusterRecipe(ChromaStacks.secondaryCluster));
		this.addRecipe(new CrystalCoreRecipe(ChromaStacks.crystalCore, new ItemStack(Items.diamond)));
		this.addRecipe(new CrystalStarRecipe(ChromaStacks.crystalStar, new ItemStack(Items.nether_star)));

		this.addRecipe(new VoidCoreRecipe(ChromaStacks.voidCore));
		this.addRecipe(new EnergyCoreRecipe(ChromaStacks.energyCore));
		this.addRecipe(new TransformationCoreRecipe(ChromaStacks.transformCore));

		this.addRecipe(new HighVoidCoreRecipe(ChromaStacks.voidCoreHigh));
		this.addRecipe(new HighEnergyCoreRecipe(ChromaStacks.energyCoreHigh));
		this.addRecipe(new HighTransformationCoreRecipe(ChromaStacks.transformCoreHigh));

		this.addRecipe(new LumenChunkRecipe(ChromaStacks.glowChunk, new ItemStack(Items.diamond)));
		this.addRecipe(new LumenCoreRecipe(ChromaStacks.lumenCore, ChromaStacks.crystalStar));

		this.addRecipe(new CrystalLensRecipe(ChromaStacks.crystalLens, new ItemStack(Blocks.glass)));

		this.addRecipe(new StorageCrystalRecipe(ChromaItems.STORAGE.getStackOf(), ChromaStacks.elementUnit));
		for (int i = 0; i < ChromaItems.STORAGE.getNumberMetadatas()-1; i++)
			this.addRecipe(new StorageCrystalRecipe(ChromaItems.STORAGE.getStackOfMetadata(i+1), ChromaItems.STORAGE.getStackOfMetadata(i)));

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];

			this.addRecipe(new RuneRecipe(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, i), i));
			this.addRecipe(new RuneRecipe(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 16, i), i+16));
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack seed = ChromaItems.SEED.getStackOfMetadata(i);
			ItemStack block = new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 8, 0);
			ItemStack lamp = new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i);

			IRecipe sr = new ShapedOreRecipe(block, " S ", "SCS", " S ", 'S', "stone", 'C', shard);
			this.addRecipe(new CrystalStoneRecipe(block, sr));

			this.addRecipe(new CrystalSeedRecipe(seed, e, false));
			this.addRecipe(new CrystalSeedRecipe(seed, e, true));

			this.addRecipe(new LumenLampRecipe(ChromaBlocks.LAMPBLOCK.getStackOfMetadata(i), e));

			this.addRecipe(new TintedLensRecipe(e, ChromaStacks.crystalLens, Items.iron_ingot, 1));
			this.addRecipe(new TintedLensRecipe(e, ChromaStacks.crystalLens, Items.gold_ingot, 2));
			this.addRecipe(new TintedLensRecipe(e, ChromaStacks.crystalLens, ChromaStacks.chromaIngot, 4));

			sr = ReikaRecipeHelper.getShapedRecipeFor(lamp, " s ", "sss", "SSS", 's', shard, 'S', ReikaItemHelper.stoneSlab);
			this.addRecipe(new CrystalLampRecipe(lamp, sr));

			this.addRecipe(new RelayRecipe(e));

			this.addRecipe(new ThrowableGemRecipe(e));

			for (int k = 0; k < Bases.baseList.length; k++) {
				Bases b = Bases.baseList[k];
				ItemStack glow = ChromaBlocks.GLOW.getStackOfMetadata(i+k*16);
				sr = ReikaRecipeHelper.getShapedRecipeFor(glow, "S", "s", 'S', shard, 's', b.ingredient);
				this.addRecipe(new CrystalGlowRecipe(glow, sr));
			}

			this.addRecipe(new CrystalAltarRecipe(e));

			this.addRecipe(new CrystalGlassRecipe(e));
		}
		this.addRecipe(new CompoundRelayRecipe(ChromaBlocks.RELAY.getStackOfMetadata(16), new ItemStack(Items.diamond)));

		this.addRecipe(new FloatingRelayRecipe(ChromaBlocks.FLOATINGRELAY.getStackOf(), ChromaBlocks.RELAY.getStackOfMetadata(16)));

		Block block = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		IRecipe sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 2, 2), "S", "S", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 2, 2), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 4, 12), "SS", "SS", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 4, 12), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 2, 1), "SS", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 2, 1), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 4, 7), " S ", "S S", " S ", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 4, 7), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 5, 8), " S ", "SSS", " S ", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 5, 8), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 5, 6), "SSS", "S  ", "S  ", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 5, 6), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 3, 11), "S", "S", "S", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 3, 11), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 3, 10), "SSS", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 3, 10), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 4, 14), "sSs", "ScS", "sSs", 'S', new ItemStack(block, 1, 0), 'c', ChromaStacks.chargedWhiteShard, 's', new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, 15));
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 4, 14), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 6, 15), "SSS", "ccc", "SSS", 'S', new ItemStack(block, 1, 0), 'c', ChromaStacks.chargedWhiteShard);
		this.addRecipe(new CrystalStoneRecipe(new ItemStack(block, 6, 15), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(ChromaItems.OREPICK.getStackOf(), "EPE", "cSc", "cSc", 'c', ChromaStacks.chromaDust, 'S', Items.stick, 'E', Items.ender_eye, 'P', Items.iron_pickaxe);
		this.addRecipe(new OrePickRecipe(ChromaItems.OREPICK.getStackOf(), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(ChromaItems.ORESILK.getStackOf(), "EPE", "cdc", "cSc", 'c', ChromaStacks.chromaDust, 'S', Items.stick, 'E', Items.emerald, 'P', Items.diamond_pickaxe, 'd', Items.diamond);
		this.addRecipe(new OreSilkerRecipe(ChromaItems.ORESILK.getStackOf(), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(ChromaItems.MULTITOOL.getStackOf(), "APS", "csc", "csc", 'c', ChromaStacks.chromaDust, 's', Items.stick, 'A', Items.iron_axe, 'S', Items.iron_shovel, 'P', Items.iron_pickaxe);
		this.addRecipe(new MultiToolRecipe(ChromaItems.MULTITOOL.getStackOf(), sr));

		this.addRecipe(new GuardianStoneRecipe(ChromaTiles.GUARDIAN.getCraftedProduct(), ChromaStacks.crystalStar));

		for (int i = 0; i < 16; i++) {
			if (AdjacencyUpgrades.upgrades[i].isImplemented()) {
				CrystalElement e = CrystalElement.elements[i];
				for (int k = 0; k < TileEntityAccelerator.MAX_TIER; k++) {
					this.addRecipe(new AdjacencyRecipe(e, k));
				}
			}
		}

		ItemStack is = ChromaTiles.STAND.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "I I", "SLS", "CCC", 'I', Items.iron_ingot, 'C', "cobblestone", 'S', ReikaItemHelper.stoneSlab, 'L', ReikaItemHelper.lapisDye);
		this.addRecipe(new StandRecipe(is, sr));

		is = ChromaTiles.ENCHANTER.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "eGe", "OEO", "BOB", 'e', Items.emerald, 'O', Blocks.obsidian, 'B', ReikaItemHelper.stoneBricks, 'E', Blocks.enchanting_table, 'G', Items.gold_ingot);
		this.addRecipe(new EnchanterRecipe(is, sr));

		is = ChromaTiles.BREWER.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "S S", "ScS", "CCC", 'C', "stone", 'c', Items.cauldron, 'S', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new CrystalBrewerRecipe(is, sr));

		is = ChromaTiles.CHROMAFLOWER.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "LFL", "GsG", 'L', "treeLeaves", 'F', "flower", 'G', Items.glowstone_dust, 's', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new ChromaFlowerRecipe(is, sr));

		is = ChromaBlocks.SELECTIVEGLASS.getStackOf();
		sr = new ShapedOreRecipe(is, "GgG", "beb", "GvG", 'G', Blocks.glass, 'g', Items.glowstone_dust, 'e', ChromaStacks.teleDust, 'v', ChromaStacks.voidDust, 'b', ChromaStacks.icyDust);
		this.addRecipe(new SelectiveGlassRecipe(is, sr));

		//sr = ReikaRecipeHelper.getShapedRecipeFor(ChromaStacks.crystalMirror, "GWI", "GWI", "GWI", 'G', Blocks.glass, 'I', Items.iron_ingot, 'W', ChromaItems.SHARD.getStackOfMetadata(15));
		this.addRecipe(new CrystalMirrorRecipe(ChromaStacks.crystalMirror, new ItemStack(Blocks.glass)));

		this.addRecipe(new RiftRecipe(ChromaTiles.RIFT.getCraftedProduct(), ChromaStacks.voidCore));
		this.addRecipe(new CrystalTankRecipe(ChromaTiles.TANK.getCraftedProduct(), ChromaStacks.voidCore));
		this.addRecipe(new RecipeTankBlock(new ItemStack(ChromaBlocks.TANK.getBlockInstance()), new ItemStack(Items.diamond)));
		this.addRecipe(new CrystalFurnaceRecipe(ChromaTiles.FURNACE.getCraftedProduct(), ChromaStacks.energyCore));
		this.addRecipe(new CrystalLaserRecipe(ChromaTiles.LASER.getCraftedProduct(), ChromaStacks.energyCore));
		this.addRecipe(new CrystalChargerRecipe(ChromaTiles.CHARGER.getCraftedProduct(), ChromaStacks.crystalCore));

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i);
			ItemStack cave = new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i);
			ItemStack supercry = new ItemStack(ChromaBlocks.SUPER.getBlockInstance(), 1, i);
			ItemStack seed = ChromaItems.SEED.getStackOfMetadata(i);

			this.addRecipe(new PendantRecipe(e));
			this.addRecipe(new EnhancedPendantRecipe(e));
			this.addRecipe(new PotionCrystalRecipe(e));

			sr = ReikaRecipeHelper.getShapedRecipeFor(ChromaStacks.rawCrystal, " F ", "FSF", " F ", 'F', ChromaStacks.purityDust, 'S', shard);
			this.addRecipe(new RawCrystalRecipe(ChromaStacks.rawCrystal, sr));
		}

		this.addRecipe(new CrystalFocusRecipe(ChromaStacks.crystalFocus, ChromaStacks.primaryCluster));
		this.addRecipe(new CrystalMirrorRecipe(ChromaStacks.crystalMirror, ChromaStacks.getChargedShard(CrystalElement.WHITE)));

		RecipeCrystalRepeater repeater = new RecipeCrystalRepeater(ChromaStacks.crystalCore);
		this.addRecipe(repeater);

		this.addRecipe(new TransitionRecipe(ChromaItems.TRANSITION.getStackOf(), ChromaStacks.transformCore));
		this.addRecipe(new ExcavatorRecipe(ChromaItems.EXCAVATOR.getStackOf(), ChromaStacks.energyCore));

		this.addRecipe(new ElementUnitRecipe(ChromaStacks.elementUnit, ChromaStacks.bindingCrystal));

		is = ChromaTiles.HEATLILY.getCraftedProduct();
		sr = new ShapedOreRecipe(is, " F ", "FBF", "LSL", 'L', Blocks.waterlily, 'F', "flower", 'S', ChromaStacks.orangeShard, 'B', Items.blaze_powder);
		HeatLilyRecipe hr = new HeatLilyRecipe(is, sr);
		this.addRecipe(hr);

		is = ChromaTiles.REVERTER.getCraftedProduct();
		sr = new ShapedOreRecipe(is, " F ", "FBF", "LSL", 'L', ReikaItemHelper.fern, 'F', "flower", 'S', ChromaStacks.greenShard, 'B', Items.redstone);
		this.addRecipe(new ReversionLotusRecipe(is, sr));

		is = ChromaTiles.COBBLEGEN.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "LSL", "FBF", " D ", 'L', "treeLeaves", 'D', Items.glowstone_dust, 'F', "flower", 'S', ChromaStacks.cyanShard, 'B', Blocks.glass);
		this.addRecipe(new CobbleGenRecipe(is, sr));

		is = ChromaTiles.PLANTACCEL.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "LSL", "FRF", "LSL", 'L', "treeLeaves", 'R', Items.redstone, 'F', "flower", 'S', ChromaStacks.lightBlueShard);
		this.addRecipe(new PlantAccelerationRecipe(is, sr));

		is = ChromaTiles.CROPSPEED.getCraftedProduct();
		sr = new ShapedOreRecipe(is, " R ", "FRF", "LSL", 'L', "treeLeaves", 'R', Items.redstone, 'F', "flower", 'S', ChromaStacks.lightBlueShard);
		this.addRecipe(new CropSpeedPlantRecipe(is, sr));

		is = ChromaTiles.RITUAL.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "SES", "CSC", "CCC", 'C', "cobblestone", 'S', ChromaItems.SHARD.getAnyMetaStack(), 'E', ChromaStacks.energyPowder);
		this.addRecipe(new RitualTableRecipe(is, sr));

		is = ChromaTiles.COLLECTOR.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "SES", "ScS", "CCC", 'E', Items.ender_eye, 'C', "stone", 'c', Blocks.glowstone, 'S', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new ChromaCollectorRecipe(is, sr));

		this.addRecipe(new SpawnerReprogrammerRecipe(ChromaTiles.REPROGRAMMER.getCraftedProduct(), ChromaStacks.transformCoreHigh));

		this.addRecipe(new TelePumpRecipe(ChromaTiles.TELEPUMP.getCraftedProduct(), ChromaStacks.energyCoreHigh));

		this.addRecipe(new CompoundRepeaterRecipe(ChromaStacks.crystalFocus));

		//is = ChromaTiles.FIBER.getCraftedProduct();
		//sr = ReikaRecipeHelper.getShapedRecipeFor(is, "GgG", "GDG", "GgG", 'G', Blocks.glass, 'D', Items.diamond, 'g', Items.glowstone_dust);
		//this.addRecipe(new FiberRecipe(is, sr));

		this.addRecipe(new CompoundRuneRecipe(new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 1, 13), ChromaStacks.bindingCrystal));

		this.addRecipe(new IridescentChunkRecipe(ChromaStacks.iridChunk, ChromaStacks.bindingCrystal));

		this.addRecipe(new IridescentCrystalRecipe(ChromaTiles.CRYSTAL.getCraftedProduct(), new ItemStack(Items.diamond)));

		this.addRecipe(new InfuserRecipe(ChromaTiles.INFUSER.getCraftedProduct(), ChromaTiles.STAND.getCraftedProduct()));

		if (ModList.THAUMCRAFT.isLoaded()) {
			this.addRecipe(new AspectFormerRecipe(ChromaTiles.ASPECT.getCraftedProduct(), ChromaStacks.transformCore));
			this.addRecipe(new AspectJarRecipe(ChromaTiles.ASPECTJAR.getCraftedProduct(), ChromaStacks.voidCore));

			is = ChromaTiles.ESSENTIARELAY.getCraftedProduct();
			sr = ReikaRecipeHelper.getShapedRecipeFor(is, "DSD", "SOS", "DSD", 'D', ChromaStacks.energyPowder, 'S', ChromaStacks.auraDust, 'O', ChromaStacks.blackShard);
			this.addRecipe(new EssentiaRelayRecipe(is, sr));

			is = ChromaItems.WARP.getStackOf();
			this.addRecipe(new AuraCleanerRecipe(is, new ItemStack(Items.potionitem)));
		}

		this.addRecipe(new InventoryLinkRecipe(ChromaItems.LINK.getStackOf(), new ItemStack(Items.ender_pearl)));

		this.addRecipe(new LampRecipe(ChromaTiles.LAMP.getCraftedProduct(), new ItemStack(Blocks.glowstone)));

		this.addRecipe(new BeaconRecipe(ChromaTiles.BEACON.getCraftedProduct(), new ItemStack(Items.nether_star)));

		is = ChromaItems.FINDER.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "SIS", "IAI", "SIS", 'A', ChromaStacks.auraDust, 'I', Items.iron_ingot, 'S', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new PylonFinderRecipe(is, sr));

		this.addRecipe(new BatteryRecipe(ChromaTiles.POWERTREE.getCraftedProduct(), ChromaStacks.crystalStar));

		this.addRecipe(new InvTickerRecipe(ChromaTiles.TICKER.getCraftedProduct(), new ItemStack(Blocks.chest)));

		this.addRecipe(new MinerRecipe(ChromaTiles.MINER.getCraftedProduct(), ChromaStacks.energyCoreHigh));

		this.addRecipe(new FabricatorRecipe(ChromaTiles.FABRICATOR.getCraftedProduct(), ChromaStacks.transformCoreHigh));

		is = ChromaTiles.LAMPCONTROL.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "DED", "RQR", "ScS", 'c', ChromaStacks.chromaDust, 'D', ChromaStacks.auraDust, 'E', Items.ender_pearl, 'R', Items.redstone, 'Q', Items.quartz, 'S', ReikaItemHelper.stoneSlab);
		this.addRecipe(new LampControlRecipe(is, sr));
		/*
		this.addRecipe(new UpgradeRecipe(ChromaStacks.silkUpgrade, new ItemStack(Items.diamond)));
		this.addRecipe(new UpgradeRecipe(ChromaStacks.speedUpgrade, new ItemStack(Items.redstone)));
		this.addRecipe(new UpgradeRecipe(ChromaStacks.efficiencyUpgrade, new ItemStack(Items.emerald)));
		 */

		if (ChromaOptions.ENDERTNT.getState())
			this.addRecipe(new RecipeEnderTNT(ChromaBlocks.TNT.getStackOf(), new ItemStack(Items.nether_star)));

		this.addRecipe(new TeleportWandRecipe(ChromaItems.TELEPORT.getStackOf(), ChromaStacks.energyCore));

		this.addRecipe(new BuilderWandRecipe(ChromaItems.BUILDER.getStackOf(), ChromaStacks.transformCore));

		this.addRecipe(new CaptureWandRecipe(ChromaItems.CAPTURE.getStackOf(), new ItemStack(Items.string)));

		this.addRecipe(new GrowthWandRecipe(ChromaItems.GROWTH.getStackOf(), new ItemStack(Items.diamond)));

		this.addRecipe(new DuplicationWandRecipe(ChromaItems.DUPLICATOR.getStackOf(), ChromaStacks.voidCore));

		if (ModList.APPENG.isLoaded()) {
			this.addRecipe(new VoidStorageRecipe(ChromaItems.VOIDCELL.getStackOf(), ChromaStacks.voidCore));
			this.addRecipe(new CrystalCellRecipe(ChromaItems.CRYSTALCELL.getStackOf(), ChromaStacks.crystalFocus));
		}

		for (int i = 0; i < BlockPath.PathType.list.length; i++) {
			PathType p = BlockPath.PathType.list[i];
			this.addRecipe(new PathRecipe(ChromaItems.ELEMENTAL.getStackOf(CrystalElement.LIME), i, p.addition));
		}

		if (ChromaOptions.BIOMEPAINTER.getState())
			this.addRecipe(new BiomePainterRecipe(ChromaTiles.BIOMEPAINTER.getCraftedProduct(), ChromaStacks.transformCoreHigh));

		this.addRecipe(new AuraPouchRecipe(ChromaItems.AURAPOUCH.getStackOf(), ChromaStacks.voidCore));

		this.addRecipe(new FarmerRecipe(ChromaTiles.FARMER.getCraftedProduct(), ChromaStacks.energyCore));

		this.addRecipe(new RelaySourceRecipe(ChromaTiles.RELAYSOURCE.getCraftedProduct(), ChromaStacks.crystalFocus));

		this.addRecipe(new ItemCollectorRecipe(ChromaTiles.ITEMCOLLECTOR.getCraftedProduct(), ChromaStacks.voidCore));

		this.addRecipe(new PortalRecipe(ChromaBlocks.PORTAL.getStackOf(), ChromaStacks.voidCoreHigh, repeater));

		Object[] metal = new Object[]{Items.iron_ingot, 1, Items.gold_ingot, 4, "ingotCopper", 1, "ingotSilver", 2, ChromaStacks.conductiveIngot, 6, ChromaStacks.fieryIngot, 8};
		for (int i = 0; i < metal.length; i += 2) {
			if (metal[i] instanceof String && !ReikaItemHelper.oreItemExists((String)metal[i]))
				continue;
			is = ReikaItemHelper.getSizedItemStack(ChromaBlocks.HEATLAMP.getStackOf(), (int)metal[i+1]);
			sr = new ShapedOreRecipe(is, "fff", "faf", "fff", 'f', ChromaStacks.firaxite, 'a', metal[i]);
			this.addRecipe(new HeatLampRecipe(is, sr, hr));

			is = ReikaItemHelper.getSizedItemStack(ChromaBlocks.REDSTONEPOD.getStackOf(), (int)metal[i+1]);
			sr = new ShapedOreRecipe(is, "fbf", "dad", "fbf", 'f', Items.redstone, 'a', metal[i], 'd', ChromaStacks.auraDust, 'b', ChromaStacks.beaconDust);
			this.addRecipe(new RedstonePodRecipe(is, sr));
		}

		this.addRecipe(new EnderCrystalRecipe(ChromaItems.ENDERCRYSTAL.getStackOf(), ChromaStacks.crystalStar));

		is = ChromaItems.BULKMOVER.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "OCO", "ODO", " I ", 'O', Blocks.obsidian, 'D', Items.diamond, 'C', Blocks.chest, 'I', Items.iron_ingot);
		this.addRecipe(new RecipeItemMover(is, sr));

		this.addRecipe(new AutomatorRecipe(ChromaTiles.AUTOMATOR.getCraftedProduct(), ChromaStacks.transformCoreHigh));

		this.addRecipe(new ChainGunRecipe(ChromaItems.CHAINGUN.getStackOf(), ChromaStacks.crystalCore));
		this.addRecipe(new SplashGunRecipe(ChromaItems.SPLASHGUN.getStackOf(), ChromaStacks.crystalCore));
		this.addRecipe(new VacuumGunRecipe(ChromaItems.VACUUMGUN.getStackOf(), ChromaStacks.voidCoreHigh));

		if (ModList.APPENG.isLoaded()) {
			this.addRecipe(new MEDistributorRecipe(ChromaTiles.MEDISTRIBUTOR.getCraftedProduct(), ChromaStacks.transformCore));
		}

		this.addRecipe(new RecipeHoverWand(ChromaItems.HOVERWAND.getStackOf(), ChromaStacks.voidCore));

		if (PowerTypes.RF.isLoaded()) {
			is = ChromaTiles.RFDISTRIBUTOR.getCraftedProduct();
			sr = ReikaRecipeHelper.getShapedRecipeFor(is, "rir", "iGi", "rir", 'i', Items.iron_ingot, 'r', Blocks.redstone_block, 'G', Blocks.glowstone);
			this.addRecipe(new RFDistributorRecipe(is, sr));
		}

		is = ChromaTiles.FLUIDDISTRIBUTOR.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "rir", "iGi", "rir", 'i', Items.iron_ingot, 'r', Items.water_bucket, 'G', Blocks.glowstone);
		this.addRecipe(new FluidDistributorRecipe(is, sr));

		this.addRecipe(new TransportWindowRecipe(ChromaTiles.WINDOW.getCraftedProduct(), new ItemStack(Items.diamond)));

		is = ChromaItems.LINKTOOL.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "  l", " s ", "g  ", 'g', ChromaStacks.grayShard, 'l', ChromaStacks.limeShard, 's', Items.stick);
		this.addRecipe(new LinkToolRecipe(is, sr));

		this.addRecipe(new RecipePersonalCharger(ChromaTiles.PERSONAL.getCraftedProduct(), ChromaStacks.crystalCore));

		is = ChromaTiles.MUSIC.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "sns", "non", "sns", 's', ChromaItems.SHARD.getAnyMetaStack(), 'n', Blocks.noteblock, 'o', Items.clock);
		this.addRecipe(new MusicRecipe(is, sr));

		this.addRecipe(new PylonTurboRecipe(ChromaTiles.PYLONTURBO.getCraftedProduct(), ChromaStacks.lumenCore, repeater));

		is = ChromaTiles.TURRET.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, " g ", "gsg", " b ", 's', ChromaItems.SHARD.getStackOf(CrystalElement.PINK), 'g', Items.glowstone_dust, 'b', new ItemStack(block));
		this.addRecipe(new LumenTurretRecipe(is, sr));

		is = ChromaBlocks.DOOR.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "grg", "rer", "lpl", 'g', Items.glowstone_dust, 'r', Items.redstone, 'l', ReikaItemHelper.lapisDye, 'e', Items.ender_pearl, 'p', Blocks.piston);
		this.addRecipe(new DoorRecipe(is, sr));

		is = ChromaItems.KEY.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "e i", " il", "ir ", 'i', Items.iron_ingot, 'r', Items.redstone, 'l', ReikaItemHelper.lapisDye, 'e', Items.ender_pearl);
		this.addRecipe(new DoorKeyRecipe(is, sr));

		is = ChromaItems.SHARE.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "lil", "gig", "rir", 'i', Items.iron_ingot, 'r', Items.redstone, 'l', ReikaItemHelper.lapisDye, 'g', Items.glowstone_dust);
		this.addRecipe(new OwnerKeyRecipe(is, sr));

		this.addRecipe(new CrystalFenceRecipe(ChromaTiles.FENCE.getCraftedProduct(), new ItemStack(Blocks.gold_block)));

		is = ChromaBlocks.FENCE.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "crc", "cgc", "cic", 'i', Items.iron_ingot, 'r', Items.redstone, 'c', Blocks.cobblestone, 'g', Items.glowstone_dust);
		this.addRecipe(new FenceAuxRecipe(is, sr));

		this.addRecipe(new LumenBroadcastRecipe(ChromaStacks.crystalStar));

		this.addRecipe(new CloakTowerRecipe(ChromaTiles.CLOAKING.getCraftedProduct(), ChromaStacks.crystalFocus));

		is = ChromaTiles.LIGHTER.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "sas", "ala", "sbs", 's', ChromaStacks.blueShard, 'b', ChromaStacks.chromaDust, 'a', ChromaStacks.auraDust, 'l', ChromaBlocks.LAMPBLOCK.getStackOfMetadata(CrystalElement.WHITE.ordinal()));
		this.addRecipe(new CaveLighterRecipe(is, sr));

		this.addRecipe(new EnchantmentRecipe(ChromaResearch.EXCAVATOR, ChromaItems.EXCAVATOR.getStackOf(), ChromaStacks.auraDust, ChromaStacks.chargedPurpleShard, ChromaStacks.chromaDust, Enchantment.silkTouch, 1));
		this.addRecipe(new EnchantmentRecipe(ChromaResearch.EXCAVATOR, ChromaItems.EXCAVATOR.getStackOf(), ReikaItemHelper.lapisDye.copy(), ChromaStacks.chargedPurpleShard, ChromaStacks.focusDust, Enchantment.fortune, 5));
		this.addRecipe(new EnchantmentRecipe(ChromaResearch.EXCAVATOR, ChromaItems.EXCAVATOR.getStackOf(), ChromaStacks.enderDust, ChromaStacks.chargedLimeShard, ChromaStacks.beaconDust, Enchantment.field_151369_A, 1));
		this.addRecipe(new EnchantmentRecipe(ChromaResearch.EXCAVATOR, ChromaItems.EXCAVATOR.getStackOf(), ChromaStacks.energyPowder, ChromaStacks.chargedBrownShard, new ItemStack(Items.diamond), Enchantment.efficiency, 1));
		this.addRecipe(new EnchantmentRecipe(ChromaResearch.EXCAVATOR, ChromaItems.EXCAVATOR.getStackOf(), ChromaStacks.teleDust, ChromaStacks.chargedBlueShard, ChromaStacks.lumenGem, Enchantment.flame, 1));

		this.addRecipe(new EnchantmentRecipe(ChromaResearch.TRANSITION, ChromaItems.TRANSITION.getStackOf(), ChromaStacks.bindingCrystal, ChromaStacks.chargedGrayShard, ChromaStacks.resonanceDust, Enchantment.silkTouch, 1));

		this.addRecipe(new EnchantmentRecipe(ChromaResearch.GROWTH, ChromaItems.GROWTH.getStackOf(), ReikaItemHelper.bonemeal.copy(), ChromaStacks.chargedGreenShard, ChromaStacks.elementDust, Enchantment.power, 1));

		is = new ItemStack(ChromaBlocks.MUSICTRIGGER.getBlockInstance(), 4, 0);
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "scs", "sas", "iri", 's', ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.STONE.ordinal()), 'c', ChromaStacks.chromaDust, 'a', ChromaStacks.auraDust, 'r', Items.redstone, 'i', Items.gold_ingot);
		this.addRecipe(new MusicTriggerRecipe(is, sr));

		//this.addRecipe(new GlowFireRecipe(ChromaTiles.GLOWFIRE.getCraftedProduct(), ChromaStacks.transformCore));

		is = ChromaTiles.INSERTER.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "dsd", "cec", "rcr", 's', ChromaStacks.limeShard, 'd', ChromaStacks.teleDust, 'c', "cobblestone", 'r', Items.redstone, 'e', Items.ender_pearl);
		this.addRecipe(new ItemInserterRecipe(is, sr));

		is = ChromaTiles.WEAKREPEATER.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "wgw", "scs", "wrw", 'r', Items.glowstone_dust, 'g', ChromaStacks.beaconDust, 'w', "plankWood", 's', "stickWood", 'c', ChromaItems.BUCKET.getStackOfMetadata(0));
		this.addRecipe(new WeakRepeaterRecipe(is, sr));

		is = ChromaTiles.ENCHANTDECOMP.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "sgs", "GbG", "ccc", 'G', Blocks.glass, 'b', Items.bucket, 'g', Blocks.glowstone, 's', ChromaItems.SHARD.getAnyMetaStack(), 'c', "stone");
		this.addRecipe(new EnchantDecompRecipe(is, sr));

		is = ChromaTiles.LUMENWIRE.getCraftedProduct();
		sr = new ShapedOreRecipe(is, " s ", "OGO", "ccc", 'O', Blocks.obsidian, 'G', Items.glowstone_dust, 's', ChromaItems.SHARD.getStackOf(CrystalElement.BLUE), 'c', "cobblestone");
		this.addRecipe(new LumenWireRecipe(is, sr));

		this.addRecipe(new PurifyCrystalRecipe(ChromaItems.PURIFY.getStackOf(), ChromaStacks.iridChunk));
		this.addRecipe(new EfficiencyCrystalRecipe(ChromaItems.EFFICIENCY.getStackOf(), ChromaBlocks.SUPER.getStackOfMetadata(CrystalElement.BLACK.ordinal())));

		is = ChromaTiles.PARTICLES.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "sg ", "gRg", " gs", 'R', Blocks.redstone_block, 'g', Items.glowstone_dust, 's', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new ParticleSpawnerRecipe(is, sr));

		this.addRecipe(new KillAuraRecipe(ChromaItems.KILLAURAGUN.getStackOf(), ChromaStacks.energyCoreHigh));

		for (int i = 0; i < 3; i++)
			this.addRecipe(new MeteorTowerRecipe(i));

		this.addRecipe(new RecipeAreaBreaker(ChromaTiles.AREABREAKER.getCraftedProduct(), ChromaStacks.crystalFocus));

		this.addRecipe(new WirelessTransmitterRecipe(ChromaTiles.WIRELESS.getCraftedProduct(), ChromaStacks.elementUnit));

		this.addRecipe(new WarpGateRecipe(ChromaTiles.TELEPORT.getCraftedProduct(), ChromaStacks.voidCoreHigh));

		this.addRecipe(new FloatstoneBootsRecipe(ChromaItems.FLOATBOOTS.getStackOf(), new ItemStack(Items.iron_boots), false));
		if (ModList.ROTARYCRAFT.isLoaded()) {
			this.addRecipe(new FloatstoneBootsRecipe(ChromaItems.FLOATBOOTS.getStackOf(), ItemRegistry.BEDBOOTS.getStackOf(), true));
			this.addRecipe(new FloatstoneBootsRecipe(ChromaItems.FLOATBOOTS.getStackOf(), ItemRegistry.BEDJUMP.getStackOfMetadata(OreDictionary.WILDCARD_VALUE), true));
		}

		is = ChromaTiles.FLUIDRELAY.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, " D ", "LAL", "IGI", 'D', ChromaStacks.beaconDust, 'A', ChromaStacks.auraDust, 'L', ReikaItemHelper.lapisDye, 'I', Items.iron_ingot, 'G', Blocks.glass);
		this.addRecipe(new FluidRelayRecipe(is, sr));

		is = ChromaItems.WARPCAPSULE.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, " ig", "aea", "li", 'e', ChromaItems.ELEMENTAL.getStackOf(CrystalElement.LIME), 'g', ChromaStacks.limeShard, 'l', ChromaStacks.lightBlueShard, 'a', ChromaStacks.auraDust, 'i', Items.iron_ingot);
		this.addRecipe(new WarpCapsuleRecipe(is, sr));

		if (ModList.MYSTCRAFT.isLoaded()) {
			is = ChromaTiles.BOOKDECOMP.getCraftedProduct();
			sr = ReikaRecipeHelper.getShapedRecipeFor(is, " g ", "dcd", "SSS", 'g', Items.glowstone_dust, 'd', Items.diamond, 'S', ReikaItemHelper.stoneSlab, 'c', ChromaStacks.enderDust);
			this.addRecipe(new PageExtractorRecipe(is, sr));
		}

		is = ChromaTiles.HARVESTPLANT.getCraftedProduct();
		Object root = ModList.BOTANIA.isLoaded() ? ReikaItemHelper.lookupItem(ModList.BOTANIA, "manaResource", 6) : null;
		if (root == null)
			root = Items.redstone;
		ItemStack in = ModList.BOTANIA.isLoaded() ? ReikaItemHelper.lookupBlock(ModList.BOTANIA, "specialFlower", 0) : null;
		if (in != null) {
			in.stackTagCompound = new NBTTagCompound();
			in.stackTagCompound.setString("type", "munchdew");
		}
		if (in == null)
			in = ChromaStacks.auraDust;
		sr = new ShapedOreRecipe(is, "FAF", "fEf", "LRL", 'E', in, 'f', "flower", 'L', "treeLeaves", 'R', root, 'F', ChromaStacks.livingEssence, 'A', ChromaStacks.auraDust);
		this.addRecipe(new HarvestPlantRecipe(is, sr));

		this.addRecipe(new GlowFireRecipe(ChromaTiles.GLOWFIRE.getCraftedProduct(), ChromaStacks.transformCore));

		this.addRecipe(new AvoLampRecipe(ChromaBlocks.AVOLAMP.getStackOf(), ChromaStacks.avolite));

		this.addRecipe(new AvoLaserRecipe(ChromaTiles.AVOLASER.getCraftedProduct(), ChromaStacks.avolite));

		this.addRecipe(new RelayFilterRecipe(ChromaBlocks.RELAYFILTER.getStackOf(), ChromaItems.LENS.getAnyMetaStack()));

		this.addRecipe(new RouterNodeRecipe(ChromaBlocks.ROUTERNODE.getStackOfMetadata(0), false));
		this.addRecipe(new RouterNodeRecipe(ChromaBlocks.ROUTERNODE.getStackOfMetadata(1), true));

		this.addRecipe(new RouterHubRecipe(ChromaTiles.ROUTERHUB.getCraftedProduct(), ChromaStacks.elementUnit));

		this.addRecipe(new FocusCrystalRecipes.FlawedFocusCrystalRecipe());
		DefaultFocusCrystalRecipe fr = new FocusCrystalRecipes.DefaultFocusCrystalRecipe();
		this.addRecipe(fr);
		this.addRecipe(new FocusCrystalRecipes.RefinedFocusCrystalRecipe(fr));
		this.addRecipe(new FocusCrystalRecipes.ExquisiteFocusCrystalRecipe(fr));

		is = ChromaItems.STRUCTUREFINDER.getStackOf();
		sr = new ShapedOreRecipe(is, "IAs", "ASA", "sAO", 'A', ChromaStacks.auraDust, 'I', Items.iron_ingot, 'O', Blocks.obsidian, 'S', "stone", 's', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new StructureFinderRecipe(is, sr));

		is = ChromaItems.MOBSONAR.getStackOf();
		sr = new ShapedOreRecipe(is, "sas", "aba", "sas", 'a', ChromaStacks.auraDust, 's', Items.stick, 'b', Items.diamond);
		this.addRecipe(new MobSonarRecipe(is, sr));

		if (ModList.THAUMCRAFT.isLoaded()) {
			is = ChromaTiles.FLUXMAKER.getCraftedProduct();
			sr = new ShapedOreRecipe(is, " g ", "asa", " g ", 'g', ChromaStacks.voidDust, 'a', ChromaStacks.auraDust, 's', ThaumItemHelper.ItemEntry.VOIDSEED.getItem());
			this.addRecipe(new FluxMakerRecipe(is, sr));
		}

		is = ChromaTiles.FUNCTIONRELAY.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, " D ", "ACA", " D ", 'D', ChromaStacks.beaconDust, 'A', ChromaStacks.auraDust, 'C', Blocks.glowstone);
		this.addRecipe(new FunctionRelayRecipe(is, sr));

		this.addRecipe(new ChromaCrafterRecipe(ChromaTiles.CHROMACRAFTER.getCraftedProduct(), ChromaStacks.transformCoreHigh));

		for (int i = 0; i < BlockRepeaterLight.MODELS.length; i++) {
			this.addRecipe(new RepeaterLampRecipe(BlockRepeaterLight.MODELS[i]));
		}

		is = ChromaItems.CAVEPATHER.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "RAC", "BCA", "SBE", 'R', Items.redstone, 'E', Items.emerald, 'B', ChromaStacks.beaconDust, 'A', ChromaStacks.auraDust, 'S', Items.stick, 'C', Items.quartz);
		this.addRecipe(new CavePatherRecipe(is, sr));

		is = ChromaItems.SPLINEATTACK.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, " sa", "sis", "bs ", 'i', Items.iron_ingot, 's', ChromaStacks.whiteShard, 'a', ChromaStacks.blueShard, 'b', ChromaStacks.pinkShard);
		this.addRecipe(new SplineAttackRecipe(is, sr));

		is = ChromaBlocks.FAKESKY.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "tGt", "gGg", "tGt", 'g', Blocks.glowstone, 'G', Blocks.glass, 't', ChromaStacks.beaconDust, 'B', ChromaStacks.beaconDust, 'A', ChromaStacks.auraDust, 'S', Items.stick, 'C', Items.quartz);
		this.addRecipe(new FakeSkyRecipe(is, sr));

		this.addRecipe(new MultiBuilderRecipe(ChromaTiles.MULTIBUILDER.getCraftedProduct(), ChromaStacks.elementUnit));

		this.addSpecialRecipes();
	}

	private void addSpecialRecipes() {
		this.addRecipe(new RepeaterTurboRecipe(ChromaTiles.REPEATER, 5000));
		this.addRecipe(new RepeaterTurboRecipe(ChromaTiles.COMPOUND, 12500));
		this.addRecipe(new RepeaterTurboRecipe(ChromaTiles.BROADCAST, 30000));
	}

	public void addPostLoadRecipes() {
		if (ModList.THAUMCRAFT.isLoaded()) {
			ItemStack is = ReikaItemHelper.getSizedItemStack(ThaumItemHelper.BlockEntry.ANCIENTROCK.getItem(), 16);
			IRecipe sr = new ShapedOreRecipe(is, "SdS", "dOd", "SdS", 'S', "stone", 'd', ChromaStacks.auraDust, 'O', Blocks.obsidian);
			this.addRecipe(new CastingRecipe(is, sr));
		}

		if (ModList.FORESTRY.isLoaded()) {
			ItemStack is = new ItemStack(CrystalBackpack.instance.getItem1());
			IRecipe sr = ReikaRecipeHelper.getShapedRecipeFor(is, "SWS", "cCc", "SWS", 'S', Items.string, 'C', Blocks.chest, 'W', Blocks.wool, 'c', ChromaItems.SHARD.getAnyMetaStack());
			this.addRecipe(new CastingRecipe(is, sr));

			ItemStack is2 = new ItemStack(CrystalBackpack.instance.getItem2());
			sr = ReikaRecipeHelper.getShapedRecipeFor(is2, "WDW", "WCW", "WWW", 'D', Items.diamond, 'C', is, 'W', ForestryHandler.CraftingMaterials.WOVENSILK.getItem());
			this.addRecipe(new CastingRecipe(is2, sr));

			ItemStack frame = ChromaItems.BEEFRAME.getStackOf();
			sr = ReikaRecipeHelper.getShapedRecipeFor(frame, "agb", "gfg", "bga", 'a', ChromaStacks.auraDust, 'b', ChromaStacks.chromaDust, 'g', ChromaStacks.lightBlueShard, 'f', ForestryHandler.ItemEntry.IMPREGFRAME.getItem());
			this.addRecipe(new BeeFrameRecipe(frame, sr));

			Block ctr = GameRegistry.findBlock("MagicBees", "magicApiary");
			if (ctr == null)
				ctr = GameRegistry.findBlock(ModList.FORESTRY.modLabel, "alveary");
			this.addRecipe(new LumenAlvearyRecipe(ChromaTiles.ALVEARY.getCraftedProduct(), new ItemStack(ctr)));
		}

		if (ModList.TINKERER.isLoaded()) {
			for (int i = 0; i < ToolParts.partList.length; i++) {
				this.addRecipe(new TinkerToolPartRecipe(ToolParts.partList[i]));
			}
			for (int i = 0; i < WeaponParts.partList.length; i++) {
				this.addRecipe(new TinkerToolPartRecipe(WeaponParts.partList[i]));
			}
		}

		for (Object o : Item.itemRegistry.getKeys()) {
			String s = (String)o;
			Item i = (Item)Item.itemRegistry.getObject(s);
			if (i instanceof ItemArmor) {
				if (((ItemArmor)i).armorType == 3) {
					this.addRecipe(new DoubleJumpRecipe(i));
				}
			}
		}
	}

	private void addRecipe(CastingRecipe r) {
		OneWayList<CastingRecipe> li = recipes.get(r.type);
		if (li == null) {
			li = new OneWayList();
			recipes.put(r.type, li);
		}
		li.add(r);

		if (!r.getClass().getName().contains("CastingRecipes.Special") && !ReikaItemHelper.getRegistrantMod(r.getOutput()).equals(ModList.CHROMATICRAFT.modLabel))
			moddedItemRecipes.add(r);

		recipeIDs.put(maxID, r);
		maxID++;
		//ChromaResearchManager.instance.register(r);

		if (r instanceof PylonCastingRecipe) {
			ElementTagCompound tag = ((PylonCastingRecipe)r).getRequiredAura();
			maxEnergyCost = Math.max(maxEnergyCost, tag.getMaximumValue());
			maxTotalEnergyCost = Math.max(maxTotalEnergyCost, tag.getTotalEnergy());
		}
	}

	public void addModdedRecipe(CastingRecipe r) {
		if (this.verifyOutputItem(r.getOutput(), false))
			this.addCustomRecipe(r);
	}

	private void addCustomRecipe(CastingRecipe r) {
		this.addRecipe(r);
		APIrecipes.add(r);
	}

	public List<CastingRecipe> getAllAPIRecipes() {
		return Collections.unmodifiableList(APIrecipes);
	}

	public List<CastingRecipe> getAllModdedItemRecipes() {
		return Collections.unmodifiableList(moddedItemRecipes);
	}

	public CastingRecipe getRecipe(TileEntityCastingTable table, ArrayList<RecipeType> type) {
		ItemStack main = table.getStackInSlot(0);
		ArrayList<CastingRecipe> li = new ArrayList();
		for (int i = 0; i < type.size(); i++) {
			ArrayList<CastingRecipe> list = recipes.get(type.get(i));
			if (list != null)
				li.addAll(list);
		}
		for (CastingRecipe r : li) {
			if (r.match(table))
				return r;
		}
		return null;
	}

	public ArrayList<CastingRecipe> getAllRecipesMaking(ItemStack result) {
		ArrayList<CastingRecipe> li = new ArrayList();
		for (RecipeType type : recipes.keySet()) {
			ArrayList<CastingRecipe> ir = recipes.get(type);
			if (ir != null) {
				for (CastingRecipe r : ir) {
					if (!(r instanceof EnchantmentRecipe)) {
						if (ReikaItemHelper.matchStacks(result, r.getOutput()) && (result.stackTagCompound == null || ItemStack.areItemStackTagsEqual(result, r.getOutput())))
							li.add(r);
					}
				}
			}
		}
		return li;
	}

	public ArrayList<CastingRecipe> getAllRecipesUsing(ItemStack ingredient) {
		ArrayList<CastingRecipe> li = new ArrayList();
		for (RecipeType type : recipes.keySet()) {
			ArrayList<CastingRecipe> ir = recipes.get(type);
			if (ir != null) {
				for (CastingRecipe r : ir) {
					if (r.usesItem(ingredient))
						li.add(r);
				}
			}
		}
		return li;
	}

	public Collection<EnchantmentRecipe> getAllEnchantingRecipes() {
		ArrayList<EnchantmentRecipe> li = new ArrayList();
		for (RecipeType type : recipes.keySet()) {
			ArrayList<CastingRecipe> ir = recipes.get(type);
			if (ir != null) {
				for (CastingRecipe r : ir) {
					if (r instanceof EnchantmentRecipe)
						li.add((EnchantmentRecipe)r);
				}
			}
		}
		return li;
	}

	public static boolean playerHasCrafted(EntityPlayer ep, RecipeType type) {
		NBTTagCompound nbt = ChromaResearchManager.instance.getRootNBTTag(ep);
		NBTTagCompound cast = nbt.getCompoundTag("castingprog");
		return cast.getBoolean(type.name().toLowerCase(Locale.ENGLISH));
	}

	public static void setPlayerHasCrafted(EntityPlayer ep, RecipeType type) {
		NBTTagCompound nbt = ChromaResearchManager.instance.getRootNBTTag(ep);
		NBTTagCompound cast = nbt.getCompoundTag("castingprog");
		cast.setBoolean(type.name().toLowerCase(Locale.ENGLISH), true);
		nbt.setTag("castingprog", cast);
		ChromaResearchManager.instance.checkForUpgrade(ep);
	}

	public CastingRecipe getRecipeByID(int id) {
		return recipeIDs.get(id);
	}

	public int getIDForRecipe(CastingRecipe cr) {
		return recipeIDs.inverse().get(cr);
	}

	public Collection<CastingRecipe> getAllRecipes() {
		ArrayList<CastingRecipe> li = new ArrayList();
		for (OneWayList oli : recipes.values()) {
			li.addAll(oli);
		}
		return li;
	}

	public void reload() {
		ChromatiCraft.logger.log("Reloading casting recipes.");
		recipes.clear();
		moddedItemRecipes.clear();
		recipeIDs.clear();
		maxID = 0;
		maxEnergyCost = 0;
		maxTotalEnergyCost = 0;

		this.loadRecipes();
		this.addPostLoadRecipes();

		for (CastingRecipe c : APIrecipes) {
			this.addRecipe(c);
		}

		//this.loadCustomRecipeFiles();

		ChromaResearch.loadPostCache();
		MinecraftForge.EVENT_BUS.post(new CastingRecipesReloadEvent());

		ChromatiCraft.logger.log("Finished reloading casting recipes.");
	}

	public int getMaxRecipeEnergyCost() {
		return maxEnergyCost;
	}

	public int getMaxRecipeTotalEnergyCost() {
		return maxTotalEnergyCost;
	}

	public final void loadCustomRecipeFiles() {
		CustomRecipeList crl = new CustomRecipeList(ChromatiCraft.instance, "castingtable");
		crl.addFieldLookup("chromaticraft_stack", ChromaStacks.class);
		crl.load();
		for (LuaBlock lb : crl.getEntries()) {
			Exception e = null;
			boolean flag = false;
			try {
				flag = this.addCustomRecipe(lb, crl);
			}
			catch (Exception ex) {
				e = ex;
				flag = false;
			}
			if (flag) {
				ChromatiCraft.logger.log("Loaded custom casting recipe '"+lb.getString("type")+"'");
			}
			else {
				ChromatiCraft.logger.logError("Could not load custom casting recipe '"+lb.getString("type")+"'");
				if (e != null)
					e.printStackTrace();
			}
		}
	}

	protected final boolean verifyOutputItem(ItemStack is, boolean throwExc) {
		if (is.getItem() instanceof ItemChromaBasic || is.getItem().getClass().getName().startsWith("Reika.ChromatiCraft")) {
			String s = "Invalid Recipe Output: This item is not allowed as an output, as it is a native ChromatiCraft item with its own recipe.";
			if (throwExc)
				throw new IllegalArgumentException(s);
			else
				ChromatiCraft.logger.logError(s);
			return false;
		}
		else {
			return true;
		}
	}

	private boolean addCustomRecipe(LuaBlock lb, CustomRecipeList crl) throws Exception {
		ItemStack out = crl.parseItemString(lb.getString("output"), lb.getChild("output_nbt"), false);
		this.verifyOutputItem(out, true);
		String lvl = lb.getString("level");
		int duration = lb.containsKey("duration") ? lb.getInt("duration") : -1;
		int typical = lb.containsKey("typical_crafted_amount") ? lb.getInt("typical_crafted_amount") : 1;
		ItemStack leftover = lb.containsKey("central_leftover") ? crl.parseItemString(lb.getString("central_leftover"), lb.getChild("central_leftover_nbt"), true) : null;
		float autocost = lb.containsKey("automation_cost_factor") ? (float)lb.getDouble("automation_cost_factor") : 1;
		float xp = lb.containsKey("experience_factor") ? MathHelper.clamp_float((float)lb.getDouble("experience_factor"), 0, 1) : 1;
		boolean stack = true;
		boolean setStack = false;
		if (lb.containsKey("can_be_stacked")) {
			stack = lb.getBoolean("can_be_stacked");
			setStack = true;
		}
		Collection<ProgressStage> progress = new HashSet();
		if (lb.hasChild("progress")) {
			for (String s : lb.getChild("progress").getDataValues()) {
				progress.add(ProgressStage.valueOf(s.toUpperCase(Locale.ENGLISH)));
			}
		}

		HashMap<Coordinate, CrystalElement> runes = new HashMap();
		if (lb.hasChild("runes")) {
			for (LuaBlock entry : lb.getChild("runes").getChildren()) {
				Coordinate c = new Coordinate(entry.getInt("x"), entry.getInt("y"), entry.getInt("z"));
				CrystalElement e = CrystalElement.valueOf(entry.getString("color").toUpperCase(Locale.ENGLISH));
				runes.put(c, e);
			}
		}

		HashMap<CrystalElement, Integer> energy = new HashMap();
		if (lb.hasChild("energy")) {
			for (LuaBlock entry : lb.getChild("energy").getChildren()) {
				CrystalElement e = CrystalElement.valueOf(entry.getString("color").toUpperCase(Locale.ENGLISH));
				int amt = entry.getInt("amount");
				energy.put(e, amt);
			}
		}

		if (lvl.equals("basic") || lvl.equals("rune")) {
			IRecipe ir = crl.parseCraftingRecipe(lb.getChild("recipe"), out);
			if (lvl.equals("rune")) {
				this.addCustomRecipe(new ConfigRecipe.Rune(ir, duration, typical, leftover, autocost, xp, runes, progress));
				return true;
			}
			else {
				this.addCustomRecipe(new ConfigRecipe.Basic(ir, duration, typical, leftover, autocost, xp, progress));
				return true;
			}
		}
		else if (lvl.equals("multi") || lvl.equals("pylon")) {
			LuaBlock recipe = lb.getChild("recipe");
			ItemStack center = crl.parseItemString(recipe.getString("center"), null, false);
			HashMap<Coordinate, Object> items = new HashMap();
			for (LuaBlock entry : recipe.getChild("items").getChildren()) {
				Coordinate c = new Coordinate(entry.getInt("x"), 0, entry.getInt("z"));
				Object item = crl.parseObjectString(entry.getString("item"));
				items.put(c, item);
			}

			if (lvl.equals("pylon")) {
				if (!setStack)
					stack = false;
				this.addCustomRecipe(new ConfigRecipe.Pylon(out, center, duration, typical, leftover, autocost, xp, runes, items, energy, progress));
				return true;
			}
			else {
				this.addCustomRecipe(new ConfigRecipe.Multi(out, center, duration, typical, leftover, autocost, xp, runes, items, progress));
				return true;
			}
		}
		else {
			throw new IllegalArgumentException("Invalid recipe tier '"+lvl+"'!");
		}
	}

}
