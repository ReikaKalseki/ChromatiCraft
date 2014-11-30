package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Instantiable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

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

		FilledBlockArray arr = page.getStructure().getStructureForDisplay();
		for (int i = 0; i < arr.getSize(); i++) {
			int[] xyz = arr.getNthBlock(i);
			int dx = xyz[0]-arr.getMinX();
			int dy = xyz[1]-arr.getMinY();
			int dz = xyz[2]-arr.getMinZ();
			BlockKey b = arr.getBlockKeyAt(xyz[0], xyz[1], xyz[2]);
			if (b != null && b.blockID != Blocks.air) {
				ReikaJavaLibrary.pConsole(b);
				int px = dx * 16 + dz * 16;
				int py = -dx * 8 + dz * 8 + dy * 50;
				api.drawItemStack(itemRender, b.asItemStack(), px, py);
			}
		}
	}

}
