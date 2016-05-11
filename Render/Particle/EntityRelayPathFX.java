/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Particle;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

/** Traverses the relay path */
public class EntityRelayPathFX extends EntityFX {

	private ArrayList<Coordinate> targets;
	private int index = 0;
	private boolean velTick = true;

	public EntityRelayPathFX(CrystalElement e, Coordinate c1, Coordinate c2) {
		this(e, c1.xCoord+0.5, c1.yCoord+0.5, c1.zCoord+0.5);

		double dx = c2.xCoord-posX+0.5;
		double dy = c2.yCoord-posY+0.5;
		double dz = c2.zCoord-posZ+0.5;
		//double d = ReikaMathLibrary.py3d(dx, dy, dz);
		double v = 0.1;
		motionX = v*dx;
		motionY = v*dy;
		motionZ = v*dz;

		targets = new ArrayList();
		targets.add(c2);
		velTick = false;
	}

	public EntityRelayPathFX(CrystalElement e, ArrayList<Coordinate> li) {
		this(e, li.get(0).xCoord+0.5, li.get(0).yCoord+0.5, li.get(0).zCoord+0.5);
		targets = li;
		targets.remove(0);
	}

	private EntityRelayPathFX(CrystalElement e, double x, double y, double z) {
		super(Minecraft.getMinecraft().theWorld, x, y, z);
		particleMaxAge = Integer.MAX_VALUE;
		particleScale = 4F;
		particleGravity = 0;
		noClip = true;

		particleRed = e.getRed()/255F;
		particleGreen = e.getGreen()/255F;
		particleBlue = e.getBlue()/255F;

		particleIcon = ChromaIcons.BIGFLARE.getIcon();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.testAndUpdate();

		if (ReikaMathLibrary.py3d(motionX, motionY, motionZ) < 0.125)
			this.die();
	}

	private void testAndUpdate() {
		Coordinate c = targets.get(index);
		double dx = c.xCoord-posX+0.5;
		double dy = c.yCoord-posY+0.5;
		double dz = c.zCoord-posZ+0.5;
		double d = ReikaMathLibrary.py3d(dx, dy, dz);
		double v = 0.5;//1;
		if (d < 0.125) {
			if (index == targets.size()-1) {
				this.die();
			}
			else {
				index++;
				this.testAndUpdate();
			}
		}
		else if (velTick) {
			motionX = v*dx/d;
			motionY = v*dy/d;
			motionZ = v*dz/d;
		}
	}

	private void die() {
		this.setDead();
	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		v5.draw();
		ReikaTextureHelper.bindTerrainTexture();
		BlendMode.ADDITIVEDARK.apply();
		GL11.glColor4f(1, 1, 1, 1);
		v5.startDrawingQuads();
		v5.setBrightness(this.getBrightnessForRender(0));
		super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
		v5.draw();
		BlendMode.DEFAULT.apply();
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

}
