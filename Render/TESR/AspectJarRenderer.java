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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TileEntityAspectJar;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TileEntityAspectJar.JarTilt;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;

import thaumcraft.api.aspects.Aspect;

public class AspectJarRenderer extends ChromaRenderBase {

	private static Class jarModelClass;
	private static Method modelRender;
	private static Field liquidIcon;

	private ModelBase model;

	private final ColorBlendList primalAspectColors = new ColorBlendList(20);

	public AspectJarRenderer() {

		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		try {
			model = (ModelBase)jarModelClass.newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RegistrationException(ChromatiCraft.instance, "Could not create ThaumCraft jar model instance to use for ChromatiCraft jar!");
		}

		for (Aspect a : Aspect.getPrimalAspects())
			primalAspectColors.addColor(a.getColor());
	}

	static {
		if (ModList.THAUMCRAFT.isLoaded()) {
			try {
				jarModelClass = Class.forName("thaumcraft.client.renderers.models.ModelJar");
				modelRender = jarModelClass.getDeclaredMethod("renderAll");

				Class jar = Class.forName("thaumcraft.common.blocks.BlockJar");
				liquidIcon = jar.getDeclaredField("iconLiquid");
				liquidIcon.setAccessible(true);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new RegistrationException(ChromatiCraft.instance, "Could not read ThaumCraft jar renderer to use for ChromatiCraft jar!");
			}
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "jar";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		TileEntityAspectJar te = (TileEntityAspectJar)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glTranslated(0.5, 0.01, 0.5);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			if (te.hasDirectDrainUpgrade())
				this.renderUpgradeFlare(te, par8);
			if (te.isLockedToCurrent())
				this.renderLockFlare(te, par8);
		}
		try {
			ForgeDirection sp = te.getSpill();
			if (sp != null) {
				int a = sp.offsetX == 0 ? 1 : 0;
				int b = 1-a;
				int dir = sp.offsetX+sp.offsetZ;
				GL11.glRotated(dir*90, a, 0, b);
			}
			else {
				JarTilt jt = te.getAngle();
				if (jt != null) {
					int a = jt.direction.offsetX == 0 ? 1 : 0;
					int b = 1-a;
					int dir = jt.direction.offsetX+jt.direction.offsetZ;
					GL11.glRotated(dir*jt.getAngle(), a, 0, b);
				}
			}
			this.renderAspects(te);
			this.renderJar(te);
		}
		catch (Exception e) {
			throw new RuntimeException("Could not render ThaumCraft jar model!");
		}
		GL11.glPopMatrix();
	}

	private void renderLockFlare(TileEntityAspectJar te, float par8) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		ReikaRenderHelper.disableEntityLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.LATTICE.getIcon();
		float u = ico.getMinU();
		float dv = ico.getMinV();
		float du = ico.getMaxU();
		float v = ico.getMaxV();

		Tessellator v5 = Tessellator.instance;
		GL11.glDisable(GL11.GL_CULL_FACE);

		double o = 0.01;
		double w = 0.3125;
		double h = 0.75;

		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorOpaque_I(primalAspectColors.getColor(te.getTicksExisted()+par8));
		v5.addVertexWithUV(-w-o, 0+o, w+o, u, v);
		v5.addVertexWithUV(w+o, 0+o, w+o, du, v);
		v5.addVertexWithUV(w+o, -h-o, w+o, du, dv);
		v5.addVertexWithUV(-w-o, -h-o, w+o, u, dv);

		v5.addVertexWithUV(-w-o, 0+o, -w-o, u, v);
		v5.addVertexWithUV(w+o, 0+o, -w-o, du, v);
		v5.addVertexWithUV(w+o, -h-o, -w-o, du, dv);
		v5.addVertexWithUV(-w-o, -h-o, -w-o, u, dv);

		v5.addVertexWithUV(-w-o, 0+o, -w-o, u, v);
		v5.addVertexWithUV(-w-o, 0+o, w+o, du, v);
		v5.addVertexWithUV(-w-o, -h-o, w+o, du, dv);
		v5.addVertexWithUV(-w-o, -h-o, -w-o, u, dv);

		v5.addVertexWithUV(w+o, 0+o, -w-o, u, v);
		v5.addVertexWithUV(w+o, 0+o, w+o, du, v);
		v5.addVertexWithUV(w+o, -h-o, w+o, du, dv);
		v5.addVertexWithUV(w+o, -h-o, -w-o, u, dv);

		v5.addVertexWithUV(-w-o, -h-o, -w-o, u, v);
		v5.addVertexWithUV(w+o, -h-o, -w-o, du, v);
		v5.addVertexWithUV(w+o, -h-o, w+o, du, dv);
		v5.addVertexWithUV(-w-o, -h-o, w+o, u, dv);

		v5.addVertexWithUV(-w-o, 0+o, -w-o, u, v);
		v5.addVertexWithUV(w+o, 0+o, -w-o, du, v);
		v5.addVertexWithUV(w+o, 0+o, w+o, du, dv);
		v5.addVertexWithUV(-w-o, 0+o, w+o, u, dv);
		v5.draw();

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();
	}

	private void renderUpgradeFlare(TileEntityAspectJar te, float par8) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		ReikaRenderHelper.disableEntityLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.SIDEDFLOW.getIcon();
		float u = ico.getMinU();
		float dv = ico.getMinV();
		float du = ico.getMaxU();
		float v = ico.getMaxV();

		Tessellator v5 = Tessellator.instance;
		GL11.glDisable(GL11.GL_CULL_FACE);

		double s1 = 0.45+0.2*Math.sin((2.3+te.getTicksExisted()+par8)/11D);
		double s2 = 0.75+0.125*Math.sin((6.7+te.getTicksExisted()+par8)/17D);
		double[] ss = {s1, s2};
		for (double s : ss) {
			GL11.glPushMatrix();
			GL11.glTranslated(0, -0.85, 0);
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);

			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.setColorOpaque_I(s == s1 ? 0xffffff : primalAspectColors.getColor(te.getTicksExisted()+par8));
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();
	}

	private void renderJar(TileEntityAspectJar te) throws Exception {
		this.bindTextureByName("/Reika/ChromatiCraft/Textures/TileEntity/jar.png");
		modelRender.invoke(model);
	}

	private void renderAspects(TileEntityAspectJar te) throws Exception {
		int i = 0;
		for (Aspect a : te.getAllAspects()) {
			this.renderAspect(te, a, te.getAmount(a), i);
			i++;
		}
	}

	private void renderAspect(TileEntityAspectJar te, Aspect a, int amt, int i) throws Exception {
		if (amt <= 0)
			return;
		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		RenderBlocks rb = RenderBlocks.getInstance();

		GL11.glDisable(GL11.GL_LIGHTING);

		double cap = a.isPrimal() ? te.CAPACITY_PRIMAL : te.CAPACITY;
		double level = 0.625*Math.sqrt(amt/cap);//ReikaMathLibrary.logbase2(te.getAmount())/ReikaMathLibrary.logbase2(te.CAPACITY);

		Tessellator v5 = Tessellator.instance;

		double w = 0.125;

		double dx = 0+w*(i/4);
		double dz = 0+w*(i%4);

		double minx = 0.25+dx;
		double minz = 0.25+dz;

		double maxx = minx+w;//0.75;
		double maxz = minz+w;//0.75;

		rb.setRenderBounds(minx, 0.0625, minz, maxx, 0.0625 + level, maxz);

		v5.startDrawingQuads();
		v5.setColorOpaque_I(a.getColor());
		int bright = 200;
		Block jar = ThaumItemHelper.BlockEntry.JAR.getBlock();
		if (te.worldObj != null) {
			bright = Math.max(200, jar.getMixedBrightnessForBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord));
		}
		v5.setBrightness(bright);

		IIcon icon = (IIcon)liquidIcon.get(jar);

		ReikaTextureHelper.bindTerrainTexture();

		rb.renderFaceYNeg(jar, -0.5D, 0.0D, -0.5D, icon);
		rb.renderFaceYPos(jar, -0.5D, 0.0D, -0.5D, icon);
		rb.renderFaceZNeg(jar, -0.5D, 0.0D, -0.5D, icon);
		rb.renderFaceZPos(jar, -0.5D, 0.0D, -0.5D, icon);
		rb.renderFaceXNeg(jar, -0.5D, 0.0D, -0.5D, icon);
		rb.renderFaceXPos(jar, -0.5D, 0.0D, -0.5D, icon);

		v5.draw();

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();

		GL11.glColor3f(1.0F, 1.0F, 1.0F);
	}

}
