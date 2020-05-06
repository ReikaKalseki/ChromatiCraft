package Reika.ChromatiCraft.Render.TESR;

import org.lwjgl.opengl.GL11;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ModInterface.Bees.TileEntityBeeStorage;
import Reika.ChromatiCraft.Models.ModelBeeStorage;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;

public class RenderBeeStorage extends RenderMassStorage {

	private final ModelBeeStorage model = new ModelBeeStorage();

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		super.renderTileEntityAt(tile, par2, par4, par6, par8);
		if (!tile.hasWorldObj() || MinecraftForgeClient.getRenderPass() == 0 || StructureRenderer.isRenderingTiles()) {
			TileEntityBeeStorage te = (TileEntityBeeStorage)tile;
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			this.renderModel(te, model);
			GL11.glPopMatrix();
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "beestorage.png";
	}

}
