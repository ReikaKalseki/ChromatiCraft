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
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.Lua.LibraryLuaMethod;
import dan200.computercraft.api.lua.LuaException;


public class LuaElementColor extends LibraryLuaMethod {

	public LuaElementColor() {
		super("elementColor");
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		int idx = ((Double)args[0]).intValue();
		return new Object[]{CrystalElement.elements[idx].getColor()};
	}

	@Override
	public String getDocumentation() {
		return "Returns element color.\nArgs: Element index 0-15\nReturns: Element color";
	}

	@Override
	public String getArgsAsString() {
		return "int index";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.INTEGER;
	}

}
