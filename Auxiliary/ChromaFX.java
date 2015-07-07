/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.Render.Particle.EntityRelayPathFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaFX {

	public static void poolRecipeParticles(EntityItem ei) {
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		double vy = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
		float s = (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.5);
		EntityFX fx = new EntityChromaFluidFX(ei.worldObj, ei.posX, ei.posY, ei.posZ, vx, vy, vz).setGravity(0.125F).setScale(s);
		fx.noClip = true;
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

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

	public static void drawEnergyTransferBeams(WorldLocation src, Collection<CrystalTarget> li) {
		if (!li.isEmpty()) {
			double t = (System.currentTimeMillis()/600D)%360;
			t /= 30D;
			byte sides = 6;
			double r = 0.35;//+0.025*Math.sin(t*12);
			drawEnergyTransferBeams(src, li, r, sides, t);
		}
	}

	public static void drawEnergyTransferBeams(WorldLocation src, Collection<CrystalTarget> li, double r, byte sides, double tick) {
		if (!li.isEmpty()) {
			ReikaRenderHelper.disableLighting();
			GL11.glDisable(GL11.GL_CULL_FACE);
			//GL11.glEnable(GL11.GL_BLEND);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			Tessellator v5 = Tessellator.instance;
			//BlendMode.ADDITIVEDARK.apply();
			GL11.glTranslated(0.5, 0.5, 0.5);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/beam.png");

			for (CrystalTarget ct : li) {
				drawEnergyTransferBeam(src, ct, r, sides, tick);
			}

			//BlendMode.DEFAULT.apply();
			//GL11.glDisable(GL11.GL_BLEND);
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glEnable(GL11.GL_CULL_FACE);
			ReikaRenderHelper.enableLighting();
		}
	}

	public static void drawEnergyTransferBeam(WorldLocation src, CrystalTarget ct, double r, byte sides, double tick) {
		//v5.setColorRGBA_I(te.getColor().color.getJavaColor().brighter().getRGB(), te.renderAlpha+255);
		//v5.addVertex(src.xCoord-te.xCoord+0.5, src.yCoord-te.yCoord+0.5, src.zCoord-te.zCoord+0.5);
		WorldLocation tgt = ct.location;
		CrystalElement e = ct.color;
		Tessellator v5 = Tessellator.instance;
		double dx = tgt.xCoord-src.xCoord+ct.offsetX;
		double dy = tgt.yCoord-src.yCoord+ct.offsetY;
		double dz = tgt.zCoord-src.zCoord+ct.offsetZ;

		GL11.glPushMatrix();
		double f7 = Math.sqrt(dx*dx+dz*dz);
		double f8 = Math.sqrt(dx*dx+dy*dy+dz*dz);
		double ang1 = -Math.atan2(dz, dx) * 180 / Math.PI - 90;
		double ang2 = -Math.atan2(f7, dy) * 180 / Math.PI - 90;
		GL11.glRotated(ang1, 0, 1, 0);
		GL11.glRotated(ang2, 1, 0, 0);

		v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
		v5.setColorOpaque_I(e.getColor());
		for (int i = 0; i <= sides; i++) {
			double f11 = r*Math.sin(i % sides * Math.PI * 2 / sides) * 0.75;
			double f12 = r*Math.cos(i % sides * Math.PI * 2 / sides) * 0.75;
			double f13 = i % sides * 1 / sides;
			v5.addVertexWithUV(f11, f12, 0, tick, tick+1);
			v5.addVertexWithUV(f11, f12, f8, tick+1, tick);
		}

		v5.draw();
		/*
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			double rx = -RenderManager.instance.playerViewX+ang2;
			GL11.glRotated(rx, 0, 0, 1);
			GL11.glTranslated(-0.5, -0.5, 0);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/haze2.png");
			v5.startDrawingQuads();
			v5.addVertexWithUV(0, 0.5, 0, 1, 0);
			v5.addVertexWithUV(1, 0.5, 0, 1, 1);
			v5.addVertexWithUV(1, 0.5, f8, 0, 1);
			v5.addVertexWithUV(0, 0.5, f8, 0, 0);
			v5.draw();
			GL11.glPopMatrix();
			BlendMode.DEFAULT.apply();
			GL11.glDisable(GL11.GL_BLEND);
		 */
		GL11.glPopMatrix();
	}

	public static void spawnRelayParticle(CrystalElement e, ArrayList<Coordinate> li) {
		// Full path
		//EntityRelayPathFX fx = new EntityRelayPathFX(e, li);
		//Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		/*//Old 1-step
		while (li.size() > 1) {
			ArrayList li2 = new ArrayList();
			li2.add(li.get(0));
			li2.add(li.get(1));
			EntityRelayPathFX fx = new EntityRelayPathFX(e, li2);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			li.remove(0);
		}*/

		//1-step
		while (li.size() > 1) {
			EntityRelayPathFX fx = new EntityRelayPathFX(e, li.get(0), li.get(1));
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			li.remove(0);
		}
	}

	public static void doShardBoostingFX(EntityItem ei) {
		CrystalElement e = CrystalElement.elements[ei.getEntityItem().getItemDamage()];
		double rx = ReikaRandomHelper.getRandomPlusMinus(ei.posX, 0.5);
		double ry = ei.posY;//ReikaRandomHelper.getRandomPlusMinus(ei.posY+1, 0.5);
		double rz = ReikaRandomHelper.getRandomPlusMinus(ei.posZ, 0.5);
		//ReikaParticleHelper.REDSTONE.spawnAt(ei.worldObj, rx, ry, rz, e.getRed(), e.getGreen(), e.getBlue());
		double vy = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
		Minecraft.getMinecraft().effectRenderer.addEffect(new EntityRuneFX(ei.worldObj, rx, ry, rz, 0, vy, 0, e));
	}

	public static void spawnShardBoostedEffects(EntityItem ei) {
		for (int i = 0; i < 16; i++) {
			double rx = ei.posX;
			double ry = ei.posY;
			double rz = ei.posZ;
			CrystalElement e = CrystalElement.elements[ei.getEntityItem().getItemDamage()];
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityFlareFX(e, ei.worldObj, rx, ry, rz, vx, vy, vz));
		}
	}

}
