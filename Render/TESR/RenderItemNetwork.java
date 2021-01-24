package Reika.ChromatiCraft.Render.TESR;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.CrystalTransmitterRender;
import Reika.ChromatiCraft.Models.ModelNetworkTransport;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityNetworkItemTransporter;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;


public class RenderItemNetwork extends CrystalTransmitterRender {

	private final ModelNetworkTransport model = new ModelNetworkTransport();

	@Override
	public final String getImageFileName(RenderFetcher te) {
		return "networktransport.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		super.renderTileEntityAt(tile, par2, par4, par6, par8);

		TileEntityNetworkItemTransporter te = (TileEntityNetworkItemTransporter)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			this.renderSprites(te, par8);
			this.renderItem(te, par8);
		}
		GL11.glPopMatrix();
	}

	private void renderSprites(TileEntityNetworkItemTransporter te, float par8) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		ReikaTextureHelper.bindTerrainTexture();
		BlendMode.ADDITIVEDARK.apply();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glDepthMask(false);

		Tessellator v5 = Tessellator.instance;

		v5.startDrawingQuads();
		ReikaTextureHelper.bindTerrainTexture();
		IIcon[] icons = {
				Blocks.glowstone.blockIcon,
				ChromaIcons.ECLIPSEFLARE.getIcon(),
				ChromaIcons.RINGS.getIcon(),
		};
		for (int i = 0; i < icons.length; i++) {
			IIcon ico = icons[i];
			double u = ico.getMinU();
			double v = ico.getMinV();
			double du = ico.getMaxU();
			double dv = ico.getMaxV();
			double s = 0.375;
			v5.setColorRGBA_I(0xffffff, 255);
			v5.addVertexWithUV(0.5-s, 0.19+i*0.01, 0.5+s, u, dv);
			v5.addVertexWithUV(0.5+s, 0.19+i*0.01, 0.5+s, du, dv);
			v5.addVertexWithUV(0.5+s, 0.19+i*0.01, 0.5-s, du, v);
			v5.addVertexWithUV(0.5-s, 0.19+i*0.01, 0.5-s, u, v);
		}

		v5.draw();

		float f11 = 0.76F;
		GL11.glColor4f(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
		ReikaTextureHelper.bindEnchantmentTexture();
		double u = -(te.getTicksExisted()+par8)*0.03;
		double u2 = (te.getTicksExisted()+par8)*0.01;
		double du = u+1;
		double v = 0;
		double dv = v+1;
		v5.startDrawingQuads();
		double s = 0.065;
		v5.addVertexWithUV(0.5-s, 0.76, 0.5+s, u2, 1);
		v5.addVertexWithUV(0.5+s, 0.76, 0.5+s, u2, 1);
		v5.addVertexWithUV(0.5+s, 0.76, 0.5-s, u2, 0);
		v5.addVertexWithUV(0.5-s, 0.76, 0.5-s, u2, 0);

		v5.addVertexWithUV(0.5+s, 0.125, 0.5-s, u, v);
		v5.addVertexWithUV(0.5+s, 0.76, 0.5-s, du, v);
		v5.addVertexWithUV(0.5+s, 0.76, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5+s, 0.125, 0.5+s, u, dv);

		v5.addVertexWithUV(0.5-s, 0.125, 0.5+s, u, dv);
		v5.addVertexWithUV(0.5-s, 0.76, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5-s, 0.76, 0.5-s, du, v);
		v5.addVertexWithUV(0.5-s, 0.125, 0.5-s, u, v);

		v5.addVertexWithUV(0.5-s, 0.125, 0.5-s, u, v);
		v5.addVertexWithUV(0.5-s, 0.76, 0.5-s, du, v);
		v5.addVertexWithUV(0.5+s, 0.76, 0.5-s, du, dv);
		v5.addVertexWithUV(0.5+s, 0.125, 0.5-s, u, dv);

		v5.addVertexWithUV(0.5+s, 0.125, 0.5+s, u, dv);
		v5.addVertexWithUV(0.5+s, 0.76, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5-s, 0.76, 0.5+s, du, v);
		v5.addVertexWithUV(0.5-s, 0.125, 0.5+s, u, v);
		v5.draw();

		GL11.glPopAttrib();
	}

	private void renderItem(TileEntityNetworkItemTransporter te, float par8) {
		EntityItem ei = te.getEntityItem();
		if (ei != null) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			double s = 1;
			double s2 = s;//0.25;
			float tick = par8+te.getTicksExisted();
			ei.age = 0;
			ei.hoverStart = 0;
			ei.rotationYaw = 0;
			//for (double dt = -0.0625; dt <= 0.0625; dt += 0.0625) {
			GL11.glPushMatrix();
			//double s = 2;
			GL11.glTranslated(0.5, 0.75+te.itemOffset, 0.5);
			GL11.glRotated(te.itemRotation, 0, 1, 0);
			GL11.glScaled(s, s, s2);
			//GL11.glTranslated(0, 0, dt);
			GL11.glEnable(GL11.GL_BLEND);
			double c = 0.2;
			GL11.glColor4d(c, c, c, 1);
			BlendMode.ADDITIVE.apply();
			ReikaRenderHelper.disableEntityLighting();
			RenderItem.renderInFrame = true;
			RenderManager.instance.renderEntityWithPosYaw(ei, 0, 0, 0, 0, 0/*tick*/);
			RenderItem.renderInFrame = false;
			GL11.glPopAttrib();
			GL11.glPopMatrix();
			//}
		}
	}

}
