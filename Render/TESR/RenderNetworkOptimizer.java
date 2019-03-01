package Reika.ChromatiCraft.Render.TESR;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityNetworkOptimizer;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderNetworkOptimizer extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityNetworkOptimizer te = (TileEntityNetworkOptimizer)tile;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDepthMask(false);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		if (MinecraftForgeClient.getRenderPass() == 1 || !te.isInWorld() || StructureRenderer.isRenderingTiles())
			this.drawInner(te, par8);

		GL11.glDisable(GL11.GL_BLEND);
		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawInner(TileEntityNetworkOptimizer te, float par8) {
		ReikaTextureHelper.bindTerrainTexture();
		double sz = te.isInWorld() ? 2.5 : 1.25;
		float br = 1;

		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		double s = te.isInWorld() ? 0.4375 : 0.33;
		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(s, s, s);
			if (StructureRenderer.isRenderingTiles()) {
				GL11.glRotated(StructureRenderer.getRenderRY(), 0, 1, 0);
				GL11.glRotated(-StructureRenderer.getRenderRX(), 1, 0, 0);
			}
			else {
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			}
		}
		else {
			s = 0.5;
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			GL11.glScaled(s, s, s);
		}

		//double ang = (System.currentTimeMillis()/20D)%360;
		//GL11.glRotated(ang, 0, 0, 1);

		boolean valid = te.hasStructure() || !te.isInWorld() || StructureRenderer.isRenderingTiles();

		double t = te.isInWorld() ? (te.getTicksExisted()+par8) : System.currentTimeMillis()/50D;

		double s2 = 1.5+0.25*Math.sin(t/32D);

		IIcon ico = valid ? ChromaIcons.COLORWHIRL.getIcon() : ChromaIcons.BIGFLARE.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		float uu = du-u;
		float vv = dv-v;

		double s3 = s2*sz;

		GL11.glPushMatrix();
		GL11.glScaled(s3, s3, s3);
		//GL11.glRotated((-System.currentTimeMillis()/50D)%360D, 0, 0, 1);
		v5.startDrawingQuads();
		//v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, br));
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
		GL11.glPopMatrix();

		if (valid) {
			int c = 0xffffff;//ReikaColorAPI.getModifiedHue(0xff0000, (int)(((System.currentTimeMillis()/5)%360)));
			br = 0.75F;
			ico = ChromaIcons.COLORWHIRLFLARE.getIcon();//ChromaIcons.SUNFLARE.getIcon();
			u = ico.getMinU();
			v = ico.getMinV();
			du = ico.getMaxU();
			dv = ico.getMaxV();
			sz = te.isInWorld() ? 4.5 : 1.5;
			s3 = s2*sz;
			GL11.glPushMatrix();
			GL11.glScaled(s3, s3, s3);
			//GL11.glRotated((-System.currentTimeMillis()/50D)%360D, 0, 0, 1);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, br));
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();

		GL11.glPopMatrix();
	}

}
