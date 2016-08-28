package Reika.ChromatiCraft.Render.TESR;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelFluidRelay;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidRelay;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Effects.TruncatedCube;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderFluidRelay extends ChromaRenderBase {

	private final ModelFluidRelay model = new ModelFluidRelay();

	private static final TruncatedCube cube = new TruncatedCube(0.045, 0.125);

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "fluidrelay.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);

		TileEntityFluidRelay te = (TileEntityFluidRelay)tile;

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

		if (MinecraftForgeClient.getRenderPass() == 0 || !te.isInWorld()) {
			GL11.glPushMatrix();
			if (!te.isInWorld()) {
				double s = 3;
				GL11.glScaled(s, s, s);
				GL11.glTranslated(0, 0.3125, 0);
			}
			this.renderModel(te, model);
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}


		if (MinecraftForgeClient.getRenderPass() == 1) {
			int c1 = 0x90909090;
			int c2 = 0xffffffff;

			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			TruncatedCube cube = new TruncatedCube(0.01, 0.125);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDepthMask(false);

			GL11.glPushMatrix();
			GL11.glTranslated(0.5, 0.325, 0.5);
			double ang = (System.currentTimeMillis()/10D+te.hashCode())%360D;
			GL11.glRotated(ang, 0, 1, 0);
			double s = 0.625+0.25*Math.sin(System.currentTimeMillis()/800D-te.hashCode())+0.1*Math.sin(System.currentTimeMillis()/300D-te.hashCode());
			GL11.glScaled(s, s, s);
			float pdist = (float)Minecraft.getMinecraft().thePlayer.getDistance(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
			cube.render(0, 0, 0, c1, c2, true, pdist);
			GL11.glPopMatrix();

			GL11.glPopMatrix();

			GL11.glEnable(GL11.GL_TEXTURE_2D);

			Collection<Coordinate> set = te.getConnections();
			for (Coordinate c : set) {
				TileEntity o = c.getTileEntity(te.worldObj);
				if (o != null && o instanceof TileEntityFluidRelay && o.hashCode() > te.hashCode()) {
					double d = 0.1875;
					TileEntityFluidRelay te2 = (TileEntityFluidRelay)o;
					double x = 0.5+d*te.getFacing().offsetX;
					double y = 0.5+d*te.getFacing().offsetY;
					double z = 0.5+d*te.getFacing().offsetZ;
					double x2 = 0.5+d*te2.getFacing().offsetX;
					double y2 = 0.5+d*te2.getFacing().offsetY;
					double z2 = 0.5+d*te2.getFacing().offsetZ;
					ChromaFX.renderBeam(x, y, z, c.xCoord-te.xCoord+x2, c.yCoord-te.yCoord+y2, c.zCoord-te.zCoord+z2, par8, 240, 0.1875);
				}
			}
		}

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

}
