package Reika.ChromatiCraft.Render;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelItemStand;
import Reika.ChromatiCraft.TileEntity.TileEntityItemStand;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.RotaryCraft.Base.ItemBlockPlacer;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

public class RenderItemStand extends ChromaRenderBase {

	private final ModelItemStand model = new ModelItemStand();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityItemStand te = (TileEntityItemStand)tile;

		if (te.hasWorldObj()) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			this.renderItem(te);
			this.renderModel(te, model);

			GL11.glPopMatrix();
		}
	}

	private void renderItem(TileEntityItemStand te) {
		ItemStack is = te.getStackInSlot(0);
		if (is != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(0.5, 1, 0.5);
			double s = 0.5;
			GL11.glScaled(s, s, s);
			double a = (System.currentTimeMillis()/100D)%360;
			GL11.glRotated(a, 0, 1, 0);
			GL11.glTranslated(-0.5, 0, 0);

			Tessellator v5 = Tessellator.instance;

			Item item = is.getItem();
			IItemRenderer iir = MinecraftForgeClient.getItemRenderer(is, ItemRenderType.INVENTORY);
			float u = 0;
			float du = 0;
			float v = 0;
			float dv = 0;

			if (item instanceof IndexedItemSprites && !(item instanceof ItemBlockPlacer)) {
				IndexedItemSprites iis = (IndexedItemSprites)item;
				ReikaTextureHelper.bindTexture(iis.getTextureReferenceClass(), iis.getTexture(is));
				int index = iis.getItemSpriteIndex(is);
				int row = index/16;
				int col = index%16;

				u = col/16F;
				v = row/16F;

				du = u+0.0625F;
				dv = v+0.0625F;
			}
			else if (iir != null) {
				;//iir.renderItem(ItemRenderType.INVENTORY, is, new RenderBlocks());
			}
			else {
				if (ReikaItemHelper.isBlock(is))
					ReikaTextureHelper.bindTerrainTexture();
				else
					ReikaTextureHelper.bindItemTexture();
				IIcon ico = item.getIcon(is, MinecraftForgeClient.getRenderPass());
				u = ico.getMinU();
				v = ico.getMinV();
				du = ico.getMaxU();
				dv = ico.getMaxV();
			}

			ItemRenderer.renderItemIn2D(v5, du, v, u, dv, 256, 256, 0.0625F);

			GL11.glPopMatrix();
		}
	}

}
