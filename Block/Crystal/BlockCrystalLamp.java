/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Crystal;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;

public class BlockCrystalLamp extends CrystalBlock {

	public BlockCrystalLamp(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1;
	}

	@Override
	public boolean shouldMakeNoise() {
		return ChromaOptions.NOISE.getState();
	}

	@Override
	public boolean shouldGiveEffects(CrystalElement e) {
		return false;
	}

	@Override
	public int getRange() {
		return 3;
	}

	@Override
	public int getDuration(CrystalElement e) {
		return 200;
	}

	@Override
	public boolean renderBase() {
		return true;
	}

	@Override
	public BlockKey getBaseBlock(IBlockAccess iba, int x, int y, int z, ForgeDirection side) {
		if (this.unMineable(iba, x, y, z))
			return new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 1);
		return side.offsetY == 0 ? new BlockKey(Blocks.stone, 0) : new BlockKey(Blocks.double_stone_slab, 0);
	}

	@Override
	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		return this.unMineable(world, x, y, z);
	}

	private boolean unMineable(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y-1, z) == ChromaBlocks.MUSICTRIGGER.getBlockInstance())
			return true;
		return world.getBlock(x, y-1, z) instanceof BlockStructureShield && world.getBlockMetadata(x, y-1, z) >= 8;
	}

	@Override
	public int getPotionLevel(CrystalElement e) {
		return 0;
	}

	@Override
	public int getBrightness(IBlockAccess iba, int x, int y, int z) {
		return 15;
	}

	@Override
	public boolean performEffect(CrystalElement e) {
		return false;
	}
}
