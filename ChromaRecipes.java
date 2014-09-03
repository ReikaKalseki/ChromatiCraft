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
			GameRegistry.addRecipe(lamp, " s ", "sss", "SSS", 's', shard, 'S', ReikaItemHelper.stoneSlab);
		}

		ChromaItems.TOOL.addRecipe("  s", " S ", "S  ", 'S', Items.stick, 's', ChromaItems.SHARD.getItemInstance());

		//GameRegistry.addRecipe(ChromaTiles.BREWER.getCraftedProduct(), "NNN", "NBN", "SSS", 'N', Items.quartz, 'S', Blocks.stone, 'B', Items.brewing_stand);
		//GameRegistry.addRecipe(ChromaItems.ENDERCRYSTAL.getStackOf(), "ISI", "SCS", "ISI", 'I', Items.iron_ingot, 'S', getShard(ReikaDyeHelper.WHITE), 'C', ChromaItems.CLUSTER.getStackOfMetadata(11));

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
