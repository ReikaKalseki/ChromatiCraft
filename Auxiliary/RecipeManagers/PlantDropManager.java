/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Base.BlockChromaTiered;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Instantiable.Data.Maps.ReversibleMultiMap;


public class PlantDropManager {

	public static final PlantDropManager instance = new PlantDropManager();

	private final ReversibleMultiMap<BlockKey, KeyedItemStack> data = new ReversibleMultiMap(new HashSetFactory());

	private PlantDropManager() {

	}

	public void registerDrops(Block b, int meta, ItemStack is) {
		data.addValue(new BlockKey(b, meta), this.getKey(is));
	}

	public Collection<BlockKey> getPlantForDrops(ItemStack result, EntityPlayer ep) {
		Collection<BlockKey> c = data.getBackward(this.getKey(result));
		if (c != null && ep != null) {
			c = new ArrayList(c);
			Iterator<BlockKey> it = c.iterator();
			while (it.hasNext()) {
				BlockKey b = it.next();
				if (b.blockID instanceof BlockChromaTiered) {
					if (!((BlockChromaTiered)b.blockID).getProgressStage(b.metadata).isPlayerAtStage(ep)) {
						it.remove();
					}
				}
			}
		}
		return c != null ? Collections.unmodifiableCollection(c) : null;
	}

	private KeyedItemStack getKey(ItemStack result) {
		return new KeyedItemStack(result).setSimpleHash(true).lock();
	}

	public Collection<ItemStack> getDropsForPlant(Block b, int meta, EntityPlayer ep) {
		ArrayList<ItemStack> li = new ArrayList();
		if (ep != null) {
			if (b instanceof BlockChromaTiered) {
				if (!((BlockChromaTiered)b).getProgressStage(meta).isPlayerAtStage(ep)) {
					return li;
				}
			}
		}
		Collection<KeyedItemStack> c = data.getForward(new BlockKey(b, meta));
		if (c != null) {
			for (KeyedItemStack is : c) {
				li.add(is.getItemStack());
			}
		}
		return li;
	}

	public Collection<BlockKey> getAllBlocks() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public Collection<ItemStack> getAllDrops() {
		ArrayList<ItemStack> li = new ArrayList();
		for (KeyedItemStack is : data.values()) {
			li.add(is.getItemStack());
		}
		return li;
	}

}
