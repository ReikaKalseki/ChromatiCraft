package Reika.ChromatiCraft.Base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;


public abstract class GuiChromaTool extends GuiScreen implements CustomSoundGui {

	protected final EntityPlayer player;

	public GuiChromaTool(EntityPlayer ep) {
		player = ep;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
	}

	public final void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
	}

	public final void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.8F, 1);
	}

	@Override
	public final void setWorldAndResolution(Minecraft mc, int x, int y) {
		super.setWorldAndResolution(mc, x, y);
		fontRendererObj = ChromaFontRenderer.FontType.GUI.renderer;
	}

	@Override
	public final boolean doesGuiPauseGame() {
		return false;
	}

}
