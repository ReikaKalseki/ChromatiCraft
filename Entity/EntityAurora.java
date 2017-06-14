/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.World.Dimension.Rendering.Aurora;
import Reika.DragonAPI.Base.InertEntity;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EntityAurora extends InertEntity implements IEntityAdditionalSpawnData {

	private AuroraData data = new AuroraData();

	@SideOnly(Side.CLIENT)
	private Aurora aurora;

	public EntityAurora(World world, AuroraData dat) {
		this(world);
		data = dat;
		if (worldObj.isRemote)
			this.initAurora();
		double x = (dat.pos2X+dat.pos1X)/2;
		double y = (dat.pos2Y+dat.pos1Y)/2;
		double z = (dat.pos2Z+dat.pos1Z)/2;
		this.setLocationAndAngles(x, y, z, 0, 0);
		this.setSize(0.5F, 0.5F);
	}

	public EntityAurora(World world) {
		super(world);

		noClip = true;
		ignoreFrustumCheck = true;
	}

	@Override
	protected void entityInit() {

	}

	@SideOnly(Side.CLIENT)
	private void initAurora() {
		aurora = data.createAurora();
	}

	@SideOnly(Side.CLIENT)
	public Aurora getAurora() {
		return aurora;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (worldObj.isRemote) {
			aurora.update();

			//EntityFX fx = new EntityBlurFX(worldObj, posX, posY, posZ).setScale(4);
			//Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound NBT) {
		NBTTagCompound tag = NBT.getCompoundTag("data");
		data.readFromNBT(tag);
		if (worldObj.isRemote)
			this.initAurora();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound NBT) {
		NBTTagCompound tag = new NBTTagCompound();
		data.writeToNBT(tag);
		NBT.setTag("data", tag);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		data.writeData(buf);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		data.readData(buf);
		if (worldObj.isRemote)
			this.initAurora();
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public boolean isInRangeToRenderDist(double dist) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double par2, double par4, double par6) {
		return true;
	}

	public static class AuroraData {

		private double pos1X;
		private double pos1Y;
		private double pos1Z;
		private double pos2X;
		private double pos2Y;
		private double pos2Z;

		private int color1;
		private int color2;

		private double variance;
		private double speed;
		private double segmentSize;

		private AuroraData() {

		}

		public AuroraData(double x1, double y1, double z1, double x2, double y2, double z2, int c1, int c2/*, double v*/, double sp/*, double s*/) {
			pos1X = x1;
			pos1Y = y1;
			pos1Z = z1;
			pos2X = x2;
			pos2Y = y2;
			pos2Z = z2;
			color1 = c1;
			color2 = c2;
			//variance = v;
			//segmentSize = s;
			speed = sp;
		}

		public void writeToNBT(NBTTagCompound tag) {
			tag.setDouble("p1x", pos1X);
			tag.setDouble("p1y", pos1Y);
			tag.setDouble("p1z", pos1Z);
			tag.setDouble("p2x", pos2X);
			tag.setDouble("p2y", pos2Y);
			tag.setDouble("p2z", pos2Z);
			//tag.setDouble("var", variance);
			tag.setDouble("speed", speed);
			//tag.setDouble("seg", segmentSize);
			tag.setInteger("c1", color1);
			tag.setInteger("c2", color2);
		}

		public void readFromNBT(NBTTagCompound tag) {
			pos1X = tag.getDouble("p1x");
			pos1Y = tag.getDouble("p1y");
			pos1Z = tag.getDouble("p1z");
			pos2X = tag.getDouble("p2x");
			pos2Y = tag.getDouble("p2y");
			pos2Z = tag.getDouble("p2z");
			//variance = tag.getDouble("var");
			speed = tag.getDouble("speed");
			//segmentSize = tag.getDouble("seg");
			color1 = tag.getInteger("c1");
			color2 = tag.getInteger("c2");
		}

		public void writeData(ByteBuf buf) {
			buf.writeDouble(pos1X);
			buf.writeDouble(pos1Y);
			buf.writeDouble(pos1Z);
			buf.writeDouble(pos2X);
			buf.writeDouble(pos2Y);
			buf.writeDouble(pos2Z);
			//buf.writeDouble(variance);
			buf.writeDouble(speed);
			//buf.writeDouble(segmentSize);
			buf.writeInt(color1);
			buf.writeInt(color2);
		}

		public void readData(ByteBuf buf) {
			pos1X = buf.readDouble();
			pos1Y = buf.readDouble();
			pos1Z = buf.readDouble();
			pos2X = buf.readDouble();
			pos2Y = buf.readDouble();
			pos2Z = buf.readDouble();
			//variance = buf.readDouble();
			speed = buf.readDouble();
			//segmentSize = buf.readDouble();
			color1 = buf.readInt();
			color2 = buf.readInt();
		}

		@SideOnly(Side.CLIENT)
		public Aurora createAurora() {
			return new Aurora(color1, color2, speed, /*variance, segmentSize, */pos1X, pos1Y, pos1Z, pos2X, pos2Y, pos2Z);
		}
	}

}
