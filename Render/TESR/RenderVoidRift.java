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

					double[] a;

					int y = te.yCoord+k;
					switch(ForgeDirection.VALID_DIRECTIONS[i+2]) {
						case NORTH:
							a = this.calcWave(te, t, r, te.xCoord, y+1, te.zCoord);
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : a[0], k+1, te.hasAt(-1, -1) ? 0 : a[1], u, v);
							a = this.calcWave(te, t, r, te.xCoord+1, y+1, te.zCoord);
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : 1-a[0], k+1, te.hasAt(1, -1) ? 0 : a[1], du, v);
							a = k > 1 ? this.calcWave(te, t, r, te.xCoord+1, y, te.zCoord) : new double[]{0, 0};
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : 1-a[0], k,  te.hasAt(1, -1) ? 0 : a[1], du, dv);
							a = k > 1 ? this.calcWave(te, t, r, te.xCoord, y, te.zCoord) : new double[]{0, 0};
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : a[0], k,  te.hasAt(-1, -1) ? 0 : a[1], u, dv);
							break;
						case SOUTH:
							a = k > 1 ? this.calcWave(te, t, r, te.xCoord, y, te.zCoord+1) : new double[]{0, 0};
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : a[0], k,  te.hasAt(-1, 1) ? 1 : 1-a[1], u, dv);
							a = k > 1 ? this.calcWave(te, t, r, te.xCoord+1, y, te.zCoord+1) : new double[]{0, 0};
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : 1-a[0], k, te.hasAt(1, 1) ? 1 : 1-a[1], du, dv);
							a = this.calcWave(te, t, r, te.xCoord+1, y+1, te.zCoord+1);
							v5.addVertexWithUV(colors[ForgeDirection.EAST.ordinal()-2] == null ? 1 : 1-a[0], k+1, te.hasAt(1, 1) ? 1 : 1-a[1], du, v);
							a = this.calcWave(te, t, r, te.xCoord, y+1, te.zCoord+1);
							v5.addVertexWithUV(colors[ForgeDirection.WEST.ordinal()-2] == null ? 0 : a[0], k+1, te.hasAt(-1, 1) ? 1 : 1-a[1], u, v);
							break;
						case EAST:
							a = this.calcWave(te, t, r, te.xCoord+1, y+1, te.zCoord);
							v5.addVertexWithUV(te.hasAt(1, -1) ? 1 : 1-a[0], k+1, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : a[1], u, v);
							a = this.calcWave(te, t, r, te.xCoord+1, y+1, te.zCoord+1);
							v5.addVertexWithUV(te.hasAt(1, 1) ? 1 : 1-a[0], k+1, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : 1-a[1], du, v);
							a = k > 1 ? this.calcWave(te, t, r, te.xCoord+1, y, te.zCoord+1) : new double[]{0, 0};
							v5.addVertexWithUV(te.hasAt(1, 1) ? 1 : 1-a[0], k,  colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : 1-a[1], du, dv);
							a = k > 1 ? this.calcWave(te, t, r, te.xCoord+1, y, te.zCoord) : new double[]{0, 0};
							v5.addVertexWithUV(te.hasAt(1, -1) ? 1 : 1-a[0], k,  colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : a[1], u, dv);
							break;
						case WEST:
							a = k > 1 ? this.calcWave(te, t, r, te.xCoord, y, te.zCoord) : new double[]{0, 0};
							v5.addVertexWithUV(te.hasAt(-1, -1) ? 0 : a[0], k,  colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : a[1], u, dv);
							a = k > 1 ? this.calcWave(te, t, r, te.xCoord, y, te.zCoord+1) : new double[]{0, 0};
							v5.addVertexWithUV(te.hasAt(-1, 1) ? 0 : a[0], k,  colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : 1-a[1], du, dv);
							a = this.calcWave(te, t, r, te.xCoord, y+1, te.zCoord+1);
							v5.addVertexWithUV(te.hasAt(-1, 1) ? 0 : a[0], k+1, colors[ForgeDirection.SOUTH.ordinal()-2] == null ? 1 : 1-a[1], du, v);
							a = this.calcWave(te, t, r, te.xCoord, y+1, te.zCoord);
							v5.addVertexWithUV(te.hasAt(-1, -1) ? 0 : a[0], k+1, colors[ForgeDirection.NORTH.ordinal()-2] == null ? 0 : a[1], u, v);
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

	private double[] calcWave(TileEntityVoidRift te, double t, double r, int x, int y, int z) {
		return new double[]{r*Math.sin(y+t)+r*Math.cos(y+t/3D), r*Math.sin(y+t)+r*Math.cos(y+t/3D)};
	}
}
