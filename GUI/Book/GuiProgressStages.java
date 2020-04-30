/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.GuiScrollingPage;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager.ProgressLink;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Maps.SequenceMap.Topology;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

public class GuiProgressStages extends GuiScrollingPage {

	private int randomIndex;
	private int clearLength;

	private ProgressStage active;

	//private ArrayList<ProgressStage> stages = new ArrayList();
	private final Topology<ProgressLink> map = ProgressionManager.instance.getTopology();
	private final Map<ProgressLink, Integer> levels = map.getDepthMap();
	private final EnumMap<ProgressStage, Point> renderPositions = new EnumMap(ProgressStage.class);
	private final EnumMap<ProgressStage, Rectangle> locations = new EnumMap(ProgressStage.class);

	private int elementWidth = 0;
	private int elementHeight = 20;

	private static final int spacingX = 30;//60;//30;//80;
	private static final int spacingY = 15;//25;//15;//30;

	public GuiProgressStages(EntityPlayer ep) {
		super(ChromaGuis.PROGRESS, ep, 256, 220, 242, 112);

		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())
			ChromaDescriptions.reload();

		/*
		for (int i = 0; i < ProgressStage.list.length; i++) {
			ProgressStage p = ProgressStage.list[i];
			if (p.playerHasPrerequisites(ep)) {
				stages.add(p);
			}
		}
		 */

		//ReikaJavaLibrary.pConsole("------------------------------");
		final HashMap<Integer, Integer> offsets = new HashMap();
		for (ProgressLink p : levels.keySet()) {
			int depth = levels.get(p);
			int d = offsets.containsKey(depth) ? offsets.get(depth) : 0;
			int dx = d*(elementWidth+spacingX);
			int dy = depth*(elementHeight+spacingY);
			offsets.put(depth, d+1);
			renderPositions.put(p.parent, new Point(dx, dy));
			elementWidth = 20;//Math.max(elementWidth, Minecraft.getMinecraft().fontRenderer.getStringWidth(p.getTitleString())+20);
			maxX = Math.max(maxX, dx+elementWidth);
			maxY = Math.max(maxY, dy+elementHeight);
			//ReikaJavaLibrary.pConsole(maxX+", "+maxY+" # "+p+" @ "+dx+"+"+elementWidth+", "+dy+"+"+elementHeight);
		}

		maxX -= paneWidth-spacingX/2;
		maxY -= paneHeight-spacingY-30;

		if (maxX < 0)
			maxX = 0;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";

		this.addAuxButton(new CustomSoundImagedGuiButton(10, j+xSize, k, 22, 39, 42, 126, file, ChromatiCraft.class, this), "Return");
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button.id == 10) {
			this.goTo(ChromaGuis.BOOKNAV, null);
			this.resetOffset();
		}
		this.initGui();
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		super.drawScreen(x, y, f);

		this.renderTree(posX, posY);
		this.renderText(posX, posY);

		//ReikaJavaLibrary.pConsole(offsetX+"/"+maxX+","+offsetY+"/"+maxY);
	}

	private void renderTree(int posX, int posY) {
		locations.clear();
		this.renderLines(posX, posY);
		this.renderElements(posX, posY);
	}

	private void renderLines(int posX, int posY) {
		for (ProgressLink p : levels.keySet()) {
			this.renderLine(posX, posY, p);
		}
	}

	private void renderLine(int posX, int posY, ProgressLink p) {
		Collection<ProgressLink> c = map.getParents(p);
		int dx = -offsetX+posX+12;
		int dy = -offsetY+posY+36;
		Point pt = renderPositions.get(p.parent);
		for (ProgressLink par : c) {
			Point pt2 = renderPositions.get(par.parent);
			int x1 = dx+pt.getX()+elementWidth/2;
			int y1 = dy+pt.getY();
			/*
			if (this.elementOnScreen(p, posX, posY, x1, y1)) {
				int x2 = dx+pt2.getX()+elementWidth/2;
				int y2 = dy+pt2.getY()+elementHeight;
				if (this.elementOnScreen(par, posX, posY, x2, y2)) {
					api.drawLine(x1, y1, x2, y2, 0xffffff);
				}
			}
			 */
			int x2 = dx+pt2.getX()+elementWidth/2;
			int y2 = dy+pt2.getY()+elementHeight;

			ImmutablePair<java.awt.Point, java.awt.Point> ps = ReikaVectorHelper.clipLine(x1, x2, y1, y2, posX+8, posY+26, posX+xSize-8, posY+ySize/2+6);
			if (ps != null) {
				int clr = p.parent == active || par.parent == active ? (par.parent.isPlayerAtStage(player) ? 0x00ff00 : 0xff4040) : 0xffffff;
				api.drawLine(ps.left.x, ps.left.y, ps.right.x, ps.right.y, clr, par.type);
			}
		}
	}

	private void renderElements(int posX, int posY) {
		for (ProgressLink p : levels.keySet()) {
			Point pt = renderPositions.get(p.parent);
			int x = -offsetX+posX+12+pt.getX();
			int y = -offsetY+posY+36+pt.getY();

			if (this.elementOnScreen(p, posX, posY, x, y))
				this.renderElement(p, x, y);
		}
	}

	private boolean elementOnScreen(ProgressLink p, int posX, int posY, int x, int y) {
		return x >= posX+8 && x <= posX+xSize-elementWidth-8 && y >= posY+24+1 && y-posY+elementHeight < ySize/2+8;
	}

	private void renderElement(ProgressLink pl, int x, int y) {
		//draw
		ProgressStage p = pl.parent;
		int color = 0xffffff;
		boolean see = this.renderClearText(p, player);
		drawRect(x, y, x+elementWidth, y+elementHeight, 0xff444444);
		Rectangle rect = new Rectangle(x, y, elementWidth, elementHeight);
		boolean hover = rect.contains(api.getMouseRealX(), api.getMouseRealY());
		boolean near = p.isOneStepAway(player);
		int border = see ? 0xffff00 : 0xff0000;
		boolean has = p.isPlayerAtStage(player);
		if (has)
			border = 0x00ff00;
		double t = (System.currentTimeMillis()/5D+p.hashCode()*23)%360;
		border = ReikaColorAPI.mixColors(border, 0xffffff, 0.5F+0.25F*(float)Math.sin(Math.toRadians(t)));
		api.drawRectFrame(x, y, elementWidth, elementHeight, border); //temp
		if (see || near) {
			String s = see ? p.getTitleString() : EnumChatFormatting.OBFUSCATED.toString()+p.getTitleString();//p.name();
			if (!see)
				color = 0xb5b5b5;
			int dx = (elementWidth-fontRendererObj.getStringWidth(s))/2;
			int dy = (elementHeight-fontRendererObj.FONT_HEIGHT)/2;
			//fontRendererObj.drawString(s, x+dx, y+dy, color);
		}
		else {
			color = 0x888888;
		}

		if (see) {
			if (has || p.alwaysRenderFullBright()) {
				p.renderIcon(itemRender, fontRendererObj, x+2, y+2);
				/*
				int d = -1;
				double t = Math.toRadians(((System.currentTimeMillis()/12D)+p.hashCode())%360);
				int c = ReikaColorAPI.mixColors(0x00ff00, 0x000000, 0.5F+0.49F*(float)(Math.sin(t)));
				api.drawRectFrame(x-d, y-d, elementWidth+d*2, elementHeight+d*2, c); //temp
				 */
				if (hover) {
					this.renderHoverOverlay(x, y, true);
				}
			}
			else {
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glColor4f(0, 0, 0, 1.0F);
				itemRender.renderWithColor = false;
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_CULL_FACE);
				p.renderIcon(itemRender, fontRendererObj, x+2, y+2);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPopAttrib();
				if (hover) {
					this.renderHoverOverlay(x, y, false);
				}
			}
		}
		else {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.INVERTEDADD.apply();
			api.drawTexturedModelRectFromIcon(x+2, y+2, ChromaIcons.QUESTION.getIcon(), 16, 16);
			GL11.glPopAttrib();
		}
		locations.put(p, rect);
	}

	private void renderHoverOverlay(int x, int y, boolean has) {
		ReikaTextureHelper.bindTerrainTexture();
		GL11.glColor4f(1, 1, 1, 1);
		int d = 2;
		api.drawRectFrame(x+d, y+d, elementWidth-d*2, elementHeight-d*2, 0xffffff);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		int z = 500;
		ReikaGuiAPI.instance.setZLevel(z+250);
		IIcon ico = has ? ChromaIcons.CHECK.getIcon() : ChromaIcons.X.getIcon();
		api.drawTexturedModelRectFromIcon(x+elementWidth/2-2+d, y+elementHeight/2-2+d, ico, (elementWidth-d*2)/2, (elementHeight-d*2)/2);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator.instance.startDrawingQuads();
		int a = 96+(int)(32*Math.sin(System.currentTimeMillis()/180D));
		Tessellator.instance.setColorRGBA_I(0xffffff, a);
		Tessellator.instance.addVertex(x+d, y+elementHeight-d, z);
		Tessellator.instance.addVertex(x+elementWidth-d, y+elementHeight-d, z);
		Tessellator.instance.addVertex(x+elementWidth-d, y+d, z);
		Tessellator.instance.addVertex(x+d, y+d, z);
		Tessellator.instance.draw();
		GL11.glPopAttrib();
	}

	public static boolean renderClearText(ProgressStage p, EntityPlayer player) {
		return p.isPlayerAtStage(player) || p.playerHasPrerequisites(player);
	}

	private void renderText(int posX, int posY) {
		int c = 0xffffff;
		int px = posX+descX;

		if (active == null) {

		}
		else {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			ProgressStage p = active;//this.getStage();
			if (p.isOneStepAway(player) || p.playerHasPrerequisites(player)) {
				fontRendererObj.drawSplitString(p.getTitleString(), px, posY+descY+36, 242, 0xffffff);
			}
			else {
				ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawSplitString(p.getTitleString(), px, posY+descY+36, 242, 0xffffff);
			}

			if (this.renderClearText(p, player)) {
				fontRendererObj.drawSplitString(p.getHintString(), px, posY+descY+36+20, 242, 0xffffff);
			}
			else {
				ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawSplitString(p.getHintString(), px, posY+descY+36+20, 242, 0xffffff);
			}

			int dy = posY+descY+100+15;
			if (p.isPlayerAtStage(player)) {
				String s = p.getRevealedString();
				dy -= fontRendererObj.FONT_HEIGHT*fontRendererObj.getStringWidth(s)/242;
				fontRendererObj.drawSplitString(s, px, dy, 242, 0xffffff);
			}
			else {
				ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawSplitString(this.getIncompleteText(), px, dy, 242, 0xffffff);
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);

		ProgressStage p = this.getUnderMouse(x, y);
		if (p != null) {
			//Minecraft.getMinecraft().thePlayer.playSound("random.click", 1, 1);
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.33F, 1);
			ChromaFontRenderer.FontType.OBFUSCATED.renderer.rerandomize();
		}
		active = p;
	}

	private ProgressStage getUnderMouse(int x, int y) {
		for (ProgressStage p : locations.keySet()) {
			Rectangle r = locations.get(p);
			if (r.contains(x, y))
				return p;
		}
		return null;
	}

	private String getIncompleteText() {
		/*
		if (this.getGuiTick()%250 == 0)
			this.randomizeString();
		String obf = EnumChatFormatting.OBFUSCATED.toString();
		String clear = EnumChatFormatting.RESET.toString();
		String root = obf+this.getIncompleteString()+clear;
		int n = randomIndex+clearLength;
		String pre = root.substring(0, randomIndex);
		String mid = root.substring(randomIndex, n);
		String post = root.substring(n);
		return pre+clear+mid+obf+post;*/
		return this.getIncompleteString();
	}

	private String getIncompleteString() {
		return "There is still much to learn...";
	}

	private void randomizeString() {
		String s = this.getIncompleteString();
		randomIndex = rand.nextInt(s.length());
		clearLength = Math.min(Math.max(4, rand.nextInt(s.length())), Math.min(rand.nextInt(3) == 0 ? 12 : 6, s.length()-randomIndex));
	}


	@Override
	public String getBackgroundTexture() {
		return "Textures/GUIs/Handbook/progress.png";
	}
	/*
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
	 */

	@Override
	protected String getScrollingTexture() {
		return "Textures/GUIs/Handbook/navbcg.png";
	}
}
