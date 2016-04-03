package Reika.ChromatiCraft.World.Dimension.Rendering;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Aurora {

	private static final int HEIGHT = 8;

	private final DecimalPosition point1;
	private final DecimalPosition point2;

	private final double length;
	private final double segmentSize;
	private final LightningBolt shape;

	public final int color1;
	public final int color2;

	public Aurora(int c1, int c2, double speed, double variance, double seg, double x1, double y1, double z1, double x2, double y2, double z2) {
		point1 = new DecimalPosition(x1, y1, z1);
		point2 = new DecimalPosition(x2, y2, z2);
		length = point2.getDistanceTo(point1);
		segmentSize = seg;
		int n = (int)Math.round(length/segmentSize);
		shape = new LightningBolt(point1, point2, n);
		shape.variance = variance;
		shape.velocity = speed;
		color1 = c1;
		color2 = c2;
	}

	public void update() {
		shape.update();
	}

	public void render() {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		ReikaRenderHelper.disableEntityLighting();

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/liquid.png");

		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setBrightness(240);

		for (int i = 0; i < shape.nsteps; i++) {
			DecimalPosition pos1 = shape.getPosition(i);
			DecimalPosition pos2 = shape.getPosition(i+1);

			v5.setColorOpaque_I(color1);
			v5.addVertexWithUV(pos1.xCoord, pos1.yCoord, pos1.zCoord, 0, 0);
			v5.addVertexWithUV(pos1.xCoord, pos1.yCoord+HEIGHT, pos1.zCoord, 0, 1);

			v5.setColorOpaque_I(color2);
			v5.addVertexWithUV(pos2.xCoord, pos2.yCoord+HEIGHT, pos2.zCoord, 1, 1);
			v5.addVertexWithUV(pos2.xCoord, pos2.yCoord, pos2.zCoord, 1, 0);

		}

		v5.draw();
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

}
