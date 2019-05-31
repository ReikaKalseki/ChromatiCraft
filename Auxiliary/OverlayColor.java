package Reika.ChromatiCraft.Auxiliary;


public interface OverlayColor {

	public int getColor();

	public static class IntOverlayColor implements OverlayColor {

		private final int color;

		public IntOverlayColor(int c) {
			color = c;
		}

		@Override
		public int getColor() {
			return color;
		}

	}
}
