/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.BlockAttachableMini;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockSpawnerShutdown extends BlockAttachableMini {

	public BlockSpawnerShutdown(Material mat) {
		super(mat);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = Blocks.glowstone.getIcon(0, 0);
	}

	@Override
	public int getColor(IBlockAccess iba, int x, int y, int z) {
		return 0xffff30;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void createFX(World world, int x, int y, int z, double dx, double dy, double dz, Random r) {
		int c = this.getColor(world, x, y, z);
		EntityFX fx = new EntityCenterBlurFX(world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2).setColor(c);
		if (r.nextInt(7) == 0) {
			fx = new EntityLaserFX(CrystalElement.WHITE, world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2).setColor(c);
		}
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntitySpawnerShutdown();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	public static class TileEntitySpawnerShutdown extends TileEntity {

		@Override
		public void updateEntity() {
			if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
				return;
			ForgeDirection dir = ((BlockSpawnerShutdown)this.getBlockType()).getSide(worldObj, xCoord, yCoord, zCoord).getOpposite();
			TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			if (te instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner tb = (TileEntityMobSpawner)te;
				tb.func_145881_a().spawnDelay = tb.func_145881_a().maxSpawnDelay-2;
			}
		}

	}
}
