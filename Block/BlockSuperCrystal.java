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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSuperCrystal extends CrystalBlock {

	public BlockSuperCrystal(Material mat) {
		super(mat);
		this.setLightLevel(1F);
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			icons[i] = ico.registerIcon("ChromatiCraft:crystal/lamp_"+ReikaDyeHelper.dyes[i].name().toLowerCase());
		}
	}

	@Override
	public boolean shouldMakeNoise() {
		return ChromaOptions.NOISE.getState();
	}

	@Override
	public boolean shouldGiveEffects() {
		return true;
	}

	@Override
	public int getRange() {
		return 12;
	}

	@Override
	public int getDuration() {
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
	public int getPotionLevel() {
		return 2;
	}

}
