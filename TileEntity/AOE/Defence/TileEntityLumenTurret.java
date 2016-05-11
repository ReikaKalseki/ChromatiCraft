/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Defence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.LumenTurretDamage;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Interfaces.Entity.TameHostile;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityLumenTurret extends TileEntityChromaticBase {

	private final HashMap<UUID, Integer> attackCooldown = new HashMap();

	private final StepTimer attackTimer = new StepTimer(5);

	private static final RayTracer tracer = new RayTracer(0, 0, 0, 0, 0, 0);

	public static final int MAX_UPGRADES = 4;

	//public boolean hasPassiveUpgrade;
	//public boolean hasPlayerUpgrade;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TURRET;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		//NBT.setBoolean("passive", hasPassiveUpgrade);
		//NBT.setBoolean("player", hasPlayerUpgrade);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		//hasPassiveUpgrade = NBT.getBoolean("passive");
		//hasPlayerUpgrade = NBT.getBoolean("player");
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			attackTimer.update();
			if (attackTimer.checkCap()) {
				if (world.canBlockSeeTheSky(x, y+1, z) || TurretUpgrades.SKY.check(this))
					this.attackEntities(world, x, y, z);
			}

			this.decrementTicks();
		}
		else {
			this.doParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		TurretUpgrades u = TurretUpgrades.list[rand.nextInt(TurretUpgrades.list.length)];
		if (u.check(this)) {
			float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(1.25, 0.25);
			int l = 10+rand.nextInt(30);
			EntityFX fx = new EntityBlurFX(world, x+0.5, y+0.625, z+0.5).setColor(u.color.getColor()).setGravity(g).setLife(l).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private void decrementTicks() {
		if (!attackCooldown.isEmpty()) {
			HashSet<UUID> rem = new HashSet();
			for (UUID id : attackCooldown.keySet()) {
				int time = attackCooldown.get(id);
				if (time > 1) {
					attackCooldown.put(id, time-1);
				}
				else {
					rem.add(id);
				}
			}
			for (UUID id : rem) {
				attackCooldown.remove(id);
			}
		}
	}

	private void attackEntities(World world, int x, int y, int z) {
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBox(world, x, y, z));
		for (EntityLivingBase e : li) {
			if (this.shouldAttack(e)) {
				this.tryAttackEntity(world, x, y, z, e);
			}
		}
	}

	private boolean shouldAttack(EntityLivingBase e) {
		if (e instanceof TameHostile || (e instanceof EntityTameable && ((EntityTameable)e).isTamed()))
			return false;
		if (ReikaEntityHelper.isHostile(e))
			return true;
		if (e instanceof EntityPlayer)
			return !this.isOwnedByPlayer((EntityPlayer)e) && this.isHostile((EntityPlayer)e) && TurretUpgrades.PLAYERS.check(this);
		return TurretUpgrades.PASSIVE.check(this);
	}

	private boolean isHostile(EntityPlayer e) {
		return false;
	}

	private void tryAttackEntity(World world, int x, int y, int z, EntityLivingBase e) {
		if (!attackCooldown.containsKey(e.getUniqueID())) {
			if (this.canSeeEntity(world, x, y, z, e)) {
				this.doAttackEntity(world, x, y, z, e);
			}
		}
	}

	private boolean canSeeEntity(World world, int x, int y, int z, Entity e) {
		int n = 2;
		for (int i = 0; i <= n; i++) {
			tracer.setOrigins(x+0.5, y+0.5625, z+0.5, e.posX, e.posY+e.height*i/n, e.posZ);
			//ReikaJavaLibrary.pConsole(e+":"+tracer.isClearLineOfSight(world)+" of "+e.height, e instanceof EntityChicken);
			if (tracer.isClearLineOfSight(world))
				return true;
		}
		return false;
	}

	private void doAttackEntity(World world, int x, int y, int z, EntityLivingBase e) {
		attackCooldown.put(e.getUniqueID(), this.getAttackCooldown(e));
		e.attackEntityFrom(new LumenTurretDamage(this), this.getAttackDamage(e));
		e.hurtResistantTime = 0;
		if (TurretUpgrades.FIRE.check(this))
			e.setFire(2);
		ChromaSounds.DISCHARGE.playSoundAtBlock(this, 0.25F, 1.5F);
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.TURRETATTACK.ordinal(), this, 32, e.getEntityId());
	}

	@SideOnly(Side.CLIENT)
	public void doAttackParticles(int entityID) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(entityID);

		double px = xCoord+0.5;
		double py = yCoord+0.5625;
		double pz = zCoord+0.5;

		double dx = e.posX-px;
		double dy = e.posY+e.height/2-py;
		double dz = e.posZ-pz;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);

		double v = 0.75;

		double vx = v*dx/dd;
		double vy = v*dy/dd;
		double vz = v*dz/dd;

		int n = 2+rand.nextInt(4);
		double dv = 0.03125;
		int l = 2+(int)dd;

		for (int i = 0; i < n; i++) {
			double dvx = ReikaRandomHelper.getRandomPlusMinus(vx, dv);
			double dvy = ReikaRandomHelper.getRandomPlusMinus(vy, dv);
			double dvz = ReikaRandomHelper.getRandomPlusMinus(vz, dv);

			EntityFX fx = new EntityFlareFX(CrystalElement.PINK, worldObj, px, py, pz, dvx, dvy, dvz).setNoGravity().setLife(l).setScale(2);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private float getAttackDamage(EntityLivingBase e) {
		return TurretUpgrades.DAMAGE.check(this) ? 7 : 4;
	}

	private int getAttackCooldown(EntityLivingBase e) {
		if (TurretUpgrades.FIRERATE.check(this))
			return e instanceof EntityEnderman || e instanceof EntityCreeper ? 2 : 5;
		return e instanceof EntityEnderman || e instanceof EntityCreeper ? 5 : 10;
	}

	private AxisAlignedBB getBox(World world, int x, int y, int z) {
		boolean flag = TurretUpgrades.RANGE.check(this);
		int r = flag ? 10 : 6;
		int r2 = flag ? 4 : 2;
		return ReikaAABBHelper.getBlockAABB(x, y, z).expand(r, r2, r);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static enum TurretUpgrades {
		PASSIVE(CrystalElement.GREEN),
		PLAYERS(CrystalElement.PURPLE),
		RANGE(CrystalElement.LIME),
		DAMAGE(CrystalElement.PINK),
		FIRERATE(CrystalElement.LIGHTBLUE),
		SKY(CrystalElement.BLUE),
		FIRE(CrystalElement.ORANGE);

		public final CrystalElement color;

		private static final TurretUpgrades[] list = values();

		private TurretUpgrades(CrystalElement e) {
			color = e;
		}

		public boolean check(TileEntityLumenTurret te) {
			int y = te.yCoord-1;
			Block b = te.worldObj.getBlock(te.xCoord, y, te.zCoord);
			if (b == ChromaBlocks.PYLONSTRUCT.getBlockInstance() && te.worldObj.getBlockMetadata(te.xCoord, y, te.zCoord) == StoneTypes.MULTICHROMIC.ordinal())
				return true;
			while (y > 0 && y >= te.yCoord-MAX_UPGRADES && b == ChromaBlocks.RUNE.getBlockInstance()) {
				if (te.worldObj.getBlockMetadata(te.xCoord, y, te.zCoord) == color.ordinal())
					return true;
				y--;
			}
			return false;
		}
	}

}
