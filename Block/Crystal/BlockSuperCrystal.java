/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Crystal;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class BlockSuperCrystal extends CrystalBlock {

	public BlockSuperCrystal(Material mat) {
		super(mat);
		this.setLightLevel(1F);
		this.setResistance(6000);
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return Item.getItemFromBlock(this);
	}

	/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			icons[i] = ico.registerIcon("ChromatiCraft:crystal/lamp_"+ReikaDyeHelper.dyes[i].name().toLowerCase());
		}
	}
	 */

	@Override
	public boolean shouldMakeNoise() {
		return ChromaOptions.NOISE.getState();
	}

	@Override
	public boolean shouldGiveEffects(CrystalElement e) {
		return true;
	}

	@Override
	public int getRange() {
		return 12;
	}

	@Override
	public int getDuration(CrystalElement e) {
		return 6000;
	}

	@Override
	public boolean renderBase() {
		return true;
	}

	@Override
	public Block getBaseBlock(ForgeDirection side) {
		return Blocks.obsidian;
	}

	@Override
	public int getPotionLevel(CrystalElement e) {
		return 2;
	}

	@Override
	public int getBrightness(IBlockAccess iba, int x, int y, int z) {
		return 15;
	}

	@Override
	public boolean performEffect(CrystalElement e) {
		return true;
	}

}
