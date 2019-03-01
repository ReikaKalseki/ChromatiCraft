/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Ability;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.IIcon;

import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;

public enum AbilityXRays {
	SPAWNERS(TileEntityMobSpawner.class, Blocks.mob_spawner, 0x224466),
	CHESTS(TileEntityChest.class, 0xC17C32),
	LOOTCHESTS(TileEntityLootChest.class, 0x303030),
	CHESTCARTS(EntityMinecartChest.class, 0xC17C32);

	public final Class objectClass;
	private final Block texture;
	public final int highlightColor;

	private AbilityXRays(Class t, int c) {
		this(t, null, c);
	}

	public IIcon getTexture() {
		return texture != null ? texture.blockIcon : null;
	}

	private AbilityXRays(Class t, Block tex, int c) {
		texture = tex;
		objectClass = t;
		highlightColor = c;
	}
}
