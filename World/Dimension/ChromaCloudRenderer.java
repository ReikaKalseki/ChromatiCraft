/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaCloudRenderer extends IRenderHandler {

	public static final ChromaCloudRenderer instance = new ChromaCloudRenderer();

	private ChromaCloudRenderer() {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float ptick, WorldClient world, Minecraft mc) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		float playerY = (float)(mc.renderViewEntity.lastTickPosY + (mc.renderViewEntity.posY - mc.renderViewEntity.lastTickPosY) * ptick);
		Tessellator tessellator = Tessellator.instance;
		float f2 = 12.0F;
		float cloudTickCounter = world.getTotalWorldTime();
		double slide = cloudTickCounter + ptick;
		double posX = (mc.renderViewEntity.prevPosX + (mc.renderViewEntity.posX - mc.renderViewEntity.prevPosX) * ptick + slide * 0.03D) / f2;
		double posZ = (mc.renderViewEntity.prevPosZ + (mc.renderViewEntity.posZ - mc.renderViewEntity.prevPosZ) * ptick) / f2 + 0.33D;
		float f4 = world.provider.getCloudHeight() - playerY + 0.33F;
		int i = MathHelper.floor_double(posX / 2048.0D);
		int j = MathHelper.floor_double(posZ / 2048.0D);
		posX -= i * 2048;
		posZ -= j * 2048;
		//mc.renderEngine.bindTexture(locationCloudsPng);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/clouds/dimension.png");
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		Vec3 vec3 = world.getCloudColour(ptick);
		float red = (float)vec3.xCoord;
		float green = (float)vec3.yCoord;
		float blue = (float)vec3.zCoord;

		if (mc.gameSettings.anaglyph) {
			float f8 = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			float f9 = (red * 30.0F + green * 70.0F) / 100.0F;
			float f10 = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = f8;
			green = f9;
			blue = f10;
		}

		float f8 = (float)(posX * 0.0D);
		float f9 = (float)(posZ * 0.0D);
		float f10 = 1/256F;
		f8 = MathHelper.floor_double(posX) * f10;
		f9 = MathHelper.floor_double(posZ) * f10;
		float floatX = (float)(posX - MathHelper.floor_double(posX));
		float floatZ = (float)(posZ - MathHelper.floor_double(posZ));
		byte b0 = 8;
		byte b1 = 4;
		float f13 = 0;
		GL11.glScalef(f2, 1.0F, f2);

		for (int k = 0; k < 2; ++k)
		{
			if (k == 0)
			{
				GL11.glColorMask(false, false, false, false);
			}
			else if (mc.gameSettings.anaglyph)
			{
				if (EntityRenderer.anaglyphField == 0)
				{
					GL11.glColorMask(false, true, true, true);
				}
				else
				{
					GL11.glColorMask(true, false, false, true);
				}
			}
			else
			{
				GL11.glColorMask(true, true, true, true);
			}

			for (int dx = -b1 + 1; dx <= b1; ++dx)
			{
				for (int dz = -b1 + 1; dz <= b1; ++dz)
				{
					tessellator.startDrawingQuads();
					float f14 = dx * b0;
					float f15 = dz * b0;
					float f16 = f14 - floatX;
					float f17 = f15 - floatZ;
					double sx = f16-mc.renderViewEntity.posX;
					double sz = f17-mc.renderViewEntity.posZ;
					double f3 = 4+2*Math.sin((sx*sx+sz*sz)/10000F);

					if (f4 > -f3 - 1.0F)
					{
						tessellator.setColorRGBA_F(red * 0.7F, green * 0.7F, blue * 0.7F, 0.8F);
						tessellator.setNormal(0.0F, -1.0F, 0.0F);
						tessellator.addVertexWithUV(f16 + 0.0F, f4 + 0.0F, f17 + b0, (f14 + 0.0F) * f10 + f8, (f15 + b0) * f10 + f9);
						tessellator.addVertexWithUV(f16 + b0, f4 + 0.0F, f17 + b0, (f14 + b0) * f10 + f8, (f15 + b0) * f10 + f9);
						tessellator.addVertexWithUV(f16 + b0, f4 + 0.0F, f17 + 0.0F, (f14 + b0) * f10 + f8, (f15 + 0.0F) * f10 + f9);
						tessellator.addVertexWithUV(f16 + 0.0F, f4 + 0.0F, f17 + 0.0F, (f14 + 0.0F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
					}

					if (f4 <= f3 + 1.0F)
					{
						tessellator.setColorRGBA_F(red, green, blue, 0.8F);
						tessellator.setNormal(0.0F, 1.0F, 0.0F);
						tessellator.addVertexWithUV(f16 + 0.0F, f4 + f3 - f13, f17 + b0, (f14 + 0.0F) * f10 + f8, (f15 + b0) * f10 + f9);
						tessellator.addVertexWithUV(f16 + b0, f4 + f3 - f13, f17 + b0, (f14 + b0) * f10 + f8, (f15 + b0) * f10 + f9);
						tessellator.addVertexWithUV(f16 + b0, f4 + f3 - f13, f17 + 0.0F, (f14 + b0) * f10 + f8, (f15 + 0.0F) * f10 + f9);
						tessellator.addVertexWithUV(f16 + 0.0F, f4 + f3 - f13, f17 + 0.0F, (f14 + 0.0F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
					}

					tessellator.setColorRGBA_F(red * 0.9F, green * 0.9F, blue * 0.9F, 0.8F);
					int j1;

					if (dx > -1)
					{
						tessellator.setNormal(-1.0F, 0.0F, 0.0F);

						for (j1 = 0; j1 < b0; ++j1)
						{
							tessellator.addVertexWithUV(f16 + j1 + 0.0F, f4 + 0.0F, f17 + b0, (f14 + j1 + 0.5F) * f10 + f8, (f15 + b0) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 0.0F, f4 + f3, f17 + b0, (f14 + j1 + 0.5F) * f10 + f8, (f15 + b0) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 0.0F, f4 + f3, f17 + 0.0F, (f14 + j1 + 0.5F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 0.0F, f4 + 0.0F, f17 + 0.0F, (f14 + j1 + 0.5F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
						}
					}

					if (dx <= 1)
					{
						tessellator.setNormal(1.0F, 0.0F, 0.0F);

						for (j1 = 0; j1 < b0; ++j1)
						{
							tessellator.addVertexWithUV(f16 + j1 + 1.0F - f13, f4 + 0.0F, f17 + b0, (f14 + j1 + 0.5F) * f10 + f8, (f15 + b0) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 1.0F - f13, f4 + f3, f17 + b0, (f14 + j1 + 0.5F) * f10 + f8, (f15 + b0) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 1.0F - f13, f4 + f3, f17 + 0.0F, (f14 + j1 + 0.5F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 1.0F - f13, f4 + 0.0F, f17 + 0.0F, (f14 + j1 + 0.5F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
						}
					}

					tessellator.setColorRGBA_F(red * 0.8F, green * 0.8F, blue * 0.8F, 0.8F);

					if (dz > -1)
					{
						tessellator.setNormal(0.0F, 0.0F, -1.0F);

						for (j1 = 0; j1 < b0; ++j1)
						{
							tessellator.addVertexWithUV(f16 + 0.0F, f4 + f3, f17 + j1 + 0.0F, (f14 + 0.0F) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + b0, f4 + f3, f17 + j1 + 0.0F, (f14 + b0) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + b0, f4 + 0.0F, f17 + j1 + 0.0F, (f14 + b0) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + 0.0F, f4 + 0.0F, f17 + j1 + 0.0F, (f14 + 0.0F) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
						}
					}

					if (dz <= 1)
					{
						tessellator.setNormal(0.0F, 0.0F, 1.0F);

						for (j1 = 0; j1 < b0; ++j1)
						{
							tessellator.addVertexWithUV(f16 + 0.0F, f4 + f3, f17 + j1 + 1.0F - f13, (f14 + 0.0F) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + b0, f4 + f3, f17 + j1 + 1.0F - f13, (f14 + b0) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + b0, f4 + 0.0F, f17 + j1 + 1.0F - f13, (f14 + b0) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + 0.0F, f4 + 0.0F, f17 + j1 + 1.0F - f13, (f14 + 0.0F) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
						}
					}

					tessellator.draw();
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

}
