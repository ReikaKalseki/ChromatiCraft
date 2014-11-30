package Reika.ChromatiCraft.Base;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Registry.ChromaResearch;

public abstract class GuiDescription extends GuiBookSection {

	protected GuiDescription(EntityPlayer ep, ChromaResearch r, int x, int y) {
		super(ep, r, x, y);
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.PLAIN;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

}
