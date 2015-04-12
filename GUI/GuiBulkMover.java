package Reika.ChromatiCraft.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Container.ContainerBulkMover;
import Reika.ChromatiCraft.Items.Tools.ItemBulkMover;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class GuiBulkMover extends GuiContainer {

	private GuiTextField number;

	private int carry;

	public GuiBulkMover(EntityPlayer ep) {
		super(new ContainerBulkMover(ep));

		ySize = 117;

		carry = ItemBulkMover.getNumberToCarry(ep.getCurrentEquippedItem());
	}

	@Override
	public void initGui() {
		super.initGui();

		buttonList.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		number = new GuiTextField(fontRendererObj, j+32, k+16, 40, 12);
		number.setFocused(false);
		number.setMaxStringLength(5);
	}

	@Override
	protected void actionPerformed(GuiButton b) {

	}

	@Override
	protected void keyTyped(char c, int key) {
		super.keyTyped(c, key);
		if (number.isFocused()) {
			number.textboxKeyTyped(c, key);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);
		number.mouseClicked(x, y, b);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (number.isFocused()) {
			int num = ReikaJavaLibrary.safeIntParse(number.getText());
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.BULKNUMBER.ordinal(), new PacketTarget.ServerTarget(), num);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, ChromaItems.BULKMOVER.getBasicName(), xSize/2, 6, 0xffffff);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), xSize-58, ySize - 96 + 2, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		String var4 = "/Reika/ChromatiCraft/Textures/GUIs/bulkmover.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);

		number.drawTextBox();
	}

}
