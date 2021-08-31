/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dye;

import java.awt.Color;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

public class BlockDye extends Block {

	public BlockDye(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
		this.setStepSound(soundTypeStone);
		this.setHardness(1F);
	}

	@Override
	public int getRenderColor(int dmg) {
		return ReikaDyeHelper.dyes[dmg].getColor();
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		int dmg = iba.getBlockMetadata(x, y, z);
		return ReikaDyeHelper.dyes[dmg].getJavaColor().brighter().getRGB();
	}

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return ChromaOptions.isVanillaDyeMoreCommon(meta) ? Items.dye : ChromaItems.DYE.getItemInstance();
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 9;
	}

	@Override
	public int damageDropped(int dmg) {
		return dmg;
	}

	@Override
	public IIcon getIcon(int par1, int par2) {
		return blockIcon;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dye/block");
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (!ChromaOptions.BLOCKPARTICLES.getState())
			return;
		if (rand.nextInt(4) > 0)
			return;
		double offset = 0.125;
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		Color color = dye.getJavaColor();
		double r = color.getRed()/255D;
		double g = color.getGreen()/255D;
		double b = dye.getBlue()/255D;
		ReikaParticleHelper.spawnColoredParticlesWithOutset(world, x, y, z, r, g, b, 1, offset);
	}

}
