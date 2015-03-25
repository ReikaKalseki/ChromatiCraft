package Reika.ChromatiCraft.Render.Particle;

import java.util.LinkedList;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;

public class EntityBoltFX extends EntityFX {

	private Coordinate p1;
	private Coordinate p2;

	private LinkedList<DecimalPosition> points = new LinkedList();

	public EntityBoltFX(World world, double x, double y, double z, int x2, int y2, int z2) {
		super(world, x, y, z);
		motionX = 0;
		motionY = 0;
		motionZ = 0;
		particleGravity = 0;
		particleMaxAge = 60;
		p1 = new Coordinate(this);
		p2 = new Coordinate(x2, y2, z2);
	}

	public EntityBoltFX setScale(float f) {
		particleScale = f;
		return this;
	}

	public final EntityBoltFX setLife(int time) {
		particleMaxAge = time;
		return this;
	}

	public final EntityBoltFX setGravity(float g) {
		particleGravity = g;
		return this;
	}

	public final EntityBoltFX setColor(int r, int g, int b) {
		particleRed = r/255F;
		particleGreen = g/255F;
		particleBlue = b/255F;
		return this;
	}

	@Override
	public void renderParticle(Tessellator v5, float ptick, float par3, float par4, float par5, float par6, float par7)
	{
		v5.draw();/*
		ReikaTextureHelper.bindTerrainTexture();
		BlendMode.ADDITIVEDARK.apply();
		GL11.glColor4f(1, 1, 1, 1);
		v5.startDrawingQuads();
		v5.setBrightness(this.getBrightnessForRender(0));
		super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
		v5.draw();
		BlendMode.DEFAULT.apply();*/

		v5.startDrawing(GL11.GL_LINE_STRIP);
		v5.addTranslation(par3, par4, par5);
		v5.setBrightness(240);
		for (DecimalPosition d : points) {
			v5.addVertex(d.xCoord+posX, d.yCoord+posY, d.zCoord+posZ);
		}
		v5.addTranslation(-par3, -par4, -par5);
		v5.draw();
		v5.startDrawingQuads();
	}

	@Override
	public int getBrightnessForRender(float par1)
	{
		return 240;
	}

	@Override
	public int getFXLayer()
	{
		return 2;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		points.clear();

		double x = p2.xCoord-p1.xCoord;
		double y = p2.yCoord-p1.yCoord;
		double z = p2.zCoord-p1.zCoord;

		int s = 20;
		for (int i = 0; i <= s; i++) {
			double[] a = {x*i/s, y*i/s, z*i/s};
			for (int k = 0; k < 3; k++) {
				a[k] += (rand.nextDouble()-0.5)*0.1+rand.nextDouble()*0.1;
			}
			points.add(new DecimalPosition(a[0], a[1], a[2]));
		}
	}

}
