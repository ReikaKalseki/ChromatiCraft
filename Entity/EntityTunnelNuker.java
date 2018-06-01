package Reika.ChromatiCraft.Entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Block.Decoration.BlockEtherealLight.Flags;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Interfaces.Entity.DestroyOnUnload;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EntityTunnelNuker extends EntityLiving implements DestroyOnUnload {

	public EntityTunnelNuker(World world) {
		super(world);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		int x = MathHelper.floor_double(posX);
		int z = MathHelper.floor_double(posZ);

		if (ticksExisted%16 == 0) {
			if (worldObj.isRemote) {
				this.doParticles();
			}
			else if (worldObj.provider.dimensionId != ExtraChromaIDs.DIMID.getValue() && ticksExisted%64 == 0) {
				int y = MathHelper.floor_double(posY);
				if (worldObj.getBlock(x, y, z).isAir(worldObj, x, y, z)) {
					worldObj.setBlock(x, y, z, ChromaBlocks.LIGHT.getBlockInstance(), Flags.DECAY.getFlag(), 3);
				}
			}
		}

		rotationPitch = 0;
		//rotationYaw = 0;
		if (ticksExisted%8 == 0)
			rotationYaw += Math.signum(System.identityHashCode(this));
		prevRotationYaw = rotationYaw;

		onGround = false;
		isAirBorne = true;

		int top = worldObj.getTopSolidOrLiquidBlock(x, z);

		double vy = motionY;

		if (posY-top < 8) {
			//posY += 0.125;
			//motionY = 0;//Math.max(motionY, 0.0625);
		}
		else {
			//motionY = Math.max(motionY, -0.03125);
			//motionY = 0;
		}

		double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.075, 0, rotationYaw);

		motionY = MathHelper.clamp_double(((top-posY)+8)/32D, -0.08, 0.125);
		motionX = xyz[0];
		motionZ = xyz[2];
		velocityChanged = true;
		/*
		if (Math.abs(motionY-vy) > 0.0625) {
			motionY = Math.max(motionY*0.25+vy*0.75, -0.03125);
		}

		motionY += 0.035;*/
	}

	@SideOnly(Side.CLIENT)
	private void doParticles() {
		float s = (rand.nextFloat()*0.75F+0.25F)*7;
		int l = ReikaRandomHelper.getRandomBetween(10, 60);
		int c = ReikaColorAPI.getModifiedHue(0xff0000, rand.nextInt(60));
		EntityBlurFX fx = new EntityBlurFX(worldObj, posX, posY+0.9, posZ);
		fx.setRapidExpand().setAlphaFading().setScale(s).setLife(l).setColor(c);//.setPositionController();
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public boolean getCanSpawnHere() {
		return !ReikaEntityHelper.existsAnotherValidEntityWithin(this, 32, new ReikaEntityHelper.ClassEntitySelector(this.getClass()));
	}

	@Override
	public void destroy() {
		this.setDead();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		if (nbt.getBoolean("isdead"))
			this.setDead();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setBoolean("isdead", isDead);
	}

	@Override
	public boolean getAlwaysRenderNameTag() {
		return false;
	}

}
