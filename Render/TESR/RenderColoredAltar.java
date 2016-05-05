/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Crystal.BlockColoredAltar.TileEntityColoredAltar;
import Reika.ChromatiCraft.Models.ModelColoredAltar;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.CubeRotation;
import Reika.DragonAPI.Instantiable.Orbit;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.TruncatedCube;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Instantiable.Rendering.ColorVariance;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

// have translucent shimmering colored "spiky" base in a metal cup, have twirling cubes flying around and rising particles
public class RenderColoredAltar extends ChromaRenderBase {

	private static final Random rand = new Random();
	private static final EnumMap<CrystalElement, ColorBlendList[]> colors = new EnumMap(CrystalElement.class);
	private static final EnumMap<CrystalElement, ArrayList<Orbit>> orbits = new EnumMap(CrystalElement.class);

	private static final TruncatedCube cube = new TruncatedCube(0.03125, 0.03125*1.875);

	static {
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			ColorBlendList[] arr = new ColorBlendList[24];
			for (int k = 0; k < arr.length; k++) {
				ColorVariance cv = new ColorVariance(e.getColor(), 10, 0, 48);
				cv.whiteVariation = 0.25F;
				cv.alphaRoot = 171;
				cv.alphaVariation = 21;
				arr[k] = cv.getBlends(8, ReikaRandomHelper.getRandomPlusMinus(40, 30));
				//ReikaJavaLibrary.pConsole(e+":"+cv+":"+arr[k]);
			}
			//ReikaJavaLibrary.pConsole(e+":"+Arrays.toString(arr));
			colors.put(e, arr);

			ArrayList<Orbit> li = new ArrayList();
			for (int k = 0; k < TileEntityColoredAltar.NUMBER_CUBES; k++) {
				double a = ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25);
				double ec = rand.nextDouble()*0.4;
				li.add(new Orbit(a, ec, rand.nextInt(90), rand.nextInt(360), rand.nextInt(360), rand.nextInt(360)));
			}

			orbits.put(e, li);
		}
	}

	public static int getBlendedCrystalColor(CrystalElement e, int pos, double tick) {
		//ReikaJavaLibrary.pConsole(colors.get(CrystalElement.RED)[0]);
		return colors.get(e)[pos].getColor(tick);
	}

	private final ModelColoredAltar model = new ModelColoredAltar();

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glTranslated(par2, par4, par6);
		TileEntityColoredAltar te = (TileEntityColoredAltar)tile;
		CrystalElement e = te.getColor();
		this.bindTextureByName("Textures/TileEntity/altartex.png");
		this.renderModel(tile, model, e, (double)(te.getRenderTick()+par8));
		if (MinecraftForgeClient.getRenderPass() == 1) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);
			this.renderFloatingCubes(te, e, par2, par4, par6, par8);
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderFloatingCubes(TileEntityColoredAltar te, CrystalElement e, double par2, double par4, double par6, float ptick) {
		ArrayList<Orbit> li = orbits.get(e);
		double dtheta = te.getRenderTick()*4;

		float pdist = (float)Minecraft.getMinecraft().thePlayer.getDistance(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);

		int n = li.size()/(1+Minecraft.getMinecraft().gameSettings.particleSetting);
		for (int i = 0; i < Math.min(n, li.size()); i++) {
			Orbit o = li.get(i);
			CubeRotation cb = te.cubeRotations[i];

			double dn = 4D*(1+Minecraft.getMinecraft().gameSettings.particleSetting/2D);
			if (pdist > 8) {
				dn = Math.min(12D, dn*2);
			}
			if (pdist > 16) {
				dn = Math.min(16D, dn*2);
			}
			for (double d = -24; d <= 0; d += dn) {
				DecimalPosition pos = o.getPosition(0.5, 1.25, 0.5, dtheta+d);
				this.renderCubeAtPos(te, e, pos.xCoord, pos.yCoord, pos.zCoord, cb.angX, cb.angY, cb.angZ, ptick, d, pdist);
			}

			double v = 0.375;
			double t = te.getTicksExisted()+ptick;
			cb.rvX = v*(1+0.5*Math.sin(Math.toRadians(t)))*(1+0.5*Math.cos(Math.toRadians(90+t)));
			cb.rvY = v*(1+0.25*Math.sin(Math.toRadians(2*t)))*(1+0.75*Math.cos(Math.toRadians(t)));
			cb.rvZ = v*(1+0.75*Math.sin(Math.toRadians(90+t)))*(1+0.25*Math.cos(Math.toRadians(2*t)));

			cb.angX += cb.rvX;
			cb.angY += cb.rvY;
			cb.angZ += cb.rvZ;
		}
	}

	private void renderCubeAtPos(TileEntityColoredAltar te, CrystalElement e, double x, double y, double z, double r1, double r2, double r3, float ptick, double f, float pdist) {
		GL11.glPushMatrix();

		Tessellator v5 = Tessellator.instance;
		double s = 0.0625;
		double s2 = s*1.875;

		GL11.glTranslated(x, y, z);
		GL11.glRotated(r1, 1, 0, 0);
		GL11.glRotated(r2, 0, 1, 0);
		GL11.glRotated(r3, 0, 0, 1);
		GL11.glTranslated(-x, -y, -z);

		int c = ReikaColorAPI.mixColors(0xffffff, e.getColor(), 0.125F);
		int c2 = ReikaColorAPI.mixColors(0xffffff, e.getColor(), 0.375F);

		int a = (int)(190*(1+f/24D));
		int a2 = 150;
		cube.render(x, y, z, c | (a << 24), c2 | (a2 << 24), f == 0, pdist);


		/*
		float p = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		if (f == 0) {
			float w = Math.max(0.125F, 2F-0.125F*pdist);
			GL11.glLineWidth(w);
		}

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			List<DecimalPosition> li = cube.getFaceVertices(dir, true, x, y, z);

			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			v5.setColorRGBA_I(c, a);
			v5.setBrightness(240);
			for (DecimalPosition d : li) {
				v5.addVertex(d.xCoord, d.yCoord, d.zCoord);
			}
			v5.draw();

			if (f == 0) {
				li = cube.getFaceVertices(dir, false, x, y, z);
				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorRGBA_I(c2, a2);
				v5.setBrightness(240);
				for (DecimalPosition d : li) {
					v5.addVertex(d.xCoord, d.yCoord, d.zCoord);
				}
				v5.draw();
			}
		}

		for (List<DecimalPosition> li : cube.getCornerVertices(x, y, z)) {
			v5.startDrawing(GL11.GL_TRIANGLES);
			v5.setColorRGBA_I(c, a);
			v5.setBrightness(240);
			for (DecimalPosition d : li) {
				v5.addVertex(d.xCoord, d.yCoord, d.zCoord);
			}
			v5.draw();

			if (f == 0) {
				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorRGBA_I(c2, a2);
				v5.setBrightness(240);
				for (DecimalPosition d : li) {
					v5.addVertex(d.xCoord, d.yCoord, d.zCoord);
				}
				v5.draw();
			}
		}

		if (f == 0) {
			GL11.glLineWidth(p);
		}
		 */


		/*
		v5.startDrawingQuads();
		v5.setColorRGBA_I(ReikaColorAPI.mixColors(0xffffff, e.getColor(), 0.125F), 190);
		v5.setBrightness(240);
		v5.addVertex(x-s, y-s2, z-s);
		v5.addVertex(x+s, y-s2, z-s);
		v5.addVertex(x+s, y-s2, z+s);
		v5.addVertex(x-s, y-s2, z+s);

		v5.addVertex(x-s, y+s2, z+s);
		v5.addVertex(x+s, y+s2, z+s);
		v5.addVertex(x+s, y+s2, z-s);
		v5.addVertex(x-s, y+s2, z-s);

		v5.addVertex(x-s2, y-s, z+s);
		v5.addVertex(x-s2, y+s, z+s);
		v5.addVertex(x-s2, y+s, z-s);
		v5.addVertex(x-s2, y-s, z-s);

		v5.addVertex(x+s2, y-s, z-s);
		v5.addVertex(x+s2, y+s, z-s);
		v5.addVertex(x+s2, y+s, z+s);
		v5.addVertex(x+s2, y-s, z+s);

		v5.addVertex(x-s, y-s, z-s2);
		v5.addVertex(x-s, y+s, z-s2);
		v5.addVertex(x+s, y+s, z-s2);
		v5.addVertex(x+s, y-s, z-s2);

		v5.addVertex(x+s, y-s, z+s2);
		v5.addVertex(x+s, y+s, z+s2);
		v5.addVertex(x-s, y+s, z+s2);
		v5.addVertex(x-s, y-s, z+s2);

		v5.draw();
		 */
		/*
		float p = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		float w = Math.max(0.125F, 2F-0.03125F*(float)Minecraft.getMinecraft().thePlayer.getDistance(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5));
		GL11.glLineWidth(w);
		v5.startDrawing(GL11.GL_LINES);
		v5.setColorRGBA_I(ReikaColorAPI.mixColors(0xffffff, e.getColor(), 0.375F), 150);
		v5.setBrightness(240);

		v5.addVertex(x-s, y-s2, z-s2);
		v5.addVertex(x+s, y-s2, z-s2);

		v5.addVertex(x-s, y-s2, z+s2);
		v5.addVertex(x+s, y-s2, z+s2);

		v5.addVertex(x-s, y+s2, z-s2);
		v5.addVertex(x+s, y+s2, z-s2);

		v5.addVertex(x-s, y+s2, z+s2);
		v5.addVertex(x+s, y+s2, z+s2);

		v5.addVertex(x+s2, y-s2, z-s);
		v5.addVertex(x+s2, y-s2, z+s);

		v5.addVertex(x-s2, y-s2, z-s);
		v5.addVertex(x-s2, y-s2, z+s);

		v5.addVertex(x+s2, y+s2, z-s);
		v5.addVertex(x+s2, y+s2, z+s);

		v5.addVertex(x-s2, y+s2, z-s);
		v5.addVertex(x-s2, y+s2, z+s);

		v5.addVertex(x-s2, y-s, z-s2);
		v5.addVertex(x-s2, y+s, z-s2);

		v5.addVertex(x+s2, y-s, z-s2);
		v5.addVertex(x+s2, y+s, z-s2);

		v5.addVertex(x-s2, y-s, z+s2);
		v5.addVertex(x-s2, y+s, z+s2);

		v5.addVertex(x+s2, y-s, z+s2);
		v5.addVertex(x+s2, y+s, z+s2);

		v5.draw();

		GL11.glLineWidth(p);
		 */

		GL11.glPopMatrix();
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

}
