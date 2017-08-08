/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.UUID;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public abstract class ItemChromaTool extends ItemChromaBasic {

	public ItemChromaTool(int index) {
		super(index);
		maxStackSize = 1;
		this.setNoRepair();
	}

	@Override
	protected final CreativeTabs getCreativePage() {
		return ChromatiCraft.tabChromaTools;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/items_tool.png";
	}

	public UseResult canPlayerUse(EntityPlayer ep) {
		return UseResult.ALLOW;
	}

	public final boolean handleUseAllowance(EntityPlayer ep) {
		UseResult res = this.canPlayerUse(ep);
		if (res == UseResult.ALLOW)
			return false;
		if (res == UseResult.PUNISH || res == UseResult.PUNISHSEVERE) {
			this.harmDisallowedPlayer(ep, res == UseResult.PUNISHSEVERE);
		}
		return true;
	}

	protected void harmDisallowedPlayer(EntityPlayer ep, boolean severe) {
		if (!ep.worldObj.isRemote)
			ReikaItemHelper.dropItem(ep, ep.getCurrentEquippedItem());
		ep.setCurrentItemOrArmor(0, null);
		ep.attackEntityFrom(DamageSource.magic, severe ? 8 : 4);
		if (!ep.worldObj.isRemote) {
			double phi = ep.getRNG().nextDouble()*360;
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(5, 0, phi);
			ep.setPositionAndUpdate(ep.posX, ep.posY+0.5, ep.posZ);
			ep.motionX = xyz[0];
			ep.motionY = 1;
			ep.motionZ = xyz[2];
			ep.velocityChanged = true;
			if (severe)
				ep.fallDistance += 8;
			ChromaSounds.DISCHARGE.playSound(ep);
		}
	}

	public final void setOwner(ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setString("ownerid", ep.getUniqueID().toString());
	}

	public final boolean isOwner(ItemStack is, EntityPlayer ep) {
		return ep.getUniqueID().equals(this.getOwner(is));
	}

	public final UUID getOwner(ItemStack is) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("ownerid"))
			return null;
		return UUID.fromString(is.stackTagCompound.getString("ownerid"));
	}

	public static enum UseResult {
		ALLOW(),
		NOTHING(),
		PUNISH(),
		PUNISHSEVERE();
	}

}
