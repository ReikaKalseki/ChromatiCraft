/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCrystalBeacon extends TileEntityChromaticBase {

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BEACON;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);
	}
	/*
	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		double ang = Math.toRadians((this.getTicksExisted()*4)%360);
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			//int da = 120;
			//int n = 360/da;
			//for (int i = 0; i < 360; i += da) {
			double r = 0.75;
			double a = ang+i*10;
			double dx = x+0.5+r*Math.sin(a);
			double dz = z+0.5+r*Math.cos(a);
			double dy = y+(((this.getTicksExisted()+i*20)/4)%80)/40D;

			EntityGlobeFX fx = new EntityGlobeFX(e, world, dx, dy, dz, 0, 0, 0);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		//}
	}*/

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		double angle = (System.currentTimeMillis()/15D)%360;
		double d = 0.05;
		double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, d);
		double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, d);
		double py = ReikaRandomHelper.getRandomPlusMinus(y+1.5+0.5*(1+Math.sin(Math.toRadians(angle))), d);
		CrystalElement c = CrystalElement.randomElement();//CrystalElement.elements[(this.getTicksExisted()/16)%16];
		EntityBlurFX fx = new EntityBlurFX(c, world, px, py, pz, 0, 0, 0).setScale(2F).setLife(10).setIcon(ChromaIcons.CENTER);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
