package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

public class GuiStructure extends GuiBookSection {

	private final VisibilityComparator visibility = new VisibilityComparator();

	private double rx = 0;
	private double ry = 0;
	private double rz = 0;

	public GuiStructure(EntityPlayer ep, ChromaResearch r) {
		super(ep, r, 256, 220);
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.STRUCT;
	}

	@Override
	public final void drawScreen(int mx, int my, float f) {
		super.drawScreen(mx, my, f);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int ox = 262;
		int oy = 200;

		FilledBlockArray arr = page.getStructure().getStructureForDisplay();
		if (page.name().toLowerCase().contains("casting"))
			arr.setBlock(arr.getMidX(), arr.getMinY()+1, arr.getMidZ(), ChromaTiles.TABLE.getBlock(), ChromaTiles.TABLE.getBlockMetadata());
		if (page.name().toLowerCase().contains("pylon"))
			arr.setBlock(arr.getMidX(), arr.getMinY()+9, arr.getMidZ(), ChromaTiles.PYLON.getBlock(), ChromaTiles.PYLON.getBlockMetadata());
		int dd = 12;
		int ddy = 12;

		HashMap<Vector3f, CoordStack> render = new HashMap();

		ry = (ry+0.25)%360;
		Matrix4f rot = new Matrix4f();
		ReikaVectorHelper.euler321Sequence(rot, rx, ry, rz);
		for (int y = arr.getMinY(); y <= arr.getMaxY(); y++) {
			for (int x = arr.getMinX(); x <= arr.getMaxX(); x++) {
				for (int z = arr.getMinZ(); z <= arr.getMaxZ(); z++) {
					ItemStack is = arr.getDisplayAt(x, y, z);
					if (is != null) {
						int dx = x-arr.getMidX();
						int dy = y-arr.getMidY();
						int dz = z-arr.getMidZ();
						Vector3f in = new Vector3f(dx, dy, dz);
						Vector3f vec = ReikaVectorHelper.multiplyVectorByMatrix(in, rot);
						int px = (int)(vec.x*dd+vec.z*dd);
						int py = (int)(-vec.x*dd/2+vec.z*dd/2-vec.y*ddy);
						int pz = 0;//250;
						render.put(vec, new CoordStack(is, px, py, pz));
					}
				}
			}
		}

		GL11.glPushMatrix();
		double d = 0.6;
		GL11.glScaled(d, d, 1);

		ArrayList<Vector3f> keys = new ArrayList(render.keySet());
		Collections.sort(keys, visibility);

		for (Vector3f vec : keys) {
			CoordStack is = render.get(vec);
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, is.coord.zCoord);
			api.drawItemStack(itemRender, is.item, j+is.coord.xCoord+ox, k+is.coord.yCoord+oy);
			GL11.glPopMatrix();
		}

		GL11.glPopMatrix();
	}

	public static class VisibilityComparator implements Comparator<Vector3f> {

		private boolean posX = true;
		private boolean posY = true;
		private boolean posZ = true;

		@Override
		public int compare(Vector3f o1, Vector3f o2) {
			/*
			int dx = o1.xCoord-o2.xCoord;
			int dy = o1.yCoord-o2.yCoord;
			int dz = o1.zCoord-o2.zCoord;
			int mx = posX ? dx : -dx;
			int my = posY ? dy : -dy;
			int mz = posZ ? dz : -dz;
			return mx+my+mz;
			 */
			return (int)Math.signum(o1.z-o2.z);
		}

	}

	private static class CoordStack {

		private final ItemStack item;
		private final Coordinate coord;

		private CoordStack(ItemStack is, int x, int y, int z) {
			this(is, new Coordinate(x, y, z));
		}

		private CoordStack(ItemStack is, Coordinate c) {
			coord = c;
			item = is;
		}

	}

}
