/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import net.minecraft.world.World;

public class ChromaCommon {

	public static int crystalRender;
	public static int runeRender;
	public static int tankRender;
	//public static int fiberRender;
	public static int treeRender;
	public static int lampRender;
	public static int relayRender;
	public static int glowRender;
	public static int vriftRender;
	public static int dimgenRender;
	public static int glowTreeRender;
	public static int colorLockRender;
	public static int specialShieldRender;
	public static int glassRender;
	public static int consoleRender;
	public static int fenceRender;

	public static int oreRender;
	public static int plantRender;
	public static int plantRender2;

	public void registerRenderers()
	{
		//unused server side. -- see ClientProxy for implementation
	}

	public void addArmorRenders() {}

	public World getClientWorld() {
		return null;
	}

	public void registerRenderInformation() {

	}

	public void registerSounds() {

	}

	public void registerKeys() {

	}

	public void addDonatorRender() {

	}

	public void logPopupWarning(String msg) {

	}

}
