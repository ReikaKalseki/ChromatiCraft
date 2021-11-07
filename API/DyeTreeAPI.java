/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;

/** Use this to fetch and/or compare against ChromatiCraft foliage blocks. */
public interface DyeTreeAPI {

	public boolean isCCLeaf(Block b);

	public ItemStack getDyeSapling(CrystalElementProxy e);

	public ItemStack getDyeFlower(CrystalElementProxy e);

	public ItemStack getDyeLeaf(CrystalElementProxy e, boolean natural);

	public Block getDyeSapling();

	public Block getDyeFlower();

	public Block getDyeLeaf(boolean natural);

	public Block getRainbowLeaf();

	public Block getRainbowSapling();

	public Block getDecoFlower();

}
