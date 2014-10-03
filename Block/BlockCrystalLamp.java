/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Registry.ChromaOptions;

public class BlockCrystalLamp extends CrystalBlock {

	public BlockCrystalLamp(Material mat) {
		super(mat);
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1;
	}
	/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			icons[i] = ico.registerIcon("ChromatiCraft:crystal/lamp_"+ReikaDyeHelper.dyes[i].name().toLowerCase());
		}
	}*/

	@Override
	public boolean shouldMakeNoise() {
		return ChromaOptions.NOISE.getState();
	}

	@Override
	public boolean shouldGiveEffects() {
		return ChromaOptions.EFFECTS.getState();
	}

	@Override
	public int getRange() {
		return 3;
	}

	@Override
	public int getDuration() {
		return 200;
	}

	@Override
	public boolean renderBase() {
		return true;
	}

	@Override
	public Block getBaseBlock(ForgeDirection side) {
		return side.offsetY == 0 ? Blocks.stone : Blocks.double_stone_slab;
	}

	@Override
	public int getPotionLevel() {
		return 0;
	}

	@Override
	public int getBrightness() {
		return 15;
	}
}
