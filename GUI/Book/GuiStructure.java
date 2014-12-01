package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class GuiStructure extends GuiBookSection {

	private final VisibilityComparator visibility = new VisibilityComparator();

	private double rx = 0;
	private double ry = 0;
	private double rz = 0;

	private int secY = 0;

	private int mode = 0;

	private int tick = 0;

	public GuiStructure(EntityPlayer ep, ChromaResearch r) {
		super(ep, r, 256, 220);
	}

	@Override
	public void initGui() {
		super.initGui();
		rx = ry = rz = 0;
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		buttonList.add(new GuiButton(0, j+185, k-2, 20, 20, "3D"));
		buttonList.add(new GuiButton(1, j+205, k-2, 20, 20, "2D"));

		if (mode == 1) {
			buttonList.add(new GuiButton(2, j+165, k-2, 20, 20, "+"));
			buttonList.add(new GuiButton(3, j+145, k-2, 20, 20, "-"));
		}
	}

	@Override
	public void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == 0) {
			mode = 0;
			secY = 0;
			this.initGui();
		}
		else if (b.id == 1) {
			mode = 1;
			rx = ry = rz = 0;
			this.initGui();
		}
		else if (b.id == 2 && secY < page.getStructure().getStructureForDisplay().getSizeY()-1) {
			secY++;
			this.initGui();
		}
		else if (b.id == 3 && secY > 0) {
			secY--;
			this.initGui();
		}
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

		tick++;

		FilledBlockArray arr = page.getStructure().getStructureForDisplay();
		if (page.name().toLowerCase().contains("casting")) {
			arr.setBlock(arr.getMidX(), arr.getMinY()+1, arr.getMidZ(), ChromaTiles.TABLE.getBlock(), ChromaTiles.TABLE.getBlockMetadata());
		}

		switch(mode) {
		case 0:
			this.draw3d(arr, j, k);
			break;
		case 1:
			this.drawSlice(arr, j, k);
			break;
		}
	}

	private void drawSlice(FilledBlockArray arr, int j, int k) {
		int y = arr.getMinY()+secY;
		int max = Math.max(arr.getSizeX(), arr.getSizeZ());
		int dd = max > 16 ? 28-max : 14;
		int ox = 120;
		int oy = 105;
		for (int x = arr.getMinX(); x <= arr.getMaxX(); x++) {
			for (int z = arr.getMinZ(); z <= arr.getMaxZ(); z++) {
				ItemStack is = arr.getDisplayAt(x, y, z);
				if (page.name().toLowerCase().contains("pylon") && x == arr.getMidX() && y == arr.getMinY()+9 && z == arr.getMidZ()) {
					is = ChromaTiles.PYLON.getCraftedProduct();
				}
				if (is != null) {
					int dx = (x-arr.getMidX())*dd;
					int dz = (z-arr.getMidZ())*dd;
					api.drawItemStack(itemRender, is, j+dx+ox, k+dz+oy);
				}
			}
		}
	}

	private void draw3d(FilledBlockArray arr, int j, int k) {
		if (Mouse.isButtonDown(0) && tick > 2) {
			rx += 0.25*Mouse.getDY();
			ry -= 0.25*Mouse.getDX();
		}
		else if (Mouse.isButtonDown(1)) {
			rx = ry = rz = 0;
		}

		int dd = 12;
		int ddy = 12;

		HashMap<Vector3f, CoordStack> render = new HashMap();

		Matrix4f rot = new Matrix4f();
		ReikaVectorHelper.euler213Sequence(rot, rx, ry, rz);
		for (int y = arr.getMinY(); y <= arr.getMaxY(); y++) {
			for (int x = arr.getMinX(); x <= arr.getMaxX(); x++) {
				for (int z = arr.getMinZ(); z <= arr.getMaxZ(); z++) {
					ItemStack is = arr.getDisplayAt(x, y, z);
					if (page.name().toLowerCase().contains("pylon") && x == arr.getMidX() && y == arr.getMinY()+9 && z == arr.getMidZ()) {
						is = ChromaTiles.PYLON.getCraftedProduct();
					}
					if (is != null) {
						int dx = x-arr.getMidX();
						int dy = y-arr.getMidY();
						int dz = z-arr.getMidZ();
						Vector3f in = new Vector3f(dx, dy, dz);
						Vector3f vec = ReikaVectorHelper.multiplyVectorByMatrix(in, rot);
						int px = Math.round(vec.x*dd+vec.z*dd);
						int py = Math.round(-vec.x*dd/2+vec.z*dd/2-vec.y*ddy);
						int pz = 0;//250;
						render.put(vec, new CoordStack(is, px, py, pz));
					}
				}
			}
		}

		double max = Math.max(arr.getSizeY()*1, Math.sqrt(Math.pow(arr.getSizeX(), 2)+Math.pow(arr.getMaxZ(), 2)));
		//ReikaJavaLibrary.pConsole(max);
		GL11.glPushMatrix();
		double d = 2;
		if (max >= 18) {
			d = 0.6;
		}
		else if (max >= 14) {
			d = 0.8;
		}
		else if (max >= 12) {
			d = 0.95;
		}
		else if (max >= 10) {
			d = 1.2;
		}
		else if (max >= 8) {
			d = 1.5;
		}
		else if (max >= 4) {
			d = 1.75;
		}
		GL11.glScaled(d, d, 1);

		int ox = (int)((j+122)/d);
		int oy = (int)((k+92)/d);
		if (d > 1)
			ox -= 5;
		if (d > 1)
			oy -= 5;

		ArrayList<Vector3f> keys = new ArrayList(render.keySet());
		Collections.sort(keys, visibility);

		for (Vector3f vec : keys) {
			CoordStack is = render.get(vec);
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, is.coord.zCoord);
			double scale = 1;
			int ox2 = 0;
			int oy2 = 0;
			if (ReikaItemHelper.matchStacks(is.item, ChromaTiles.PYLON.getCraftedProduct())) {
				scale = 2;
				ox2 = -4;
				oy2 = -6;
			}
			GL11.glScaled(scale, scale, 1);
			api.drawItemStack(itemRender, is.item, (int)((is.coord.xCoord+ox)/scale)+ox2, (int)((is.coord.yCoord+oy)/scale)+oy2);
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
