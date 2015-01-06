/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.GUI.InWorldGui;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiHelp extends InWorldGui {

	private final String tex = "Textures/GUIs/helpgui.png";

	public GuiHelp() {
		super(176, 176);
	}

	@Override
	protected void init() {
		super.init();

		//this.addButton(0, xSize-10, ySize/2-32, 8, 64, 176, 0, tex, ChromatiCraft.class);
		//this.addButton(1, 2, ySize/2-32, 8, 64, 184, 0, tex, ChromatiCraft.class);

		this.addButton(2, xSize/2-32, 2, 64, 8, 192, 0, tex, ChromatiCraft.class);
		this.addButton(3, xSize/2-32, ySize-10, 64, 8, 192, 8, tex, ChromatiCraft.class);
	}

	@Override
	protected void onButtonClicked(int id) {
		switch(id) {
		case 0:
			this.scrollRight();
			break;
		case 1:
			this.scrollLeft();
			break;
		case 2:
			this.scrollUp();
			break;
		case 3:
			this.scrollDown();
			break;
		}
	}

	private void scrollRight() {

	}

	private void scrollLeft() {

	}

	private void scrollUp() {

	}

	private void scrollDown() {

	}

	@Override
	protected void renderCallback(float ptick) {

	}

	@Override
	public void bindMainTexture() {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, tex);
	}

}
