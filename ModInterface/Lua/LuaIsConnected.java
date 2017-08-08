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

import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import dan200.computercraft.api.lua.LuaException;

public class LuaIsConnected extends LuaMethod {

	public LuaIsConnected() {
		super("isConnected", CrystalReceiver.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		Object[] o = new Object[1];
		o[0] = CrystalNetworker.instance.checkConnectivity(CrystalElement.elements[(Integer)args[0]], (CrystalReceiver)te);
		return o;
	}

	@Override
	public String getDocumentation() {
		return "Returns whether a crystal tile is connected to a given element on a repeater network.\nArgs: Element Index 0-16\nReturns: Yes/No";
	}

	@Override
	public String getArgsAsString() {
		return "int Element";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.BOOLEAN;
	}

}
