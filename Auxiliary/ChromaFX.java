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
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.Interfaces.ChargingPoint;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRelayPathFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaFX {

	private static final Random rand = new Random();

	public static void drawFillBar(CrystalElement e, int x, int y, int w, int h, float f) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/bartex.png");
		float u = e.ordinal()/16F;
		float du = u+Math.min(0.0625F, w/512F);
		float v = 0;
		float dv = h/128F;

		f = Math.min(f, 1);

		int dh = (int)(f*h);

		Tessellator v5 = Tessellator.instance;

		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.setBrightness(240);

		v5.addVertexWithUV(x, y+h, 0, u, dv);
		v5.addVertexWithUV(x+w, y+h, 0, du, dv);
		v5.addVertexWithUV(x+w, y, 0, du, v);
		v5.addVertexWithUV(x, y, 0, u, v);

		v5.draw();

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		v5.startDrawingQuads();
		v5.setColorOpaque_I(e.getColor());
		v5.setBrightness(240);

		v5.addVertex(x, y+h, 0);
		v5.addVertex(x+w, y+h, 0);
		v5.addVertex(x+w, y+h-dh, 0);
		v5.addVertex(x, y+h-dh, 0);

		v5.draw();

		GL11.glPopAttrib();
	}

	public static void drawHorizontalFillBar(CrystalElement e, int x, int y, int w, int h, float f) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/bartex.png");
		float u = e.ordinal()/16F;
		float du = u+Math.min(0.0625F, h/512F);
		float v = 0;
		float dv = w/128F;

		f = Math.min(f, 1);

		int dw = (int)(f*w);

		Tessellator v5 = Tessellator.instance;

		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.setBrightness(240);

		v5.addVertexWithUV(x, y+h, 0, du, dv);
		v5.addVertexWithUV(x+w, y+h, 0, du, v);
		v5.addVertexWithUV(x+w, y, 0, u, v);
		v5.addVertexWithUV(x, y, 0, u, dv);

		v5.draw();

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		v5.startDrawingQuads();
		v5.setColorOpaque_I(e.getColor());
		v5.setBrightness(240);

		v5.addVertex(x, y+h, 0);
		v5.addVertex(x+dw, y+h, 0);
		v5.addVertex(x+dw, y, 0);
		v5.addVertex(x, y, 0);

		v5.draw();

		GL11.glPopAttrib();
	}

	public static void poolRecipeParticles(EntityItem ei) {
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		double vy = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
		float s = (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.5);
		EntityFX fx = new EntityChromaFluidFX(ei.worldObj, ei.posX, ei.posY, ei.posZ, vx, vy, vz).setGravity(0.125F).setScale(s);
		fx.noClip = true;
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	public static void createPylonChargeBeam(ChargingPoint te, EntityPlayer ep, double dist, CrystalElement e) {
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

	public static int getBlendedColorFromElementList(List<CrystalElement> li, double tick, double cycleModulus) {
		if (li.isEmpty())
			return 0;
		int f1 = (int)(tick/cycleModulus);
		f1 = (f1+li.size())%li.size();
		int c1 = li.get(f1%li.size()).getColor();
		int c2 = li.get((f1+1)%li.size()).getColor();
		float f = (float)(tick%cycleModulus/cycleModulus);
		return ReikaColorAPI.mixColors(c1, c2, 1-f);
	}

	public static void drawLeyLineParticles(World world, int x, int y, int z, Collection<CrystalTarget> li) {
		if (!li.isEmpty()) {
			double t = (System.currentTimeMillis()/600D)%360+2; //+2 to compensate for particle delay
			t /= 30D;

			MultiMap<DecimalPosition, CrystalElement> map = ChromaAux.getBeamColorMixes(li);

			for (DecimalPosition pos : map.keySet()) {
				List<CrystalElement> lc = (List<CrystalElement>)map.get(pos);
				int clr = getBlendedColorFromElementList(lc, t, 0.125);
				int p = Minecraft.getMinecraft().gameSettings.particleSetting;
				if (rand.nextInt(1+p*2) == 0) {
					double dx = pos.xCoord-x-0.5;
					double dy = pos.yCoord-y-0.5;
					double dz = pos.zCoord-z-0.5;
					double dd = ReikaMathLibrary.py3d(dx, dy, dz);
					double dr = rand.nextDouble();
					double px = dx*dr+x+0.5;
					double py = dy*dr+y+0.5;
					double pz = dz*dr+z+0.5;
					EntityLaserFX fx = new EntityLaserFX(CrystalElement.WHITE, world, px, py, pz).setScale(15).setColor(clr);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}
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

			MultiMap<DecimalPosition, CrystalElement> map = ChromaAux.getBeamColorMixes(li);

			for (DecimalPosition pos : map.keySet()) {
				List<CrystalElement> lc = (List<CrystalElement>)map.get(pos);
				int clr = getBlendedColorFromElementList(lc, tick, 0.125);
				drawEnergyTransferBeam(src, pos, clr, r, sides, tick);
			}

			//BlendMode.DEFAULT.apply();
			//GL11.glDisable(GL11.GL_BLEND);
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glEnable(GL11.GL_CULL_FACE);
			ReikaRenderHelper.enableLighting();
		}
	}

	public static void drawEnergyTransferBeam(WorldLocation src, DecimalPosition pos, int color, double r, byte sides, double tick) {
		drawEnergyTransferBeam(new DecimalPosition(src), pos, color, r, sides, tick);
	}

	public static void drawEnergyTransferBeam(DecimalPosition src, DecimalPosition tgt, int color, double r, byte sides, double tick) {
		//v5.setColorRGBA_I(te.getColor().color.getJavaColor().brighter().getRGB(), te.renderAlpha+255);
		//v5.addVertex(src.xCoord-te.xCoord+0.5, src.yCoord-te.yCoord+0.5, src.zCoord-te.zCoord+0.5);
		Tessellator v5 = Tessellator.instance;
		double dx = tgt.xCoord-src.xCoord;
		double dy = tgt.yCoord-src.yCoord;
		double dz = tgt.zCoord-src.zCoord;

		GL11.glPushMatrix();
		double f7 = Math.sqrt(dx*dx+dz*dz);
		double f8 = Math.sqrt(dx*dx+dy*dy+dz*dz);
		double ang1 = -Math.atan2(dz, dx) * 180 / Math.PI - 90;
		double ang2 = -Math.atan2(f7, dy) * 180 / Math.PI - 90;
		GL11.glRotated(ang1, 0, 1, 0);
		GL11.glRotated(ang2, 1, 0, 0);

		v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
		v5.setColorOpaque_I(color);
		v5.setBrightness(240);
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

	public static void doDashParticles(World world, EntityPlayer e, boolean offset) {
		ReikaSoundHelper.playClientSound(ChromaSounds.DASH, Minecraft.getMinecraft().thePlayer, 1, 1, false);
		double x = e.posX;
		double y = e.posY;
		double z = e.posZ;

		if (offset)
			y += 1.62;

		double angX = Math.cos(Math.toRadians(e.rotationYawHead+90));
		double angZ = Math.sin(Math.toRadians(e.rotationYawHead+90));
		double leftX = Math.cos(Math.toRadians(e.rotationYawHead));
		double leftZ = Math.sin(Math.toRadians(e.rotationYawHead));

		int n = 128;
		double dd = 26;
		for (int i = 0; i < n; i++) {
			double d = rand.nextDouble()*dd;
			double py = y-1+rand.nextDouble()*1.85;
			double px = x+d*angX+ReikaRandomHelper.getRandomPlusMinus(0, 1.25)*Math.abs(leftX);
			double pz = z+d*angZ+ReikaRandomHelper.getRandomPlusMinus(0, 1.25)*Math.abs(leftZ);

			double v = 0.125;
			double vx = v*angX;
			double vz = v*angZ;

			int l = 15+rand.nextInt(25);
			float s = 1+rand.nextFloat();

			//int c = ReikaColorAPI.mixColors(0xffffff, ReikaColorAPI.RGBtoHex(96, 192, 255), rand.nextFloat());

			EntityBlurFX fx = new EntityBlurFX(world, px, py, pz, vx, 0, vz).setScale(s).setLife(l);//.setColor(c);
			EntityBlurFX fx2 = new EntityBlurFX(world, px, py, pz, -vx, 0, -vz).setScale(s).setLife(l);//.setColor(c);

			switch(rand.nextInt(3)) {
				case 0:
					fx.setColor(255, 255, 255);
					fx2.setColor(255, 255, 255);
					break;
				case 1:
					fx.setColor(96, 192, 255);
					fx2.setColor(96, 192, 255);
					break;
				case 2:
					fx.setColor(0, 255, 0);
					fx2.setColor(0, 255, 0);
					break;
			}


			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

}
