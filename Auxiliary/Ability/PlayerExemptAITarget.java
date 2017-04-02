package Reika.ChromatiCraft.Auxiliary.Ability;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Registry.Chromabilities;


class PlayerExemptAITarget implements IEntitySelector {

	private final IEntitySelector base;

	public PlayerExemptAITarget(IEntitySelector ie)
	{
		base = ie;
	}

	@Override
	public boolean isEntityApplicable(Entity e) {
		if (base.isEntityApplicable(e)) {
			if (e instanceof EntityPlayer) {
				if (Chromabilities.COMMUNICATE.enabledOn((EntityPlayer)e)) {
					return false;
				}
				else {
					return true;
				}
			}
			else {
				return true;
			}
		}
		return false;
	}

}
