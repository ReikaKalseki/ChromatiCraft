/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Decoration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.BoundedValue;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Interfaces.TileEntity.NBTCopyable;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityParticleSpawner extends TileEntityChromaticBase implements GuiController, NBTCopyable {

	public ParticleDefinition particles = new ParticleDefinition();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PARTICLES;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote && !this.hasRedstoneSignal()) {
			this.spawnParticles(world, x, y, z);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		particles.location = new Coordinate(this);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		EntityFX fx = particles.getFX(this.getTicksExisted());
		if (fx != null) {
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		particles.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		particles.readFromNBT(NBT);
	}

	@Override
	public void writeCopyableData(NBTTagCompound NBT) {
		particles.writeToNBT(NBT);
	}

	@Override
	public void readCopyableData(NBTTagCompound NBT) {
		particles.readFromNBT(NBT);
		particles.location = new Coordinate(this);
		this.syncAllData(true);
	}

	public static class ParticleDefinition {

		private Coordinate location = new Coordinate(0, 0, 0);

		public int particleColor = 0xffffff;

		public VariableValue<Double> particlePositionX = new VariableValue(-8D, 8D).setStep(0.03125);
		public VariableValue<Double> particlePositionY = new VariableValue(-8D, 8D).setStep(0.03125);
		public VariableValue<Double> particlePositionZ = new VariableValue(-8D, 8D).setStep(0.03125);

		public VariableValue<Double> particleVelocityX = new VariableValue(-2D, 2D).setStep(0.03125);
		public VariableValue<Double> particleVelocityY = new VariableValue(-2D, 2D).setStep(0.03125);
		public VariableValue<Double> particleVelocityZ = new VariableValue(-2D, 2D).setStep(0.03125);

		public VariableValue<Float> particleSize = new VariableValue(0.125F, 50F, 1F).setStep(0.125F);

		public VariableValue<Float> particleGravity = new VariableValue(-2F, 2F).setStep(0.03125F/2F);

		public VariableValue<Integer> particleLife = new VariableValue(5, 1200, 20).setStep(1);

		public boolean particleCollision = false;
		public boolean rapidExpand = false;
		public boolean noSlowdown = false;
		public boolean alphaFade = false;
		public boolean cyclingColor = false;

		public ChromaIcons particleIcon = ChromaIcons.FADE;

		public BoundedValue<Integer> particleRate = new BoundedValue(1, 300, 10).setStep(1);

		private ParticleDefinition() {

		}

		@SideOnly(Side.CLIENT)
		public EntityFX getFX(int tick) {
			if (tick%(int)particleRate.getValue() == 0) {
				double px = location.xCoord+0.5+ReikaRandomHelper.getRandomPlusMinus(particlePositionX.getValue(), particlePositionX.getVariation());
				double py = location.yCoord+0.5+ReikaRandomHelper.getRandomPlusMinus(particlePositionY.getValue(), particlePositionY.getVariation());
				double pz = location.zCoord+0.5+ReikaRandomHelper.getRandomPlusMinus(particlePositionZ.getValue(), particlePositionZ.getVariation());
				double vx = ReikaRandomHelper.getRandomPlusMinus(particleVelocityX.getValue(), particleVelocityX.getVariation());
				double vy = ReikaRandomHelper.getRandomPlusMinus(particleVelocityY.getValue(), particleVelocityY.getVariation());
				double vz = ReikaRandomHelper.getRandomPlusMinus(particleVelocityZ.getValue(), particleVelocityZ.getVariation());
				float g = (float)ReikaRandomHelper.getRandomPlusMinus(particleGravity.getValue(), particleGravity.getVariation());
				float s = (float)ReikaRandomHelper.getRandomPlusMinus(particleSize.getValue(), particleSize.getVariation());
				int l = ReikaRandomHelper.getRandomPlusMinus((int)particleLife.getValue(), (int)particleLife.getVariation());
				EntityBlurFX fx = new EntityBlurFX(Minecraft.getMinecraft().theWorld, px, py, pz, vx, vy, vz).setGravity(g).setLife(l).setScale(s).setColor(particleColor);
				fx.setIcon(particleIcon);
				if (particleCollision)
					fx.setColliding();
				if (particleIcon.isTransparent())
					fx.setBasicBlend();
				if (rapidExpand)
					fx.setRapidExpand();
				if (noSlowdown)
					fx.setNoSlowdown();
				if (alphaFade)
					fx.setAlphaFading();
				if (cyclingColor)
					fx.setCyclingColor(1);
				return fx;
			}
			else {
				return null;
			}
		}

		@SideOnly(Side.CLIENT)
		public void sendData() {
			NBTTagCompound nbt = new NBTTagCompound();
			this.writeToNBT(nbt);
			location.writeToNBT("loc", nbt);
			ReikaPacketHelper.sendNBTPacket(ChromatiCraft.packetChannel, ChromaPackets.PARTICLESPAWNER.ordinal(), nbt, new PacketTarget.ServerTarget());
		}

		public void writeToNBT(NBTTagCompound NBT) {
			NBT.setInteger("color", particleColor);

			NBT.setBoolean("collide", particleCollision);
			NBT.setBoolean("rapid", rapidExpand);
			NBT.setBoolean("noslow", noSlowdown);
			NBT.setBoolean("afade", alphaFade);
			NBT.setBoolean("cycle", cyclingColor);

			NBT.setString("icon", particleIcon.name());

			NBTTagCompound tag = new NBTTagCompound();
			particleRate.writeToNBT(tag);
			NBT.setTag("rate", tag);

			particleLife.writeToNBT("life", NBT);

			particleSize.writeToNBT("size", NBT);

			particleGravity.writeToNBT("gravity", NBT);

			particlePositionX.writeToNBT("spawnX", NBT);

			particlePositionY.writeToNBT("spawnY", NBT);

			particlePositionZ.writeToNBT("spawnZ", NBT);

			particleVelocityX.writeToNBT("speedX", NBT);

			particleVelocityY.writeToNBT("speedY", NBT);

			particleVelocityZ.writeToNBT("speedZ", NBT);

			location.writeToNBT("loc", NBT);
		}

		public void readFromNBT(NBTTagCompound NBT) {
			particleColor = NBT.getInteger("color");

			particleCollision = NBT.getBoolean("collide");
			rapidExpand = NBT.getBoolean("rapid");
			noSlowdown = NBT.getBoolean("noslow");
			alphaFade = NBT.getBoolean("afade");
			cyclingColor = NBT.getBoolean("cycle");

			try {
				particleIcon = ChromaIcons.valueOf(NBT.getString("icon"));
			}
			catch (IllegalArgumentException e) {
				ChromatiCraft.logger.logError("Tried to load invalid particle type '"+NBT.getString("icon")+"' from NBT.");
				e.printStackTrace();
			}

			NBTTagCompound tag = NBT.getCompoundTag("rate");
			particleRate = BoundedValue.readFromNBT(tag);

			particleLife.readFromNBT("life", NBT);

			particleSize.readFromNBT("size", NBT);

			particleGravity.readFromNBT("gravity", NBT);

			particlePositionX.readFromNBT("spawnX", NBT);

			particlePositionY.readFromNBT("spawnY", NBT);

			particlePositionZ.readFromNBT("spawnZ", NBT);

			particleVelocityX.readFromNBT("speedX", NBT);

			particleVelocityY.readFromNBT("speedY", NBT);

			particleVelocityZ.readFromNBT("speedZ", NBT);

			location = Coordinate.readFromNBT("loc", NBT);
		}

	}

	public static class VariableValue<N extends Number> {

		private BoundedValue<N> bounds;
		private BoundedValue<N> variation;

		private VariableValue(N min, N max) {
			bounds = new BoundedValue(min, max);
			variation = new BoundedValue(0D, this.getMaxVariance(), 0);
		}

		private VariableValue(N min, N max, N init) {
			bounds = new BoundedValue(min, max, init);
			variation = new BoundedValue(0D, this.getMaxVariance(), 0);
		}

		public VariableValue<N> setStep(N step) {
			bounds.setStep(step);
			variation.setStep(step);
			return this;
		}

		public double getValue() {
			return bounds.getValue();
		}

		public double getVariation() {
			return variation.getValue();
		}

		public boolean increase() {
			if (bounds.increase()) {
				this.onValueChange();
				return true;
			}
			return false;
		}

		public boolean decrease() {
			if (bounds.decrease()) {
				this.onValueChange();
				return true;
			}
			return false;
		}

		public boolean increaseVariation() {
			if (variation.increase()) {
				return true;
			}
			return false;
		}

		public boolean decreaseVariation() {
			if (variation.decrease()) {
				return true;
			}
			return false;
		}

		public void setValue(N value) {
			bounds.setValue(value);
			this.onValueChange();
		}

		private void onValueChange() {
			variation = new BoundedValue(0D, this.getMaxVariance(), variation.getValue()).setStep(bounds.getStep());
		}

		private double getMaxVariance() {
			return Math.min(Math.abs(Math.abs(bounds.getMinValue())-Math.abs(bounds.getValue())), Math.abs(Math.abs(bounds.getMaxValue())-Math.abs(bounds.getValue())));
		}

		public void writeToNBT(String s, NBTTagCompound NBT) {
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound tag1 = new NBTTagCompound();
			NBTTagCompound tag2 = new NBTTagCompound();
			bounds.writeToNBT(tag1);
			variation.writeToNBT(tag2);
			tag.setTag("bounds", tag1);
			tag.setTag("vary", tag2);
			NBT.setTag(s, tag);
			//ReikaJavaLibrary.pConsole("Writing vary "+variation);
		}

		public void readFromNBT(String s, NBTTagCompound NBT) {
			NBTTagCompound tag = NBT.getCompoundTag(s);
			NBTTagCompound tag1 = tag.getCompoundTag("bounds");
			NBTTagCompound tag2 = tag.getCompoundTag("vary");
			bounds = BoundedValue.readFromNBT(tag1);
			variation = BoundedValue.readFromNBT(tag2);
			//ReikaJavaLibrary.pConsole("Reading vary "+variation);
		}

		@Override
		public String toString() {
			return bounds.toString()+" & "+variation.toString();
		}

	}

}
