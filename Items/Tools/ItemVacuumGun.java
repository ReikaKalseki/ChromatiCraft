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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemProgressGatedTool;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemVacuumGun extends ItemProgressGatedTool {

	public ItemVacuumGun(int index) {
		super(index, UseResult.PUNISHSEVERE);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {

		if (!world.isRemote) {
			if (this.handleUseAllowance(ep))
				return is;
			EntityVacuum e = new EntityVacuum(world, ep);
			Vec3 vec = ep.getLookVec();
			e.setLocationAndAngles(ep.posX+vec.xCoord, ep.posY+vec.yCoord+1.5, ep.posZ+vec.zCoord, 0, 0);
			world.spawnEntityInWorld(e);

			ReikaSoundHelper.playSoundAtEntity(world, e, "random.fizz", 2, 0.7F);
		}

		return is;
	}

	@Override
	protected void harmDisallowedPlayer(EntityPlayer ep, boolean severe) {
		super.harmDisallowedPlayer(ep, severe);

		if (severe) {
			ReikaItemHelper.dropInventory(ep);
		}
	}

}
