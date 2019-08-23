/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FocusAcceleratable;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.Interfaces.ChargingPoint;
import Reika.ChromatiCraft.Magic.Network.TargetData;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRelayPathFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityAtmosphericRelay;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.ParticleController.EntityLockMotionController;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.MotionController;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaFX {

	private static final Random rand = new Random();

	private static final ColorBlendList FOCUS_CRYSTAL_PARTICLES = new ColorBlendList(6, 0x45ff45, 0x45ff45, 0x45ff45, 0xff4040, 0xff4040, 0xff4040, 0xff4040, 0xff4040, 0xff4040, 0x22aaff, 0x22aaff, 0x22aaff, 0x22aaff, 0xb220ff, 0xb220ff);

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
		Coordinate loc = te.getChargeParticleOrigin(ep, e);
		//te.addTarget(loc, te.getColor(), ep.posX-loc.xCoord, ep.posY+ep.getEyeHeight()-loc.yCoord, ep.posZ-loc.zCoord);
		int sx = loc.xCoord;
		int sy = loc.yCoord;
		int sz = loc.zCoord;

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

		float s = (float)(1.875+0.5*Math.sin(Math.toRadians(dist*360)));
		MotionController m = new EntityLockMotionController(ep, 0.03125/8, 0.125*4, 0.875);
		EntityFX fx = new EntityBlurFX(e, te.getWorld(), x, y, z, 0, 0, 0).setScale(s).setNoSlowdown().setLife((int)dd*10).setMotionController(m);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
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

	public static void drawLeyLineParticles(World world, int x, int y, int z, double r, Collection<CrystalTarget> li) {
		if (!li.isEmpty()) {
			double t = (System.currentTimeMillis()/600D)%360+2; //+2 to compensate for particle delay
			t /= 30D;

			MultiMap<TargetData, CrystalElement> map = ChromaAux.getBeamColorMixes(li);
			Minecraft mc = Minecraft.getMinecraft();
			int p = mc.gameSettings.particleSetting;
			for (TargetData pos : map.keySet()) {
				if (pos.targetClass == TileEntityAtmosphericRelay.class)
					continue;
				if (pos.isRenderable() && pos.isMaximumEndpointDistanceWithin(mc.thePlayer, 512)) {
					List<CrystalElement> lc = (List<CrystalElement>)map.get(pos);
					int clr = getBlendedColorFromElementList(lc, t, 0.125);
					if (rand.nextInt(1+p*3) == 0) {
						double dx = pos.position.xCoord-x-0.5;
						double dy = pos.position.yCoord-y-0.5;
						double dz = pos.position.zCoord-z-0.5;
						//double dd = ReikaMathLibrary.py3d(dx, dy, dz);
						double dr = rand.nextDouble();
						float s = (float)(15D/0.35*ReikaMathLibrary.linterpolate(dr, 0, 1, r, pos.targetWidth));
						//ReikaJavaLibrary.pConsole(dr+" @ "+r+" > "+pos.right+" = "+s);
						double px = dx*dr+x+0.5;
						double py = dy*dr+y+0.5;
						double pz = dz*dr+z+0.5;
						EntityLaserFX fx = new EntityLaserFX(CrystalElement.WHITE, world, px, py, pz).setScale(s).setColor(clr);
						mc.effectRenderer.addEffect(fx);
					}
				}
			}
		}
	}

	public static void drawEnergyTransferBeams(WorldLocation src, double r, Collection<CrystalTarget> li) {
		if (!li.isEmpty()) {
			double t = (System.currentTimeMillis()/600D)%360;
			t /= 30D;
			//double r = 0.35;//+0.025*Math.sin(t*12);
			drawEnergyTransferBeams(src, li, r, 6, t);
		}
	}

	public static void drawEnergyTransferBeams(WorldLocation src, Collection<CrystalTarget> li, double r, int sides, double tick) {
		if (!li.isEmpty()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			ReikaRenderHelper.disableLighting();
			GL11.glDisable(GL11.GL_CULL_FACE);
			//GL11.glEnable(GL11.GL_BLEND);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			Tessellator v5 = Tessellator.instance;
			//BlendMode.ADDITIVEDARK.apply();
			GL11.glTranslated(0.5, 0.5, 0.5);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/beam.png");

			MultiMap<TargetData, CrystalElement> map = ChromaAux.getBeamColorMixes(li);
			for (TargetData pos : map.keySet()) {
				if (pos.isRenderable()) {
					List<CrystalElement> lc = (List<CrystalElement>)map.get(pos);
					int clr = getBlendedColorFromElementList(lc, tick, 0.125);
					if (pos.targetClass == TileEntityAtmosphericRelay.class) {
						GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
						GL11.glEnable(GL11.GL_BLEND);
						ReikaTextureHelper.bindTexture(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/beam_trans.png");
						DecimalPosition pos2 = DecimalPosition.interpolate(src.xCoord+0.5, src.yCoord+0.5, src.zCoord+0.5, pos.position.xCoord, pos.position.yCoord, pos.position.zCoord, 0.5).offset(0, 192, 0);
						drawEnergyTransferBeam(src, pos2, clr, r, pos.targetWidth, sides, tick, false);
						GL11.glPushMatrix();
						GL11.glTranslated(pos2.xCoord-src.xCoord-0.5, pos2.yCoord-src.yCoord-0.5, pos2.zCoord-src.zCoord-0.5);
						drawEnergyTransferBeam(pos2, pos.position, clr, r, pos.targetWidth, sides, tick, false);
						GL11.glPopMatrix();
						ReikaTextureHelper.bindTexture(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/beam.png");
						GL11.glPopAttrib();
					}
					else {
						//ReikaJavaLibrary.pConsole("Rendering beam from "+src+" to "+pos.position);
						drawEnergyTransferBeam(src, pos.position, clr, r, pos.targetWidth, sides, tick, false);
					}
				}
			}

			//BlendMode.DEFAULT.apply();
			//GL11.glDisable(GL11.GL_BLEND);
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glPopAttrib();
		}
	}

	public static void drawEnergyTransferBeam(WorldLocation src, DecimalPosition tgt, int color, double r1, double r2, int sides, double tick, boolean spiralTex) {
		drawEnergyTransferBeam(src.xCoord+0.5, src.yCoord+0.5, src.zCoord+0.5, tgt.xCoord, tgt.yCoord, tgt.zCoord, color, color, r1, r2, sides, tick, spiralTex);
	}

	public static void drawEnergyTransferBeam(DecimalPosition src, DecimalPosition tgt, int color, double r1, double r2, int sides, double tick, boolean spiralTex) {
		drawEnergyTransferBeam(src.xCoord, src.yCoord, src.zCoord, tgt.xCoord, tgt.yCoord, tgt.zCoord, color, color, r1, r2, sides, tick, spiralTex);
	}

	public static void drawEnergyTransferBeam(DecimalPosition src, DecimalPosition tgt, int color1, int color2, double r1, double r2, int sides, double tick, boolean spiralTex) {
		drawEnergyTransferBeam(src.xCoord, src.yCoord, src.zCoord, tgt.xCoord, tgt.yCoord, tgt.zCoord, color1, color2, r1, r2, sides, tick, spiralTex);
	}

	public static void drawEnergyTransferBeam(double x1, double y1, double z1, double x2, double y2, double z2, int color1, int color2, double r1, double r2, int sides, double tick, boolean spiralTex) {
		//v5.setColorRGBA_I(te.getColor().color.getJavaColor().brighter().getRGB(), te.renderAlpha+255);
		//v5.addVertex(src.xCoord-te.xCoord+0.5, src.yCoord-te.yCoord+0.5, src.zCoord-te.zCoord+0.5);
		Tessellator v5 = Tessellator.instance;
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;

		GL11.glPushMatrix();
		double f7 = Math.sqrt(dx*dx+dz*dz);
		double f8 = Math.sqrt(dx*dx+dy*dy+dz*dz);
		double ang1 = -Math.atan2(dz, dx) * 180 / Math.PI - 90;
		double ang2 = -Math.atan2(f7, dy) * 180 / Math.PI - 90;
		GL11.glRotated(ang1, 0, 1, 0);
		GL11.glRotated(ang2, 1, 0, 0);

		v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
		v5.setBrightness(240);
		for (int i = 0; i <= sides; i++) {
			double f11a = r1*Math.sin(i % sides * Math.PI * 2 / sides) * 0.75;
			double f12a = r1*Math.cos(i % sides * Math.PI * 2 / sides) * 0.75;
			double f11b = r2*Math.sin(i % sides * Math.PI * 2 / sides) * 0.75;
			double f12b = r2*Math.cos(i % sides * Math.PI * 2 / sides) * 0.75;
			double f13 = i % sides * 1 / sides;
			double d = spiralTex ? i/(double)sides : 0;
			v5.setColorOpaque_I(color1);
			v5.addVertexWithUV(f11a, f12a, 0, tick+d, tick+1+d);
			v5.setColorOpaque_I(color2);
			v5.addVertexWithUV(f11b, f12b, f8, tick+1+d, tick+d);
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

	@SideOnly(Side.CLIENT)
	public static void doGrowthWandParticles(World world, int x, int y, int z) {
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
		double vy = ReikaRandomHelper.getRandomPlusMinus(0.1875, 0.0625);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
		EntityFX fx = new EntityBlurFX(world, x+0.5, y+0.125, z+0.5, vx, vy, vz).setColor(0, 192, 0).setScale(1).setLife(20).setGravity(0.25F);
		fx.noClip = true;
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@SideOnly(Side.CLIENT)
	public static void doGluonClientside(World world, int e1, int e2) {
		EntityBallLightning src = (EntityBallLightning)world.getEntityByID(e1);
		EntityBallLightning tgt = (EntityBallLightning)world.getEntityByID(e2);
		if (src == null || tgt == null) {
			//ChromatiCraft.logger.debug("Null ball lightning to receive effect???");
			return;
		}
		Vec3 vec = ReikaVectorHelper.getVec2Pt(src.posX, src.posY, src.posZ, tgt.posX, tgt.posY, tgt.posZ);
		double lenv = vec.lengthVector();
		for (float i = 0; i <= lenv; i += 0.125) {
			double f = i/lenv;
			double ddx = src.posX-vec.xCoord*f;
			double ddy = src.posY-vec.yCoord*f;
			double ddz = src.posZ-vec.zCoord*f;
			int c = ReikaColorAPI.mixColors(tgt.getRenderColor(), src.getRenderColor(), (float)f);
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityBlurFX(world, ddx, ddy, ddz).setColor(c).setLife(8));
		}
		src.doBoltClient(tgt);
		tgt.doBoltClient(src);
	}

	public static int[] getChromaColorTiles() {
		int[] arr = new int[]{
				0x94DE5C,
				0xC5D471,
				0xE6C192,
				0xF9A9B0,
				0xFE8CC8,
				0xFC71D5,
				0xF061D8,
				0xD563D1,
				0xAC76C0,
				0x7290AA,
				0x28A792,
				0x00B781,
				0x00C47E,
				0x18CD8B,
				0x58CF9F,
				0x96C8B2,
				0xBEBCC0,
				0xD9AEC8,
				0xE99EC8,
				0xF090C1,
				0xED87B5,
				0xE186A8,
				0xCE8B9C,
				0xB49596,
				0x97A298,
				0x78ADA0,
				0x60B6AA,
				0x56BDB5,
				0x60C0BC,
				0x77BFBE,
				0x8FB8BB,
				0x98ACBA,
		};
		int[] arr2 = Arrays.copyOf(arr, arr.length);
		ArrayUtils.reverse(arr2);
		return ArrayUtils.addAll(arr, arr2);
	}

	public static void drawRadialFillbar(double frac, int tintColor, boolean segments) {
		double u = 0;
		double du = 0.5;
		double v = segments ? 0.5 : 0;
		double dv = v+0.5;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/radialfill.png");
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.draw();

		v5.startDrawing(GL11.GL_TRIANGLE_FAN);
		v5.setBrightness(240);
		v5.setColorOpaque_I(tintColor);
		u = 0.75;
		v = v+0.25;
		v5.addVertexWithUV(0, 0, 0, u, v);
		double ma = 360*frac;
		double da = 0.25;
		for (double a = 0; a < ma; a += da) {
			double x = Math.sin(Math.toRadians(a+90));
			double y = Math.cos(Math.toRadians(a+90));
			du = u+x*0.25;
			dv = v+y*0.25;
			//ReikaJavaLibrary.pConsole(a+">"+x+","+y+" @ "+du+","+dv+" from "+u+","+v);
			v5.addVertexWithUV(x, y, 0, du, dv);
		}
		v5.draw();
	}

	/*
	public static void renderBeam(double x1, double y1, double z1, double x2, double y2, double z2, float par8, double h, int argb) {
		renderBeam(x1, y1, z1, x2, y2, z2, par8, ReikaColorAPI.getAlpha(argb), h, argb & 0xffffff);
	}
	 */

	public static void renderBeam(double x1, double y1, double z1, double x2, double y2, double z2, float par8, int a, double h) {
		renderBeam(x1, y1, z1, x2, y2, z2, par8, a, h, 0xffffff);
	}

	public static void renderBeam(double x1, double y1, double z1, double x2, double y2, double z2, float par8, int a, double h, int baseColor) {
		renderBeam(x1, y1, z1, x2, y2, z2, par8, a, h, baseColor, baseColor);
	}

	public static void renderBeam(double x1, double y1, double z1, double x2, double y2, double z2, float par8, int a, double h, int c1, int c2) {
		Tessellator v5 = Tessellator.instance;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		if (c1 != c2)
			GL11.glShadeModel(GL11.GL_SMOOTH);
		v5.startDrawing(GL11.GL_LINES);
		v5.setBrightness(240);
		v5.setColorRGBA_I(c1, 255);
		v5.addVertex(x1, y1, z1);
		if (c1 != c2)
			v5.setColorRGBA_I(c2, 255);
		v5.addVertex(x2, y2, z2);

		v5.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.LASER.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		double dd = ReikaMathLibrary.py3d(x2-x1, y2-y1, z2-z1);
		int n = (int)(dd);
		double dn = dd-n;

		GL11.glPushMatrix();
		//GL11.glRotated(ang, 0, 0, 0);
		v5.startDrawingQuads();
		v5.setBrightness(240);
		if (c1 == c2)
			v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c1, a/255F), a);
		for (double d = 0; d < n; d++) {
			double nx = x1+(x2-x1)*d/dd;
			double ny = y1+(y2-y1)*d/dd;
			double nz = z1+(z2-z1)*d/dd;

			double px = x1+(x2-x1)*(d+1)/dd;
			double py = y1+(y2-y1)*(d+1)/dd;
			double pz = z1+(z2-z1)*(d+1)/dd;

			int ca = c1 != c2 ? ReikaColorAPI.mixColors(c1, c2, 1-(float)(d/dd)) : c1;
			int cb = c1 != c2 ? ReikaColorAPI.mixColors(c1, c2, 1-(float)((d+1)/dd)) : c1;

			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(ca, a/255F), a);
			v5.addVertexWithUV(nx, ny-h, nz, u, v);
			v5.addVertexWithUV(nx, ny+h, nz, u, dv);
			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(cb, a/255F), a);
			v5.addVertexWithUV(px, py+h, pz, du, dv);
			v5.addVertexWithUV(px, py-h, pz, du, v);

			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(ca, a/255F), a);
			v5.addVertexWithUV(nx-h, ny, nz, u, v);
			v5.addVertexWithUV(nx+h, ny, nz, u, dv);
			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(cb, a/255F), a);
			v5.addVertexWithUV(px+h, py, pz, du, dv);
			v5.addVertexWithUV(px-h, py, pz, du, v);

			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(ca, a/255F), a);
			v5.addVertexWithUV(nx, ny, nz-h, u, v);
			v5.addVertexWithUV(nx, ny, nz+h, u, dv);
			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(cb, a/255F), a);
			v5.addVertexWithUV(px, py, pz+h, du, dv);
			v5.addVertexWithUV(px, py, pz-h, du, v);
		}
		if (dn > 0) {
			double nx = x1+(x2-x1)*n/dd;
			double ny = y1+(y2-y1)*n/dd;
			double nz = z1+(z2-z1)*n/dd;

			double px = x1+(x2-x1)*(dn+n)/dd;
			double py = y1+(y2-y1)*(dn+n)/dd;
			double pz = z1+(z2-z1)*(dn+n)/dd;

			int ca = c1 != c2 ? ReikaColorAPI.mixColors(c1, c2, 1-(float)(n/dd)) : c1;
			int cb = c1 != c2 ? c2 : c1;

			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(ca, a/255F), a);
			v5.addVertexWithUV(nx, ny-h, nz, u, v);
			v5.addVertexWithUV(nx, ny+h, nz, u, dv);
			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(cb, a/255F), a);
			v5.addVertexWithUV(px, py+h, pz, du, dv);
			v5.addVertexWithUV(px, py-h, pz, du, v);

			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(ca, a/255F), a);
			v5.addVertexWithUV(nx-h, ny, nz, u, v);
			v5.addVertexWithUV(nx+h, ny, nz, u, dv);
			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(cb, a/255F), a);
			v5.addVertexWithUV(px+h, py, pz, du, dv);
			v5.addVertexWithUV(px-h, py, pz, du, v);

			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(ca, a/255F), a);
			v5.addVertexWithUV(nx, ny, nz-h, u, v);
			v5.addVertexWithUV(nx, ny, nz+h, u, dv);
			if (c1 != c2)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(cb, a/255F), a);
			v5.addVertexWithUV(px, py, pz+h, du, dv);
			v5.addVertexWithUV(px, py, pz-h, du, v);
		}
		v5.draw();
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	public static void renderBolt(LightningBolt b, float par8, int a, double h, int splinePoints) {
		renderBolt(b, par8, a, h, splinePoints, 0xffffff);
	}

	public static void renderBolt(LightningBolt b, float par8, int a, double h, int splinePoints, int color) {
		if (splinePoints > 2) {
			Spline s = new Spline(SplineType.CENTRIPETAL);
			for (int i = 0; i <= b.nsteps; i++) {
				DecimalPosition pos = b.getPosition(i);
				s.addPoint(new BasicSplinePoint(pos));
			}
			List<DecimalPosition> li = s.get(splinePoints, false);
			for (int i = 0; i < li.size()-1; i++) {
				DecimalPosition pos1 = li.get(i);
				DecimalPosition pos2 = li.get(i+1);
				renderBeam(pos1.xCoord, pos1.yCoord, pos1.zCoord, pos2.xCoord, pos2.yCoord, pos2.zCoord, par8, a, h, color);
			}
		}
		else {
			for (int i = 0; i < b.nsteps; i++) {
				DecimalPosition pos1 = b.getPosition(i);
				DecimalPosition pos2 = b.getPosition(i+1);
				renderBeam(pos1.xCoord, pos1.yCoord, pos1.zCoord, pos2.xCoord, pos2.yCoord, pos2.zCoord, par8, a, h, color);
			}
		}
	}

	public static void doFocusCrystalParticles(World world, int x, int y, int z, FocusAcceleratable fa) {
		if (HoldingChecks.FOCUSCRYSTAL.isClientHolding()) {
			Collection<Coordinate> set = fa.getRelativeFocusCrystalLocations();
			for (Coordinate c : set) {
				double dx = x+c.xCoord+0.5;
				double dy = y+c.yCoord+0.125;
				double dz = z+c.zCoord+0.5;
				int n = ReikaRandomHelper.getRandomBetween(1, 3);
				for (int i = 0; i < n; i++) {
					boolean ctr = rand.nextInt(3) == 0;
					double px = ReikaRandomHelper.getRandomPlusMinus(dx, 0.03125);
					double py = ReikaRandomHelper.getRandomPlusMinus(dy, 0.03125);
					double pz = ReikaRandomHelper.getRandomPlusMinus(dz, 0.03125);
					double ang = (world.getTotalWorldTime()/2D)%360;
					int split = 6;//3;
					double dAng = 360D/split*rand.nextInt(split);
					ang = (ang+dAng)%360D;
					ang = ReikaRandomHelper.getRandomPlusMinus(ang, 6);
					if (!ctr) {
						double d = ReikaRandomHelper.getRandomBetween(0.15, 0.25);
						double[] off = ReikaPhysicsHelper.polarToCartesian(d, 0, ang);
						px += off[0];
						pz += off[2];
					}
					double vel = ctr ? 0 : ReikaRandomHelper.getRandomBetween(0.015, 0.03);
					double[] v = ReikaPhysicsHelper.polarToCartesian(vel, 0, ang);
					float g = -(float)ReikaRandomHelper.getRandomBetween(0, 0.125);
					int l = ReikaRandomHelper.getRandomBetween(8, 40);
					float s = (float)ReikaRandomHelper.getRandomBetween(0.25, 1.25);
					int clr = FOCUS_CRYSTAL_PARTICLES.getColor(world.getTotalWorldTime()/8D);
					EntityBlurFX fx = new EntityBlurFX(world, px, py, pz, v[0], v[1], v[2]);
					ChromaIcons ico = ChromaIcons.FADE;
					switch(rand.nextInt(4)) {
						case 0:
							ico = ChromaIcons.NODE2;
							break;
						case 1:
							ico = ChromaIcons.FLARE;
							break;
					}
					fx.setColor(clr).setLife(l).setScale(s).setGravity(g).setRapidExpand().setAlphaFading().setIcon(ico);
					EntityBlurFX fx2 = new EntityBlurFX(world, px, py, pz, v[0], v[1], v[2]);
					fx2.setColor(0xffffff).setLife(l).setScale(s*0.6F).setGravity(g).setRapidExpand().setAlphaFading().setIcon(ico).lockTo(fx);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
				}
			}
		}
	}

}
