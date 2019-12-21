/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.Render.TESRIcon;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.VoidRitual.TileEntityVoidMonsterTrap;
import Reika.ChromatiCraft.ModInterface.VoidRitual.TileEntityVoidMonsterTrap.VoidMonsterTether;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.VariableEndpointSpline;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderVoidMonsterTrap extends ChromaRenderBase {

	protected static final RayTracer LOS = RayTracer.getVisualLOS();

	public static boolean netherRender = false;

	private static TESRIcon[] overworld = {
			new TESRIcon(ChromaIcons.SUNFLARE, 4, 1.25, 1),
			new TESRIcon(ChromaIcons.CELLFLARE, 2.75, 1.25, 1),
			new TESRIcon(ChromaIcons.ECLIPSEFLARE, 3, 0.9375, 0.75F),
	};
	private static TESRIcon[] nether = {
			new TESRIcon(ChromaIcons.WHITEHOLE, 2, 1.25, 1),
			new TESRIcon(ChromaIcons.PORTALRING, 1.25, 1.25, 2),
			new TESRIcon(ChromaIcons.HEXFLARE2, 2, 1.25, 0.5F),
	};
	private static TESRIcon[] base = {
			new TESRIcon(ChromaIcons.CELLFLARE, 1.5, 1, 1),
			new TESRIcon(ChromaIcons.PORTALRING, 0.75, 1, 1),
			new TESRIcon(ChromaIcons.PORTALRING, 1, 1, 1),
	};

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityVoidMonsterTrap te = (TileEntityVoidMonsterTrap)tile;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDepthMask(false);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		if (te.isInWorld() && te.hasStructure()) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			LOS.setOrigins(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, ep.posX, ep.posY, ep.posZ);
			if (LOS.isClearLineOfSight(te.worldObj)) {
				double dist = ep.getDistance(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
				float f = 0;
				if (dist <= 16) {
					f = 1;
				}
				else if (dist <= 32) {
					f = 1-(float)((dist-16D)/16D);
				}
				ChromaShaders.VOIDTRAP.clearOnRender = true;
				ChromaShaders.VOIDTRAP.setIntensity(f);
				ChromaShaders.VOIDTRAP.getShader().setFocus(te);
				ChromaShaders.VOIDTRAP.getShader().setMatricesToCurrent();
				ChromaShaders.VOIDTRAP.getShader().setField("distance", dist*dist);
				ChromaShaders.VOIDTRAP.getShader().setField("rotation", te.getShaderRotation());
			}
		}

		if (MinecraftForgeClient.getRenderPass() == 1 || !te.isInWorld() || StructureRenderer.isRenderingTiles()) {
			this.drawInner(te, par8);
			if (te.isInWorld() && !StructureRenderer.isRenderingTiles()) {
				this.renderTethers(te, par8);
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderTethers(TileEntityVoidMonsterTrap te, float par8) {
		Collection<VoidMonsterTether> c = te.getTethers();
		BlendMode.ADDITIVEDARK.apply();
		for (VoidMonsterTether v : c) {
			Entity e = v.getEntity(te.worldObj);
			double d = 0.25+0.5*(1-v.getDistance()/16D);
			//ReikaJavaLibrary.pConsole(v.getDistance()+" > "+d);
			int c1 = 0xffffff;
			int c2 = 0xda50ff;
			double dc = te.getTicksExisted()/5D;
			for (VariableEndpointSpline s : v.getSplines()) {
				List<DecimalPosition> li = s.getPoints(8);
				//ReikaJavaLibrary.pConsole(li);
				//ChromaFX.renderBeam(0.5, 0.5, 0.5, e.posX-te.xCoord, te.yCoord-e.posY, -(e.posZ-te.zCoord), par8, 255, d, 0xffffff);
				for (int i = 0; i < li.size()-1; i++) {
					float f = (float)(0.5+0.5*Math.sin(dc));
					int clr = ReikaColorAPI.mixColors(c1, c2, f);
					DecimalPosition pos1 = li.get(i);
					DecimalPosition pos2 = li.get(i+1);
					ChromaFX.renderBeam(pos1.xCoord-te.xCoord, te.yCoord+1-pos1.yCoord, te.zCoord+1-pos1.zCoord, pos2.xCoord-te.xCoord, te.yCoord+1-pos2.yCoord, te.zCoord+1-pos2.zCoord, par8, 255, d, clr);
					dc += 0.4;
				}
				dc += 0.9;
			}
		}
	}

	private void drawInner(TileEntityVoidMonsterTrap te, float par8) {
		ReikaTextureHelper.bindTerrainTexture();
		TESRIcon[] icons = base;
		if (te.hasStructure() || StructureRenderer.isRenderingTiles())
			icons = netherRender || te.isNether() ? nether : overworld;
		else if (!te.isInWorld())
			icons = overworld;

		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		double s = te.isInWorld() ? 0.4375 : 0.33;
		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(s, s, s);
			if (StructureRenderer.isRenderingTiles()) {
				GL11.glRotated(StructureRenderer.getRenderRY(), 0, 1, 0);
				GL11.glRotated(-StructureRenderer.getRenderRX(), 1, 0, 0);
			}
			else {
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			}
		}
		else {
			s = 0.5;
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			GL11.glScaled(s, s, s);
		}

		//double ang = (System.currentTimeMillis()/20D)%360;
		//GL11.glRotated(ang, 0, 0, 1);

		double t = te.isInWorld() ? (te.getTicksExisted()+par8) : System.currentTimeMillis()/50D;

		double s2 = 2+0.5*Math.sin(t/90D);
		//float f = te.isInWorld() ? ((float)(Math.sin(t/5.4D)+Math.cos(t/3.9D))*15-14) : 0;
		//if (f > 1)
		//	f = 1;
		float f = te.getFlashBrightness();

		for (int i = 0; i < icons.length; i++) {
			int c = ReikaColorAPI.getModifiedHue(0xff0000, 265+(int)(15*Math.sin(t/30D+i)));
			c = ReikaColorAPI.getModifiedSat(c, 0.9F+0.1F*(float)Math.sin(t/20D+i/4D));
			c = ReikaColorAPI.getModifiedSat(c, 1-0.15F*i);
			if (f > 0)
				c = ReikaColorAPI.getModifiedSat(c, 1-f);
			IIcon ico = icons[i].icon.getIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			float uu = du-u;
			float vv = dv-v;

			double s3 = s2*icons[i].getSize(te);
			if (StructureRenderer.isRenderingTiles())
				s3 *= 0.625;

			GL11.glPushMatrix();
			GL11.glScaled(s3, s3, s3);
			double da = 0;
			if (te.isNether() && i == 0)
				da = (System.currentTimeMillis()/12D)%360D;
			else if (!te.isNether() && i == 1)
				da = (-System.currentTimeMillis()/50D)%360D;
			if (da != 0)
				GL11.glRotated(da, 0, 0, 1);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, icons[i].getBrightness(te)));
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();

		GL11.glPopMatrix();
	}

}
