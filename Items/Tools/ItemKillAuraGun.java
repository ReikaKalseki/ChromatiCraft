/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemProgressGatedTool;
import Reika.ChromatiCraft.Registry.ChromaEnchants;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCFloatingSeedsFX;
import Reika.DragonAPI.Instantiable.Effects.EntityFloatingSeedsFX;
import Reika.DragonAPI.Instantiable.ParticleController.CollectingPositionController;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemKillAuraGun extends ItemProgressGatedTool {

	public static final int CHARGE_TIME = 100;

	public static final int BASE_DAMAGE = 12;
	public static final int MAX_DAMAGE = 18;
	public static final int RANGE = 12;

	public static final float RANGE_FALLOFF = BASE_DAMAGE/2F/RANGE;
	public static final float ANGLE_FALLOFF = BASE_DAMAGE*0.875F/180F;
	public static final float CHARGE_FALLOFF = BASE_DAMAGE/(float)CHARGE_TIME;

	private static int useTick;
	private static int useTickUnbounded;

	public ItemKillAuraGun(int index) {
		super(index, UseResult.PUNISH);
	}

	private static boolean fire(ItemStack is, EntityPlayer ep, int power) {
		power = power+1; //since never reaches 100
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(ep, RANGE);
		List<EntityLivingBase> li = ep.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (Entity e : li) {
			if (e != ep) {
				float dmg = getWallAttackDamage(is, ep, e, power);
				if (dmg > 0) {
					e.attackEntityFrom(new ReikaEntityHelper.WrappedDamageSource(DamageSource.magic, ep), dmg);
				}
			}
		}
		ChromaSounds.KILLAURA.playSound(ep, 1, 0.4F+0.6F*power/CHARGE_TIME);
		if (ep.worldObj.isRemote) {
			doFireParticles(ep, power);
		}
		return false;
	}

	private static float getFalloffFactor(ItemStack is) {
		return 1-ReikaEnchantmentHelper.getEnchantmentLevel(ChromaEnchants.WEAPONAOE.getEnchantment(), is)/10F;
	}

	private static float getRawDamage(ItemStack is) {
		float f = (MAX_DAMAGE-BASE_DAMAGE)/10F;
		return BASE_DAMAGE+ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.power, is)*f;
	}

	@SideOnly(Side.CLIENT)
	private static void doFireParticles(EntityPlayer ep, int power) {
		int n = (128+itemRand.nextInt(128))*power/CHARGE_TIME;
		Vec3 vec = ep.getLookVec();
		double r = 0.5;
		double lx = ep.posX+(vec.xCoord)*r;
		double ly = ep.posY+(vec.yCoord)*r;
		double lz = ep.posZ+(vec.zCoord)*r;
		for (int i = 0; i < n; i++) {
			double phi = itemRand.nextDouble()*360;
			double theta = itemRand.nextDouble()*360;
			boolean flag = false;
			int d = itemRand.nextInt(4);
			if (d > 0) {
				double b = d%2 == 0 ? 10 : 5;
				phi = ReikaRandomHelper.getRandomPlusMinus(90+ep.rotationYawHead, b);
				theta = ReikaRandomHelper.getRandomPlusMinus(-ep.rotationPitch, b);
				flag = true;
			}
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(2D, 1D);
			if (!flag)
				s *= 2;
			ChromaIcons ico = ChromaIcons.FLARE;
			switch(itemRand.nextInt(3)) {
				case 0:
					ico = ChromaIcons.CENTER;
					break;
				case 1:
					ico = ChromaIcons.NODE2;
					break;
			}
			EntityFloatingSeedsFX fx = (EntityFloatingSeedsFX)new EntityCCFloatingSeedsFX(ep.worldObj, lx, ly, lz, phi, theta, ico).setColor(0xffffff).setNoSlowdown().setScale(s);
			fx.particleVelocity *= 2*ReikaRandomHelper.getRandomPlusMinus(4D, 1.5D);
			fx.freedom *= ReikaRandomHelper.getRandomPlusMinus(1.5D, 0.5D);
			fx.angleVelocity *= ReikaRandomHelper.getRandomPlusMinus(1D, 0.75D);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private static float getWallAttackDamage(ItemStack is, EntityPlayer ep, Entity e, int power) {
		double d = e.getDistanceToEntity(ep);
		if (d <= RANGE) {
			double[] angs = ReikaPhysicsHelper.cartesianToPolar(e.posX-ep.posX, e.posY-ep.posY, e.posZ-ep.posZ);
			angs[1] = angs[1]-90-ep.rotationPitch;
			angs[2] = 180-angs[2]-ep.rotationYawHead;
			angs[1] = Math.abs(MathHelper.wrapAngleTo180_double(angs[1]));
			angs[2] = Math.abs(MathHelper.wrapAngleTo180_double(angs[2]));
			//ReikaJavaLibrary.pConsole(ep.rotationYawHead+":"+ep.rotationPitch+":"+Arrays.toString(angs), Side.SERVER);
			double da = ReikaMathLibrary.py3d(angs[2], 0, angs[1]);
			//ReikaJavaLibrary.pConsole(power+":"+((CHARGE_TIME-power)*CHARGE_FALLOFF));
			float f = getFalloffFactor(is);
			double dmg = getRawDamage(is)-RANGE_FALLOFF*f*Math.max(0, (d-2))-ANGLE_FALLOFF*f*da-(CHARGE_TIME-power)*CHARGE_FALLOFF;
			return (float)Math.max(1F, dmg);
		}
		else {
			return 0;
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack is, World world, EntityPlayer ep, int count) {
		count = MathHelper.clamp_int(this.getMaxItemUseDuration(is)-count, 0, CHARGE_TIME);
		//ReikaChatHelper.write(power+"  ->  "+charge);
		if (count > 10)
			this.fire(is, ep, count);
		useTick = 0;
		useTickUnbounded = 0;
		ep.setItemInUse(null, 0);
	}

	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer ep)
	{
		return is;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack is)
	{
		return 72000;//CHARGE_TIME;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack is)
	{
		return EnumAction.bow;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep)
	{
		if (!this.handleUseAllowance(ep))
			ep.setItemInUse(is, this.getMaxItemUseDuration(is));
		return is;
	}

	@Override
	public void onUsingTick(ItemStack is, EntityPlayer ep, int count) {
		//ReikaJavaLibrary.pConsole(count);
		count = this.getMaxItemUseDuration(is)-count;
		useTickUnbounded = count;
		count = MathHelper.clamp_int(count, 0, CHARGE_TIME);
		if (ep.worldObj.isRemote) {
			this.doChargingParticles(ep, count);
			useTick = count;
		}
		if (!ep.worldObj.isRemote)
			ChromaSounds.KILLAURA_CHARGE.playSound(ep, 0.25F+2F*count/CHARGE_TIME, MathHelper.clamp_float(0.5F, 2F*count/CHARGE_TIME, 2F));
		/*
		if (count >= CHARGE_TIME-1) {
			useTick = 0;
			this.fire(ep, count);
			ep.setItemInUse(null, 0);
		}
		 */
	}

	@SideOnly(Side.CLIENT)
	public static int getUseTick() {
		return useTick;
	}

	@SideOnly(Side.CLIENT)
	public static int getUnboundedUseTick() {
		return useTickUnbounded;
	}

	@SideOnly(Side.CLIENT)
	private void doChargingParticles(EntityPlayer ep, int power) {
		double f = Math.pow(0.125F+2F*power/CHARGE_TIME, 2);
		int n = (int)f;
		if (ReikaRandomHelper.doWithChance(f-n))
			n++;
		Vec3 vec = ep.getLookVec();
		double r = 0.5;
		double lx = ep.posX+(vec.xCoord)*r;
		double ly = ep.posY+(vec.yCoord)*r;
		double lz = ep.posZ+(vec.zCoord)*r;
		for (int i = 0; i < n; i++) {
			double dx = ReikaRandomHelper.getRandomPlusMinus(0, 0.25);
			double dy = ReikaRandomHelper.getRandomPlusMinus(0, 0.25);
			double dz = ReikaRandomHelper.getRandomPlusMinus(0, 0.25);
			double px = lx+dx;
			double py = ly+dy;
			double pz = lz+dz;
			double v = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			double vx = -v*dx;
			double vy = -v*dx;
			double vz = -v*dx;
			int t = 6+itemRand.nextInt(5);
			PositionController p = new CollectingPositionController(px, py, pz, lx, ly, lz, t);
			EntityFX fx = new EntityCCBlurFX(ep.worldObj, px, py, pz).setIcon(ChromaIcons.FLARE).setLife(t).setScale(0.5F).setPositionController(p);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public int getItemEnchantability(ItemStack is) {
		return Items.stone_pickaxe.getItemEnchantability(is);
	}

}
