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

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelTurbo;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityPylonTurboCharger;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityPylonTurboCharger.Location;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderPylonTurboCharger extends ChromaRenderBase {

	private final ModelTurbo model = new ModelTurbo();

	private static final LightningBolt[][] bolts = new LightningBolt[3][Location.list.length+1];

	static {
		for (int i = 0; i < bolts.length; i++) {
			for (int k = 0; k < bolts[i].length; k++) {
				double px = 0;
				double py = 8;
				double pz = 0;

				Location loc = k == 0 ? null : Location.list[k-1];
				if (loc != null) {
					Coordinate rel = loc.getRelativePylonLocation();
					px = rel.xCoord;
					py = rel.yCoord;
					pz = rel.zCoord;
				}

				DecimalPosition end = new DecimalPosition(px+0.5, py+0.5, pz+0.5);
				LightningBolt b = new LightningBolt(new DecimalPosition(0.5, 0.5, 0.5), end, 4+i*4);
				b.setVariance(0.375);
				b.setVelocity(0.0625);
				bolts[i][k] = b;
			}
		}
	}

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

		BlendMode.DEFAULT.apply();

		GL11.glPushMatrix();
		if (te.isInWorld()) {

		}
		else {
			double s = 1.75;
			GL11.glScaled(s, s, s);
			GL11.glTranslated(0, 0.25, 0);
		}
		if (MinecraftForgeClient.getRenderPass() == 0 || !te.isInWorld() || StructureRenderer.isRenderingTiles())
			this.renderModel(te, model);
		GL11.glPopMatrix();

		BlendMode.ADDITIVEDARK.apply();
		if (te.isInWorld()) {
			if (pass == 1) {
				int tick = te.getTick();

				this.renderConnection(te, par2, par4, par6, par8);
				if (tick > 0) {
					this.renderLightning(te, par2, par4, par6, par8);
				}
				if (te.getSkyTick() > 0) {
					GL11.glPushMatrix();
					GL11.glTranslated(0.5, 8.5, 0.5);
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glEnable(GL11.GL_CULL_FACE);
					double t = ((System.currentTimeMillis()/600D)%360)/30D;
					BlendMode.DEFAULT.apply();
					GL11.glShadeModel(GL11.GL_SMOOTH);
					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/beam.png");
					int h = 16;

					for (int i = 0; i < 512; i += h) {
						GL11.glPushMatrix();
						GL11.glTranslated(0, i, 0);
						DecimalPosition p1 = new DecimalPosition(te);
						DecimalPosition p2 = new DecimalPosition(te).offset(0, h, 0);
						TileEntityCrystalPylon tp = te.getPylon(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
						if (tp != null) {
							ChromaFX.drawEnergyTransferBeam(p1, p2, tp.getColor().getColor(), 0.35, 0.35, 6, t, true);
						}
						GL11.glPopMatrix();
					}

					GL11.glPopAttrib();
					GL11.glPopMatrix();
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
			Coordinate test = end.offset(te.xCoord, te.yCoord, te.zCoord);
			if (ChromaTiles.getTileFromIDandMetadata(test.getBlock(te.worldObj), test.getBlockMetadata(te.worldObj)) == ChromaTiles.PYLONTURBO)
				ChromaFX.renderBeam(0.5, 0.4375, 0.5, end.xCoord+0.5, end.yCoord+0.4375, end.zCoord+0.5, par8, 192, 0.5);
		}
	}

	private void renderLightning(TileEntityPylonTurboCharger te, double par2, double par4, double par6, float par8) {
		for (int f = 0; f < bolts.length; f++) {
			Location loc = te.getLocation();
			int idx = loc != null ? loc.ordinal()+1 : 0;
			LightningBolt b = bolts[f][idx];
			b.update();
			ChromaFX.renderBolt(b, par8, 192, 0.25, 0);
		}
	}

	private void doNonWorldRender(TileEntityPylonTurboCharger te, float par8) {

	}

}
