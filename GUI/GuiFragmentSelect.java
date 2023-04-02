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

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Container.ContainerFragmentSelect;
import Reika.ChromatiCraft.Magic.Progression.ProgressionChoiceSystem.Selection;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;


public class GuiFragmentSelect extends GuiContainer implements CustomSoundGui {

	private static final int DURATION = 1500;

	private final EntityPlayer player;

	private int selectionIndex = -1;
	private long selectionCountdown = -1;

	public GuiFragmentSelect(EntityPlayer ep) {
		super(new ContainerFragmentSelect(ep));
		player = ep;

		ySize = 162;
		xSize = 180;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		buttonList.clear();
		for (int i = 0; i < 3; i++)
			buttonList.add(new SelectionButton(i, j+33+i*(9+SelectionButton.SIZE), k+37));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id <= 2) {
			if (this.hasFragment()) {
				this.startSelection(b.id);
			}
		}
	}

	private void startSelection(int id) {
		selectionIndex = id;
		selectionCountdown = System.currentTimeMillis()+DURATION;
	}

	public boolean hasFragment() {
		return this.getContainer().hasFragment();
	}

	private ContainerFragmentSelect getContainer() {
		return (ContainerFragmentSelect)inventorySlots;
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

		boolean frag = this.hasFragment();
		for (GuiButton b : ((List<GuiButton>)buttonList)) {
			if (b.id <= 2) {
				SelectionButton sb = (SelectionButton)b;
				b.enabled = b.visible = frag;
				Selection s = frag ? this.getContainer().getOption(b.id) : null;
				int idx = s != null ? s.category.ordinal() : -1;
				sb.textureU = (idx%SelectionButton.ROWCOLS)*SelectionButton.SIZE;
				sb.textureV = (idx/SelectionButton.ROWCOLS)*SelectionButton.SIZE;
			}
		}

		if (selectionIndex >= 0) {
			long time = System.currentTimeMillis();
			SelectionButton b = (SelectionButton)buttonList.get(selectionIndex);
			float frac = MathHelper.clamp_float(1F-(selectionCountdown-time)/(float)DURATION, 0, 1);
			if (frac > 0) {
				//int dy = (int)(b.yPosition+(1-frac)*b.SIZE);
				//ReikaGuiAPI.instance.drawLine(b.xPosition, dy, b.xPosition+b.SIZE, dy, 0xffffffff);
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				IIcon ico = ChromaIcons.RINGS.getIcon();
				double x0 = b.xPosition+b.SIZE/2D;
				double y0 = b.yPosition+b.SIZE/2D;
				Tessellator v5 = Tessellator.instance;
				ReikaRenderHelper.disableLighting();
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.ADDITIVEDARK.apply();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				ReikaTextureHelper.bindTerrainTexture();
				v5.startDrawing(GL11.GL_TRIANGLE_FAN);
				v5.setColorOpaque_I(0xffffff);
				v5.setBrightness(240);
				v5.addVertexWithUV(x0, y0, 0, ico.getInterpolatedU(8), ico.getInterpolatedV(8));
				double r = b.SIZE*0.5;
				for (int i = 0; i <= 360*frac; i += 2) {
					double ang = Math.toRadians(i-90);
					double dx = -Math.cos(ang);
					double dy = Math.sin(ang);
					v5.addVertexWithUV(x0+dx*r, y0+dy*r, 0, ico.getInterpolatedU(8+8*dx), ico.getInterpolatedV(8+8*dy));
				}
				b.rollout = 1-frac;
				v5.draw();
				GL11.glPopAttrib();
			}
			if (time >= selectionCountdown) {
				this.getContainer().selectSlot(selectionIndex);
				selectionIndex = -1;
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ptick, int mx, int my) {
		GL11.glEnable(GL11.GL_BLEND);
		String var4 = "Textures/GUIs/fragselect.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		BlendMode.DEFAULT.apply();
		double a = 0.75+0.04*Math.sin(System.currentTimeMillis()/437D)+0.06*Math.cos(System.currentTimeMillis()/639D);
		GL11.glColor4f(1, 1, 1, (float)a);
		this.drawTexturedModalRect(var5-32, var6-32, 0, 0, xSize+64, ySize+64);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private class SelectionButton extends CustomSoundImagedGuiButton {

		private static final int SIZE = 32;
		private static final int TEX_SIZE = 256;
		private static final int ROWCOLS = TEX_SIZE/SIZE;

		private int textureU = TEX_SIZE-SIZE;
		private int textureV = TEX_SIZE-SIZE;

		private float rollout = 1;

		public SelectionButton(int id, int x, int y) {
			super(id, x, y, SIZE, SIZE, TEX_SIZE-SIZE, TEX_SIZE-SIZE, "Textures/fragmentcategories.png", ChromatiCraft.class, GuiFragmentSelect.this);
			visible = enabled = false;
		}

		@Override
		protected void modifyTextureUV() {
			u = textureU;
			v = textureV;
		}

		@Override
		protected void renderButton() {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(false);
			BlendMode.DEFAULT.apply();
			ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/squarefog.png");
			float f = 48F/256F;
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			int d1 = 6;
			v5.addVertexWithUV(xPosition-d1, yPosition-d1+height+d1*2, zLevel, 0, 1);
			v5.addVertexWithUV(xPosition-d1+width+d1*2, yPosition-d1+height+d1*2, zLevel, 1, 1);
			v5.addVertexWithUV(xPosition-d1+width+d1*2, yPosition-d1, zLevel, 1, 0);
			v5.addVertexWithUV(xPosition-d1, yPosition-d1, zLevel, 0, 0);
			v5.draw();
			int d = 3;
			int hash = System.identityHashCode(this);
			int c = CrystalElement.getBlendedColor(ReikaRenderHelper.getSystemTimeAsInt()/48+17*hash, 30);
			int br = ReikaColorAPI.GStoHex((int)(192+64*Math.sin(System.currentTimeMillis()/271D+hash)));
			float mix = (float)(0.5+0.5*Math.sin(System.currentTimeMillis()/443D-13*hash));
			c = ReikaColorAPI.mixColors(c, br, mix);
			c = ReikaColorAPI.getModifiedSat(c, 1.5F);
			GL11.glColor4f(ReikaColorAPI.getRed(c)/255F, ReikaColorAPI.getGreen(c)/255F, ReikaColorAPI.getBlue(c)/255F, 1);
			BlendMode.ADDITIVEDARK.apply();
			ReikaTextureHelper.bindTerrainTexture();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(xPosition-d, yPosition-d, ChromaIcons.LATTICE.getIcon(), width+d*2, height+d*2);
			BlendMode.DEFAULT.apply();
			super.renderButton();
			GL11.glPushMatrix();
			double sc = SIZE/16D;
			GL11.glScaled(sc, sc, sc);
			int h = height;
			height *= rollout;
			Selection s = GuiFragmentSelect.this.getContainer().getOption(id);
			GL11.glDepthMask(true);
			if (id == 0)
				GL11.glTranslated(0.375, 0, 0);
			if (id == 2)
				GL11.glTranslated(0.25, 0, 0);
			if (s != null && s.fragment != null)
				s.fragment.drawTabIcon(itemRender, (int)(xPosition/sc), (int)(yPosition/sc));
			GL11.glDepthMask(false);
			GL11.glPopMatrix();
			c = ReikaColorAPI.mixColors(c, 0xffffff, 0.5F);
			float t = ReikaJavaLibrary.getSystemTimeAsInt()/2000F;
			int c1 = ReikaColorAPI.getShiftedHue(c, 45F*(float)Math.sin(2*t));
			int c2 = ReikaColorAPI.getShiftedHue(c, 45F*(float)Math.sin(5*t));
			int c3 = ReikaColorAPI.getShiftedHue(c, 45F*(float)Math.sin(9*t));
			int c4 = ReikaColorAPI.getShiftedHue(c, 45F*(float)Math.sin(13*t));
			//GL11.glColor4f(ReikaColorAPI.getRed(c)/255F, ReikaColorAPI.getGreen(c)/255F, ReikaColorAPI.getBlue(c)/255F, 1);
			ReikaTextureHelper.bindTexture(modClass, this.getButtonTexture());
			f = 1F/textureSize;
			GL11.glShadeModel(GL11.GL_SMOOTH);
			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.setColorOpaque_I(c1);
			v5.addVertexWithUV(xPosition+0, yPosition+height, zLevel, (u+0)*f, (v+height)*f);
			v5.setColorOpaque_I(c2);
			v5.addVertexWithUV(xPosition+width, yPosition+height, zLevel, (u+width)*f, (v+height)*f);
			v5.setColorOpaque_I(c3);
			v5.addVertexWithUV(xPosition+width, yPosition+0, zLevel, (u+width)*f, (v+0)*f);
			v5.setColorOpaque_I(c4);
			v5.addVertexWithUV(xPosition+0, yPosition+0, zLevel, (u+0)*f, (v+0)*f);
			v5.draw();
			GL11.glColor4f(0.4F, 0.4F, 0.4F, 0.3F);
			BlendMode.ADDITIVEDARK.apply();
			super.renderButton();
			GL11.glPopAttrib();
			height = h;
		}

	}

}
