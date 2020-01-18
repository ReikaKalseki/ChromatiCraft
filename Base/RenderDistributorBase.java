package Reika.ChromatiCraft.Base;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public abstract class RenderDistributorBase extends ChromaRenderBase {

	protected abstract double pulsationSpeed();

	protected double pulsationAmplitude() {
		return 0.0125;
	}

	public abstract int getColor();

	protected final void renderHalo(TileEntityChromaticBase te, double par2, double par4, double par6, float ptick) {
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glPushMatrix();

		double h = 0.2875;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.LATTICE.getIcon();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_LIGHTING);
		//RenderManager rm = RenderManager.instance;
		//GL11.glTranslated(0.5, h, 0.5);
		//GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		//GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.setBrightness(240);

		v5.setColorOpaque_I(this.getColor());

		double s = this.pulsationSpeed();
		if (s > 0) {
			double[] out = new double[6];
			double a = this.pulsationAmplitude();
			for (int i = 0; i < 6; i++) {
				out[i] = a+a*Math.sin(Math.toRadians((te.getTicksExisted()+ptick)*s+i*60D));
			}

			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], du, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], du, v);
			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);

			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], u, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, v);
			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], u, v);

			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, dv);
			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, v);
			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);

			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, v);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);

			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], u, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], du, v);
			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);

			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], u, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
			v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], du, v);
			v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);
		}
		else {
			v5.addVertexWithUV(0, 1, 0, u, dv);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(0, 0, 0, u, v);

			v5.addVertexWithUV(0, 1, 1, u, dv);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(1, 0, 1, du, v);
			v5.addVertexWithUV(0, 0, 1, u, v);

			v5.addVertexWithUV(0, 1, 0, u, dv);
			v5.addVertexWithUV(0, 1, 1, du, dv);
			v5.addVertexWithUV(0, 0, 1, du, v);
			v5.addVertexWithUV(0, 0, 0, u, v);

			v5.addVertexWithUV(1, 1, 0, u, dv);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(1, 0, 1, du, v);
			v5.addVertexWithUV(1, 0, 0, u, v);

			v5.addVertexWithUV(0, 1, 1, u, dv);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(1, 1, 0, du, v);
			v5.addVertexWithUV(0, 1, 0, u, v);

			v5.addVertexWithUV(0, 0, 1, u, dv);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(0, 0, 0, u, v);
		}
		v5.draw();

		BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
