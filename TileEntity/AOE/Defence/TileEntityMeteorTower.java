/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Defence;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Entity.EntityMeteorShot;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


//Put multiaura repeater in bottom of tower?
public class TileEntityMeteorTower extends CrystalReceiverBase implements NBTTile, MultiBlockChromaTile {

	private static final ElementTagCompound[] required = new ElementTagCompound[3];

	public static final MeteorShot[] attacks = new MeteorShot[3];

	public static final int[] COLORS = {0x75F922, 0xFDCE0E, 0xEA3520};

	public static final int MINRANGE = 12;

	private static final Comparator<EntityLiving> mobPriority = new MobPriorityComparator();

	static {
		for (int i = 0; i < 3; i++) {
			required[i] = new ElementTagCompound();
			required[i].addValueToColor(CrystalElement.PINK, 1000*ReikaMathLibrary.intpow2(i+1, 2));
			required[i].addValueToColor(CrystalElement.ORANGE, 200+100*i*i);
			if (i > 0) {
				required[i].addValueToColor(CrystalElement.LIME, 50*(i+1));
				if (i > 1)
					required[i].addValueToColor(CrystalElement.PURPLE, 50*(i*i+1));
			}
		}

		attacks[0] = new MeteorShot(48, 2, 8, 1, 100);
		attacks[1] = new MeteorShot(48, 4, 16, 4, 80);
		attacks[2] = new MeteorShot(64, 8, 24, 10, 80);
	}

	private static class MobPriorityComparator implements Comparator<EntityLiving> {

		private MobPriorityComparator() {

		}

		@Override
		public int compare(EntityLiving o1, EntityLiving o2) {
			return -Integer.compare(this.getWeight(o1), this.getWeight(o2));
		}

		private int getWeight(EntityLiving e) {
			if (e instanceof EntityCreeper) {
				return 30;
			}
			else if (e instanceof EntityEnderman) {
				return 20;
			}
			else if (e instanceof EntitySkeleton || e instanceof EntityBlaze) {
				return 10;
			}
			else if (e instanceof EntityWither) {
				return 100;
			}
			return 0;
		}

	}

	public static class MeteorShot {

		public final double range;
		public final double splashRange;

		public final int baseDamage;
		public final int fireDuration;

		public final int chargeTime;

		private MeteorShot(int r, int s, int d, int f, int c) {
			range = r;
			splashRange = s;
			baseDamage = d;
			fireDuration = f;
			chargeTime = c;
		}

	}

	private int tier;

	private boolean hasStructure = false;

	private int fireTick;

	private StepTimer scanTimer = new StepTimer(5);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (hasStructure) {
			//if (!world.isRemote) {
			boolean flag = false;
			if (energy.containsAtLeast(required[tier])) {
				scanTimer.update();
				if (scanTimer.checkCap() || fireTick > 0) {
					if (this.searchAndFire(world, x, y, z)) {
						fireTick++;
						flag = true;
					}
				}
			}
			if (!flag) {
				fireTick = Math.max(fireTick-2, 0);
			}
			//}
			//else {
			if (world.isRemote) {
				if (fireTick > 0) {
					this.doChargingFX(world, x, y, z);
				}
				this.doIdleParticles(world, x, y, z);
			}
		}

		if (!world.isRemote && hasStructure && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.validateStructure();
	}

	@SideOnly(Side.CLIENT)
	private void doIdleParticles(World world, int x, int y, int z) {
		double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 2.5);
		double py = ReikaRandomHelper.getRandomBetween(y-11, y+2);
		double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 2.5);
		float s = (float)ReikaRandomHelper.getRandomBetween(2D, 3D);
		float g = (float)ReikaRandomHelper.getRandomPlusMinus(0D, 0.25);
		EntityFX fx1 = new EntityBlurFX(world, px, py, pz).setColor(COLORS[tier]).setScale(s).setLife(40).setGravity(g).setColliding();
		EntityFX fx2 = new EntityBlurFX(world, px, py, pz).setColor(0xffffff).setScale(s*0.5F).setLife(40).setGravity(g).setColliding().lockTo(fx1).setIcon(ChromaIcons.FADE_RAY);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
	}

	@SideOnly(Side.CLIENT)
	private void doChargingFX(World world, int x, int y, int z) {
		double f = fireTick/(double)attacks[tier].chargeTime;
		double posY = y-6.5+0.5+4*Math.sin(0.03125*this.getTicksExisted()/(1+1D/attacks[tier].chargeTime-0.5625*Math.pow(f, 1.5)));
		int n = (int)Math.min(16, 1+16*f*2);
		for (int i = 0; i < n; i++) {
			double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 1);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY, 0.75);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 1);
			float s = 2.5F+4F*(float)f;
			EntityFX fx1 = new EntityBlurFX(world, px, py, pz).setColor(COLORS[tier]).setScale(s).setLife(8).setRapidExpand();
			EntityFX fx2 = new EntityBlurFX(world, px, py, pz).setColor(0xffffff).setScale(s*0.67F).setLife(8).setRapidExpand().lockTo(fx1);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doFireFX(World world, int x, int y, int z) {
		int n = 24+rand.nextInt(48);
		for (int i = 0; i < n; i++) {
			double px = x+rand.nextDouble();
			double py = y+2+rand.nextDouble();
			double pz = z+rand.nextDouble();
			float s = 2.5F;
			double a1 = rand.nextDouble()*360;
			double a2 = rand.nextDouble()*360;
			EntityFloatingSeedsFX fx = (EntityFloatingSeedsFX)new EntityFloatingSeedsFX(world, px, py, pz, a1, a2).setColor(COLORS[tier]).setScale(s).setLife(40).setRapidExpand().setIcon(ChromaIcons.NODE2);
			fx.particleVelocity *= 4;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		for (int i = 0; i < 96; i++) {
			double posY = ReikaRandomHelper.getRandomPlusMinus(y-6.5+0.5, 4);
			double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 1.5);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY, 0.75);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 1.5);
			float s = 7.5F;
			EntityFX fx1 = new EntityBlurFX(world, px, py, pz).setColor(COLORS[tier]).setScale(s).setLife(12).setRapidExpand();
			EntityFX fx2 = new EntityBlurFX(world, px, py, pz).setColor(0xffffff).setScale(s*0.67F).setLife(12).setRapidExpand().lockTo(fx1);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	private boolean searchAndFire(World world, int x, int y, int z) {
		double r = attacks[tier].range;
		List<EntityLiving> li = world.getEntitiesWithinAABB(EntityLiving.class, ReikaAABBHelper.getBlockAABB(x, y, z).expand(r, 200, r));
		if (!li.isEmpty()) {
			Collections.sort(li, mobPriority);
			for (EntityLiving e : li) {
				if (this.canAttack(e)) {
					this.queueAttack(e);
					return true;
				}
			}
		}
		return false;
	}

	private void queueAttack(EntityLiving e) {
		float f = fireTick/(float)attacks[tier].chargeTime;
		ChromaSounds.KILLAURA_CHARGE.playSoundAtBlock(this, 0.25F+f, 0.5F+0.5F*f);
		if (fireTick >= attacks[tier].chargeTime) {
			this.doAttack(e);
		}
	}

	private void doAttack(EntityLiving e) {
		ChromaSounds.KILLAURA.playSoundAtBlock(this, 2, 1-0.03125F);
		fireTick = 0;
		if (worldObj.isRemote) {
			this.doFireFX(worldObj, xCoord, yCoord, zCoord);
		}
		if (!worldObj.isRemote) {
			EntityMeteorShot m = new EntityMeteorShot(this, e);
			worldObj.spawnEntityInWorld(m);
		}
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(6, 6, 6);
		List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase in : li) {
			ReikaEntityHelper.knockbackEntityFromPos(xCoord+0.5, yCoord+0.5, zCoord+0.5, in, 2);
		}
		this.drainEnergy(required[tier]);
	}

	private boolean canAttack(EntityLiving e) {
		if (e.isDead || e.getHealth() <= 0 || e instanceof EntityFlying || !e.onGround)
			return false;
		if (!ReikaEntityHelper.isHostile(e))
			return false;
		if (e instanceof EntitySlime)
			if (!(e instanceof EntityMagmaCube || ((EntitySlime)e).getSlimeSize() > 1))
				return false;
		if (!ReikaEntityHelper.isNearSkylight(e)) {
			return false;
		}
		double d = e.getDistance(xCoord+0.5, e.posY, zCoord+0.5);
		return d >= MINRANGE && d <= attacks[tier].range;
	}

	private void checkAndRequest() {
		for (CrystalElement e : required[tier].elementSet()) {
			int capacity = this.getMaxStorage(e);
			int space = capacity-this.getEnergy(e);
			if (space > 0) {
				this.requestEnergy(e, space);
			}
		}
	}

	public void validateStructure() {
		ChromaStructures struct = null;
		switch(tier) {
			case 0:
				struct = ChromaStructures.METEOR1;
				break;
			case 1:
				struct = ChromaStructures.METEOR2;
				break;
			case 2:
				struct = ChromaStructures.METEOR3;
				break;
		}
		hasStructure = !worldObj.isRemote && struct.getArray(worldObj, xCoord, yCoord, zCoord).matchInWorld();
		//ReikaJavaLibrary.pConsole(hasStructure, Side.SERVER);
		if (!hasStructure) {
			fireTick = 0;
		}
		this.syncAllData(false);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.hasStructure() ? ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(6, 4, 6) : super.getRenderBoundingBox();
	}

	public boolean hasStructure() {
		return hasStructure;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");
		tier = NBT.getInteger("tier");
		fireTick = NBT.getInteger("fire");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);
		NBT.setInteger("fire", fireTick);
		NBT.setInteger("tier", tier);
	}

	public void upgrade() {
		tier++;
	}

	public int getTier() {
		return tier;
	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return required[tier].contains(e);
	}

	@Override
	public int maxThroughput() {
		return 100+200*tier;
	}

	@Override
	public boolean canConduct() {
		return hasStructure;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 20000*(tier+1);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.METEOR;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		super.getTagsToWriteToStack(NBT);
		NBT.setInteger("tier", tier);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		if (is.stackTagCompound == null)
			return;
		super.setDataFromItemStackTag(is);
		tier = is.stackTagCompound.getInteger("tier");
	}

}
