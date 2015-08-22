/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.ChromaFlowerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CompoundRelayRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CompoundRuneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CrystalAltarRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CrystalGlowRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CrystalLampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.CrystalStoneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.HeatLampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.LumenLampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.PathRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.PortalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.PotionCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RecipeEnderTNT;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RecipeTankBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RelayRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks.RuneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalClusterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalFocusRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalGroupRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalLensRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalMirrorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalSeedRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.CrystalStarRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.ElementUnitRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.EnergyCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.IridescentChunkRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.RawCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.TransformationCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.UpgradeRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.VoidCoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items.VoidStorageRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.RepeaterTurboRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.AcceleratorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.AspectFormerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.AspectJarRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.AutomatorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.BatteryRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.BeaconRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.BiomePainterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.ChromaCollectorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CompoundRepeaterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalBrewerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalChargerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalFurnaceRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalLaserRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.CrystalTankRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.EnchanterRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FabricatorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FarmerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.GuardianStoneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.HeatLilyRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.InfuserRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.InvTickerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.IridescentCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.ItemCollectorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.LampControlRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.LampRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.MEDistributorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.MinerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.MusicRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RFDistributorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RecipeCrystalRepeater;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RecipePersonalCharger;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RelaySourceRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RiftRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RitualTableRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.SpawnerReprogrammerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.StandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.TelePumpRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.TransportWindowRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.AuraCleanerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.AuraPouchRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.BreakerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.BuilderWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.CaptureWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.ChainGunRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.DuplicationWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.EnderCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.EnhancedPendantRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.GrowthWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.InventoryLinkRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.LinkToolRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.MultiToolRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.OrePickRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.OreSilkerRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.PendantRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.PylonFinderRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.RecipeHoverWand;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.RecipeItemMover;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.SplashGunRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.StorageCrystalRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.TeleportWandRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.TintedLensRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.TransitionRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools.VacuumGunRecipe;
import Reika.ChromatiCraft.Block.BlockPath;
import Reika.ChromatiCraft.Block.BlockPath.PathType;
import Reika.ChromatiCraft.Block.Crystal.BlockCrystalGlow.Bases;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayList;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModRegistry.PowerTypes;

import com.google.common.collect.HashBiMap;

public class RecipesCastingTable {

	public static final RecipesCastingTable instance = new RecipesCastingTable();

	private final HashMap<RecipeType, OneWayList<CastingRecipe>> recipes = new HashMap();
	private final OneWayList<CastingRecipe> APIrecipes = new OneWayList();

	private int maxID = 0;
	private final HashBiMap<Integer, CastingRecipe> recipeIDs;

	private RecipesCastingTable() {
		recipeIDs = HashBiMap.create();
		this.loadRecipes();
	}

	private void loadRecipes() {
		if (ChromatiCraft.instance.isLocked())
			return;

		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.redGroup, " R ", "B P", " M ", 'B', CrystalElement.BLUE, 'R', CrystalElement.RED, 'P', CrystalElement.PURPLE, 'M', CrystalElement.MAGENTA));
		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.greenGroup, " Y ", "C L", " G ", 'G', CrystalElement.GREEN, 'Y', CrystalElement.YELLOW, 'C', CrystalElement.CYAN, 'L', CrystalElement.LIME));
		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.orangeGroup, " B ", "P O", " L ", 'B', CrystalElement.BROWN, 'P', CrystalElement.PINK, 'O', CrystalElement.ORANGE, 'L', CrystalElement.LIGHTBLUE));
		this.addRecipe(new CrystalGroupRecipe(ChromaStacks.whiteGroup, " B ", "G L", " W ", 'B', CrystalElement.BLACK, 'G', CrystalElement.GRAY, 'L', CrystalElement.LIGHTGRAY, 'W', CrystalElement.WHITE));
		this.addRecipe(new CrystalClusterRecipe(ChromaStacks.primaryCluster));
		this.addRecipe(new CrystalClusterRecipe(ChromaStacks.secondaryCluster));
		this.addRecipe(new CrystalCoreRecipe(ChromaStacks.crystalCore, new ItemStack(Items.diamond)));
		this.addRecipe(new CrystalStarRecipe(ChromaStacks.crystalStar, new ItemStack(Items.nether_star)));

		this.addRecipe(new VoidCoreRecipe(ChromaStacks.voidCore, ChromaStacks.crystalStar));
		this.addRecipe(new EnergyCoreRecipe(ChromaStacks.energyCore, ChromaStacks.crystalStar));
		this.addRecipe(new TransformationCoreRecipe(ChromaStacks.transformCore, ChromaStacks.crystalCore));
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

			for (int k = 0; k < Bases.baseList.length; k++) {
				Bases b = Bases.baseList[k];
				ItemStack glow = ChromaBlocks.GLOW.getStackOfMetadata(i+k*16);
				sr = ReikaRecipeHelper.getShapedRecipeFor(glow, "S", "s", 'S', shard, 's', b.ingredient);
				this.addRecipe(new CrystalGlowRecipe(glow, sr));
			}

			this.addRecipe(new CrystalAltarRecipe(e));
		}
		this.addRecipe(new CompoundRelayRecipe(ChromaBlocks.RELAY.getStackOfMetadata(16), new ItemStack(Items.diamond)));

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

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 3, 11), " S ", " S ", " S ", 'S', new ItemStack(block, 1, 0));
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

		for (int i = 0; i <= TileEntityAccelerator.MAX_TIER; i++)
			this.addRecipe(new AcceleratorRecipe(i));

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
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "LFL", "GsG", 'L', Blocks.leaves, 'F', Blocks.red_flower, 'G', Items.glowstone_dust, 's', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new ChromaFlowerRecipe(is, sr));

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
			ItemStack pendant = ChromaItems.PENDANT.getStackOfMetadata(i);
			ItemStack pendant3 = ChromaItems.PENDANT3.getStackOfMetadata(i);

			this.addRecipe(new PendantRecipe(pendant, cave));
			this.addRecipe(new EnhancedPendantRecipe(pendant3, supercry));
			this.addRecipe(new PotionCrystalRecipe(supercry, cave));

			sr = ReikaRecipeHelper.getShapedRecipeFor(ChromaStacks.rawCrystal, " F ", "FSF", " F ", 'F', ChromaStacks.purityDust, 'S', shard);
			this.addRecipe(new RawCrystalRecipe(ChromaStacks.rawCrystal, sr));
		}

		this.addRecipe(new CrystalFocusRecipe(ChromaStacks.crystalFocus, ChromaStacks.primaryCluster));
		this.addRecipe(new CrystalMirrorRecipe(ChromaStacks.crystalMirror, ChromaStacks.getChargedShard(CrystalElement.WHITE)));

		RecipeCrystalRepeater repeater = new RecipeCrystalRepeater(ChromaTiles.REPEATER.getCraftedProduct(), ChromaStacks.crystalCore);
		this.addRecipe(repeater);

		this.addRecipe(new TransitionRecipe(ChromaItems.TRANSITION.getStackOf(), ChromaStacks.transformCore));
		this.addRecipe(new BreakerRecipe(ChromaItems.EXCAVATOR.getStackOf(), ChromaStacks.energyCore));

		this.addRecipe(new ElementUnitRecipe(ChromaStacks.elementUnit, ChromaStacks.bindingCrystal));

		is = ChromaTiles.HEATLILY.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, " F ", "FBF", "LSL", 'L', Blocks.waterlily, 'F', Blocks.yellow_flower, 'S', ChromaStacks.orangeShard, 'B', Items.blaze_powder);
		HeatLilyRecipe hr = new HeatLilyRecipe(is, sr);
		this.addRecipe(hr);

		is = ChromaTiles.RITUAL.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "SSS", "CSC", "CCC", 'C', "cobblestone", 'S', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new RitualTableRecipe(is, sr));

		is = ChromaTiles.COLLECTOR.getCraftedProduct();
		sr = new ShapedOreRecipe(is, "SES", "ScS", "CCC", 'E', Items.ender_eye, 'C', "stone", 'c', Blocks.glowstone, 'S', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new ChromaCollectorRecipe(is, sr));

		this.addRecipe(new SpawnerReprogrammerRecipe(ChromaTiles.REPROGRAMMER.getCraftedProduct(), ChromaStacks.transformCore));

		this.addRecipe(new TelePumpRecipe(ChromaTiles.TELEPUMP.getCraftedProduct(), ChromaStacks.energyCore));

		this.addRecipe(new CompoundRepeaterRecipe(ChromaTiles.COMPOUND.getCraftedProduct(), ChromaStacks.crystalFocus));

		//is = ChromaTiles.FIBER.getCraftedProduct();
		//sr = ReikaRecipeHelper.getShapedRecipeFor(is, "GgG", "GDG", "GgG", 'G', Blocks.glass, 'D', Items.diamond, 'g', Items.glowstone_dust);
		//this.addRecipe(new FiberRecipe(is, sr));

		this.addRecipe(new CompoundRuneRecipe(new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 1, 13), ChromaStacks.bindingCrystal));

		this.addRecipe(new IridescentChunkRecipe(ChromaStacks.iridChunk, ChromaStacks.bindingCrystal));

		this.addRecipe(new IridescentCrystalRecipe(ChromaTiles.CRYSTAL.getCraftedProduct(), new ItemStack(Items.diamond)));

		this.addRecipe(new InfuserRecipe(ChromaTiles.INFUSER.getCraftedProduct(), ChromaTiles.STAND.getCraftedProduct()));

		if (ModList.THAUMCRAFT.isLoaded()) {
			this.addRecipe(new AspectFormerRecipe(ChromaTiles.ASPECT.getCraftedProduct(), ChromaStacks.transformCore));
		}

		this.addRecipe(new InventoryLinkRecipe(ChromaItems.LINK.getStackOf(), ChromaStacks.resonanceDust));

		this.addRecipe(new LampRecipe(ChromaTiles.LAMP.getCraftedProduct(), new ItemStack(Blocks.glowstone)));

		this.addRecipe(new BeaconRecipe(ChromaTiles.BEACON.getCraftedProduct(), new ItemStack(Items.nether_star)));

		is = ChromaItems.FINDER.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "SIS", "IAI", "SIS", 'A', ChromaStacks.auraDust, 'I', Items.iron_ingot, 'S', ChromaItems.SHARD.getAnyMetaStack());
		this.addRecipe(new PylonFinderRecipe(is, sr));

		this.addRecipe(new BatteryRecipe(ChromaTiles.POWERTREE.getCraftedProduct(), ChromaStacks.crystalStar));

		this.addRecipe(new InvTickerRecipe(ChromaTiles.TICKER.getCraftedProduct(), new ItemStack(Blocks.chest)));

		if (ModList.THAUMCRAFT.isLoaded()) {
			is = ChromaItems.WARP.getStackOf();
			this.addRecipe(new AuraCleanerRecipe(is, new ItemStack(Items.potionitem)));
		}

		this.addRecipe(new MinerRecipe(ChromaTiles.MINER.getCraftedProduct(), ChromaStacks.energyCore));

		this.addRecipe(new FabricatorRecipe(ChromaTiles.FABRICATOR.getCraftedProduct(), ChromaStacks.transformCore));

		is = ChromaTiles.LAMPCONTROL.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "DED", "RQR", "ScS", 'c', ChromaStacks.chromaDust, 'D', ChromaStacks.auraDust, 'E', Items.ender_pearl, 'R', Items.redstone, 'Q', Items.quartz, 'S', ReikaItemHelper.stoneSlab);
		this.addRecipe(new LampControlRecipe(is, sr));

		this.addRecipe(new UpgradeRecipe(ChromaStacks.silkUpgrade, new ItemStack(Items.diamond)));
		this.addRecipe(new UpgradeRecipe(ChromaStacks.speedUpgrade, new ItemStack(Items.redstone)));
		this.addRecipe(new UpgradeRecipe(ChromaStacks.efficiencyUpgrade, new ItemStack(Items.emerald)));

		if (ChromaOptions.ENDERTNT.getState())
			this.addRecipe(new RecipeEnderTNT(ChromaBlocks.TNT.getStackOf(), ChromaItems.BUCKET.getStackOfMetadata(1)));

		this.addRecipe(new TeleportWandRecipe(ChromaItems.TELEPORT.getStackOf(), ChromaStacks.energyCore));

		this.addRecipe(new BuilderWandRecipe(ChromaItems.BUILDER.getStackOf(), ChromaStacks.transformCore));

		this.addRecipe(new CaptureWandRecipe(ChromaItems.CAPTURE.getStackOf(), new ItemStack(Items.string)));

		this.addRecipe(new GrowthWandRecipe(ChromaItems.GROWTH.getStackOf(), new ItemStack(Items.diamond)));

		this.addRecipe(new DuplicationWandRecipe(ChromaItems.DUPLICATOR.getStackOf(), ChromaStacks.voidCore));

		if (ModList.APPENG.isLoaded()) {
			this.addRecipe(new VoidStorageRecipe(ChromaItems.VOIDCELL.getStackOf(), ChromaStacks.voidCore));
		}

		for (int i = 0; i < BlockPath.PathType.list.length; i++) {
			PathType p = BlockPath.PathType.list[i];
			this.addRecipe(new PathRecipe(ChromaItems.ELEMENTAL.getStackOf(CrystalElement.LIME), i, p.addition));
		}

		this.addRecipe(new AspectJarRecipe(ChromaTiles.ASPECTJAR.getCraftedProduct(), ChromaStacks.voidCore));

		if (ChromaOptions.BIOMEPAINTER.getState())
			this.addRecipe(new BiomePainterRecipe(ChromaTiles.BIOMEPAINTER.getCraftedProduct(), ChromaStacks.transformCore));

		this.addRecipe(new AuraPouchRecipe(ChromaItems.AURAPOUCH.getStackOf(), ChromaStacks.voidCore));

		this.addRecipe(new FarmerRecipe(ChromaTiles.FARMER.getCraftedProduct(), ChromaStacks.energyCore));

		this.addRecipe(new RelaySourceRecipe(ChromaTiles.RELAYSOURCE.getCraftedProduct(), ChromaStacks.crystalFocus));

		this.addRecipe(new ItemCollectorRecipe(ChromaTiles.ITEMCOLLECTOR.getCraftedProduct(), ChromaStacks.voidCore));

		this.addRecipe(new RepeaterTurboRecipe());

		this.addRecipe(new PortalRecipe(ChromaBlocks.PORTAL.getStackOf(), ChromaStacks.voidCore, repeater));

		is = ChromaBlocks.HEATLAMP.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "fff", "faf", "fff", 'f', ChromaStacks.firaxite, 'a', ChromaStacks.auraIngot);
		this.addRecipe(new HeatLampRecipe(is, sr, hr));

		this.addRecipe(new EnderCrystalRecipe(ChromaItems.ENDERCRYSTAL.getStackOf(), ChromaStacks.crystalStar));

		is = ChromaItems.BULKMOVER.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "OCO", "ODO", " I ", 'O', Blocks.obsidian, 'D', Items.diamond, 'C', Blocks.chest, 'I', Items.iron_ingot);
		this.addRecipe(new RecipeItemMover(is, sr));

		this.addRecipe(new AutomatorRecipe(ChromaTiles.AUTOMATOR.getCraftedProduct(), ChromaStacks.transformCore));

		this.addRecipe(new ChainGunRecipe(ChromaItems.CHAINGUN.getStackOf(), ChromaStacks.crystalCore));
		this.addRecipe(new SplashGunRecipe(ChromaItems.SPLASHGUN.getStackOf(), ChromaStacks.crystalCore));
		this.addRecipe(new VacuumGunRecipe(ChromaItems.VACUUMGUN.getStackOf(), ChromaStacks.voidCore));

		if (ModList.APPENG.isLoaded()) {
			this.addRecipe(new MEDistributorRecipe(ChromaTiles.MEDISTRIBUTOR.getCraftedProduct(), ChromaStacks.transformCore));
		}

		this.addRecipe(new RecipeHoverWand(ChromaItems.HOVERWAND.getStackOf(), ChromaStacks.voidCore));

		if (PowerTypes.RF.exists()) {
			is = ChromaTiles.RFDISTRIBUTOR.getCraftedProduct();
			sr = ReikaRecipeHelper.getShapedRecipeFor(is, "rir", "iGi", "rir", 'i', Items.iron_ingot, 'r', Blocks.redstone_block, 'G', Blocks.glowstone);
			this.addRecipe(new RFDistributorRecipe(is, sr));
		}

		this.addRecipe(new TransportWindowRecipe(ChromaTiles.WINDOW.getCraftedProduct(), new ItemStack(Items.diamond)));

		is = ChromaItems.LINKTOOL.getStackOf();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "  l", " s ", "g  ", 'g', ChromaStacks.grayShard, 'l', ChromaStacks.limeShard, 's', Items.stick);
		this.addRecipe(new LinkToolRecipe(is, sr));

		this.addRecipe(new RecipePersonalCharger(ChromaTiles.PERSONAL.getCraftedProduct(), ChromaStacks.crystalStar));

		is = ChromaTiles.MUSIC.getCraftedProduct();
		sr = ReikaRecipeHelper.getShapedRecipeFor(is, "sns", "non", "sns", 's', ChromaItems.SHARD.getAnyMetaStack(), 'n', Blocks.noteblock, 'o', Items.clock);
		this.addRecipe(new MusicRecipe(is, sr));
	}

	public void addPostLoadRecipes() {
		if (ModList.THAUMCRAFT.isLoaded()) {
			ItemStack is = ThaumItemHelper.BlockEntry.ANCIENTROCK.getItem();
			IRecipe sr = new ShapedOreRecipe(is, "SdS", "dOd", "SdS", 'S', "stone", 'd', ChromaStacks.auraDust, 'O', Blocks.obsidian);
			this.addRecipe(new CastingRecipe(is, sr));
		}
	}

	private void addRecipe(CastingRecipe r) {
		OneWayList<CastingRecipe> li = recipes.get(r.type);
		if (li == null) {
			li = new OneWayList();
			recipes.put(r.type, li);
		}
		li.add(r);

		recipeIDs.put(maxID, r);
		maxID++;
		//ChromaResearchManager.instance.register(r);
	}

	public void addModdedRecipe(CastingRecipe r) {
		this.addRecipe(r);
		APIrecipes.add(r);
	}

	public List<CastingRecipe> getAllAPIRecipes() {
		return Collections.unmodifiableList(APIrecipes);
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
					if (ReikaItemHelper.matchStacks(result, r.getOutput()) && (result.stackTagCompound == null || ItemStack.areItemStackTagsEqual(result, r.getOutput())))
						li.add(r);
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

	public static boolean playerHasCrafted(EntityPlayer ep, RecipeType type) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound cast = nbt.getCompoundTag("castingprog");
		return cast.getBoolean(type.name().toLowerCase());
	}

	public static void setPlayerHasCrafted(EntityPlayer ep, RecipeType type) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound cast = nbt.getCompoundTag("castingprog");
		cast.setBoolean(type.name().toLowerCase(), true);
		nbt.setTag("castingprog", cast);
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
		recipes.clear();
		recipeIDs.clear();
		maxID = 0;

		this.loadRecipes();
		this.addPostLoadRecipes();

		for (CastingRecipe c : APIrecipes) {
			this.addRecipe(c);
		}
	}

}
