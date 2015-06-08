package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiDescription;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;

public class GuiPackChanges extends GuiDescription {

	public GuiPackChanges(EntityPlayer ep) {
		super(ep, ChromaResearch.PACKCHANGES, 256, 220);
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.PLAIN;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		super.drawScreen(x, y, f);
		int px = posX+descX;

		String title = "";
		fontRendererObj.drawSplitString(title, px, posY+descY, 242, 0xffffff);
	}

	@Override
	protected int getMaxSubpage() {
		return PackModificationTracker.instance.getModifications(ChromatiCraft.instance).size();
	}

}
