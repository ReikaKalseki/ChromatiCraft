package Reika.ChromatiCraft.Entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.ColorData;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonTarget;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class EntityPistonSpline extends EntitySplineProjectile {

	public ColorData color;

	public EntityPistonSpline(World world, Spline s, ColorData clr) {
		super(world, s, 60);
		color = clr;
	}

	public EntityPistonSpline(World world) {
		super(world);
	}

	@Override
	protected void onTick() {
		super.onTick();
		if (!worldObj.isRemote) {
			dataWatcher.updateObject(24, color.red ? 1 : 0);
			dataWatcher.updateObject(25, color.green ? 1 : 0);
			dataWatcher.updateObject(26, color.blue ? 1 : 0);
		}
		else {
			color.red = dataWatcher.getWatchableObjectInt(24) > 0;
			color.green = dataWatcher.getWatchableObjectInt(25) > 0;
			color.blue = dataWatcher.getWatchableObjectInt(26) > 0;
			this.spawnParticle();
			if (ticksExisted%2 == 0)
				ReikaSoundHelper.playClientSound(ChromaSounds.METEOR, this, 0.3F, 2F);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticle() {
		Minecraft mc = Minecraft.getMinecraft();
		int l = 10+rand.nextInt(15);
		if (rand.nextInt(HoldingChecks.MANIPULATOR.isClientHolding() ? 3 : 12) == 0)
			l *= 16;
		double[] r = {0.1875, 0.125, 0.0625};
		for (int i = 0; i < r.length; i++) {
			float s = (1+rand.nextFloat())/(i+1);
			double px = ReikaRandomHelper.getRandomPlusMinus(posX, r[i]);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY, r[i]);
			double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, r[i]);
			EntityFX fx = new EntityBlurFX(worldObj, px, py, pz).setColor(color.getRenderColor()).setIcon(ChromaIcons.FADE_GENTLE).setLife(l).setScale(s);
			mc.effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		color = new ColorData(true);
		color.readFromNBT(tag);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		color.writeToNBT(tag);
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		super.writeSpawnData(data);
		color.writeBuf(data);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		super.readSpawnData(data);
		color = new ColorData(true);
		color.readBuf(data);
	}

	@Override
	protected boolean onEnterBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == ChromaBlocks.PISTONTARGET.getBlockInstance()) {
			((BlockPistonTarget)b).receiveSplineParticle(world, x, y, z, this);
		}
		return !b.isAir(world, x, y, z);
	}

}
