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

import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCaveCrystal extends CrystalBlock {

	private static final Random rand = new Random();

	public BlockCaveCrystal(Material mat) {
		super(mat);
		this.setLightLevel(0.65F);
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return ChromaItems.SHARD.getItemInstance();
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1+r.nextInt(6)+r.nextInt(3);
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer ep, int x, int y, int z, int meta)
	{
		return true;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		int num = this.getNumberDrops(meta, fortune);
		for (int i = 0; i < num; i++)
			li.add(ChromaItems.SHARD.getStackOfMetadata(meta));
		return li;
	}

	private int getNumberDrops(int meta, int fortune) {
		return 1+rand.nextInt(6+fortune)+(1+fortune)*rand.nextInt(3)+rand.nextInt(1+fortune);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			icons[i] = ico.registerIcon("ChromatiCraft:crystal/crystal_outline");
		}
	}

	@Override
	public boolean shouldMakeNoise() {
		return true;
	}

	@Override
	public boolean shouldGiveEffects() {
		return true;
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
		return false;
	}

	@Override
	public Block getBaseBlock(ForgeDirection side) {
		return Blocks.cobblestone;
	}

	@Override
	public int getPotionLevel() {
		return 0;
	}
}