/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalConsole;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import dan200.computercraft.api.lua.LuaException;

public class LuaCrystalConsole extends LuaMethod {

	public LuaCrystalConsole() {
		super("setState", TileEntityCrystalConsole.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		TileEntityCrystalConsole tile = (TileEntityCrystalConsole) te;
		int slot = ((Double)args[0]).intValue();
		boolean on = (Boolean)args[1];
		tile.toggle(slot, on);
		return null;
	}

	@Override
	public String getDocumentation() {
		return "Sets a console state.\nArgs: Slot, State\nReturns: Nothing";
	}

	@Override
	public String getArgsAsString() {
		return "int slot, boolean pitch";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.VOID;
	}

}
