package Reika.ChromatiCraft.ModInterface.Lua;

import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod.ModDependentMethod;

import forestry.api.multiblock.IAlvearyController;

@ModDependentMethod(ModList.FORESTRY)
public class LuaCycleBee extends AlvearyLuaMethod {

	protected LuaCycleBee() {
		super("cycleBee");
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	protected Object[] invoke(TileEntityLumenAlveary tile, IAlvearyController iac, Object[] args) {
		tile.forceCycleBees();
		return null;
	}

	@Override
	public String getDocumentation() {
		return "Attempts to forcibly cycle the bees from the outputs back to the input slots.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.VOID;
	}

}
