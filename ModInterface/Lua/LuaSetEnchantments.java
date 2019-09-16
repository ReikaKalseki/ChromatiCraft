/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Lua;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;

public class LuaSetEnchantments extends LuaMethod {

	public LuaSetEnchantments() {
		super("setEnchantments", TileEntityAutoEnchanter.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		TileEntityAutoEnchanter tile = (TileEntityAutoEnchanter)te;
		tile.clearEnchantments();
		for (int i = 0; i < args.length; i += 2) {
			Enchantment e = Enchantment.enchantmentsList[(Integer)args[i]];
			int lvl = (Integer)args[i+1];
			tile.setEnchantment(e, lvl);
		}
		return null;
	}

	@Override
	public String getDocumentation() {
		return "Sets the desired enchantment levels.\nArgs: List<EnchantmentID, Level>\nReturns: Nothing";
	}

	@Override
	public String getArgsAsString() {
		return "[int ID, int level]...";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.VOID;
	}

}
