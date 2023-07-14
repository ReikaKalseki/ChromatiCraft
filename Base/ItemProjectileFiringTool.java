/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.Interfaces.ProjectileFiringTool;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;



public abstract class ItemProjectileFiringTool extends ItemChromaTool implements ProjectileFiringTool {

	public ItemProjectileFiringTool(int index) {
		super(index);
	}

	@Override
	public final ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (!world.isRemote) {
			this.fire(is, world, ep, false);
		}
		return is;
	}

	public final void fire(ItemStack is, World world, EntityPlayer ep, boolean randomVec) {
		Entity e = this.createProjectile(is, world, ep, randomVec);
		Vec3 vec = ep.getLookVec();
		e.setLocationAndAngles(ep.posX+vec.xCoord, ep.posY+vec.yCoord+1.5, ep.posZ+vec.zCoord, 0, 0);
		world.spawnEntityInWorld(e);
		ReikaSoundHelper.playSoundAtEntity(world, e, "random.fizz", 2, 0.7F);
	}

	@Override
	public int getAutofireRate() {
		return 10;
	}

	protected abstract Entity createProjectile(ItemStack is, World world, EntityPlayer ep, boolean randomVec);

	public static abstract class ProgressGatedProjectileFiringTool extends ItemProgressGatedTool implements ProjectileFiringTool {

		public ProgressGatedProjectileFiringTool(int index, UseResult ur) {
			super(index, ur);
		}

		@Override
		public final ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
			if (!world.isRemote) {
				if (this.handleUseAllowance(ep))
					return is;
				this.fire(is, world, ep, false);
			}
			return is;
		}

		public final void fire(ItemStack is, World world, EntityPlayer ep, boolean randomVec) {
			Entity e = this.createProjectile(is, world, ep, randomVec);
			Vec3 vec = ep.getLookVec();
			e.setLocationAndAngles(ep.posX+vec.xCoord, ep.posY+vec.yCoord+1.5, ep.posZ+vec.zCoord, 0, 0);
			world.spawnEntityInWorld(e);
			ReikaSoundHelper.playSoundAtEntity(world, e, "random.fizz", 2, 0.7F);
		}

		protected abstract Entity createProjectile(ItemStack is, World world, EntityPlayer ep, boolean randomVec);

	}

}
