package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockGravityTile.GravityTarget;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderGravityTarget extends ChromaRenderBase {

	//private final RandomVariance randomX = new RandomVariance(0.0625, 0.0625, 0.03125/32D);
	//private final RandomVariance randomY = new RandomVariance(0.03125, 0.0625, 0.03125/32D);
	//private final RandomVariance randomZ = new RandomVariance(0.0625, 0.0625, 0.03125/32D);

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();

		GravityTarget te = (GravityTarget)tile;
		GL11.glTranslated(par2, par4, par6);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);
		ReikaRenderHelper.disableEntityLighting();
		BlendMode.ADDITIVEDARK.apply();

		double t = System.currentTimeMillis();

		boolean flag = false;
		if (te.timer != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(0.5, te.getBlockType().getBlockBoundsMaxY()+0.0125, 0.5);
			GL11.glRotated(90, 1, 0, 0);
			te.timer.render(0.5, par8);
			GL11.glPopMatrix();
			flag = true;
		}

		GL11.glPushMatrix();
		//GL11.glTranslated(0.5, 0.75, 0.5);
		double s = 0.75;
		if (flag) {
			s = 0.375;
			/*
			double f = Math.min(1, (double)te.timer.getTotalTick()/te.timer.getTotalDuration());
			if (f >= 0.75)
				s = 0.375+0.375*4*(f-0.75);
			else if (f < 0.0625/2)
				s = 0.375+(0.0625/2-f)*16*2*0.375;
			 */
		}
		else {
			/*
			double shake = 8*(te.getFillFraction()-1);
			if (shake > 0) {
				randomX.update();
				randomY.update();
				randomZ.update();
				GL11.glTranslated(shake*randomX.getValue(), shake*randomY.getValue(), shake*randomZ.getValue());
			}
			 */
		}
		GL11.glTranslated(0.5, te.getBlockType().getBlockBoundsMaxY()+0.0125, 0.5);
		GL11.glRotated(90, 1, 0, 0);

		s *= 0.125+te.getFillFraction();
		GL11.glScaled(s, s, s);
		//RenderManager rm = RenderManager.instance;
		//GL11.glRotatef(180-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		//GL11.glRotatef(-rm.playerViewX, 1.0F, 0.0F, 0.0F);

		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.HOLE.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(te.getColor().getColor(), te.renderAlpha/255F));
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.draw();
		GL11.glPopMatrix();

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}
