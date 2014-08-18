package Reika.ChromatiCraft.GUI;

import Reika.ChromatiCraft.Registry.Chromabilities;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class GuiChromability extends GuiScreen {

	private ArrayList<Chromabilities> available = new ArrayList();
	private final EntityPlayer player;

	public GuiChromability(EntityPlayer ep) {
		player = ep;
		for (int i = 0; i < Chromabilities.abilities.length; i++) {
			available.add(Chromabilities.abilities[i]); //for now
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();

		for (int i = 0; i < available.size(); i++) {
			Chromabilities c = available.get(i);
			String name = c.name();
			buttonList.add(new GuiButton(i, 30, 30+i*20, 40, 20, name));
		}
	}

	@Override
	public void actionPerformed(GuiButton b) {
		Chromabilities c = available.get(b.id);
		ArrayList<Integer> li = new ArrayList();
		li.add(5);
		li.add(0);
		li.add(0);
		li.add(0);
		li.add(0);
		li.add(0);
		li.add(0);
		li.add(0);
		c.trigger(player, li);
		this.initGui();
		player.closeScreen();
	}

	@Override
	public final void drawScreen(int x, int y, float f)
	{
		super.drawScreen(x, y, f);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed() {

	}

}
