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

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ReikaThaumHelper;
import Reika.RotaryCraft.API.BlockColorInterface;
import Reika.RotaryCraft.API.GrinderAPI;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

public class ChromaRecipes {

	public static void addRecipes() {

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i);
			ItemStack cave = new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i);
			ItemStack supercry = new ItemStack(ChromaBlocks.SUPER.getBlockInstance(), 1, i);
			ItemStack seed = ChromaItems.SEED.getStackOfMetadata(i);
			ItemStack pendant = ChromaItems.PENDANT.getStackOfMetadata(i);
			ItemStack pendant3 = ChromaItems.PENDANT3.getStackOfMetadata(i);

			GameRegistry.addRecipe(lamp, " s ", "sss", "SSS", 's', shard, 'S', ReikaItemHelper.stoneSlab);
			GameRegistry.addRecipe(pendant, "GSG", "QCQ", "EDE", 'E', Items.ender_pearl, 'D', Items.diamond, 'G', Blocks.glowstone, 'Q', Items.quartz, 'C', cave, 'S', Items.string);
			GameRegistry.addRecipe(pendant3, "DSD", "GCG", "ETE", 'E', Items.ender_eye, 'D', Items.diamond, 'G', Items.gold_ingot, 'T', Items.ghast_tear, 'C', supercry, 'S', Items.string);
		}

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i);
			ItemStack potion = new ItemStack(ChromaBlocks.SUPER.getBlockInstance(), 1, i);
			GameRegistry.addRecipe(potion, "RsG", "sss", "SDS", 's', shard, 'S', Blocks.obsidian, 'D', Blocks.gold_block, 'R', Blocks.redstone_block, 'G', Blocks.glowstone);
			GameRegistry.addRecipe(potion, "RlG", "SDS", 'l', lamp, 'S', Blocks.obsidian, 'D', Blocks.gold_block, 'R', Blocks.redstone_block, 'G', Blocks.glowstone);
		}

		//GameRegistry.addRecipe(ChromaTiles.BREWER.getCraftedProduct(), "NNN", "NBN", "SSS", 'N', Items.quartz, 'S', Blocks.stone, 'B', Items.brewing_stand);
		//GameRegistry.addRecipe(ChromaTiles.GUARDIAN.getCraftedProduct(), "BBB", "BPB", "BBB", 'B', getShard(ReikaDyeHelper.WHITE), 'P', ChromaItems.CLUSTER.getStackOfMetadata(11));
		//GameRegistry.addRecipe(ChromaTiles.ACCELERATOR.getCraftedProduct(), "DCD", "CSC", "DCD", 'D', Items.diamond, 'S', ChromaItems.CLUSTER.getStackOfMetadata(11), 'C', getShard(ReikaDyeHelper.BLUE));
		//GameRegistry.addRecipe(ChromaItems.ENDERCRYSTAL.getStackOf(), "ISI", "SCS", "ISI", 'I', Items.iron_ingot, 'S', getShard(ReikaDyeHelper.WHITE), 'C', ChromaItems.CLUSTER.getStackOfMetadata(11));
		/*
		Item[] upgrade = {Items.iron_ingot, Items.gold_ingot, Items.diamond, Items.emerald, Items.nether_star};
		int[] index = {0, 0, 1, 1, 2, 3, 4};

		for (int i = 0; i < TileEntityAccelerator.MAX_TIER; i++) {
			ItemStack s1 = getShard(ReikaDyeHelper.getColorFromDamage(i%4*4));
			ItemStack s2 = getShard(ReikaDyeHelper.getColorFromDamage(1+i%4*4));
			ItemStack s3 = getShard(ReikaDyeHelper.getColorFromDamage(2+i%4*4));
			ItemStack s4 = getShard(ReikaDyeHelper.getColorFromDamage(3+i%4*4));
			ItemStack prev = new ItemStack(ChromaBlocks.ACCELERATOR.getBlockInstance(), 1, i);
			GameRegistry.addRecipe(new ItemStack(ChromaBlocks.ACCELERATOR.getBlockInstance(), 1, i+1), "D1D", "2A3", "D4D", 'D', upgrade[index[i]], 'A', prev, '1', s1, '2', s2, '3', s3, '4', s4);
		}*/

		if (ModList.THERMALEXPANSION.isLoaded()) {
			FluidStack crystal = FluidRegistry.getFluidStack("potion crystal", 8000);
			int energy = 40000;
			for (int i = 0; i < 16; i++) {
				NBTTagCompound toSend = new NBTTagCompound();
				toSend.setInteger("energy", energy);
				toSend.setTag("input", new NBTTagCompound());
				toSend.setTag("output", new NBTTagCompound());
				ChromaItems.SHARD.getStackOfMetadata(i).writeToNBT(toSend.getCompoundTag("input"));
				crystal.writeToNBT(toSend.getCompoundTag("output"));
				FMLInterModComms.sendMessage(ModList.THERMALEXPANSION.modLabel, "CrucibleRecipe", toSend);
			}
		}

		GameRegistry.addRecipe(ChromaItems.CLUSTER.getStackOfMetadata(0), " R ", "B P", " M ", 'B', getShard(ReikaDyeHelper.BLUE), 'R', getShard(ReikaDyeHelper.RED), 'P', getShard(ReikaDyeHelper.PURPLE), 'M', getShard(ReikaDyeHelper.MAGENTA));
		GameRegistry.addRecipe(ChromaItems.CLUSTER.getStackOfMetadata(1), " Y ", "C L", " G ", 'G', getShard(ReikaDyeHelper.GREEN), 'Y', getShard(ReikaDyeHelper.YELLOW), 'C', getShard(ReikaDyeHelper.CYAN), 'L', getShard(ReikaDyeHelper.LIME));
		GameRegistry.addRecipe(ChromaItems.CLUSTER.getStackOfMetadata(2), " B ", "P O", " L ", 'B', getShard(ReikaDyeHelper.BROWN), 'P', getShard(ReikaDyeHelper.PINK), 'O', getShard(ReikaDyeHelper.ORANGE), 'L', getShard(ReikaDyeHelper.LIGHTBLUE));
		GameRegistry.addRecipe(ChromaItems.CLUSTER.getStackOfMetadata(3), " B ", "G L", " W ", 'B', getShard(ReikaDyeHelper.BLACK), 'G', getShard(ReikaDyeHelper.GRAY), 'L', getShard(ReikaDyeHelper.LIGHTGRAY), 'W', getShard(ReikaDyeHelper.WHITE));

		GameRegistry.addRecipe(ChromaItems.CLUSTER.getStackOfMetadata(8), " B ", "G G", " B ", 'B', ChromaItems.CLUSTER.getStackOfMetadata(2), 'G', ChromaItems.CLUSTER.getStackOfMetadata(3));
		GameRegistry.addRecipe(ChromaItems.CLUSTER.getStackOfMetadata(9), " B ", "G G", " B ", 'B', ChromaItems.CLUSTER.getStackOfMetadata(0), 'G', ChromaItems.CLUSTER.getStackOfMetadata(1));

		GameRegistry.addRecipe(ChromaItems.CLUSTER.getStackOfMetadata(10), " B ", "G G", " B ", 'B', ChromaItems.CLUSTER.getStackOfMetadata(8), 'G', ChromaItems.CLUSTER.getStackOfMetadata(9));

		GameRegistry.addRecipe(ChromaItems.CLUSTER.getStackOfMetadata(11), " B ", "BPB", " B ", 'B', ChromaItems.CLUSTER.getStackOfMetadata(10), 'P', Items.nether_star);
	}

	private static ItemStack getShard(ReikaDyeHelper color) {
		return ChromaItems.SHARD.getStackOfMetadata(color.ordinal());
	}

	public static void addPostLoadRecipes() {
		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.CRYSTAL.getBlockInstance(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.LAMP.getBlockInstance(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.SUPER.getBlockInstance(), i, dye.color);
				ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
				GrinderAPI.addRecipe(new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 12));
				GrinderAPI.addRecipe(new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 4));
			}
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				ItemStack crystal = new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i);
				ItemStack lamp = new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i);
				ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
				ArrayList<Aspect> li = CrystalPotionController.getAspects(dye);

				ReikaThaumHelper.addAspects(shard, Aspect.CRYSTAL, 1);
				ReikaThaumHelper.addAspects(crystal, Aspect.CRYSTAL, 20);
				ReikaThaumHelper.addAspects(crystal, Aspect.AURA, 4);
				ReikaThaumHelper.addAspects(crystal, Aspect.LIGHT, 3);
				ReikaThaumHelper.addAspects(crystal, Aspect.MAGIC, 6);
				ReikaThaumHelper.addAspects(lamp, Aspect.LIGHT, 8);

				for (int k = 0; k < li.size(); k++) {
					Aspect as = li.get(k);
					ReikaThaumHelper.addAspects(shard, as, 2);
					ReikaThaumHelper.addAspects(crystal, as, 16);
				}
			}
		}
	}

	public static void loadDictionary() {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ReikaDyeHelper color = ReikaDyeHelper.dyes[i];
			ItemStack crystal = new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i);
			ItemStack shard = new ItemStack(ChromaItems.SHARD.getItemInstance(), 1, i);
			OreDictionary.registerOre(color.getOreDictName()+"Crystal", crystal);
			OreDictionary.registerOre(color.getOreDictName()+"CrystalShard", shard);
			OreDictionary.registerOre("dyeCrystal", crystal);
			OreDictionary.registerOre("caveCrystal", crystal);
			OreDictionary.registerOre("shardCrystal", shard);
		}
	}

}
