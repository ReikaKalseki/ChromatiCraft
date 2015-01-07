package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Registry.ChromaGuis;

public class GuiProgressStages extends GuiBookSection {

	private int randomIndex;
	private int clearLength;

	private ArrayList<ProgressStage> stages = new ArrayList();

	public GuiProgressStages(EntityPlayer ep) {
		super(ep, null, 256, 220, false);

		for (int i = 0; i < ProgressStage.list.length; i++) {
			ProgressStage p = ProgressStage.list[i];
			if (p.playerHasPrerequisites(ep)) {
				stages.add(p);
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";

		//buttonList.add(new ImagedGuiButton(10, j+xSize, k, 22, 39, 42, 126, file, ChromatiCraft.class));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button.id == 10) {
			this.goTo(ChromaGuis.BOOKNAV, null);
		}
		this.initGui();
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		super.drawScreen(x, y, f);

		int c = 0xffffff;
		int px = posX+descX;

		if (subpage == 0) {

		}
		else {
			ProgressStage p = this.getStage();
			fontRendererObj.drawSplitString(p.getHintString(), px, posY+descY, 242, 0xffffff);

			if (p.isPlayerAtStage(player)) {
				fontRendererObj.drawSplitString(p.getRevealedString(), px, posY+descY+60, 242, 0xffffff);
			}
			else {
				fontRendererObj.drawSplitString(this.getIncompleteText(), px, posY+descY+60, 242, 0xffffff);
			}
		}
	}

	private String getIncompleteText() {
		if (this.getGuiTick()%250 == 0)
			this.randomizeString();
		String obf = EnumChatFormatting.OBFUSCATED.toString();
		String clear = EnumChatFormatting.RESET.toString();
		String root = obf+this.getIncompleteString()+clear;
		int n = randomIndex+clearLength;
		String pre = root.substring(0, randomIndex);
		String mid = root.substring(randomIndex, n);
		String post = root.substring(n);
		return pre+clear+mid+obf+post;
	}

	private String getIncompleteString() {
		return "There is still much to learn...";
	}

	private void randomizeString() {
		String s = this.getIncompleteString();
		randomIndex = rand.nextInt(s.length());
		clearLength = Math.min(Math.max(2, rand.nextInt(s.length())), Math.min(rand.nextInt(3) == 0 ? 12 : 6, s.length()-randomIndex));
	}

	/*
	@Override
	public String getBackgroundTexture() {
		return "Textures/GUIs/Handbook/handbook.png";
	}
	 */
	@Override
	public String getPageTitle() {
		return subpage > 0 ? this.getStage().getTitleString() : "Research Notes";
	}

	private ProgressStage getStage() {
		return subpage > 0 ? stages.get(subpage-1) : null;
	}

	@Override
	protected int getMaxSubpage() {
		return stages.size();
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.PLAIN;
	}

}
