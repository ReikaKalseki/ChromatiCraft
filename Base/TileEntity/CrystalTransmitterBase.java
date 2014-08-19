package Reika.ChromatiCraft.Base.TileEntity;

import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.WorldLocation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public abstract class CrystalTransmitterBase extends TileEntityCrystalBase implements CrystalTransmitter {

	private CrystalTarget target; //need to reset some way
	public int renderAlpha;

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (renderAlpha > 0)
			renderAlpha -= 4;
		if (renderAlpha < 0)
			renderAlpha = 0;
	}

	@Override
	public void markTarget(WorldLocation loc, CrystalElement e) {
		target = new CrystalTarget(loc, e);
		this.onTargetChanged();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (target != null && world.isRemote) {
			//this.spawnParticle();
		}
	}

	private void onTargetChanged() {
		renderAlpha = 512;
		this.syncAllData(true);
	}

	public void clearTarget() {
		target = null;
		this.onTargetChanged();
	}

	public CrystalTarget getTarget() {
		return target;
	}
	/*
	private void spawnParticle(World world, int x, int y, int z) {
		double dd = target.getDistanceTo(x, y, z);
		double vx = (target.xCoord-x)/dd;
		double vy = (target.yCoord-y)/dd;
		double vz = (target.zCoord-z)/dd;
		ForgeDirection dir = dirs[rand.nextInt(6)];
		WorldLocation loc = new WorldLocation(this);
		int t = 5;
		int ang = (this.getTicksExisted()*t)%360;
		float r = 0.3F;

		for (int i = 0; i < 360; i += 90) {
			float rx = (float)(r*Math.sin(Math.toRadians(ang+i))*Math.abs(vx));
			float ry = r*(float)Math.cos(Math.toRadians(ang+i));
			float rz = (float)(r*Math.sin(Math.toRadians(ang+i))*Math.abs(vz));
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityFlareFX(color, world, loc, target, rx, ry, rz));
		}
	}*/

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		target = CrystalTarget.readFromNBT("target", NBT);

		renderAlpha = NBT.getInteger("alpha");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (target != null)
			target.writeToNBT("target", NBT);

		NBT.setInteger("alpha", renderAlpha);
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		return target != null ? INFINITE_EXTENT_AABB : super.getRenderBoundingBox();
	}

	public double getMaxRenderDistance() {
		return super.getMaxRenderDistanceSquared()*8;
	}
}
