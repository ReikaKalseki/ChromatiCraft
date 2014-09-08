/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.DragonAPI.Interfaces.Connectable;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class ItemConnector extends ItemChromaTool {

	public ItemConnector(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof Connectable) {
			return this.tryConnection((Connectable)te, world, x, y, z, is, ep);
		}
		is.stackTagCompound = null;
		return false;
	}

	private boolean tryConnection(Connectable te, World world, int x, int y, int z, ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("ex", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("ey", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("ez", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("rx", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("ry", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("rz", Integer.MIN_VALUE);
		}
		if (te.isEmitting()) {
			is.stackTagCompound.setInteger("ex", x);
			is.stackTagCompound.setInteger("ey", y);
			is.stackTagCompound.setInteger("ez", z);
		}
		else {
			is.stackTagCompound.setInteger("rx", x);
			is.stackTagCompound.setInteger("ry", y);
			is.stackTagCompound.setInteger("rz", z);
		}
		int ex = is.stackTagCompound.getInteger("ex");
		int ey = is.stackTagCompound.getInteger("ey");
		int ez = is.stackTagCompound.getInteger("ez");
		int rx = is.stackTagCompound.getInteger("rx");
		int ry = is.stackTagCompound.getInteger("ry");
		int rz = is.stackTagCompound.getInteger("rz");

		int dl = Math.abs(ex-rx+ey-ry+ez-rz)-1;

		//ReikaJavaLibrary.pConsole(is.stackTagCompound);
		//ReikaJavaLibrary.pConsole(dl);
		if (is.stackSize >= dl || ep.capabilities.isCreativeMode) {
			if (rx != Integer.MIN_VALUE && ry != Integer.MIN_VALUE && rz != Integer.MIN_VALUE) {
				if (ex != Integer.MIN_VALUE && ey != Integer.MIN_VALUE && ez != Integer.MIN_VALUE) {
					Connectable em = (Connectable)world.getTileEntity(ex, ey, ez);
					Connectable rec = (Connectable)world.getTileEntity(rx, ry, rz);

					//ReikaJavaLibrary.pConsole(rec+"\n"+em);
					if (em == null) {
						ReikaChatHelper.writeString("Tile missing at "+ex+", "+ey+", "+ez);
						is.stackTagCompound = null;
						return false;
					}
					if (rec == null) {
						ReikaChatHelper.writeString("Tile Hub missing at "+rx+", "+ry+", "+rz);
						is.stackTagCompound = null;
						return false;
					}
					rec.resetOther();
					em.resetOther();
					em.reset();
					rec.reset();
					boolean src = em.setSource(rx, ry, rz);
					boolean tg = rec.setTarget(ex, ey, ez);
					//ReikaJavaLibrary.pConsole(src+":"+tg, Side.SERVER);
					if (src && tg) {
						//ReikaJavaLibrary.pConsole("connected", Side.SERVER);
						if (!ep.capabilities.isCreativeMode)
							is.stackSize -= dl;
					}
					is.stackTagCompound = null;
				}
			}
		}
		return false;
	}

}
