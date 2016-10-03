package Reika.ChromatiCraft.World.Dimension.Structure.LightPanel;


public enum LightType {

	TARGET(0x00ff00),
	BLOCK(0xff0000),
	CANCEL(0x0000ff);

	public final int renderColor;

	public static final LightType[] list = values();

	private LightType(int color) {
		renderColor = color;
	}

}
