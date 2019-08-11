package Reika.ChromatiCraft.Auxiliary.Render;

public class ScrollingObfuscatedString extends PartiallyObfuscatedString {

	private static final int OBF_FRAMES = 6;
	private static final int FRAMES_PER_CHAR = 2;
	private static final int DEOBF_FRAMES = 20;

	private int age = 0;
	private final int deobfTicks;
	private int charSplit = -1;
	private final int lifespan;

	public ScrollingObfuscatedString(String s) {
		super(s);
		deobfTicks = OBF_FRAMES+FRAMES_PER_CHAR*text.length();
		lifespan = deobfTicks+DEOBF_FRAMES;
	}

	@Override
	public void render(int x, int y, int c) {
		if (this.update()) {
			age = 0;
			charSplit = -1;
		}
		if (charSplit >= 0) {
			if (charSplit >= text.length()) {
				ChromaFontRenderer.FontType.GUI.renderer.drawString(text, x, y, c);
			}
			else {
				String s1 = text.substring(0, charSplit);
				String s2 = text.substring(charSplit);
				ChromaFontRenderer.FontType.GUI.renderer.drawString(s1, x, y, c);
				int x2 = x+ChromaFontRenderer.FontType.GUI.renderer.getStringWidth(s1);
				ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawString(s2, x2, y, c);
			}
		}
		else {
			ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawString(text, x, y, c);
		}
	}

	private boolean update() {
		age++;
		if (age >= OBF_FRAMES) {
			if (age >= deobfTicks) {
				charSplit = text.length();
			}
			else {
				charSplit = (age-OBF_FRAMES)/FRAMES_PER_CHAR;
			}
			return age >= lifespan;
		}
		return false;
	}

}
