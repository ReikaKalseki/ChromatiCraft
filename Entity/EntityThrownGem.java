/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EntityThrownGem extends EntityThrowable implements IEntityAdditionalSpawnData {

	private CrystalElement color;
	private boolean impacted = false;

	private int lifespan;
	private int ticks;

	public EntityThrownGem(World world) {
		super(world);
	}

	public EntityThrownGem(World world, EntityPlayer ep, CrystalElement e) {
		super(world, ep);
		color = e;
		lifespan = 40+rand.nextInt(80);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataWatcher.addObject(24, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		dataWatcher.updateObject(24, impacted ? 1 : 0);

		velocityChanged = true;
		if (impacted) {
			motionX = 0;
			motionY = 0;
			motionZ = 0;
			posY = worldObj.getTopSolidOrLiquidBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posZ));
			ticks++;
			this.doTickEffect();
			if (ticks >= lifespan) {
				this.endOfLife();
			}
		}
	}

	@Override
	public final boolean canAttackWithItem()
	{
		return false;
	}

	@Override
	public final boolean canRenderOnFire()
	{
		return false;
	}

	@Override
	public final boolean isEntityInvulnerable()
	{
		return true;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	@Override
	public void applyEntityCollision(Entity e)
	{

	}

	@Override
	protected float getGravityVelocity() {
		return 0.03F;
	}

	@Override
	protected float func_70182_d() {
		return 1.5F;
	}

	@Override
	protected float func_70183_g() {
		return super.func_70183_g();//-20.0F;
	}

	private void doTickEffect() {
		switch(color) {
			case BLACK:
				break;
			case RED:
				break;
			case GREEN:
				break;
			case BROWN:
				break;
			case BLUE:
				break;
			case PURPLE:
				break;
			case CYAN:
				break;
			case LIGHTGRAY:
				break;
			case GRAY:
				break;
			case PINK:
				break;
			case LIME:
				break;
			case YELLOW:
				break;
			case LIGHTBLUE:
				break;
			case MAGENTA:
				break;
			case ORANGE:
				break;
			case WHITE:
				break;
		}
		if (worldObj.isRemote)
			this.doTickEffectFX();
	}

	private void doImpactEffect() {
		switch(color) {
			case BLACK:
				break;
			case RED:
				break;
			case GREEN:
				break;
			case BROWN:
				break;
			case BLUE:
				break;
			case PURPLE:
				break;
			case CYAN:
				break;
			case LIGHTGRAY:
				break;
			case GRAY:
				break;
			case PINK:
				break;
			case LIME:
				break;
			case YELLOW:
				break;
			case LIGHTBLUE:
				break;
			case MAGENTA:
				break;
			case ORANGE:
				break;
			case WHITE:
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	private void doTickEffectFX() {
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
		double vy = ReikaRandomHelper.getRandomBetween(0, 0.125);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
		EntityFX fx = new EntityRuneFX(worldObj, posX, posY, posZ, vx, vy, vz, color);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private void endOfLife() {
		this.setDead();
		ChromaSounds.POWERDOWN.playSound(this, 0.5F, 2F);
	}

	@Override
	protected void onImpact(MovingObjectPosition pos) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
		this.doImpactEffect();
		if (!worldObj.isRemote)
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.THROWNGEM.ordinal(), new PacketTarget.RadiusTarget(this, 32), this.getEntityId(), color.ordinal());
		velocityChanged = true;
		impacted = true;
	}

	@SideOnly(Side.CLIENT)
	public void doImpactFX(CrystalElement e) {

	}

	public boolean hasImpacted() {
		return dataWatcher.getWatchableObjectInt(24) > 0;
	}

	public CrystalElement getColor() {
		return color;
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(color.ordinal());
		buf.writeInt(lifespan);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		color = CrystalElement.elements[buf.readInt()];
		lifespan = buf.readInt();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound NBT) {
		super.readEntityFromNBT(NBT);

		color = CrystalElement.elements[NBT.getInteger("color")];
	}


	@Override
	public void writeEntityToNBT(NBTTagCompound NBT) {
		super.writeEntityToNBT(NBT);

		NBT.setInteger("color", color.ordinal());
	}

}
