/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockVoidRift extends Block {

	private static final Random rand = new Random();

	public BlockVoidRift(Material mat) {
		super(mat);
		this.setResistance(900000);
		//this.setBlockUnbreakable();
		this.setHardness(10);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		return ProgressStage.CTM.isPlayerAtStage(ep) ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z) : -1;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.vriftRender;
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityVoidRift();
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {

	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimgen/voidrift");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s == 1 ? blockIcon : ChromaBlocks.STRUCTSHIELD.getBlockInstance().getIcon(0, 0);
	}

	/*
	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		VoidRiftRenderer.renderPass = pass;
		return true;
	}
	 */
	public static class TileEntityVoidRift extends TileEntity {

		public static final int HEIGHT = 16;

		private BlockKey[][] blockCache = new BlockKey[3][3];

		@Override
		public boolean canUpdate() {
			return false;//true;
		}

		@Override
		public void updateEntity() {
			if (worldObj.isRemote) {
				this.spawnParticles(worldObj, xCoord, yCoord, zCoord);
			}
		}

		@SideOnly(Side.CLIENT)
		private void spawnParticles(World world, int x, int y, int z) {
			int n = Math.max(1, (4-Minecraft.getMinecraft().gameSettings.particleSetting)/2);
			for (int i = 0; i < n; i++) {
				ArrayList<Integer> sides = ReikaJavaLibrary.makeListFrom(2, 3, 4, 5);
				Iterator<Integer> it = sides.iterator();
				while (it.hasNext()) {
					int side = it.next();
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
					int dx = x+dir.offsetX;
					int dz = z+dir.offsetZ;
					if (world.getBlock(dx, y, dz) == this.getBlockType() && world.getBlockMetadata(dx, y, dz) == this.getBlockMetadata())
						it.remove();
				}
				if (sides.isEmpty())
					return;
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[sides.get(rand.nextInt(sides.size()))];
				double px = dir.offsetX == 0 ? x+rand.nextDouble() : x+0.5+dir.offsetX*0.5;
				double pz = dir.offsetZ == 0 ? z+rand.nextDouble() : z+0.5+dir.offsetZ*0.5;
				float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
				int l = 40+rand.nextInt(80);
				float s = (float)ReikaRandomHelper.getRandomBetween(2.5, 6);
				EntityBlurFX fx = new EntityBlurFX(world, px, y+1, pz).fadeColors(0xffffff, this.getColor().getColor()).setScale(s).setLife(l).setGravity(g).setRapidExpand();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass <= 1;
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1+HEIGHT, zCoord+1);
		}

		@Override
		public double getMaxRenderDistanceSquared()
		{
			return 65536;
		}

		public CrystalElement getColor() {
			return CrystalElement.elements[this.getBlockMetadata()];
		}

		public boolean hasAt(int dx, int dz) {
			return this.getAt(dx, dz).blockID == this.getBlockType();// && worldObj.getBlockMetadata(xCoord+dx, yCoord+dy, zCoord+dz) == this.getBlockMetadata();
		}

		public BlockKey getAt(int dx, int dz) {
			BlockKey bk = blockCache[dx+1][dz+1];
			if (bk == null) {
				bk = BlockKey.getAt(worldObj, xCoord+dx, yCoord, zCoord+dz);
				blockCache[dx+1][dz+1] = bk;
			}
			return bk;
		}

	}

}
