package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Base.ChromaBookGui;

public class GuiNotes extends ChromaBookGui {

	public GuiNotes(EntityPlayer ep) {
		super(ep, 256, 220);
	}

	@Override
	public String getBackgroundTexture() {
		return "";
	}

}