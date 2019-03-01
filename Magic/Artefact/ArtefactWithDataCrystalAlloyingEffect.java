package Reika.ChromatiCraft.Magic.Artefact;

import net.minecraft.entity.item.EntityItem;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.AlloyingEffect;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArtefactWithDataCrystalAlloyingEffect implements AlloyingEffect {

	public static final ArtefactWithDataCrystalAlloyingEffect instance = new ArtefactWithDataCrystalAlloyingEffect();

	private ArtefactWithDataCrystalAlloyingEffect() {

	}

	@Override
	public void doEffect(EntityItem ei) {
		if (ei.worldObj.isRemote) {
			this.doClientFX(ei);
		}
		else {
			ChromaSounds.DISCHARGE.playSound(ei, 2, 1);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doClientFX(EntityItem ei) {

	}

	@Override
	public void onFinish(EntityItem ei, EntityItem result) {
		ReikaEntityHelper.setInvulnerable(ei, true);
		ReikaEntityHelper.setInvulnerable(result, true);
		ei.worldObj.newExplosion(ei, ei.posX, ei.posY, ei.posZ, 6, true, true);
	}

}
