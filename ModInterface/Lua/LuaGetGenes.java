package Reika.ChromatiCraft.ModInterface.Lua;

import java.util.ArrayList;

import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod.ModDependentMethod;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.IAllele;
import forestry.api.multiblock.IAlvearyController;

@ModDependentMethod(ModList.FORESTRY)
public class LuaGetGenes extends AlvearyLuaMethod {

	public LuaGetGenes() {
		super("getGenes");
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	protected Object[] invoke(TileEntityLumenAlveary tile, IAlvearyController iac, Object[] args) {
		IBeeGenome data = tile.getBeeGenome();
		if (data != null) {
			ArrayList<String> ret = new ArrayList();
			for (EnumBeeChromosome ebc : EnumBeeChromosome.values()) {
				if (ebc == EnumBeeChromosome.HUMIDITY)
					continue;
				IAllele iae = data.getActiveAllele(ebc);
				ret.add(iae.getName());
			}
			return ret.toArray(new String[ret.size()]);
		}
		return null;
	}

	@Override
	public String getDocumentation() {
		return "Returns the main genetic template of the active queen.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.ARRAY;
	}

}
