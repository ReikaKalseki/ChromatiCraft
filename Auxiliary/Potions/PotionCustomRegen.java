package Reika.ChromatiCraft.Auxiliary.Potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class PotionCustomRegen extends Potion {

	public PotionCustomRegen(int id) {
		super(id, false, Potion.regeneration.getLiquidColor());
	}

	@Override
	public void performEffect(EntityLivingBase elb, int level)  {
		if (elb.getHealth() < elb.getMaxHealth()) {
			elb.heal(1.0F);
		}
	}

	@Override
	public String getName() {
		return Potion.regeneration.getName();//StatCollector.translateToLocal("chromapotion.sat");
	}

	@Override
	public boolean isReady(int dura, int level) {
		int d = level > 0 ? 1+(120 >> (6*level)) : 190;
		return dura%d == 0 && (level > 0 || ReikaRandomHelper.doWithChance(10));
	}

}
