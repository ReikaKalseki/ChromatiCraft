package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Registry.ChromaResearch;

public class GuiStructure extends GuiBookSection {

	public GuiStructure(EntityPlayer ep, ChromaResearch r) {
		super(ep, r, 256, 220);
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.STRUCT;
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
	}

}
