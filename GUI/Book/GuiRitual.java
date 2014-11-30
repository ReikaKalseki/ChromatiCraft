/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.Chromabilities;

public class GuiRitual extends GuiBookSection {

	private final Chromabilities ability;

	public GuiRitual(EntityPlayer ep, Chromabilities r) {
		super(ep, null, 256, 220);
		ability = r;
	}

	private ElementTagCompound getEnergy() {
		return AbilityHelper.instance.getElementsFor(ability);
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.PLAIN;
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

	@Override
	public String getPageTitle() {
		return "Ritual";
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (buttontimer > 0)
			return;
		buttontimer = 20;
		subpage = 0; //?
		//renderq = 22.5F;
		this.initGui();
	}

}
