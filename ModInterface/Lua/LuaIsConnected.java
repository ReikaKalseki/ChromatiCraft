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
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import dan200.computercraft.api.lua.LuaException;

public class LuaIsConnected extends LuaMethod {

	public LuaIsConnected() {
		super("isConnected", CrystalReceiver.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		Object[] o = new Object[1];
		;//o[0] = CrystalNetworker.instance.checkConnectivity(CrystalElement.elements[(Integer)args[0]], (CrystalReceiver)te);
		return o;
	}

	@Override
	public String getDocumentation() {
		return "Returns whether a crystal tile is connected to a given element on a repeater network.\n" +
				"Args: Element Index 0-16, Request mode (0 = initiate, 1 = is complete, 2 = get return)\nReturns: Yes/No";
	}

	@Override
	public String getArgsAsString() {
		return "int Element, int requestMode";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.BOOLEAN;
	}

}
