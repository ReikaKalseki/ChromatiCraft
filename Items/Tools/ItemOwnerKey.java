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

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaSounds;


public class ItemOwnerKey extends ItemChromaTool {

	public ItemOwnerKey(int index) {
		super(index);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int par4, boolean par5) {
		if (!(e instanceof EntityPlayer))
			return;
		if (!is.hasTagCompound())
			is.stackTagCompound = new NBTTagCompound();
		if (is.stackTagCompound.hasKey("owner"))
			return;
		EntityPlayer ep = (EntityPlayer)e;
		is.stackTagCompound.setString("owner", ep.getUniqueID().toString());
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List par3List, boolean par4) {
		if (is.stackTagCompound == null)
			return;
		if (is.stackTagCompound.hasKey("owner"))
			par3List.add(is.stackTagCompound.getString("owner")+"'s Lumen Key");
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (is.stackTagCompound == null)
			return false;
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityChromaticBase) {
			TileEntityChromaticBase tc = (TileEntityChromaticBase)te;
			UUID id = UUID.fromString(is.stackTagCompound.getString("owner"));
			EntityPlayer plc = tc.getPlacer();
			if (plc != null && id.equals(plc.getUniqueID())) {
				tc.addOwner(ep);
				ep.setCurrentItemOrArmor(0, null);
				ChromaSounds.CAST.playSoundAtBlock(te, 1, 0.75F);
				return true;
			}
		}
		return false;
	}

}
