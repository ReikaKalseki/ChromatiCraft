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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.DragonAPI.Instantiable.EntityTumblingBlock;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.Block.SelectiveMovable;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Interfaces.Entity.CustomProjectile;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class EntityVacuum extends Entity implements IEntityAdditionalSpawnData, CustomProjectile {

	public static final int ACTIVATION_TIME = 60;

	private EntityPlayer firingPlayer;
	private boolean isActivated;
	private int lifespan = 400+rand.nextInt(200)+ACTIVATION_TIME;
	private int entityRange = ReikaRandomHelper.getRandomPlusMinus(10, 2);
	private int blockRange = ReikaRandomHelper.getRandomPlusMinus(6, 2);

	public EntityVacuum(World world, EntityPlayer ep, boolean randomVec) {
		super(world);
		noClip = true;
		firingPlayer = ep;

		Vec3 vec = ep.getLookVec();
		if (randomVec) {
			vec.xCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.yCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.zCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec = vec.normalize();
		}
		double v = 0.85;
		motionX = v*vec.xCoord;
		motionY = v*vec.yCoord;
		motionZ = v*vec.zCoord;

		this.setSize(0.125F, 0.125F);
	}

	public EntityVacuum(World world) {
		super(world);
		noClip = true;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.moveEntity(motionX, motionY, motionZ);
		if (isActivated) {
			if (!worldObj.isRemote) {
				this.suckInBlocks();
				this.suckInEntities();
			}
		}
		else if (ticksExisted >= ACTIVATION_TIME) {
			this.activate();
		}
		else {
			if (worldObj.isRemote) {
				this.travelParticles();
			}
			if (ticksExisted >= ACTIVATION_TIME-10) {
				motionX *= 0.8;
				motionY *= 0.8;
				motionZ *= 0.8;
			}
		}

		if (ticksExisted > lifespan)
			this.destroy();
		ticksExisted++;
	}

	private void destroy() {
		if (!worldObj.isRemote) {
			PacketTarget pt = new PacketTarget.RadiusTarget(this, 48);
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.VACUUMGUNEND.ordinal(), pt, this.getEntityId());
		}
		this.setDead();
	}

	@SideOnly(Side.CLIENT)
	private void travelParticles() {
		int n = 2+rand.nextInt(9);
		for (int i = 0; i < n; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(posX, 0.0625);
			double ry = ReikaRandomHelper.getRandomPlusMinus(posY, 0.0625);
			double rz = ReikaRandomHelper.getRandomPlusMinus(posZ, 0.0625);
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.025);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.025);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25);
			float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			int l = 10+rand.nextInt(30);
			EntityFX fx = new EntitySparkleFX(worldObj, rx, ry, rz, vx, 0, vz).setGravity(g).setScale(s).setLife(l);
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doDestroyParticles(int entityID) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(entityID);
		int n = 92+e.worldObj.rand.nextInt(192);
		for (int i = 0; i < n; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(e.posX, 0.005);
			double ry = ReikaRandomHelper.getRandomPlusMinus(e.posY, 0.005);
			double rz = ReikaRandomHelper.getRandomPlusMinus(e.posZ, 0.005);
			double v = 0.5+e.worldObj.rand.nextDouble();
			double[] vp = ReikaPhysicsHelper.polarToCartesian(v, e.worldObj.rand.nextDouble()*360, e.worldObj.rand.nextDouble()*360);
			float s = 0.5F+e.worldObj.rand.nextFloat()*4;
			int l = 5+e.worldObj.rand.nextInt(20)*(1+e.worldObj.rand.nextInt(2));
			CrystalElement c = CrystalElement.randomElement();
			EntityFX fx = new EntityCCBlurFX(c, e.worldObj, rx, ry, rz, vp[0], vp[1], vp[2]).setNoSlowdown().setScale(s).setRapidExpand().setLife(l);
			fx.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private void activate() {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
		isActivated = true;
	}

	private void suckInEntities() {
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(entityRange, entityRange, entityRange);
		List<Entity> li = worldObj.getEntitiesWithinAABB(Entity.class, box);
		for (Entity e : li) {
			if (!(e instanceof EntityVacuum)) {
				this.suck(e);

				if (e != firingPlayer && this.getDistanceToEntity(e) <= 1) {
					e.attackEntityFrom(DamageSource.magic, 1);
				}
			}
		}
	}

	private void suckInBlocks() {
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);
		for (int i = -blockRange; i <= blockRange; i++) {
			for (int j = -blockRange; j <= blockRange; j++) {
				for (int k = -blockRange; k <= blockRange; k++) {
					if (i*i+j*j+k*k <= blockRange*blockRange+0.5) {
						int dx = x+i;
						int dy = y+j;
						int dz = z+k;
						if (y >= 0 && y <= 255) {
							Block b = worldObj.getBlock(dx, dy, dz);
							int meta = worldObj.getBlockMetadata(dx, dy, dz);
							if (this.canMove(b, meta, worldObj, dx, dy, dz)) {
								EntityFallingBlock e = new EntityTumblingBlock(worldObj, dx, dy, dz, b, worldObj.getBlockMetadata(dx, dy, dz));
								e.field_145812_b = -10000;
								e.field_145813_c = false;
								TileEntity te = worldObj.getTileEntity(dx, dy, dz);
								if (te != null) {
									e.field_145810_d = new NBTTagCompound();
									te.writeToNBT(e.field_145810_d);
								}
								worldObj.setBlockToAir(dx, dy, dz);
								worldObj.spawnEntityInWorld(e);
							}
						}
					}
				}
			}
		}
	}

	private boolean canMove(Block b, int meta, World world, int x, int y, int z) {
		if (b.blockHardness < 0)
			return false;
		if (ReikaBlockHelper.isUnbreakable(world, x, y, z, b, meta, firingPlayer))
			return false;
		if (b.isAir(world, x, y, z))
			return false;
		if (b instanceof SemiUnbreakable)
			return !((SemiUnbreakable)b).isUnbreakable(world, x, y, z, world.getBlockMetadata(x, y, z));
		if (ReikaBlockHelper.isLiquid(b) && world.getBlockMetadata(x, y, z) != 0)
			return false;
		if (b instanceof SelectiveMovable && !((SelectiveMovable)b).canMove(world, x, y, z))
			return false;
		return world.isRemote || ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, x, y, z, (EntityPlayerMP)firingPlayer);
	}

	private void suck(Entity e) {
		double dx = e.posX-posX;
		double dy = e.posY-posY;
		double dz = e.posZ-posZ;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);

		double v = -0.125;
		if (ticksExisted == lifespan) {
			v = 0.5+rand.nextDouble();
			e.motionY += 1+rand.nextDouble()*2;
		}

		e.motionX += v*dx/dd;
		e.motionY += v*dy/dd+0.04; //0.04 is to fight gravity
		e.motionZ += v*dz/dd;

		e.noClip = false;
		e.velocityChanged = true;

		if (e instanceof EntityDimensionFlare) {
			((EntityDimensionFlare)e).aggroTo(firingPlayer);
		}
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound NBT) {
		ticksExisted = NBT.getInteger("tick");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound NBT) {
		NBT.setInteger("tick", ticksExisted);
	}

	@Override
	public float getBrightness(float p_70013_1_)
	{
		return 1;
	}

	@Override
	public int getBrightnessForRender(float p_70070_1_)
	{
		return 15728880;
	}

	@Override
	public final boolean canAttackWithItem()
	{
		return false;
	}

	@Override
	public final boolean isEntityInvulnerable()
	{
		return true;
	}

	@Override
	public final boolean canRenderOnFire()
	{
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(lifespan);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		lifespan = buf.readInt();
	}

	@Override
	public Entity getFiringEntity() {
		return firingPlayer;
	}

}
