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

import Reika.DragonAPI.ModInteract.Lua.LibraryLuaMethod;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;

public class ChromaLuaMethods {

	private static final LuaMethod getLumens = new LuaGetLumens();
	private static final LuaMethod isConnected = new LuaIsConnected();
	private static final LuaMethod getTankFraction = new LuaGetTankFraction();
	private static final LuaMethod console = new LuaCrystalConsole();

	private static final LibraryLuaMethod elementColor = new LuaElementColor();

}
