/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CrystalRenderedBlock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRainbowCrystal extends BlockCrystalTileNonCube implements CrystalRenderedBlock {

	public BlockRainbowCrystal(Material mat) {
		super(mat);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public final IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	public final void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:crystal/chroma");
	}

	@Override
	public final int getRenderType() {
		return ChromatiCraft.proxy.crystalRender; //6 was crops
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass)
	{
		CrystalRenderer.renderPass = pass;
		return pass <= 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		double[] v = ReikaDyeHelper.getRandomColor().getRedstoneParticleVelocityForColor();
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, v[0], v[1], v[2], 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		Random rand = new Random();
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		ReikaDyeHelper dye = ReikaDyeHelper.getRandomColor();
		double[] v = dye.getRedstoneParticleVelocityForColor();
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, v[0], v[1], v[2], 4);
		return false;
	}

	public boolean renderAllArms() {
		return true;
	}

	public boolean renderBase() {
		return true;
	}

	public Block getBaseBlock(ForgeDirection side) {
		return ChromaBlocks.PYLONSTRUCT.getBlockInstance();
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity e) {
		return false;
	}

	public final int getTintColor(int meta) {
		return 0xffffff;
	}

}
