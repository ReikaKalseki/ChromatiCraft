/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundGui;
import Reika.ChromatiCraft.Magic.Lore.KeyAssemblyPuzzle;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.HexGrid.Hex;
import Reika.DragonAPI.Instantiable.HexGrid.Point;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;


public class GuiLoreKeyAssembly extends GuiScreen implements CustomSoundGui {

	private final EntityPlayer player;

	private final KeyAssemblyPuzzle puzzle;

	public GuiLoreKeyAssembly(EntityPlayer ep) {
		player = ep;
		puzzle = LoreManager.instance.getPuzzle(ep);
		LoreManager.instance.preparePuzzle(ep);
	}

	@Override
	public void initGui() {
		super.initGui();

		buttonList.clear();
	}

	public void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
	}

	public void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.8F, 1);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		GL11.glPushMatrix();
		int dy = -16*0;
		GL11.glTranslated(0, dy, 0);
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		Hex h = puzzle.getHexAt(x-sr.getScaledWidth()/2-puzzle.CELL_SIZE/2, y-sr.getScaledHeight()/2-puzzle.CELL_SIZE/2-dy);
		if (h != null) {
			puzzle.getCell(h).isHovered = true;
			//Point p = puzzle.getHexLocation(h);
			//ReikaGuiAPI.instance.drawLine(x, y, sr.getScaledWidth()/2+puzzle.CELL_SIZE/2+p.x, sr.getScaledHeight()/2+puzzle.CELL_SIZE/2+p.y, 0xffffffff);
		}

		//GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.5F);
		puzzle.render(Tessellator.instance, player, sr);
		GL11.glPopMatrix();
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		if (button == 0 && !LoreManager.instance.hasPlayerCompletedBoard(player)) {
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

			Hex h = puzzle.getHexAt(x-sr.getScaledWidth()/2-puzzle.CELL_SIZE/2, y-sr.getScaledHeight()/2-puzzle.CELL_SIZE/2);
			if (h != null) {
				if (!LoreManager.instance.hasScannedAllTowers(player)) {
					ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, player, 2, 1);
					puzzle.flashUnknownHexes(player);
				}
				else {
					Point p = puzzle.getHexLocation(h);
					puzzle.getCell(h).onClick(puzzle, x-(sr.getScaledWidth()/2+puzzle.CELL_SIZE/2+p.x), y-(sr.getScaledHeight()/2+puzzle.CELL_SIZE/2+p.y)+0*16, player);
					if (puzzle.isComplete()) {
						LoreManager.instance.completeBoard(player);
					}
				}
			}
		}
	}

}
