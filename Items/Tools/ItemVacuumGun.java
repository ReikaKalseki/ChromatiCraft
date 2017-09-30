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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemProjectileFiringTool.ProgressGatedProjectileFiringTool;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemVacuumGun extends ProgressGatedProjectileFiringTool {

	public ItemVacuumGun(int index) {
		super(index, UseResult.PUNISHSEVERE);
	}

	@Override
	protected void harmDisallowedPlayer(EntityPlayer ep, boolean severe) {
		super.harmDisallowedPlayer(ep, severe);

		if (severe) {
			ReikaItemHelper.dropInventory(ep);
		}
	}

	@Override
	protected Entity createProjectile(ItemStack is, World world, EntityPlayer ep) {
		return new EntityVacuum(world, ep);
	}

}
