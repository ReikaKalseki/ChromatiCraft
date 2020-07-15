package Reika.ChromatiCraft.Magic.Artefact;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.AlloyingEffect;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArtefactWithDataCrystalAlloyingEffect implements AlloyingEffect {

	public static final ArtefactWithDataCrystalAlloyingEffect instance = new ArtefactWithDataCrystalAlloyingEffect();

	private final Random rand = new Random();

	private ArtefactWithDataCrystalAlloyingEffect() {

	}

	public void initialize(EntityItem ei) {
		rand.setSeed(ei.getEntityId());
		rand.nextBoolean();
		rand.nextBoolean();
		ei.getEntityData().setBoolean("artealloy", true);
	}

	@Override
	public void doEffect(EntityItem ei) {
		int tick = ei.ticksExisted;
		//ReikaJavaLibrary.pConsole(ei.worldObj.isRemote+" @ "+tick+" for "+ei.getEntityItem());
		if (ei.worldObj.isRemote) {
			this.doClientFX(ei);
		}
		else {
			if (tick%48 == 0)
				ChromaSounds.ARTEALLOY.playSound(ei, 2, 1);
			if (tick > 10 && rand.nextInt(90) == 0)
				ChromaSounds.ARTEALLOYHIT.playSound(ei, 2, 1);
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
