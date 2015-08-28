package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.StepTimer;
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

	private static final RayTracer tracer = new RayTracer(0, 0, 0, 0, 0 ,0);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TURRET;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		attackTimer.update();
		if (attackTimer.checkCap()) {
			if (world.canBlockSeeTheSky(x, y+1, z))
				this.attackEntities(world, x, y, z);
		}

		this.decrementTicks();
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
			if (ReikaEntityHelper.isHostile(e)) {
				this.tryAttackEntity(world, x, y, z, e);
			}
		}
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
		e.attackEntityFrom(DamageSource.magic, this.getAttackDamage(e));
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
		return 4;
	}

	private int getAttackCooldown(EntityLivingBase e) {
		return e instanceof EntityEnderman || e instanceof EntityCreeper ? 5 : 10;
	}

	private AxisAlignedBB getBox(World world, int x, int y, int z) {
		return ReikaAABBHelper.getBlockAABB(x, y, z).expand(6, 2, 6);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
