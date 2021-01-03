/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Item;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Items.Tools.ItemStructureMap;
import Reika.DragonAPI.Instantiable.Rendering.MultiSheetItemRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;


public class StructureMapRenderer implements IItemRenderer {

	private final MultiSheetItemRenderer baseRender;

	public StructureMapRenderer(MultiSheetItemRenderer ref) {
		baseRender = ref;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return baseRender.shouldUseRenderHelper(type, item, helper);//helper == ItemRendererHelper.ENTITY_BOBBING;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			float ptick = ReikaRenderHelper.getPartialTickTime();
			AbstractClientPlayer e = (AbstractClientPlayer)data[1];
			GL11.glLoadIdentity();
			this.prepareRender(ptick, e);
			this.drawBackground();

			ItemStructureMap.renderMap(item, ptick, e);

			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glPopAttrib();
		}
		else {
			baseRender.renderItem(type, item, data);
		}
	}

	private void prepareRender(float ptick, AbstractClientPlayer e) {
		float f1 = 1;//this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * ptick;
		float f13 = 0.8F;
		float f2 = e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * ptick;
		float f5 = e.getSwingProgress(ptick);
		float f6 = MathHelper.sin(f5 * (float)Math.PI);
		float f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI);
		GL11.glTranslatef(-f7 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI * 2.0F) * 0.2F, -f6 * 0.2F);
		f5 = 1.0F - f2 / 45.0F + 0.1F;

		if (f5 < 0.0F)
		{
			f5 = 0.0F;
		}

		if (f5 > 1.0F)
		{
			f5 = 1.0F;
		}

		f5 = -MathHelper.cos(f5 * (float)Math.PI) * 0.5F + 0.5F;
		GL11.glTranslatef(0.0F, 0.0F * f13 - (1.0F - f1) * 1.2F - f5 * 0.5F + 0.04F, -0.9F * f13);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(f5 * -85.0F, 0.0F, 0.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		Minecraft.getMinecraft().getTextureManager().bindTexture(e.getLocationSkin());

		for (int i1 = 0; i1 < 2; ++i1)
		{
			int j1 = i1 * 2 - 1;
			GL11.glPushMatrix();
			GL11.glTranslatef(-0.0F, -0.6F, 1.1F * j1);
			GL11.glRotatef(-45 * j1, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-65 * j1, 0.0F, 1.0F, 0.0F);
			RenderPlayer renderplayer = (RenderPlayer)RenderManager.instance.getEntityRenderObject(e);
			renderplayer.renderFirstPersonArm(e);
			GL11.glPopMatrix();
		}

		f6 = e.getSwingProgress(ptick);
		f7 = MathHelper.sin(f6 * f6 * (float)Math.PI);
		float f8 = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI);
		GL11.glRotatef(-f7 * 20.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-f8 * 20.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(-f8 * 80.0F, 1.0F, 0.0F, 0.0F);
		float f9 = 0.38F;
		GL11.glScalef(f9, f9, f9);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
		float f10 = 0.015625F;
		GL11.glScalef(f10, f10, f10);
	}

	private void drawBackground() {
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/StructureMap/bcg.png");
		Tessellator tessellator = Tessellator.instance;
		GL11.glNormal3f(0.0F, 0.0F, -1.0F);
		tessellator.startDrawingQuads();
		byte b0 = 7;
		tessellator.addVertexWithUV(0 - b0, 128 + b0, 0.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(128 + b0, 128 + b0, 0.0D, 1.0D, 1.0D);
		tessellator.addVertexWithUV(128 + b0, 0 - b0, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(0 - b0, 0 - b0, 0.0D, 0.0D, 0.0D);
		tessellator.draw();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

}
