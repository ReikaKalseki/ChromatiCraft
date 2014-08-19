package Reika.ChromatiCraft.Render;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelCrystalLaser;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalLaser;
import Reika.DragonAPI.Interfaces.RenderFetcher;

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class RenderCrystalLaser extends ChromaRenderBase {

	private final ModelCrystalLaser model = new ModelCrystalLaser();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "lasertex.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalLaser te = (TileEntityCrystalLaser)tile;
		if (te.hasWorldObj()) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			int rot = 0;
			int rotx = 0;
			switch(tile.getBlockMetadata()) {
			case 0:
				break;
			case 1:
				rot = 180;
				break;
			case 2:
				rot = 90;
				break;
			case 3:
				rot = 270;
				break;
			case 4:
				rotx = 90;
				break;
			case 5:
				rotx = 270;
				break;
			}
			GL11.glRotated(rot, 0, 1, 0);
			GL11.glRotated(rotx, 1, 0, 0);

			this.renderBeam(te);
			this.renderModel(te, model);

			GL11.glPopMatrix();
		}
	}

	private void renderBeam(TileEntityCrystalLaser te) {

	}

}
