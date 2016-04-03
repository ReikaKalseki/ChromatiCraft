/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBallLightningFX;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityWeakRepeater extends TileEntityCrystalRepeater {

	public static final int MAX_LUMENS_MIN = 30000;
	public static final int MAX_LUMENS_MAX = 80000;

	private int originalUse;
	private int remainingUse;

	private CrystalElement overloadColor;
	private int eolTicks;

	public static final int WEAK_RANGE = 16;

	public TileEntityWeakRepeater() {
		originalUse = ReikaRandomHelper.getRandomBetween(MAX_LUMENS_MIN, MAX_LUMENS_MAX);
		remainingUse = originalUse;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.WEAKREPEATER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (world.isRemote) {
			this.doLifespanParticles(world, x, y, z);
		}

		if (remainingUse <= 0) {
			eolTicks++;
			world.setBlock(x, y+1, z, Blocks.fire);
			this.doDestroyFX(world, x, y, z);
		}
	}

	private void doDestroyFX(World world, int x, int y, int z) {
		if (eolTicks > 320) {
			this.delete();
			for (int i = 0; i < 6; i++) {
				ReikaWorldHelper.ignite(world, x+dirs[i].offsetX, y+dirs[i].offsetY, z+dirs[i].offsetZ);
			}
			world.newExplosion(null, x+0.5, y+0.5, z+0.5, 2, true, true);
			if (world.isRemote) {
				this.doDestroyFXClient(world, x, y, z);
			}
		}
		else {
			if (ReikaRandomHelper.doWithChance(Math.pow(eolTicks/320D, 0.5))) {
				if (world.isRemote)
					this.doDestroyingFXClient(world, x, y, z);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doDestroyingFXClient(World world, int x, int y, int z) {
		double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 1);
		double dy = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 1);
		double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 1);
		float s = 2+rand.nextFloat()*5;
		CrystalElement e = CrystalElement.elements[(this.getTicksExisted()/16)%16];
		int l = 10+rand.nextInt(10);
		EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setRapidExpand().setColor(e.getColor()).setScale(s).setLife(l).setIcon(ChromaIcons.TURBO);
		EntityFX fxb = new EntityBlurFX(world, dx, dy, dz).setRapidExpand().setColor(e.getColor()).setScale(s/1.125F).setLife(l).setIcon(ChromaIcons.TURBO);
		EntityFX fx2 = new EntityBlurFX(world, dx, dy, dz).setRapidExpand().setColor(0x000000).setScale(s/2.5F).setBasicBlend().setIcon(ChromaIcons.TRANSFADE).setLife(l);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		Minecraft.getMinecraft().effectRenderer.addEffect(fxb);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
	}

	@SideOnly(Side.CLIENT)
	private void doDestroyFXClient(World world, int x, int y, int z) {
		int n = 32+rand.nextInt(64);
		for (int i = 0; i < n; i++) {
			double phi = rand.nextDouble()*360;
			double theta = rand.nextDouble()*360;
			double v = ReikaRandomHelper.getRandomPlusMinus(0.25, 0.125);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, theta, phi);
			int c1 = overloadColor.getColor();
			int c2 = ReikaColorAPI.mixColors(c1, 0xffffff, 0.25F);
			int c3 = ReikaColorAPI.mixColors(c1, 0x000000, 0.25F);
			double dx = x+rand.nextDouble();
			double dy = y+rand.nextDouble();
			double dz = z+rand.nextDouble();
			float s = 5+2.5F*rand.nextFloat();
			EntityFX fx = new EntityBlurFX(world, dx, dy, dz, vel[0], vel[1], vel[2]).setRapidExpand().setColor(c1).setScale(s);
			EntityFX fx1 = new EntityBlurFX(world, dx, dy, dz, vel[0], vel[1], vel[2]).setRapidExpand().setColor(c2).setScale(s*0.5F).lockTo(fx);
			EntityFX fx2 = new EntityBlurFX(world, dx, dy, dz, vel[0], vel[1], vel[2]).setRapidExpand().setColor(c3).setScale(s*0.25F).setIcon(ChromaIcons.TRANSFADE).setBasicBlend().lockTo(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
		ReikaSoundHelper.playClientSound(ChromaSounds.POWERDOWN, x+0.5, y+0.5, z+0.5, 1, 1, false);
	}

	@SideOnly(Side.CLIENT)
	private void doLifespanParticles(World world, int x, int y, int z) {
		if (remainingUse < originalUse) {
			double frac = (double)remainingUse/originalUse;
			double f = 0.8*(1-Math.pow(frac, 1/6D));
			CrystalElement e = CrystalElement.elements[(this.getTicksExisted()/16)%16];
			if (eolTicks == 0 && ReikaRandomHelper.doWithChance(f)) {
				EntityBallLightningFX fx = new EntityBallLightningFX(world, x+0.5, y+0.5, z+0.5, e);
				fx.setVelocity(0.125, rand.nextInt(360), rand.nextInt(45));
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			if (frac < 0.5 && ReikaRandomHelper.doWithChance(0.125*Math.pow(f, 2))) {
				double dr = ReikaRandomHelper.getRandomPlusMinus(8D, 2D);
				double ex = ReikaRandomHelper.getRandomPlusMinus(x+0.5, dr);
				double ez = ReikaRandomHelper.getRandomPlusMinus(z+0.5, dr);
				double ey = ReikaRandomHelper.getRandomBetween(y+0.05, y+6);
				LightningBolt b = new LightningBolt(new DecimalPosition(x+0.5, y+0.5, z+0.5), new DecimalPosition(ex, ey, ez), 4);
				b.variance = 0.375;
				b.update();
				int l = 20+rand.nextInt(20);
				for (int i = 0; i < b.nsteps; i++) {
					DecimalPosition pos1 = b.getPosition(i);
					DecimalPosition pos2 = b.getPosition(i+1);
					for (double r = 0; r <= 1; r += 0.03125) {
						float s = 2F;
						int clr = e.getColor();
						double dx = pos1.xCoord+r*(pos2.xCoord-pos1.xCoord);
						double dy = pos1.yCoord+r*(pos2.yCoord-pos1.yCoord);
						double dz = pos1.zCoord+r*(pos2.zCoord-pos1.zCoord);
						EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setScale(s).setColor(clr).setLife(l).setRapidExpand();
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						EntityFX fx2 = new EntityBlurFX(world, dx, dy, dz).setScale(s/2F).setColor(0xffffff).setLife(l).setRapidExpand();
						Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
					}
				}
				ReikaSoundHelper.playClientSound(ChromaSounds.DISCHARGE, x+0.5, y+0.5, z+0.5, 0.125F, CrystalMusicManager.instance.getRandomScaledDing(e), true);
			}
		}
	}

	private void endOfLife(CrystalElement e) {
		overloadColor = e;
		worldObj.setBlock(xCoord, yCoord+1, zCoord, Blocks.fire);
		this.syncAllData(false);
	}

	public boolean hasRemainingLife() {
		return remainingUse > 0;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		remainingUse = NBT.getInteger("remaining");

		eolTicks = NBT.getInteger("eol");
		overloadColor = CrystalElement.elements[NBT.getInteger("overload")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("remaining", remainingUse);

		NBT.setInteger("eol", eolTicks);
		if (overloadColor != null) {
			NBT.setInteger("overload", overloadColor.ordinal());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		originalUse = NBT.getInteger("lifespan");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("lifespan", originalUse);
	}

	@Override
	public void onTransfer(CrystalElement e, int amt) {
		if (remainingUse > 0) {
			remainingUse -= amt;
			if (remainingUse <= 0) {
				this.endOfLife(e);
			}
		}
	}

	@Override
	public int getSendRange() {
		return WEAK_RANGE;
	}

	@Override
	public int getReceiveRange() {
		return WEAK_RANGE;
	}

	@Override
	public int getSignalDegradation() {
		return 25;
	}

	@Override
	protected boolean checkForStructure() {
		return ChromaStructures.getWeakRepeaterStructure(worldObj, xCoord, yCoord, zCoord).matchInWorld();
	}

	@Override
	public CrystalElement getActiveColor() {
		return null;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		super.getTagsToWriteToStack(NBT);

		NBT.setInteger("total", remainingUse);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		super.setDataFromItemStackTag(is);

		remainingUse = is.stackTagCompound != null && is.stackTagCompound.hasKey("total") ? is.stackTagCompound.getInteger("total") : 0;
	}

	@Override
	protected boolean shouldDrop() {
		return this.hasRemainingLife();
	}

	@Override
	public int maxThroughput() {
		return Math.min(remainingUse, 120);
	}

	@Override
	public double getIncomingBeamRadius() {
		return 0.125;
	}

	@Override
	public double getOutgoingBeamRadius() {
		return 0.125;
	}

	@Override
	public boolean canTransmitTo(CrystalReceiver r) {
		return super.canTransmitTo(r) && r.getResearchTier().ordinal() <= this.getResearchTier().ordinal();
	}

	@Override
	public ResearchLevel getResearchTier() {
		return ResearchLevel.ENERGYEXPLORE;
	}

	@Override
	public float getFailureWeight(CrystalElement e) {
		return 30;
	}

}
