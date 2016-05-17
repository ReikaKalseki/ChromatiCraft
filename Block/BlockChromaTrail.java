/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockChromaTrail extends BlockContainer {

	public BlockChromaTrail(Material mat) {
		super(mat);

		this.setHardness(0);
		this.setResistance(60000);
		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileChromaTrail();
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return ChromaIcons.TRANSPARENT.getIcon();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return super.getItemDropped(meta, rand, fortune);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		float s = 0.125F;
		this.setBlockBounds(0.5F-s, 0.5F-s, 0.5F-s, 0.5F+s, 0.5F+s, 0.5F+s);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		TileChromaTrail te = (TileChromaTrail)world.getTileEntity(x, y, z);
		te.facingPhi = e.rotationYaw+180+90;
		te.facingTheta = Math.signum(e.rotationPitch)*Math.max(0, (Math.abs(e.rotationPitch)-45));
		world.markBlockForUpdate(x, y, z);
	}

	public static class TileChromaTrail extends TileEntity {

		private double facingPhi;
		private double facingTheta;

		@Override
		public void updateEntity() {
			if (worldObj.isRemote) {
				this.doParticles(worldObj, xCoord, yCoord, zCoord);
			}
		}

		@SideOnly(Side.CLIENT)
		private void doParticles(World world, int x, int y, int z) {
			double d = Minecraft.getMinecraft().thePlayer.getDistanceSq(x+0.5, y+0.5, z+0.5);
			if (d <= 576) {
				if (d <= 256 || world.rand.nextBoolean()) {
					double v = ReikaRandomHelper.getRandomBetween(0.03125, 0.125);
					double t = ReikaRandomHelper.getRandomPlusMinus(facingTheta, 5);
					double p = ReikaRandomHelper.getRandomPlusMinus(facingPhi, 5);
					double[] vel = ReikaPhysicsHelper.polarToCartesian(v, t, p);
					int c = BlockEtherealLight.getParticleColor(world, y);
					float s = (float)ReikaRandomHelper.getRandomBetween(1, 2.5);
					int l = ReikaRandomHelper.getRandomBetween(10, 40);
					EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5, vel[0], vel[1], vel[2]);
					fx.setColor(c).setScale(s).setLife(l).setRapidExpand();//.setNoDepthTest();
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setDouble("phi", facingPhi);
			tag.setDouble("theta", facingTheta);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			facingPhi = tag.getDouble("phi");
			facingTheta = tag.getDouble("theta");
		}

	}

}
