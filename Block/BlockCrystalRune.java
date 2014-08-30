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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockDyeTypes;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalRepeater;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrystalRune extends BlockDyeTypes {

	public BlockCrystalRune(Material par2Material) {
		super(par2Material);
		blockHardness = 2;
		blockResistance = 5;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		if (world.isRemote)
			ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 256);
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block id, int meta) {
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		if (world.isRemote)
			ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 256);
		if (world.getBlock(x, y-1, z) == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
			((BlockPylonStructure)ChromaBlocks.PYLONSTRUCT.getBlockInstance()).triggerBreakCheck(world, x, y-1, z);
		}
		if (ChromaTiles.getTile(world, x, y+1, z) == ChromaTiles.REPEATER) {
			CrystalNetworker.instance.breakPaths((TileEntityCrystalRepeater)world.getTileEntity(x, y+1, z));
		}
		super.breakBlock(world, x, y, z, id, meta);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		super.randomDisplayTick(world, x, y, z, r);
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 8);
		//this.runeParticles(world, x, y, z, meta, r);
	}

	@SideOnly(Side.CLIENT)
	private void runeParticles(World world, int x, int y, int z, int meta, Random rand) {
		double r = 0.75;
		double dx = ReikaRandomHelper.getRandomPlusMinus(0, r);
		double dy = rand.nextDouble();
		double dz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		while (ReikaMathLibrary.py3d(dx, 0, dz) < 0.65) {
			dx = ReikaRandomHelper.getRandomPlusMinus(0, r);
			dz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		}

		CrystalElement e = CrystalElement.elements[meta];
		Minecraft.getMinecraft().effectRenderer.addEffect(new EntityRuneFX(world, x+dx+0.5, y+dy+0.5, z+dz+0.5, e));
	}

	@Override
	public String getIconFolder() {
		return "runes/backpng/";
	}

	@Override
	public boolean useNamedIcons() {
		return false;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.runeRender;
	}

}
