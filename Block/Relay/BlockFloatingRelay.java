/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Relay;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockFloatingRelay extends BlockRelayBase {

	public BlockFloatingRelay(Material mat) {
		super(mat);
		this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new FloatingRelayTile();
	}

	@Override
	public final void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/relay_floating");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		double dx = x+0.5;
		double dy = y+0.5;
		double dz = z+0.5;
		int c = 0x22aaff;
		switch(r.nextInt(3)) {
			case 0:
				break;
			case 1:
				c = 0x44ffff;
				break;
			case 2:
				c = 0x2060ff;
				break;
		}
		EntityFX fx = new EntityCenterBlurFX(world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2).setColor(c);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	public static class FloatingRelayTile extends TileRelayBase {

		@Override
		public boolean canTransmit(CrystalElement e) {
			return true;
		}

	}

}
