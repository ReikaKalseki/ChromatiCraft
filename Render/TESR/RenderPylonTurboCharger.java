package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelTurbo;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.TileEntityPylonTurboCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityPylonTurboCharger.Location;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class RenderPylonTurboCharger extends ChromaRenderBase {

	private final ModelTurbo model = new ModelTurbo();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "turbo.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityPylonTurboCharger te = (TileEntityPylonTurboCharger)tile;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		int pass = MinecraftForgeClient.getRenderPass();
		GL11.glEnable(GL11.GL_BLEND);
		ReikaRenderHelper.disableLighting();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);

		GL11.glPushMatrix();
		if (te.isInWorld()) {

		}
		else {
			double s = 1.75;
			GL11.glScaled(s, s, s);
			GL11.glTranslated(0, 0.25, 0);
		}
		this.renderModel(te, model);
		GL11.glPopMatrix();

		BlendMode.ADDITIVEDARK.apply();
		if (te.isInWorld()) {
			if (pass == 1) {
				int tick = te.getTick();

				tick = 500;

				this.renderConnection(te, par2, par4, par6, par8);
				if (tick > 0) {
					this.renderLightning(te, par2, par4, par6, par8);
				}
			}
		}
		else {
			this.doNonWorldRender(te, par8);
		}
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void renderConnection(TileEntityPylonTurboCharger te, double par2, double par4, double par6, float par8) {
		Location loc = te.getLocation();
		if (loc != null) {
			Location nx = loc.getNext();
			Coordinate end = nx.position.offset(loc.position.negate());
			this.renderBeam(0.5, 0.4375, 0.5, end.xCoord+0.5, end.yCoord+0.4375, end.zCoord+0.5, par8, 192, 0.5);
		}
	}

	private static void renderBeam(double x1, double y1, double z1, double x2, double y2, double z2, float par8, int a, double h) {
		Tessellator v5 = Tessellator.instance;

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		v5.startDrawing(GL11.GL_LINES);
		v5.setBrightness(240);
		v5.setColorRGBA_I(0xffffff, 255);

		v5.addVertex(x1, y1, z1);
		v5.addVertex(x2, y2, z2);

		v5.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.LASER.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		double dd = ReikaMathLibrary.py3d(x2-x1, y2-y1, z2-z1);
		int n = (int)(dd);

		GL11.glPushMatrix();
		//GL11.glRotated(ang, 0, 0, 0);
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorRGBA_I(ReikaColorAPI.GStoHex(a), a);
		for (double d = 0; d < n; d++) {
			double nx = x1+(x2-x1)*d/n;
			double ny = y1+(y2-y1)*d/n;
			double nz = z1+(z2-z1)*d/n;

			double px = x1+(x2-x1)*(d+1)/n;
			double py = y1+(y2-y1)*(d+1)/n;
			double pz = z1+(z2-z1)*(d+1)/n;

			v5.addVertexWithUV(nx, ny-h, nz, u, v);
			v5.addVertexWithUV(nx, ny+h, nz, u, dv);
			v5.addVertexWithUV(px, py+h, pz, du, dv);
			v5.addVertexWithUV(px, py-h, pz, du, v);

			v5.addVertexWithUV(nx-h, ny, nz, u, v);
			v5.addVertexWithUV(nx+h, ny, nz, u, dv);
			v5.addVertexWithUV(px+h, py, pz, du, dv);
			v5.addVertexWithUV(px-h, py, pz, du, v);

			v5.addVertexWithUV(nx, ny, nz-h, u, v);
			v5.addVertexWithUV(nx, ny, nz+h, u, dv);
			v5.addVertexWithUV(px, py, pz+h, du, dv);
			v5.addVertexWithUV(px, py, pz-h, du, v);
		}
		v5.draw();
		GL11.glPopMatrix();
	}

	private void renderLightning(TileEntityPylonTurboCharger te, double par2, double par4, double par6, float par8) {
		for (int f = 0; f < 3; f++) {
			int n = 4+f*4;
			double d = 0.375;

			double px = te.xCoord;
			double py = te.yCoord+9;
			double pz = te.zCoord;

			Location loc = te.getLocation();
			if (loc != null) {
				Coordinate rel = loc.getRelativePylonLocation().offset(te.xCoord, te.yCoord, te.zCoord);
				px = rel.xCoord;
				py = rel.yCoord+0.5;
				pz = rel.zCoord;
			}

			double dx = (px-te.xCoord)/n;
			double dy = (py-te.yCoord)/n;
			double dz = (pz-te.zCoord)/n;

			DecimalPosition[] pos = new DecimalPosition[n];
			for (int i = 0; i < n; i++) {
				pos[i] = new DecimalPosition(0.5+dx*i, 0.5+dy*i, 0.5+dz*i);

				if (i > 0 && i < n-1) {
					double rx = ReikaRandomHelper.getRandomPlusMinus(0, d);
					double rz = ReikaRandomHelper.getRandomPlusMinus(0, d);
					pos[i] = pos[i].offset(rx, 0, rz);
				}
			}

			for (int i = 0; i < pos.length-1; i++) {
				this.renderBeam(pos[i].xCoord, pos[i].yCoord, pos[i].zCoord, pos[i+1].xCoord, pos[i+1].yCoord, pos[i+1].zCoord, par8, 192, 0.25);
			}
		}
	}

	private void doNonWorldRender(TileEntityPylonTurboCharger te, float par8) {

	}

}
