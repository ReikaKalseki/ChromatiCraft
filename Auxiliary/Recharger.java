/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class Recharger {

	private int stepDuration;
	private int nsteps;

	private int step;
	private int stepTick;

	private int stepInterval;

	private RechargeWaiter notify;

	public Recharger(int d, int n) {
		this(d, n, null);
	}

	public Recharger(int d, int n, RechargeWaiter r) {
		stepDuration = d;
		this.verifySteps(n);
		nsteps = n;
		notify = r;
		stepInterval = 16/n;
	}

	private void verifySteps(int n) {
		if (n < 1 || n > 16 || !ReikaMathLibrary.isInteger(16D/n))
			throw new IllegalArgumentException("You must have some even divisor of 16 steps!");
	}

	public final void tick() {
		stepTick++;
		if (stepTick >= stepDuration) {
			this.onStepComplete();
		}
	}

	protected void onStepComplete() {
		stepTick = 0;
		step += 1;//stepInterval;
		if (step > nsteps) {
			this.onComplete();
		}
		else
			if (notify != null)
				notify.onSegmentComplete();
	}

	protected void onComplete() {
		step = 0;
		if (notify != null)
			notify.onChargingComplete();
	}

	public final int getTotalDuration() {
		return nsteps*stepDuration;
	}

	public final int getTotalTick() {
		return step*stepDuration+stepTick;
	}

	@SideOnly(Side.CLIENT)
	public final void render(double s, float ptick) {
		Tessellator v5 = Tessellator.instance;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/recharge-tile.png");
		double u = (step*stepInterval%5)/5D;
		double v = (step*stepInterval/5)/4D;
		double du = u+1/5D;
		double dv = v+1/4D;

		GL11.glPushMatrix();

		GL11.glRotated(180, 0, 0, 1);

		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.setBrightness(240);
		v5.addVertexWithUV(-s, s, 0, u, dv);
		v5.addVertexWithUV(s, s, 0, du, dv);
		v5.addVertexWithUV(s, -s, 0, du, v);
		v5.addVertexWithUV(-s, -s, 0, u, v);
		v5.draw();

		double a = stepDuration == 0 ? 0 : 360D*((stepTick+ptick)/(double)stepDuration)-90;
		//ReikaJavaLibrary.pConsole(stepTick+"/"+stepDuration+" > "+a);
		GL11.glRotated(a, 0, 0, 1);

		dv = 1;
		du = 1;
		u = du-1/5D;
		v = dv-1/4D;

		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.setBrightness(240);
		v5.addVertexWithUV(-s, s, 0, u, dv);
		v5.addVertexWithUV(s, s, 0, du, dv);
		v5.addVertexWithUV(s, -s, 0, du, v);
		v5.addVertexWithUV(-s, -s, 0, u, v);
		v5.draw();

		GL11.glPopMatrix();
	}

	public void readFromNBT(NBTTagCompound tag, RechargeWaiter r) {
		nsteps = tag.getInteger("steps");
		stepDuration = tag.getInteger("dur");
		step = tag.getInteger("step");
		stepTick = tag.getInteger("tick");
		stepInterval = tag.getInteger("interval");
		notify = r;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("steps", nsteps);
		tag.setInteger("dur", stepDuration);
		tag.setInteger("step", step);
		tag.setInteger("tick", stepTick);
		tag.setInteger("interval", stepInterval);
	}

	public static interface RechargeWaiter {

		public void onSegmentComplete();
		public void onChargingComplete();

	}

}
