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

import java.util.Collection;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.BlockRouterNode.TileEntityRouterNode;
import Reika.ChromatiCraft.Models.ModelRouter;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRouterHub;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRouterHub.Highlight;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;


public class RenderRouterHub extends ChromaRenderBase {

	private final ModelRouter model = new ModelRouter();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "router.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityRouterHub te = (TileEntityRouterHub)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glPushMatrix();
		if (te.isInWorld()) {
			switch(te.getFacing()) {
				case NORTH:
					GL11.glTranslated(0, 1, 0);
					GL11.glRotated(90, 1, 0, 0);
					break;
				case SOUTH:
					GL11.glTranslated(0, 0, 1);
					GL11.glRotated(-90, 1, 0, 0);
					break;
				case WEST:
					GL11.glTranslated(0, 1, 0);
					GL11.glRotated(-90, 0, 0, 1);
					break;
				case EAST:
					GL11.glTranslated(1, 0, 0);
					GL11.glRotated(90, 0, 0, 1);
					break;
				case UP:
					GL11.glTranslated(0, 1, 1);
					GL11.glRotated(180, 1, 0, 0);
					break;
				case DOWN:
				default:
					break;
			}
		}
		this.renderModel(te, model);
		GL11.glPopMatrix();

		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDepthMask(false);

			Tessellator v5 = Tessellator.instance;

			/*
			Collection<Coordinate> li = te.getExtractionLocations();
			if (!li.isEmpty()) {
				for (Coordinate c : li) {
					int a = (int)(255*Math.sin((te.getTicksExisted()+c.hashCode())/16D));
					if (a > 0) {
						DecimalPosition d = new DecimalPosition(c).offset(-0.5, -0.5, -0.5);
						d = d.offset(((TileEntityRouterNode)c.getTileEntity(te.worldObj)).getSide(), -0.5).offset(-te.xCoord, -te.yCoord, -te.zCoord);
						ChromaFX.renderBeam(0.5, 0.75, 0.5, d.xCoord+0.5, d.yCoord+0.5, d.zCoord+0.5, par8, a, 0.1875);
					}
				}
			}

			li = te.getInsertionLocations();
			if (!li.isEmpty()) {
				for (Coordinate c : li) {
					int a = (int)(255*Math.sin((te.getTicksExisted()+c.xCoord*2+c.zCoord)/16D));
					if (a > 0) {
						DecimalPosition d = new DecimalPosition(c).offset(-0.5, -0.5, -0.5);
						d = d.offset(((TileEntityRouterNode)c.getTileEntity(te.worldObj)).getSide(), -0.5).offset(-te.xCoord, -te.yCoord, -te.zCoord);
						ChromaFX.renderBeam(0.5, 0.75, 0.5, d.xCoord+0.5, d.yCoord+0.5, d.zCoord+0.5, par8, a, 0.1875);
					}
				}
			}
			 */

			Collection<Highlight> li = te.getHighlightLocations();
			Iterator<Highlight> it = li.iterator();
			ForgeDirection dir = te.getFacing();
			double ox = 0.5-dir.offsetX*0.3125;
			double oy = 0.5-dir.offsetY*0.3125;
			double oz = 0.5-dir.offsetZ*0.3125;
			while (it.hasNext()) {
				Highlight h = it.next();
				Coordinate c = h.location;
				TileEntity node = c.getTileEntity(te.worldObj);
				if (node != null) {
					int a = (int)(255*ReikaMathLibrary.cosInterpolation(0, h.lifespan, h.age));
					if (a > 0) {
						DecimalPosition d = new DecimalPosition(c).offset(-0.5, -0.5, -0.5);
						d = d.offset(((TileEntityRouterNode)node).getSide(), -0.5).offset(-te.xCoord, -te.yCoord, -te.zCoord);
						ChromaFX.renderBeam(ox, oy, oz, d.xCoord+0.5, d.yCoord+0.5, d.zCoord+0.5, par8, a, 0.1875);
					}
					h.age++;
					if (h.age >= h.lifespan)
						it.remove();
				}
			}


			GL11.glPopAttrib();
		}
		GL11.glPopMatrix();
	}

}
