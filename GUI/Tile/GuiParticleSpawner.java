/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityParticleSpawner;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityParticleSpawner.VariableValue;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.BoundedValue;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Instantiable.GUI.ScrollingButtonList;
import Reika.DragonAPI.Interfaces.IconEnum;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class GuiParticleSpawner extends GuiChromaBase {

	private final TileEntityParticleSpawner tile;

	private GuiPage page = GuiPage.POSITION;

	private boolean RGBMode = true;
	private int red;
	private int green;
	private int blue;
	private int hue;
	private float saturation;
	private float luminosity;
	private int color;

	private static ArrayList<IconEnum> permittedIcons = new ArrayList();

	public static final int MAX_ICON_ROWS = 6;
	public static final int MAX_ICON_COLS = 7;

	private static final ScrollingButtonList iconButtons = new ScrollingButtonList(MAX_ICON_ROWS, MAX_ICON_COLS);

	public GuiParticleSpawner(EntityPlayer ep, TileEntityParticleSpawner te) {
		super(new CoreContainer(ep, te), ep, te);
		tile = te;

		color = tile.particles.particleColor;
		red = ReikaColorAPI.getRed(color);
		green = ReikaColorAPI.getGreen(color);
		blue = ReikaColorAPI.getBlue(color);

		float[] hsv = Color.RGBtoHSB(red, green, blue, null);
		hue = (int)(hsv[0]*360);
		saturation = hsv[1];
		luminosity = hsv[2];

		this.buildIconList();
	}

	static {
		buildIconList();
	}

	private static void buildIconList() {
		permittedIcons.clear();
		iconButtons.reset();
		for (int i = 0; i < 16; i++) {
			permittedIcons.add(CrystalElement.elements[i]);
			iconButtons.addButton();
		}
		for (int i = 0; i < ChromaIcons.iconList.length; i++) {
			ChromaIcons ico = ChromaIcons.iconList[i];
			if (isIconAllowed(ico)) {
				permittedIcons.add(ico);
				iconButtons.addButton();
				//ReikaJavaLibrary.pConsole(ico);
			}
		}
	}

	private static boolean isIconAllowed(ChromaIcons ico) {
		switch(ico) {
			case TRANSPARENT:
			case GUARDIANOUTER:
			case SPARKLE_ROUNDED:
			case REPEATER:
			case MULTIREPEATER:
			case BROADCAST:
			case LASER:
			case LASEREND:
			case RIFT:
			case RIFTHALO:
			case NOENTER:
			case CHECK:
			case QUESTION:
			case BLUEFIRE:
			case BATTERY:
			case BLANK:
			case ALLCOLORS:
			case BASICFADE:
			case BASICFADE_FAST:
			case FRAME:
			case REGIONS:
			case WEAKREPEATER:
			case LATTICE:
			case GLOWFRAME_TRANS:
			case GLOWFRAMEDOT_TRANS:
			case FAN:
			case SIDEDFLOW:
			case WIDEBAR:
			case AVOLASER:
			case AVOLASER_CORE:
			case HIVE:
			case HIVESPARKS:
			case CAUSTICS_GENTLE_ALPHA:
				return false;
			default:
				return true;
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		for (int i = 0; i < GuiPage.list.length; i++) {
			GuiPage.list[i].initOptions(tile, RGBMode);
		}

		String file = this.getFullTexturePath();
		int n = 4;
		for (int i = 0; i < GuiPage.list.length; i++) {
			int x = j+n;
			int y = k+n+20*i;
			int u = i == page.ordinal() ? 198 : 178;
			int v = i*20;
			buttonList.add(new CustomSoundImagedGuiButton(i, x, y, 20, 20, u, v, file, ChromatiCraft.class, this));
		}

		switch(page) {
			case POSITION:
			case VELOCITY:
				for (int i = 0; i < 12; i++) {
					int x = j+29+(i%2)*122;
					int y = k+19+(i/2)*24;
					int u = i%2 == 1 ? 126 : 106;
					int v = 173;
					GuiButton b = new CustomSoundImagedGuiButton(i+100, x, y, 20, 20, u, v, file, ChromatiCraft.class, this);
					buttonList.add(b);
				}
				break;
			case COLOR:
				for (int i = 0; i < 16; i++) {
					int x = j+27+(i%8)*18;
					int y = k+18+(i/8)*18;
					int u = 106;
					int v = 213;
					ImagedGuiButton b = new CustomSoundImagedGuiButton(i+100, x, y, 20, 20, u, v, file, ChromatiCraft.class, this);
					b.icon = CrystalElement.elements[i].getGlowRune();
					b.iconHeight = b.iconWidth = 16;
					buttonList.add(b);
				}
				for (int i = 0; i < 8; i++) {
					int x = j+29+(i%2)*122;
					int y = k+58+(i/2)*20;
					int u = i%2 == 1 ? 126 : 106;
					int v = i >= 6 ? 193 : 173;
					GuiButton b = new CustomSoundImagedGuiButton(i+200, x, y, 20, 20, u, v, file, ChromatiCraft.class, this);
					buttonList.add(b);
				}
				int in = 45;
				buttonList.add(new GuiButton(300, j+29+in, k+142, 143-in*2, 20, !RGBMode ? "HSV" : "RGB"));
				break;
			case ICON: {
				buttonList.add(new GuiButton(50, j+27, k+144, 20, 20, "^"));
				buttonList.add(new GuiButton(51, j+47, k+144, 20, 20, "v"));
				int i = 0;
				for (IconEnum ico : permittedIcons) {
					if (i >= iconButtons.getBaseOffset() && i <= iconButtons.getHighestVisible()) {
						int rowPos = (i/MAX_ICON_COLS)-iconButtons.getScroll();
						int x = j+29+(i%MAX_ICON_COLS)*21-2;
						int y = k+18+rowPos*21;
						int u = 106;
						int v = 213;
						ImagedGuiButton b = new CustomSoundImagedGuiButton(i+100, x, y, 20, 20, u, v, file, ChromatiCraft.class, this);
						b.icon = ico.getIcon();
						b.iconHeight = b.iconWidth = 16;
						buttonList.add(b);
					}
					i++;
				}
				break;
			}
			case TIMING:
				for (int i = 0; i < 8; i++) {
					int x = j+29+(i%2)*122;
					int y = k+19+(i/2)*24;
					int u = i%2 == 1 ? 126 : 106;
					int v = 173;
					if (i >= 6)
						v += 20;
					GuiButton b = new CustomSoundImagedGuiButton(i+100, x, y, 20, 20, u, v, file, ChromatiCraft.class, this);
					buttonList.add(b);
				}
				break;
			case MODIFIER:
				for (int i = 0; i < 14; i++) {
					int x = j+29+(i%2)*122;
					int y = k+19+(i/2)*20; //24
					int u = i%2 == 1 ? 126 : 106;
					int v = 173;
					if (i >= 8)
						v += 20;
					GuiButton b = new CustomSoundImagedGuiButton(i+100, x, y, 20, 20, u, v, file, ChromatiCraft.class, this);
					buttonList.add(b);
				}
				break;
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);
		if (b.id < GuiPage.list.length) {
			page = GuiPage.list[b.id];
			iconButtons.reset();
		}
		else {
			int n = 1;
			if (GuiScreen.isShiftKeyDown()) {
				n = 4;
				if (page == GuiPage.COLOR || (page == GuiPage.TIMING && !ReikaMathLibrary.isValueInsideBoundsIncl(2, 3, b.id-100)))
					n = 5;
				else if (page == GuiPage.ICON || (page == GuiPage.MODIFIER && ReikaMathLibrary.isValueInsideBoundsIncl(8, 9, b.id-100)))
					n = 1;
			}
			for (int i = 0; i < n; i++) {
				switch(page) {
					case POSITION:
						this.handlePositionButton(b.id-100);
						break;
					case VELOCITY:
						this.handleVelocityButton(b.id-100);
						break;
					case COLOR:
						this.handleColorButton(b.id);
						break;
					case ICON:
						if (b.id == 50) {
							if (iconButtons.scrollUp()) {
								this.initGui();
							}
						}
						else if (b.id == 51) {
							if (iconButtons.scrollDown()) {
								this.initGui();
							}
						}
						else if (b.id >= 100) {
							int idx = b.id-100;
							tile.particles.particleIcon = permittedIcons.get(idx);
						}
						break;
					case TIMING:
						this.handleTimingButton(b.id-100);
						break;
					case MODIFIER:
						this.handleModifierButton(b.id-100);
						break;
				}
			}
		}
		tile.particles.sendData();
		this.initGui();
	}

	private void handleModifierButton(int i) {
		switch(i) {
			case 0:
				tile.particles.particleGravity.decrease();
				break;
			case 1:
				tile.particles.particleGravity.increase();
				break;
			case 2:
				tile.particles.particleGravity.decreaseVariation();
				break;
			case 3:
				tile.particles.particleGravity.increaseVariation();
				break;
			case 4:
				tile.particles.particleSize.decrease();
				break;
			case 5:
				tile.particles.particleSize.increase();
				break;
			case 6:
				tile.particles.particleSize.decreaseVariation();
				break;
			case 7:
				tile.particles.particleSize.increaseVariation();
				break;
			case 8:
				tile.particles.particleCollision = false;
				break;
			case 9:
				tile.particles.particleCollision = true;
				break;
			case 10:
				tile.particles.rapidExpand = false;
				break;
			case 11:
				tile.particles.rapidExpand = true;
				break;
			case 12:
				tile.particles.noSlowdown = false;
				break;
			case 13:
				tile.particles.noSlowdown = true;
				break;
		}
	}

	private void handleTimingButton(int i) {
		switch(i) {
			case 0:
				tile.particles.particleLife.decrease();
				break;
			case 1:
				tile.particles.particleLife.increase();
				break;
			case 2:
				tile.particles.particleLife.decreaseVariation();
				break;
			case 3:
				tile.particles.particleLife.increaseVariation();
				break;
			case 4:
				tile.particles.particleRate.decrease();
				break;
			case 5:
				tile.particles.particleRate.increase();
				break;
			case 6:
				tile.particles.alphaFade = false;
				break;
			case 7:
				tile.particles.alphaFade = true;
				break;
		}
	}

	private void handlePositionButton(int i) {
		switch(i) {
			case 0:
				tile.particles.particlePositionX.decrease();
				break;
			case 1:
				tile.particles.particlePositionX.increase();
				break;
			case 2:
				tile.particles.particlePositionX.decreaseVariation();
				break;
			case 3:
				tile.particles.particlePositionX.increaseVariation();
				break;
			case 4:
				tile.particles.particlePositionY.decrease();
				break;
			case 5:
				tile.particles.particlePositionY.increase();
				break;
			case 6:
				tile.particles.particlePositionY.decreaseVariation();
				break;
			case 7:
				tile.particles.particlePositionY.increaseVariation();
				break;
			case 8:
				tile.particles.particlePositionZ.decrease();
				break;
			case 9:
				tile.particles.particlePositionZ.increase();
				break;
			case 10:
				tile.particles.particlePositionZ.decreaseVariation();
				break;
			case 11:
				tile.particles.particlePositionZ.increaseVariation();
				break;
		}
	}

	private void handleVelocityButton(int i) {
		switch(i) {
			case 0:
				tile.particles.particleVelocityX.decrease();
				break;
			case 1:
				tile.particles.particleVelocityX.increase();
				break;
			case 2:
				tile.particles.particleVelocityX.decreaseVariation();
				break;
			case 3:
				tile.particles.particleVelocityX.increaseVariation();
				break;
			case 4:
				tile.particles.particleVelocityY.decrease();
				break;
			case 5:
				tile.particles.particleVelocityY.increase();
				break;
			case 6:
				tile.particles.particleVelocityY.decreaseVariation();
				break;
			case 7:
				tile.particles.particleVelocityY.increaseVariation();
				break;
			case 8:
				tile.particles.particleVelocityZ.decrease();
				break;
			case 9:
				tile.particles.particleVelocityZ.increase();
				break;
			case 10:
				tile.particles.particleVelocityZ.decreaseVariation();
				break;
			case 11:
				tile.particles.particleVelocityZ.increaseVariation();
				break;
		}
	}

	private void handleColorButton(int id) {
		if (id == 300) {
			RGBMode = !RGBMode;
			this.roundColors();
		}
		else if (id >= 200) {
			if (RGBMode) {
				switch(id-200) {
					case 0:
						red = Math.max(red-1, 0);
						break;
					case 1:
						red = Math.min(red+1, 255);
						break;
					case 2:
						green = Math.max(green-1, 0);
						break;
					case 3:
						green = Math.min(green+1, 255);
						break;
					case 4:
						blue = Math.max(blue-1, 0);
						break;
					case 5:
						blue = Math.min(blue+1, 255);
						break;
				}
				this.calcColors(1);
			}
			else {
				switch(id-200) {
					case 0:
						hue = Math.max(hue-1, 0);
						break;
					case 1:
						hue = Math.min(hue+1, 360);
						break;
					case 2:
						saturation = Math.max(saturation-0.05F, 0);
						break;
					case 3:
						saturation = Math.min(saturation+0.05F, 1);
						break;
					case 4:
						luminosity = Math.max(luminosity-0.05F, 0);
						break;
					case 5:
						luminosity = Math.min(luminosity+0.05F, 1);
						break;
				}
				this.calcColors(0);
			}
			if (id-200 == 6) {
				tile.particles.cyclingColor = false;
			}
			if (id-200 == 7) {
				tile.particles.cyclingColor = true;
			}
		}
		else if (id >= 100) {
			color = CrystalElement.elements[id-100].getColor();
			this.calcColors(2);
		}
	}

	private void roundColors() {
		if (!RGBMode) {
			saturation = ReikaMathLibrary.roundToDecimalPlaces(saturation, 1);
			luminosity = ReikaMathLibrary.roundToDecimalPlaces(luminosity, 1);
		}
		this.calcColors(0);
	}

	private void calcColors(int mode) {
		switch(mode) {
			case 0: {
				color = Color.HSBtoRGB(hue/360F, saturation, luminosity);
				red = ReikaColorAPI.getRed(color);
				green = ReikaColorAPI.getGreen(color);
				blue = ReikaColorAPI.getBlue(color);
				break;
			}
			case 1: {
				color = ReikaColorAPI.RGBtoHex(red, green, blue);

				float[] hsv = Color.RGBtoHSB(red, green, blue, null);
				hue = (int)(hsv[0]*360);
				saturation = hsv[1];
				luminosity = hsv[2];
				break;
			}
			case 2: {
				red = ReikaColorAPI.getRed(color);
				green = ReikaColorAPI.getGreen(color);
				blue = ReikaColorAPI.getBlue(color);

				float[] hsv = Color.RGBtoHSB(red, green, blue, null);
				hue = (int)(hsv[0]*360);
				saturation = hsv[1];
				luminosity = hsv[2];
				break;
			}
		}
		tile.particles.particleColor = color;
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		super.drawGuiContainerBackgroundLayer(f, x, y);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int i = 0;
		int sy = page == GuiPage.MODIFIER || page == GuiPage.COLOR ? 20 : 24;
		for (Option o : page.options) {
			int dx = j+49;
			int dy = k+19+sy*i;
			if (page == GuiPage.COLOR)
				dy += 39;
			i++;
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getFullTexturePath());
			api.drawTexturedModalRect(dx, dy, 0, 173, 102, 20);
			String s = o.display+": "+String.valueOf(o.getValue());
			GL11.glTranslated(0, 0, 350);
			api.drawCenteredStringNoShadow(fontRendererObj, s, dx+51, dy+6, 0xffffff);
			GL11.glTranslated(0, 0, -350);
			if (o instanceof VariableOption) {
				dy += sy;
				VariableOption v = (VariableOption)o;
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getFullTexturePath());
				api.drawTexturedModalRect(dx, dy, 0, 173, 102, 20);
				s = "Variance: "+String.valueOf(v.getVariance());
				GL11.glTranslated(0, 0, 350);
				api.drawCenteredStringNoShadow(fontRendererObj, s, dx+51, dy+6, 0xffffff);
				GL11.glTranslated(0, 0, -350);
				i++;
			}
		}
	}

	@Override
	public String getGuiTexture() {
		return "particle";
	}

	private static enum GuiPage {
		POSITION(),
		VELOCITY(),
		COLOR(),
		ICON(),
		TIMING(), //life, rate
		MODIFIER(); //gravity, size, collision

		private static final GuiPage[] list = values();

		private final ArrayList<Option> options = new ArrayList();

		private void initOptions(TileEntityParticleSpawner p, boolean RGB) {
			options.clear();
			switch(this) {
				case POSITION:
					options.add(new VariableOption("X Position", p.particles.particlePositionX));
					options.add(new VariableOption("Y Position", p.particles.particlePositionY));
					options.add(new VariableOption("Z Position", p.particles.particlePositionZ));
					break;
				case VELOCITY:
					options.add(new VariableOption("X Velocity", p.particles.particleVelocityX));
					options.add(new VariableOption("Y Velocity", p.particles.particleVelocityY));
					options.add(new VariableOption("Z Velocity", p.particles.particleVelocityZ));
					break;
				case COLOR:
					int color = p.particles.particleColor;
					int r = ReikaColorAPI.getRed(color);
					int g = ReikaColorAPI.getGreen(color);
					int b = ReikaColorAPI.getBlue(color);
					if (RGB) {
						options.add(new SliderOption("Red", new BoundedValue(0, 255, r)));
						options.add(new SliderOption("Green", new BoundedValue(0, 255, g)));
						options.add(new SliderOption("Blue", new BoundedValue(0, 255, b)));
					}
					else {
						float[] hsv = Color.RGBtoHSB(r, g, b, null);
						int hue = (int)(hsv[0]*360);
						float sat = hsv[1];
						float lum = hsv[2];
						options.add(new SliderOption("Hue", new BoundedValue(0, 360, hue)));
						options.add(new SliderOption("Saturation", new BoundedValue(0F, 1, sat).setStep(0.03125F)));
						options.add(new SliderOption("Luminosity", new BoundedValue(0F, 1, lum).setStep(0.03125F)));
					}
					options.add(new ToggleOption("Cycling Color", p.particles.cyclingColor));
					break;
				case ICON:
					break;
				case TIMING:
					options.add(new VariableOption("Lifetime", p.particles.particleLife));
					options.add(new SliderOption("Rate", p.particles.particleRate));
					options.add(new ToggleOption("Alpha Fade", p.particles.alphaFade));
					break;
				case MODIFIER:
					options.add(new VariableOption("Gravity", p.particles.particleGravity));
					options.add(new VariableOption("Size", p.particles.particleSize));
					options.add(new ToggleOption("Collision", p.particles.particleCollision));
					options.add(new ToggleOption("Fast Expand", p.particles.rapidExpand));
					options.add(new ToggleOption("No Slowdown", p.particles.noSlowdown));
					break;
			}
		}
	}

	private static abstract class Option<N extends Number> {

		public final String display;

		private Option(String s) {
			this.display = s;
		}

		public abstract N getValue();

	}

	private static class ToggleOption<N extends Number> extends Option {

		private boolean isEnabled;

		private ToggleOption(String s) {
			this(s, false);
		}

		private ToggleOption(String s, boolean b) {
			super(s);
			this.isEnabled = b;
		}

		@Override
		public Number getValue() {
			return this.isEnabled ? 1 : 0;
		}

	}

	private static class SliderOption<N extends Number> extends Option {

		private final BoundedValue<N> value;

		private SliderOption(String s, BoundedValue<N> v) {
			super(s);
			value = v;
		}

		@Override
		public Number getValue() {
			return value.getValue();
		}

	}

	private static class VariableOption<N extends Number> extends Option {

		private final VariableValue<N> value;

		private VariableOption(String s, VariableValue<N> v) {
			super(s);
			value = v;
		}

		@Override
		public Number getValue() {
			return value.getValue();
		}

		public Number getVariance() {
			return value.getVariation();
		}

	}

}
