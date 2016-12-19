package Reika.ChromatiCraft.Render.TESR;

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.TileEntity.TileEntityDataNode;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderDataNode extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityDataNode te = (TileEntityDataNode)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2+0.5, par4+0.5, par6+0.5);

		Tessellator v5 = Tessellator.instance;

		if (MinecraftForgeClient.getRenderPass() == 0 || StructureRenderer.isRenderingTiles() || !te.isInWorld()) {

			//ReikaTextureHelper.bindTerrainTexture();
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/datanode.png");
			int l = te.isInWorld() ? te.getBlockType().getMixedBrightnessForBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord) : 240;

			double h = 1.5;
			double R = 0.125;
			double r = 0.03125/2;
			double dy = 0;

			double af = 1D/Math.sin(Math.toRadians(60));

			//IIcon ico = ChromaBlocks.PYLONSTRUCT.getBlockInstance().getIcon(0, 0);
			float iw = 64;
			float ih = 96;

			float u1a = 2.5F/iw;
			float v1a = 31/ih;
			float u1b = 9.5F/iw;
			float v1b = 17/ih;
			float u2a = 46/iw;
			float v2a = 31/ih;
			float u2b = 38/iw;
			float v2b = 17/ih;

			//float u = ico.getMinU();
			//float du = ico.getMaxU();
			//float v = ico.getMinV();
			//float dv = ico.getMaxV();
			//float uu = du-u;
			//float vv = dv-v;

			double w = R*af*1.5+r;
			double oc = R*af*0.5;

			v5.startDrawingQuads();
			v5.setBrightness(l);

			v5.setColorOpaque_F(1, 1, 1);
			v5.setNormal(0, 1, 0);

			v5.addVertexWithUV(-R*af, dy+h, R, u1b, v1b);
			v5.addVertexWithUV(R*af, dy+h, R, u2b, v2b);
			v5.addVertexWithUV(w, dy+h, 0, u2a, v2a);
			v5.addVertexWithUV(-w, dy+h, 0, u1a, v1a);

			v5.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			v5.setNormal(0, 0.5F, 0);

			v5.addVertexWithUV(-w, dy, 0, u1a, v1a);
			v5.addVertexWithUV(w, dy, 0, u2a, v2a);
			v5.addVertexWithUV(R*af, dy, R, u2b, v2b);
			v5.addVertexWithUV(-R*af, dy, R, u1b, v1b);

			v5.setColorOpaque_F(0.675F, 0.675F, 0.675F);
			v5.setNormal(0, 0.675F, 0);

			u1a = 3/iw;
			v1a = 34/ih;
			u1b = 3/iw;
			v1b = 94/ih;
			u2a = 45/iw;
			v2a = 34/ih;
			u2b = 45/iw;
			v2b = 94/ih;
			float u16a = 31/iw;
			float v16a = 34/ih;
			float u16b = 31/iw;
			float v16b = 94/ih;
			float u13a = 17/iw;
			float v13a = 34/ih;
			float u13b = 17/iw;
			float v13b = 94/ih;

			v5.addVertexWithUV(w, dy+h, 0, u2a, v2a);
			v5.addVertexWithUV(w, dy, 0, u2b, v2b);
			v5.addVertexWithUV(-w, dy, 0, u1b, v1b);
			v5.addVertexWithUV(-w, dy+h, 0, u1a, v1a);

			v5.addVertexWithUV(-R*af, dy+h, R, u1a, v1a);
			v5.addVertexWithUV(-R*af, dy, R, u1b, v1b);
			v5.addVertexWithUV(R*af, dy, R, u16b, v16b);
			v5.addVertexWithUV(R*af, dy+h, R, u16a, v16a);

			v5.addVertexWithUV(-w, dy+h, 0, u13a, v13a);
			v5.addVertexWithUV(-w, dy, 0, u13b, v13b);
			v5.addVertexWithUV(-R*af, dy, R, u1b, v1b);
			v5.addVertexWithUV(-R*af, dy+h, R, u1a, v1a);

			v5.addVertexWithUV(R*af, dy+h, R, u1a, v1a);
			v5.addVertexWithUV(R*af, dy, R, u1b, v1b);
			v5.addVertexWithUV(w, dy, 0, u13b, v13b);
			v5.addVertexWithUV(w, dy+h, 0, u13a, v13a);

			/*
			v5.addVertexWithUV(-oc, 0, 0, u, dv);
			v5.addVertexWithUV(-oc, h, 0, u, v);
			v5.addVertexWithUV(oc, h, 0, du, v);
			v5.addVertexWithUV(oc, 0, 0, du, dv);

			v5.addVertexWithUV(-oc+r+R*af, 0, 0, u, dv);
			v5.addVertexWithUV(-oc+r+R*af, h, 0, u, v);
			v5.addVertexWithUV(oc+r+R*af, h, 0, du, v);
			v5.addVertexWithUV(oc+r+R*af, 0, 0, du, dv);

			v5.addVertexWithUV(-oc-r-R*af, 0, 0, u, dv);
			v5.addVertexWithUV(-oc-r-R*af, h, 0, u, v);
			v5.addVertexWithUV(oc-r-R*af, h, 0, du, v);
			v5.addVertexWithUV(oc-r-R*af, 0, 0, du, dv);

			v5.addVertexWithUV(r/2+R*af, 0, R, u, dv);
			v5.addVertexWithUV(r/2+R*af, h, R, u, v);
			v5.addVertexWithUV(r/2, h, R, du, v);
			v5.addVertexWithUV(r/2, 0, R, du, dv);

			v5.addVertexWithUV(-r/2, 0, R, du, dv);
			v5.addVertexWithUV(-r/2, h, R, du, v);
			v5.addVertexWithUV(-r/2-R*af, h, R, u, v);
			v5.addVertexWithUV(-r/2-R*af, 0, R, u, dv);

			//Row 2
			v5.addVertexWithUV(r/2, 0, R+r/2, du, dv);
			v5.addVertexWithUV(r/2, h, R+r/2, du, v);
			v5.addVertexWithUV(r/2+R*af, h, R+r/2, u, v);
			v5.addVertexWithUV(r/2+R*af, 0, R+r/2, u, dv);

			v5.addVertexWithUV(-r/2-R*af, 0, R+r/2, u, dv);
			v5.addVertexWithUV(-r/2-R*af, h, R+r/2, u, v);
			v5.addVertexWithUV(-r/2, h, R+r/2, du, v);
			v5.addVertexWithUV(-r/2, 0, R+r/2, du, dv);

			v5.addVertexWithUV(oc, 0, R*2+r/2, du, dv);
			v5.addVertexWithUV(oc, h, R*2+r/2, du, v);
			v5.addVertexWithUV(-oc, h, R*2+r/2, u, v);
			v5.addVertexWithUV(-oc, 0, R*2+r/2, u, dv);

			//Angled panels
			v5.addVertexWithUV(-R*af-r, 0, R, u, dv);
			v5.addVertexWithUV(-R*af-r, h, R, u, v);
			v5.addVertexWithUV(-w, h, 0, du, v);
			v5.addVertexWithUV(-w, 0, 0, du, dv);

			v5.addVertexWithUV(0, 0, R, u, dv);
			v5.addVertexWithUV(0, h, R, u, v);
			v5.addVertexWithUV(-oc, h, 0, du, v);
			v5.addVertexWithUV(-oc, 0, 0, du, dv);

			v5.addVertexWithUV(0, 0, R, u, dv);
			v5.addVertexWithUV(0, h, R, u, v);
			v5.addVertexWithUV(-oc, h, 0, du, v);
			v5.addVertexWithUV(-oc, 0, 0, du, dv);

			v5.addVertexWithUV(R*af+r, 0, R, u, dv);
			v5.addVertexWithUV(R*af+r, h, R, u, v);
			v5.addVertexWithUV(oc+r, h, 0, du, v);
			v5.addVertexWithUV(oc+r, 0, 0, du, dv);

			v5.addVertexWithUV(oc+r/2, 0, 0, du, dv);
			v5.addVertexWithUV(oc+r/2, h, 0, du, v);
			v5.addVertexWithUV(R*af+r/2, h, R, u, v);
			v5.addVertexWithUV(R*af+r/2, 0, R, u, dv);

			v5.addVertexWithUV(-oc-r/2, 0, 0, du, dv);
			v5.addVertexWithUV(-oc-r/2, h, 0, du, v);
			v5.addVertexWithUV(-r/2, h, R, u, v);
			v5.addVertexWithUV(-r/2, 0, R, u, dv);

			v5.addVertexWithUV(w, 0, 0, du, dv);
			v5.addVertexWithUV(w, h, 0, du, v);
			v5.addVertexWithUV(R*af+r, h, R, u, v);
			v5.addVertexWithUV(R*af+r, 0, R, u, dv);

			v5.addVertexWithUV(oc, 0, 0, du, dv);
			v5.addVertexWithUV(oc, h, 0, du, v);
			v5.addVertexWithUV(0, h, R, u, v);
			v5.addVertexWithUV(0, 0, R, u, dv);

			v5.addVertexWithUV(-oc-r, 0, 0, du, dv);
			v5.addVertexWithUV(-oc-r, h, 0, du, v);
			v5.addVertexWithUV(-R*af-r, h, R, u, v);
			v5.addVertexWithUV(-R*af-r, 0, R, u, dv);

			v5.addVertexWithUV(-R*af-r/2, 0, R, u, dv);
			v5.addVertexWithUV(-R*af-r/2, h, R, u, v);
			v5.addVertexWithUV(-oc-r/2, h, 0, du, v);
			v5.addVertexWithUV(-oc-r/2, 0, 0, du, dv);

			v5.addVertexWithUV(r/2, 0, R, u, dv);
			v5.addVertexWithUV(r/2, h, R, u, v);
			v5.addVertexWithUV(oc+r/2, h, 0, du, v);
			v5.addVertexWithUV(oc+r/2, 0, 0, du, dv);
			v5.draw();

			v5.startDrawing(GL11.GL_TRIANGLES);
			//Top
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(l);
			v5.setNormal(0, 1, 0);

			//Row 1
			v5.addVertexWithUV(-R*af-r, dy+h, R, u, v);
			v5.addVertexWithUV(-oc-r, dy+h, 0, u+uu/3, dv);
			v5.addVertexWithUV(-w, dy+h, 0, u, dv);

			v5.addVertexWithUV(w, dy+h, 0, du, dv);
			v5.addVertexWithUV(oc+r, dy+h, 0, u+2*uu/3, dv);
			v5.addVertexWithUV(R*af+r, dy+h, R, du, v);

			v5.addVertexWithUV(-r/2, dy+h, R, u+uu/2, v);
			v5.addVertexWithUV(-oc-r/2, dy+h, 0, u+uu/3, dv);
			v5.addVertexWithUV(-R*af-r/2, dy+h, R, u, v);

			v5.addVertexWithUV(-oc, dy+h, 0, u+uu/3, dv);
			v5.addVertexWithUV(0, dy+h, R, u+uu/2, v);
			v5.addVertexWithUV(oc, dy+h, 0, u+2*uu/3, dv);

			v5.addVertexWithUV(R*af+r/2, dy+h, R, u, v);
			v5.addVertexWithUV(oc+r/2, dy+h, 0, u+2*uu/3, dv);
			v5.addVertexWithUV(r/2, dy+h, R, u+uu/2, v);

			//Row 2
			v5.addVertexWithUV(-R*af-r/2, dy+h, R+r/2, u, dv);
			v5.addVertexWithUV(-oc-r/2, dy+h, 2*R+r/2, u, v);
			v5.addVertexWithUV(-r/2, dy+h, R+r/2, u+uu/2, dv);

			v5.addVertexWithUV(oc, dy+h, 2*R+r/2, du, v);
			v5.addVertexWithUV(0, dy+h, R+r/2, u+uu/2, dv);
			v5.addVertexWithUV(-oc, dy+h, 2*R+r/2, u, v);

			v5.addVertexWithUV(r/2, dy+h, R+r/2, u+uu/2, dv);
			v5.addVertexWithUV(oc+r/2, dy+h, 2*R+r/2, du, v);
			v5.addVertexWithUV(R*af+r/2, dy+h, R+r/2, du, dv);

			//Bottom
			v5.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			v5.setNormal(0, 0.5F, 0);
			v5.addVertexWithUV(-w, dy, 0, u, dv);
			v5.addVertexWithUV(-oc-r, dy, 0, u+uu/3, dv);
			v5.addVertexWithUV(-R*af-r, dy, R, u, v);

			v5.addVertexWithUV(R*af+r, dy, R, du, v);
			v5.addVertexWithUV(oc+r, dy, 0, u+2*uu/3, dv);
			v5.addVertexWithUV(w, dy, 0, du, dv);

			v5.addVertexWithUV(-R*af-r/2, dy, R, u, v);
			v5.addVertexWithUV(-oc-r/2, dy, 0, u+uu/3, dv);
			v5.addVertexWithUV(-r/2, dy, R, u+uu/2, v);

			v5.addVertexWithUV(oc, dy, 0, u+2*uu/3, dv);
			v5.addVertexWithUV(0, dy, R, u+uu/2, v);
			v5.addVertexWithUV(-oc, dy, 0, u+uu/3, dv);

			v5.addVertexWithUV(r/2, dy, R, u+uu/2, v);
			v5.addVertexWithUV(oc+r/2, dy, 0, u+2*uu/3, dv);
			v5.addVertexWithUV(R*af+r/2, dy, R, u, v);

			//Row 2
			v5.addVertexWithUV(-r/2, dy, R+r/2, u+uu/2, dv);
			v5.addVertexWithUV(-oc-r/2, dy, 2*R+r/2, u, v);
			v5.addVertexWithUV(-R*af-r/2, dy, R+r/2, u, dv);

			v5.addVertexWithUV(-oc, dy, 2*R+r/2, u, v);
			v5.addVertexWithUV(0, dy, R+r/2, u+uu/2, dv);
			v5.addVertexWithUV(oc, dy, 2*R+r/2, du, v);

			v5.addVertexWithUV(R*af+r/2, dy, R+r/2, du, dv);
			v5.addVertexWithUV(oc+r/2, dy, 2*R+r/2, du, v);
			v5.addVertexWithUV(r/2, dy, R+r/2, u+uu/2, dv);

			 */
			v5.draw();

		}
		if (MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles() || !te.isInWorld()) {

			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_TEXTURE_2D);

			double s = 0.125;
			double h = te.isInWorld() ? 2 : 1;
			double dy = te.isInWorld() ? te.getExtension1()+te.getExtension2()+0.0625*Math.sin((te.getTicksExisted()/8D)%(2*Math.PI)) : -0.5;
			int c1 = 0xffffff;
			int c2 = 0x909090;

			GL11.glTranslated(0, dy, 0);
			/*
		v5.startDrawingQuads();
		v5.setColorOpaque_I(c);
		v5.addVertex(s, 0, -s/2);
		v5.addVertex(s, h, -s/2);
		v5.addVertex(s, h, s/2);
		v5.addVertex(s, 0, s/2);
		v5.draw();
			 */

			double r1 = 2*s*0.875;
			double r2 = 2*s;

			double[] da = {90, 15, 15};
			double[] ra = {r1, r2, r1};
			int i = 0;
			double a0 = 45+(te.isInWorld() ? te.getTicksExisted()*2 : System.currentTimeMillis()/20D)%360;

			ArrayList<DecimalPosition> li = new ArrayList();

			for (double a = a0; a <= a0+360; a += da[i]) {
				double r = ra[i];
				double dx = r*Math.cos(Math.toRadians(a));
				double dz = r*Math.sin(Math.toRadians(a));
				li.add(new DecimalPosition(dx, 0, dz));
				i = (i+1)%da.length;
			}

			v5.startDrawing(GL11.GL_LINE_LOOP);
			v5.setColorOpaque_I(c1);
			for (DecimalPosition p : li) {
				v5.addVertex(p.xCoord, p.yCoord, p.zCoord);
			}
			v5.draw();

			v5.startDrawing(GL11.GL_LINE_LOOP);
			v5.setColorOpaque_I(c1);
			for (DecimalPosition p : li) {
				v5.addVertex(p.xCoord, p.yCoord+h, p.zCoord);
			}
			v5.draw();

			v5.startDrawing(GL11.GL_LINES);
			v5.setColorOpaque_I(c1);
			for (DecimalPosition p : li) {
				v5.addVertex(p.xCoord, p.yCoord, p.zCoord);
				v5.addVertex(p.xCoord, p.yCoord+h, p.zCoord);
			}
			v5.draw();

			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			v5.setColorOpaque_I(c2);
			v5.addVertex(0, 0, 0);
			for (DecimalPosition p : li) {
				v5.addVertex(p.xCoord, p.yCoord, p.zCoord);
			}
			v5.draw();

			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			v5.setColorOpaque_I(c2);
			v5.addVertex(0, h, 0);
			for (int idx = li.size()-1; idx >= 0; idx--) {
				DecimalPosition p = li.get(idx);
				v5.addVertex(p.xCoord, p.yCoord+h, p.zCoord);
			}
			v5.draw();

			v5.startDrawingQuads();
			v5.setColorOpaque_I(c2);
			for (int idx = 0; idx < li.size(); idx++) {
				DecimalPosition p1 = li.get(idx);
				DecimalPosition p2 = li.get((idx+1)%li.size());
				v5.addVertex(p1.xCoord, p1.yCoord, p1.zCoord);
				v5.addVertex(p1.xCoord, p1.yCoord+h, p1.zCoord);
				v5.addVertex(p2.xCoord, p2.yCoord+h, p2.zCoord);
				v5.addVertex(p2.xCoord, p2.yCoord, p2.zCoord);
			}
			v5.draw();

			/*
		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorOpaque_I(c1);

		for (double a = a0; a <= a0+360; a += da[i]) {
			double r = ra[i];
			double dx = r*Math.cos(Math.toRadians(a));
			double dz = r*Math.sin(Math.toRadians(a));
			v5.addVertex(dx, 0, dz);
			i = (i+1)%da.length;
		}
		v5.draw();

		/*
		i = 0;
		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorOpaque_I(c1);
		for (double a = a0; a <= a0+360; a += da[i]) {
			double r = ra[i];
			double dx = r*Math.cos(Math.toRadians(a));
			double dz = r*Math.sin(Math.toRadians(a));
			v5.addVertex(dx, h, dz);
			i = (i+1)%da.length;
		}
		v5.draw();

		/*
		i = 0;
		v5.startDrawing(GL11.GL_LINES);
		v5.setColorOpaque_I(c1);
		for (double a = a0; a <= a0+360; a += da[i]) {
			double r = ra[i];
			double dx = r*Math.cos(Math.toRadians(a));
			double dz = r*Math.sin(Math.toRadians(a));
			v5.addVertex(dx, 0, dz);
			v5.addVertex(dx, h, dz);
			i = (i+1)%da.length;
		}
		v5.draw();
			 */

			GL11.glPopAttrib();
		}


		GL11.glPopMatrix();
	}

}
