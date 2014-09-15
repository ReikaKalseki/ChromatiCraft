package Reika.ChromatiCraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.input.Mouse;

import Reika.ChromatiCraft.Auxiliary.ChromaHelpData;
import Reika.ChromatiCraft.Auxiliary.ChromaHelpData.HelpEntry;
import Reika.DragonAPI.Instantiable.Data.CoordinateData;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaHelpHUD {

	public static ChromaHelpHUD instance = new ChromaHelpHUD();

	private int roll = 0;
	private int rollv = 0;

	private static final int SIZE = 30;

	private ChromaHelpHUD() {

	}

	@SubscribeEvent
	public void renderHUD(RenderGameOverlayEvent evt) {
		this.renderHelpIcon(evt);
		this.checkAndRenderBlockHUD(evt);

		if (this.isToggleable() && Mouse.isButtonDown(0) && this.isCursorInExpandButton()) {
			this.toggle();
		}

		if (roll > 0) {
			roll += rollv;
		}

		if (roll <= 0) {
			rollv = 0;
			roll = 0;
		}
		else if (roll >= SIZE) {
			rollv = 0;
			roll = SIZE;
		}

		ReikaTextureHelper.bindHUDTexture();
	}

	private boolean isCursorInExpandButton() {
		return false;
	}

	private boolean isToggleable() {
		return rollv == 0;
	}

	private void toggle() {
		rollv = roll > 0 ? -2 : 2;
	}

	private void renderHelpIcon(RenderGameOverlayEvent evt) {

	}

	private void renderHelpBar(HelpEntry h, RenderGameOverlayEvent evt) {
		String s = h.getTitle();
	}

	private boolean isExpanded() {
		return roll == SIZE;
	}

	private void checkAndRenderBlockHUD(RenderGameOverlayEvent evt) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer ep = mc.thePlayer;
		FontRenderer f = mc.fontRenderer;

		MovingObjectPosition hit = ReikaPlayerAPI.getLookedAtBlockClient(4);
		if (hit != null) {
			CoordinateData dat = new CoordinateData(ep.worldObj, hit);
			HelpEntry help = ChromaHelpData.getEntryFor(ep, dat);
			if (help != null) {
				if (this.isExpanded()) {
					help.render(evt);
				}
				else {
					this.renderHelpBar(help, evt);
				}
			}
		}
	}

}
