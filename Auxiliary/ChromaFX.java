/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPylon;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaFX {

	public static void createPylonChargeBeam(CrystalSource te, EntityPlayer ep, double dist, CrystalElement e) {
		//WorldLocation loc = new WorldLocation(ep);
		//te.addTarget(loc, te.getColor(), ep.posX-loc.xCoord, ep.posY+ep.getEyeHeight()-loc.yCoord, ep.posZ-loc.zCoord);
		int sx = te.getX();
		int sy = te.getY();
		int sz = te.getZ();

		double dx = ep.posX-sx-0.5;
		double dy = ep.posY-0.125-sy-0.5;
		double dz = ep.posZ-sz-0.5;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		double r = 0;//1-dist;
		double ox = -0*Math.sin(Math.toRadians(ep.rotationYawHead+22.5))*Math.abs(Math.cos(ep.rotationPitch));
		double oy = -0.0625;//-0.875*Math.sin(ep.rotationPitch);
		double oz = 0*Math.cos(Math.toRadians(ep.rotationYawHead+22.5))*Math.abs(Math.cos(ep.rotationPitch));
		//ReikaJavaLibrary.pConsole(String.format("%.2f, %.2f", dx, dz));
		double x = sx+0.5+(dx+ox)*r;
		double y = sy+0.5+(dy)*r;
		double z = sz+0.5+(dz+oz)*r;

		double dx2 = dx+ox;
		double dy2 = dy+oy;
		double dz2 = dz+oz;
		double v = 0.125;
		double vx = dx2/dd*v;
		double vy = dy2/dd*v;
		double vz = dz2/dd*v;

		float s = (float)(1.75+0.5*Math.sin(Math.toRadians(dist*360)));
		Minecraft.getMinecraft().effectRenderer.addEffect(new EntityBlurFX(e, te.getWorld(), x, y, z, vx, vy, vz).setScale(s).setNoSlowdown().setLife((int)dd*10));
	}

	public static void killPylonChargeBeam(TileEntityCrystalPylon te, EntityPlayer ep) {
		//WorldLocation loc = new WorldLocation(ep);
		//te.removeTarget(loc, te.getColor(), ep.posX-loc.xCoord, ep.posY+ep.getEyeHeight()-loc.yCoord, ep.posZ-loc.zCoord);
	}

}
