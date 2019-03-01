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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.ChromatiCraft.Items.Tools.ItemEnderCrystal;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class EnderCrystalRenderer implements IItemRenderer {

	private static RenderEnderCrystal renderer = (RenderEnderCrystal)ReikaEntityHelper.getEntityRenderer(EntityEnderCrystal.class);
	private EntityEnderCrystal crystal;

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		World world = Minecraft.getMinecraft().theWorld;
		if (crystal == null) {
			crystal = new EntityChromaEnderCrystal(world);
		}
		int tick = (int)(Minecraft.getMinecraft().theWorld.getTotalWorldTime()%24000);
		crystal.ticksExisted = tick;
		crystal.innerRotation = tick;
		try {
			GL11.glPushMatrix();
			double ax = type == ItemRenderType.ENTITY ? 0 : 0.5;
			double ay = type == ItemRenderType.ENTITY || type == ItemRenderType.INVENTORY ? 0.25 : 0.5;
			double az = type == ItemRenderType.ENTITY ? 0 : 0.5;
			GL11.glTranslated(ax, ay, az);
			double sc = type == ItemRenderType.ENTITY ? 0.25 : 0.5;
			GL11.glScaled(sc, sc, sc);
			double dx = -0.5;
			double dy = -0.75;
			double dz = -0.5;
			GL11.glTranslated(dx, dy, dz);
			this.renderBedrock(255, 255, 255);
			GL11.glTranslated(-dx, -dy, -dz);
			if (((ItemEnderCrystal)item.getItem()).canPlaceCrystal(item)) {
				renderer.doRender(crystal, 0, 0, 0, 0, 0);
			}
			else {
				GL11.glScaled(1.5, 0.5, 1.5);
				GL11.glTranslated(dx, dy, dz);
				GL11.glTranslated(0, -0.8, 0);
				//GL11.glColor4f(0.5F, 0.5F, 1F, 1F);
				this.renderBedrock(190, 190, 255);
			}
			GL11.glPopMatrix();
		}
		catch (Exception e) { //randomly thrown by bindTexture
			e.printStackTrace();
		}
	}

	private void renderBedrock(int r, int g, int b) {
		Tessellator v5 = Tessellator.instance;
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = Blocks.bedrock.getIcon(0, 0);
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();
		v5.setColorOpaque(r, g, b);
		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 1, 1, u, dv);
		v5.addVertexWithUV(1, 1, 1, du, dv);
		v5.addVertexWithUV(1, 1, 0, du, v);
		v5.addVertexWithUV(0, 1, 0, u, v);

		v5.setNormal(0, 0F, 0);
		v5.addVertexWithUV(0, 0, 0, u, dv);
		v5.addVertexWithUV(1, 0, 0, du, dv);
		v5.addVertexWithUV(1, 0, 1, du, v);
		v5.addVertexWithUV(0, 0, 1, u, v);

		v5.setNormal(0, 0.6F, 0);
		v5.addVertexWithUV(1, 0, 0, u, dv);
		v5.addVertexWithUV(0, 0, 0, du, dv);
		v5.addVertexWithUV(0, 1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, u, v);

		v5.setNormal(0, 0.6F, 0);
		v5.addVertexWithUV(0, 0, 1, u, dv);
		v5.addVertexWithUV(1, 0, 1, du, dv);
		v5.addVertexWithUV(1, 1, 1, du, v);
		v5.addVertexWithUV(0, 1, 1, u, v);

		v5.setNormal(0, 0.3F, 0);
		v5.addVertexWithUV(1, 0, 1, u, dv);
		v5.addVertexWithUV(1, 0, 0, du, dv);
		v5.addVertexWithUV(1, 1, 0, du, v);
		v5.addVertexWithUV(1, 1, 1, u, v);

		v5.setNormal(0, 0.3F, 0);
		v5.addVertexWithUV(0, 0, 0, u, dv);
		v5.addVertexWithUV(0, 0, 1, du, dv);
		v5.addVertexWithUV(0, 1, 1, du, v);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.draw();
	}
}
