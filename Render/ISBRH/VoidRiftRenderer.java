package Reika.ChromatiCraft.Render.ISBRH;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.CrystalElement;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class VoidRiftRenderer implements ISimpleBlockRenderingHandler {

	//public static int renderPass;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		CrystalElement e = CrystalElement.elements[world.getBlockMetadata(x, y, z)];
		boolean flag = false;
		//if (renderPass == 0) {
		float r = 1;
		float g = 1;
		float b = 1;
		rb.renderStandardBlockWithAmbientOcclusion(block, x, y, z, r, g, b);
		flag = true;
		//}
		//else if (renderPass == 1) {
		//	flag = this.renderAura(block, world, x, y, z, e);
		//}

		return flag;
	}
	/*
	private boolean renderAura(Block block, IBlockAccess world, int x, int y, int z, CrystalElement e) {
		boolean flag = false;
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			Integer color = null;
			if (b != block) {
				color = e.getColor();
			}
			else if (meta != e.ordinal()) {
				//blend colors
				CrystalElement e2 = CrystalElement.elements[meta];
				color = ReikaColorAPI.mixColors(e.getColor(), e2.getColor(), 0.5F);
			}
			//ReikaJavaLibrary.pConsole(color);
			if (color != null) {
				Tessellator v5 = Tessellator.instance;
				v5.addTranslation(x, y, z);
				v5.setBrightness(240);
				v5.setColorOpaque_I(color);
				int h = 12;
				for (int k = 0; k < h; k++) {
					IIcon ico = ChromaIcons.AURA.getIcon(k);
					int hy = k+1;
					float u = ico.getMinU();
					float v = ico.getMinV();
					float du = ico.getMaxU();
					float dv = ico.getMaxV();

					switch(dir) {

					case NORTH:
						v5.addVertexWithUV(0, hy+1, 0, du, dv);
						v5.addVertexWithUV(1, hy+1, 0, du, v);
						v5.addVertexWithUV(1, hy, 0, u, v);
						v5.addVertexWithUV(0, hy, 0, u, dv);
						break;
					case EAST:
						v5.addVertexWithUV(1, hy+1, 0, u, dv);
						v5.addVertexWithUV(1, hy+1, 1, du, dv);
						v5.addVertexWithUV(1, hy, 1, du, v);
						v5.addVertexWithUV(1, hy, 0, u, v);
						break;
					case SOUTH:
						v5.addVertexWithUV(0, hy, 1, u, v);
						v5.addVertexWithUV(1, hy, 1, du, v);
						v5.addVertexWithUV(1, hy+1, 1, du, dv);
						v5.addVertexWithUV(0, hy+1, 1, u, dv);
						break;
					case WEST:
						v5.addVertexWithUV(0, hy, 0, u, v);
						v5.addVertexWithUV(0, hy, 1, du, v);
						v5.addVertexWithUV(0, hy+1, 1, du, dv);
						v5.addVertexWithUV(0, hy+1, 0, u, dv);
						break;
					default:
						break;
					}
				}

				v5.addTranslation(-x, -y, -z);
				flag = true;
			}
		}
		return flag;
	}
	 */
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.vriftRender;
	}

}
