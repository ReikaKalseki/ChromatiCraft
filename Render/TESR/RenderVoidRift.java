/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Dimension.BlockVoidRift.TileEntityVoidRift;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderVoidRift extends ChromaRenderBase {

	private double[] wave = new double[2];

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
		GL11.glDepthMask(false);
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
			BlockKey b = te.getAt(dir.offsetX, dir.offsetZ);
			if (b.blockID != te.getBlockType()) {
				colors[i-2] = e.getColor();
			}
			else if (b.metadata != e.ordinal()) {
				//blend colors
				CrystalElement e2 = CrystalElement.elements[b.metadata];
				colors[i-2] = ReikaColorAPI.mixColors(e.getColor(), e2.getColor(), 0.5F);
			}
		}
		for (int i = 0; i < 4; i++) {
			Integer color = colors[i];
			//ReikaJavaLibrary.pConsole(color);
			if (color != null) {
				float ang = (float)(0.875F+0.125F*Math.sin(System.currentTimeMillis()/800D));
				color = ReikaColorAPI.getColorWithBrightnessMultiplier(color, ang);
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/voidaura-strip_page.png");
				Tessellator v5 = Tessellator.instance;
				int h = te.HEIGHT;
				long tick = System.currentTimeMillis();

				int hx = (16+((te.xCoord+te.zCoord)%16))%16;

				v5.startDrawingQuads();
				v5.setBrightness(240);

				int f = (int)((tick/32)%128);

				for (int k = 1; k < h; k++) {
					double u = hx/256D+(f%16)/16D;
					double du = u+1/256D;

					double dv = (f/16+1)/8D + (1-(double)k/h)/8D;
					double v = dv-1/128D;

					v5.setColorOpaque_I(color);

					double t = tick/200D;
					double r = 0.03125;

					int y = te.yCoord+k;
					switch(ForgeDirection.VALID_DIRECTIONS[i+2]) {
						case NORTH:
							this.calcWave(te, t, r, te.xCoord, y+1, te.zCoord);
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : wave[0], k+1, te.hasAt(-1, -1) ? 0 : wave[1], u, v);
							this.calcWave(te, t, r, te.xCoord+1, y+1, te.zCoord);
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : 1-wave[0], k+1, te.hasAt(1, -1) ? 0 : wave[1], du, v);
							if (k > 1)
								this.calcWave(te, t, r, te.xCoord+1, y, te.zCoord);
							else this.resetWave();
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : 1-wave[0], k,  te.hasAt(1, -1) ? 0 : wave[1], du, dv);
							if (k > 1)
								this.calcWave(te, t, r, te.xCoord, y, te.zCoord);
							else this.resetWave();
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : wave[0], k,  te.hasAt(-1, -1) ? 0 : wave[1], u, dv);
							break;
						case SOUTH:
							if (k > 1)
								this.calcWave(te, t, r, te.xCoord, y, te.zCoord+1);
							else this.resetWave();
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : wave[0], k,  te.hasAt(-1, 1) ? 1 : 1-wave[1], u, dv);
							if (k > 1)
								this.calcWave(te, t, r, te.xCoord+1, y, te.zCoord+1);
							else this.resetWave();
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : 1-wave[0], k, te.hasAt(1, 1) ? 1 : 1-wave[1], du, dv);
							this.calcWave(te, t, r, te.xCoord+1, y+1, te.zCoord+1);
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : 1-wave[0], k+1, te.hasAt(1, 1) ? 1 : 1-wave[1], du, v);
							this.calcWave(te, t, r, te.xCoord, y+1, te.zCoord+1);
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : wave[0], k+1, te.hasAt(-1, 1) ? 1 : 1-wave[1], u, v);
							break;
						case EAST:
							this.calcWave(te, t, r, te.xCoord+1, y+1, te.zCoord);
							v5.addVertexWithUV(te.hasAt(1, -1) ? 1 : 1-wave[0], k+1, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : wave[1], u, v);
							this.calcWave(te, t, r, te.xCoord+1, y+1, te.zCoord+1);
							v5.addVertexWithUV(te.hasAt(1, 1) ? 1 : 1-wave[0], k+1, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : 1-wave[1], du, v);
							if (k > 1)
								this.calcWave(te, t, r, te.xCoord+1, y, te.zCoord+1);
							else this.resetWave();
							v5.addVertexWithUV(te.hasAt(1, 1) ? 1 : 1-wave[0], k,  colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : 1-wave[1], du, dv);
							if (k > 1)
								this.calcWave(te, t, r, te.xCoord+1, y, te.zCoord);
							else this.resetWave();
							v5.addVertexWithUV(te.hasAt(1, -1) ? 1 : 1-wave[0], k,  colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : wave[1], u, dv);
							break;
						case WEST:
							if (k > 1)
								this.calcWave(te, t, r, te.xCoord, y, te.zCoord);
							else this.resetWave();
							v5.addVertexWithUV(te.hasAt(-1, -1) ? 0 : wave[0], k,  colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : wave[1], u, dv);
							if (k > 1)
								this.calcWave(te, t, r, te.xCoord, y, te.zCoord+1);
							else this.resetWave();
							v5.addVertexWithUV(te.hasAt(-1, 1) ? 0 : wave[0], k,  colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : 1-wave[1], du, dv);
							this.calcWave(te, t, r, te.xCoord, y+1, te.zCoord+1);
							v5.addVertexWithUV(te.hasAt(-1, 1) ? 0 : wave[0], k+1, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : 1-wave[1], du, v);
							this.calcWave(te, t, r, te.xCoord, y+1, te.zCoord);
							v5.addVertexWithUV(te.hasAt(-1, -1) ? 0 : wave[0], k+1, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : wave[1], u, v);
							break;
						default:
							break;
					}

					/*
					switch(ForgeDirection.VALID_DIRECTIONS[i+2]) {
						case NORTH:
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : o[0][1][0], dk, o[0][1][1], u, v);
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : o[1][3][0], dk, o[1][3][1], du, v);
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : o[1][2][0], k,  o[1][2][1], du, dv);
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : o[0][0][0], k,  o[0][0][1], u, dv);
							break;
						case SOUTH:
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : o[2][0][0], k,  o[2][0][1], u, dv);
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : o[3][2][0], k,  o[3][2][1], du, dv);
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : o[3][3][0], dk, o[3][3][1], du, v);
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : o[2][1][0], dk, o[2][1][1], u, v);
							break;
						case EAST:
							v5.addVertexWithUV(o[1][3][0], dk, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : o[1][3][1], u, v);
							v5.addVertexWithUV(o[3][3][0], dk, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : o[3][3][1], du, v);
							v5.addVertexWithUV(o[3][2][0], k,  colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : o[3][2][1], du, dv);
							v5.addVertexWithUV(o[1][2][0], k,  colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : o[1][2][1], u, dv);
							break;
						case WEST:
							v5.addVertexWithUV(o[0][0][0], k,  colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : o[0][0][1], u, dv);
							v5.addVertexWithUV(o[2][0][0], k,  colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : o[2][0][1], du, dv);
							v5.addVertexWithUV(o[2][1][0], dk, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : o[2][2][1], du, v);
							v5.addVertexWithUV(o[0][1][0], dk, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : o[0][1][1], u, v);
							break;
						default:
							break;
					}
					 */

				}

				v5.draw();
			}
		}
	}

	private void calcWave(TileEntityVoidRift te, double t, double r, int x, int y, int z) {
		wave[0] = r*approxSin(y+t)+r*approxCos(y+t/3D);
		wave[1] = r*approxSin(y+t)+r*approxCos(y+t/3D);
	}

	private static double approxSin(double ang) {
		ang = (ang%(2*Math.PI))-Math.PI;
		if (ang < 0)
			return -(1.27323954*ang+0.405284735*ang*ang);
		else
			return -(1.27323954*ang-0.405284735*ang*ang);
	}

	private static double approxCos(double ang) {
		return approxSin(ang+Math.PI/2);
	}

	private void resetWave() {
		wave[0] = wave[1] = 0;
	}
}
