package Reika.ChromatiCraft.Render;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.Effects.EntityParticleEmitterFX;
import Reika.DragonAPI.Instantiable.Effects.EntityParticleEmitterFX.ParticleSpawner;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BotaniaHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BotaniaPetalShower implements ParticleSpawner {

	public final ReikaDyeHelper color;

	public BotaniaPetalShower(ReikaDyeHelper dye) {
		color = dye;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EntityFX spawnParticle(EntityParticleEmitterFX fx) {
		if (fx.worldObj.rand.nextBoolean())
			return null;
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
		double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
		ItemStack is = BotaniaHandler.getInstance().getPetal(color);
		int l = ReikaRandomHelper.getRandomBetween(5, 15);
		float g = (float)ReikaRandomHelper.getRandomBetween(0, 0.125);
		/*
		EntityItemTexFX spr = new EntityItemTexFX(fx.worldObj, fx.posX, fx.posY, fx.posZ, vx, vy, vz, is);
		return spr.setScale(0.8F).setLife(l).setGravity(g).applyRenderColor();
		 */
		InertItem ei = new InertItem(fx.worldObj, is);
		ei.setLocationAndAngles(fx.posX, fx.posY, fx.posZ, 0, 0);
		ei.simulatePhysics = true;
		ei.ageSpeed = 5;
		ei.lifespan = l*ei.ageSpeed;
		ei.gravity *= 0.35;
		ei.noClip = true;
		fx.worldObj.spawnEntityInWorld(ei);
		return null;
	}

}
