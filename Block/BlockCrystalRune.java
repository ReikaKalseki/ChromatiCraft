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

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.BlockDyeTypes;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

public class BlockCrystalRune extends BlockDyeTypes {

	public BlockCrystalRune(int par1, Material par2Material) {
		super(par1, par2Material);
		blockHardness = 2;
		blockResistance = 5;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 256);
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 256);
		super.breakBlock(world, x, y, z, id, meta);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		super.randomDisplayTick(world, x, y, z, r);
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 8);
	}

	@Override
	public String getIconFolder() {
		return "runes/real/";
	}

	@Override
	public boolean useNamedIcons() {
		return false;
	}

}
