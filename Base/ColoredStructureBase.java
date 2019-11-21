package Reika.ChromatiCraft.Base;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public abstract class ColoredStructureBase extends ChromaStructureBase {

	private CrystalElement currentColor;

	public final FilledBlockArray getArray(World world, int x, int y, int z, CrystalElement e) {
		currentColor = e;
		FilledBlockArray ret = this.getArray(world, x, y, z);
		currentColor = null;
		return ret;
	}

	protected final CrystalElement getCurrentColor() {
		return currentColor;
	}

	@Override
	protected void initDisplayData() {
		currentColor = CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16];
	}

	@Override
	protected void finishDisplayCall() {
		currentColor = null;
	}

}
