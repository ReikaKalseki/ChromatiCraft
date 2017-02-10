package Reika.ChromatiCraft.Magic.Artefact.Effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import Reika.ChromatiCraft.Magic.Artefact.UABombingEffect;


public class LightningStrikeEffect extends UABombingEffect.EntityEffect {

	@Override
	public void trigger(Entity e) {
		e.worldObj.addWeatherEffect(new EntityLightningBolt(e.worldObj, e.posX, e.posY, e.posZ));
	}

}
