/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;

import com.google.common.collect.HashBiMap;

/** Specifies the magic elements present in each "fundamental" Items. */
public class ItemMagicRegistry {

	public static final ItemMagicRegistry instance = new ItemMagicRegistry();

	private final HashBiMap<KeyedItemStack, ElementTagCompound> data = HashBiMap.create();

	private ItemMagicRegistry() {
		this.addElement(Blocks.stone, CrystalElement.RED, 10);

		this.addElement(Items.coal, CrystalElement.ORANGE, 100);
		this.addElement(Items.coal, CrystalElement.YELLOW, 100);

		this.addElement(Items.iron_ingot, CrystalElement.BROWN, 200);
		this.addElement(Items.iron_ingot, CrystalElement.ORANGE, 50);

		this.addElement(Items.magma_cream, CrystalElement.ORANGE, 200);
		this.addElement(Items.magma_cream, CrystalElement.RED, 200);

		this.addElement(Items.slime_ball, CrystalElement.CYAN, 200);
		this.addElement(Items.slime_ball, CrystalElement.GREEN, 400);

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack shard2 = ChromaItems.SHARD.getStackOfMetadata(i+16);
			ItemStack berry = ChromaItems.BERRY.getStackOfMetadata(i);
			ItemStack crystal = new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i);
			ItemStack seed = ChromaItems.SEED.getStackOfMetadata(i);
			this.addElement(seed, e, 5);
			this.addElement(crystal, e, 1000);
			this.addElement(berry, e, 10);
			this.addElement(shard, e, 100);
			this.addElement(shard2, e, 1000);
		}
	}

	private void addElement(Block b, CrystalElement e, int amt) {
		this.addElement(new ItemStack(b), e, amt);
	}

	private void addElement(Item b, CrystalElement e, int amt) {
		this.addElement(new ItemStack(b), e, amt);
	}

	private void addElement(ItemStack is, CrystalElement e, int amt) {
		KeyedItemStack ks = new KeyedItemStack(is).setSized();
		ElementTagCompound tag = data.get(ks);
		if (tag == null) {
			tag = new ElementTagCompound();
			data.put(ks, tag);
		}
		tag.addTag(e, amt);
	}

	public ElementTagCompound getItemValue(ItemStack is) {
		KeyedItemStack ks = new KeyedItemStack(is);
		ElementTagCompound tag = data.get(ks);
		return tag != null ? tag.copy() : null;
	}

	public Collection<ItemStack> getAllRegisteredItems() {
		ArrayList<ItemStack> li = new ArrayList();
		for (KeyedItemStack item : data.keySet()) {
			li.add(item.getItemStack());
		}
		return li;
	}

	public Map<KeyedItemStack, ElementTagCompound> getMap() {
		return Collections.unmodifiableMap(data);
	}

}
