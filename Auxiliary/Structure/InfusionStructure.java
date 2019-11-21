package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;

public class InfusionStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		double r = 0.6;
		for (int i = 0; i < 360; i += 15) {
			int dx = MathHelper.floor_double(x+0.5+r*Math.sin(Math.toRadians(i)));
			int dz = MathHelper.floor_double(z+0.5+r*Math.cos(Math.toRadians(i)));
			array.setBlock(dx, y-1, dz, b, 12);
		}

		r = 2;
		for (int i = 0; i < 360; i += 15) {
			int dx = MathHelper.floor_double(x+0.5+r*Math.sin(Math.toRadians(i)));
			int dz = MathHelper.floor_double(z+0.5+r*Math.cos(Math.toRadians(i)));
			array.setBlock(dx, y-1, dz, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(dx, y-2, dz, b, 0);
		}

		r = 3.2;
		for (int i = 0; i < 360; i += 15) {
			int dx = MathHelper.floor_double(x+0.5+r*Math.sin(Math.toRadians(i)));
			int dz = MathHelper.floor_double(z+0.5+r*Math.cos(Math.toRadians(i)));
			array.setBlock(dx, y-1, dz, b, 12);
		}

		//ReikaJavaLibrary.pConsole(array);
		return array;
	}

}
