package Reika.ChromatiCraft.Render.TESR.Dimension;

import java.util.Collection;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Dimension.Structure.Water.BlockRotatingLock.TileEntityRotatingLock;
import Reika.ChromatiCraft.Models.ModelWaterLock;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class RenderWaterLock extends ChromaRenderBase {

	private final ModelWaterLock model = new ModelWaterLock();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityRotatingLock te = (TileEntityRotatingLock)tile;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4+0.95, par6);
		this.bindTextureByName(this.getTextureFolder()+"waterlock.png");
		this.preRenderModel();
		//double s = 0.85; //clipping avoidance
		//GL11.glScaled(s, s, s);
		Collection<ForgeDirection> li = ReikaJavaLibrary.makeSetFromArray(ForgeDirection.VALID_DIRECTIONS);
		li.remove(ForgeDirection.UP);
		li.remove(ForgeDirection.DOWN);
		li.removeAll(te.getOpenEndsForRender());
		for (ForgeDirection dir : li) {
			GL11.glPushMatrix();
			GL11.glRotated(ReikaDirectionHelper.getHeading(dir)+te.getRotationProgress(), 0, 1, 0);
			model.renderChannel(te);
			GL11.glPopMatrix();
		}
		for (int i = 2; i < 6; i++) {
			GL11.glPushMatrix();
			GL11.glRotated(ReikaDirectionHelper.getHeading(ForgeDirection.VALID_DIRECTIONS[i])+te.getRotationProgress(), 0, 1, 0);
			model.renderSegment(te);
			GL11.glPopMatrix();
		}
		this.postRenderModel(tile);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

}
