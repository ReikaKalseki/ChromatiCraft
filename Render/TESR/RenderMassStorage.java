package Reika.ChromatiCraft.Render.TESR;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMassStorage;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class RenderMassStorage extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityMassStorage te = (TileEntityMassStorage)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1)
			this.renderItem(te, par8);
		GL11.glPopMatrix();
	}

	private void renderItem(TileEntityMassStorage te, float par8) {
		EntityItem ei = te.getFilterItemRender();
		if (ei != null && ReikaItemHelper.matchStackWithBlock(ei.getEntityItem(), Blocks.stone))
			ei = null;
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 1.25, 0.5);
		double s = 1;
		float tick = par8+te.getTicksExisted();
		double ang = 45*Math.sin(Math.toRadians(tick*3));
		if (ei == null) {
			s *= 0.5;
		}
		GL11.glScaled(s, s, s);
		GL11.glRotated(ang, 0, 1, 0);
		if (ei != null) {
			double s2 = s;//0.25;
			ei.age = 0;
			ei.hoverStart = 0;
			ei.rotationYaw = 0;
			//for (double dt = -0.0625; dt <= 0.0625; dt += 0.0625) {
			boolean blend = true;//Math.abs(dt) < 0.125;
			//double s = 2;
			GL11.glScaled(s, s, s2);
			//GL11.glTranslated(0, 0, dt);
			GL11.glEnable(GL11.GL_BLEND);
			double c = 0.2;
			double a = blend ? 1 : 0.125;
			GL11.glColor4d(c, c, c, a);
			if (blend) {
				//GL11.glDisable(GL11.GL_CULL_FACE);
				//GL11.glDisable(GL11.GL_DEPTH_TEST);
				BlendMode.ADDITIVE.apply();
			}
			ReikaRenderHelper.disableEntityLighting();
			RenderItem.renderInFrame = true;
			RenderManager.instance.renderEntityWithPosYaw(ei, 0, 0, 0, 0, 0/*tick*/);
			RenderItem.renderInFrame = false;
			ReikaRenderHelper.enableEntityLighting();
			if (blend) {
				BlendMode.DEFAULT.apply();
				//GL11.glEnable(GL11.GL_DEPTH_TEST);
				//GL11.glEnable(GL11.GL_CULL_FACE);
			}

			GL11.glDisable(GL11.GL_BLEND);
			//}
		}
		else {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4d(1, 1, 1, 1);
			GL11.glTranslated(-0.5, 0, 0);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			ReikaRenderHelper.disableEntityLighting();
			ReikaRenderHelper.disableLighting();
			BlendMode.ADDITIVEDARK.apply();
			ReikaTextureHelper.bindTerrainTexture();
			Tessellator v5 = Tessellator.instance;
			IIcon ico = ChromaIcons.QUESTION.getIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);
			//v5.addVertexWithUV(-s, -s, 0, du, dv);
			//v5.addVertexWithUV(s, -s, 0, u, dv);
			//v5.addVertexWithUV(s, s, 0, u, v);
			//v5.addVertexWithUV(-s, s, 0, du, v);
			ItemRenderer.renderItemIn2D(v5, u, v, du, dv, 256, 256, 0.0625F);
			GL11.glPopAttrib();
		}
		GL11.glPopMatrix();
	}

}
