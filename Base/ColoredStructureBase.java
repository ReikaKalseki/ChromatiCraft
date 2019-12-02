package Reika.ChromatiCraft.Base;

import java.util.Stack;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public abstract class ColoredStructureBase extends ChromaStructureBase {

	private Stack<CrystalElement> currentColor = new Stack();

	//private int currentCalling = 0;

	public synchronized final FilledBlockArray getArray(World world, int x, int y, int z, CrystalElement e) {
		/*
		if (currentCalling > 0) {
			ReikaJavaLibrary.pConsole("Called while already being called ("+currentCalling+")! "+Thread.currentThread().getName());
			Thread.dumpStack();
		}
		currentCalling++;
		 */
		currentColor.push(e);
		//ReikaJavaLibrary.pConsole("Setting color to "+e+" on "+Thread.currentThread().getName());
		FilledBlockArray ret = this.getArray(world, x, y, z);
		currentColor.pop();
		//ReikaJavaLibrary.pConsole("Color was nulled on "+Thread.currentThread().getName());
		//Thread.dumpStack();
		//currentCalling--;
		return ret;
	}

	protected final CrystalElement getCurrentColor() {
		return currentColor.peek();
	}

	@Override
	protected void initDisplayData() {
		currentColor.clear();
		currentColor.push(CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16]);
	}

	@Override
	protected void finishDisplayCall() {
		ReikaJavaLibrary.pConsole("Nulling current color on "+Thread.currentThread().getName());
		//Thread.dumpStack();
		currentColor.clear();
	}

}
