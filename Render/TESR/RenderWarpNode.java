/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockWarpNode.TileEntityWarpNode;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.DragonAPI.Instantiable.IO.RemoteSourcedAsset;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderWarpNode extends ChromaRenderBase {

	private final RemoteSourcedAsset texture = ChromaClient.dynamicAssets.createAsset("Textures/warpnode-small.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityWarpNode te = (TileEntityWarpNode)tile;

		if (!tile.hasWorldObj() || MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles()) {
			ReikaTextureHelper.bindTexture(texture);
			int idx = (int)(System.currentTimeMillis()/20%64);
			double u = idx%8/8D;
			double v = idx/8/8D;
			double du = u+1/8D;
			double dv = v+1/8D;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			Tessellator v5 = Tessellator.instance;

			GL11.glTranslated(0.5, 0.5, 0.5);

			GL11.glPushMatrix();
			double t = (System.currentTimeMillis()/2000D+te.hashCode())%360;
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			double dist = ep.getDistance(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);

			boolean flag = te.isOpen() && ProgressStage.WARPNODE.isPlayerAtStage(ep);
			double sch = this.getDisplayDistance(ep, flag);
			if (flag && ChromaItems.TOOL.matchWith(ep.getCurrentEquippedItem())) {
				sch *= 2;
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			}
			double d = sch == Double.POSITIVE_INFINITY ? 0 : Math.max(0, dist-sch);
			float f = sch == Double.POSITIVE_INFINITY ? 1 : 0.5F+0.5F*(float)(1-d/8D);
			f = MathHelper.clamp_float(f, 0, 1);
			double s = 0.125+0.5*f+0.125*Math.sin(t);
			s *= 4;

			if (te.hasWorldObj()) {
				float val = 0;
				if (dist <= 3) {
					val = 1;
				}
				else if (dist <= 12) {
					val = 1-(float)((dist-3D)/9D);
				}
				float val2 = 0;
				if (te.isOpen()) {
					if (dist <= 1) {
						val2 = 1;
					}
					else if (dist <= 3) {
						val2 = 1-(float)((dist-1D)/2D);
					}
				}
				//if (dist >= 0.875 && !ReikaEntityHelper.isLookingAt(ep, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5))
				//	val = 0;
				ChromaShaders shd = te.isOpen() ? ChromaShaders.WARPNODE_OPEN : ChromaShaders.WARPNODE;
				shd.clearOnRender = true;
				shd.setIntensity(val);
				shd.getShader().setFocus(te);
				shd.getShader().setMatricesToCurrent();
				shd.getShader().setField("distance", dist*dist);
				shd.getShader().setField("scale", s);
				shd.getShader().setField("washout", val2);
			}

			GL11.glScaled(s, s, s);
			if (StructureRenderer.isRenderingTiles()) {
				GL11.glRotated(-StructureRenderer.getRenderRY(), 0, 1, 0);
				GL11.glRotated(-StructureRenderer.getRenderRX(), 1, 0, 0);
			}
			else {
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			}

			int c = ReikaColorAPI.mixColors(0xffffff, 0x000000, f);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

			GL11.glPopMatrix();

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	private double getDisplayDistance(EntityPlayer ep, boolean incr) {
		if (ProgressStage.CTM.isPlayerAtStage(ep))
			return Double.POSITIVE_INFINITY;
		if (ProgressStage.STRUCTCOMPLETE.isPlayerAtStage(ep))
			return 864;
		else if (ProgressStage.DIMENSION.isPlayerAtStage(ep))
			return 768;
		else if (ProgressStage.LINK.isPlayerAtStage(ep))
			return incr ? 512 : 384;
		else if (ProgressStage.ALLCOLORS.isPlayerAtStage(ep) || ProgressStage.CHARGE.isPlayerAtStage(ep))
			return incr ? 256 : 192;
		else if (ProgressStage.PYLON.isPlayerAtStage(ep) || ProgressStage.CRYSTALS.isPlayerAtStage(ep) || ProgressStage.TOWER.isPlayerAtStage(ep))
			return incr ? 128 : 96;
		return incr ? 48 : 32;
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

}
