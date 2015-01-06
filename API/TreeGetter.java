/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/** Use this to fetch and/or compare against ChromatiCraft foliage blocks. */
public class TreeGetter {

	private static Class dyeTreeBlocks;
	private static Block[] dyeBlocks;

	public static final int LEAF_INDEX = 15;
	public static final int SAPLING_INDEX = 16;
	public static final int NATURAL_LEAF_INDEX = 14;
	public static final int RAINBOW_LEAF_INDEX = 18;
	public static final int RAINBOW_SAPLING_INDEX = 19;
	public static final int FLOWER_INDEX = 20;

	public static boolean isDyeLeaf(ItemStack block) {
		return Block.getBlockFromItem(block.getItem()) == dyeBlocks[LEAF_INDEX] || Block.getBlockFromItem(block.getItem()) == dyeBlocks[NATURAL_LEAF_INDEX];
	}

	public static boolean isDyeSapling(ItemStack block) {
		return Block.getBlockFromItem(block.getItem()) == dyeBlocks[SAPLING_INDEX];
	}

	public static boolean isRainbowLeaf(ItemStack block) {
		return Block.getBlockFromItem(block.getItem()) == dyeBlocks[RAINBOW_LEAF_INDEX];
	}

	public static boolean isRainbowSapling(ItemStack block) {
		return Block.getBlockFromItem(block.getItem()) == dyeBlocks[RAINBOW_SAPLING_INDEX];
	}

	public static boolean isDyeFlower(ItemStack block) {
		return Block.getBlockFromItem(block.getItem()) == dyeBlocks[FLOWER_INDEX];
	}

	public static ItemStack getDyeSapling(int dyeMeta) {
		return new ItemStack(dyeBlocks[SAPLING_INDEX], 1, dyeMeta);
	}

	public static ItemStack getDyeFlower(int dyeMeta) {
		return new ItemStack(dyeBlocks[FLOWER_INDEX], 1, dyeMeta);
	}

	public static ItemStack getHeldDyeLeaf(int dyeMeta) {
		return new ItemStack(getHeldDyeLeafID(), 1, dyeMeta);
	}

	public static ItemStack getNaturalDyeLeaf(int dyeMeta) {
		return new ItemStack(getNaturalDyeLeafID(), 1, dyeMeta);
	}

	public static ItemStack getRainbowLeaf() {
		return new ItemStack(getRainbowLeafID(), 1, 0);
	}

	public static ItemStack getRainbowSapling() {
		return new ItemStack(getRainbowSaplingID(), 1, 0);
	}

	public static Block getHeldDyeLeafID() {
		return dyeBlocks[LEAF_INDEX];
	}

	public static Block getNaturalDyeLeafID() {
		return dyeBlocks[NATURAL_LEAF_INDEX];
	}

	public static Block getRainbowLeafID() {
		return dyeBlocks[RAINBOW_LEAF_INDEX];
	}

	public static Block getSaplingID() {
		return dyeBlocks[SAPLING_INDEX];
	}

	public static Block getRainbowSaplingID() {
		return dyeBlocks[RAINBOW_SAPLING_INDEX];
	}

	public static Block getDyeFlowerID() {
		return dyeBlocks[FLOWER_INDEX];
	}

	static {
		try {
			dyeTreeBlocks = Class.forName("Reika.ChromatiCraft.ChromatiCraft", false, TreeGetter.class.getClassLoader());
			dyeBlocks = (Block[])dyeTreeBlocks.getField("blocks").get(null);
		}
		catch (ClassNotFoundException e) {
			System.out.println("ChromatiCraft class not found!");
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			System.out.println("ChromatiCraft class not read correctly!");
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			System.out.println("ChromatiCraft class not read correctly!");
			e.printStackTrace();
		}
		catch (NoSuchFieldException e) {
			System.out.println("ChromatiCraft class not read correctly!");
			e.printStackTrace();
		}
		catch (SecurityException e) {
			System.out.println("ChromatiCraft class not read correctly!");
			e.printStackTrace();
		}
	}

}
