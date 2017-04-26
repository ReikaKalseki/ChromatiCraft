/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Decoration;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Instantiable.CubeRotation;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockColoredAltar extends CrystalTypeBlock {

	private static final Random rand = new Random();

	public BlockColoredAltar(Material mat) {
		super(mat);

		this.setHardness(2);
		this.setResistance(600000);

		this.setBlockBounds(0, 0, 0, 1, 0.5F, 1);

		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
	}

	public static class TileEntityColoredAltar extends TileEntity {

		private int ticks;
		private int tickOffset;

		public CrystalElement renderColor = CrystalElement.WHITE;

		public static final int NUMBER_CUBES = 12;

		public final CubeRotation[] cubeRotations = new CubeRotation[NUMBER_CUBES];

		public TileEntityColoredAltar() {
			this.randomize();
		}

		private void randomize() {
			tickOffset = rand.nextInt(360);

			for (int i = 0; i < cubeRotations.length; i++) {
				cubeRotations[i] = new CubeRotation().randomize(rand);
			}
		}

		public CrystalElement getColor() {
			return worldObj != null ? CrystalElement.elements[this.getBlockMetadata()] : renderColor;
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			ticks++;

			if (worldObj.isRemote) {
				this.renderParticles(this.getColor());
			}
		}

		//Have particles (blur) spill over the rim, use some more to make a pulsing glow effect in the middle, and then make some laser ones lazily float up
		@SideOnly(Side.CLIENT)
		private void renderParticles(CrystalElement e) {
			int ps = Minecraft.getMinecraft().gameSettings.particleSetting;
			double n = 2;
			switch(ps) {
				case 1:
					n = 1;
					break;
				case 2:
					n = 0.5;
					break;
			}
			double d = Math.max(1, Minecraft.getMinecraft().thePlayer.getDistanceSq(xCoord+0.5, yCoord+0.25, zCoord+0.5)/256D);
			n /= d;

			int num = n <= 1 ? ReikaRandomHelper.doWithChance(n) ? 1 : 0 : (int)n;

			for (int i = 0; i < num; i++) {
				double r = ReikaRandomHelper.getRandomPlusMinus(0.55, 0.05);
				double ang = rand.nextDouble()*360;;//(ticks*8+i*360/n)%360
				double dx = xCoord+0.5+r*Math.sin(Math.toRadians(ang));
				double dy = yCoord+0.5;
				double dz = zCoord+0.5+r*Math.cos(Math.toRadians(ang));
				float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
				int l = 20+rand.nextInt(20);
				float s = (float)ReikaRandomHelper.getRandomPlusMinus(2F, 1F);
				double dang = ReikaRandomHelper.getRandomPlusMinus(ang, 10);
				EntityFX fx = new EntityBlurFX(e, worldObj, dx, dy, dz, 0, 0, 0).setGravity(g).setLife(l).setScale(s).setColliding(dang);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}

			if (rand.nextInt(2) == 0) {
				double dr = 0.25;
				double dx = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, dr);
				double dz = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, dr);
				double dy = yCoord+0.375;
				int l = 10+rand.nextInt(60);
				float s = (float)ReikaRandomHelper.getRandomPlusMinus(1.5F, 1F);

				int r = e.getRed();
				int g = e.getGreen();
				int b = e.getBlue();

				r = (int)(r*ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25));
				g = (int)(g*ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25));
				b = (int)(b*ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25));

				if (e == CrystalElement.WHITE || e == CrystalElement.BLACK || e == CrystalElement.GRAY || e == CrystalElement.LIGHTGRAY) {
					int avg = (r+g+b)/3;
					r = g = b = avg;
				}

				EntityFX fx = new EntityBlurFX(worldObj, dx, dy, dz, 0, 0, 0).setGravity(0).setLife(l).setScale(s).setColor(r, g, b);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}

			if (rand.nextInt(8) == 0) {
				double dr = 0.375;
				double dx = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, dr);
				double dz = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, dr);
				double dy = yCoord+0.55;
				int l = 40+rand.nextInt(40);
				float s = (float)ReikaRandomHelper.getRandomPlusMinus(1.5F, 1F);
				float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
				EntityFX fx = new EntityLaserFX(e, worldObj, dx, dy, dz, 0, 0, 0).setGravity(g).setScale(s);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass <= 1;
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return AxisAlignedBB.getBoundingBox(xCoord-0.5, yCoord, zCoord-0.5, xCoord+1.5, yCoord+3, zCoord+1.5);
		}

		public int getTicksExisted() {
			return ticks;
		}

		public int getRenderTick() {
			return this.getTicksExisted()+tickOffset;
		}

	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityColoredAltar();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public int getBrightness(IBlockAccess iba, int x, int y, int z) {
		return 15;
	}
}
