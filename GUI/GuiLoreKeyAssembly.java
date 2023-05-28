/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Magic.Lore.KeyAssemblyPuzzle;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
import Reika.DragonAPI.Instantiable.Math.HexGrid.Hex;
import Reika.DragonAPI.Instantiable.Math.HexGrid.Point;
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
	public void onGuiClosed() {
		LoreManager.instance.getOrCreateRosetta(player).clear();
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		GL11.glPushMatrix();
		int dy = -16*0;
		GL11.glTranslated(0, dy, 0);
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		double mx = x-sr.getScaledWidth()/2D-puzzle.CELL_SIZE/2D;
		double my = y-sr.getScaledHeight()/2D-puzzle.CELL_SIZE/2D-dy;
		Hex h = puzzle.getHexAt((int)mx, (int)my);
		if (h != null) {
			puzzle.getCell(h).hover(puzzle, mx, my);
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

		if (button == 0 && (!LoreManager.instance.hasPlayerCompletedBoard(player) || !puzzle.isComplete())) {
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
					puzzle.tickCells(player);
					if (puzzle.isComplete()) {
						LoreManager.instance.completeBoard(player);
					}
				}
			}
		}
	}

}
