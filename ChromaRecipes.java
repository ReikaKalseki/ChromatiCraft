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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.RotaryCraft.API.BlockColorInterface;
import Reika.RotaryCraft.API.GrinderAPI;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

public class ChromaRecipes {

	public static void addRecipes() {

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i);
			ItemStack berry = ChromaItems.BERRY.getStackOfMetadata(i);
			GameRegistry.addRecipe(lamp, " s ", "sss", "SSS", 's', shard, 'S', ReikaItemHelper.stoneSlab);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, i), berry);
		}

		ChromaItems.TOOL.addRecipe("  s", " S ", "S  ", 'S', Items.stick, 's', ChromaItems.SHARD.getAnyMetaStack());
		GameRegistry.addRecipe(ChromaTiles.TABLE.getCraftedProduct(), "SCS", "SsS", "sss", 'S', Blocks.stone, 's', new ItemStack(ChromaItems.SHARD.getItemInstance(), 1, OreDictionary.WILDCARD_VALUE), 'C', Blocks.crafting_table);
		ChromaItems.HELP.addRecipe("abc", "gBg", "def", 'B', Items.book, 'a', getShard(CrystalElement.BLACK), 'b', getShard(CrystalElement.BLUE), 'c', getShard(CrystalElement.GREEN), 'd', getShard(CrystalElement.YELLOW), 'e', getShard(CrystalElement.RED), 'f', getShard(CrystalElement.WHITE), 'g', Items.glowstone_dust);

		//GameRegistry.addRecipe(ChromaTiles.BREWER.getCraftedProduct(), "NNN", "NBN", "SSS", 'N', Items.quartz, 'S', Blocks.stone, 'B', Items.brewing_stand);
		//GameRegistry.addRecipe(ChromaItems.ENDERCRYSTAL.getStackOf(), "ISI", "SCS", "ISI", 'I', Items.iron_ingot, 'S', getShard(ReikaDyeHelper.WHITE), 'C', ChromaItems.CLUSTER.getStackOfMetadata(11));

		if (ModList.THERMALEXPANSION.isLoaded()) {
			FluidStack crystal = FluidRegistry.getFluidStack("potion crystal", 500);
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

	private static ItemStack getShard(CrystalElement color) {
		return ChromaItems.SHARD.getStackOfMetadata(color.ordinal());
	}

	public static void addPostLoadRecipes() {
		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < CrystalElement.elements.length; i++) {
				CrystalElement color = CrystalElement.elements[i];
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.CRYSTAL.getBlockInstance(), i, color.getColor());
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.LAMP.getBlockInstance(), i, color.getColor());
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.SUPER.getBlockInstance(), i, color.getColor());
				ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
				GrinderAPI.addRecipe(new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 12));
				GrinderAPI.addRecipe(new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 4));
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
