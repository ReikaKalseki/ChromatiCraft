/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v2.APIv2;
import com.chocolate.chocolateQuest.API.RegisterChestItem;
import com.google.common.base.Throwables;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Block.Worldgen.BlockCliffStone.Variants;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.ModInterface.Bees.ApiaryAcceleration;
import Reika.ChromatiCraft.ModInterface.Bees.ChromaTrees;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.ChromaAspectManager;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.ChromaAspectMapper;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.CrystalWand;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TieredOreCap;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.BasicModEntry;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerMaterialHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerMaterialHelper.CustomTinkerMaterial;
import Reika.DragonAPI.ModInteract.DeepInteract.TwilightForestLootHooks;
import Reika.DragonAPI.ModInteract.DeepInteract.TwilightForestLootHooks.LootLevels;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler.Potions;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.RotaryCraft.API.BlockColorInterface;

import cpw.mods.fml.common.event.FMLInterModComms;
import forestry.api.apiculture.FlowerManager;
import mekanism.api.MekanismAPI;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchItem;
import vazkii.botania.api.BotaniaAPI;


public class ModInteraction {

	public static void blacklistTravelStaff() {
		try {
			Class c = Class.forName("crazypants.enderio.config.Config");
			//Class c2 = Class.forName("crazypants.enderio.teleport.TravelController");
			Field check = c.getField("travelStaffBlinkThroughSolidBlocksEnabled");
			if (check.getBoolean(null)) {
				Field f = c.getField("travelStaffBlinkBlackList");
				//Field f2 = c2.getDeclaredField("blackList");
				//f2.setAccessible(true);
				//Field inst = c2.getField("instance");

				ArrayList<ChromaBlocks> add = new ArrayList();
				add.add(ChromaBlocks.STRUCTSHIELD);
				add.add(ChromaBlocks.SPECIALSHIELD);
				add.add(ChromaBlocks.DOOR);
				add.add(ChromaBlocks.TELEPORT);

				for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
					ChromaBlocks cb = ChromaBlocks.blockList[i];
					if (cb.isDimensionStructureBlock())
						add.add(cb);
				}

				String[] arr = (String[])f.get(null);
				String[] next = new String[arr.length+add.size()];
				System.arraycopy(arr, 0, next, 0, arr.length);
				for (int i = 0; i < add.size(); i++) {
					next[next.length-add.size()+i] = ReikaRegistryHelper.getGameRegistryName(ChromatiCraft.instance, add.get(i));
				}
				f.set(null, next);
				//ArrayList<UniqueIdentifier> li = (ArrayList<UniqueIdentifier>)f2.get(inst.get(null));
				//for (ChromaBlocks b : add) {
				//	li.add(GameRegistry.findUniqueIdentifierFor(b.getBlockInstance()));
				//}
				for (ChromaBlocks b : add) {
					FMLInterModComms.sendMessage(ModList.ENDERIO.modLabel, "teleport:blacklist:add", ReikaRegistryHelper.getGameRegistryName(ChromatiCraft.instance, b));
				}
			}
		}
		catch (Exception e) {
			ChromatiCraft.logger.logError("Could not add EnderIO travelling staff blacklisting!");
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.ENDERIO, e);
			e.printStackTrace();
		}
	}

	public static void addThaumCraft() {
		ChromaAspectManager.instance.register();
		ChromaAspectMapper.instance.register();

		new CrystalWand();
		TieredOreCap.registerAll();

		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.STRUCTSHIELD.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.SPECIALSHIELD.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.LASEREFFECT.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.TELEPORT.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.DOOR.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.RUNE.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.PYLONSTRUCT.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.PYLON.getBlockInstance());

		if (ModList.THAUMICTINKER.isLoaded()) {
			try {
				Class c = Class.forName("thaumic.tinkerer.common.item.foci.ItemFocusDislocation");
				Field f = c.getDeclaredField("blacklist");
				f.setAccessible(true);
				ArrayList<Block> li = (ArrayList<Block>)f.get(null);
				for (ChromaTiles ct : ChromaTiles.TEList) {
					li.add(ct.getBlock());
				}
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not add dislocation focus blacklisting!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMICTINKER, e);
				e.printStackTrace();
			}
		}

		for (int i = 0; i < ThaumIDHandler.Potions.list.length; i++) {
			Potions p = ThaumIDHandler.Potions.list[i];
			if (p.isWarpRelated() && p.getID() != -1)
				CrystalPotionController.instance.addBadPotionForIgnore(Potion.potionTypes[p.getID()]);
		}
	}

	public static void blacklistMekBoxes() {
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
				ChromatiCraft.logger.logError("Unable to blacklist tiles from Mekanism box");
				e.printStackTrace();
			}
			catch (LinkageError e) {
				ChromatiCraft.logger.logError("Unable to blacklist tiles from Mekanism box");
				e.printStackTrace();
			}
		}
	}

	public static void addForestry() {
		try {
			CrystalBees.register();
			ChromaTrees.register();

			FlowerManager.flowerRegistry.registerAcceptableFlower(ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.ENDERFLOWER.ordinal(), FlowerManager.FlowerTypeVanilla);
			FlowerManager.flowerRegistry.registerAcceptableFlower(ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.GLOWDAISY.ordinal(), FlowerManager.FlowerTypeVanilla);
			FlowerManager.flowerRegistry.registerAcceptableFlower(ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.RESOCLOVER.ordinal(), FlowerManager.FlowerTypeVanilla);
			FlowerManager.flowerRegistry.registerAcceptableFlower(ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.LUMALILY.ordinal(), FlowerManager.FlowerTypeVanilla);
			FlowerManager.flowerRegistry.registerAcceptableFlower(ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.LUMALILY.ordinal(), FlowerManager.FlowerTypeSnow);
			FlowerManager.flowerRegistry.registerAcceptableFlower(ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.SANOBLOOM.ordinal(), FlowerManager.FlowerTypeJungle);
			FlowerManager.flowerRegistry.registerAcceptableFlower(ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.VOIDREED.ordinal(), FlowerManager.FlowerTypeMushrooms);
		}
		catch (Exception e) {
			if (ReikaObfuscationHelper.isDeObfEnvironment())
				Throwables.propagate(e);
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not add Forestry integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
		}
		catch (LinkageError e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not add Forestry integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
		}
		ApiaryAcceleration.instance.register();
		ForestryMultifarmAcceleration.instance.register();
	}

	public static void addTFLoot() {
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

	public static void addChocoDungeonLoot() {
		try {
			RegisterChestItem.addTreasureItem(ChromaItems.FRAGMENT.getStackOf(), 40);

			for (int i = 0; i < 16; i++) {
				ItemStack is = ChromaItems.SHARD.getStackOfMetadata(i);
				int wt = CrystalElement.elements[i].isPrimary() ? 50 : 25;
				RegisterChestItem.addMineralsChestItem(is, wt);
			}
		}
		catch (IllegalAccessError e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not add ChocolateQuest integration, as his API is not accessible (Java code visiblity). Contact that developer.");
		}
		catch (IncompatibleClassChangeError e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not add ChocolateQuest integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
		}
		catch (Exception e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not add ChocolateQuest integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
		}
	}

	public static void blacklistLoonium() {
		try {
			BotaniaAPI.blackListItemFromLoonium(ChromaItems.FRAGMENT.getItemInstance());
			BotaniaAPI.blackListItemFromLoonium(ChromaItems.SHARD.getItemInstance());
		}
		catch (LinkageError e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not add Botania integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
		}
		catch (Exception e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not add Botania integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
		}
	}

	public static void blacklistMFRSafariNet() {
		try {
			Class c = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			Method m = c.getMethod("registerSafariNetBlacklist", Class.class);
			m.invoke(null, EntityBallLightning.class);
			m = c.getMethod("registerAutoSpawnerBlacklistClass", Class.class);
			m.invoke(null, EntityBallLightning.class);
		}
		catch (Exception e) {
			ChromatiCraft.logger.logError("Could not blacklist Ball Lightning from MFR Safari Net!");
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.MINEFACTORY, e);
			e.printStackTrace();
		}
	}

	public static void addCarpenterCovers() {
		try {
			Class c = Class.forName("com.carpentersblocks.util.registry.FeatureRegistry");
			Field f = c.getDeclaredField("coverExceptions");
			f.setAccessible(true);
			List<String> li = (List<String>)f.get(null);
			for (int i = 0; i < 16; i++)
				li.add(ChromaBlocks.GLASS.getStackOfMetadata(i).getDisplayName());
		}
		catch (Exception e) {
			ChromatiCraft.logger.logError("Could not add compatibility with Carpenter's blocks!");
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.CARPENTER, e);
			e.printStackTrace();
		}
	}

	public static void blacklistRFToolsTeleport() {
		try {
			Class c = Class.forName("mcjty.rftools.blocks.teleporter.TeleportConfiguration");
			Field f1 = c.getDeclaredField("blacklistedTeleportationSourcesSet");
			Field f2 = c.getDeclaredField("blacklistedTeleportationDestinationsSet");
			f1.setAccessible(true);
			f2.setAccessible(true);
			Set<Integer> set1 = (Set<Integer>)f1.get(null);
			Set<Integer> set2 = (Set<Integer>)f2.get(null);
			if (set1 == null) {
				set1 = new HashSet();
				f1.set(null, set1);
			}
			if (set2 == null) {
				set2 = new HashSet();
				f2.set(null, set2);
			}
			set1.add(ExtraChromaIDs.DIMID.getValue());
			set2.add(ExtraChromaIDs.DIMID.getValue());
		}
		catch (Exception e) {
			ChromatiCraft.logger.logError("Could not add RF Tools teleportation blacklisting!");
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.RFTOOLS, e);
			e.printStackTrace();
		}
	}

	public static void addRCGPRColors() {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
			CrystalElement e = CrystalElement.elements[i];
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.DECAY.getBlockInstance(), i, dye.color);
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.DYELEAF.getBlockInstance(), i, dye.color);
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.DYE.getBlockInstance(), i, dye.color);
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.DYESAPLING.getBlockInstance(), i, dye.color);
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.CRYSTAL.getBlockInstance(), i, e.getColor());
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.SUPER.getBlockInstance(), i, e.getColor());
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.LAMP.getBlockInstance(), i, e.getColor());
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.GLOW.getBlockInstance(), i, e.getColor());
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.GLASS.getBlockInstance(), i, e.getColor());
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.PLANT.getBlockInstance(), i, ReikaColorAPI.mixColors(e.getColor(), 0x00990F, 0.5F));
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.RUNE.getBlockInstance(), i, ReikaColorAPI.mixColors(e.getColor(), 0x303030, 0.5F));
		}

		BlockColorInterface.addGPRBlockColor(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 0x3F3F3F);
		BlockColorInterface.addGPRBlockColor(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 0x303030);
		BlockColorInterface.addGPRBlockColor(ChromaBlocks.CHROMA.getBlockInstance(), 0xE5C7E5);
		BlockColorInterface.addGPRBlockColor(ChromaBlocks.LUMA.getBlockInstance(), 0x570091);

		for (int i = 0; i < Flowers.list.length; i++) {
			BlockColorInterface.addGPRBlockColor(ChromaBlocks.DECOFLOWER.getBlockInstance(), i, Flowers.list[i].getColor());
		}

		for (int i = 0; i < TieredOres.list.length; i++) {
			BlockColorInterface.addGPRBlockMimic(ChromaBlocks.TIEREDORE.getBlockInstance(), i, Blocks.stone);
		}

		for (int i = 0; i < TieredPlants.list.length; i++) {
			BlockColorInterface.addGPRBlockMimic(ChromaBlocks.TIEREDPLANT.getBlockInstance(), i, Blocks.air);
		}
	}

	public static void blacklistGoldAppleUncrafting() {
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
			ChromatiCraft.logger.logError("Unable to blacklist golden apple recycling");
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.BLUEPOWER, e);
			e.printStackTrace();
		}
	}

	public static void addMicroblocks() {
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
	}

	public static void addMystCraft() {
		ReikaMystcraftHelper.registerPageRegistry(MystPages.instance);
		for (int i = 0; i < Biomes.biomeList.length; i++) {
			ReikaMystcraftHelper.disableBiomePage(Biomes.biomeList[i].getBiome());
		}
		for (int i = 0; i < SubBiomes.biomeList.length; i++) {
			ReikaMystcraftHelper.disableBiomePage(SubBiomes.biomeList[i].getBiome());
		}
	}

	public static void addChromastoneTools() {
		int id = ExtraChromaIDs.CHROMAMATID.getValue();
		CustomTinkerMaterial mat = TinkerMaterialHelper.instance.createMaterial(id, ChromatiCraft.instance, "Chromastone");
		mat.durability = 200;
		mat.damageBoost = 10;
		mat.harvestLevel = 1200;
		mat.miningSpeed = 2700;
		mat.handleModifier = 0.25F;
		mat.chatColor = EnumChatFormatting.GOLD.toString();
		mat.renderColor = 0x22aaff;

		mat.register(true).registerTexture("tinkertools/chromastone/chromastone", false);
		//mat.registerPatternBuilder(ChromaStacks.complexIngot);
		mat.registerWeapons(ChromaStacks.complexIngot, 10, 0.5F, 5F, 4F, 15F, 0);
	}

	@ModDependent(ModList.FORESTRY)
	public static void addCrystalBackpack() {
		CrystalBackpack.instance.register();
	}

	@ModDependent(ModList.THAUMCRAFT)
	public static void addThaumRecipes() {
		Class root = ChromatiCraft.class;
		String ref = ChromaDescriptions.getParentPage()+"thaum.xml";

		ReikaThaumHelper.addBookCategory(new ResourceLocation("chromaticraft", "textures/blocks/tile/table_top.png"), "chromaticraft");

		MathExpression costFactor = new MathExpression() {
			@Override
			public double evaluate(double arg) throws ArithmeticException {
				return 1.5*Math.sqrt(arg);
			}

			@Override
			public double getBaseValue() {
				return 0;
			}

			@Override
			public String toString() {
				return "1.5*sqrt()";
			}
		};

		{
			ItemStack in = ChromaItems.WARP.getStackOfMetadata(0);
			ItemStack out = ChromaItems.WARP.getStackOfMetadata(1);
			String desc = "Pitting one kind of magic against another";
			AspectList al = new AspectList();
			al.add(Aspect.ELDRITCH, 120);
			al.add(Aspect.TAINT, 240);
			al.add(Aspect.MAGIC, 120);
			al.add(Aspect.EXCHANGE, 60);
			al.add(Aspect.CRYSTAL, 90);
			al.add(Aspect.HEAL, 240);
			al.add(Aspect.SENSES, 60);
			al.add(ChromaAspectManager.instance.PUZZLE, 90);
			ItemStack[] recipe = {
					ThaumItemHelper.ItemEntry.GOO.getItem(),
					ThaumItemHelper.ItemEntry.FABRIC.getItem(),
					ThaumItemHelper.ItemEntry.SALTS.getItem(),
					new ItemStack(Items.string),
					ThaumItemHelper.ItemEntry.PRIMALFOCUS.getItem(),
					ThaumItemHelper.ItemEntry.FABRIC.getItem(),
					ThaumItemHelper.ItemEntry.SALTS.getItem(),
					new ItemStack(Items.string),
					new ItemStack(ThaumItemHelper.BlockEntry.CRYSTAL.getBlock(), 1, 6),
			};
			InfusionRecipe ir = ThaumcraftApi.addInfusionCraftingRecipe("WARPPROOF", out, 24, al, in, recipe);
			ReikaThaumHelper.addInfusionRecipeBookEntryViaXML(ChromatiCraft.instance, "WARPPROOF", desc, "chromaticraft", ir, costFactor, -2, 0, root, ref).setParents("ELDRITCHMAJOR").setSpecial();
			ThaumcraftApi.addWarpToResearch("WARPPROOF", 6); //Taboo is 5
		}

		{
			AspectList al = new AspectList();
			al.add(Aspect.MAGIC, 80);
			al.add(Aspect.ORDER, 40);
			al.add(Aspect.AURA, 120);
			al.add(Aspect.ENERGY, 200);
			al.add(Aspect.EXCHANGE, 50);
			al.add(ChromaAspectManager.instance.SIGNAL, 360);
			al.add(Aspect.CRYSTAL, 200);
			ItemStack[] recipe = {
					ThaumItemHelper.ItemEntry.BALANCED.getItem(),
					ThaumItemHelper.ItemEntry.THAUMIUM.getItem(),
					ThaumItemHelper.ItemEntry.VISFITLER.getItem(),
					ThaumItemHelper.ItemEntry.BALANCED.getItem(),
					ThaumItemHelper.ItemEntry.THAUMIUM.getItem(),
					ThaumItemHelper.ItemEntry.VISFITLER.getItem(),
			};
			InfusionRecipe ir = ThaumcraftApi.addInfusionCraftingRecipe("ROD_CRYSTALWAND", ChromaStacks.crystalWand, 18, al, ChromaStacks.iridChunk, recipe);
			ReikaThaumHelper.addInfusionRecipeBookEntryViaXML(ChromatiCraft.instance, "ROD_CRYSTALWAND", "Fashioning a wand from crystals", "chromaticraft", ir, costFactor, 2, 0, root, ref).setSpecial().setParents("ROD_silverwood", "CAP_thaumium", "SCEPTRE", "PYLONWANDING");
		}

		{
			AspectList al = new AspectList();
			al.add(Aspect.MAGIC, 10);
			al.add(Aspect.CRYSTAL, 10);
			al.add(ChromaAspectManager.instance.PUZZLE, 10);
			ItemStack[] recipe = {
					ThaumItemHelper.ItemEntry.BALANCED.getItem(),
					new ItemStack(Items.quartz),
					ChromaStacks.rawCrystal,
					ThaumItemHelper.ItemEntry.BALANCED.getItem(),
					new ItemStack(Items.quartz),
					ChromaStacks.rawCrystal,
			};
			InfusionRecipe ir = ThaumcraftApi.addInfusionCraftingRecipe("MANIPFOCUS", ChromaItems.MANIPFOCUS.getStackOf(), 0, al, ChromaItems.TOOL.getStackOf(), recipe);
			ReikaThaumHelper.addInfusionRecipeBookEntryViaXML(ChromatiCraft.instance, "MANIPFOCUS", "Why have two wands when you can have one?", "chromaticraft", ir, costFactor, 2, -4, root, ref).setParents("BASICTHAUMATURGY", "INFUSION", "CCINTRO");
		}

		{
			String desc = "Big energy for even bigger ambitions";
			AspectList al = new AspectList();
			al.add(Aspect.MAGIC, 5);
			al.add(ChromaAspectManager.instance.SIGNAL, 8);
			al.add(Aspect.EXCHANGE, 3);
			al.add(Aspect.HARVEST, 2);
			if (ChromaOptions.HARDTHAUM.getState()) {
				for (Entry<Aspect, Integer> e : al.aspects.entrySet()) {
					e.setValue(e.getValue()*5);
				}
			}
			ResourceLocation ico = new ResourceLocation("chromaticraft", "textures/aspects/pylonwanding.png");
			ResearchItem ri = ReikaThaumHelper.addBasicBookEntryViaXML(ChromatiCraft.instance, "PYLONWANDING", "Pylon-Sourced Vis", desc, "chromaticraft", al, 2, -2, root, ref, ico);
			ri.setRound().setSpecial();
			if (ChromaOptions.HARDTHAUM.getState())
				ri.setParents("NODETAPPER2", "RESEARCHER2", "WANDPEDFOC", "CCINTRO", "CAP_gold", "ROD_greatwood");
			else
				ri.setParents("BASICTHAUMATURGY", "WANDPED", "CCINTRO");
		}

		{
			AspectList al = new AspectList();
			al.add(Aspect.MAGIC, 20);
			al.add(Aspect.TOOL, 10);
			al.add(ChromaAspectManager.instance.PRECURSOR, 20);
			ItemStack in = ThaumItemHelper.ItemEntry.PRIMALFOCUS.getItem();
			ArrayList<ItemStack> items = new ArrayList();
			for (int i = 0; i < 16; i++) {
				//items.add(new ItemStack(ThaumOreHandler.getInstance().shardID, 1, i));
				items.add(ChromaStacks.getChargedShard(CrystalElement.elements[i]));
				items.add(ThaumItemHelper.ItemEntry.BALANCED.getItem());
				if (i%4 == 0) {
					items.add(ChromaStacks.elementDust);
				}
				if (i%8 == 0) {
					items.add(new ItemStack(Blocks.glowstone));
				}
			}
			InfusionRecipe ir = ThaumcraftApi.addInfusionCraftingRecipe("ABILITYFOCUS", ChromaItems.ABILITYFOCUS.getStackOf(), 5, al, in, items.toArray(new ItemStack[items.size()]));
			ReikaThaumHelper.addInfusionRecipeBookEntryViaXML(ChromatiCraft.instance, "ABILITYFOCUS", "For when convenience becomes inconvenient", "chromaticraft", ir, costFactor, 0, -2, root, ref).setParents("FOCALMANIPULATION", "INFUSION", "MANIPFOCUS", "FOCUSPRIMAL");
		}

		{
			String desc = "Making parallel lines cross";
			ResourceLocation ico = new ResourceLocation("chromaticraft", "textures/aspects/intro.png");
			ResearchItem ri = ReikaThaumHelper.addBasicBookEntryViaXML(ChromatiCraft.instance, "CCINTRO", "Two kinds of magic", desc, "chromaticraft", null, 2, -2, root, ref, ico);
			ri.setRound().setAutoUnlock();
		}

		TieredOreCap.addRecipes();

		ThaumcraftApi.addWarpToResearch("ROD_CRYSTALWAND", ChromaOptions.HARDTHAUM.getState() ? 5 : 2);

		ThaumcraftApi.addWarpToResearch("PYLONWANDING", ChromaOptions.HARDTHAUM.getState() ? 4 : 2);
	}

	public static void modifyRFToolsPages() {
		for (int i = 0; i < Biomes.biomeList.length; i++) {
			FMLInterModComms.sendMessage(ModList.RFTOOLS.modLabel, "dimlet_blacklist", "Biome."+Biomes.biomeList[i].getBiome().biomeName);
		}
		for (int i = 0; i < SubBiomes.biomeList.length; i++) {
			FMLInterModComms.sendMessage(ModList.RFTOOLS.modLabel, "dimlet_blacklist", "Biome."+SubBiomes.biomeList[i].getBiome().biomeName);
		}
		FMLInterModComms.sendMessage(ModList.RFTOOLS.modLabel, "dimlet_blacklist", "Biome."+ChromatiCraft.glowingcliffsEdge.biomeName);

		FMLInterModComms.sendMessage(ModList.RFTOOLS.modLabel, "dimlet_configure", "Biome."+ChromatiCraft.rainbowforest.biomeName+"=600000,12000,600,3");
		FMLInterModComms.sendMessage(ModList.RFTOOLS.modLabel, "dimlet_configure", "Biome."+ChromatiCraft.glowingcliffs.biomeName+"=400000,9000,300,2");
		FMLInterModComms.sendMessage(ModList.RFTOOLS.modLabel, "dimlet_configure", "Biome."+ChromatiCraft.enderforest.biomeName+"=500000,9000,400,3");
	}

	public static void blacklistTardisFromDimension() {
		try {
			Class c = Class.forName("tardis.common.dimension.TardisDimensionHandler");
			Field f = c.getDeclaredField("blacklistedIDs");
			f.setAccessible(true);
			ArrayList<Integer> li = (ArrayList<Integer>)f.get(null);
			li.add(ExtraChromaIDs.DIMID.getValue());
		}
		catch (Exception e) {
			ChromatiCraft.logger.logError("Unable to blacklist Tardis Mod from dimension");
			ReflectiveFailureTracker.instance.logModReflectiveFailure(new BasicModEntry("TardisMod"), e);
			e.printStackTrace();
		}
	}

	public static void setDynSurroundSettings() {
		ChromatiCraft.logger.log("Adding DynSurrounds biome compat.");
		try {
			Class c = Class.forName("org.blockartistry.mod.DynSurround.data.BiomeRegistry");
			Class c2 = Class.forName("org.blockartistry.mod.DynSurround.data.BiomeRegistry$Entry");
			Method m = c.getDeclaredMethod("get", BiomeGenBase.class);
			m.setAccessible(true);
			Field f1 = c2.getDeclaredField("sounds");
			Field f2 = c2.getDeclaredField("spotSounds");
			f1.setAccessible(true);
			f2.setAccessible(true);
			Object entry = m.invoke(null, ChromatiCraft.glowingcliffs);
			if (entry != null) {
				Field f = entry.getClass().getDeclaredField("hasAurora");
				f.setAccessible(true);
				f.setBoolean(entry, true);
			}
			else {
				ChromatiCraft.logger.logError("Entry for biome glowcliffs was null.");
			}
			for (Biomes b : Biomes.biomeList) {
				clearSounds(m, f1, f2, b.getBiome());
				SubBiomes sub = b.getSubBiome();
				if (sub != null)
					clearSounds(m, f1, f2, sub.getBiome());
			}
		}
		catch (Exception e) {
			ChromatiCraft.logger.logError("Unable to load DynSurround biome data");
			ReflectiveFailureTracker.instance.logModReflectiveFailure(new BasicModEntry("dsurround"), e);
			e.printStackTrace();
		}
	}

	private static void clearSounds(Method m, Field soundField1, Field soundField2, ChromaDimensionBiome biome) throws Exception {
		Object entry = m.invoke(null, biome);
		if (entry != null) {
			List li = (List)soundField1.get(entry);
			li.clear();
			li = (List)soundField2.get(entry);
			li.clear();
		}
		else {
			ChromatiCraft.logger.logError("Entry for biome "+biome.biomeName+" was null.");
		}
	}

	public static void registerCliffSoil() {
		((APIv2)API.getAPI(2)).registerDefaultSoil(new BlockWithMeta(ChromaBlocks.CLIFFSTONE.getBlockInstance(), Variants.FARMLAND.getMeta(false, false)));
	}
}
