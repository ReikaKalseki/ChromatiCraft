/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.ModRegistry.ModOreList;

import com.google.common.collect.HashBiMap;

/** Specifies the magic elements present in each "fundamental" item. */
public class ItemMagicRegistry {

	public static final ItemMagicRegistry instance = new ItemMagicRegistry();

	private final HashBiMap<KeyedItemStack, ElementTagCompound> data = HashBiMap.create();

	private ItemMagicRegistry() {
		OreDictionary.initVanillaEntries();
		this.addElement("stone", CrystalElement.RED, 1);
		this.addElement("stone", CrystalElement.BROWN, 2);
		this.addElement("cobblestone", CrystalElement.BROWN, 1);

		this.addElement("logWood", CrystalElement.GREEN, 2);
		this.addElement("plankWood", CrystalElement.GREEN, 1);
		this.addElement("stickWood", CrystalElement.GREEN, 1);
		this.addElement("slabWood", CrystalElement.GREEN, 1);
		this.addElement("stairWood", CrystalElement.GREEN, 1);
		this.addElement("stairWood", CrystalElement.LIME, 1);
		this.addElement("treeSapling", CrystalElement.GREEN, 1);
		this.addElement("treeLeaves", CrystalElement.GREEN, 1);

		this.addElement(Blocks.dirt, CrystalElement.GREEN, 1);
		this.addElement(Blocks.dirt, CrystalElement.BROWN, 1);

		this.addElement("gemDiamond", CrystalElement.BROWN, 6);
		this.addElement("gemDiamond", CrystalElement.PURPLE, 4);
		this.addElement("gemDiamond", CrystalElement.WHITE, 1);
		this.addElement("gemEmerald", CrystalElement.BROWN, 6);
		this.addElement("gemEmerald", CrystalElement.PURPLE, 4);
		this.addElement("gemEmerald", CrystalElement.WHITE, 1);
		this.addElement("gemQuartz", CrystalElement.BROWN, 4);
		this.addElement("gemQuartz", CrystalElement.YELLOW, 2);
		this.addElement("ingotIron", CrystalElement.BROWN, 4);
		this.addElement("ingotGold", CrystalElement.BROWN, 4);
		this.addElement("ingotGold", CrystalElement.PURPLE, 2);
		this.addElement(Items.coal, CrystalElement.YELLOW, 4);
		this.addElement(Items.coal, CrystalElement.ORANGE, 4);
		this.addElement("dustRedstone", CrystalElement.YELLOW, 2);
		this.addElement("dustRedstone", CrystalElement.LIME, 2);
		this.addElement("dustGlowstone", CrystalElement.BLUE, 4);
		this.addElement(ModOreList.CERTUSQUARTZ.getProductOreDictName(), CrystalElement.BROWN, 3);
		this.addElement(ModOreList.CERTUSQUARTZ.getProductOreDictName(), CrystalElement.WHITE, 2);
		this.addElement(ModOreList.APATITE.getProductOreDictName(), CrystalElement.BROWN, 2);
		this.addElement(ModOreList.APATITE.getProductOreDictName(), CrystalElement.WHITE, 1);
		this.addElement(ModOreList.SULFUR.getProductOreDictName(), CrystalElement.BROWN, 2);
		this.addElement(ModOreList.SULFUR.getProductOreDictName(), CrystalElement.ORANGE, 2);

		this.addElement("ingotCopper", CrystalElement.BROWN, 2);
		this.addElement("ingotTin", CrystalElement.BROWN, 2);
		this.addElement("ingotSilver", CrystalElement.BROWN, 3);
		this.addElement("ingotLead", CrystalElement.BROWN, 3);
		this.addElement("ingotNickel", CrystalElement.BROWN, 2);
		this.addElement("ingotAluminum", CrystalElement.BROWN, 2);
		this.addElement("ingotUranium", CrystalElement.BROWN, 4);
		this.addElement("ingotIridium", CrystalElement.BROWN, 8);
		this.addElement("ingotIridium", CrystalElement.WHITE, 2);
		this.addElement("ingotIridium", CrystalElement.PURPLE, 8);
		this.addElement("ingotPlatinum", CrystalElement.BROWN, 6);
		this.addElement("ingotPlatinum", CrystalElement.WHITE, 1);
		this.addElement("ingotPlatinum", CrystalElement.PURPLE, 6);

		this.addElement(Blocks.sand, CrystalElement.BROWN, 1);
		this.addElement(Blocks.grass, CrystalElement.GREEN, 1);
		this.addElement(Blocks.grass, CrystalElement.BROWN, 1);
		this.addElement(Items.clay_ball, CrystalElement.BROWN, 1);
		this.addElement(Items.clay_ball, CrystalElement.CYAN, 1);
		this.addElement(Blocks.end_stone, CrystalElement.BLACK, 1);
		this.addElement(Blocks.end_stone, CrystalElement.BROWN, 1);
		this.addElement(Blocks.gravel, CrystalElement.BROWN, 1);
		this.addElement(Blocks.mycelium, CrystalElement.BROWN, 1);
		this.addElement(Blocks.mycelium, CrystalElement.GREEN, 1);
		this.addElement(Blocks.soul_sand, CrystalElement.BROWN, 2);
		this.addElement(Blocks.soul_sand, CrystalElement.ORANGE, 1);
		this.addElement(Blocks.netherrack, CrystalElement.BROWN, 2);
		this.addElement(Blocks.netherrack, CrystalElement.ORANGE, 2);
		this.addElement(Blocks.mossy_cobblestone, CrystalElement.BROWN, 2);
		this.addElement(Blocks.mossy_cobblestone, CrystalElement.GREEN, 2);
		this.addElement(Blocks.obsidian, CrystalElement.ORANGE, 2);
		this.addElement(Blocks.obsidian, CrystalElement.BROWN, 5);
		this.addElement(Blocks.obsidian, CrystalElement.RED, 5);
		this.addElement(Blocks.sandstone, CrystalElement.BROWN, 2);

		this.addElement(Blocks.tallgrass, CrystalElement.GREEN, 2);
		this.addElement(Blocks.waterlily, CrystalElement.GREEN, 3);
		this.addElement(Blocks.waterlily, CrystalElement.CYAN, 3);
		this.addElement(Blocks.vine, CrystalElement.GREEN, 3);
		this.addElement(Items.wheat_seeds, CrystalElement.GREEN, 1);
		this.addElement(Items.melon_seeds, CrystalElement.GREEN, 1);
		this.addElement(Items.pumpkin_seeds, CrystalElement.GREEN, 1);
		this.addElement(Items.nether_wart, CrystalElement.GREEN, 1);
		this.addElement(Items.nether_wart, CrystalElement.ORANGE, 1);
		this.addElement(Items.nether_wart, CrystalElement.BLACK, 1);
		this.addElement(Items.reeds, CrystalElement.GREEN, 2);
		this.addElement(Items.reeds, CrystalElement.CYAN, 2);

		this.addElement(Blocks.red_flower, CrystalElement.GREEN, 1);
		this.addElement(Blocks.yellow_flower, CrystalElement.GREEN, 1);
		this.addElement(Blocks.cactus, CrystalElement.GREEN, 2);
		this.addElement(Blocks.cactus, CrystalElement.CYAN, 1);
		this.addElement(Blocks.brown_mushroom, CrystalElement.GREEN, 1);
		this.addElement(Blocks.red_mushroom, CrystalElement.GREEN, 1);

		this.addElement(Items.apple, CrystalElement.GREEN, 1);
		this.addElement(Items.carrot, CrystalElement.GREEN, 1);
		this.addElement(Items.carrot, CrystalElement.BROWN, 1);
		this.addElement(Items.potato, CrystalElement.GREEN, 1);
		this.addElement(Items.potato, CrystalElement.BROWN, 1);

		//this.addElement(FluidRegistry.WATER, CrystalElement.CYAN, 5);
		//this.addElement(FluidRegistry.LAVA, CrystalElement.ORANGE, 5);

		this.addElement(Blocks.ice, CrystalElement.CYAN, 2);
		this.addElement(Blocks.ice, CrystalElement.GRAY, 1);
		this.addElement(Blocks.packed_ice, CrystalElement.CYAN, 2);
		this.addElement(Blocks.packed_ice, CrystalElement.GRAY, 1);
		this.addElement(Items.snowball, CrystalElement.CYAN, 1);
		this.addElement(Blocks.web, CrystalElement.LIGHTGRAY, 1);
		this.addElement(Blocks.web, CrystalElement.LIME, 1);
		this.addElement(Items.flint, CrystalElement.BROWN, 1);
		this.addElement(Items.string, CrystalElement.GREEN, 1);
		this.addElement(Items.magma_cream, CrystalElement.ORANGE, 4);
		this.addElement(Items.magma_cream, CrystalElement.RED, 4);
		this.addElement(Items.magma_cream, CrystalElement.GREEN, 1);
		this.addElement(Items.slime_ball, CrystalElement.CYAN, 2);
		this.addElement(Items.slime_ball, CrystalElement.PINK, 2);
		this.addElement(Items.slime_ball, CrystalElement.GREEN, 1);
		this.addElement(Items.leather, CrystalElement.GREEN, 1);
		this.addElement(Items.rotten_flesh, CrystalElement.GREEN, 1);
		this.addElement(Items.feather, CrystalElement.GREEN, 1);
		this.addElement(Items.bone, CrystalElement.GREEN, 1);
		this.addElement(Items.egg, CrystalElement.GREEN, 1);
		this.addElement(Items.spider_eye, CrystalElement.GREEN, 1);
		this.addElement(Items.gunpowder, CrystalElement.GREEN, 1);
		this.addElement(Items.gunpowder, CrystalElement.ORANGE, 3);
		this.addElement(Blocks.wool, CrystalElement.GREEN, 1);
		this.addElement(Items.fish, CrystalElement.GREEN, 1);
		this.addElement(Items.fish, CrystalElement.CYAN, 1);
		this.addElement(Items.chicken, CrystalElement.GREEN, 1);
		this.addElement(Items.porkchop, CrystalElement.GREEN, 1);
		this.addElement(Items.beef, CrystalElement.GREEN, 1);
		this.addElement(Items.blaze_rod, CrystalElement.ORANGE, 4);
		this.addElement(Items.blaze_rod, CrystalElement.GREEN, 1);
		this.addElement(Items.blaze_rod, CrystalElement.PINK, 2);
		this.addElement(Items.saddle, CrystalElement.PURPLE, 1);
		this.addElement(Items.ender_pearl, CrystalElement.LIME, 4);
		this.addElement(Items.ender_pearl, CrystalElement.GREEN, 1);
		this.addElement(Items.ender_pearl, CrystalElement.BLACK, 4);
		this.addElement(Items.ghast_tear, CrystalElement.BLACK, 1);
		this.addElement(Items.ghast_tear, CrystalElement.GREEN, 1);
		this.addElement(Items.skull, CrystalElement.PINK, 4);
		/*
		this.addElement(Items.record_11, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_13, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.WATER, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_cat, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.BEAST, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_chirp, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.EARTH, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_far, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.ELDRITCH, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_mall, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.MAN, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_mellohi, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.CRAFT, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_stal, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.DARKNESS, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_strad, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.MECHANISM, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_ward, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.MAGIC, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_blocks, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.TOOL, 4).add(Aspect.GREED, 4));
		this.addElement(Items.record_wait, new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.TRAP, 4).add(Aspect.GREED, 4));
		 */
		this.addElement(Items.nether_star, CrystalElement.PINK, 8);
		this.addElement(Items.nether_star, CrystalElement.BLACK, 4);

		//this.addElement(Blocks.dragon_egg, new AspectList().add(Aspect.ELDRITCH, 8).add(Aspect.BEAST, 8).add(Aspect.MAGIC, 8));
		//this.addElement(Blocks.end_portal_frame, new AspectList().add(Aspect.ELDRITCH, 4).add(Aspect.MECHANISM, 4).add(Aspect.TRAVEL, 4));
		//this.addElement(Blocks.mob_spawner, new AspectList().add(Aspect.BEAST, 4).add(Aspect.TRAVEL, 4).add(Aspect.UNDEAD, 4).add(Aspect.MAGIC, 4));

		this.addElement(Items.golden_apple, CrystalElement.MAGENTA, 8);
		this.addElement(Items.golden_apple, CrystalElement.BROWN, 2);

		//this.addElement(Blocks.piston, new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 4));

		//this.addElement(Blocks.beacon, new AspectList().add(Aspect.AURA, 2).add(Aspect.MAGIC, 2).add(Aspect.EXCHANGE, 2));

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack shard2 = ChromaItems.SHARD.getStackOfMetadata(i+16);
			ItemStack berry = ChromaItems.BERRY.getStackOfMetadata(i);
			ItemStack crystal = new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i);
			ItemStack seed = ChromaItems.SEED.getStackOfMetadata(i);
			ItemStack dye = ChromaItems.DYE.getStackOfMetadata(i);
			ItemStack vdye = ChromaItems.DYE.getStackOfMetadata(i);
			this.addElement(seed, e, 1);
			this.addElement(crystal, e, 10);
			this.addElement(berry, e, 1);
			this.addElement(shard, e, 4);
			this.addElement(shard2, e, 6);
			this.addElement(vdye, e, 1);
			this.addElement(dye, e, 1);
		}
	}

	private void addElement(String s, CrystalElement e, int amt) {
		Collection<ItemStack> li = OreDictionary.getOres(s);
		for (ItemStack is : li) {
			this.addElement(is, e, amt);
		}
	}

	private void addElement(Block b, CrystalElement e, int amt) {
		this.addElement(new ItemStack(b), e, amt);
	}

	private void addElement(Item b, CrystalElement e, int amt) {
		this.addElement(new ItemStack(b), e, amt);
	}

	private void addElement(ItemStack is, CrystalElement e, int amt) {
		KeyedItemStack ks = new KeyedItemStack(is).setSimpleHash(true).lock();//.setSized(true);
		ElementTagCompound tag = data.get(ks);
		if (tag == null) {
			tag = new ElementTagCompound();
			data.put(ks, tag);
		}
		tag.addTag(e, amt);
	}

	public ElementTagCompound getItemValue(ItemStack is) {
		return this.getItemValue(new KeyedItemStack(is).setSimpleHash(true));
	}

	public ElementTagCompound getItemValue(KeyedItemStack is) {
		ElementTagCompound tag = data.get(is);
		return tag != null ? tag.copy() : null;
	}

	public Collection<ItemStack> getAllRegisteredItems() {
		ArrayList<ItemStack> li = new ArrayList();
		for (KeyedItemStack item : data.keySet()) {
			li.add(item.getItemStack());
		}
		return li;
	}

	public Collection<KeyedItemStack> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

}
