package Reika.ChromatiCraft.Entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonTarget;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.RGBColorData;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class EntityPistonSpline extends EntitySplineProjectile {

	public RGBColorData color = RGBColorData.white();

	public EntityPistonSpline(World world, Spline s, RGBColorData clr) {
		super(world, s, 20);
		color = clr;
	}

	public EntityPistonSpline(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(24, 0);
		dataWatcher.addObject(25, 0);
		dataWatcher.addObject(26, 0);
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
			if (ticksExisted%40 == 0)
				ReikaSoundHelper.playClientSound(ChromaSounds.FIRE, this, 0.3F, 0.5F);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticle() {
		Minecraft mc = Minecraft.getMinecraft();
		int n = ReikaRandomHelper.getRandomBetween(1, 3);
		double r = 0.125;
		for (int i = 0; i < n; i++) {
			int l = ReikaRandomHelper.getRandomBetween(20, 60);
			double px = ReikaRandomHelper.getRandomPlusMinus(posX, r);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY, r);
			double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, r);
			float s = (float)ReikaRandomHelper.getRandomBetween(2.25, 3.75);
			float g = rand.nextInt(3) > 0 ? 0 : (float)ReikaRandomHelper.getRandomBetween(0, 0.125);
			int c = color.getRenderColor();//ReikaColorAPI.mixColors(color.getRenderColor(), 0xffffff, 0.75F);
			EntityBlurFX fx = new EntityBlurFX(worldObj, px, py, pz);
			fx.setColor(c).setLife(l).setScale(s).setGravity(g);
			fx.setIcon(ChromaIcons.FADE_GENTLE).setAlphaFading().setRapidExpand();
			mc.effectRenderer.addEffect(fx);

			EntityBlurFX fx2 = new EntityBlurFX(worldObj, px, py, pz);
			fx2.setColor(0xffffff).setLife(l).setScale(s*0.72F).setGravity(g);
			fx2.setIcon(ChromaIcons.FADE).setAlphaFading().setRapidExpand();
			fx2.lockTo(fx);
			mc.effectRenderer.addEffect(fx2);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		color = RGBColorData.white();
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
		color = RGBColorData.white();
		color.readBuf(data);
	}

	@Override
	protected boolean onEnterBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == ChromaBlocks.PISTONTARGET.getBlockInstance()) {
			((BlockPistonTarget)b).receiveSplineParticle(world, x, y, z, this);
			return true;
		}
		return false;//!b.isAir(world, x, y, z);
	}

}
