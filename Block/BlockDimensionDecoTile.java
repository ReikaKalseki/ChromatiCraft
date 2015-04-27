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

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDimensionDecoTile extends BlockDimensionDeco {

	private static final IIcon[][] icons = new IIcon[2][16];

	public static enum Types {
		FIREJET();

		public static Types[] list = values();

		public ItemStack getItem() {
			return new ItemStack(ChromaBlocks.DIMGENTILE.getBlockInstance(), 1, this.ordinal());
		}

		public boolean hasBlockRender() {
			return true;
		}

		public IIcon getOverlay() {
			return icons[1][this.ordinal()];
		}
	}

	public BlockDimensionDecoTile(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new DimensionDecoTile();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		DimensionDecoTile te = (DimensionDecoTile)world.getTileEntity(x, y, z);
		te.activate();
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world)+rand.nextInt(1200));
	}

	@Override
	public int tickRate(World world) {
		return 800;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[0][meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			icons[0][i] = ico.registerIcon("chromaticraft:dimgen2/underlay_"+i);
			icons[1][i] = ico.registerIcon("chromaticraft:dimgen2/overlay_"+i);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return Types.list[meta].hasBlockRender() ? ReikaAABBHelper.getBlockAABB(x, y, z) : null;
	}

	public static class DimensionDecoTile extends TileEntity {

		private static final Random rand = new Random();

		private int tick = 0;

		public DimensionDecoTile() {

		}

		public void activate() {
			tick = 100+rand.nextInt(1200);
		}

		@Override
		public void updateEntity() {
			if (tick > 0) {
				int meta = this.getBlockMetadata();

				switch(Types.list[meta]) {
				case FIREJET:
					break;
				}

				if (worldObj.isRemote)
					this.spawnParticles(Types.list[meta]);

				tick--;
			}

		}

		@SideOnly(Side.CLIENT)
		private void spawnParticles(Types t) {
			switch(t) {
			case FIREJET: {
				if (worldObj.rand.nextBoolean()) {
					double vy = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.01);
					int r = worldObj.rand.nextInt(256);
					int g = worldObj.rand.nextInt(256);
					int b = worldObj.rand.nextInt(256);
					EntityFX fx = new EntityBlurFX(worldObj, xCoord+0.5, yCoord+0.9, zCoord+0.5, 0, vy, 0).setRapidExpand().setScale(4).setColor(r, g, b);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
				break;
			}
			}
		}

	}

}
