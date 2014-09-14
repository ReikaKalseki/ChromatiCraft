/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class ChromaStacks {

	public static final ItemStack redGroup = ChromaItems.CLUSTER.getStackOfMetadata(0);
	public static final ItemStack greenGroup = ChromaItems.CLUSTER.getStackOfMetadata(1);
	public static final ItemStack orangeGroup = ChromaItems.CLUSTER.getStackOfMetadata(2);
	public static final ItemStack whiteGroup = ChromaItems.CLUSTER.getStackOfMetadata(3);
	public static final ItemStack primaryBunch = ChromaItems.CLUSTER.getStackOfMetadata(4);
	public static final ItemStack secondaryBunch = ChromaItems.CLUSTER.getStackOfMetadata(5);
	public static final ItemStack tertiaryBunch = ChromaItems.CLUSTER.getStackOfMetadata(6);
	public static final ItemStack quaternaryBunch = ChromaItems.CLUSTER.getStackOfMetadata(7);
	public static final ItemStack primaryCluster = ChromaItems.CLUSTER.getStackOfMetadata(8);
	public static final ItemStack secondaryCluster = ChromaItems.CLUSTER.getStackOfMetadata(9);
	public static final ItemStack crystalCore = ChromaItems.CLUSTER.getStackOfMetadata(10);
	public static final ItemStack crystalStar = ChromaItems.CLUSTER.getStackOfMetadata(11);
	public static final ItemStack multiShard = ChromaItems.CLUSTER.getStackOfMetadata(12);

	public static final ItemStack voidCore = ChromaItems.CRAFTING.getStackOfMetadata(0);
	public static final ItemStack crystalLens = ChromaItems.CRAFTING.getStackOfMetadata(1);
	public static final ItemStack crystalFocus = ChromaItems.CRAFTING.getStackOfMetadata(2);
	public static final ItemStack crystalMirror = ChromaItems.CRAFTING.getStackOfMetadata(3);
	public static final ItemStack rawCrystal = ChromaItems.CRAFTING.getStackOfMetadata(4);
	public static final ItemStack energyCore = ChromaItems.CRAFTING.getStackOfMetadata(5);
	public static final ItemStack crystalPowder = ChromaItems.CRAFTING.getStackOfMetadata(6);

	public static final ItemStack chargedBlackShard = ChromaItems.SHARD.getStackOfMetadata(16);
	public static final ItemStack chargedRedShard = ChromaItems.SHARD.getStackOfMetadata(17);
	public static final ItemStack chargedGreenShard = ChromaItems.SHARD.getStackOfMetadata(18);
	public static final ItemStack chargedBrownShard = ChromaItems.SHARD.getStackOfMetadata(19);
	public static final ItemStack chargedBlueShard = ChromaItems.SHARD.getStackOfMetadata(20);
	public static final ItemStack chargedPurpleShard = ChromaItems.SHARD.getStackOfMetadata(21);
	public static final ItemStack chargedCyanShard = ChromaItems.SHARD.getStackOfMetadata(22);
	public static final ItemStack chargedLightGrayShard = ChromaItems.SHARD.getStackOfMetadata(23);
	public static final ItemStack chargedGrayShard = ChromaItems.SHARD.getStackOfMetadata(24);
	public static final ItemStack chargedPinkShard = ChromaItems.SHARD.getStackOfMetadata(25);
	public static final ItemStack chargedLimeShard = ChromaItems.SHARD.getStackOfMetadata(26);
	public static final ItemStack chargedYellowShard = ChromaItems.SHARD.getStackOfMetadata(27);
	public static final ItemStack chargedLightBlueShard = ChromaItems.SHARD.getStackOfMetadata(28);
	public static final ItemStack chargedMagentaShard = ChromaItems.SHARD.getStackOfMetadata(29);
	public static final ItemStack chargedOrangeShard = ChromaItems.SHARD.getStackOfMetadata(30);
	public static final ItemStack chargedWhiteShard = ChromaItems.SHARD.getStackOfMetadata(31);

	public static ItemStack getShard(CrystalElement e) {
		return ChromaItems.SHARD.getStackOfMetadata(e.ordinal());
	}

	public static ItemStack getChargedShard(CrystalElement e) {
		return ChromaItems.SHARD.getStackOfMetadata(16+e.ordinal());
	}

	private static final ItemStack[] shards = {
		ChromaItems.SHARD.getStackOfMetadata(0),
		ChromaItems.SHARD.getStackOfMetadata(1),
		ChromaItems.SHARD.getStackOfMetadata(2),
		ChromaItems.SHARD.getStackOfMetadata(3),
		ChromaItems.SHARD.getStackOfMetadata(4),
		ChromaItems.SHARD.getStackOfMetadata(5),
		ChromaItems.SHARD.getStackOfMetadata(6),
		ChromaItems.SHARD.getStackOfMetadata(7),
		ChromaItems.SHARD.getStackOfMetadata(8),
		ChromaItems.SHARD.getStackOfMetadata(9),
		ChromaItems.SHARD.getStackOfMetadata(10),
		ChromaItems.SHARD.getStackOfMetadata(11),
		ChromaItems.SHARD.getStackOfMetadata(12),
		ChromaItems.SHARD.getStackOfMetadata(13),
		ChromaItems.SHARD.getStackOfMetadata(14),
		ChromaItems.SHARD.getStackOfMetadata(15),
	};

}
