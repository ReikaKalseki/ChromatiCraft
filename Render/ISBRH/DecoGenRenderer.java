package Reika.ChromatiCraft.Render.ISBRH;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Rendering.RotatedQuad;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class DecoGenRenderer implements ISBRH {

	private static final RotatedQuad[][][] crystalShapes = new RotatedQuad[4][4][4];

	static {
		for (int i = 0; i < crystalShapes.length; i++) {
			for (int j = 0; j < crystalShapes[i].length; j++) {
				for (int k = 0; k < crystalShapes[i][j].length; k++) {
					double r1 = ReikaRandomHelper.getRandomBetween(0.125, 0.375);
					double r2 = ReikaRandomHelper.getRandomBetween(0.125, 0.375);
					double r3 = ReikaRandomHelper.getRandomBetween(0.125, 0.375);
					double r4 = ReikaRandomHelper.getRandomBetween(0.125, 0.375);
					double rot = ReikaRandomHelper.getRandomPlusMinus(0D, 30D);
					crystalShapes[i][j][k] = new RotatedQuad(r1, r2, r3, r4, rot);
				}
			}
		}
	}

	private static RotatedQuad getCrystalShape(int x, int y, int z) {
		int i = ((x%crystalShapes.length)+crystalShapes.length)%crystalShapes.length;
		int j = ((y%crystalShapes[i].length)+crystalShapes[i].length)%crystalShapes[i].length;
		int k = ((z%crystalShapes[i][j].length)+crystalShapes[i][j].length)%crystalShapes[i][j].length;
		return crystalShapes[i][j][k];
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		RotatedQuad r1 = getCrystalShape(x, y, z);
		RotatedQuad r2 = getCrystalShape(x, y+1, z);
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaBlocks.CRYSTAL.getBlockInstance().getIcon(0, 0);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);

		v5.addTranslation(x, y, z);
		v5.addTranslation(0.5F, 0, 0.5F);

		double r10x = r1.getPosX(0);
		double r11x = r1.getPosX(1);
		double r12x = r1.getPosX(2);
		double r13x = r1.getPosX(3);
		double r20x = r2.getPosX(0);
		double r21x = r2.getPosX(1);
		double r22x = r2.getPosX(2);
		double r23x = r2.getPosX(3);
		double r10z = r1.getPosZ(0);
		double r11z = r1.getPosZ(1);
		double r12z = r1.getPosZ(2);
		double r13z = r1.getPosZ(3);
		double r20z = r2.getPosZ(0);
		double r21z = r2.getPosZ(1);
		double r22z = r2.getPosZ(2);
		double r23z = r2.getPosZ(3);

		if (world.getBlock(x, y+1, z) != block || world.getBlockMetadata(x, y+1, z) != world.getBlockMetadata(x, y, z)) {
			double d = 0.125;
			r20x *= d;
			r21x *= d;
			r22x *= d;
			r23x *= d;
			r20z *= d;
			r21z *= d;
			r22z *= d;
			r23z *= d;
		}

		if (world.getBlock(x, y-1, z) != block || world.getBlockMetadata(x, y-1, z) != world.getBlockMetadata(x, y, z)) {
			double d = 0.75;
			r10x = Math.signum(r10x)*(1-(d*(1-Math.abs(r10x))));
			r11x = Math.signum(r11x)*(1-(d*(1-Math.abs(r11x))));
			r12x = Math.signum(r12x)*(1-(d*(1-Math.abs(r12x))));
			r13x = Math.signum(r13x)*(1-(d*(1-Math.abs(r13x))));
			r10z = Math.signum(r10z)*(1-(d*(1-Math.abs(r10z))));
			r11z = Math.signum(r11z)*(1-(d*(1-Math.abs(r11z))));
			r12z = Math.signum(r12z)*(1-(d*(1-Math.abs(r12z))));
			r13z = Math.signum(r13z)*(1-(d*(1-Math.abs(r13z))));
		}

		v5.addVertexWithUV(r10x, 0, r10z, u, v);
		v5.addVertexWithUV(r11x, 0, r11z, du, v);
		v5.addVertexWithUV(r12x, 0, r12z, du, dv);
		v5.addVertexWithUV(r13x, 0, r13z, u, dv);

		v5.addVertexWithUV(r23x, 1, r23z, u, dv);
		v5.addVertexWithUV(r22x, 1, r22z, du, dv);
		v5.addVertexWithUV(r21x, 1, r21z, du, v);
		v5.addVertexWithUV(r20x, 1, r20z, u, v);

		v5.addVertexWithUV(r20x, 1, r20z, u, dv);
		v5.addVertexWithUV(r21x, 1, r21z, du, dv);
		v5.addVertexWithUV(r11x, 0, r11z, du, v);
		v5.addVertexWithUV(r10x, 0, r10z, u, v);

		v5.addVertexWithUV(r13x, 0, r13z, u, v);
		v5.addVertexWithUV(r12x, 0, r12z, du, v);
		v5.addVertexWithUV(r22x, 1, r22z, du, dv);
		v5.addVertexWithUV(r23x, 1, r23z, u, dv);

		v5.addVertexWithUV(r21x, 1, r21z, u, dv);
		v5.addVertexWithUV(r22x, 1, r22z, du, dv);
		v5.addVertexWithUV(r12x, 0, r12z, du, v);
		v5.addVertexWithUV(r11x, 0, r11z, u, v);

		v5.addVertexWithUV(r10x, 0, r10z, u, v);
		v5.addVertexWithUV(r13x, 0, r13z, du, v);
		v5.addVertexWithUV(r23x, 1, r23z, du, dv);
		v5.addVertexWithUV(r20x, 1, r20z, u, dv);

		v5.addTranslation(-0.5F, 0, -0.5F);
		v5.addTranslation(-x, -y, -z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}
