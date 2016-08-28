package Reika.ChromatiCraft.Render.ISBRH;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Worldgen.BlockSparkle;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class SparklingBlockRender implements ISBRH {

	private final Random rand = new Random();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks rb) {
		rb.renderBlockAsItem(BlockSparkle.BlockTypes.list[metadata].getBlockProxy(), 0, 1);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glPushMatrix();
		//GL11.glTranslated(-0.5, -0.42, -0.5);
		//rb.renderBlockAsItem(ChromaBlocks.SPECIALSHIELD.getBlockInstance(), BlockType.GLASS.metadata, 1);
		/*
		GL11.glTranslated(-0.5, -0.5, -0.5);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(0, 0, ChromaIcons.SPARKLE.getIcon(), 1, 1);
		GL11.glTranslated(0, 0, 1);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(0, 0, ChromaIcons.SPARKLE.getIcon(), 1, 1);
		GL11.glRotated(90, 1, 0, 0);
		GL11.glTranslated(0, -1, -1);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(0, 0, ChromaIcons.SPARKLE.getIcon(), 1, 1);
		GL11.glTranslated(0, 0, 1);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(0, 0, ChromaIcons.SPARKLE.getIcon(), 1, 1);
		GL11.glRotated(90, 0, 1, 0);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(0, 0, ChromaIcons.SPARKLE.getIcon(), 1, 1);
		GL11.glTranslated(0, 0, 1);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(0, 0, ChromaIcons.SPARKLE.getIcon(), 1, 1);
		 */
		GL11.glTranslated(-0.5, -0.5, -0.5);
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setColorOpaque_I(0x909090);
		rb.renderFaceYPos(block, 0, 0, 0, ChromaIcons.SPARKLE.getIcon());
		rb.renderFaceYNeg(block, 0, 0, 0, ChromaIcons.SPARKLE.getIcon());
		rb.renderFaceXPos(block, 0, 0, 0, ChromaIcons.SPARKLE.getIcon());
		rb.renderFaceXNeg(block, 0, 0, 0, ChromaIcons.SPARKLE.getIcon());
		rb.renderFaceZPos(block, 0, 0, 0, ChromaIcons.SPARKLE.getIcon());
		rb.renderFaceZNeg(block, 0, 0, 0, ChromaIcons.SPARKLE.getIcon());
		Tessellator.instance.draw();
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		//Tessellator.instance.startDrawingQuads();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		rb.renderStandardBlockWithAmbientOcclusion(block, x, y, z, 1, 1, 1);

		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);


		double o = 0.005;
		for (int s = 0; s < 6; s++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
			if (block.shouldSideBeRendered(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, s)) {
				int n = 1+rand.nextInt(6);
				boolean[][] draw = new boolean[16][16];
				for (int i = 0; i < n; i++) {
					int c = ReikaColorAPI.RGBtoHex(192+rand.nextInt(64), 192+rand.nextInt(64), 192+rand.nextInt(64));
					int c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.5F);
					IIcon ico = rand.nextInt(3) == 0 ? ChromaIcons.BASICFADE_FAST.getIcon() : ChromaIcons.BASICFADE.getIcon();
					float ua = ico.getMinU();
					float va = ico.getMinV();
					float dua = ico.getMaxU();
					float dva = ico.getMaxV();
					IIcon ico2 = ChromaBlocks.DYE.getBlockInstance().getIcon(0, 0);
					float ub = ico2.getMinU();
					float vb = ico2.getMinV();
					float dub = ico2.getMaxU();
					float dvb = ico2.getMaxV();
					int da = ReikaRandomHelper.getRandomBetween(1, 14);
					int db = ReikaRandomHelper.getRandomBetween(1, 14);
					draw[da][db] = true;

					for (int ar = -1; ar <= 1; ar++) {
						for (int br = -1; br <= 1; br++) {
							if (Math.abs(ar) == 0 || Math.abs(br) == 0) {
								if ((ar == 0 && br == 0) || !draw[da+ar][db+br]) {
									double u1 = ar == 0 && br == 0 ? ua : ub;
									double v1 = ar == 0 && br == 0 ? va : vb;
									double u2 = ar == 0 && br == 0 ? dua : dub;
									int clr = ar == 0 && br == 0 ? c : c2;
									v5.setColorOpaque_I(clr);
									double v2 = ar == 0 && br == 0 ? dva : dvb;
									switch(s) {
										case 0:
											v5.addVertexWithUV(x+(da+ar)/16D,		y+1+o,				z+(db+br+1)/16D, u1, v1);
											v5.addVertexWithUV(x+(da+ar+1)/16D,		y+1+o,				z+(db+br+1)/16D, u2, v1);
											v5.addVertexWithUV(x+(da+ar+1)/16D,		y+1+o,				z+(db+br)/16D, u2, v2);
											v5.addVertexWithUV(x+(da+ar)/16D,		y+1+o,				z+(db+br)/16D, u1, v2);
											break;

										case 1:
											v5.addVertexWithUV(x+(da+ar)/16D,		y-o,				z+(db+br)/16D, u1, v2);
											v5.addVertexWithUV(x+(da+ar+1)/16D,		y-o,				z+(db+br)/16D, u2, v2);
											v5.addVertexWithUV(x+(da+ar+1)/16D,		y-o,				z+(db+br+1)/16D, u2, v1);
											v5.addVertexWithUV(x+(da+ar)/16D,		y-o,				z+(db+br+1)/16D, u1, v1);
											break;

										case 2:
											v5.addVertexWithUV(x+(da+ar)/16D,		y+(db+br)/16D,		z+1+o, u1, v2);
											v5.addVertexWithUV(x+(da+ar+1)/16D,		y+(db+br)/16D,		z+1+o, u2, v2);
											v5.addVertexWithUV(x+(da+ar+1)/16D,		y+(db+br+1)/16D,	z+1+o, u2, v1);
											v5.addVertexWithUV(x+(da+ar)/16D,		y+(db+br+1)/16D,	z+1+o, u1, v1);
											break;

										case 3:
											v5.addVertexWithUV(x+(da+ar)/16D,		y+(db+br+1)/16D,	z-o, u1, v1);
											v5.addVertexWithUV(x+(da+ar+1)/16D,		y+(db+br+1)/16D,	z-o, u2, v1);
											v5.addVertexWithUV(x+(da+ar+1)/16D,		y+(db+br)/16D,		z-o, u2, v2);
											v5.addVertexWithUV(x+(da+ar)/16D,		y+(db+br)/16D,		z-o, u1, v2);
											break;

										case 4:
											v5.addVertexWithUV(x+1+o,				y+(db+br+1)/16D,	z+(da+ar)/16D, u1, v1);
											v5.addVertexWithUV(x+1+o,				y+(db+br+1)/16D,	z+(da+ar+1)/16D, u2, v1);
											v5.addVertexWithUV(x+1+o,				y+(db+br)/16D,		z+(da+ar+1)/16D, u2, v2);
											v5.addVertexWithUV(x+1+o,				y+(db+br)/16D,		z+(da+ar)/16D, u1, v2);
											break;

										case 5:
											v5.addVertexWithUV(x-o,					y+(db+br)/16D,		z+(da+ar)/16D, u1, v2);
											v5.addVertexWithUV(x-o,					y+(db+br)/16D,		z+(da+ar+1)/16D, u2, v2);
											v5.addVertexWithUV(x-o,					y+(db+br+1)/16D,	z+(da+ar+1)/16D, u2, v1);
											v5.addVertexWithUV(x-o,					y+(db+br+1)/16D,	z+(da+ar)/16D, u1, v1);
											break;
									}
								}
							}
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.sparkleRender;
	}

}
