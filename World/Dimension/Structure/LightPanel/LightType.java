/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.LightPanel;


public enum LightType {

	TARGET(0x00ff00),
	BLOCK(0xff0000),
	CANCEL(0x0000ff);

	public final int renderColor;

	public static final LightType[] list = values();

	private LightType(int color) {
		renderColor = color;
	}

}
