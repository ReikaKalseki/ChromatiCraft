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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Dimension.BlockVoidRift.TileEntityVoidRift;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderVoidRift extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityVoidRift te = (TileEntityVoidRift)tile;
		if (MinecraftForgeClient.getRenderPass() != 1)
			return;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		this.renderAura(te);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void renderAura(TileEntityVoidRift te) {
		CrystalElement e = te.getColor();
		Integer[] colors = new Integer[4];
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			int dx = te.xCoord+dir.offsetX;
			int dy = te.yCoord+dir.offsetY;
			int dz = te.zCoord+dir.offsetZ;
			Block b = te.worldObj.getBlock(dx, dy, dz);
			int meta = te.worldObj.getBlockMetadata(dx, dy, dz);
			if (b != te.getBlockType()) {
				colors[i-2] = e.getColor();
			}
			else if (meta != e.ordinal()) {
				//blend colors
				CrystalElement e2 = CrystalElement.elements[meta];
				colors[i-2] = ReikaColorAPI.mixColors(e.getColor(), e2.getColor(), 0.5F);
			}
		}
		for (int i = 0; i < 4; i++) {
			Integer color = colors[i];
			//ReikaJavaLibrary.pConsole(color);
			if (color != null) {
				float a = (float)(0.875F+0.125F*Math.sin(System.currentTimeMillis()/800D));
				color = ReikaColorAPI.getColorWithBrightnessMultiplier(color, a);
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/voidaura-strip_page.png");
				Tessellator v5 = Tessellator.instance;
				int h = te.HEIGHT;
				long tick = System.currentTimeMillis();

				int hx = (16+((te.xCoord+te.zCoord)%16))%16;

				v5.startDrawingQuads();
				v5.setBrightness(240);

				int f = (int)((tick/32)%128);

				for (int k = 1; k < h; k++) {
					int dk = k+1;

					double u = hx/256D+(f%16)/16D;
					double du = u+1/256D;

					double dv = (f/16+1)/8D + (1-(double)k/h)/8D;
					double v = dv-1/128D;

					v5.setColorOpaque_I(color);

					double t = tick/200D;
					double r = 0.03125;
					double w0 = this.calcWave(te, k-1, t, r);
					double w = this.calcWave(te, k, t, r);
					double w2 = this.calcWave(te, k+1, t, r);

					double o1a = (w+w0)/2;
					double o1b = (w+w2)/2;
					double o2a = 1-o1a;
					double o2b = 1-o1b;

					if (k == 1) {
						o1a = 0;
						o2a = 1;
					}

					switch(ForgeDirection.VALID_DIRECTIONS[i+2]) {
					case NORTH:
						v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : o1b, dk, o1b, u, v);
						v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : o2b, dk, o1b, du, v);
						v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : o2a, k, o1a, du, dv);
						v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : o1a, k, o1a, u, dv);
						break;
					case SOUTH:
						v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : o1a, k, o2a, u, dv);
						v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : o2a, k, o2a, du, dv);
						v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : o2b, dk, o2b, du, v);
						v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : o1b, dk, o2b, u, v);
						break;
					case EAST:
						v5.addVertexWithUV(o2b, dk, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : o1b, u, v);
						v5.addVertexWithUV(o2b, dk, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : o2b, du, v);
						v5.addVertexWithUV(o2a, k, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : o2a, du, dv);
						v5.addVertexWithUV(o2a, k, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : o1a, u, dv);
						break;
					case WEST:
						v5.addVertexWithUV(o1a, k, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : o1a, u, dv);
						v5.addVertexWithUV(o1a, k, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : o2a, du, dv);
						v5.addVertexWithUV(o1b, dk, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : o2b, du, v);
						v5.addVertexWithUV(o1b, dk, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : o1b, u, v);
						break;
					default:
						break;
					}

				}

				v5.draw();
			}
		}
	}

	private double calcWave(TileEntityVoidRift te, int k, double t, double r) {
		return r*Math.sin(k+t)+r*Math.cos(k+t/3D);
	}
}
