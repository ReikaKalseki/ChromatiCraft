/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectJar;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumBlockHandler;

public class AspectJarRenderer extends ChromaRenderBase {

	private static Class jarModelClass;
	private static Method modelRender;
	private static Field liquidIcon;

	private ModelBase model;

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
		try {
			this.renderAspects(te);
			this.renderJar(te);
		}
		catch (Exception e) {
			throw new RuntimeException("Could not render ThaumCraft jar model!");
		}
		GL11.glPopMatrix();
	}

	private void renderJar(TileEntityAspectJar te) throws Exception {
		this.bindTextureByName("/Reika/ChromatiCraft/Textures/TileEntity/jar.png");
		modelRender.invoke(model);
	}

	private void renderAspects(TileEntityAspectJar te) throws Exception {
		if (te.getAmount() <= 0)
			return;
		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		RenderBlocks rb = RenderBlocks.getInstance();

		GL11.glDisable(2896);

		double level = 0.625*Math.sqrt((double)te.getAmount()/te.CAPACITY);//ReikaMathLibrary.logbase2(te.getAmount())/ReikaMathLibrary.logbase2(te.CAPACITY);

		Tessellator v5 = Tessellator.instance;
		rb.setRenderBounds(0.25, 0.0625, 0.25, 0.75, 0.0625 + level, 0.75);
		v5.startDrawingQuads();
		if (te.getAspect() != null) {
			v5.setColorOpaque_I(te.getAspect().getColor());
		}
		int bright = 200;
		Block jar = ThaumBlockHandler.getInstance().jarID;
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

		GL11.glEnable(2896);
		GL11.glPopMatrix();

		GL11.glColor3f(1.0F, 1.0F, 1.0F);
	}

}
