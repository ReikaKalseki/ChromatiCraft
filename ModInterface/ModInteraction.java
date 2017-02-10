/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import java.util.Set;

import mekanism.api.MekanismAPI;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import thaumcraft.api.ThaumcraftApi;
import vazkii.botania.api.BotaniaAPI;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.ModInterface.Bees.ApiaryAcceleration;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerMaterialHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerMaterialHelper.CustomTinkerMaterial;
import Reika.DragonAPI.ModInteract.DeepInteract.TwilightForestLootHooks;
import Reika.DragonAPI.ModInteract.DeepInteract.TwilightForestLootHooks.LootLevels;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler.Potions;
import Reika.RotaryCraft.API.BlockColorInterface;

import com.chocolate.chocolateQuest.API.RegisterChestItem;
import com.chocolate.chocolateQuest.API.WeightedItemStack;

import cpw.mods.fml.common.event.FMLInterModComms;


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

		new CrystalWand().setGlowing(true);
		TieredOreCap.registerAll();

		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.STRUCTSHIELD.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.SPECIALSHIELD.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.LASEREFFECT.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.TELEPORT.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.DOOR.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.RUNE.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.PYLONSTRUCT.getBlockInstance());
		ThaumcraftApi.portableHoleBlackList.add(ChromaBlocks.PYLON.getBlockInstance());

		for (int i = 0; i < ThaumIDHandler.Potions.list.length; i++) {
			Potions p = ThaumIDHandler.Potions.list[i];
			if (p.isWarpRelated() && p.getID() != -1)
				CrystalPotionController.addIgnoredPotion(Potion.potionTypes[p.getID()]);
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
		}
		catch (Exception e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not add Forestry integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
		}
		catch (LinkageError e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not add Forestry integration. Check your versions; if you are up-to-date with both mods, notify Reika.");
		}
		ApiaryAcceleration.instance.register();
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
			RegisterChestItem.treasureList.add(new WeightedItemStack(ChromaItems.FRAGMENT.getStackOf(), 40));

			for (int i = 0; i < 16; i++) {
				ItemStack is = ChromaItems.SHARD.getStackOfMetadata(i);
				int wt = CrystalElement.elements[i].isPrimary() ? 50 : 25;
				RegisterChestItem.mineralList.add(new WeightedItemStack(is, wt));
			}
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
}
